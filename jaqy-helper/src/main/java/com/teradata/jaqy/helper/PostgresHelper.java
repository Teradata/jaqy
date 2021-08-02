/*
 * Copyright (c) 2017-2021 Teradata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teradata.jaqy.helper;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.Collection;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;

/**
 * @author  Heng Yuan
 */
class PostgresHelper extends DefaultHelper
{
    public PostgresHelper (JdbcFeatures features, JaqyConnection conn, Globals globals)
    {
        super (features, conn, globals);
    }

    @Override
    public Array createArrayOf (ParameterInfo paramInfo, Object[] elements) throws SQLException
    {
        String name = paramInfo.typeName;
        if (name.startsWith ("_"))
            name = name.substring (1);
        return getConnection ().createArrayOf (name, elements);
    }

    @Override
    public void fixColumnInfo (FullColumnInfo info)
    {
        if (info.type == Types.STRUCT)
        {
            // PostgreSQL JDBC only transmit composite types as strings
            info.type = Types.VARCHAR;
        }
        else if (info.type == Types.ARRAY)
        {
            //
            // See https://jdbc.postgresql.org/documentation/head/arrays.html
            // for the supported array types.
            //
            String elementType = info.typeName.substring (1);
            if ("bool".equals (elementType))
            {
                info.children = createElementType (Types.BIT, elementType);
            }
            else if ("int2".equals (elementType))
            {
                info.children = createElementType (Types.SMALLINT, elementType);
            }
            else if ("int4".equals (elementType) ||
                     "serial".equals (elementType))
            {
                info.children = createElementType (Types.INTEGER, elementType);
            }
            else if ("int8".equals (elementType) ||
                     "bigserial".equals (elementType))
            {
                info.children = createElementType (Types.BIGINT, elementType);
            }
            else if ("float4".equals (elementType))
            {
                info.children = createElementType (Types.REAL, elementType);
            }
            else if ("float8".equals (elementType))
            {
                info.children = createElementType (Types.DOUBLE, elementType);
            }
            else if ("bytea".equals (elementType))
            {
                info.children = createElementType (Types.VARBINARY, elementType);
            }
            else if ("varchar".equals (elementType) ||
                     "text".equals (elementType))
            {
                info.children = createElementType (Types.VARCHAR, elementType);
            }
            else
            {
                super.fixColumnInfo (info);
            }
        }
        else if (info.type == Types.DOUBLE &&
                 "money".equals (info.typeName))
        {
            // PostgreSQL JDBC money handling is a pain.  Let's just
            // treat it as a string type.
            info.type = Types.VARCHAR;
            info.typeName = "varchar";
            info.precision = 26;        // rough estimate
        }
        else
        {
            super.fixColumnInfo (info);
        }
    }

    @Override
    public void setObject (JaqyPreparedStatement stmt, int columnIndex, ParameterInfo paramInfo, Object o, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception
    {
        // Do a workaround fix for PostgreSQL JDBC's XML streaming issue.
        if (paramInfo.type == Types.SQLXML)
        {
            if (o instanceof SQLXML ||
                o instanceof CharSequence)
            {
                SQLXML x = getConnection ().createSQLXML ();
                if (o instanceof SQLXML)
                    x.setString (((SQLXML)o).getString ());
                else
                    x.setString (o.toString ());
                stmt.setSQLXML (columnIndex, x);
                freeList.add (x);
                return;
            }
        }
        super.setObject (stmt, columnIndex, paramInfo, o, freeList, interpreter);
    }
}

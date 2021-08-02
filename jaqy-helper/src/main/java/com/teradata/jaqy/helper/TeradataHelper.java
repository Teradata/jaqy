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

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Level;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyDefaultResultSet;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.resultset.CachedClob;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.typehandler.StringTypeHandler;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.ResultSetUtils;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author  Heng Yuan
 */
class TeradataHelper extends DefaultHelper
{
    private final static String getPDTString (Struct s) throws SQLException
    {
        if (s == null)
            return null;
        StringBuilder builder = new StringBuilder ();
        builder.append ("('");
        for (Object obj : s.getAttributes ())
        {
            if (builder.length () > 2)
                builder.append ("', '");
            builder.append (obj);
        }
        builder.append ("')");
        return builder.toString ();
    }

    private final static TypeHandler s_pdtHandler = new TypeHandler ()
    {
        @Override
        public String getString (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
        {
            Object obj = rs.getObject (column);
            if (obj == null)
            {
                return null;
            }
            if (obj instanceof Struct)
            {
                return getPDTString ((Struct)obj);
            }
            return obj.toString();
        }

        @Override
        public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
        {
            String str = getString (rs, column, interpreter);
            if (str == null)
                return -1;
            return str.length();
        }
    };

    public TeradataHelper (JdbcFeatures features, JaqyConnection conn, Globals globals)
    {
        super (features, conn, globals);
    }

    @Override
    public JaqyResultSet getResultSet (ResultSet rs, JaqyInterpreter interpreter) throws SQLException
    {
        if (rs == null)
            return null;
        ResultSetMetaData meta = rs.getMetaData ();

        // guess if the output is from SHOW statement.
        if (meta != null &&
            meta.getColumnCount () == 1)
        {
            int type = meta.getColumnType (1);
            if (TypesUtils.isString (type))
            {
                getGlobals ().log (Level.INFO, "Potential SHOW ResultSet.");
                //
                // Teradata SHOW statements due to legacy, use '\r' instead of '\n'
                // characters.  That can be problematic in the output.
                //
                // Per discussion with a Teradata JDBC architect, no internals of
                // Teradata JDBC should be exposed in OSS code.  So the only thing
                // we can do is to guess the output could be from, and replace
                // the EOL character that way.
                //
                // All SHOW statements are single column.  Typically, the output
                // is a row of VARCHAR.  However, I am not sure if there is ever
                // a case of having CLOB output, or just multiple rows.
                //
                // So we do generic handling here.
                //
                InMemoryResultSet newRS = ResultSetUtils.copyResultSet (rs, 0, this, interpreter);
                rs.close ();
                for (Object[] row : newRS.getRows ())
                {
                    for (int i = 0; i < row.length; ++i)
                    {
                        if (row[i] != null)
                        {
                            Object o = row[i];
                            if (o instanceof String)
                            {
                                row[i] = ((String)o).replace ('\r', '\n');
                            }
                            else if (o instanceof CachedClob)
                            {
                                CachedClob clob = (CachedClob)o;
                                String s = clob.getSubString (1, (int)clob.length ());
                                clob = new CachedClob (s);
                            }
                        }
                    }
                }
                rs = newRS;
            }
        }
        return new JaqyDefaultResultSet (rs, this);
    }

    @Override
    public Object getObject (JaqyResultSet rs, int index) throws SQLException
    {
        Object o = rs.getObjectInternal (index);
        if (o instanceof Struct)
        {
            if (rs.getMetaData ().getColumnType (index) == Types.STRUCT)
            {
                String typeName = rs.getMetaData ().getColumnTypeName (index);
                if (typeName != null && typeName.startsWith ("PERIOD("))
                {
                    return getPDTString ((Struct) o);
                }
            }
        }
        return o;
    }

    @Override
    public TypeHandler getTypeHandler (JaqyResultSet rs, int column) throws SQLException
    {
        if (rs.getMetaData ().getColumnType (column) == Types.STRUCT)
        {
            String typeName = rs.getMetaData ().getColumnTypeName (column);
            if (typeName != null && typeName.startsWith ("PERIOD("))
            {
                return s_pdtHandler;
            }
        }
        else if (rs.getMetaData ().getColumnType (column) == Types.SQLXML)
        {
            return StringTypeHandler.getInstance ();
        }

        return super.getTypeHandler (rs, column);
    }

    @Override
    public Struct createStruct (ParameterInfo paramInfo, Object[] elements) throws SQLException
    {
        /*
         * Handle Teradata PERIOD data types here.
         */
        String typeName = paramInfo.typeName;
        if (elements.length == 2 &&
            typeName.startsWith ("PERIOD") &&
            elements[0] != null &&
            elements[0] instanceof String &&
            elements[1] != null &&
            elements[1] instanceof String)
        {
            if ("PERIOD(DATE)".equals (typeName))
            {
                elements[0] = Date.valueOf ((String) elements[0]);
                elements[1] = Date.valueOf ((String) elements[1]);
            }
            else if ("PERIOD(TIME)".equals (typeName) ||
                     "PERIOD(TIME WITH TIME ZONE)".equals (typeName))
            {
                elements[0] = Time.valueOf ((String) elements[0]);
                elements[1] = Time.valueOf ((String) elements[1]);
            }
            else if ("PERIOD(TIMESTAMP)".equals (typeName) ||
                     "PERIOD(TIMESTAMP WITH TIME ZONE)".equals (typeName))
            {
                elements[0] = Timestamp.valueOf ((String) elements[0]);
                elements[1] = Timestamp.valueOf ((String) elements[1]);
            }
        }

        return getConnection ().createStruct (typeName, elements);
    }

    @Override
    public void fixColumnInfo (FullColumnInfo info)
    {
        if (info.type == Types.STRUCT &&
            info.typeName != null &&
            info.typeName.startsWith ("PERIOD("))
        {
            info.type = Types.VARCHAR;
            info.precision = 100;
        }
        else if (info.type == Types.OTHER &&
                 info.className != null)
        {
            fixOtherType (info);
        }
    }

    @Override
    public String getImportTableIndex ()
    {
        return "\nNO PRIMARY INDEX";
    }
}

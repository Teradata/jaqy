/*
 * Copyright (c) 2017 Teradata
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.schema.ColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;

/**
 * @author	Heng Yuan
 */
class PostgresHelper extends DefaultHelper
{
	public PostgresHelper (JaqyConnection conn, Globals globals)
	{
		super (new JdbcFeatures (), conn, globals);
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
	public String getColumnType (JaqyResultSetMetaData meta, int column) throws SQLException
	{
		String type = meta.getColumnTypeName (column);
		if ("numeric".equals (type))
		{
			int precision = meta.getPrecision (column);
			int scale = meta.getScale (column);
			return type + "(" + precision + "," + scale + ")";
		}
		return super.getColumnType (meta, column);
	}

	@Override
	public void fixColumnInfo (ColumnInfo info)
	{
		if (info.type == Types.STRUCT)
		{
			// PostgreSQL JDBC only transmit composite types as strings
			info.type = Types.VARCHAR;
		}
		else if (info.type == Types.ARRAY)
		{
			String elementType = info.typeName.substring (1);
			if ("bytea".equals (elementType))
			{
				info.children = createElementType (Types.VARBINARY, elementType);
				info.children[0].precision = Integer.MAX_VALUE;
				info.children[0].nullable = ResultSetMetaData.columnNullable;
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
			else if ("float8".equals (elementType))
			{
				info.children = createElementType (Types.DOUBLE, elementType);
			}
			else if ("numeric".equals (elementType))
			{
				info.children = createElementType (Types.NUMERIC, elementType);
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
			info.precision = 26;		// rough estimate
		}
		else
		{
			super.fixColumnInfo (info);
		}
	}
}

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
package com.teradata.jaqy.schema;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.helper.DummyHelper;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.resultset.InMemoryResultSet;

/**
 * @author	Heng Yuan
 */
public class SchemaUtils
{
	public static String getTableSchema (JaqyHelper helper, SchemaInfo schemaInfo, String tableName) throws SQLException
	{
		int count = schemaInfo.columns.length;
		StringBuilder buffer = new StringBuilder ();
		buffer.append ("CREATE TABLE ").append (tableName).append ("\n(");
		for (int i = 0; i < count; ++i)
		{
			String columnName = schemaInfo.columns[i].name;
			// The schema column type is undefined.  Let's make it an
			// integer type which is nullable.
			if (schemaInfo.columns[i].type == Types.NULL)
			{
				schemaInfo.columns[i].type = Types.INTEGER;
				schemaInfo.columns[i].nullable = ResultSetMetaData.columnNullable;
			}
			String columnType = helper.getTypeName (schemaInfo.columns[i]);
			if (i == 0)
				buffer.append ('\n');
			else
				buffer.append (",\n");
			buffer.append ('\t');
			buffer.append (columnName).append (" ").append (columnType);
			if (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNoNulls)
				buffer.append (" NOT NULL");
		}
		buffer.append ("\n)");
		return buffer.toString ();
	}

	public static JaqyResultSet getSchemaResultSet (JaqyHelper helper, SchemaInfo schemaInfo) throws SQLException
	{
		int count = schemaInfo.columns.length;
		PropertyTable pt = new PropertyTable (new String[]{ "Column", "Type", "Nullable" });
		for (int i = 0; i < count; ++i)
		{
			String columnName = schemaInfo.columns[i].name;
			String columnType = helper.getTypeName (schemaInfo.columns[i]);
			String nullable = (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNoNulls) ? "No" : (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNullable ? "Yes" : "Unknown");
			pt.addRow (new String[]{ columnName, columnType, nullable });
		}

		InMemoryResultSet columnRS = new InMemoryResultSet (pt);
		return DummyHelper.getInstance ().getResultSet (columnRS);
	}

	public static TypeMap getTypeMap (JaqyConnection conn) throws SQLException
	{
		ResultSet rs = conn.getMetaData ().getTypeInfo ();
		if (rs == null)
			return null;
		try
		{
			HashMap<Integer, TypeInfo> map = new HashMap<Integer, TypeInfo> ();
			while (rs.next ())
			{
				TypeInfo info = new TypeInfo ();
				info.typeName = rs.getString ("TYPE_NAME");
				info.type = rs.getInt ("DATA_TYPE");
				info.maxPrecision = rs.getInt ("PRECISION");
				info.literalPrefix = rs.getString ("LITERAL_PREFIX");
				info.literalSuffix = rs.getString ("LITERAL_SUFFIX");
				info.nullable = rs.getShort ("NULLABLE");
				info.caseSensitive = rs.getBoolean ("CASE_SENSITIVE");
				info.searchable = rs.getShort ("SEARCHABLE");
				info.signed = !rs.getBoolean ("UNSIGNED_ATTRIBUTE");
				info.money = rs.getBoolean ("FIXED_PREC_SCALE");
				info.autoIncrement = rs.getBoolean ("AUTO_INCREMENT");
				info.minScale = rs.getShort ("MINIMUM_SCALE");
				info.maxScale = rs.getShort ("MAXIMUM_SCALE");
				info.radix = rs.getInt ("NUM_PREC_RADIX");

				TypeInfo oldInfo = map.get (info.type);
				if (oldInfo == null )
					map.put (info.type, info);
				else
				{
					switch (info.type)
					{
						case Types.VARCHAR:
						case Types.CHAR:
						case Types.NVARCHAR:
						case Types.NCHAR:
						case Types.BINARY:
						case Types.VARBINARY:
						case Types.CLOB:
						case Types.NCLOB:
						case Types.BLOB:
							// for these types, pick the one that has maximum precision
							if (info.maxPrecision > 0)
								map.put (info.type, info);
							break;
					}
				}
			}
			return new TypeMap (map);
		}
		finally
		{
			rs.close ();
		}
	}
}

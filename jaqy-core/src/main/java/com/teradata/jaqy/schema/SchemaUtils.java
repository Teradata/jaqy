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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.interfaces.JaqyHelper;

/**
 * @author	Heng Yuan
 */
public class SchemaUtils
{
	public static String getTableSchema (JaqyHelper helper, SchemaInfo schemaInfo, String tableName) throws SQLException
	{
		int count = schemaInfo.columns.length;
		StringBuilder buffer = new StringBuilder ();
		buffer.append ("CREATE TABLE ").append (tableName).append (" (");
		for (int i = 0; i < count; ++i)
		{
			String columnName = schemaInfo.columns[i].name;
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
}

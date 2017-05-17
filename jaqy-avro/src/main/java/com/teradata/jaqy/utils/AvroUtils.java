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
package com.teradata.jaqy.utils;

import java.nio.ByteBuffer;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;

/**
 * @author Heng Yuan
 */
public class AvroUtils
{
	static Schema.Type getAvroType (int sqlType)
	{
		switch (sqlType)
		{
			case Types.BIT:
				return Schema.Type.BYTES;
			case Types.BOOLEAN:
				return Schema.Type.BOOLEAN;
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				return Schema.Type.INT;
			case Types.BIGINT:
				return Schema.Type.LONG;
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.REAL:
				return Schema.Type.STRING;
			case Types.FLOAT:
				return Schema.Type.FLOAT;
			case Types.DOUBLE:
				return Schema.Type.DOUBLE;
			case Types.BINARY:
			case Types.BLOB:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return Schema.Type.BYTES;
			case Types.NULL:
				return Schema.Type.NULL;
			default:
				return Schema.Type.STRING;
		}
	}

	public static Schema getSchema (JaqyResultSetMetaData rsmd) throws SQLException
	{
		int columns = rsmd.getColumnCount ();
		ArrayList<Schema.Field> fields = new ArrayList<Schema.Field> (columns);

		for (int i = 0; i < columns; ++i)
		{
			String header = rsmd.getColumnLabel (i + 1);
			int type = rsmd.getColumnType (i + 1);

			Schema fieldSchema = Schema.create (getAvroType (type));
			if (rsmd.isNullable (i + 1) != ResultSetMetaData.columnNoNulls)
			{
				// In order to include NULLs in the record, we have to include
				// NULL as part of field schema for the column.
				ArrayList<Schema> list = new ArrayList<Schema> ();
				list.add (Schema.create (Schema.Type.NULL));
				list.add (fieldSchema);
				fieldSchema = Schema.createUnion (list);
			}

			Schema.Field field = new Schema.Field (header, fieldSchema, null, (Object) null);
			fields.add (field);
		}

		// create a dummy record schema name.
		String schemaName = "rsmd" + rsmd.hashCode ();
		Schema schema = Schema.createRecord (schemaName, null, null, false);
		schema.setFields (fields);
		return schema;
	}

	public static Object getAvroObject (Object obj)
	{
		if (obj instanceof byte[])
		{
			return ByteBuffer.wrap ((byte[]) obj);
		}
		return obj;
	}

	public static long print (DataFileWriter<GenericRecord> writer, Schema schema, JaqyResultSet rs) throws Exception
	{
		JaqyResultSetMetaData metaData = rs.getMetaData ();
		int columns = metaData.getColumnCount ();
		String[] headers = new String[columns];

		for (int i = 0; i < columns; ++i)
		{
			headers[i] = metaData.getColumnLabel (i + 1);
		}

		long count = 0;
		while (rs.next ())
		{
			++count;
			GenericRecord r = new GenericData.Record (schema);
			for (int i = 0; i < columns; ++i)
			{
				Object obj = rs.getObject (i + 1);
				r.put (i, getAvroObject (obj));
			}
			writer.append (r);
		}
		return count;
	}
}

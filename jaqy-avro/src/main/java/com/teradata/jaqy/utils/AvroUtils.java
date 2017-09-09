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
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.schema.ColumnInfo;
import com.teradata.jaqy.schema.SchemaInfo;

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
				return Schema.Type.BOOLEAN;
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
			case Types.STRUCT:
				return Schema.Type.RECORD;
			case Types.ARRAY:
				return Schema.Type.ARRAY;
			default:
				return Schema.Type.STRING;
		}
	}

	private static Schema getArraySchema (ColumnInfo columnInfo)
	{
		ColumnInfo[] childrenInfos = columnInfo.children;
		if (childrenInfos == null ||
			childrenInfos.length != 1)
		{
			// We cannot handle this case.
			throw new RuntimeException ("Cannot handle column " + columnInfo.label + " type.");
		}
		Schema.Type childAvroType = getAvroType (childrenInfos[0].type);
		if (childAvroType == Schema.Type.ARRAY ||
			childAvroType == Schema.Type.RECORD)
		{
			// We cannot handle this case.
			throw new RuntimeException ("Cannot handle column " + columnInfo.label + " type.");
		}
		Schema childSchema = Schema.create (childAvroType);
		if (childrenInfos[0].nullable != ResultSetMetaData.columnNoNulls)
		{
			// In order to include NULLs in the record, we have to include
			// NULL as part of field schema for the column.
			ArrayList<Schema> list = new ArrayList<Schema> ();
			list.add (Schema.create (Schema.Type.NULL));
			list.add (childSchema);
			childSchema = Schema.createUnion (list);
		}
		return Schema.createArray (childSchema);
	}

	private static Schema getStructSchema (ColumnInfo columnInfo)
	{
		ColumnInfo[] childrenInfos = columnInfo.children;
		if (childrenInfos == null)
		{
			// We cannot handle this case.
			throw new RuntimeException ("Cannot handle column " + columnInfo.label + " type.");
		}
		List<Schema.Field> childrenSchema = new ArrayList<Schema.Field> ();
		for (ColumnInfo child : childrenInfos)
		{
			Schema.Type childAvroType = getAvroType (child.type);
			if (childAvroType == Schema.Type.ARRAY ||
				childAvroType == Schema.Type.RECORD)
			{
				// We cannot handle this case.
				throw new RuntimeException ("Cannot handle column " + columnInfo.label + " type.");
			}
			Schema childSchema = Schema.create (childAvroType);
			if (child.nullable != ResultSetMetaData.columnNoNulls)
			{
				// In order to include NULLs in the record, we have to include
				// NULL as part of field schema for the column.
				ArrayList<Schema> list = new ArrayList<Schema> ();
				list.add (Schema.create (Schema.Type.NULL));
				list.add (childSchema);
				childSchema = Schema.createUnion (list);
			}
			Schema.Field childField = new Schema.Field (child.label, childSchema, null, (Object)null);
			childrenSchema.add (childField);
		}
		return Schema.createRecord (childrenSchema);
	}

	public static Schema getSchema (SchemaInfo schemaInfo, JaqyHelper helper) throws SQLException
	{
		ColumnInfo[] columnInfos = schemaInfo.columns;
		int columns = columnInfos.length;
		ArrayList<Schema.Field> fields = new ArrayList<Schema.Field> (columns);

		for (int i = 0; i < columns; ++i)
		{
			String header = columnInfos[i].label;
			int type = columnInfos[i].type;
			Schema.Type avroType = getAvroType (type);
			Schema fieldSchema;
			if (avroType == Schema.Type.ARRAY)
			{
				fieldSchema = getArraySchema (columnInfos[i]);
			}
			else if (avroType == Schema.Type.RECORD)
			{
				fieldSchema = getStructSchema (columnInfos[i]);
			}
			else
			{
				fieldSchema = Schema.create (getAvroType (type));
			}

			if (columnInfos[i].nullable != ResultSetMetaData.columnNoNulls)
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
		String schemaName = "rsmd" + columnInfos.hashCode ();
		Schema schema = Schema.createRecord (schemaName, null, null, false);
		schema.setFields (fields);
		return schema;
	}

	public static Object getAvroObject (Object obj, Schema.Type avroType, ColumnInfo columnInfo, Schema childSchema) throws SQLException
	{
		switch (avroType)
		{
			case STRING:
			{
				String value;
				if (obj instanceof Clob)
				{
					Clob clob = (Clob)obj;
					value = clob.getSubString (1, (int)clob.length ());
					clob.free ();
				}
				else if (obj instanceof SQLXML)
				{
					SQLXML xml = ((SQLXML)obj);
					value = xml.getString ();
					xml.free ();
				}
				else
				{
					value = obj.toString ();
				}
				return value;
			}
			case BYTES:
			{
				byte[] bytes;
				if (obj instanceof byte[])
				{
					bytes = (byte[])obj;
				}
				else if (obj instanceof Blob)
				{
					Blob blob = ((Blob)obj);
					bytes = blob.getBytes (1, (int)blob.length ());
					blob.free ();
				}
				else
				{
					break;
				}
				return ByteBuffer.wrap (bytes);
			}
			case ARRAY:
			{
				if (!(obj instanceof Array))
					break;
				ResultSet rs = ((Array)obj).getResultSet ();
				GenericArray<Object> arr = new GenericData.Array<Object> (1, childSchema);
				Schema.Type childType = getAvroType (columnInfo.children[0].type);
				while (rs.next ())
				{
					Object o = rs.getObject (2);
					arr.add (getAvroObject (o, childType, columnInfo.children[0], null));
				}
				rs.close ();
				return arr;
			}
			case RECORD:
			{
				if (!(obj instanceof Struct))
					break;
				Object[] attrs = ((Struct)obj).getAttributes ();
				GenericRecord r = new GenericData.Record (childSchema);
				for (int i = 0; i < attrs.length; ++i)
				{
					if (attrs[i] == null)
						r.put (i, null);
					else
						r.put (i, getAvroObject (attrs[i], getAvroType (columnInfo.children[i].type), columnInfo.children[i], null));
				}
				return r;
			}
			default:
			{
				return obj;
			}
		}
		throw new IllegalArgumentException ("Cannot convert " + obj.getClass () + " to " + avroType);
	}

	public static long print (DataFileWriter<GenericRecord> writer, Schema schema, JaqyResultSet rs, SchemaInfo schemaInfo) throws Exception
	{
		ColumnInfo[] columnInfos = schemaInfo.columns;
		int columns = columnInfos.length;
		Schema.Type[] avroTypes = new Schema.Type[columns];
		Schema[] avroSchemas = new Schema[columns];

		for (int i = 0; i < columns; ++i)
		{
			avroTypes[i] = getAvroType (columnInfos[i].type);
			if (avroTypes[i] == Schema.Type.ARRAY)
			{
				avroSchemas[i] = getArraySchema (columnInfos[i]);
			}
			else if (avroTypes[i] == Schema.Type.RECORD)
			{
				avroSchemas[i] = getStructSchema (columnInfos[i]);
			}
		}

		long count = 0;
		while (rs.next ())
		{
			++count;
			GenericRecord r = new GenericData.Record (schema);
			for (int i = 0; i < columns; ++i)
			{
				Object obj = rs.getObject (i + 1);
				if (obj == null)
				{
					r.put (i, null);
				}
				else
				{
					r.put (i, getAvroObject (obj, avroTypes[i], columnInfos[i], avroSchemas[i]));
				}
			}
			writer.append (r);
		}
		return count;
	}
}

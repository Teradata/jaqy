/*
 * Copyright (c) 2017-2018 Teradata
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
package com.teradata.jaqy.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.AvroUtils;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author Heng Yuan
 */
class AvroImporter implements JaqyImporter<String>
{
	private static Object unwrapAvroObject (Object v)
	{
		if (v instanceof CharSequence)
			return v.toString ();
		if (v instanceof ByteBuffer)
		{
			ByteBuffer bb = (ByteBuffer)v;
			byte[] bytes = new byte[bb.remaining ()];
			bb.get (bytes);
			return bytes;
		}
		return v;
	}

	private final Path m_file;
	private final JaqyConnection m_conn;
	private DataFileReader<GenericRecord> m_dataFileReader;
	private boolean m_end;
	private Iterator<GenericRecord> m_iter;
	private GenericRecord m_record;

	public AvroImporter (JaqyConnection conn, Path file) throws IOException
	{
		m_conn = conn;
		m_file = file;
		openFile (file);
	}

	private void openFile (Path file) throws IOException
	{
		DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord> ();
		InputStream is = file.getInputStream ();
		if (!(is instanceof FileInputStream))
		{
			is.close ();
			throw new IOException ("Unable to get file based InputStream from " + file.getPath ());
		}
		m_dataFileReader = new DataFileReader<GenericRecord> (new AvroInputStream ((FileInputStream)is), reader);

		m_iter = m_dataFileReader.iterator ();
	}

	@Override
	public void close () throws IOException
	{
		m_dataFileReader.close ();
	}

	@Override
	public String getName ()
	{
		return "avro";
	}

	@Override
	public SchemaInfo getSchema () throws IOException
	{
		SchemaInfo schema = AvroUtils.getSchema (m_dataFileReader.getSchema (), m_iter);
		m_dataFileReader.close ();
		openFile (m_file);
		return schema;
	}

	@Override
	public boolean next () throws Exception
	{
		if (m_end)
			return false;
		try
		{
			if (m_iter.hasNext ())
			{
				m_record = m_iter.next ();
				return true;
			}
		}
		catch (Exception ex)
		{
		}
		m_end = true;
		m_dataFileReader.close ();
		return false;
	}

	private Object getObject (Object v, ParameterInfo paramInfo) throws Exception
	{
		if (v == null)
			return null;

		JaqyHelper helper = m_conn.getHelper ();
		switch (paramInfo.type)
		{
			case Types.BIT:
			case Types.BOOLEAN:
			{
				if (v instanceof Boolean)
					return v;
				if (v instanceof Number)
					return v;
				if (v instanceof CharSequence)
					return v.toString ();
				break;
			}
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
			{
				if (v instanceof Boolean)
				{
					if (((Boolean)v).booleanValue ())
						return 1;
					else
						return 0;
				}
				if (v instanceof Number)
					return v;
				if (v instanceof CharSequence)
					return v.toString ();
				break;
			}
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
			case Types.SQLXML:
			{
				if (v instanceof CharSequence)
					return v.toString ();
				break;
			}
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
			{
				if (v instanceof ByteBuffer)
				{
					ByteBuffer bb = (ByteBuffer) v;
					byte[] bytes = new byte[bb.remaining ()];
					bb.get (bytes);
					return bytes;
				}
				break;
			}
			case Types.ARRAY:
			{
				if (v instanceof List)
				{
					Object[] objs = new Object[((List<?>)v).size ()];
					((List<?>) v).toArray (objs);
					for (int i = 0; i < objs.length; ++i)
					{
						objs[i] = unwrapAvroObject (objs[i]);
					}
					return helper.createArrayOf (paramInfo, objs);
				}
				break;
			}
			case Types.STRUCT:
			{
				if (v instanceof List)
				{
					Object[] objs = new Object[((List<?>)v).size ()];
					((List<?>) v).toArray (objs);
					for (int i = 0; i < objs.length; ++i)
					{
						objs[i] = unwrapAvroObject (objs[i]);
					}
					return helper.createStruct (paramInfo, objs);
				}
				break;
			}
			case Types.OTHER:
			{
				if (v instanceof CharSequence)
					return v.toString ();
				if (v instanceof ByteBuffer)
				{
					ByteBuffer bb = (ByteBuffer) v;
					byte[] bytes = new byte[bb.remaining ()];
					bb.get (bytes);
					return bytes;
				}
				break;
			}
		}
		throw new IOException ("Type mismatch: object is " + v.getClass () + ", target type is " + TypesUtils.getTypeName (paramInfo.type));
	}

	@Override
	public Object getObject (int index, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
	{
		return getObject (m_record.get (index), paramInfo);
	}

	@Override
	public String getPath (String name) throws Exception
	{
		return name;
	}

	@Override
	public Object getObjectFromPath (String name, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
	{
		return getObject (m_record.get (name), paramInfo);
	}

	@Override
	public void setNull (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo) throws Exception
	{
		stmt.setNull (column, paramInfo.type, paramInfo.typeName);
	}
}

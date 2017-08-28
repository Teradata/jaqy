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
package com.teradata.jaqy.importer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Types;
import java.util.Iterator;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.utils.ParameterInfo;

/**
 * @author Heng Yuan
 */
class AvroImporter implements JaqyImporter<String>
{
	private DataFileReader<GenericRecord> m_dataFileReader;
	private boolean m_end;
	private Iterator<GenericRecord> m_iter;
	private GenericRecord m_record;

	public AvroImporter (File file) throws IOException
	{
		DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord> ();
		m_dataFileReader = new DataFileReader<GenericRecord> (file, reader);

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
	public void showSchema (Display display)
	{
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

	@Override
	public Object getObject (int index, ParameterInfo paramInfo) throws IOException
	{
		throw new IOException ("AVRO data has to be accessed via field.");
	}

	@Override
	public String getPath (String name) throws Exception
	{
		return name;
	}

	@Override
	public Object getObjectFromPath (String name, ParameterInfo paramInfo) throws Exception
	{
		Object v = m_record.get (name);
		if (v == null)
			return null;

		switch (paramInfo.type)
		{
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
			{
				if (v instanceof Number)
					return v;
				throw new IOException ("Invalid type.");
			}
			case Types.DATE:
			{
				if (v instanceof Date)
					return v;
				throw new IOException ("Invalid type.");
			}
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
			{
				if (v instanceof String)
					return v;
				return v.toString ();
			}
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
			{
				if (v instanceof byte[])
				{
					return v;
				}
				else if (v instanceof ByteBuffer)
				{
					ByteBuffer bb = (ByteBuffer) v;
					byte[] bytes = new byte[bb.remaining ()];
					bb.get (bytes);
					return bytes;
				}
				throw new IOException ("Invalid type.");
			}
		}
		throw new IOException ("Invalid type.");
	}
}

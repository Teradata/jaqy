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
package com.teradata.jaqy.exporter;

import java.io.OutputStream;
import java.util.logging.Level;

import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.AvroUtils;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;

/**
 * @author Heng Yuan
 */
class AvroExporter implements JaqyExporter
{
	private final OutputStream m_os;
	private final CodecFactory m_codecFactory;

	public AvroExporter (OutputStream os, CodecFactory codecFactory)
	{
		m_os = os;
		m_codecFactory = codecFactory;
	}

	@Override
	public String getName ()
	{
		return "avro";
	}

	@Override
	public long export (JaqyResultSet rs, Session session, JaqyInterpreter interpreter, Globals globals) throws Exception
	{
		JaqyHelper helper = rs.getHelper ();
		SchemaInfo schemaInfo = ResultSetMetaDataUtils.getColumnInfo (rs.getMetaData ().getMetaData (), helper);
		Schema schema = AvroUtils.getSchema (schemaInfo, helper);
		globals.log (Level.INFO, "schema is " + schema.toString (true));

		DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord> (schema);
		DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord> (writer);
		if (m_codecFactory != null)
			dataFileWriter.setCodec (m_codecFactory);

		dataFileWriter.create (schema, m_os);

		long count = AvroUtils.print (dataFileWriter, schema, rs, schemaInfo);
		dataFileWriter.close ();
		return count;
	}
}

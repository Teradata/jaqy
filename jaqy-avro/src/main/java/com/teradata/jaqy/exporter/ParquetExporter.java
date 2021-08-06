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
package com.teradata.jaqy.exporter;

import java.util.logging.Level;

import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.AvroUtils;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;

/**
 * @author Heng Yuan
 */
class ParquetExporter implements JaqyExporter
{
    private final Path m_path;
    private final CodecFactory m_codecFactory;

    public ParquetExporter (Path path, CodecFactory codecFactory)
    {
        m_path = path;
        m_codecFactory = codecFactory;
    }

    @Override
    public String getName ()
    {
        return "pq";
    }

    @Override
    public long export (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
    {
        JaqyHelper helper = rs.getHelper ();
        SchemaInfo schemaInfo = ResultSetMetaDataUtils.getColumnInfo (rs.getMetaData (), helper);
        Schema schema = AvroUtils.getSchema (schemaInfo, helper);
        interpreter.getGlobals ().log (Level.INFO, "schema is " + schema.toString (true));

        try (ParquetWriter<GenericRecord> writer =
                AvroParquetWriter.<GenericRecord>builder (new JaqyOutputFile (m_path))
                .withDataModel(GenericData.get())
                .withSchema (schema)
                .build ())
        {
            long count = AvroUtils.print (writer, schema, rs, schemaInfo);
            return count;
        }
    }

    @Override
    public void close ()
    {
    }
}

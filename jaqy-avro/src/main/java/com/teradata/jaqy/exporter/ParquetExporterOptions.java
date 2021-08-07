/*
 * Copyright (c) 2021 Teradata
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

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.JaqyParquetOutputFile;

/**
 * @author  Heng Yuan
 */
class ParquetExporterOptions
{
    public CompressionCodecName codec = CompressionCodecName.UNCOMPRESSED;
    public int blockSize = -1;
    public int pageSize = -1;
    public int rowCount = -1;
    public int paddingSize = -1;

    public ParquetWriter<GenericRecord> build (Path path, Schema schema) throws IOException
    {
        AvroParquetWriter.Builder<GenericRecord> builder = AvroParquetWriter.<GenericRecord>builder (new JaqyParquetOutputFile (path))
                .withDataModel(GenericData.get())
                .withCompressionCodec (codec)
                .withSchema (schema);

        if (blockSize > 0)
        {
            builder = builder.withRowGroupSize (blockSize);
        }
        if (pageSize > 0)
        {
            builder = builder.withPageSize (pageSize);
        }
        if (rowCount > 0)
        {
            builder = builder.withPageRowCountLimit (rowCount);
        }
        if (paddingSize > 0)
        {
            builder = builder.withMaxPaddingSize (paddingSize);
        }

        return builder.build ();
    }
}

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
package com.teradata.jaqy.utils;

import java.io.IOException;
import java.util.Iterator;

import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.hadoop.ParquetReader;

/**
 * @author  Heng Yuan
 */
public class JaqyParquetIterator implements Iterator<GenericRecord>
{
    private ParquetReader<GenericRecord> m_reader;
    private GenericRecord m_record;

    public JaqyParquetIterator (ParquetReader<GenericRecord> reader)
    {
        m_reader = reader;
    }

    @Override
    public boolean hasNext ()
    {
        try
        {
            m_record = m_reader.read ();
            return m_record != null;
        }
        catch (IOException ex)
        {
        }
        return false;
    }

    @Override
    public GenericRecord next ()
    {
        return m_record;
    }
}

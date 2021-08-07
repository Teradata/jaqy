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
package com.teradata.jaqy.importer;

import java.io.IOException;
import java.util.Collection;

import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePath;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.AvroUtils;
import com.teradata.jaqy.utils.JaqyParquetInputFile;

/**
 * @author Heng Yuan
 */
class ParquetImporter implements JaqyImporter
{
    private final Path m_file;
    private final JaqyConnection m_conn;
    private ParquetReader<GenericRecord> m_reader;
    private boolean m_end;
    private GenericRecord m_record;
    private String[] m_exps;

    public ParquetImporter (JaqyConnection conn, Path file) throws IOException
    {
        m_conn = conn;
        m_file = file;
        openFile (file);
    }

    private void openFile (Path file) throws IOException
    {
        if (!(file instanceof FilePath))
        {
            throw new IOException ("Unable to get file based InputStream from " + file.getPath ());
        }
        m_reader = AvroParquetReader.genericRecordReader (new JaqyParquetInputFile ((FilePath)file));
    }

    @Override
    public void close () throws IOException
    {
        m_reader.close ();
    }

    @Override
    public String getName ()
    {
        return "pq";
    }

    @Override
    public SchemaInfo getSchema () throws IOException
    {
        GenericRecord record = m_reader.read ();
        if (record == null)
        {
            return null;
        }

        SchemaInfo schema = AvroUtils.getSchema (record.getSchema (), new ParquetRecordIterator (m_reader));
        m_reader.close ();
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
            m_record = m_reader.read ();
            return m_record != null;
        }
        catch (Exception ex)
        {
        }
        m_end = true;
        m_reader.close ();
        return false;
    }

    @Override
    public void setParameters (String[] exps)
    {
        m_exps = exps;
    }

    @Override
    public Object importColumn (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception
    {
        Object obj = getObject (column - 1, paramInfo, interpreter);
        if (obj == null)
        {
            stmt.setNull (column, paramInfo.type, paramInfo.typeName);
        }
        else
        {
            stmt.getHelper ().setObject (stmt, column, paramInfo, obj, freeList, interpreter);
        }
        return obj;
    }

    private Object getObject (int index, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
    {
        JaqyHelper helper = m_conn.getHelper ();
        if (m_exps == null)
            return AvroUtils.getDbObject (m_record.get (index), paramInfo, helper);
        return AvroUtils.getDbObject (m_record.get (m_exps[index]), paramInfo, helper);
    }
}

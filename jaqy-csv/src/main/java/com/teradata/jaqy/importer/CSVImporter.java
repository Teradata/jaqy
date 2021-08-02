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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.resultset.FileBlob;
import com.teradata.jaqy.resultset.FileClob;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.CSVImportInfo;
import com.teradata.jaqy.utils.CSVUtils;
import com.teradata.jaqy.utils.FileUtils;
import com.teradata.jaqy.utils.ImporterUtils;

/**
 * @author  Heng Yuan
 */
public class CSVImporter implements JaqyImporter
{
    public final static String[] DEFAULT_NA_VALUES =
    {
        "-1.#IND", "1.#QNAN", "1.#IND", "-1.#QNAN", "#N/A N/A", "#N/A",
        "N/A", "NA", "#NA", "NULL", "NaN", "-NaN", "nan", "-nan", ""
    };

    private final Path m_file;
    private final CSVImporterOptions m_options;
    private CSVParser m_parser;
    private Map<String, Integer> m_headers;
    private Iterator<CSVRecord> m_iterator;
    private CSVRecord m_record;
    private boolean m_naFilter;
    private String[] m_naValues;
    private SchemaInfo m_schemaInfo;
    private int[] m_exps;

    public CSVImporter (Path file, CSVImporterOptions options) throws IOException
    {
        m_file = file;
        m_options = options;
        openFile (file, m_options.charset, m_options.format);
    }

    private void openFile (Path file, String charset, CSVFormat format) throws IOException
    {
        Reader reader = FileUtils.getReader (file.getInputStream (), charset);
        m_parser = format.parse (reader);
        m_headers = m_parser.getHeaderMap ();
        m_iterator = m_parser.iterator ();
        m_naValues = DEFAULT_NA_VALUES;
    }

    @Override
    public String getName ()
    {
        return "csv";
    }

    @Override
    public SchemaInfo getSchema () throws Exception
    {
        if (m_schemaInfo != null)
            return m_schemaInfo;
        String[] headers = null;
        if (m_headers != null)
        {
            HashMap<Integer, String> map = new HashMap<Integer, String> ();
            for (Map.Entry<String, Integer> entry : m_headers.entrySet ())
            {
                map.put (entry.getValue (), entry.getKey ());
            }
            int size = map.size ();
            headers = new String[size];
            for (int i = 0; i < size; ++i)
            {
                headers[i] = map.get (i);
            }
        }
        m_schemaInfo = CSVUtils.getSchemaInfo (headers, m_iterator, m_naValues, m_options.precise, m_options.scanThreshold);
        m_parser.close ();
        // reopen the file since we just did the scan
        openFile (m_file, m_options.charset, m_options.format);
        return m_schemaInfo;
    }

    @Override
    public boolean next ()
    {
        if (m_iterator.hasNext ())
        {
            m_record = m_iterator.next ();
            return true;
        }
        return false;
    }


    @Override
    public void setParameters (String[] exps)
    {
        String[] headers = ImporterUtils.getHeaders (m_headers);
        m_exps = ImporterUtils.getParameterIndexes (headers, exps);
    }

    private int getIndex (int index)
    {
        return m_exps == null ? index : m_exps[index];
    }

    @Override
    public Object importColumn (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception
    {
        JaqyHelper      helper = stmt.getHelper();
        int             index = getIndex (column - 1);
        CSVImportInfo   importInfo = m_options.importInfoMap.get (index);

        Object obj = getObject (index, importInfo, paramInfo, interpreter);

        if (obj == null)
        {
            if (importInfo == null)
            {
                helper.setCSVNull (stmt, column, paramInfo, interpreter);
            }
            else
            {
                helper.setNull (stmt, column, paramInfo, interpreter);
            }
        }
        else
        {
            if (importInfo == null)
            {
                helper.setCSVObject (stmt, column, paramInfo, obj, freeList, interpreter);
            }
            else
            {
                helper.setObject (stmt, column, paramInfo, obj, freeList, interpreter);
            }
        }
        return obj;
    }

    private Object getObject (int index, CSVImportInfo importInfo, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
    {
        try
        {
            String value = m_record.get (index);
            if (m_naFilter)
            {
                for (String f : m_naValues)
                    if (value.equals (f))
                        return null;
            }
            if (importInfo != null)
            {
                if (value.length () == 0)
                    return null;
                Path file = m_file.getRelativePath (value);
                if (!file.isFile ())
                    throw new FileNotFoundException ("External file " + file.getPath () + " was not found.");
                if (importInfo.isBinary ())
                    return new FileBlob (file);
                else
                    return new FileClob (file, importInfo.charset);
            }
            return value;
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new IOException ("Column " + (index + 1) + " is not found.");
        }
    }

    @Override
    public void close () throws IOException
    {
        m_parser.close ();
    }

    public void setNaFilter (boolean naFilter)
    {
        m_naFilter = naFilter;
    }

    public void setNaValues (String[] naValues)
    {
        if (naValues == null)
            m_naValues = DEFAULT_NA_VALUES;
        else
            m_naValues = naValues;
    }
}

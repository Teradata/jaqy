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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.CSVUtils;

/**
 * @author	Heng Yuan
 */
public class CSVImporter implements JaqyImporter<Integer>
{
	public final static String[] DEFAULT_NA_VALUES =
	{
		"-1.#IND", "1.#QNAN", "1.#IND", "-1.#QNAN", "#N/A N/A", "#N/A",
		"N/A", "NA", "#NA", "NULL", "NaN", "-NaN", "nan", "-nan", ""
	};

	private final File m_file;
	private final Charset m_charset;
	private final CSVFormat m_format;
	private final boolean m_precise;
	private CSVParser m_parser;
	private Map<String, Integer> m_headers;
	private Iterator<CSVRecord> m_iterator;
	private CSVRecord m_record;
	private boolean m_naFilter;
	private String[] m_naValues;
	private SchemaInfo m_schemaInfo;

	public CSVImporter (File file, Charset charset, CSVFormat format, boolean precise) throws IOException
	{
		m_file = file;
		m_charset = charset;
		m_format = format;
		m_precise = precise;
		openFile (file, charset, format);
	}

	private void openFile (File file, Charset charset, CSVFormat format) throws IOException
	{
		Reader reader = new InputStreamReader (new FileInputStream (file), charset);
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
		m_schemaInfo = CSVUtils.getSchemaInfo (headers, m_iterator, m_naValues, m_precise);
		m_parser.close ();
		// reopen the file since we just did the scan
		openFile (m_file, m_charset, m_format);
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
	public Object getObject (int index, ParameterInfo paramInfo) throws Exception
	{
		try
		{
			String value = m_record.get (index);
			if (!m_naFilter)
				return value;
			for (String f : m_naValues)
				if (value.equals (f))
					return null;
			return value;
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			throw new IOException ("Column " + (index + 1) + " is not found.");
		}
	}

	@Override
	public Integer getPath (String name) throws Exception
	{
		if (m_headers != null)
			return m_headers.get (name);
		else if (name.startsWith ("col"))
		{
			String str = name.substring (3);
			int index = -1;
			try
			{
				index = Integer.valueOf (str) - 1;
			}
			catch (Exception ex)
			{
			}
			if (index < 0)
				throw new IllegalArgumentException ("Invalid column name: " + name);
			return new Integer (index);
		}
		throw new IllegalArgumentException ("Invalid column name: " + name);
	}

	@Override
	public Object getObjectFromPath (Integer path, ParameterInfo paramInfo) throws Exception
	{
		return getObject ((int)path, paramInfo);
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

	@Override
	public void setNull (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo) throws Exception
	{
		stmt.setNull (column, Types.VARCHAR, "VARCHAR");
	}
}

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

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.resultset.FileBlob;
import com.teradata.jaqy.resultset.FileClob;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.CSVImportInfo;
import com.teradata.jaqy.utils.CSVUtils;
import com.teradata.jaqy.utils.StringUtils;
import com.teradata.jaqy.utils.TypesUtils;

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

	private final Path m_file;
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
	private long m_scanThreshold;
	private HashMap<Integer, CSVImportInfo> m_importInfoMap;

	public CSVImporter (Path file, Charset charset, CSVFormat format, HashMap<Integer, CSVImportInfo> importInfoMap, boolean precise, long scanThreshold) throws IOException
	{
		m_file = file;
		m_charset = charset;
		m_format = format;
		m_importInfoMap = importInfoMap;
		m_precise = precise;
		m_scanThreshold = scanThreshold;
		openFile (file, charset, format);
	}

	private void openFile (Path file, Charset charset, CSVFormat format) throws IOException
	{
		Reader reader = new InputStreamReader (file.getInputStream (), charset);
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
		m_schemaInfo = CSVUtils.getSchemaInfo (headers, m_iterator, m_naValues, m_precise, m_scanThreshold);
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
	public Object getObject (int index, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
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
			CSVImportInfo importInfo = m_importInfoMap.get (index);
			if (importInfo != null)
			{
				if (value.length () == 0)
					return null;
				Path file = m_file.getRelativePath (value);
				if (!file.isFile ())
					throw new FileNotFoundException ("External file " + file.getPath () + " is not found.");
				if (importInfo.charset == null)
					return new FileBlob (file);
				else
					return new FileClob (file, importInfo.charset);
			}
			if (TypesUtils.isBinary (paramInfo.type))
				return StringUtils.getBytesFromHexString (value);
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
	public Object getObjectFromPath (Integer path, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
	{
		return getObject ((int)path, paramInfo, interpreter);
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
		switch (paramInfo.type)
		{
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
				stmt.setNull (column, paramInfo.type);
				break;
			default:
				stmt.setObject (column, null);
		}
	}
}

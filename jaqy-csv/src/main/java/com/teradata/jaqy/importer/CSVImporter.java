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

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyImporter;

/**
 * @author	Heng Yuan
 */
class CSVImporter implements JaqyImporter<Integer>
{
	private final static String[] s_defaultNaValues =
	{
		"-1.#IND", "1.#QNAN", "1.#IND", "-1.#QNAN", "#N/A N/A", "#N/A",
		"N/A", "NA", "#NA", "NULL", "NaN", "-NaN", "nan", "-nan", ""
	};

	private final CSVParser m_parser;
	private final Map<String, Integer> m_headers;
	private final Iterator<CSVRecord> m_iterator;
	private CSVRecord m_record;
	private boolean m_naFilter;
	private String[] m_naValues;

	public CSVImporter (Reader reader, CSVFormat format) throws IOException
	{
		m_parser = format.parse (reader);
		m_headers = m_parser.getHeaderMap ();
		m_iterator = m_parser.iterator ();
		m_naValues = s_defaultNaValues;
	}

	@Override
	public String getName ()
	{
		return "csv";
	}

	@Override
	public void showSchema (Display display)
	{
		if (m_headers != null)
			display.println (null, m_headers.toString ());
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
	public Object getObject (int index, int type) throws Exception
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
	public Object getObjectFromPath (Integer path, int type) throws Exception
	{
		return getObject ((int)path, type);
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
			m_naValues = s_defaultNaValues;
		else
			m_naValues = naValues;
	}
}

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
package com.teradata.jaqy.resultset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLXML;

import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class InMemSQLXML extends SQLXMLWrapper implements Comparable<SQLXML>
{
	private String m_xml;

	public InMemSQLXML (SQLXML xml) throws SQLException
	{
		m_xml = xml.getString ();
		xml.free ();
	}

	@Override
	public void free () throws SQLException
	{
		m_xml = null;
	}

	@Override
	public InputStream getBinaryStream () throws SQLException
	{
		return new ByteArrayInputStream (m_xml.getBytes (Charset.forName ("utf-8")));
	}

	@Override
	public Reader getCharacterStream ()
	{
		return new StringReader (m_xml);
	}

	@Override
	public String getString () throws SQLException
	{
		return m_xml;
	}

	public int getLength ()
	{
		return m_xml.length ();
	}

	@Override
	public int compareTo (SQLXML o)
	{
		if (o instanceof InMemSQLXML)
		{
			return m_xml.compareTo (((InMemSQLXML)o).m_xml);
		}
		try
		{
			return FileUtils.compare (getCharacterStream(), o.getCharacterStream ());
		}
		catch (Exception ex)
		{
			// shouldn't reach here
			return -1;
		}
	}
}

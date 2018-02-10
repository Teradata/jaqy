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
import java.sql.Clob;
import java.sql.SQLException;

import com.teradata.jaqy.utils.ExceptionUtils;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class InMemClob extends ClobWrapper implements Comparable<Clob>
{
	private String m_str;

	public InMemClob (Clob clob) throws SQLException
	{
		this (clob.getSubString (1, (int)clob.length ()));
		clob.free ();
	}

	InMemClob (String str)
	{
		m_str = str;
	}

	public void replace (String str)
	{
		m_str = str;
	}

	@Override
	public long length () throws SQLException
	{
		return m_str.length ();
	}

	@Override
	public String getSubString (long pos, int length) throws SQLException
	{
		if (pos == 1 && length == m_str.length ())
			return m_str;
		return m_str.substring ((int)(pos - 1), length);
	}

	@Override
	public Reader getCharacterStream ()
	{
		return new StringReader (m_str);
	}

	@Override
	public InputStream getAsciiStream ()
	{
		return new ByteArrayInputStream (m_str.getBytes (Charset.forName ("utf-8")));
	}

	@Override
	public long position (String searchstr, long start) throws SQLException
	{
		if (start < 1)
			ExceptionUtils.getIllegalArgument ();
		return m_str.indexOf (searchstr, (int)(start - 1));
	}

	@Override
	public void free ()
	{
		m_str = null;
	}

	@Override
	public String toString ()
	{
		return m_str;
	}

	@Override
	public int compareTo (Clob o)
	{
		if (o instanceof InMemClob)
			return m_str.compareTo (((InMemClob)o).m_str);
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

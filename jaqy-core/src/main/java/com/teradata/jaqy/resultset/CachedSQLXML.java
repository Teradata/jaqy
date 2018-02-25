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

import java.io.*;
import java.sql.SQLException;
import java.sql.SQLXML;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class CachedSQLXML extends SQLXMLWrapper implements Comparable<CachedSQLXML>
{
	private int m_length;
	private File m_file;
	private String m_str;

	public CachedSQLXML (SQLXML xml, int cacheSize, char[] charBuffer) throws SQLException
	{
		try
		{
			m_file = FileUtils.createTempFile ();
			m_length = (int)FileUtils.writeFile (m_file, xml.getCharacterStream (), charBuffer);
			xml.free ();

			if (m_length <= cacheSize)
			{
				m_str = FileUtils.readString (m_file, 0, m_length);
				m_file.delete ();
				m_file = null;
			}
			else
			{
				m_str = FileUtils.readString (m_file, 0, cacheSize);
			}
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public void free ()
	{
		m_str = null;
		if (m_file != null)
		{
			m_file.delete ();
			m_file = null;
		}
	}

	@Override
	public Reader getCharacterStream ()
	{
		try
		{
			if (m_file != null)
				return new InputStreamReader (new FileInputStream (m_file), "UTF-8");
			return new StringReader (m_str);
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public String getString ()
	{
		try
		{
			if (m_file != null)
				return FileUtils.readString (m_file, 0, m_length);
			return m_str;
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public int compareTo (CachedSQLXML o)
	{
		try
		{
			// First compare the in-memory cache
			//
			// We assume the cache size is the same for both Clobs.
			// This assumption makes things a lot easier.
			int c = m_str.compareTo (o.m_str);
			if (c != 0)
				return c;
			// This is the simpler cases of both Clobs being in-memory.
			if (m_file == null && o.m_file == null)
				return 0;

			// Full comparison.
			return FileUtils.compare (getCharacterStream(), o.getCharacterStream ());
		}
		catch (Exception ex)
		{
			// shouldn't reach here
			return -1;
		}
	}

	@Override
	public void finalize ()
	{
		free ();
	}
}

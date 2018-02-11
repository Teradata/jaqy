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
import java.sql.Clob;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class CachedClob extends ClobWrapper implements Comparable<CachedClob>
{
	private long m_length;
	private String m_str;
	private File m_file;

	public CachedClob (Clob clob, int cacheSize, char[] charBuffer) throws SQLException
	{
		try
		{
			m_length = clob.length ();
			if (m_length < cacheSize)
			{
				m_str = clob.getSubString (1, (int)m_length);
				m_file = null;
			}
			else
			{
				m_str = clob.getSubString (1, cacheSize);
				if (m_length > cacheSize)
				{
					m_file = FileUtils.createTempFile ();
					FileUtils.writeFile (m_file, clob.getCharacterStream (), charBuffer);
				}
			}
			clob.free ();
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	public CachedClob (String str)
	{
		m_length = str.length ();
		m_str = str;
		m_file = null;
	}

	@Override
	public long length () throws SQLException
	{
		return m_length;
	}

	@Override
	public String getSubString (long pos, int length) throws SQLException
	{
		// most common case check
		if (pos == 1 && length == m_str.length ())
			return m_str;

		if (pos < 1 ||
			length < 0 ||
			(pos + length - 1) > m_length)
			throw new IllegalArgumentException ("Invalid arguments.");
		try
		{
			// if the data is already in the memory
			if ((pos + length - 1) <= m_str.length ())
				return m_str.substring ((int)(pos - 1), (int)(pos + length - 1));
			return FileUtils.readString (m_file, pos - 1, length);
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public Reader getCharacterStream () throws SQLException
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
	public void free ()
	{
		if (m_str != null)
		{
			m_str = null;
			if (m_file != null)
			{
				m_file.delete ();
				m_file = null;
			}
		}
	}

	@Override
	public int compareTo (CachedClob o)
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
}

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
public class FileClob extends ClobWrapper implements CloseableData, Comparable<Clob>
{
	private long m_length;
	private File m_file;

	public FileClob (Clob clob, char[] charBuffer) throws SQLException
	{
		try
		{
			m_length = clob.length ();
			m_file = FileUtils.createTempFile ();
			FileUtils.writeFile (m_file, clob.getCharacterStream (), charBuffer);
			clob.free ();
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public long length () throws SQLException
	{
		return m_length;
	}

	@Override
	public String getSubString (long pos, int length) throws SQLException
	{
		if (pos < 1 ||
			pos > length ||
			(pos + length - 1) > m_length)
			throw new IllegalArgumentException ("Invalid arguments.");
		try
		{
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
			return new InputStreamReader (new FileInputStream (m_file), "UTF-8");
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public InputStream getAsciiStream () throws SQLException
	{
		try
		{
			return new FileInputStream (m_file);
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public void free ()
	{
		m_file.delete ();
	}

	@Override
	public void close ()
	{
		free ();
	}

	@Override
	public int compareTo (Clob o)
	{
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

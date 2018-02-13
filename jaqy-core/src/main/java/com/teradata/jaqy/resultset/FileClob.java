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
import java.nio.charset.Charset;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class FileClob extends ClobWrapper
{
	private long m_length;
	private File m_file;
	private Charset m_charset;

	public FileClob (File file, Charset charset) throws SQLException
	{
		m_length = -1;
		m_file = file;
		m_charset = charset;
	}

	@Override
	public long length () throws SQLException
	{
		if (m_length < 0)
		{
			try
			{
				m_length = FileUtils.getLength (m_file, m_charset);
			}
			catch (IOException ex)
			{
				throw new JaqyException (ex);
			}
		}
		return m_length;
	}

	@Override
	public String getSubString (long pos, int length) throws SQLException
	{
		if (pos < 1 ||
			length < 0 ||
			(pos + length - 1) > m_length)
			throw new IllegalArgumentException ("Invalid arguments.");
		try
		{
			return FileUtils.readString (m_file, m_charset, pos - 1, length);
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
			return new InputStreamReader (new FileInputStream (m_file), m_charset);
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public void free ()
	{
		m_file = null;
	}
}

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
package com.teradata.jaqy.resultset;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class FileBlob extends BlobWrapper
{
	private Path m_file;

	public FileBlob (Path file) throws SQLException
	{
		m_file = file;
	}

	@Override
	public long length ()
	{
		return m_file.length ();
	}

	@Override
	public byte[] getBytes (long pos, int length) throws SQLException
	{
		if (pos < 1 ||
			length < 0 ||
			(pos + length - 1) > m_file.length ())
			throw new IllegalArgumentException ("Invalid arguments");
		try
		{
			return FileUtils.readFile (m_file, pos - 1, length);
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public InputStream getBinaryStream () throws SQLException
	{
		try
		{
			return m_file.getInputStream ();
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

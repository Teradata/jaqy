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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class FileBlob extends BlobWrapper implements CloseableData, Comparable<Blob>
{
	private long m_length;
	private File m_file;

	public FileBlob (Blob blob, byte[] byteBuffer) throws SQLException
	{
		try
		{
			m_length = blob.length ();
			m_file = FileUtils.createTempFile ();
			FileUtils.writeFile (m_file, blob.getBinaryStream (), byteBuffer);
			blob.free ();
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	@Override
	public long length ()
	{
		return m_length;
	}

	@Override
	public byte[] getBytes (long pos, int length) throws SQLException
	{
		if (pos < 1 ||
			pos > length ||
			(pos + length - 1) > m_length)
			throw new SQLException ("Invalid arguments");
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
	public InputStream getBinaryStream ()
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
	public int compareTo (Blob o)
	{
		try
		{
			return FileUtils.compare (getBinaryStream(), o.getBinaryStream ());
		}
		catch (Exception ex)
		{
			// shouldn't reach here
			return -1;
		}
	}

	@Override
	public void close ()
	{
		free ();
	}
}

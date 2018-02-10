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
import java.sql.Blob;
import java.sql.SQLException;

import com.teradata.jaqy.utils.ByteArrayUtils;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class InMemBlob extends BlobWrapper implements Comparable<Blob>
{
	private byte[] m_bytes;

	public InMemBlob (Blob blob) throws SQLException
	{
		this (blob.getBytes (1, (int)blob.length ()));
		blob.free ();
	}

	InMemBlob (byte[] bytes)
	{
		m_bytes = bytes;
	}

	@Override
	public long length ()
	{
		return m_bytes.length;
	}

	@Override
	public byte[] getBytes (long pos, int length) throws SQLException
	{
		if (pos == 1 &&
			length == m_bytes.length)
			return m_bytes;
		if ((pos - 1 + length) > m_bytes.length)
			throw new SQLException ("Invalid arguments");
		byte[] bytes = new byte[length];
		System.arraycopy (m_bytes, 0, bytes, (int)(pos - 1), length);
		return null;
	}

	@Override
	public InputStream getBinaryStream ()
	{
		return new ByteArrayInputStream (m_bytes);
	}

	@Override
	public void free ()
	{
		m_bytes = null;
	}

	@Override
	public int compareTo (Blob o)
	{
		if (o instanceof InMemBlob)
			return ByteArrayUtils.compare (m_bytes, ((InMemBlob)o).m_bytes);
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
}

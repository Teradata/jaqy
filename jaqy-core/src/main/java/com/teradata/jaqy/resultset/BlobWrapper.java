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

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import com.teradata.jaqy.utils.ExceptionUtils;

/**
 * @author	Heng Yuan
 */
class BlobWrapper implements Blob
{
	@Override
	public long length () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public byte[] getBytes (long pos, int length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public InputStream getBinaryStream () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public long position (byte[] pattern, long start) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public long position (Blob pattern, long start) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int setBytes (long pos, byte[] bytes) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int setBytes (long pos, byte[] bytes, int offset, int len) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public OutputStream setBinaryStream (long pos) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void truncate (long len) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void free () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public InputStream getBinaryStream (long pos, long length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}
}

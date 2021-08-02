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
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import com.teradata.jaqy.utils.ExceptionUtils;

/**
 * @author	Heng Yuan
 */
class ClobWrapper implements Clob
{
	@Override
	public long length () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public String getSubString (long pos, int length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Reader getCharacterStream () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public InputStream getAsciiStream () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public long position (String searchstr, long start) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public long position (Clob searchstr, long start) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int setString (long pos, String str) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int setString (long pos, String str, int offset, int len) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public OutputStream setAsciiStream (long pos) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Writer setCharacterStream (long pos) throws SQLException
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
	public Reader getCharacterStream (long pos, long length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}
}

/*
 * Copyright (c) 2017 Teradata
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
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;

/**
 * @author	Heng Yuan
 */
class InMemNClob implements NClob
{
	private String m_str;

	InMemNClob (NClob clob) throws SQLException
	{
		this (clob.getSubString (1, (int)clob.length ()));
		clob.free ();
	}

	InMemNClob (String str)
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
	public Reader getCharacterStream () throws SQLException
	{
		return new StringReader (m_str);
	}

	@Override
	public InputStream getAsciiStream () throws SQLException
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
		m_str = null;
	}

	@Override
	public Reader getCharacterStream (long pos, long length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public String toString ()
	{
		return m_str;
	}
}

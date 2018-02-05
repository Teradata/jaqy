package com.teradata.jaqy.resultset;

import java.io.*;
import java.sql.Clob;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.utils.ExceptionUtils;
import com.teradata.jaqy.utils.FileUtils;

public class FileClob implements Clob
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
		}
		catch (IOException ex)
		{
			throw new JaqyException (ex);
		}
	}

	public FileClob (String str)
	{
		try
		{
			m_length = str.length ();
			m_file = FileUtils.createTempFile ();
			FileUtils.writeFile (m_file, str);
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
		m_file.delete ();
	}

	@Override
	public Reader getCharacterStream (long pos, long length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}
}

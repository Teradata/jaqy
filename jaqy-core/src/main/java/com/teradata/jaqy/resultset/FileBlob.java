package com.teradata.jaqy.resultset;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.utils.ExceptionUtils;
import com.teradata.jaqy.utils.FileUtils;

public class FileBlob implements Blob, CloseableData, Comparable<Blob>
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

	public FileBlob (byte[] bytes)
	{
		try
		{
			m_length = bytes.length;
			m_file = FileUtils.createTempFile ();
			FileUtils.writeFile (m_file, bytes);
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
	public void free ()
	{
		m_file.delete ();
	}

	@Override
	public InputStream getBinaryStream (long pos, long length) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
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
		m_file.delete ();
	}
}

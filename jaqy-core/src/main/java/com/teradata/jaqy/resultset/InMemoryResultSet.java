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
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.utils.ExceptionUtils;

/**
 * This class keeps a copy of ResultSet passed in.
 *
 * @author	Heng Yuan
 */
public class InMemoryResultSet implements ResultSet
{
	private ArrayList<Object[]> m_rows = new ArrayList<Object[]> ();
	private InMemoryResultSetMetaData m_meta;
	private int m_columnCount;
	private boolean m_closed;
	private int m_rowId;
	private Object[] m_row;
	private int m_dir = 1;
	private boolean m_wasNull;
	private Statement m_statement;

	public InMemoryResultSet (ResultSet rs) throws SQLException
	{
		m_meta = new InMemoryResultSetMetaData (rs.getMetaData ());
		m_statement = rs.getStatement ();
		m_columnCount = m_meta.getColumnCount ();
		while (rs.next ())
		{
			Object[] row = new Object[m_columnCount];
			for (int i = 0; i < m_columnCount; ++i)
			{
				Object o = rs.getObject (i + 1);
				if (o instanceof Blob)
				{
					row[i] = new InMemBlob ((Blob)o);
				}
				else if (o instanceof NClob)
				{
					row[i] = new InMemNClob ((NClob)o);
				}
				else if (o instanceof Clob)
				{
					row[i] = new InMemClob ((Clob)o);
				}
				else if (o instanceof SQLXML)
				{
					row[i] = new InMemSQLXML ((SQLXML)o);
				}
				else
				{
					row[i] = o;
				}
			}
			m_rows.add (row);
		}
	}

	public InMemoryResultSet (PropertyTable pt) throws SQLException
	{
		m_meta = new InMemoryResultSetMetaData (pt);
		m_statement = null;
		m_columnCount = m_meta.getColumnCount ();
		m_rows = pt.getRows ();
	}

	public ArrayList<Object[]> getRows ()
	{
		return m_rows;
	}

	private void checkClosed () throws SQLException
	{
		if (m_closed)
			throw ExceptionUtils.getClosed ();
	}

	private void checkColumnIndex (int column) throws SQLException
	{
		if (column < 1 || column > m_columnCount)
			throw ExceptionUtils.getInvalidColumnIndex (column);
		if (m_row == null)
			throw ExceptionUtils.getInvalidRow ();
		if (m_row[column - 1] == null)
			m_wasNull = true;
	}

	@Override
	public <T> T unwrap (Class<T> iface) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean isWrapperFor (Class<?> iface) throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean next () throws SQLException
	{
		return relative (1);
	}

	@Override
	public void close () throws SQLException
	{
		m_rows = null;
		m_meta = null;
		m_closed = true;
		m_statement = null;
	}

	@Override
	public boolean wasNull () throws SQLException
	{
		checkClosed ();
		return m_wasNull;
	}

	@Override
	public String getString (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;
		return o.toString ();
	}

	@Override
	public boolean getBoolean (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return false;

		int type = m_meta.getColumnType (columnIndex);
		switch (type)
		{
			case Types.NULL:
				return false;
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
				if (o instanceof Number)
				{
					int v = ((Number)o).intValue ();
					if (v == 0)
						return false;
					if (v == 1)
						return true;
				}
				break;
			case Types.CHAR:
			case Types.VARCHAR:
				if (o instanceof CharSequence &&
					((CharSequence)o).length () == 1)
				{
					char ch = ((CharSequence)o).charAt (0);
					if ('0' == ch)
						return false;
					if ('1' == ch)
						return true;
				}
				break;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public byte getByte (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return 0;

		if (o instanceof Number)
		{
			return ((Number)o).byteValue ();
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public short getShort (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return 0;

		if (o instanceof Number)
		{
			return ((Number)o).shortValue ();
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public int getInt (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return 0;

		if (o instanceof Number)
		{
			return ((Number)o).intValue ();
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public long getLong (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return 0;

		if (o instanceof Number)
		{
			return ((Number)o).longValue ();
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public float getFloat (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return 0;

		if (o instanceof Number)
		{
			return ((Number)o).floatValue ();
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public double getDouble (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return 0;

		if (o instanceof Number)
		{
			return ((Number)o).doubleValue ();
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public BigDecimal getBigDecimal (int columnIndex, int scale) throws SQLException
	{
		return getBigDecimal (columnIndex);
	}

	@Override
	public byte[] getBytes (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof byte[])
		{
			return (byte[])o;
		}
		if (o instanceof Blob)
		{
			return ((Blob)o).getBytes (1, (int)((Blob)o).length ());
		}

		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Date getDate (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Date)
		{
			return (Date)o;
		}
		if (o instanceof Integer)
		{
			// This is integer date from Unix Epoch (1970 / 1 / 1)
			int d = ((Integer)o).intValue ();
			long time = (long)d * 24 * 3600;
			return new Date (time);
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public Time getTime (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Time)
		{
			return (Time)o;
		}
		if (o instanceof Integer)
		{
			// milisecond precision time
			int t = ((Integer)o).intValue ();
			return new Time ((long)t);
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public Timestamp getTimestamp (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Timestamp)
		{
			return (Timestamp)o;
		}
		if (o instanceof Long)
		{
			// milisecond precision timestamp
			long t = ((Long)o).longValue ();
			return new Timestamp (t);
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public InputStream getAsciiStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		return new ByteArrayInputStream (o.toString ().getBytes (Charset.forName ("us_ascii")));
	}

	@Override
	public InputStream getUnicodeStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		return new ByteArrayInputStream (o.toString ().getBytes (Charset.forName ("utf-8")));
	}

	@Override
	public InputStream getBinaryStream (int columnIndex) throws SQLException
	{
		return new ByteArrayInputStream (getBytes (columnIndex));
	}

	@Override
	public String getString (String columnLabel) throws SQLException
	{
		return getString (findColumn (columnLabel));
	}

	@Override
	public boolean getBoolean (String columnLabel) throws SQLException
	{
		return getBoolean (findColumn (columnLabel));
	}

	@Override
	public byte getByte (String columnLabel) throws SQLException
	{
		return getByte (findColumn (columnLabel));
	}

	@Override
	public short getShort (String columnLabel) throws SQLException
	{
		return getShort (findColumn (columnLabel));
	}

	@Override
	public int getInt (String columnLabel) throws SQLException
	{
		return getInt (findColumn (columnLabel));
	}

	@Override
	public long getLong (String columnLabel) throws SQLException
	{
		return getLong (findColumn (columnLabel));
	}

	@Override
	public float getFloat (String columnLabel) throws SQLException
	{
		return getFloat (findColumn (columnLabel));
	}

	@Override
	public double getDouble (String columnLabel) throws SQLException
	{
		return getDouble (findColumn (columnLabel));
	}

	@Override
	public BigDecimal getBigDecimal (String columnLabel, int scale) throws SQLException
	{
		return getBigDecimal (findColumn (columnLabel), scale);
	}

	@Override
	public byte[] getBytes (String columnLabel) throws SQLException
	{
		return getBytes (findColumn (columnLabel));
	}

	@Override
	public Date getDate (String columnLabel) throws SQLException
	{
		return getDate (findColumn (columnLabel));
	}

	@Override
	public Time getTime (String columnLabel) throws SQLException
	{
		return getTime (findColumn (columnLabel));
	}

	@Override
	public Timestamp getTimestamp (String columnLabel) throws SQLException
	{
		return getTimestamp (findColumn (columnLabel));
	}

	@Override
	public InputStream getAsciiStream (String columnLabel) throws SQLException
	{
		return getAsciiStream (findColumn (columnLabel));
	}

	@Override
	public InputStream getUnicodeStream (String columnLabel) throws SQLException
	{
		return getUnicodeStream (findColumn (columnLabel));
	}

	@Override
	public InputStream getBinaryStream (String columnLabel) throws SQLException
	{
		return getBinaryStream (findColumn (columnLabel));
	}

	@Override
	public SQLWarning getWarnings () throws SQLException
	{
		checkClosed ();
		return null;
	}

	@Override
	public void clearWarnings () throws SQLException
	{
		checkClosed ();
	}

	@Override
	public String getCursorName () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public ResultSetMetaData getMetaData () throws SQLException
	{
		checkClosed ();
		return m_meta;
	}

	@Override
	public Object getObject (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);
		return m_row[columnIndex - 1];
	}

	@Override
	public Object getObject (String columnLabel) throws SQLException
	{
		return getObject (findColumn (columnLabel));
	}

	@Override
	public int findColumn (String columnLabel) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Reader getCharacterStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		return new StringReader (o.toString ());
	}

	@Override
	public Reader getCharacterStream (String columnLabel) throws SQLException
	{
		return getCharacterStream (findColumn (columnLabel));
	}

	@Override
	public BigDecimal getBigDecimal (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof BigDecimal)
			return (BigDecimal)o;

		if (o instanceof Double ||
			o instanceof Float)
		{
			return BigDecimal.valueOf (((Number)o).doubleValue ());
		}
		if (o instanceof Number)
		{
			return BigDecimal.valueOf (((Number)o).longValue ());
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public BigDecimal getBigDecimal (String columnLabel) throws SQLException
	{
		return getBigDecimal (findColumn (columnLabel));
	}

	@Override
	public boolean isBeforeFirst () throws SQLException
	{
		checkClosed ();
		return m_rowId == 0;
	}

	@Override
	public boolean isAfterLast () throws SQLException
	{
		checkClosed ();
		return m_rowId > m_rows.size ();
	}

	@Override
	public boolean isFirst () throws SQLException
	{
		checkClosed ();
		return m_rowId == 0 && m_row != null;
	}

	@Override
	public boolean isLast () throws SQLException
	{
		checkClosed ();
		return m_rowId == m_rows.size () && m_row != null;
	}

	@Override
	public void beforeFirst () throws SQLException
	{
		checkClosed ();
		m_rowId = 0;
		m_row = null;
	}

	@Override
	public void afterLast () throws SQLException
	{
		checkClosed ();
		m_rowId = m_rows.size () + 1;
	}

	@Override
	public boolean first () throws SQLException
	{
		checkClosed ();
		m_rowId = 0;
		if (m_rows.size () == 0)
		{
			m_row = null;
			return false;
		}
		m_row = m_rows.get (0);
		return true;
	}

	@Override
	public boolean last () throws SQLException
	{
		checkClosed ();
		m_rowId = m_rows.size ();
		if (m_rows.size () == 0)
		{
			m_row = null;
			return false;
		}
		m_row = m_rows.get (m_rowId - 1);
		return true;
	}

	@Override
	public int getRow () throws SQLException
	{
		checkClosed ();
		return m_rowId;
	}

	@Override
	public boolean absolute (int row) throws SQLException
	{
		checkClosed ();
		if (row > 0)
		{
			if (row > m_rows.size ())
			{
				m_rowId = m_rows.size () + 1;
				m_row = null;
				return false;
			}
			m_rowId = row;
			m_row = m_rows.get (row - 1);
			return true;
		}
		else if (row < 0)
		{
			m_rowId = m_rows.size () + 1 - row;
			if (m_rowId <= 0)
			{
				m_rowId = 0;
				m_row = null;
				return false;
			}
			m_row = m_rows.get (m_rowId - 1);
			return true;
		}
		else
		{
			m_rowId = 0;
			m_row = null;
			return false;
		}
	}

	@Override
	public boolean relative (int rows) throws SQLException
	{
		checkClosed ();

		if (m_dir > 0)
			m_rowId += rows;
		else
			m_rowId -= rows;

		if (m_rowId <= 0)
		{
			m_rowId = 0;
			m_row = null;
			return false;
		}
		if (m_rowId > m_rows.size ())
		{
			m_rowId = m_rows.size () + 1;
			m_row = null;
			return false;
		}
		m_row = m_rows.get (m_rowId - 1);
		return true;
	}

	@Override
	public boolean previous () throws SQLException
	{
		return relative (-1);
	}

	@Override
	public void setFetchDirection (int direction) throws SQLException
	{
		checkClosed ();
		switch (direction)
		{
			case FETCH_UNKNOWN:
			case FETCH_FORWARD:
				m_dir = 1;
				break;
			case FETCH_REVERSE:
				m_dir = -1;
				break;
		}
		ExceptionUtils.getIllegalArgument ();
	}

	@Override
	public int getFetchDirection () throws SQLException
	{
		checkClosed ();
		if (m_dir == 1)
			return FETCH_FORWARD;
		return FETCH_REVERSE;
	}

	@Override
	public void setFetchSize (int rows) throws SQLException
	{
		checkClosed ();
	}

	@Override
	public int getFetchSize () throws SQLException
	{
		checkClosed ();
		return m_rows.size ();
	}

	@Override
	public int getType () throws SQLException
	{
		checkClosed ();
		return TYPE_SCROLL_INSENSITIVE;
	}

	@Override
	public int getConcurrency () throws SQLException
	{
		checkClosed ();
		return CONCUR_READ_ONLY;
	}

	@Override
	public boolean rowUpdated () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean rowInserted () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean rowDeleted () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNull (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBoolean (int columnIndex, boolean x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateByte (int columnIndex, byte x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateShort (int columnIndex, short x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateInt (int columnIndex, int x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateLong (int columnIndex, long x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateFloat (int columnIndex, float x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateDouble (int columnIndex, double x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBigDecimal (int columnIndex, BigDecimal x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateString (int columnIndex, String x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBytes (int columnIndex, byte[] x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateDate (int columnIndex, Date x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateTime (int columnIndex, Time x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateTimestamp (int columnIndex, Timestamp x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateAsciiStream (int columnIndex, InputStream x, int length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBinaryStream (int columnIndex, InputStream x, int length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateCharacterStream (int columnIndex, Reader x, int length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateObject (int columnIndex, Object x, int scaleOrLength) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateObject (int columnIndex, Object x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNull (String columnLabel) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBoolean (String columnLabel, boolean x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateByte (String columnLabel, byte x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateShort (String columnLabel, short x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateInt (String columnLabel, int x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateLong (String columnLabel, long x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateFloat (String columnLabel, float x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateDouble (String columnLabel, double x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBigDecimal (String columnLabel, BigDecimal x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateString (String columnLabel, String x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBytes (String columnLabel, byte[] x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateDate (String columnLabel, Date x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateTime (String columnLabel, Time x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateTimestamp (String columnLabel, Timestamp x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateAsciiStream (String columnLabel, InputStream x, int length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBinaryStream (String columnLabel, InputStream x, int length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateCharacterStream (String columnLabel, Reader reader, int length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateObject (String columnLabel, Object x, int scaleOrLength) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateObject (String columnLabel, Object x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void insertRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void deleteRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void refreshRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void cancelRowUpdates () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void moveToInsertRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void moveToCurrentRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Statement getStatement () throws SQLException
	{
		checkClosed ();
		return m_statement;
	}

	@Override
	public Object getObject (int columnIndex, Map<String, Class<?>> map) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Ref getRef (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Ref)
		{
			return (Ref)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public Blob getBlob (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Blob)
		{
			return (Blob)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public Clob getClob (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Clob)
		{
			return (Clob)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public Array getArray (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof Array)
		{
			return (Array)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public Object getObject (String columnLabel, Map<String, Class<?>> map) throws SQLException
	{
		return getObject (findColumn (columnLabel), map);
	}

	@Override
	public Ref getRef (String columnLabel) throws SQLException
	{
		return getRef (findColumn (columnLabel));
	}

	@Override
	public Blob getBlob (String columnLabel) throws SQLException
	{
		return getBlob (findColumn (columnLabel));
	}

	@Override
	public Clob getClob (String columnLabel) throws SQLException
	{
		return getClob (findColumn (columnLabel));
	}

	@Override
	public Array getArray (String columnLabel) throws SQLException
	{
		return getArray (findColumn (columnLabel));
	}

	@Override
	public Date getDate (int columnIndex, Calendar cal) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Date getDate (String columnLabel, Calendar cal) throws SQLException
	{
		return getDate (findColumn (columnLabel), cal);
	}

	@Override
	public Time getTime (int columnIndex, Calendar cal) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Time getTime (String columnLabel, Calendar cal) throws SQLException
	{
		return getTime (findColumn (columnLabel), cal);
	}

	@Override
	public Timestamp getTimestamp (int columnIndex, Calendar cal) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Timestamp getTimestamp (String columnLabel, Calendar cal) throws SQLException
	{
		return getTimestamp (findColumn (columnLabel), cal);
	}

	@Override
	public URL getURL (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof URL)
		{
			return (URL)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public URL getURL (String columnLabel) throws SQLException
	{
		return getURL (findColumn (columnLabel));
	}

	@Override
	public void updateRef (int columnIndex, Ref x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateRef (String columnLabel, Ref x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBlob (int columnIndex, Blob x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBlob (String columnLabel, Blob x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateClob (int columnIndex, Clob x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateClob (String columnLabel, Clob x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateArray (int columnIndex, Array x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateArray (String columnLabel, Array x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public RowId getRowId (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof RowId)
		{
			return (RowId)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public RowId getRowId (String columnLabel) throws SQLException
	{
		return getRowId (findColumn (columnLabel));
	}

	@Override
	public void updateRowId (int columnIndex, RowId x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateRowId (String columnLabel, RowId x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getHoldability () throws SQLException
	{
		checkClosed ();
		return HOLD_CURSORS_OVER_COMMIT;
	}

	@Override
	public boolean isClosed () throws SQLException
	{
		return m_closed;
	}

	@Override
	public void updateNString (int columnIndex, String nString) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNString (String columnLabel, String nString) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNClob (int columnIndex, NClob nClob) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNClob (String columnLabel, NClob nClob) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public NClob getNClob (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof NClob)
		{
			return (NClob)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public NClob getNClob (String columnLabel) throws SQLException
	{
		return getNClob (findColumn (columnLabel));
	}

	@Override
	public SQLXML getSQLXML (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;

		if (o instanceof SQLXML)
		{
			return (SQLXML)o;
		}

		throw ExceptionUtils.getCannotCast ();
	}

	@Override
	public SQLXML getSQLXML (String columnLabel) throws SQLException
	{
		return getSQLXML (findColumn (columnLabel));
	}

	@Override
	public void updateSQLXML (int columnIndex, SQLXML xmlObject) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateSQLXML (String columnLabel, SQLXML xmlObject) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public String getNString (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;
		return o.toString ();
	}

	@Override
	public String getNString (String columnLabel) throws SQLException
	{
		return getNString (findColumn (columnLabel));
	}

	@Override
	public Reader getNCharacterStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;
		return new StringReader (o.toString ());
	}

	@Override
	public Reader getNCharacterStream (String columnLabel) throws SQLException
	{
		return getNCharacterStream (findColumn (columnLabel));
	}

	@Override
	public void updateNCharacterStream (int columnIndex, Reader x, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNCharacterStream (String columnLabel, Reader reader, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateAsciiStream (int columnIndex, InputStream x, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBinaryStream (int columnIndex, InputStream x, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateCharacterStream (int columnIndex, Reader x, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateAsciiStream (String columnLabel, InputStream x, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBinaryStream (String columnLabel, InputStream x, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateCharacterStream (String columnLabel, Reader reader, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBlob (int columnIndex, InputStream inputStream, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBlob (String columnLabel, InputStream inputStream, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateClob (int columnIndex, Reader reader, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateClob (String columnLabel, Reader reader, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNClob (int columnIndex, Reader reader, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNClob (String columnLabel, Reader reader, long length) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNCharacterStream (int columnIndex, Reader x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNCharacterStream (String columnLabel, Reader reader) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateAsciiStream (int columnIndex, InputStream x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBinaryStream (int columnIndex, InputStream x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateCharacterStream (int columnIndex, Reader x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateAsciiStream (String columnLabel, InputStream x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBinaryStream (String columnLabel, InputStream x) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateCharacterStream (String columnLabel, Reader reader) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBlob (int columnIndex, InputStream inputStream) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateBlob (String columnLabel, InputStream inputStream) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateClob (int columnIndex, Reader reader) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateClob (String columnLabel, Reader reader) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNClob (int columnIndex, Reader reader) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void updateNClob (String columnLabel, Reader reader) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public <T> T getObject (int columnIndex, Class<T> type) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public <T> T getObject (String columnLabel, Class<T> type) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}
}

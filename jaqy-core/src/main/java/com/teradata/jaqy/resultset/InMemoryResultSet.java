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
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

import com.teradata.jaqy.utils.ExceptionUtils;
import com.teradata.jaqy.utils.RSSorter;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;
import com.teradata.jaqy.utils.SortInfo;

/**
 * This class keeps a copy of ResultSet passed in.
 *
 * @author	Heng Yuan
 */
public class InMemoryResultSet extends ResultSetWrapper
{
	private ArrayList<Object[]> m_rows = new ArrayList<Object[]> ();
	private ResultSetMetaData m_meta;
	private int m_columnCount;
	private boolean m_closed;
	private int m_rowId;
	private Object[] m_row;
	private int m_dir = 1;
	private boolean m_wasNull;
	private Statement m_statement;
	private boolean m_hasLob;

	public InMemoryResultSet (ArrayList<Object[]> rows, InMemoryResultSetMetaData rsmd, Statement stmt)
	{
		m_meta = rsmd;
		m_statement = stmt;
		m_columnCount = rsmd.getColumnCount ();
		m_rows = rows;
	}

	public ArrayList<Object[]> getRows ()
	{
		return m_rows;
	}

	@Override
	public int findColumn (String columnLabel) throws SQLException
	{
		checkClosed ();
		return ResultSetMetaDataUtils.findColumn (m_meta, columnLabel);
	}

	private void checkColumnIndex (int column) throws SQLException
	{
		if (column < 1 || column > m_columnCount)
			throw ExceptionUtils.getInvalidColumnIndex (column);
		if (m_row == null)
			throw ExceptionUtils.getInvalidRow ();
		m_wasNull = (m_row[column - 1] == null);
	}

	@Override
	public boolean next () throws SQLException
	{
		return relative (1);
	}

	@Override
	public void close ()
	{
		if (m_hasLob)
		{
			for (Object[] row : m_rows)
			{
				for (Object o : row)
				{
					if (o instanceof CachedClob)
						((CachedClob)o).free ();
					else if (o instanceof CachedBlob)
						((CachedBlob)o).free ();
					else if (o instanceof CachedSQLXML)
						((CachedSQLXML)o).free ();
				}
			}
			m_hasLob = false;
		}
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
			return new Time (t);
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
			default:
				ExceptionUtils.getIllegalArgument ();
				break;
		}
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
	public Statement getStatement () throws SQLException
	{
		checkClosed ();
		return m_statement;
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
	public int getHoldability () throws SQLException
	{
		checkClosed ();
		return HOLD_CURSORS_OVER_COMMIT;
	}

	@Override
	public boolean isClosed ()
	{
		return m_closed;
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
	public Reader getNCharacterStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		checkColumnIndex (columnIndex);

		Object o = m_row[columnIndex - 1];
		if (o == null)
			return null;
		return new StringReader (o.toString ());
	}

	public void sort (SortInfo[] sortInfos) throws SQLException
	{
		Collections.sort (m_rows, RSSorter.createSorter (m_meta, sortInfos));
	}


	@Override
	public void finalize ()
	{
		close ();
	}

	public void setHasLob (boolean b)
	{
		m_hasLob = b;
	}
}

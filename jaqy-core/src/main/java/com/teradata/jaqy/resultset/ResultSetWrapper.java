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
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

import com.teradata.jaqy.utils.ExceptionUtils;

/**
 * @author	Heng Yuan
 */
class ResultSetWrapper implements ResultSet
{
	void checkClosed () throws SQLException
	{
		if (isClosed ())
			throw ExceptionUtils.getClosed ();
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
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void close () throws SQLException
	{
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean wasNull () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public String getString (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean getBoolean (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public byte getByte (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public short getShort (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getInt (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public long getLong (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public float getFloat (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public double getDouble (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public BigDecimal getBigDecimal (int columnIndex, int scale) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public byte[] getBytes (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Date getDate (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Time getTime (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Timestamp getTimestamp (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public InputStream getAsciiStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public InputStream getUnicodeStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public InputStream getBinaryStream (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public String getString (String columnLabel) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void clearWarnings () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Object getObject (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean isAfterLast () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean isFirst () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean isLast () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void beforeFirst () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void afterLast () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean first () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean last () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getRow () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean absolute (int row) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean relative (int rows) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean previous () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void setFetchDirection (int direction) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getFetchDirection () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public void setFetchSize (int rows) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getFetchSize () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getType () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public int getConcurrency () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		updateNull (findColumn (columnLabel));
	}

	@Override
	public void updateBoolean (String columnLabel, boolean x) throws SQLException
	{
		updateBoolean (findColumn (columnLabel), x);
	}

	@Override
	public void updateByte (String columnLabel, byte x) throws SQLException
	{
		updateByte (findColumn (columnLabel), x);
	}

	@Override
	public void updateShort (String columnLabel, short x) throws SQLException
	{
		updateShort (findColumn (columnLabel), x);
	}

	@Override
	public void updateInt (String columnLabel, int x) throws SQLException
	{
		updateInt (findColumn (columnLabel), x);
	}

	@Override
	public void updateLong (String columnLabel, long x) throws SQLException
	{
		updateLong (findColumn (columnLabel), x);
	}

	@Override
	public void updateFloat (String columnLabel, float x) throws SQLException
	{
		updateFloat (findColumn (columnLabel), x);
	}

	@Override
	public void updateDouble (String columnLabel, double x) throws SQLException
	{
		updateDouble (findColumn (columnLabel), x);
	}

	@Override
	public void updateBigDecimal (String columnLabel, BigDecimal x) throws SQLException
	{
		updateBigDecimal (findColumn (columnLabel), x);
	}

	@Override
	public void updateString (String columnLabel, String x) throws SQLException
	{
		updateString (findColumn (columnLabel), x);
	}

	@Override
	public void updateBytes (String columnLabel, byte[] x) throws SQLException
	{
		updateBytes (findColumn (columnLabel), x);
	}

	@Override
	public void updateDate (String columnLabel, Date x) throws SQLException
	{
		updateDate (findColumn (columnLabel), x);
	}

	@Override
	public void updateTime (String columnLabel, Time x) throws SQLException
	{
		updateTime (findColumn (columnLabel), x);
	}

	@Override
	public void updateTimestamp (String columnLabel, Timestamp x) throws SQLException
	{
		updateTimestamp (findColumn (columnLabel), x);
	}

	@Override
	public void updateAsciiStream (String columnLabel, InputStream x, int length) throws SQLException
	{
		updateAsciiStream (findColumn (columnLabel), x, length);
	}

	@Override
	public void updateBinaryStream (String columnLabel, InputStream x, int length) throws SQLException
	{
		updateBinaryStream (findColumn (columnLabel), x, length);
	}

	@Override
	public void updateCharacterStream (String columnLabel, Reader reader, int length) throws SQLException
	{
		updateCharacterStream (findColumn (columnLabel), reader, length);
	}

	@Override
	public void updateObject (String columnLabel, Object x, int scaleOrLength) throws SQLException
	{
		updateObject (findColumn (columnLabel), x, scaleOrLength);
	}

	@Override
	public void updateObject (String columnLabel, Object x) throws SQLException
	{
		updateObject (findColumn (columnLabel), x);
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
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Blob getBlob (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Clob getClob (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public Array getArray (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
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
		updateRef (findColumn (columnLabel), x);
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
		updateBlob (findColumn (columnLabel), x);
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
		updateClob (findColumn (columnLabel), x);
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
		updateArray (findColumn (columnLabel), x);
	}

	@Override
	public RowId getRowId (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		updateRowId (findColumn (columnLabel), x);
	}

	@Override
	public int getHoldability () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
	}

	@Override
	public boolean isClosed () throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		updateNString (findColumn (columnLabel), nString);
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
		updateNClob (findColumn (columnLabel), nClob);
	}

	@Override
	public NClob getNClob (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
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
		updateSQLXML (findColumn (columnLabel), xmlObject);
	}

	@Override
	public String getNString (int columnIndex) throws SQLException
	{
		checkClosed ();
		throw ExceptionUtils.getNotImplemented ();
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
		throw ExceptionUtils.getNotImplemented ();
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
		updateNCharacterStream (findColumn (columnLabel), reader, length);
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
		updateAsciiStream (findColumn (columnLabel), x, length);
	}

	@Override
	public void updateBinaryStream (String columnLabel, InputStream x, long length) throws SQLException
	{
		updateBinaryStream (findColumn (columnLabel), x, length);
	}

	@Override
	public void updateCharacterStream (String columnLabel, Reader reader, long length) throws SQLException
	{
		updateCharacterStream (findColumn (columnLabel), reader, length);
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
		updateBlob (findColumn (columnLabel), inputStream, length);
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
		updateClob (findColumn (columnLabel), reader, length);
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
		updateNClob (findColumn (columnLabel), reader, length);
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
		updateNCharacterStream (findColumn (columnLabel), reader);
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
		updateAsciiStream (findColumn (columnLabel), x);
	}

	@Override
	public void updateBinaryStream (String columnLabel, InputStream x) throws SQLException
	{
		updateBinaryStream (findColumn (columnLabel), x);
	}

	@Override
	public void updateCharacterStream (String columnLabel, Reader reader) throws SQLException
	{
		updateCharacterStream (findColumn (columnLabel), reader);
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
		updateBlob (findColumn (columnLabel), inputStream);
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
		updateClob (findColumn (columnLabel), reader);
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
		updateNClob (findColumn (columnLabel), reader);
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

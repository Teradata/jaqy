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
package com.teradata.jaqy.connection;

import java.sql.*;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyHelper;

/**
 * A wrapper to detect features supported by a JDBC connection.
 *
 * @author	Heng Yuan
 */
public class JaqyConnection
{
	public final static long DEFAULT_BATCH_SIZE = 5000;

	private final Connection m_connection;
	private JaqyHelper m_helper;
	private long m_batchSize = DEFAULT_BATCH_SIZE;
	private int m_fetchSize = 0;

	public JaqyConnection (Connection conn)
	{
		m_connection = conn;
	}

	public void setHelper (JaqyHelper helper)
	{
		m_helper = helper;
	}

	public JaqyHelper getHelper ()
	{
		return m_helper;
	}

	public void close ()
	{
		try
		{
			getConnection().close ();
		}
		catch (SQLException ex)
		{
		}
	}

	public boolean isClosed ()
	{
		try
		{
			return getConnection().isClosed ();
		}
		catch (SQLException ex)
		{
			close ();
		}
		return true;
	}

	public JaqyStatement createStatement (boolean forwardOnly) throws SQLException
	{
		JaqyStatement stmt = m_helper.createStatement (forwardOnly);
		if (m_fetchSize > 0)
			stmt.setFetchSize (m_fetchSize);
		return stmt;
	}

	public JaqyPreparedStatement prepareStatement (String sql) throws SQLException
	{
		JaqyPreparedStatement stmt = m_helper.preparedStatement (sql);
		if (m_fetchSize > 0)
			stmt.setFetchSize (m_fetchSize);
		return stmt;
	}

	public void setAutoCommit (boolean b) throws SQLException
	{
		m_connection.setAutoCommit (b);
	}

	public boolean getAutoCommit () throws SQLException
	{
		return m_connection.getAutoCommit ();
	}

	public void commit () throws SQLException
	{
		m_connection.commit ();
	}

	public void rollback () throws SQLException
	{
		m_connection.rollback ();
	}

	public String getCatalog (JaqyInterpreter interpreter) throws SQLException
	{
		return m_helper.getCatalog (interpreter);
	}

	public String getSchema (JaqyInterpreter interpreter) throws SQLException
	{
		return m_helper.getSchema (interpreter);
	}

	public DatabaseMetaData getMetaData () throws SQLException
	{
		return m_connection.getMetaData ();
	}

	public String getCatalogSeparator ()
	{
		try
		{
			if (!isClosed ())
			{
				return getMetaData ().getCatalogSeparator ();
			}
		}
		catch (Throwable t)
		{
		}
		return ".";
	}

	/**
	 * Gets the JDBC connection underneath.
	 * @return	the JDBC connection
	 */
	public Connection getConnection ()
	{
		return m_connection;
	}

	public Clob createClob () throws SQLException
	{
		return m_connection.createClob ();
	}

	public NClob createNClob () throws SQLException
	{
		return m_connection.createNClob ();
	}

	public Blob createBlob () throws SQLException
	{
		return m_connection.createBlob ();
	}

	public SQLXML createSQLXML () throws SQLException
	{
		return m_connection.createSQLXML ();
	}

	public Array createArrayOf (String typeName, Object[] elements) throws SQLException
	{
		return m_connection.createArrayOf (typeName, elements);
	}

	public Struct createStruct (String typeName, Object[] elements) throws SQLException
	{
		return m_connection.createStruct (typeName, elements);
	}

	public long getBatchSize ()
	{
		return m_batchSize;
	}

	public void setBatchSize (long batchSize)
	{
		if (batchSize == 0)
			batchSize = DEFAULT_BATCH_SIZE;
		m_batchSize = batchSize;
	}

	public void setFetchSize (int size)
	{
		m_fetchSize = size;
	}

	public int getFetchSize ()
	{
		return m_fetchSize;
	}
}

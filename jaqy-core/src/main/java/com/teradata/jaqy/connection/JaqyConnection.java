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
package com.teradata.jaqy.connection;

import java.sql.*;

/**
 * A wrapper to detect features supported by a JDBC connection.
 *
 * @author	Heng Yuan
 */
public class JaqyConnection
{
	public static JaqyConnection getConnection (Connection conn)
	{
		return new JaqyConnection (conn, null);
	}

	public static JaqyConnection getDummyConnection ()
	{
		return new JaqyConnection (null, null);
	}

	private final Connection m_connection;
	private final JdbcFeatures m_features;

	public JaqyConnection (Connection conn, JdbcFeatures features)
	{
		m_connection = conn;
		if (features == null)
			m_features = new JdbcFeatures ();
		else
			m_features = features;
	}

	public boolean isClosed ()
	{
		try
		{
			return getConnection().isClosed ();
		}
		catch (SQLException ex)
		{
			try
			{
				getConnection().close ();
			}
			catch (SQLException ex2)
			{
			}
		}
		return true;
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
	
	public JaqyStatement createStatement () throws SQLException
	{
		if (getFeatures().forwardOnlyRS)
			return new JaqyStatement (getConnection().createStatement (), this);
		else
		{
			try
			{
				return new JaqyStatement (getConnection().createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY), this);
			}
			catch (SQLException ex)
			{
				// we are catching all SQLException rather than just
				// SQLFeatureNotSupportedException because some JDBC drivers
				// throw SQLException in all cases.
				getFeatures().forwardOnlyRS = true;
				return new JaqyStatement (getConnection().createStatement (), this);
			}
		}
	}

	public JaqyPreparedStatement prepareStatement (String sql) throws SQLException
	{
		if (getFeatures().forwardOnlyRS)
			return new JaqyPreparedStatement (getConnection().prepareStatement (sql), this);
		else
		{
			try
			{
				return new JaqyPreparedStatement (getConnection().prepareStatement (sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY), this);
			}
			catch (SQLException ex)
			{
				// we are catching all SQLException rather than just
				// SQLFeatureNotSupportedException because some JDBC drivers
				// throw SQLException in all cases.
				getFeatures().forwardOnlyRS = true;
				return new JaqyPreparedStatement (getConnection().prepareStatement (sql), this);
			}
		}
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

	public String getCatalog () throws SQLException
	{
		if (getFeatures().noCatalog)
			return null;
		try
		{
			return m_connection.getCatalog ();
		}
		catch (SQLFeatureNotSupportedException ex)
		{
			getFeatures().noCatalog = true;
			return null;
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			getFeatures().noSchema = true;
			return null;
		}
	}

	public String getSchema () throws SQLException
	{
		if (getFeatures().noSchema)
			return null;
		try
		{
			return m_connection.getSchema ();
		}
		catch (SQLFeatureNotSupportedException ex)
		{
			getFeatures().noSchema = true;
			return null;
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			getFeatures().noSchema = true;
			return null;
		}
	}

	public DatabaseMetaData getMetaData () throws SQLException
	{
		return m_connection.getMetaData ();
	}

	/**
	 * Gets the JDBC connection underneath.
	 * @return	the JDBC connection
	 */
	public Connection getConnection ()
	{
		return m_connection;
	}

	/**
	 * @return	the features
	 */
	public JdbcFeatures getFeatures ()
	{
		return m_features;
	}
}

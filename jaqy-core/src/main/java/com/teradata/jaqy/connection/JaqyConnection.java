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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.interfaces.JaqyHelper;

/**
 * A wrapper to detect features supported by a JDBC connection.
 *
 * @author	Heng Yuan
 */
public class JaqyConnection
{
	private final Connection m_connection;
	private JaqyHelper m_helper;

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
		return m_helper.createStatement ();
	}

	public JaqyPreparedStatement prepareStatement (String sql) throws SQLException
	{
		return m_helper.preparedStatement (sql);
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
		return m_helper.getCatalog ();
	}

	public String getSchema () throws SQLException
	{
		return m_helper.getSchema ();
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
}

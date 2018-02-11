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

import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.interfaces.JaqyHelper;

/**
 * @author	Heng Yuan
 */
public class JaqyResultSet
{
	private final ResultSet m_rs;
	private final JaqyHelper m_helper;
	private JaqyResultSetMetaData m_metaData;

	public JaqyResultSet (ResultSet rs, JaqyHelper helper)
	{
		m_rs = rs;
		m_helper = helper;
	}

	/**
	 * @return	the connection
	 */
	public JaqyConnection getConnection ()
	{
		return m_helper.getConnection ();
	}

	public ResultSet getResultSet ()
	{
		return m_rs;
	}

	public JaqyHelper getHelper ()
	{
		return m_helper;
	}

	public JaqyResultSetMetaData getMetaData () throws SQLException
	{
		if (m_metaData != null)
			return m_metaData;
		{
			m_metaData = new JaqyResultSetMetaData (m_rs.getMetaData (), m_helper);
			return m_metaData;
		}
	}

	public int findColumn (String columnLabel) throws SQLException
	{
		return m_rs.findColumn (columnLabel);
	}

	public void close () throws SQLException
	{
		m_rs.close ();
	}

	public boolean next () throws SQLException
	{
		return m_rs.next ();
	}

	public int getType () throws SQLException
	{
		return m_rs.getType ();
	}

	public Object getObject (int column) throws SQLException
	{
		return m_rs.getObject (column);
	}

	public String getString (int column) throws SQLException
	{
		return m_rs.getString (column);
	}

	public Reader getCharacterStream (int column) throws SQLException
	{
		return m_rs.getCharacterStream (column);
	}

	public InputStream getBinaryStream (int column) throws SQLException
	{
		return m_rs.getBinaryStream (column);
	}

	public byte[] getBytes (int column) throws SQLException
	{
		return m_rs.getBytes (column);
	}

	public void beforeFirst () throws SQLException
	{
		m_rs.beforeFirst ();;
	}
}

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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.utils.SortInfo;

/**
 * This is the simple implementation of JaqyResultSet to wrap around
 * ResultSet.
 *
 * @author	Heng Yuan
 */
public class JaqyDefaultResultSet implements JaqyResultSet
{
	private final ResultSet m_rs;
	private final JaqyHelper m_helper;
	private JaqyResultSetMetaData m_metaData;
	private JaqyStatement m_statement;

	public JaqyDefaultResultSet (ResultSet rs, JaqyHelper helper)
	{
		m_rs = rs;
		m_helper = helper;
	}

	/**
	 * the number of rows this result has.
	 *
	 * @return	the number of rows this result has.
	 */
	public int size () throws SQLException
	{
		if (m_rs.getType () != ResultSet.TYPE_FORWARD_ONLY)
		{
			m_rs.last ();
			int s = m_rs.getRow ();
			m_rs.beforeFirst ();
			return s;
		}
		return 0;
	}

	@Override
	public JaqyStatement getStatement ()
	{
		return m_statement;
	}

	@Override
	public void setStatement (JaqyStatement statement)
	{
		m_statement = statement;
	}

	@Override
	public ResultSet getResultSet ()
	{
		return m_rs;
	}

	@Override
	public JaqyHelper getHelper ()
	{
		return m_helper;
	}

	@Override
	public JaqyResultSetMetaData getMetaData () throws SQLException
	{
		if (m_metaData != null)
			return m_metaData;
		m_metaData = new JaqyResultSetMetaData (m_rs.getMetaData (), m_helper);
		return m_metaData;
	}

	@Override
	public int findColumn (String columnLabel) throws SQLException
	{
		return m_rs.findColumn (columnLabel);
	}

	@Override
	public void close () throws SQLException
	{
		m_rs.close ();
	}

	@Override
	public boolean next () throws SQLException
	{
		return m_rs.next ();
	}

	@Override
	public int getType () throws SQLException
	{
		return m_rs.getType ();
	}

	@Override
	public Object getObject (int column) throws SQLException
	{
		return m_helper.getObject (this, column);
	}

	public Object getObjectInternal (int column) throws SQLException
	{
		return m_rs.getObject (column);
	}

	@Override
	public String getString (int column) throws SQLException
	{
		return m_rs.getString (column);
	}

	@Override
	public void beforeFirst () throws SQLException
	{
		m_rs.beforeFirst ();;
	}

	public Object get (int row, int column) throws SQLException
	{
		if (m_rs.getType () != ResultSet.TYPE_FORWARD_ONLY)
		{
			if (m_rs.absolute (row))
				return m_rs.getObject (column);
		}
		return null;
	}

	@Override
	public boolean isSortable ()
	{
		return m_rs instanceof InMemoryResultSet;
	}

	@Override
	public void sort (SortInfo[] sortInfos) throws SQLException
	{
		if (m_rs instanceof InMemoryResultSet)
		{
			((InMemoryResultSet)m_rs).sort (sortInfos);
		}
	}
}

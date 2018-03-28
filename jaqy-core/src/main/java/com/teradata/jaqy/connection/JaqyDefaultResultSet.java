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
import java.sql.Statement;

import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.resultset.InMemoryResultSet;

/**
 * @author	Heng Yuan
 */
public class JaqyDefaultResultSet implements JaqyResultSet
{
	private final ResultSet m_rs;
	private final JaqyHelper m_helper;
	private JaqyResultSetMetaData m_metaData;
	private Statement m_statement;

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

	/**
	 * Get a particular row.  Note that the row # here is 0 based.
	 * The resultset must be InMemoryResultSet.
	 *
	 * @param	row
	 * 			0-based row id
	 * @return	the row retrieved.
	 */
	public Object[] get (int row)
	{
		if (m_rs instanceof InMemoryResultSet)
		{
			return ((InMemoryResultSet)m_rs).getRows ().get (row);
		}
		return null;
	}

	@Override
	public Statement getStatement ()
	{
		return m_statement;
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

	public void setStatement (Statement statement)
	{
		m_statement = statement;
	}
}

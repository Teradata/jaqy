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

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.Predicate;
import com.teradata.jaqy.resultset.InMemoryResultSet;

/**
 * @author	Heng Yuan
 */
public class JaqyResultSet
{
	private final ResultSet m_rs;
	private final JaqyHelper m_helper;
	private JaqyResultSetMetaData m_metaData;
	private Predicate m_predicate;
	private final JaqyInterpreter m_interpreter;

	public JaqyResultSet (ResultSet rs, JaqyHelper helper, JaqyInterpreter interpreter)
	{
		m_rs = rs;
		m_helper = helper;
		m_interpreter = interpreter;
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
		return findColumn (columnLabel, true);
	}

	public int findColumn (String columnLabel, boolean mapped) throws SQLException
	{
		return m_rs.findColumn (columnLabel);
	}

	public void close () throws SQLException
	{
		if (m_predicate != null)
			m_predicate.close ();
		m_rs.close ();
	}

	public boolean next () throws SQLException
	{
		while (m_rs.next ())
		{
			if (m_predicate == null ||
				m_predicate.eval (this, m_interpreter))
			{
				return true;
			}
		}
		return false;
	}

	public int getType () throws SQLException
	{
		return m_rs.getType ();
	}

	public Object getObject (int column) throws SQLException
	{
		return m_helper.getObject (this, column, true);
	}

	public Object getObject (int column, boolean mapped) throws SQLException
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

	public Object get (int row, int column) throws SQLException
	{
		if (m_rs.getType () != ResultSet.TYPE_FORWARD_ONLY)
		{
			if (m_rs.absolute (row))
				return m_rs.getObject (column);
		}
		return null;
	}

	public Predicate getPredicate ()
	{
		return m_predicate;
	}

	public void setPredicate (Predicate predicate) throws Exception
	{
		if (predicate != null)
		{
			predicate.bind (this, m_interpreter);
		}
		m_predicate = predicate;
	}
}

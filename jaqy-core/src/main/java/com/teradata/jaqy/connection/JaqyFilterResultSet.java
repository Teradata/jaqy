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

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Predicate;
import com.teradata.jaqy.interfaces.Project;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.utils.ProjectColumnList;
import com.teradata.jaqy.utils.SortInfo;

/**
 * @author	Heng Yuan
 */
public class JaqyFilterResultSet implements JaqyResultSet
{
	private final JaqyResultSet m_rs;
	private JaqyResultSetMetaData m_rsmd;
	private final JaqyHelper m_helper;
	private Predicate m_predicate;
	private Project m_project;
	private final JaqyInterpreter m_interpreter;
	private JaqyStatement m_statement;

	public JaqyFilterResultSet (JaqyResultSet rs, JaqyHelper helper, JaqyInterpreter interpreter)
	{
		m_rs = rs;
		m_helper = helper;
		m_interpreter = interpreter;
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
		return m_rs.getResultSet ();
	}

	@Override
	public JaqyHelper getHelper ()
	{
		return m_helper;
	}

	@Override
	public JaqyResultSetMetaData getMetaData () throws SQLException
	{
		if (m_rsmd != null)
			return m_rsmd;
		return m_rs.getMetaData ();
	}

	@Override
	public int findColumn (String columnLabel) throws SQLException
	{
		if (m_rsmd != null)
			return m_rsmd.findColumn (columnLabel);
		return m_rs.findColumn (columnLabel);
	}

	@Override
	public void close () throws SQLException
	{
		if (m_predicate != null)
			m_predicate.close ();
		m_rs.close ();
	}

	@Override
	public boolean next () throws SQLException
	{
		while (m_rs.next ())
		{
			if (m_predicate == null ||
				m_predicate.eval ())
			{
				return true;
			}
		}
		return false;
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
		if (m_project != null)
		{
			try
			{
				return m_project.get (column);
			}
			catch (Exception ex)
			{
				ex.printStackTrace ();
				throw new SQLException (ex);
			}
		}
		return m_rs.getObject (column);
	}

	@Override
	public String getString (int column) throws SQLException
	{
		Object o = getObject (column);
		if (o == null)
			return null;
		return o.toString ();
	}

	@Override
	public void beforeFirst () throws SQLException
	{
		m_rs.beforeFirst ();;
	}

	public void setPredicate (Predicate predicate) throws Exception
	{
		if (predicate != null)
		{
			predicate.bind (m_rs, m_interpreter);
		}
		m_predicate = predicate;
	}

	public void setProjection (ProjectColumnList expList) throws SQLException
	{
		if (expList != null)
		{
			expList.bind (m_rs, m_interpreter);
			m_project = expList.getProject ();
			m_rsmd = expList.getMetaData ();
		}
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

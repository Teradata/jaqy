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
package com.teradata.jaqy.importer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;

/**
 * @author	Heng Yuan
 */
public class PipeImporter implements JaqyImporter
{
	private final Globals m_globals;
	private final JaqyResultSet m_rs;
	private final SchemaInfo m_schema;
	private int[] m_indexMap;

	public PipeImporter (JaqyResultSet rs, Globals globals) throws Exception
	{
		m_rs = rs;
		m_globals = globals;
		m_schema = ResultSetMetaDataUtils.getColumnInfo (m_rs.getMetaData ().getMetaData (), rs.getHelper ());
	}

	@Override
	public String getName ()
	{
		return "pipe";
	}

	@Override
	public SchemaInfo getSchema () throws Exception
	{
		return m_schema;
	}

	@Override
	public boolean next () throws SQLException
	{
		return m_rs.next ();
	}

	@Override
	public void setParameters (String[] exps) throws Exception
	{
		if (exps == null)
		{
			m_indexMap = null;
			return;
		}

		int[] indexMap = new int[exps.length];
		for (int i = 0; i < exps.length; ++i)
		{
			indexMap[i] = m_rs.findColumn (exps[i]) - 1;
		}
		m_indexMap = indexMap;
	}

	@Override
	public Object importColumn (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception
	{
		Object obj = getObject (column - 1, paramInfo, interpreter);
		if (obj == null)
		{
			stmt.getHelper ().setNull (stmt, column, paramInfo, interpreter);
		}
		else
		{
			stmt.getHelper ().setObject (stmt, column, paramInfo, obj, freeList, interpreter);
		}
		return obj;
	}

	public Object getObject (int index, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception
	{
		if (m_indexMap != null)
		{
			index = m_indexMap[index];
		}
		return m_rs.getObject (index + 1);
	}

	public void setNull (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo) throws Exception
	{
		// If possible, we use the source type info since
		// 1. We need to match the source type
		// 2. ParamInfo may be dummy (as in case of MySQL)
		FullColumnInfo info = getColumnInfo (column);
		stmt.setNull (column, info.type, info.typeName);
	}

	@Override
	public void close () throws IOException
	{
		try
		{
			Statement stmt = m_rs.getResultSet ().getStatement ();
			m_rs.close ();
			stmt.close ();
		}
		catch (SQLException ex)
		{
			m_globals.log (Level.INFO, ex);
		}
	}

	private FullColumnInfo getColumnInfo (int column)
	{
		return m_schema.columns[column - 1];
	}
}

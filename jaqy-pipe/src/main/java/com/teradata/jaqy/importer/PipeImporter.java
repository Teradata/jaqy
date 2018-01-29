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
import java.util.logging.Level;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;

/**
 * @author	Heng Yuan
 */
public class PipeImporter implements JaqyImporter<Integer>
{
	private final Globals m_globals;
	private final JaqyResultSet m_rs;

	public PipeImporter (JaqyResultSet rs, Globals globals) throws IOException
	{
		m_rs = rs;
		m_globals = globals;
	}

	@Override
	public String getName ()
	{
		return "pipe";
	}

	@Override
	public SchemaInfo getSchema () throws Exception
	{
		return null;
	}

	@Override
	public boolean next () throws SQLException
	{
		return m_rs.next ();
	}

	@Override
	public Object getObject (int index, ParameterInfo paramInfo) throws Exception
	{
		try
		{
			return m_rs.getObject (index + 1);
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			throw new IOException ("Column " + (index + 1) + " is not found.");
		}
	}

	@Override
	public Integer getPath (String name) throws Exception
	{
		JaqyResultSetMetaData meta = m_rs.getMetaData ();
		int numCols = meta.getColumnCount ();
		for (int i = 0; i < numCols; ++i)
		{
			String colName = meta.getColumnLabel (i + 1);
			if (name.equals (colName))
			{
				return i;
			}
		}
		throw new IllegalArgumentException ("Invalid column name: " + name);
	}

	@Override
	public Object getObjectFromPath (Integer path, ParameterInfo paramInfo) throws Exception
	{
		return m_rs.getObject (path);
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

	@Override
	public void setNull (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo) throws Exception
	{
		stmt.setNull (column, paramInfo.type, paramInfo.typeName);
	}
}

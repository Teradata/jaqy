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
package com.teradata.jaqy;

import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.connection.JaqyParameterMetaData;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.utils.ParameterMetaDataUtils;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;

/**
 * @author	Heng Yuan
 */
public class DebugManager
{
	private boolean m_dumpConnection;
	private boolean m_dumpResultSet;
	private boolean m_dumpPreparedStatement;

	DebugManager ()
	{
	}

	/**
	 * Are we dumping Connection metadata?
	 *
	 * @return	whether we dump Connection metadata.
	 */
	public boolean isDumpConnection ()
	{
		return m_dumpConnection;
	}

	/**
	 * Sets whether we dump Connection metadata.
	 * @param	b
	 *			enable / disable Connection metadata dump.
	 */
	public void setDumpConnection (boolean b)
	{
		m_dumpConnection = b;
	}

	/**
	 * Are we dumping ResultSet metadata?
	 *
	 * @return	whether we dump ResultSet metadata.
	 */
	public boolean isDumpResultSet ()
	{
		return m_dumpResultSet;
	}

	/**
	 * Sets whether we dump ResultSet metadata.
	 * @param	b
	 *			enable / disable ResultSet metadata dump.
	 */
	public void setDumpResultSet (boolean b)
	{
		m_dumpResultSet = b;
	}

	public void dumpResultSet (Display display, Session session, JaqyResultSet rs)
	{
		if (m_dumpResultSet)
		{
			try
			{
				ResultSetMetaDataUtils.dump (display, session, rs.getMetaData ());
			}
			catch (SQLException ex)
			{
				display.getGlobals ().log (Level.INFO, ex);
			}
		}
	}

	/**
	 * @return	the dumpPreparedStatement
	 */
	public boolean isDumpPreparedStatement ()
	{
		return m_dumpPreparedStatement;
	}

	/**
	 * @param	dumpPreparedStatement
	 *			the dumpPreparedStatement to set
	 */
	public void setDumpPreparedStatement (boolean dumpPreparedStatement)
	{
		m_dumpPreparedStatement = dumpPreparedStatement;
	}

	public void dumpPreparedStatement (Display display, Session session, JaqyPreparedStatement stmt, JaqyInterpreter interpreter)
	{
		if (m_dumpPreparedStatement)
		{
			try
			{
				JaqyParameterMetaData metaData = stmt.getParameterMetaData ();
				ParameterMetaDataUtils.dump (display, session, metaData);
			}
			catch (SQLException ex)
			{
				display.getGlobals ().log (Level.INFO, ex);
			}
			try
			{
				JaqyResultSet rs = stmt.getResultSet (interpreter);
				if (rs != null)
					ResultSetMetaDataUtils.dump (display, session, rs.getMetaData ());
			}
			catch (SQLException ex)
			{
				display.getGlobals ().log (Level.INFO, ex);
			}
		}
	}
}

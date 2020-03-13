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

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyParameterMetaData;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.interfaces.*;
import com.teradata.jaqy.parser.FieldParser;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.utils.DriverManagerUtils;
import com.teradata.jaqy.utils.ImportExpressionHandler;
import com.teradata.jaqy.utils.ParameterMetaDataUtils;

/**
 * @author Heng Yuan
 */
public class Session
{
	private final int m_sessionId;
	private final Globals m_globals;

	private JaqyConnection m_connection;

	private long m_iteration;
	private final Object m_lock = new Object ();
	private boolean m_doNotClose;

	Session (Globals globals, int sessionId, Display display)
	{
		m_globals = globals;
		m_sessionId = sessionId;
	}

	private void reset ()
	{
		synchronized (m_lock)
		{
			m_connection = null;
		}
	}

	private void setConnection (String protocol, Connection conn)
	{
		JaqyConnection c = new JaqyConnection (conn);
		JaqyHelperFactory helperFactory = m_globals.getHelperManager ().getHelperFactory (protocol);
		JaqyHelper helper = helperFactory.getHelper (c, m_globals);
		c.setHelper (helper);
		synchronized (m_lock)
		{
			m_connection = c;
		}
	}

	public Globals getGlobals ()
	{
		return m_globals;
	}

	public void open (String url, Properties properties, JaqyInterpreter interpreter, Display display)
	{
		try
		{
			String protocol = DriverManagerUtils.getProtocol (m_globals.getDriverManager (), url);
			if (protocol == null)
			{
				interpreter.error ("Unknown protocol");
			}

			boolean loaded = m_globals.getDriverManager ().loadDriver (display, interpreter, protocol);
			if (!loaded)
			{
				interpreter.error ("Unable to load driver for " + protocol);
			}

			// append jdbc: prefix if necessary
			if (!url.startsWith ("jdbc:"))
				url = "jdbc:" + url;
			Connection conn = DriverManager.getConnection (url, properties);
			setConnection (protocol, conn);
		}
		catch (Throwable t)
		{
			interpreter.getDisplay ().error (interpreter, t);
		}
		display.showTitle (interpreter);
	}

	public JaqyConnection getConnection ()
	{
		synchronized (m_lock)
		{
			return m_connection;
		}
	}

	public void close (JaqyInterpreter interpreter, boolean isExit)
	{
		JaqyConnection conn = getConnection ();
		if (conn != null)
		{
			conn.close ();
		}
		reset ();

		if (!isExit)
			interpreter.getDisplay ().showTitle (interpreter);
	}

	public boolean isClosed ()
	{
		JaqyConnection conn = getConnection ();
		if (conn == null)
			return true;
		return conn.isClosed ();
	}

	private JaqyStatement createStatement (boolean forwardOnly) throws SQLException
	{
		JaqyConnection conn;
		synchronized (m_lock)
		{
			conn = m_connection;
		}
		return conn.createStatement (forwardOnly);
	}

	private JaqyPreparedStatement prepareStatement (String sql, JaqyInterpreter interpreter) throws SQLException
	{
		JaqyConnection conn;
		synchronized (m_lock)
		{
			conn = m_connection;
		}
		JaqyPreparedStatement stmt = conn.prepareStatement (sql);
		m_globals.getDebugManager ().dumpPreparedStatement (interpreter.getDisplay (), this, stmt, interpreter);
		return stmt;
	}

	private void handleQueryResult (JaqyStatement stmt, JaqyInterpreter interpreter) throws SQLException
	{
		Display display = interpreter.getDisplay ();
		JaqyResultSet rs = stmt.getResultSet (interpreter);
		for (;;)
		{
			if (rs != null)
			{
				rs.setStatement (stmt);
				m_globals.getDebugManager ().dumpResultSet (display, this, rs);
				display.showSuccess (interpreter);
				rs = interpreter.print (rs);
				if (interpreter.getActivityCount () >= 0)
				{
					display.showActivityCount (interpreter);
				}
				if (rs != null && !m_doNotClose)
					rs.close ();
				rs = null;
			}
			else
			{
				long activityCount = stmt.getUpdateCount ();
				if (activityCount >= 0)
				{
					interpreter.setActivityCount (activityCount);
					display.showSuccessUpdate (interpreter);
				}
				else
				{
					// we do not actually have a result
					break;
				}
			}
			if (m_doNotClose)
				break;
			// move onto the next result set
			boolean moreRS = stmt.getMoreResults ();
			if (moreRS)
			{
				rs = stmt.getResultSet (interpreter);
			}
		}
	}

	public JaqyPreparedStatement prepareQuery (String sql, JaqyInterpreter interpreter, boolean incCount) throws Exception
	{
		if (incCount)
		{
			interpreter.incSqlCount ();
		}
		sql = interpreter.expand (sql);
		m_globals.log (Level.INFO, "prepareQuery: " + sql);
		if (isClosed ())
		{
			interpreter.error ("session closed.");
		}

		// remove trailing ; since some JDBC drivers (notably Apache Derby)
		// does not permit trailing semicolons.
		if (sql.endsWith (";"))
			sql = sql.substring (0, sql.length () - 1);

		return prepareStatement (sql, interpreter);
	}

	private void freeObjects (ArrayList<Object> list)
	{
		for (Object o : list)
		{
			try
			{
				if (o instanceof Clob)
				{
					((Clob)o).free ();
				}
				else if (o instanceof Blob)
				{
					((Blob)o).free ();
				}
				else if (o instanceof SQLXML)
				{
					((SQLXML)o).free ();
				}
			}
			catch (SQLException ex)
			{
				m_globals.log (Level.INFO, ex);
			}
		}
		list.clear ();
	}

	public void importQuery (String sql, JaqyInterpreter interpreter) throws Exception
	{
		interpreter.incSqlCount ();

		JaqyPreparedStatement stmt = null;
		JaqyImporter importer = interpreter.getImporter ();

		try
		{
			m_globals.log (Level.INFO, "importQuery: " + importer);
			ImportExpressionHandler expHandler = new ImportExpressionHandler ();
			sql = interpreter.expand (sql);
			sql = FieldParser.getString (sql, expHandler);
			m_globals.log (Level.INFO, "field sql: " + sql);

			String[] exps = expHandler.getExpressions ();
			importer.setParameters (exps);

			stmt = prepareQuery (sql, interpreter, false);
			JaqyHelper helper = stmt.getHelper ();

			JaqyParameterMetaData metaData = stmt.getParameterMetaData ();
			ParameterInfo[] parameterInfos = ParameterMetaDataUtils.getParameterInfos (metaData, m_globals);
			if (parameterInfos.length == 0)
				throw new IOException ("no parameters detected.");
			for (ParameterInfo paramInfo : parameterInfos)
				helper.fixParameterInfo (paramInfo);

			int columns = stmt.getParameterCount ();
			long batchCount = 0;
			long batchSize = m_connection.getBatchSize ();
			ArrayList<Object> freeList = new ArrayList<Object> ();
			while (importer.next ())
			{
				for (int i = 0; i < columns; ++i)
				{
					Object o = importer.getObject (i, parameterInfos[i], interpreter);
					if (o == null)
					{
						//stmt.setNull (i + 1, parameterInfos[i].type, parameterInfos[i].typeName);
						importer.setNull (stmt, i + 1, parameterInfos[i]);
						continue;
					}

					helper.setObject (stmt, i + 1, parameterInfos[i], o, freeList, interpreter);

					// free Blob / Clob / SQLXML / Array
					if (o instanceof SQLXML)
					{
						((SQLXML)o).free ();
					}
					else if (o instanceof Clob)
					{
						((Clob)o).free ();
					}
					else if (o instanceof Blob)
					{
						((Blob)o).free ();
					}
					else if (o instanceof Array)
					{
						((Array)o).free ();
					}
				}
				if (batchSize == 1)
				{
					stmt.execute ();
					freeObjects (freeList);
					handleQueryResult (stmt, interpreter);
				}
				else
				{
					++batchCount;
					stmt.addBatch ();
					freeObjects (freeList);
					if (batchCount == batchSize)
					{
						stmt.executeBatch ();
						handleQueryResult (stmt, interpreter);
						batchCount = 0;
					}
				}
			}
			if (batchCount > 0)
			{
				stmt.executeBatch ();
				handleQueryResult (stmt, interpreter);
			}
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close ();
			}
			catch (Exception ex)
			{
			}
			try
			{
				importer.close ();
			}
			catch (Exception ex)
			{
				m_globals.log (Level.INFO, ex);
			}
		}
	}

	public void executeQuery (String sql, JaqyInterpreter interpreter, long repeat) throws Exception
	{
		interpreter.incSqlCount ();
		if (isClosed ())
		{
			interpreter.error ("session closed.");
		}

		// remove trailing ; since some JDBC drivers (notably Apache Derby)
		// does not permit trailing semicolons.
		if (sql.endsWith (";"))
			sql = sql.substring (0, sql.length () - 1);

		// optimize the ResultSet type a bit.  FORWARD_ONLY resultsets
		// for some drivers are necessary to obtain large results.
		boolean forwardOnly = (interpreter.getExporter () != null) ||
							  interpreter.isQuiet () ||
							  interpreter.getPrinter ().isForwardOnly ();
		if (forwardOnly)
		{
			m_globals.log (Level.INFO, "executeQuery - forwardOnly");
		}

		JaqyStatement stmt = createStatement (forwardOnly);
		try
		{
			for (int iter = 0; iter < repeat; ++iter)
			{
				setIteration (iter + 1);
				if (repeat > 1)
				{
					interpreter.getDisplay ().showIteration (interpreter);
				}
				String actualSql = interpreter.expand (sql);
				m_globals.log (Level.INFO, "executeQuery: " + actualSql);
				stmt.execute (actualSql);
				handleQueryResult (stmt, interpreter);
			}
		}
		catch (Error err)
		{
			throw err;
		}
		finally
		{
			try
			{
				if (!m_doNotClose && stmt != null)
				{
					m_globals.log (Level.INFO, "executeQuery - closing statement.");
					stmt.close ();
				}
			}
			catch (Exception ex)
			{
			}
			m_doNotClose = false;
		}
	}

	/**
	 * @return the sessionId
	 */
	public int getId ()
	{
		return m_sessionId;
	}

	public String getDescription ()
	{
		StringBuffer buffer = new StringBuffer ();
		buffer.append (getId ()).append (" - ");

		JaqyConnection conn = getConnection ();
		try
		{
			if (conn == null || conn.isClosed ())
			{
				buffer.append ("closed.");
			}
			else
			{
				buffer.append (conn.getHelper ().getPath ());
			}
		}
		catch (SQLException ex)
		{
			buffer.append ("closed.");
		}
		return buffer.toString ();
	}

	@Override
	public String toString ()
	{
		return Integer.toString (m_sessionId);
	}

	public void setDoNotClose (boolean b)
	{
		m_doNotClose = b;
	}

	public long getIteration ()
	{
		return m_iteration;
	}

	public void setIteration (long iteration)
	{
		m_iteration = iteration;
	}
}

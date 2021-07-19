/*
 * Copyright (c) 2017-2021 Teradata
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
package com.teradata.jaqy.utils;

import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
public class QueryUtils
{
	/**
	 * Get the string result from a query.
	 * @param	conn
	 * 			The JDBC connection
	 * @param	sql
	 * 			The query string
	 * @param	column
	 * 			The column to retrieve data from
	 * @param interpreter TODO
	 *
	 * @return	a string representation of the output for a particular column.
	 * 			It can retrieve multiple rows of data if needed.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public static String getQueryString (JaqyConnection conn, String sql, int column, JaqyInterpreter interpreter) throws SQLException
	{
		JaqyStatement stmt = null;
		interpreter.getGlobals ().log (Level.INFO, "SQL: " + sql);
		try
		{
			stmt = conn.createStatement (true);
			stmt.execute (sql);
			JaqyResultSet rs = stmt.getResultSet (interpreter);
			if (rs == null)
				return null;

			StringBuilder builder = new StringBuilder ();
			while (rs.next ())
			{
				builder.append (rs.getString (column));
			}
			rs.close ();
			return builder.toString ();
		}
		finally
		{
			try
			{
				stmt.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	/**
	 * Get the ResultSet from a query.
	 *
	 * @param	globals
	 *			global variables
	 * @param	conn
	 * 			The JDBC connection
	 * @param	sql
	 * 			The query string
	 * @param	interpreter
	 * 			the interpreter
	 * @return	an in-memory COPY of the query ResultSet.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public static JaqyResultSet getResultSet (Globals globals, JaqyConnection conn, String sql, JaqyInterpreter interpreter) throws SQLException
	{
		globals.log (Level.INFO, "SQL: " + sql);
		try (JaqyStatement stmt = conn.createStatement (true))
		{
			stmt.execute (sql);
			JaqyResultSet rs = stmt.getResultSet (interpreter);
			if (rs == null)
				return null;

			JaqyResultSet newRS = ResultSetUtils.copyResultSet (rs, 0, interpreter);
			rs.close ();
			return newRS;
		}
	}
}

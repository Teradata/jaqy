package com.teradata.jaqy.utils;

import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.helper.DummyHelper;
import com.teradata.jaqy.resultset.InMemoryResultSet;

public class QueryUtils
{
	/**
	 * Get the string result from a query.
	 *
	 * @param	globals
	 *			global variables
	 * @param	conn
	 * 			The JDBC connection
	 * @param	sql
	 * 			The query string
	 * @param	column
	 * 			The column to retrieve data from
	 * @return	a string representation of the output for a particular column.
	 * 			It can retrieve multiple rows of data if needed.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public static String getQueryString (Globals globals, JaqyConnection conn, String sql, int column) throws SQLException
	{
		JaqyStatement stmt = null;
		globals.log (Level.INFO, "SQL: " + sql);
		try
		{
			stmt = conn.createStatement (true);
			stmt.execute (sql);
			JaqyResultSet rs = stmt.getResultSet ();
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
	 * @return	an in-memory COPY of the query ResultSet.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public static JaqyResultSet getResultSet (Globals globals, JaqyConnection conn, String sql) throws SQLException
	{
		JaqyStatement stmt = null;
		globals.log (Level.INFO, "SQL: " + sql);
		try
		{
			stmt = conn.createStatement (true);
			stmt.execute (sql);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				return null;

			InMemoryResultSet columnRS = new InMemoryResultSet (rs.getResultSet (), 0);
			rs.close ();
			return DummyHelper.getInstance ().getResultSet (columnRS);
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
}

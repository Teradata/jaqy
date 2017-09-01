package com.teradata.jaqy.utils;

import java.sql.SQLException;

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
	public static String getQueryString (JaqyConnection conn, String sql, int column) throws SQLException
	{
		JaqyStatement stmt = null;
		try
		{
			stmt = conn.createStatement ();
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
	 * @param	conn
	 * 			The JDBC connection
	 * @param	sql
	 * 			The query string
	 * @return	an in-memory COPY of the query ResultSet.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public static JaqyResultSet getResultSet (JaqyConnection conn, String sql) throws SQLException
	{
		JaqyStatement stmt = null;
		try
		{
			stmt = conn.createStatement ();
			stmt.execute (sql);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				return null;

			InMemoryResultSet columnRS = new InMemoryResultSet (rs.getResultSet ());
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

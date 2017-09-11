package com.teradata.jaqy.utils;

public class SimpleQuery
{
	/**
	 * The SQL query to be executed.
	 */
	public final String sql;
	/**
	 * The field of the result set to be retrieved.  If multiple rows are
	 * returned, the values from the column index specified will be
	 * concatenated.
	 */
	public final int columnIndex;

	public SimpleQuery (String s, int column)
	{
		this.sql = s;
		this.columnIndex = column;
	}
}

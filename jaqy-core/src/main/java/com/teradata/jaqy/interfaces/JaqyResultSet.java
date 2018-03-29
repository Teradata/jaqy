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
package com.teradata.jaqy.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.utils.SortInfo;

/**
 * @author	Heng Yuan
 */
public interface JaqyResultSet extends AutoCloseable
{
	public JaqyHelper getHelper ();

	public JaqyResultSetMetaData getMetaData () throws SQLException;

	public JaqyStatement getStatement ();
	public void setStatement (JaqyStatement statement);

	public ResultSet getResultSet ();

	public int findColumn (String columnLabel) throws SQLException;

	public void close () throws SQLException;

	public 	boolean next () throws SQLException;

	public int getType () throws SQLException;

	/**
	 * This returns object for a particular column.  It internally calls
	 * the helper object to translate the object if necessary.
	 *
	 * @param	column
	 * 			the column index
	 * @return	the object associated with a column.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public Object getObject (int column) throws SQLException;

	/**
	 * This call should only be called from JaqyHelper.
	 *
	 * @param	column
	 * 			the column index
	 * @return	the internal ResultSet get object
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public Object getObjectInternal (int column) throws SQLException;

	public String getString (int column) throws SQLException;
	/**
	 * Rewind the cursor.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public void beforeFirst () throws SQLException;
	/**
	 * Check if this ResultSet can be sorted or not.
	 *
	 * @return	true if this ResultSet allows setSortInfos() to be called.
	 */
	public boolean isSortable ();
	/**
	 * Sort the ResultSet.
	 *
	 * @param	sortInfos
	 * 			info necessary to do the sort.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public void sort (SortInfo[] sortInfos) throws SQLException;
}

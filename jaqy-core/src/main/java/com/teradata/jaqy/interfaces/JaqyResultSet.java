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
package com.teradata.jaqy.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.utils.SortInfo;

/**
 * This is the generic interface for an ResultSet wrapper.
 *
 * @author	Heng Yuan
 */
public interface JaqyResultSet extends AutoCloseable
{
	/**
	 * Get the helper object associated with this ResultSet.
	 * @return	the helper object associated with this ResultSet.
	 */
	public JaqyHelper getHelper ();
	/**
	 * Get the metadata associated with this ResultSet.
	 * @return	the metadata associated with this ResultSet.
	 * @throws	SQLException
	 * 			in case of error
	 */
	public JaqyResultSetMetaData getMetaData () throws SQLException;
	/**
	 * Get the statement associated with this ResultSet.
	 * @return	the statement associated with this ResultSet.
	 */
	public JaqyStatement getStatement ();
	/**
	 * Set the statement associated with this ResultSet.
	 */
	public void setStatement (JaqyStatement statement);
	/**
	 * Get the wrapped ResultSet object.
	 * @return	the wrapped ResultSet object.
	 */
	public ResultSet getResultSet ();
	/**
	 * Find the column index corresponding to a column label.
	 * Depending on the database, it may be case sensitive or
	 * case insensitive match.
	 *
	 * @param	columnLabel
	 * 			the column label
	 * @return	the column index corresponding the label.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public int findColumn (String columnLabel) throws SQLException;
	/**
	 * Close the ResultSet and free any resources.
	 */
	public void close () throws SQLException;
	/**
	 * Get the next row.
	 * @return	true if there is the next row.  false otherwise.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public boolean next () throws SQLException;
	/**
	 * Get the ResultSet type.
	 * @return	the ResultSet type
	 * @throws	SQLException
	 * 			in case of error.
	 */
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
	/**
	 * Get the string representation of the value at a column.
	 * @param	column
	 * 			the column index
	 * @return	the string representation of the value at a column.
	 * @throws	SQLException
	 * 			in case of error.
	 */
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

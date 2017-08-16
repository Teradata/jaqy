/*
 * Copyright (c) 2017 Teradata
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

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.typehandler.TypeHandler;

/**
 * 
 * @author	Heng Yuan
 */
public interface JaqyHelper
{
	public JdbcFeatures getFeatures ();

	public JaqyStatement createStatement () throws SQLException;
	public JaqyPreparedStatement preparedStatement (String sql) throws SQLException;

	public JaqyConnection getConnection ();

	public String getURL () throws SQLException;

	/**
	 * Utility function for getting catalog.
	 * @return	The database catalog string if it is support catalog.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getCatalog () throws SQLException;
	/**
	 * Utility function for getting schema.
	 * @return	The database schema string if it is support schema.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getSchema () throws SQLException;
	/**
	 * Getting a path info string for interactive mode.  This string should
	 * be fairly informative of the user, host, database, etc without
	 * being too long.
	 *
	 * @return	an informative string
	 * @throws	SQLException
	 *			in case of error.
	 */
	public String getPath () throws SQLException;
	/**
	 * This function is used to do any post-query modifications to the ResultSet.
	 *
	 * @param	rs
	 * 			query ResultSet
	 * @return	JaqyResultSet which is a wrapper for some actions.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public JaqyResultSet getResultSet (ResultSet rs) throws SQLException;
	/**
	 * Check if a ResultSet column is a JSON column.  Until JDBC has
	 * JSON built-in, we need a way to recognize a JSON column.
	 *
	 * @param	meta
	 * @param	column
	 * 			the column index, starting from 1.
	 * @return	true if the column is a JSON column.
	 * 			false otherwise.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public boolean isJsonColumn (JaqyResultSetMetaData meta, int column)  throws SQLException;
	/**
	 * Check if a ResultSet column is a geospatial column (i.e. ST_GEOMETRY).
	 * Until JDBC has ST_GEOMETRY built-in, we need a way to recognizes it.
	 *
	 * @param	meta
	 * @param	column
	 * 			the column index, starting from 1.
	 * @return	true if the column is a JSON column.
	 * 			false otherwise.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public boolean isSpatialColumn (JaqyResultSetMetaData meta, int column) throws SQLException;
	/**
	 * Get the type handler for a particular column.
	 *
	 * @param	rs
	 * 			the ResultSet.
	 * @param	column
	 *			column index.  It should starts from 1.
	 * @return	the type handler for the column.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public TypeHandler getTypeHandler (JaqyResultSet rs, int column) throws SQLException;
}

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

import java.sql.*;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.schema.BasicColumnInfo;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.TypeMap;
import com.teradata.jaqy.typehandler.TypeHandler;

/**
 * 
 * @author	Heng Yuan
 */
public interface JaqyHelper
{
	public JdbcFeatures getFeatures ();

	public JaqyStatement createStatement (boolean forwardOnly) throws SQLException;
	public JaqyPreparedStatement preparedStatement (String sql) throws SQLException;

	public JaqyConnection getConnection ();

	public String getURL () throws SQLException;

	public TypeMap getTypeMap () throws SQLException;
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
	 * This function retrieves an object from a ResultSet using the index provided.
	 * The purpose of this function is to provide an ability for Helper class
	 * to modify the object retrieved.
	 *
	 * @param	rs
	 * 			resultset
	 * @param	index
	 * 			column index
	 * @return	the object at the column position
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public Object getObject (JaqyResultSet rs, int index) throws SQLException;
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
	/**
	 * Create the Array object.
	 * @param	paramInfo
	 * 			the parameter information.
	 * @param	elements
	 * 			an array of objects for creating the array.
	 * @return	an array object if it is created successfully.
	 * @throws	SQLException
	 * 			if the creation failed.
	 */
	public Array createArrayOf (ParameterInfo paramInfo, Object[] elements) throws SQLException;
	/**
	 * Create the Struct object.
	 * @param	paramInfo
	 * 			the parameter information.
	 * @param	elements
	 * 			an array of objects for creating the struct.
	 * @return	a Struct object if it is created successfully.
	 * @throws	SQLException
	 * 			if the creation failed.
	 */
	public Struct createStruct (ParameterInfo paramInfo, Object[] elements) throws SQLException;
	/**
	 * Given the type, precision, and scale, give the closest type name that
	 * matches the need.
	 *
	 * @param	type
	 * 			See {@link Types}.
	 * @param	precision
	 * 			the precision for the type.
	 * @param	scale
	 * 			the scale for the type.
	 * @param	exact
	 * 			if exact is false, the closest type (based on casting rule)
	 * 			is used.
	 * @return	a type name.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getTypeName (int type, int precision, int scale, boolean exact) throws SQLException;
	/**
	 * Based on the typeInfo, infer the SQL type.
	 * @param	typeInfo
	 * 			the basic type information.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getTypeName (BasicColumnInfo typeInfo) throws SQLException;
	/**
	 * Gets the quoted identifier
	 * @param	name
	 * 			table / column identifier name.
	 * @return	quoted table / column name.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getQuotedIdentifier (String name) throws SQLException;
	/**
	 * Get the SQL schema for the given table.
	 * @param	tableName
	 * 			the table to retrieve the SQL schema.
	 * @return	the SQL schema
	 * @throws	Exception
	 * 			in case of error.
	 */
	public String getTableSchema (String tableName) throws Exception;
	/**
	 * Get the column description of the given table.
	 * @param	tableName
	 * 			the table to retrieve the column information.
	 * @return	a ResultSet describing the columns
	 * @throws	Exception
	 * 			in case of error.
	 */
	public JaqyResultSet getTableColumns (String tableName) throws Exception;
	/**
	 * Do a client side fix of the column metadata information.  The fix
	 * is to get rid of incorrect or ambiguous type informations.
	 *
	 * @param	info
	 * 			column metadata
	 */
	public void fixColumnInfo (FullColumnInfo info);
	/**
	 * Do a client side fix of the column metadata information.  The fix
	 * is to get rid of incorrect or ambiguous type informations.
	 *
	 * @param	info
	 * 			parameter metadata
	 */
	public void fixParameterInfo (ParameterInfo info);
}

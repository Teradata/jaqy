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

import java.sql.*;
import java.util.Collection;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.*;
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
	/**
	 * Gets the current connection.
	 * @return	the current connection.
	 */
	public JaqyConnection getConnection ();
	/**
	 * Some simple features (or features missing) in the JDBC.
	 * @return	the features of the JDBC driver.
	 */
	public JdbcFeatures getFeatures ();
	/**
	 * Creates a normal query statement.
	 * @param	forwardOnly
	 * 			The hint if only a TYPE_FORWARD_ONLY statement is needed.
	 * @return	a Statement for executing normal query.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public JaqyStatement createStatement (boolean forwardOnly) throws SQLException;
	/**
	 * Creates a prepared query statement.
	 * @param	sql
	 * 			the SQL query
	 * @return	prepared statement
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public JaqyPreparedStatement preparedStatement (String sql) throws SQLException;
	/**
	 * Gets the current connection URL.
	 * @return	the current connection URL.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getURL () throws SQLException;
	/**
	 * Get the type to string name map.
	 *
	 * @param	forImport
	 * 			if true, the type map is for import purposes.
	 * 			otherwise, the type map is for describing table schema.
	 * @return	the type to string name map.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public TypeMap getTypeMap (boolean forImport) throws SQLException;
	/**
	 * Utility function for getting catalog.
	 * @param	interpreter
	 * 			the interpreter
	 * @return	The database catalog string if it is support catalog.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getCatalog (JaqyInterpreter interpreter) throws SQLException;
	/**
	 * Utility function for getting schema.
	 * @param	interpreter
	 * 			the interpreter
	 * @return	The database schema string if it is support schema.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getSchema (JaqyInterpreter interpreter) throws SQLException;
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
	 * @param	interpreter
	 * 			the interpreter
	 * @return	JaqyResultSet which is a wrapper for some actions.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public JaqyResultSet getResultSet (ResultSet rs, JaqyInterpreter interpreter) throws SQLException;
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
	 * @param	forImport
	 * 			if the type name is for import.
	 * @return	a type name.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getTypeName (int type, int precision, int scale, boolean exact, boolean forImport) throws SQLException;
	/**
	 * Based on the typeInfo, infer the SQL type.
	 * @param	typeInfo
	 * 			the basic type information.
	 * @param	forImport
	 * 			if the type name is for import.
	 * @throws	SQLException
	 * 			in case of error.
	 */
	public String getTypeName (BasicColumnInfo typeInfo, boolean forImport) throws SQLException;
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
	 * Check if a table exists already.
	 * @param	tableName
	 * 			the table to retrieve the SQL schema.
	 * @param	interpreter
	 * 			the interpreter
	 * @return	true if the table exists.  false otherwise.
	 * @throws	Exception
	 * 			in case of error.
	 */
	public boolean checkTableExists (String tableName, JaqyInterpreter interpreter) throws SQLException;
	/**
	 * Get the SQL schema for the given table.
	 * @param	tableName
	 * 			the table to retrieve the SQL schema.
	 * @param	interpreter
	 * 			the interpreter
	 * @return	the SQL schema
	 * @throws	Exception
	 * 			in case of error.
	 */
	public String getTableSchema (String tableName, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Get the column description of the given table.
	 * @param	tableName
	 * 			the table to retrieve the column information.
	 * @param	interpreter
	 * 			the interpreter
	 * @return	a ResultSet describing the columns
	 * @throws	Exception
	 * 			in case of error.
	 */
	public JaqyResultSet getTableColumns (String tableName, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Get the number of columns for a table.
	 * @param	tableName
	 * 			the table to retrieve the column information.
	 * @param	interpreter
	 * 			the interpreter
	 * @return	number of columns in the table.
	 * @throws	Exception
	 * 			in case of error.
	 */
	public int getNumColumns (String tableName, JaqyInterpreter interpreter) throws Exception;
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
	/**
	 * Help setting null for a particular column.
	 * @param	stmt
	 * 			the prepared statement
	 * @param	columnIndex
	 * 			the column index
	 * @param	paramInfo
	 * 			the processed parameter information.
	 * @param	interpreter
	 * 			the interpreter
	 * @since	1.1
	 */
	public void setNull (JaqyPreparedStatement stmt, int columnIndex, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Help setting null for a particular column from CSV-like importers.
	 * @param	stmt
	 * 			the prepared statement
	 * @param	columnIndex
	 * 			the column index
	 * @param	paramInfo
	 * 			the processed parameter information.
	 * @param	interpreter
	 * 			the interpreter
	 * @since	1.1
	 */
	public void setCSVNull (JaqyPreparedStatement stmt, int columnIndex, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Help setting the object for a particular column.
	 * @param	stmt
	 * 			the prepared statement
	 * @param	columnIndex
	 * 			the column index
	 * @param	paramInfo
	 * 			the processed parameter information.
	 * @param	o
	 * 			the value to be set (it should not be null).
	 * @param	freeList
	 * 			if any resources should be freed, add to this list.
	 * @param	interpreter
	 * 			the interpreter
	 * @since	1.1
	 */
	public void setObject (JaqyPreparedStatement stmt, int columnIndex, ParameterInfo paramInfo, Object o, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Help setting the object for a particular column from CSV-like
	 * importers.
	 * @param	stmt
	 * 			the prepared statement
	 * @param	columnIndex
	 * 			the column index
	 * @param	paramInfo
	 * 			the processed parameter information.
	 * @param	o
	 * 			the value to be set (it should not be null).
	 * @param	freeList
	 * 			if any resources should be freed, add to this list.
	 * @param	interpreter
	 * 			the interpreter
	 * @since	1.1
	 */
	public void setCSVObject (JaqyPreparedStatement stmt, int columnIndex, ParameterInfo paramInfo, Object o, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Get the staging table primary index for import.
	 *
	 * Databases such as Teradata with NO PRIMARY INDEX can import
	 * significantly faster if no primary index is used.
	 */
	public String getImportTableIndex ();
}

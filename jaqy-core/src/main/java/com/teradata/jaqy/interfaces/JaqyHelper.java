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
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.connection.JdbcFeatures;

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
	public JaqyResultSet getResultSet (ResultSet rs) throws SQLException;

	public String getURL () throws SQLException;
	public String getCatalog () throws SQLException;
	public String getSchema () throws SQLException;
	public String getPath () throws SQLException;
}

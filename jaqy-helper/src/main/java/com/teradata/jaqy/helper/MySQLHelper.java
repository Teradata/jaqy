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
package com.teradata.jaqy.helper;

import java.sql.SQLException;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.utils.QueryUtils;

/**
 * @author	Heng Yuan
 */
class MySQLHelper extends DefaultHelper
{
	public MySQLHelper (JaqyConnection conn, Globals globals)
	{
		super (new JdbcFeatures (), conn, globals);
	}

	@Override
	public String getCatalog () throws SQLException
	{
		String sql = "SELECT DATABASE()";
		return QueryUtils.getQueryString (getConnection(), sql, 1);
	}

	@Override
	public String getSchema (String tableName) throws Exception
	{
		String sql = "SHOW CREATE TABLE " + tableName;
		return QueryUtils.getQueryString (getConnection(), sql, 2);
	}

	@Override
	public JaqyResultSet getColumns (String tableName) throws Exception
	{
		String sql = "DESCRIBE " + tableName;
		return QueryUtils.getResultSet (getConnection(), sql);
	}
}

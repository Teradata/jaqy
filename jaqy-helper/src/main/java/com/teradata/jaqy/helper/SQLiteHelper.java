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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.utils.QueryUtils;

/**
 * @author	Heng Yuan
 */
class SQLiteHelper extends DefaultHelper
{
	public SQLiteHelper (JaqyConnection conn, Globals globals)
	{
		super (new JdbcFeatures (), conn, globals);
	}

	@Override
	public String getSchema (String tableName) throws Exception
	{
		String sql = "SELECT sql FROM SQLITE_MASTER WHERE NAME ='" + tableName + "' COLLATE NOCASE";
		String result = QueryUtils.getQueryString (getConnection(), sql, 1);
		if (result.length () == 0)
			throw new RuntimeException ("Table was not found.");
		return result;
	}

	@Override
	public JaqyResultSet getColumns (String tableName) throws Exception
	{
		String sql = "PRAGMA table_info([" + tableName + "])";
		JaqyResultSet rs = QueryUtils.getResultSet (getConnection(), sql);
		if (rs == null)
			throw new RuntimeException ("Table was not found.");
		return rs;
	}
}

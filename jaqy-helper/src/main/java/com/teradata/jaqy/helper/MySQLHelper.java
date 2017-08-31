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
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.resultset.InMemoryResultSet;

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
	public String getSchema (String tableName) throws Exception
	{
		String query = "SHOW CREATE TABLE " + tableName;
		JaqyStatement stmt = null;
		try
		{
			stmt = createStatement ();
			stmt.execute (query);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				throw new RuntimeException ("Table was not found.");

			StringBuilder schema = new StringBuilder ();
			while (rs.next ())
			{
				schema.append (rs.getString (2));
			}
			rs.close ();
			if (schema.length () == 0)
				throw new RuntimeException ("Table was not found.");
			return schema.toString ();
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

	@Override
	public JaqyResultSet getColumns (String tableName) throws Exception
	{
		String query = "DESCRIBE " + tableName;
		JaqyStatement stmt = null;
		try
		{
			stmt = createStatement ();
			stmt.execute (query);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				throw new RuntimeException ("Table was not found.");

			InMemoryResultSet columnRS = new InMemoryResultSet (rs.getResultSet ());
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

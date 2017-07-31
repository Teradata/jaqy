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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.resultset.InMemClob;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
class TeradataHelper implements JaqyHelper
{
	private final JaqyConnection m_conn;

	public boolean isShowStmt (int activityType)
	{
		return activityType == 49;
	}

	public TeradataHelper (JaqyConnection conn)
	{
		m_conn = conn;
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs) throws SQLException
	{
		if (rs == null)
			return null;
		ResultSetMetaData meta = rs.getMetaData ();

		// guess if the output is from SHOW statement.
		if (meta != null &&
			meta.getColumnCount () == 1)
		{
			int type = meta.getColumnType (1);
			if (TypesUtils.isString (type))
			{
				//
				// Teradata SHOW statements due to legacy, use '\r' instead of '\n'
				// characters.  That can be problematic in the output.
				//
				// Per discussion with a Teradata JDBC architect, no internals of
				// Teradata JDBC should be exposed in OSS code.  So the only thing
				// we can do is to guess the output could be from, and replace
				// the EOL character that way.
				//
				// All SHOW statements are single column.  Typically, the output
				// is a row of VARCHAR.  However, I am not sure if there is ever
				// a case of having CLOB output, or just multiple rows.
				//
				// So we do generic handling here.
				//
				@SuppressWarnings ("resource")
				InMemoryResultSet newRS = new InMemoryResultSet (rs);
				rs.close ();
				for (Object[] row : newRS.getRows ())
				{
					for (int i = 0; i < row.length; ++i)
					{
						if (row[i] != null)
						{
							Object o = row[i];
							if (o instanceof String)
							{
								row[i] = ((String)o).replace ('\r', '\n');
							}
							else if (o instanceof InMemClob)
							{
								InMemClob clob = (InMemClob)o;
								String s = clob.getSubString (1, (int)clob.length ());
								clob.replace (s);
							}
						}
					}
				}
				rs = newRS;
			}
		}
		return new JaqyResultSet (rs, this);
	}

	@Override
	public JaqyConnection getConnection ()
	{
		return m_conn;
	}
}

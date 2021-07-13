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
package com.teradata.jaqy.utils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
public class SessionUtils
{
	public static void checkOpen (JaqyInterpreter interpreter)
	{
		Session session = interpreter.getSession ();
		if (session == null)
		{
			interpreter.error ("Not in a session.");
		}
		else
		{
			if (session.isClosed ())
			{
				interpreter.error ("Current session is closed.");
			}
		}
	}

	public static boolean tableExists (Session session, String tableName)
	{
		try
		{
			JaqyConnection conn = session.getConnection ();
			DatabaseMetaData meta = conn.getMetaData ();
			ResultSet rs = null;
			try
			{
				rs = meta.getTables (".", ".", tableName, null);

				if (rs.next ())
				{
					rs.close ();
					return true;
				}
			}
			catch (SQLException ex)
			{
				session.getGlobals ().log (Level.INFO, ex);
				if (rs != null)
				{
					try
					{
						rs.close ();
					}
					catch (SQLException ex2)
					{
					}
				}
			}
		}
		catch (SQLException ex)
		{
			session.getGlobals ().log (Level.INFO, ex);
		}
		return false;
	}

	public static int getNumColumns (Session session, JaqyInterpreter interpreter, String tableName)
	{
		JaqyResultSet rs = null;
		try
		{
			rs = interpreter.getSession ().getConnection ().getHelper ().getTableColumns (tableName, interpreter);
			int count = 0;
			while (rs.next ())
				++count;
			rs.close ();
			return count;
		}
		catch (Exception ex)
		{
			session.getGlobals ().log (Level.INFO, ex);
			if (rs != null)
			{
				try
				{
					rs.close ();
				}
				catch (SQLException ex2)
				{
				}
			}
		}
		return 0;
	}
}

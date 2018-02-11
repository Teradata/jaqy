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
package com.teradata.jaqy.command;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class PwdCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "prints the current catalog and schema.";
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		SessionUtils.checkOpen (interpreter);
		Session session = interpreter.getSession ();
		JaqyConnection conn = session.getConnection ();
		JaqyHelper helper = conn.getHelper ();
		String catalog = null;
		String schema = null;
		DatabaseMetaData meta = conn.getMetaData ();
		String catalogName = "CATALOG";
		String schemaName = "SCHEMA";

		StringBuilder builder = new StringBuilder ();

		try
		{
			catalog = conn.getCatalog (interpreter);
		}
		catch (SQLException ex)
		{
		}
		if (catalog != null && catalog.length () != 0)
		{
			try
			{
				catalogName = meta.getCatalogTerm ();
			}
			catch (SQLException ex)
			{
			}
			builder.append (catalogName).append (" : ").append (catalog);
		}

		try
		{
			schema = helper.getSchema (interpreter);
		}
		catch (SQLException ex)
		{
		}
		if (schema != null && schema.length () != 0)
		{
			try
			{
				schemaName = meta.getSchemaTerm ();
			}
			catch (SQLException ex)
			{
			}
			if (builder.length () > 0)
				builder.append (", ");
			builder.append (schemaName).append (" : ").append (schema);
		}

		interpreter.println (builder.toString ());
	}
}

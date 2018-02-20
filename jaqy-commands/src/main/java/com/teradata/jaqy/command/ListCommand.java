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
package com.teradata.jaqy.command;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class ListCommand extends JaqyCommandAdapter
{
	public ListCommand ()
	{
		super ("list.txt");
	}

	@Override
	public String getDescription ()
	{
		return "lists tables in the current catalog / schema.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.sql;
	}

	@Override
	public void execute (String[] args, boolean silent, JaqyInterpreter interpreter) throws SQLException
	{
		SessionUtils.checkOpen (interpreter);

		int listType = 2;	// 0 = catalog, 1 = schema, 2 = table

		String catalogPattern = ".";
		String schemaPattern = ".";
		String tablePattern = "%";

		if (args.length == 0)
		{
			// no change to the pattern.
		}
		else if (args.length == 1)
		{
			catalogPattern = args[0];
			// we are selecting the catalogs
			if ("%".equals (args[0]))
			{
				listType = 0;
			}
			else
			{
				listType = 1;
			}
		}
		else if (args.length == 2)
		{
			catalogPattern = args[0];
			schemaPattern = args[1];
			listType = 1;
		}
		else if (args.length >= 3)
		{
			catalogPattern = args[0];
			schemaPattern = args[1];
			tablePattern = args[2];
			listType = 2;
		}

		Session session = interpreter.getSession ();
		JaqyConnection conn = session.getConnection ();
		DatabaseMetaData meta = conn.getMetaData ();

		if (".".equals (catalogPattern))
		{
			catalogPattern = conn.getCatalog (interpreter);
		}

		if (".".equals (schemaPattern))
		{
			schemaPattern = conn.getSchema (interpreter);
		}

		JaqyHelper helper = conn.getHelper ();
		ResultSet rs = null;

		String sep = meta.getCatalogSeparator ();
		if (sep == null)
			sep = "/";
		if (listType == 0)
		{
			interpreter.println ("-- Listing catalogs");
			rs = meta.getCatalogs ();
		}
		else if (listType == 1)
		{
			interpreter.println ("-- Listing schema: " + catalogPattern + sep + schemaPattern);
			rs = meta.getSchemas (catalogPattern, schemaPattern);
		}
		else if (listType == 2)
		{
			interpreter.println ("-- Listing tables: " + catalogPattern + sep + schemaPattern + sep + tablePattern);
			rs = meta.getTables (catalogPattern, schemaPattern, tablePattern, null);
		}
		if (rs == null)
			return;

		interpreter.print (helper.getResultSet (rs, interpreter));
		rs.close ();
	}
}

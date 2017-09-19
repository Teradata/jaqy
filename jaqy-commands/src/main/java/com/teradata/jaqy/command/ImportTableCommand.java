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

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.QueryMode;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.schema.SchemaUtils;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class ImportTableCommand extends JaqyCommandAdapter
{
	public ImportTableCommand ()
	{
	}

	@Override
	public String getDescription ()
	{
		return "creates a staging table and imports data into it.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.sql;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		JaqyImporter<?> importer = interpreter.getImporter ();
		if (importer == null)
		{
			interpreter.error ("There is no current import.");
			return;
		}
		SchemaInfo schemaInfo = importer.getSchema ();
		if (schemaInfo == null)
		{
			interpreter.error ("Current import schema is not available.");
			return;
		}
		if (args.length == 0)
		{
			interpreter.error ("Staging table name is not specified.");
		}
		StringBuilder buffer = new StringBuilder ();
		for (String arg : args)
			buffer.append (arg);
		String tableName = buffer.toString ();

		SessionUtils.checkOpen (interpreter);
		Session session = interpreter.getSession ();
		JaqyConnection conn = session.getConnection ();
		JaqyHelper helper = conn.getHelper ();
		String sql = SchemaUtils.getTableSchema (helper, schemaInfo, tableName);

		boolean prevCommit = conn.getAutoCommit ();
		if (!prevCommit)
			conn.setAutoCommit (true);
		interpreter.println ("-- Table Schema --");
		interpreter.println (sql);
		session.executeQuery (sql, interpreter);
		sql = null;
		if (!prevCommit)
			conn.setAutoCommit (false);
		buffer.setLength (0);
		buffer.append ("INSERT INTO ").append (tableName).append (" VALUES (");
		int columnCount = schemaInfo.columns.length;
		for (int i = 0; i < columnCount; ++i)
		{
			if (i > 0)
				buffer.append (',');
			buffer.append ('?');
		}
		buffer.append (')');
		sql = buffer.toString ();
		interpreter.println ("-- INSERTION --");
		interpreter.println (sql);
		session.importQuery (sql, interpreter);
		interpreter.setQueryMode (QueryMode.Regular);
	}
}

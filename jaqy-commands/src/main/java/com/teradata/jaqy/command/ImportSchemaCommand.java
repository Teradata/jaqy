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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.schema.SchemaUtils;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class ImportSchemaCommand extends JaqyCommandAdapter
{
	public ImportSchemaCommand ()
	{
		addOption ("s", "sql", false, "display schema in SQL");
	}

	@Override
	protected String getSyntax ()
	{
		return getCommand () + " [options]";
	}

	@Override
	public String getDescription ()
	{
		return "displays the schema of the current import.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		JaqyImporter<?> importer = interpreter.getImporter ();
		if (importer == null)
		{
			interpreter.error ("There is no current import.");
		}
		SchemaInfo schemaInfo = importer.getSchema ();
		if (schemaInfo == null)
		{
			interpreter.error ("Current import schema is not available.");
		}
		boolean displaySQL = false;
		CommandLine cmdLine = getCommandLine (args);
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 's':
				{
					displaySQL = true;
					break;
				}
			}
		}
		SessionUtils.checkOpen (interpreter);
		Session session = interpreter.getSession ();
		JaqyHelper helper = session.getConnection ().getHelper ();
		if (displaySQL)
		{
			String sql = SchemaUtils.getTableSchema (helper, schemaInfo, "TABLENAME", false);
			interpreter.println (sql);
		}
		else
		{
			JaqyResultSet rs = SchemaUtils.getSchemaResultSet (helper, schemaInfo, false, interpreter);
			interpreter.print (rs);
			rs.close ();
		}
	}
}

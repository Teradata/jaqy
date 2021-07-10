/*
 * Copyright (c) 2021 Teradata
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
import com.teradata.jaqy.connection.JaqyDefaultResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.schema.TypeMap;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class TypeMapCommand extends JaqyCommandAdapter
{
	public TypeMapCommand ()
	{
		addOption ("i", "import", false, "display import type map");
	}

	@Override
	public String getDescription ()
	{
		return "shows the type map.";
	}

	@Override
	protected String getSyntax ()
	{
		return "usage: " + getCommand () + " [options]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.sql;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		SessionUtils.checkOpen (interpreter);
		CommandLine cmdLine = getCommandLine (args);
		boolean forImport = false;
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'i':
				{
					forImport = true;
					break;
				}
			}
		}
		JaqyHelper helper = interpreter.getSession ().getConnection ().getHelper ();
		TypeMap typeMap = helper.getTypeMap (forImport);
		JaqyDefaultResultSet rs = TypeMap.getTypeMapTable (typeMap);
		interpreter.print (rs);
		rs.close ();
	}
}

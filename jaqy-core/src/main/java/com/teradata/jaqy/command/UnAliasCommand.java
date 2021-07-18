/*
 * Copyright (c) 2017-2021 Teradata
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

/**
 * @author	Heng Yuan
 */
public class UnAliasCommand extends JaqyCommandAdapter
{
	public UnAliasCommand ()
	{
		super ("unalias");
	}

	@Override
	public String getDescription ()
	{
		return "removes a command alias.";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [name]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter)
	{
		if (args.length == 0)
		{
			interpreter.error ("missing argument.");
		}
		else
		{
			String name = args[0];
			Globals globals = interpreter.getGlobals ();
			if (globals.getAliasManager ().getAlias (name) == null)
			{
				interpreter.error ("alias " + args[0] + " is not found.");
			}
			globals.getAliasManager ().setAlias (name, null);
		}
	}
}

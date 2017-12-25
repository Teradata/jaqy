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

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;

/**
 * @author	Heng Yuan
 */
public class AliasCommand extends JaqyCommandAdapter
{
	public AliasCommand ()
	{
		super ("alias.txt");
	}

	@Override
	public String getDescription ()
	{
		return "creates a command alias.";
	}

	private void printAlias (PrintWriter pw, String name, String value)
	{
		pw.println (getCommand () + " " + name);
		pw.println (value);
		pw.println (".end " + getName ());
	}

	private void printAliases (PrintWriter pw, Map<String, String> map)
	{
		TreeMap<String, String> treeMap = new TreeMap<String, String> (map);
		for (Map.Entry<String, String> entry : treeMap.entrySet ())
		{
			printAlias (pw, entry.getKey (), entry.getValue ());
		}
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
		if (args.length == 0)
		{
			printAliases (interpreter.getDisplay ().getPrintWriter (), globals.getAliasManager ().getAliasMap ());
		}
		else
		{
			if (globals.getCommandManager ().getCommand (args[0]) != null)
			{
				interpreter.error ("alias name conflicts with an existing command.");
			}
			interpreter.setParseAction (this, args[0]);
		}
	}

	@Override
	public JaqyCommand.Type getType ()
	{
		return JaqyCommand.Type.mixed;
	}

	@Override
	public void parse (String action, Object value, Globals globals, JaqyInterpreter interpreter)
	{
		Display display = interpreter.getDisplay ();
		display.echo (interpreter, action, false);
		display.echo (interpreter, ".end " + getName (), false);
		globals.getAliasManager ().setAlias ((String)value, action);
	}
}

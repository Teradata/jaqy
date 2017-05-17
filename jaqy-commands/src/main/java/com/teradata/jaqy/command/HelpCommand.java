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

import com.teradata.jaqy.*;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class HelpCommand extends JaqyCommandAdapter
{
	private final Map<String,JaqyCommand> m_commandMap;

	public HelpCommand (CommandManager manager)
	{
		m_commandMap = manager.getCommandMap ();
	}

	private void listCommands (PrintWriter pw)
	{
		TreeMap<String,JaqyCommand> sortedMap = new TreeMap<String,JaqyCommand> ();
		sortedMap.putAll (m_commandMap);
		for (Map.Entry<String, JaqyCommand> entry : sortedMap.entrySet ())
		{
			pw.println ("\t." + entry.getKey () + " - " + entry.getValue ().getDescription ());
		}
	}

	private void helpCommand (Globals globals, PrintWriter pw, String name, JaqyCommand command, String[] args)
	{
		String syntax = command.getLongDescription ();
		if (syntax == null)
			pw.println ("No detailed help information.");
		else
			pw.println (syntax);
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, Globals globals, JaqyInterpreter interpreter)
	{
		PrintWriter pw = interpreter.getDisplay ().getPrintWriter ();

		if (args.length == 0)
		{
			listCommands (pw);
		}
		else
		{
			String cmdName = args[0];
			if (cmdName.startsWith ("."))
				cmdName = cmdName.substring (1);
			JaqyCommand cmd = m_commandMap.get (cmdName);
			if (cmd != null)
			{
				helpCommand (globals, pw, cmdName, cmd, StringUtils.shiftArgs (args));
			}
		}
	}

	@Override
	public String getDescription ()
	{
		return "displays this help message";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [optional command]";
	}
}

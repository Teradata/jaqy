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

import java.util.logging.Level;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Logging;
import com.teradata.jaqy.interfaces.Display;

/**
 * @author	Heng Yuan
 */
public class LoggingCommand extends JaqyCommandAdapter
{
	public LoggingCommand ()
	{
		super ("logging.txt");
	}

	@Override
	public String getDescription ()
	{
		return "displays / sets JDBC logging level.";
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
			interpreter.println (getCommand () + " " + (getLevel ()).toLowerCase ());
			return;
		}
		Display display = interpreter.getDisplay ();
		if ("off".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.OFF);
		}
		else if ("severe".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.SEVERE);
		}
		else if ("warning".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.WARNING);
		}
		else if ("info".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.INFO);
		}
		else if ("config".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.CONFIG);
		}
		else if ("fine".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.FINE);
		}
		else if ("finer".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.FINER);
		}
		else if ("finest".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.FINEST);
		}
		else if ("all".equals (args[0]))
		{
			Logging.setLogLevel (display, Level.ALL);
		}
	}

	private String getLevel ()
	{
		return Logging.getLevel ().getName ();
	}
}

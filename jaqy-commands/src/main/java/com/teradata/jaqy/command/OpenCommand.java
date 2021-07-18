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

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.interfaces.Display;

/**
 * @author	Heng Yuan
 */
public class OpenCommand extends JaqyCommandAdapter
{
	public OpenCommand ()
	{
		super ("open");

		addOption ("u", "user", true, "specify the user");

		OptionGroup group = new OptionGroup ();
		Option option;
		option = new Option ("p", "password", true, "specify the password");
		group.addOption (option);
		option = new Option ("f", "prompt", false, "force password prompt");
		group.addOption (option);
		addOptionGroup (group);

		Option property = Option.builder ("D")
								.argName ("name=value")
								.numberOfArgs (2)
								.valueSeparator ()
								.desc ("set a connection property")
								.build ();

		addOption (property);
	}

	@Override
	protected String getSyntax ()
	{
		return getCommand () + " [options] [url]";
	}

	@Override
	public String getDescription ()
	{
		return "opens a new JDBC session.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		Session session = interpreter.getSession ();
		Display display = interpreter.getDisplay ();

		if (session == null)
		{
			interpreter.error ("No active sessions.");
		}
		if (!session.isClosed ())
		{
			interpreter.error ("The current session already has a connection open.");
		}

		CommandLine cmdLine = getCommandLine (args);
		args = cmdLine.getArgs ();
		if (args.length == 0)
		{
			interpreter.error ("invalid command arguments.");
		}
		Properties prop = new Properties ();
		cmdLine.getOptions ();
		boolean hasUser = false;
		boolean hasPassword = false;
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'u':
				{
					prop.setProperty ("user", option.getValue ());
					hasUser = true;
					break;
				}
				case 'p':
				{
					prop.setProperty ("password", option.getValue ());
					hasPassword = true;
					break;
				}
				case 'f':
				{
					String password = display.getPassword (interpreter, "Password: ");
					prop.setProperty ("password", password);
					hasPassword = true;
				}
				case 'D':
				{
					String key = option.getValue (0);
					String value = option.getValue (1);
					prop.setProperty (key, value);
					break;
				}
			}
		}

		if (hasUser && !hasPassword)
		{
			// use a blank password if the user is specified and password is not.
			prop.setProperty ("password", "");
		}
		session.open (args[0], prop, interpreter, display);
	}
}

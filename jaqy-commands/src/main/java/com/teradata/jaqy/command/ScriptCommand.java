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

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class ScriptCommand extends JaqyCommandAdapter
{
	private static class ScriptOptions
	{
		String	encoding;
	}

	public ScriptCommand ()
	{
		addOption ("c", "charset", true, "specifies the file character set");
	}

	@Override
	public String getDescription ()
	{
		return "runs a script.";
	}

	@Override
	protected String getSyntax ()
	{
		return getCommand () + " [options] [file]";
	}

	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		ScriptOptions scriptOptions = new ScriptOptions ();

		CommandLine cmdLine = getCommandLine (args);
		args = cmdLine.getArgs ();
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'c':
					scriptOptions.encoding = option.getValue ();
					break;
			}
		}
		String file = null;
		if (args.length > 0)
			file = args[0];
		if (file == null)
		{
			interpreter.setParseAction (this, scriptOptions);
		}
		else
		{
			File scriptFile = interpreter.getFile (file);
			if (!scriptFile.exists ())
			{
				interpreter.error ("file not found: " + file);
			}
			Reader reader = FileUtils.getReader (new FileInputStream (scriptFile), scriptOptions.encoding);
			interpreter.eval (reader);
		}
	}

	@Override
	public JaqyCommand.Type getType ()
	{
		return JaqyCommand.Type.exclusive;
	}

	@Override
	public void parse (String action, Object value, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		if (!silent)
		{
			Display display = interpreter.getDisplay ();
			display.echo (interpreter, action, false);
			display.echo (interpreter, ".end " + getName (), false);
		}
		interpreter.eval (action);
	}
}

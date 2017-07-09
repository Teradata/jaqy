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
import java.io.StringReader;

import javax.script.ScriptEngine;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.interfaces.ParseAction;
import com.teradata.jaqy.parser.CommandParser;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class ScriptCommand extends JaqyCommandAdapter implements ParseAction
{
	public static String DEFAULT_LANG = "javascript";

	private static class ScriptOptions
	{
		String	lang;
		boolean	temp;
		String	encoding;
	}

	public ScriptCommand ()
	{
		addOption ("l", "lang", true, "specifies the language");
		addOption ("f", "file", true, "specifies the file");
		addOption ("t", "temporary", false, "specifies the script engine is temporary");
		addOption ("e", "encoding", true, "specifies the file encoding");
	}

	@Override
	public String getDescription ()
	{
		return "runs a script.";
	}

	@Override
	protected String getSyntax ()
	{
		return getCommand () + " [options]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		ScriptOptions scriptOptions = new ScriptOptions ();
		scriptOptions.lang = DEFAULT_LANG;
		String file = null;

		CommandLine cmdLine = getCommandLine (args);
		for (Option option : cmdLine.getOptions ())
		{
			if ("l".equals (option.getOpt ()))
			{
				scriptOptions.lang = option.getValue ();
			}
			else if ("f".equals (option.getOpt ()))
			{
				file = option.getValue ();
			}
			else if ("e".equals (option.getOpt ()))
			{
				scriptOptions.encoding = option.getValue ();
			}
			else if ("t".equals (option.getOpt ()))
			{
				scriptOptions.temp = true;
			}
		}
		if (file == null)
		{
			interpreter.setParseAction (this, scriptOptions);
		}
		else
		{
			File scriptFile = new File (file);
			if (!scriptFile.exists ())
			{
				interpreter.error ("file not found: " + file);
				return;
			}
			Reader reader = FileUtils.getReader (new FileInputStream (scriptFile), scriptOptions.encoding);
			runScript (scriptOptions, reader, globals, interpreter);
		}
	}

	@Override
	public JaqyCommand.Type getType (String arguments)
	{
		try
		{
			String[] args = CommandParser.getFileParser ().parse (arguments);
			CommandLine cmdLine = getCommandLine (args);
			for (Option option : cmdLine.getOptions ())
			{
				if ("f".equals (option.getOpt ()))
					return JaqyCommand.Type.none;
			}
			return JaqyCommand.Type.begin;
		}
		catch (Exception ex)
		{
			return JaqyCommand.Type.none;
		}
	}

	@Override
	public void parse (String action, Object value, Globals globals, JaqyInterpreter interpreter)
	{
		Display display = interpreter.getDisplay ();
		display.echo (interpreter, action, false);
		display.echo (interpreter, ".end " + getName (), false);
		runScript ((ScriptOptions)value, new StringReader (action), globals, interpreter);
	}


	private void runScript (ScriptOptions scriptOptions, Reader reader, Globals globals, JaqyInterpreter interpreter)
	{
		ScriptEngine engine = interpreter.getScriptEngine (scriptOptions.lang, scriptOptions.temp);
		if (engine == null)
		{
			interpreter.error ("unknown language: " + scriptOptions.lang);
			return;
		}
		try
		{
			engine.eval (reader);
		}
		catch (Exception ex)
		{
			ex.printStackTrace ();
		}
		finally
		{
			try
			{
				reader.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}
}

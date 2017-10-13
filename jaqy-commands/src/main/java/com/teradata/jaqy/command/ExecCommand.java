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
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.interfaces.ParseAction;
import com.teradata.jaqy.utils.FileUtils;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class ExecCommand extends JaqyCommandAdapter implements ParseAction
{
	public ExecCommand ()
	{
		addOption ("c", "charset", true, "sets the file character set");
	}

	@Override
	public String getDescription ()
	{
		return "executes a block of SQL.";
	}

	@Override
	protected String getSyntax ()
	{
		return getCommand () + " [options] [file]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		if (args.length == 0)
		{
			interpreter.setParseAction (this, null);
		}
		else
		{
			CommandLine cmdLine = getCommandLine (args);
			args = cmdLine.getArgs ();
			String charset = null;
			for (Option option : cmdLine.getOptions ())
			{
				if ("c".equals (option.getOpt ()))
				{
					charset = option.getValue ();
				}
			}
			if (args.length > 0)
			{
				File file = interpreter.getFile (args[0]);
				if (!file.exists ())
				{
					interpreter.error ("file not found: " + args[0]);
					return;
				}
				if (!SessionUtils.checkOpen (interpreter))
					return;
				Reader reader = FileUtils.getReader (new FileInputStream (file), charset);
				String sql = StringUtils.getStringFromReader (reader);
				Session session = interpreter.getSession ();
				session.executeQuery (sql, interpreter, 1);
			}
			else
			{
				interpreter.setParseAction (this, null);
			}
		}
	}

	@Override
	public JaqyCommand.Type getType (String arguments)
	{
		return JaqyCommand.Type.begin;
	}

	@Override
	public void parse (String action, Object value, Globals globals, JaqyInterpreter interpreter) throws SQLException
	{
		if (!SessionUtils.checkOpen (interpreter))
			return;
		Display display = interpreter.getDisplay ();
		display.echo (interpreter, action, false);
		Session session = interpreter.getSession ();
		session.executeQuery (action, interpreter, 1);
	}
}

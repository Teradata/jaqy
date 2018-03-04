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
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.FileUtils;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class ExecCommand extends JaqyCommandAdapter
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
	public void execute (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception
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
				Path file = interpreter.getPath (args[0]);
				if (!file.exists ())
				{
					interpreter.error ("file not found: " + args[0]);
				}
				SessionUtils.checkOpen (interpreter);
				Reader reader = FileUtils.getReader (file.getInputStream (), charset);
				String sql = StringUtils.getStringFromReader (reader);
				Session session = interpreter.getSession ();
				session.executeQuery (sql, interpreter, interpreter.getRepeatCount ());

				// now reset things
				interpreter.setRepeatCount (1);
				interpreter.setLimit (0);
			}
			else
			{
				interpreter.setParseAction (this, null);
			}
		}
	}

	@Override
	public JaqyCommand.Type getType ()
	{
		return JaqyCommand.Type.exclusive;
	}

	@Override
	public void parse (String action, Object value, boolean silent, Globals globals, JaqyInterpreter interpreter) throws SQLException
	{
		SessionUtils.checkOpen (interpreter);
		if (!silent)
		{
			Display display = interpreter.getDisplay ();
			display.echo (interpreter, action, false);
		}
		Session session = interpreter.getSession ();
		session.executeQuery (action, interpreter, 1);
	}
}

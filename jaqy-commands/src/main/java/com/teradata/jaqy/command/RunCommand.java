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

import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.lineinput.LineInputFactory;

/**
 * @author	Heng Yuan
 */
public class RunCommand extends JaqyCommandAdapter
{
	public RunCommand ()
	{
		addOption ("e", "encoding", true, "file encoding");
	}

	@Override
	protected String getSyntax ()
	{
		return ".run [options] [file]";
	}

	@Override
	public String getDescription ()
	{
		return "runs a Jaqy script.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		CommandLine cmdLine = getCommandLine (args);

		String encoding = cmdLine.getOptionValue ('e');
		args = cmdLine.getArgs ();
		if (args.length == 0)
		{
			interpreter.error ("missing file name.");
			return;
		}
		LineInput input;
		try
		{
			input = LineInputFactory.getLineInput (interpreter.getFile (args[0]), encoding, false);
		}
		catch (IOException e)
		{
			interpreter.error ("invalid file: " + args[0]);
			return;
		}
		interpreter.println ("-- Running script: " + args[0]);
		interpreter.push (input);
	}
}

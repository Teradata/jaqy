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

/**
 * @author	Heng Yuan
 */
public class ExitCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "exits the program";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [exit code]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
		globals.log (Level.INFO, "Errors: " + interpreter.getErrorCount () + ", Failures: " + interpreter.getFailureCount ());
		if (args.length == 0)
		{
			System.exit (interpreter.getExitCode ());
		}
		System.exit (Integer.parseInt (args[0]));
	}
}

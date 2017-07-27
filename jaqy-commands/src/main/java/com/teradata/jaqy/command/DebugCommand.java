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

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.DebugManager;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class DebugCommand extends JaqyCommandAdapter
{
	public DebugCommand ()
	{
		super ("debug.txt");
	}

	@Override
	public String getDescription ()
	{
		return "debug dump.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
		DebugManager debug = globals.getDebugManager ();

		if (args.length == 0)
		{
			if (debug.isDumpPreparedStatement ())
				interpreter.println (".debug preparedstatement on");
			if (debug.isDumpResultSet ())
				interpreter.println (".debug resultset on");
			return;
		}

		if (args.length != 2)
			interpreter.errorParsingArgument ();

		if ("resultset".equals (args[0]))
		{
			debug.setDumpResultSet (StringUtils.getOnOffState (args[1], "resultset"));
		}
		else if ("preparedstatement".equals (args[0]))
		{
			debug.setDumpPreparedStatement (StringUtils.getOnOffState (args[1], "preparedstatement"));
		}
	}
}

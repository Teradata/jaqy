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

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class RepeatPrevCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "repeats a previous SQL a number of times.";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [number]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.none;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		SessionUtils.checkOpen (interpreter);

		if (args.length == 0 || args[0].length () == 0)
		{
			interpreter.error ("Missing repeat count.");
		}

		String sql = interpreter.getPrevSQL ();
		if (sql == null)
		{
			interpreter.error ("No suitable previous SQL to repeat.");
		}

		String arg = interpreter.expand (args[0]);
		long repeatCount = Long.parseLong (arg);
		if (repeatCount < 1)
		{
			throw new IllegalArgumentException ("Invalid repeat count: " + repeatCount);
		}
		interpreter.getSession ().executeQuery (sql, interpreter, repeatCount);
	}
}

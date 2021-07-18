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

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class LimitCommand extends JaqyCommandAdapter
{
	public LimitCommand ()
	{
		super ("limit");
	}

	@Override
	public String getDescription ()
	{
		return "limits the output to a number of rows.";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [number]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter)
	{
		SessionUtils.checkOpen (interpreter);
		if (args.length == 0)
		{
			interpreter.println (getCommand () + " " + interpreter.getLimit ());
			return;
		}

		try
		{
			int limit = Integer.parseInt (args[0]);
			interpreter.setLimit (limit);
		}
		catch (Exception ex)
		{
			interpreter.error ("invalid limit number.");
		}
	}
}

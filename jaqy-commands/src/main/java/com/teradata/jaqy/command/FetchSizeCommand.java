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

import java.sql.SQLException;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class FetchSizeCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "sets the statement fetch size.";
	}

	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [size]";
	}

	@Override
	public void execute (String[] args, boolean silent, JaqyInterpreter interpreter) throws SQLException
	{
		SessionUtils.checkOpen (interpreter);

		if (args.length == 0)
		{
			interpreter.println (getCommand () + " " + interpreter.getSession ().getConnection ().getFetchSize ());
		}
		else
		{
			int fetchSize = Integer.parseInt (args[0]);
			if (fetchSize < 0)
			{
				interpreter.error ("Fetch size cannot be negative.");
			}
			interpreter.getSession ().getConnection ().setFetchSize (fetchSize);
		}
	}
}

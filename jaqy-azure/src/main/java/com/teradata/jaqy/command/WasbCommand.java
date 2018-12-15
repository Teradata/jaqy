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
import com.teradata.jaqy.azure.AzureUtils;

/**
 * @author	Heng Yuan
 */
public class WasbCommand extends JaqyCommandAdapter
{
	public WasbCommand ()
	{
		super ("wasb.txt");
	}

	@Override
	public String getDescription ()
	{
		return "configures Windows Azure Blob Storage client.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws SQLException
	{
		if (args.length == 0)
			interpreter.error ("missing type.");
		String type = args[0];
		String setting;
		if (args.length == 1)
			setting = "";
		else
			setting = args[1];
		if ("key".equals (type))
		{
			AzureUtils.setKey (setting, interpreter);
		}
		else if ("account".equals (type))
		{
			AzureUtils.setAccount (setting, interpreter);
		}
		else if ("endpoint".equals (type))
		{
			AzureUtils.setEndPoint (setting, interpreter);
		}
		else
		{
			interpreter.error ("unknown type.");
		}
	}
}

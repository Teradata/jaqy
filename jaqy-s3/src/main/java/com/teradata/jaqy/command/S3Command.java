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

import java.sql.SQLException;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.s3.S3Utils;

/**
 * @author	Heng Yuan
 */
public class S3Command extends JaqyCommandAdapter
{
	public S3Command ()
	{
		super ("s3", "s3.txt");
	}

	@Override
	public String getDescription ()
	{
		return "configures AWS S3 client.";
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
		if ("access".equals (type))
		{
			S3Utils.setAccess (setting, interpreter);
		}
		else if ("secret".equals (type))
		{
			S3Utils.setSecret (setting, interpreter);
		}
		else if ("region".equals (type))
		{
			S3Utils.getS3Builder (interpreter).setRegion (setting);
		}
		else
		{
			interpreter.error ("unknown type.");
		}
	}
}

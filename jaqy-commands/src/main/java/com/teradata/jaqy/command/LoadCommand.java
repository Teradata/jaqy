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
import com.teradata.jaqy.utils.PathUtils;

/**
 * @author	Heng Yuan
 */
public class LoadCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "loads a Jaqy plugin";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [classpath]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, JaqyInterpreter interpreter)
	{
		if (args.length != 1)
		{
			interpreter.error ("error parsing argument.");
		}
		String path = args[0];
		path = PathUtils.toAbsolutePath (path, interpreter.getDirectory ());
		interpreter.getGlobals ().loadPlugin (path, interpreter);
	}
}

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

import com.teradata.jaqy.JaqyInterpreter;

/**
 * Runs a shell command.
 *
 * @author	Heng Yuan
 */
public class OsCommand extends JaqyCommandAdapter
{
	public OsCommand ()
	{
	}

	@Override
	public String getDescription ()
	{
		return "executes shell commands.";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [shell commands]";
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		String argument = args[0];
		if (argument.trim ().length () == 0)
			return;
		interpreter.getGlobals ().getOs ().shell (interpreter.getFileDirectory (), argument);
	}
}

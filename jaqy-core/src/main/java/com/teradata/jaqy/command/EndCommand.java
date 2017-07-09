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
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyCommand;

/**
 * @author	Heng Yuan
 */
public class EndCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "ends a multi-line command.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	/**
	 * Mark this command as the command that ends a ParseAction.  Basically,
	 * this command always executes, even when inside a parseAction.
	 *
	 * @param	arguments
	 *			unparsed command arguments.
	 * @return	command type.
	 */
	@Override
	public JaqyCommand.Type getType (String arguments)
	{
		return JaqyCommand.Type.end;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
	}
}

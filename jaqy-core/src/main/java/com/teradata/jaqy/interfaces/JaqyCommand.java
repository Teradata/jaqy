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
package com.teradata.jaqy.interfaces;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public interface JaqyCommand
{
	enum Type
	{
		/** Nothing special. */
		none,
		/** Begins a block of action that should be handled by the command. */
		begin,
		/** Ends a block of action. */
		end
	}

	/**
	 * Initiate a command.
	 * @param	name
	 *			the command name.
	 * @param globals TODO
	 */
	public void init (String name, Globals globals);

	/**
	 * Gets the one line command description.
	 * @return	the one line command description.
	 */
	public String getDescription ();

	/**
	 * Gets the detailed command description.
	 * @return	the detailed command syntax.
	 */
	public String getLongDescription ();

	/**
	 * Gets the command parser argument type.
	 * @return	the command parser argument type.
	 */
	public CommandArgumentType getArgumentType ();

	/**
	 * Executes a command.
	 * @param	args
	 *			the command arguments.
	 * @param silent TODO
	 * @param	globals
	 *			Global states.
	 * @param	interpreter
	 *			The interpreter that calls the command.
	 * @throws	Exception
	 *			in case of error.
	 */
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception;

	/**
	 * Gets the command type.
	 *
	 * @param	arguments
	 *			unparsed command arguments.
	 * @return	the command type
	 */
	public Type getType (String arguments);
}

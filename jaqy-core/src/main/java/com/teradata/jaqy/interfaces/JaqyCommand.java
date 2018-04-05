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
package com.teradata.jaqy.interfaces;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public interface JaqyCommand extends JaqyObject
{
	enum Type
	{
		/** Not a multi-line command. */
		none,
		/** A multi-line command with regular parsing. */
		mixed,
		/** A multi-line command with its own parsing. */
		exclusive
	}

	/**
	 * Gets the command name.
	 *
	 * @return	the command name.
	 */
	public String getName ();

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
	 * @param	silent
	 * 			if the command is executed silently.
	 * @param	interactive
	 * 			if the current command is interactively executed.
	 * @param	interpreter
	 *			The interpreter that calls the command.
	 * @throws	Exception
	 *			in case of error.
	 */
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception;

	/**
	 * Gets the command type.
	 *
	 * @return	the command type
	 */
	public Type getType ();

	/**
	 * Check if a command with the given arguments is a multi-line command.
	 * The command should not be executed.
	 *
	 * @param	args
	 *			the command arguments.
	 * @return	true if the command is multi-line.  false otherwise.
	 */
	public boolean isMultiLine (String[] args);

	/**
	 * Handle the multi-line parsing.
	 *
	 * @param	action
	 * 			the multi-line text.
	 * @param	value
	 * 			the value saved prior.
	 * @param	silent
	 * 			if the command is executed silently.
	 * @param	interactive
	 * 			if the current command is interactively executed.
	 * @param	globals
	 *			Global states.
	 * @param	interpreter
	 *			The interpreter that calls the command.
	 * @throws	Exception
	 *			in case of error.
	 */
	public void parse (String action, Object value, boolean silent, boolean interactive, Globals globals, JaqyInterpreter interpreter) throws Exception;
}

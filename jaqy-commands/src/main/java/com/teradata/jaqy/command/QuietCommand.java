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

import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public class QuietCommand extends OnOffCommand
{
	@Override
	public String getDescription ()
	{
		return "turns ResultSet output on / off.";
	}

	@Override
	void execute (boolean on, JaqyInterpreter interpreter) throws SQLException
	{
		interpreter.setQuiet (on);
	}

	@Override
	void info (JaqyInterpreter interpreter) throws SQLException
	{
		interpreter.println (getCommand () + " " + (interpreter.isQuiet () ? "on" : "off"));
	}
}

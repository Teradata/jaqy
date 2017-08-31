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
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class DescribeCommand extends JaqyCommandAdapter
{
	public DescribeCommand ()
	{
	}

	@Override
	public String getDescription ()
	{
		return "describes the columns in a table.";
	}

	@Override
	protected String getSyntax ()
	{
		return getCommand () + " [table name]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.sql;
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		if (!SessionUtils.checkOpen (interpreter))
			return;
		if (args.length == 0)
		{
			interpreter.error ("Missing table name.");
			return;
		}
		String tableName = args[0];
		for (int i = 1; i < args.length; ++i)
			tableName += args[i];
		JaqyResultSet rs = interpreter.getSession ().getConnection ().getHelper ().getColumns (tableName);
		interpreter.print (rs);
		rs.close ();
	}
}

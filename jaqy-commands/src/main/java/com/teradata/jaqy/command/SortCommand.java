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

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.SortInfo;

/**
 * @author	Heng Yuan
 */
public class SortCommand extends JaqyCommandAdapter
{
	public SortCommand ()
	{
		addOption ("a", "ascending", true, "ascending sort");
		addOption ("d", "descending", true, "descending sort");
		addOption ("l", "low", false, "null sorts low");
		addOption ("h", "high", false, "null sorts high");
	}

	@Override
	protected String getSyntax ()
	{
		return ".sort [options]";
	}

	@Override
	public String getDescription ()
	{
		return "does client side ResultSet sorting.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.sql;
	}

	private SortInfo getSortInfo (String name, boolean asc, boolean nullLow)
	{
		SortInfo sortInfo = new SortInfo ();
		try
		{
			sortInfo.column = Integer.parseInt (name);
		}
		catch (Exception ex)
		{
			sortInfo.column = -1;
			sortInfo.name = name;
		}
		sortInfo.nullLow = nullLow;
		sortInfo.asc = asc;
		return sortInfo;
	}

	@Override
	public void execute (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception
	{
		SessionUtils.checkOpen (interpreter);

		CommandLine cmdLine = getCommandLine (args);

		boolean nullLow = true;

		ArrayList<SortInfo> sortInfos = new ArrayList<SortInfo> ();
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'a':
				{
					String value = option.getValue ();
					sortInfos.add (getSortInfo (value, true, nullLow));
					break;
				}
				case 'd':
				{
					String value = option.getValue ();
					sortInfos.add (getSortInfo (value, false, nullLow));
					break;
				}
				case 'l':
				{
					nullLow = true;
					break;
				}
				case 'h':
				{
					nullLow = false;
					break;
				}
			}
		}
		if (sortInfos.isEmpty ())
		{
			interpreter.error ("missing sort columns");
		}
		interpreter.setSortInfos (sortInfos.toArray (new SortInfo[sortInfos.size ()]));
	}
}

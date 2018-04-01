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
package com.teradata.jaqy.setting;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.ClientRSUtils;

/**
 * @author	Heng Yuan
 */
public class NullSortSetting extends JaqySettingAdapter
{
	@Override
	public String getDescription ()
	{
		return "sets the sort order of nulls for client side sorting.";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public Object get (JaqyInterpreter interpreter) throws Exception
	{
		return ClientRSUtils.getSortNull (interpreter) ? "low" : "high";
	}

	@Override
	public void set (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception
	{
		if ("low".equalsIgnoreCase (args[0]))
			ClientRSUtils.setSortNull (interpreter, true);
		else if ("high".equalsIgnoreCase (args[0]))
			ClientRSUtils.setSortNull (interpreter, false);
		else
			interpreter.error ("invalid nullsort value.");
	}
}

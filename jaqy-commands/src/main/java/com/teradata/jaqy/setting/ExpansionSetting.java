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

import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public class ExpansionSetting extends JaqySettingAdapter
{
	@Override
	public String getDescription ()
	{
		return "sets expression expansion on / off";
	}

	@Override
	public Object get (JaqyInterpreter interpreter) throws Exception
	{
		return (interpreter.isExpansion () ? "on" : "off");
	}

	@Override
	public void set (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception
	{
		if ("on".equalsIgnoreCase (args[0]))
			interpreter.setExpansion (true);
		else if ("off".equalsIgnoreCase (args[0]))
			interpreter.setExpansion (false);
		else
			interpreter.error ("invalid setting value");
	}
}

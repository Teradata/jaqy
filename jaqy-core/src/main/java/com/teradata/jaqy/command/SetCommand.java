/*
 * Copyright (c) 2017-2021 Teradata
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

import java.util.Map;
import java.util.TreeMap;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqySetting;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class SetCommand extends JaqyCommandAdapter
{
	public SetCommand ()
	{
		super ("set");
	}

	@Override
	public String getDescription ()
	{
		return "sets / displays settings.";
	}

	@Override
	public String getLongDescription ()
	{
		StringBuilder builder = new StringBuilder ();
		builder.append ("usage: " + getCommand () + " [setting] [value]\n\n");
		displayDescriptions (builder);
		return builder.toString ();
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.none;
	}

	private void getMap (Map<String, JaqySetting> interpreterMap, Map<String, JaqySetting> sessionMap)
	{
		Map<String, JaqySetting> settingMap = getGlobals ().getSettingManager ().getObjectMap ();
		for (Map.Entry<String, JaqySetting> entry : settingMap.entrySet ())
		{
			JaqySetting setting = entry.getValue ();
			if (setting.getType () == JaqySetting.Type.session)
			{
				sessionMap.put (entry.getKey (), setting);
			}
			else
			{
				interpreterMap.put (entry.getKey (), setting);
			}
		}
	}

	private void printDescription (String name, JaqySetting setting, StringBuilder builder)
	{
		builder.append ("\t" + getCommand () + " " + name + " - " + setting.getDescription () + "\n");
	}

	private void printDescriptions (Map<String, JaqySetting> map, StringBuilder builder)
	{
		for (Map.Entry<String, JaqySetting> entry : map.entrySet ())
		{
			printDescription (entry.getKey (), entry.getValue (), builder);
		}
	}

	private void displayDescriptions (StringBuilder builder)
	{
		TreeMap<String, JaqySetting> interpreterMap = new TreeMap<String, JaqySetting> ();
		TreeMap<String, JaqySetting> sessionMap = new TreeMap<String, JaqySetting> ();
		getMap (interpreterMap, sessionMap);

		builder.append ("-- interpreter level settings\n\n");
		printDescriptions (interpreterMap, builder);
		builder.append ("\n-- session level settings\n\n");
		printDescriptions (sessionMap, builder);
	}

	private void printSetting (String name, JaqySetting setting, JaqyInterpreter interpreter) throws Exception
	{
		interpreter.println (getCommand () + " " + name + " " + setting.get (interpreter));
	}

	private void printSettings (Map<String, JaqySetting> map, JaqyInterpreter interpreter) throws Exception
	{
		for (Map.Entry<String, JaqySetting> entry : map.entrySet ())
		{
			printSetting (entry.getKey (), entry.getValue (), interpreter);
		}
	}

	private void displaySettings (JaqyInterpreter interpreter) throws Exception
	{
		TreeMap<String, JaqySetting> interpreterMap = new TreeMap<String, JaqySetting> ();
		TreeMap<String, JaqySetting> sessionMap = new TreeMap<String, JaqySetting> ();
		getMap (interpreterMap, sessionMap);

		interpreter.println ("-- interpreter level settings");
		printSettings (interpreterMap, interpreter);
		if (!interpreter.getSession ().isClosed ())
		{
			interpreter.println ("\n-- session level settings");
			printSettings (sessionMap, interpreter);
		}
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		String str = args[0].trim ();
		if (str.length () == 0)
		{
			displaySettings (interpreter);
			return;
		}

		int space = str.indexOf (' ');
		int tab = str.indexOf ('\t');

		if (space < 0)
			space = str.length ();
		if (tab < 0)
			tab = str.length ();

		int index;
		if (space < tab)
			index = space;
		else
			index = tab;

		String prop = str.substring (0, index);
		JaqySetting setting = getGlobals ().getSettingManager ().getObject (prop);
		if (setting == null)
		{
			interpreter.error ("unknown setting: " + prop);
		}

		String arguments;
		if (index < str.length ())
			arguments = str.substring (index + 1);
		else
			arguments = "";

		if (arguments.length () == 0)
		{
			// display setting
			if (setting.getType () == JaqySetting.Type.session)
				SessionUtils.checkOpen (interpreter);
			printSetting (prop, setting, interpreter);
		}
		else
		{
			// set setting
			if (setting.getType () == JaqySetting.Type.session)
				SessionUtils.checkOpen (interpreter);
			args = StringUtils.parseArgs (setting.getArgumentType (), arguments, interpreter);
			setting.set (args, silent, interpreter);
		}
	}
}

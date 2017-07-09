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

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.PropertyTable;

/**
 * @author	Heng Yuan
 */
public class EnvCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "displays the execution environment.";
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
		PropertyTable pt = new PropertyTable (new String[] { "Key", "Value" });
		TreeMap<String, String> tree = new TreeMap<String, String> ();

		// Get system environments
//		{
//			Map<String, String> env = System.getenv ();
//			tree.putAll (env);
//		}

		// Get JVM properties
		{
			Properties prop = System.getProperties ();
			for (Enumeration<?> e = prop.propertyNames ();
				 e.hasMoreElements ();)
			{
				String key = (String) e.nextElement ();
				String value = prop.getProperty (key);
				tree.put (key, value);
			}
		}

		// Print
		for (Map.Entry<String, String> entry : tree.entrySet ())
		{
			pt.addRow (new String[] { entry.getKey (), entry.getValue () });
		}
		interpreter.print (pt);
	}
}

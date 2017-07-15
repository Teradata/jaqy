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

import java.util.Map;
import java.util.TreeMap;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.interfaces.Variable;

/**
 * @author	Heng Yuan
 */
public class VarCommand extends JaqyCommandAdapter
{
	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
		Session session = interpreter.getSession ();
		TreeMap<String, String> varMap = new TreeMap<String, String> ();
		for (Map.Entry<String, Object> entry: session.getVariableManager ().entrySet ())
		{
			Variable var = (Variable) entry.getValue ();
			varMap.put (var.getName (), var.getDescription ());
		}

		for (Map.Entry<String, String> entry: varMap.entrySet ())
		{
			System.out.printf ("%-30s%s\n", entry.getKey (), entry.getValue ());
		}
	}

	@Override
	public String getDescription ()
	{
		return "displays variables";
	}
}

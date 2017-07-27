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
package com.teradata.jaqy;

import java.util.HashMap;

import com.teradata.jaqy.interfaces.JaqyCommand;

/**
 * @author	Heng Yuan
 */
public class CommandManager
{
	private final Globals m_globals;
	private final Object m_lock = new Object ();
	private final HashMap<String, JaqyCommand> m_commandMap = new HashMap<String, JaqyCommand> ();

	public CommandManager (Globals globals)
	{
		m_globals = globals;
	}

	public void addCommand (String name, JaqyCommand cmd)
	{
		cmd.init (name, m_globals);
		synchronized (m_lock)
		{
			m_commandMap.put (name, cmd);
		}
	}

	public JaqyCommand getCommand (String name)
	{
		synchronized (m_lock)
		{
			return m_commandMap.get (name);
		}
	}

	public HashMap<String, JaqyCommand> getCommandMap ()
	{
		HashMap<String, JaqyCommand> map = new HashMap<String, JaqyCommand> ();
		synchronized (m_lock)
		{
			map.putAll (m_commandMap);
		}
		return map;
	}
}

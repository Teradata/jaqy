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
package com.teradata.jaqy.utils;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.StateHandler;

/**
 * @author	Heng Yuan
 */
public class ScriptStateHandler implements StateHandler
{
	private String m_script;
	private final Object m_lock = new Object ();

	@Override
	public String getString (JaqyInterpreter interpreter)
	{
		String script = getScript ();
		if (script == null)
			return "";
		return StringUtils.eval (script, interpreter);
	}

	public String getScript ()
	{
		synchronized (m_lock)
		{
			return m_script;
		}
	}

	public void setScript (String script)
	{
		synchronized (m_lock)
		{
			m_script = script;
		}
	}
}

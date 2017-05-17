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

/*
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * A simple manager for managing script engines.
 *
 * @author	Heng Yuan
 */
public class ScriptManager
{
	private final ScriptEngineManager m_scriptEngineManager;

	ScriptManager ()
	{
		m_scriptEngineManager = new ScriptEngineManager ();
	}

	public ScriptEngine createEngine (String type)
	{
		return m_scriptEngineManager.getEngineByName (type);
	}

	public void registerEngine (String type, ScriptEngineFactory factory)
	{
		m_scriptEngineManager.registerEngineExtension (type, factory);
	}
}

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
package com.teradata.jaqy.utils.exp;

import javax.script.ScriptEngine;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
public class JSExpNode implements ExpNode
{
	private ScriptEngine m_engine;
	private VariableManager m_vm;

	@Override
	public void bind (JaqyResultSet rs, VariableManager vm, JaqyInterpreter interpreter) throws Exception
	{
		m_engine = interpreter.getScriptEngine ();
		m_vm = vm;
	}

	@Override
	public Object get () throws Exception
	{
		return m_engine.eval (toString (), m_vm);
	}
}

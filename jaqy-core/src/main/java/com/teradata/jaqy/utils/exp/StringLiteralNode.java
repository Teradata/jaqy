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
package com.teradata.jaqy.utils.exp;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class StringLiteralNode implements ExpNode
{
	private final String m_literal;

	public StringLiteralNode (String literal)
	{
		m_literal = literal;
	}

	@Override
	public void bind (JaqyResultSet rs, JaqyInterpreter interpreter)
	{
	}

	@Override
	public Object get (ScriptEngine engine, Bindings bindings)
	{
		return m_literal;
	}

	@Override
	public String toString ()
	{
		return StringUtils.quoteJavaString (m_literal);
	}
}

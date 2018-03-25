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

/**
 * @author	Heng Yuan
 */
public class ColumnNode implements ExpNode
{
	private final String m_name;
	private final boolean m_mapped;
	private int m_column;
	private JaqyResultSet m_rs;

	public ColumnNode (String name, boolean mapped)
	{
		m_name = name;
		m_mapped = mapped;
	}

	@Override
	public void bind (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		m_rs = rs;
		m_column = rs.findColumn (m_name, m_mapped);
	}

	@Override
	public Object get (ScriptEngine engine, Bindings binding) throws Exception
	{
		return m_rs.getObject (m_column, m_mapped);
	}

	@Override
	public String toString ()
	{
		return "(rs.getObject (" + m_column + "," + m_mapped + "))";
	}
}

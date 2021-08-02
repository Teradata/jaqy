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

import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
public class ColumnNode implements ExpNode
{
	private final String m_name;
	private int m_column;
	private JaqyResultSet m_rs;

	public ColumnNode (String name)
	{
		m_name = name;
	}

	@Override
	public void bind (JaqyResultSet rs, VariableManager vm, JaqyInterpreter interpreter) throws SQLException
	{
		m_rs = rs;
		m_column = rs.findColumn (m_name);
	}

	@Override
	public Object get () throws Exception
	{
		return m_rs.getObject (m_column);
	}

	@Override
	public String toString ()
	{
		return "(rs.getObject (" + m_column + "))";
	}
}

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

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
public class FunctionNode extends JSExpNode
{
	private final String m_function;
	private final ExpNode[] m_parameters;

	public FunctionNode (String function, ExpNode[] parameters)
	{
		m_function = function;
		m_parameters = parameters;
	}

	@Override
	public void bind (JaqyResultSet rs, VariableManager vm, JaqyInterpreter interpreter) throws Exception
	{
		super.bind (rs, vm, interpreter);
		for (ExpNode parameter : m_parameters)
			parameter.bind (rs, vm, interpreter);
	}

	@Override
	public String toString ()
	{
		StringBuilder builder = new StringBuilder ();
		builder.append ('(').append (m_function).append ('(');
		boolean first = true;
		for (ExpNode parameter : m_parameters)
		{
			if (first)
				first = false;
			else
				builder.append (',');
			builder.append (parameter);
		}
		builder.append ("))");
		return builder.toString ();
	}
}

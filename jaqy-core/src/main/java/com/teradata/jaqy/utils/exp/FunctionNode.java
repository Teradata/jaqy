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
import com.teradata.jaqy.connection.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
public class FunctionNode extends JSExpNode
{
	public final String function;
	public final ExpNode[] parameters;

	public FunctionNode (String function, ExpNode[] parameters)
	{
		this.function = function;
		this.parameters = parameters;
	}

	@Override
	public void bind (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		for (ExpNode parameter : parameters)
			parameter.bind (rs, interpreter);
	}

	@Override
	public String toString ()
	{
		StringBuilder builder = new StringBuilder ();
		builder.append ('(').append (function).append ('(');
		boolean first = true;
		for (ExpNode parameter : parameters)
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

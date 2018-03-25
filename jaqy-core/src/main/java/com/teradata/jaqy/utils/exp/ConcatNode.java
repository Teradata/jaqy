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
public class ConcatNode extends JSExpNode
{
	public final ExpNode left;
	public final ExpNode right;

	public ConcatNode (ExpNode left, ExpNode right)
	{
		this.left = left;
		this.right = right;
	}

	@Override
	public void bind (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		left.bind (rs, interpreter);
		right.bind (rs, interpreter);
	}

	@Override
	public String toString ()
	{
		String leftSide = left.toString ();
		if (left instanceof ConcatNode)
		{
			leftSide = "(" + left.toString ();
		}
		else
		{
			if (left instanceof StringLiteralNode)
				leftSide = "(" + left + ".toString ()";
			else
				leftSide = "(''.concat(" + leftSide + ")";
		}
		return leftSide + ".concat (" + right + "))";
	}
}

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
package com.teradata.jaqy.command;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.parser.WhereParser;
import com.teradata.jaqy.utils.ExpNodePredicate;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.exp.ExpNode;

/**
 * @author	Heng Yuan
 */
public class FilterCommand extends JaqyCommandAdapter
{
	public FilterCommand ()
	{
		super ("filter.txt");
	}

	@Override
	protected String getSyntax ()
	{
		return ".filter [predicate]";
	}

	@Override
	public String getDescription ()
	{
		return "does client side ResultSet filtering.";
	}

	@Override
	public void execute (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception
	{
		SessionUtils.checkOpen (interpreter);

		String str = args[0].trim ();
		if (str.length () == 0)
		{
			interpreter.error ("missing predicate");
		}
		ExpNode exp = WhereParser.getExp (args[0], false);
		interpreter.setPredicate (new ExpNodePredicate (exp));
	}
}

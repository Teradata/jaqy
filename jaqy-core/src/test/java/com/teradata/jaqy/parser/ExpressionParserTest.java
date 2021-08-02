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
package com.teradata.jaqy.parser;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.ExpressionHandler;

/**
 * @author	Heng Yuan 
 */
public class ExpressionParserTest
{
	@Test
	public void testVariable () throws IOException
	{
		final HashMap<String,String> varMap = new HashMap<String, String> ();
		varMap.put ("PATH", "/c/d:/e/f");

		ExpressionHandler varHandler = new ExpressionHandler ()
		{
			@Override
			public String eval (String name) throws IOException
			{
				return varMap.get (name);
			}
		};
		String str = "abcd ${1} ${2+2} ${PATH} ${SHELL} {{abc}} {{}} {{d}} {{def}}";
		String value;
		value = ExpressionParser.getString (str, varHandler);
		Assert.assertEquals ("abcd   /c/d:/e/f  {{abc}} {{}} {{d}} {{def}}", value);
	}
}

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
package com.teradata.jaqy.parser;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.VariableHandler;

/**
 * @author	Heng Yuan 
 */
public class VariableParserTest
{
	@Test
	public void testVariable () throws IOException
	{
		final HashMap<String,String> varMap = new HashMap<String, String> ();
		varMap.put ("PATH", "/c/d:/e/f");

		final HashMap<String,String> fieldMap = new HashMap<String, String> ();
		fieldMap.put ("abc", "cool than ice");
		fieldMap.put ("def", "ice cold");

		VariableHandler varHandler = new VariableHandler ()
		{
			@Override
			public String getVariable (String name) throws IOException
			{
				return varMap.get (name);
			}
		};
		VariableHandler fieldHandler = new VariableHandler ()
		{
			@Override
			public String getVariable (String name) throws IOException
			{
				return fieldMap.get (name);
			}
		};
		String str = "abcd ${1} ${2} ${PATH} ${SHELL} {{abc}} {{}} {{d}} {{def}}";
		String value;
		value = VariableParser.getString (str, varHandler, null);
		Assert.assertEquals (value, "abcd ${1} ${2} /c/d:/e/f  {{abc}} {{}} {{d}} {{def}}");

		value = VariableParser.getString (str, varHandler, fieldHandler);
		Assert.assertEquals (value, "abcd ${1} ${2} /c/d:/e/f  cool than ice {{}}  ice cold");
	}
}

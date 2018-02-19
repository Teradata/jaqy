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
package com.teradata.jaqy.parser;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.ExpressionHandler;

/**
 * @author	Heng Yuan 
 */
public class FieldParserTest
{
	@Test
	public void testVariable () throws IOException
	{
		final HashMap<String,String> fieldMap = new HashMap<String, String> ();
		fieldMap.put ("abc", "cooler than ice");
		fieldMap.put ("def", "ice cold");

		ExpressionHandler fieldHandler = new ExpressionHandler ()
		{
			@Override
			public String eval (String name) throws IOException
			{
				return fieldMap.get (name);
			}
		};
		String str = "abcd ${1} ${2} ${PATH} ${SHELL} {{abc}} {{}} {{d}} {{def}}";
		String value;

		value = FieldParser.getString (str, fieldHandler);
		Assert.assertEquals ("abcd ${1} ${2} ${PATH} ${SHELL} cooler than ice {{}}  ice cold", value);
	}
}

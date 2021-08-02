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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan 
 */
public class ProjectParserTest
{
	@Test
	public void test1 () throws Exception
	{
		Assert.assertNotNull (ProjectParser.getExpList ("a"));
		Assert.assertNotNull (ProjectParser.getExpList ("a, b"));
		Assert.assertNotNull (ProjectParser.getExpList ("a as b, c"));
		Assert.assertNotNull (ProjectParser.getExpList ("a as b, c as d"));
		Assert.assertNotNull (ProjectParser.getExpList ("\"a\" as \" b \", c as d"));
	}

	@Test (expected = IOException.class)
	public void testError1 () throws IOException
	{
		ProjectParser.getExpList ("");
	}

	@Test (expected = IOException.class)
	public void testError2 () throws IOException
	{
		ProjectParser.getExpList ("a b");
	}

	@Test (expected = IOException.class)
	public void testError3 () throws IOException
	{
		WhereParser.getExp ("a 1");
	}

	@Test (expected = IOException.class)
	public void testError4 () throws IOException
	{
		WhereParser.getExp ("a \\ ");
	}
}

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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.utils.exp.ExpNode;

/**
 * @author	Heng Yuan
 */
public class WhereParserTest
{
	@Test
	public void test1 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (1), exp.get ());

		exp = WhereParser.getExp ("1--1");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (2), exp.get ());

		exp = WhereParser.getExp ("1-1");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (0), exp.get ());

		exp = WhereParser.getExp ("1 + 2 * 3 % 5 * 2 / 1");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (3), exp.get ());

		exp = WhereParser.getExp ("-1 + 2 * 3 % 5 * -2.125 - 2");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Double (-5.125), exp.get ());

		exp = WhereParser.getExp ("4 & 8 | 3");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (3), exp.get ());

		exp = WhereParser.getExp ("1 ^ 2");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (3), exp.get ());

		exp = WhereParser.getExp ("~2");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Integer (-3), exp.get ());

		exp = WhereParser.getExp ("'abc'");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals ("abc", exp.get ());

		exp = WhereParser.getExp ("1 < 2");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 <= (2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 = 2");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 <> 2");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 > 2");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 >= 2");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("Math.sqrt(4)");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (new Double (2), exp.get ());

		exp = WhereParser.getExp ("Math.random()");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals (Double.class, exp.get ().getClass ());

		exp = WhereParser.getExp ("1 || 2");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals ("12", exp.get ());

		exp = WhereParser.getExp ("'abc' || 2");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals ("abc2", exp.get ());

		exp = WhereParser.getExp ("'abc' || 1 || 2");
		exp.bind (null, vm, interpreter);
		Assert.assertEquals ("abc12", exp.get ());
	}

	@Test
	public void test2 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1 < any (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 < some (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 <= some (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 = some (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 <> some (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 > some (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 >= some (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 < ALL (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 <= ALL (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 = ALL (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 <> ALL (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 > ALL (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 >= ALL (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 IN (1, 2)");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("'abc' like 'a'");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("'abc' like 'a$'");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("'abc' like 'a.*'");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());
	}

	@Test
	public void test3 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1 is NULL");
		exp.bind (null, vm, interpreter);
		Assert.assertFalse ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 is not NULL");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());
	}

	@Test
	public void test4 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1 > 0 AND 2 < 4");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());

		exp = WhereParser.getExp ("1 > 0 OR 4 < 2");
		exp.bind (null, vm, interpreter);
		Assert.assertTrue ((Boolean)exp.get ());
	}

	@Test (expected = IOException.class)
	public void testError1 () throws IOException
	{
		WhereParser.getExp ("");
	}

	@Test (expected = IOException.class)
	public void testError2 () throws IOException
	{
		WhereParser.getExp ("a IN 1");
	}

	@Test (expected = IOException.class)
	public void testError3 () throws IOException
	{
		WhereParser.getExp ("a LIKE 1");
	}

	@Test (expected = IOException.class)
	public void testError4 () throws IOException
	{
		WhereParser.getExp ("a \\ ");
	}

}

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

import javax.script.ScriptEngine;

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
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		ScriptEngine engine = interpreter.getScriptEngine ();
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Integer (1), exp.get (engine, vm));

		exp = WhereParser.getExp ("1 + 2 * 3 % 5 * 2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Integer (3), exp.get (engine, vm));

		exp = WhereParser.getExp ("-1 + 2 * 3 % 5 * -2.125 - 2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Double (-5.125), exp.get (engine, vm));

		exp = WhereParser.getExp ("4 & 8 | 3", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Integer (3), exp.get (engine, vm));

		exp = WhereParser.getExp ("1 ^ 2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Integer (3), exp.get (engine, vm));

		exp = WhereParser.getExp ("~2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Integer (-3), exp.get (engine, vm));

		exp = WhereParser.getExp ("'abc'", false);
		exp.bind (null, interpreter);
		Assert.assertEquals ("abc", exp.get (engine, vm));

		exp = WhereParser.getExp ("1 < 2", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 <= 2", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 = 2", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 <> 2", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 > 2", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 >= 2", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("Math.sqrt(4)", false);
		exp.bind (null, interpreter);
		Assert.assertEquals (new Double (2), exp.get (engine, vm));

		exp = WhereParser.getExp ("1 || 2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals ("12", exp.get (engine, vm));

		exp = WhereParser.getExp ("'abc' || 2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals ("abc2", exp.get (engine, vm));

		exp = WhereParser.getExp ("'abc' || 1 || 2", false);
		exp.bind (null, interpreter);
		Assert.assertEquals ("abc12", exp.get (engine, vm));
	}

	@Test
	public void test2 () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		ScriptEngine engine = interpreter.getScriptEngine ();
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1 < any (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 < some (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 <= some (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 = some (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 <> some (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 > some (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 >= some (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 < ALL (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 <= ALL (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 = ALL (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));
		
		exp = WhereParser.getExp ("1 <> ALL (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));
		
		exp = WhereParser.getExp ("1 > ALL (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));
		
		exp = WhereParser.getExp ("1 >= ALL (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));
		
		exp = WhereParser.getExp ("1 IN (1, 2)", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("'abc' like 'a'", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("'abc' like 'a$'", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("'abc' like 'a.*'", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));
	}

	@Test
	public void test3 () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		ScriptEngine engine = interpreter.getScriptEngine ();
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1 is NULL", false);
		exp.bind (null, interpreter);
		Assert.assertFalse ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 is not NULL", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));
	}

	@Test
	public void test4 () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		ScriptEngine engine = interpreter.getScriptEngine ();
		VariableManager vm = interpreter.getVariableManager ();
		ExpNode exp;

		exp = WhereParser.getExp ("1 > 0 AND 2 < 4", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));

		exp = WhereParser.getExp ("1 > 0 OR 4 < 2", false);
		exp.bind (null, interpreter);
		Assert.assertTrue ((Boolean)exp.get (engine, vm));
	}

	@Test (expected = IOException.class)
	public void testError1 () throws IOException
	{
		WhereParser.getExp ("", false);
	}

	@Test (expected = IOException.class)
	public void testError2 () throws IOException
	{
		WhereParser.getExp ("a IN 1", false);
	}

	@Test (expected = IOException.class)
	public void testError3 () throws IOException
	{
		WhereParser.getExp ("a LIKE 1", false);
	}
}

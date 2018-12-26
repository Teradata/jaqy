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
package com.teradata.jaqy.azure;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.azure.WasbCommand;

/**
 * @author	Heng Yuan
 */
public class WasbCommandTest
{
	@Test
	public void testSettings () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		WasbCommand cmd = new WasbCommand ();

		Assert.assertNotNull (cmd.getDescription ());
		Assert.assertNotNull (cmd.getLongDescription ());
		Assert.assertEquals (CommandArgumentType.file, cmd.getArgumentType ());

		cmd.execute (new String[]{ "key" }, false, false, interpreter);
		cmd.execute (new String[]{ "key", "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==" }, false, false, interpreter);
		cmd.execute (new String[]{ "endpoint" }, false, false, interpreter);
		cmd.execute (new String[]{ "endpoint", "http://127.0.0.1:10000/devstoreaccount1" }, false, false, interpreter);
		cmd.execute (new String[]{ "account" }, false, false, interpreter);
		cmd.execute (new String[]{ "account", "devstoreaccount1" }, false, false, interpreter);
	}

	@Test(expected = JaqyException.class)
	public void testSettingsError1 () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		WasbCommand cmd = new WasbCommand ();

		Assert.assertNotNull (cmd.getDescription ());
		Assert.assertNotNull (cmd.getLongDescription ());

		cmd.execute (new String[]{ "asdf" }, false, false, interpreter);
	}

	@Test(expected = JaqyException.class)
	public void testSettingsError2 () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		WasbCommand cmd = new WasbCommand ();

		Assert.assertNotNull (cmd.getDescription ());
		Assert.assertNotNull (cmd.getLongDescription ());

		cmd.execute (new String[]{ }, false, false, interpreter);
	}
}

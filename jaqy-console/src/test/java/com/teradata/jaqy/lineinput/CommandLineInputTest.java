/*
 * Copyright (c) 2021 Teradata
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
package com.teradata.jaqy.lineinput;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.path.FilePath;

/**
 * @author	Heng Yuan
 */
public class CommandLineInputTest
{
	@Test
	public void testInput1 () throws Exception
	{
		String[] args = new String[]{ ".open", "-u", "vagrant", "postgresql://localhost" };
		FilePath path = new FilePath (new File (".").getCanonicalFile ());

		CommandLineInput cmdInput = new CommandLineInput (args, path);

		Assert.assertEquals (path, cmdInput.getDirectory ());
		Assert.assertEquals (path.getFile (), cmdInput.getFileDirectory ());

		LineInput.Input input = new LineInput.Input ();
		input.interactive = false;

		Assert.assertEquals (true, cmdInput.getLine (input));
		Assert.assertEquals (".open -u vagrant postgresql://localhost", input.line);

		Assert.assertEquals (false, cmdInput.getLine (input));
	}

	@Test
	public void testInput2 () throws Exception
	{
		String[] args = new String[]{ ".open", "-u", "vagrant", "postgresql://localhost", ";", ".help" };
		FilePath path = new FilePath (new File (".").getCanonicalFile ());

		CommandLineInput cmdInput = new CommandLineInput (args, path);

		Assert.assertEquals (path, cmdInput.getDirectory ());
		Assert.assertEquals (path.getFile (), cmdInput.getFileDirectory ());

		LineInput.Input input = new LineInput.Input ();
		input.interactive = false;

		Assert.assertEquals (true, cmdInput.getLine (input));
		Assert.assertEquals (".open -u vagrant postgresql://localhost", input.line);

		Assert.assertEquals (true, cmdInput.getLine (input));
		Assert.assertEquals (".help", input.line);

		Assert.assertEquals (false, cmdInput.getLine (input));
	}
}

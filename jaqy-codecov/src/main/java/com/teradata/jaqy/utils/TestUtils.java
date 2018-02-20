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
package com.teradata.jaqy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.rules.TemporaryFolder;

/**
 * @author	Heng Yuan
 */
public class TestUtils
{
	private static String readFile (File file) throws IOException
	{
		StringBuilder builder = new StringBuilder ();
		BufferedReader reader = new BufferedReader (new FileReader (file));
		String line;
		while ((line = reader.readLine ()) != null)
		{
			builder.append (line).append ('\n');
		}
		reader.close ();
		return builder.toString ();
	}

	public static void fileCompare (File f1, File f2) throws IOException
	{
//		boolean diff = (f1.length () != f2.length ());
//		if (diff)
//		{
//			System.out.println (f1 + " is different from " + f2);
//			System.exit (1);
//		}

		String s1 = readFile (f1);
		String s2 = readFile (f2);

		Assert.assertEquals (s1, s2);
	}

	public static void jaqyTest (TemporaryFolder testFolder, String srcFile, String controlFile) throws Exception
	{
		File tmpFile = testFolder.newFile ();

		PrintStream oldOut = System.out;
//		PrintStream oldErr = System.err;
		PrintStream newOut = new PrintStream (new FileOutputStream (tmpFile));
		System.setOut (newOut);
//		System.setErr (newOut);

		String[] args = new String[]
		{
			"--norc",
			".@run", srcFile
		};
//		com.teradata.jaqy.Log.setLevel (Level.OFF);
		com.teradata.jaqy.Main.skipStdin = true;
		com.teradata.jaqy.Main.main (args);

		System.setOut (oldOut);
//		System.setErr (oldErr);
		newOut.close ();

		fileCompare (new File (controlFile), tmpFile);
	}
}

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

import java.io.*;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.SystemUtils;
import org.junit.rules.TemporaryFolder;

/**
 * @author	Heng Yuan
 */
public class TestUtils
{
	private static class CollectingLogOutputStream extends LogOutputStream
	{
		private final StringBuilder m_builder = new StringBuilder ();
	    @Override
	    protected void processLine (String line, int level)
	    {
	    	m_builder.append (line);
	    }
	    @Override
		public String toString ()
	    {
	        return m_builder.toString ();
	    }
	}

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
		String prog;
		if (SystemUtils.IS_OS_UNIX)
		{
			prog = "diff";
		}
		else
		{
			prog = "fc";
		}
		CommandLine cmdLine = CommandLine.parse (prog + " " + f1.getCanonicalPath () + " " + f2.getCanonicalPath ());
		DefaultExecutor executor = new DefaultExecutor ();
		CollectingLogOutputStream outStream = new CollectingLogOutputStream ();
		PumpStreamHandler streamHandler = new PumpStreamHandler (outStream);
		executor.setStreamHandler (streamHandler);
		executor.execute(cmdLine);
		int exitValue = 0;
		try
		{
			exitValue = executor.execute (cmdLine);
		}
		catch (Exception ex)
		{
			exitValue = 1;
		}
		if (exitValue != 0)
		{
			String diffString = outStream.toString ();
			throw new IOException ("Input / output mismatch:\n" + diffString);
		}
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

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
package com.teradata.jaqy;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.fusesource.jansi.AnsiConsole;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.lineinput.CommandLineInput;
import com.teradata.jaqy.lineinput.JLineConsoleLineInput;
import com.teradata.jaqy.lineinput.LineInputFactory;
import com.teradata.jaqy.lineinput.ReaderLineInput;
import com.teradata.jaqy.path.FilePath;
import com.teradata.jaqy.utils.JaqyShutdownHook;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class Main
{
	public static boolean skipStdin = false;

	private final static String INTERNAL_INIT_RC = "initrc";
	private final static String USER_INIT_RC = ".jqrc";
	private final static String GREET_RC = "greet.txt";

	private static File getDefaultInitFile ()
	{
		// check if ~/.jqrc exists
		// It is a known issue for getting the correct home directory
		// on windows before JRE8.
		//
		// See http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
		//
		// I am not going to lose sleep over this particular issue.
		String home = System.getProperty ("user.home");

		File file = new File (home, USER_INIT_RC);
		if (file.exists ())
		{
			return file;
		}
		return null;
	}

	private static void loadInit (Globals globals, JaqyInterpreter interpreter, Display display, Path initFile)
	{
		LineInput lineInput;
		try
		{
			Reader reader = new InputStreamReader (Main.class.getResourceAsStream (INTERNAL_INIT_RC), "UTF-8");
			Path startDir = new FilePath (new File (System.getProperty ("user.dir")));
			lineInput = new ReaderLineInput (reader, startDir, false);
			interpreter.push (lineInput);
			interpreter.interpret (false);
		}
		catch (Exception ex)
		{
			ex.printStackTrace ();
		}

		if (initFile != null && initFile.exists ())
		{
			// check if ~/.jqrc exists
			try
			{
				lineInput = LineInputFactory.getLineInput (initFile, null, false);
				interpreter.push (lineInput);
				interpreter.interpret (false);
			}
			catch (Exception ex)
			{
				ex.printStackTrace ();
			}
		}
	}

	private static void initScreen (Globals globals, Display display)
	{
		// print version
		globals.printVersion (display.getPrintWriter (), "Jaqy Console", "1.0");

		if (display.isInteractive ())
		{
			String greet = globals.getGreeting ();
			// print greeting message
			if (greet != null)
			{
				display.println (null, greet);
			}
		}
	}

	public static void main (String[] args) throws Exception
	{
		// disable jline trace messages on SLES systems
		jline.internal.Log.setOutput (new PrintStream (new OutputStream ()
		{
			@Override
			public void write (int b) throws IOException
			{
			}
		}));

		Globals globals = new Globals ();
		// initiate the name and version
		Package pkg = Main.class.getPackage ();
		globals.setName (pkg.getImplementationTitle ());
		globals.setVersion (pkg.getImplementationVersion ());

		globals.getOs ();
		// install Jansi
		if (Os.isWindows ())
		{
			AnsiConsole.systemInstall ();
		}

		// initiate the greeting message.
		try
		{
			String greet = StringUtils.getStringFromStream (Main.class.getResourceAsStream (GREET_RC));
			globals.setGreeting (greet);
		}
		catch (Exception ex)
		{
		}

		// initiate the display
		ConsoleDisplay display = new ConsoleDisplay (globals);

		// Add shutdown hook that closes all sessions on exit.
		JaqyShutdownHook.register (globals);

		globals.getOs ();
		// register signal handlers for dealing with Ctrl-C
		if (!Os.isWindows ())
		{
			// The signal handler only works on Linux
//			new ConsoleSignalHandler (display).register ();
		}

		// now create an initial session and set the session to it.
		Session session = globals.createSession (display);

		// create an interpreter
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, display, session);

		// initiate commands
		CommandSetup.init (globals);

		// handle command line options
		OptionSetup.init (globals);

		// install predefined helper factories
		HelperSetup.init (globals);

		// load initiation scripts
		Path initFile = new FilePath (getDefaultInitFile ());
		CommandLine cmdLine = globals.getOptionManager ().getCommandLine (args);
		if (cmdLine.hasOption ("norc"))
		{
			initFile = null;
		}
		else if (cmdLine.hasOption ("rcfile"))
		{
			String fileName = cmdLine.getOptionValue ("rcfile");
			initFile = new FilePath (new File (fileName));
		}
		loadInit (globals, interpreter, display, initFile);

		// Now handle command line options
		// We want to do this after loading the initiation script to
		// allow any custom addons to be installed.
		args = globals.getOptionManager ().handleOptions (globals, display, args);

		// print the start up screen
		initScreen (globals, display);

		// we are done with the loading phase
		display.setInitiated ();

		// display the title
		if (display.isInteractive ())
		{
			display.showTitle (interpreter);
		}

		// reset the command counter so we get consistent result regardless
		// of the initiation script
		interpreter.resetCommandCount ();

		Path currentDir = new FilePath (globals.getDirectory ());
		// Current dir
		// setup the input
		if (display.isInteractive ())
		{
			globals.getOs ();
			if (Os.isWindows ())
			{
				// windows
				//
				// Windows have its own readline-like support for
				// all apps, so we can just use the default system
				// behavior.
				interpreter.push (LineInputFactory.getSimpleLineInput (System.in, currentDir, true));
			}
			else
			{
				try
				{
					// we use JLine other systems.
					interpreter.push (new JLineConsoleLineInput (currentDir));
				}
				catch (IOException ex)
				{
					// just in case we fail with JLine,
					// fall back to default.
					interpreter.push (LineInputFactory.getSimpleLineInput (System.in, currentDir, true));
				}
			}
		}
		else
		{
			if (!skipStdin)
			{
				String encoding = null;
				if (System.in.available () == 0)
				{
					// If the input available bytes is 0, it is possible the input
					// is not available.  So we do not want to guess the input
					// encoding at all.  Instead, use the default.
					encoding = Charset.defaultCharset ().displayName ();
				}
				interpreter.push (LineInputFactory.getLineInput (System.in, currentDir, encoding, false));
			}
		}

		// Interpret any remaining command line arguments first
		if (args.length > 0)
		{
			interpreter.push (new CommandLineInput (args, currentDir));
		}

		// parse the user commands
		interpreter.interpret (display.isInteractive ());

		if (!skipStdin)
		{
			globals.log (Level.INFO, "Errors: " + interpreter.getErrorCount () + ", Failures: " + interpreter.getFailureCount ());
			System.exit (interpreter.getExitCode ());
		}
	}
}

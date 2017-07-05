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
package com.teradata.jaqy;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.fusesource.jansi.AnsiConsole;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.lineinput.CommandLineInput;
import com.teradata.jaqy.lineinput.JLineConsoleLineInput;
import com.teradata.jaqy.lineinput.LineInputFactory;
import com.teradata.jaqy.lineinput.ReaderLineInput;
import com.teradata.jaqy.utils.ConsoleSignalHandler;
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

	private static void loadInit (Globals globals, JaqyInterpreter interpreter, Display display, File initFile)
	{
		LineInput lineInput;
		try
		{
			Reader reader = new InputStreamReader (Main.class.getResourceAsStream (INTERNAL_INIT_RC), "UTF-8");
			lineInput = new ReaderLineInput (reader, false);
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
		globals.printVersion (display.getPrintWriter ());

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
		Globals globals = new Globals ();
		// initiate the name and version
		Package pkg = Main.class.getPackage ();
		globals.setName (pkg.getImplementationTitle ());
		globals.setVersion (pkg.getImplementationVersion ());

		// install Jansi
		if (globals.getOs ().isWindows ())
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

		// register signal handlers for dealing with Ctrl-C
		if (!globals.getOs ().isWindows ())
		{
			// The signal handler only works on Linux
			new ConsoleSignalHandler (display).register ();
		}

		// now create an initial session and set the session to it.
		Session session = globals.createSession (display);

		// create an interpreter
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, display, session);
		// sets the display's active interpreter.
		display.setInterpreter (interpreter);


		// initiate logging
		Logging.init ();

		// initiate commands
		CommandSetup.init (globals);

		// handle command line options
		OptionSetup.init (globals);

		// load initiation scripts
		File initFile = getDefaultInitFile ();
		CommandLine cmdLine = globals.getOptionManager ().getCommandLine (args);
		if (cmdLine.hasOption ("norc"))
		{
			initFile = null;
		}
		else if (cmdLine.hasOption ("rcfile"))
		{
			String fileName = cmdLine.getOptionValue ("rcfile");
			initFile = new File (fileName);
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

		// setup the input
		if (display.isInteractive ())
		{
			if (globals.getOs ().isWindows ())
			{
				// windows
				//
				// Windows have its own readline-like support for
				// all apps, so we can just use the default system
				// behavior.
				interpreter.push (LineInputFactory.getSimpleLineInput (System.in, true));
			}
			else
			{
				try
				{
					// we use JLine other systems.
					interpreter.push (new JLineConsoleLineInput ());
				}
				catch (IOException ex)
				{
					// just in case we fail with JLine,
					// fall back to default.
					interpreter.push (LineInputFactory.getSimpleLineInput (System.in, true));
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
				interpreter.push (LineInputFactory.getLineInput (System.in, encoding, false));
			}
		}

		// Interpret any remaining command line arguments first
		if (args.length > 0)
		{
			interpreter.push (new CommandLineInput (args));
		}

		// parse the user commands
		interpreter.interpret (display.isInteractive ());
	}
}

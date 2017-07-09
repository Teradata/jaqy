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

import java.io.PrintWriter;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.ErrorStateHandler;
import com.teradata.jaqy.interfaces.StateHandler;
import com.teradata.jaqy.utils.Escape;
import com.teradata.jaqy.utils.FixedVariable;
import com.teradata.jaqy.utils.SimpleStateHandlers;
import com.teradata.jaqy.utils.TitleUtils;

public class ConsoleDisplay implements Display
{
	private final Globals m_globals;

	private final FixedVariable m_displayVar = new FixedVariable ("display", this, "Display object");
	private final Escape m_escape = new Escape (this);
	private final FixedVariable m_escapeVar = new FixedVariable ("esc", m_escape, "Escape object");

	private PrintWriter m_pw;
	private Echo m_echo = Echo.auto;
	private boolean m_colorEnabled;

	private java.io.Console m_console;

	private boolean m_initiated;

	private JaqyInterpreter m_interpreter;
	private final Object m_interpreterLock = new Object ();

	private StateHandler m_promptHandler = SimpleStateHandlers.promptHandler;
	private StateHandler m_titleHandler = SimpleStateHandlers.titleHandler;
	private StateHandler m_successHandler = SimpleStateHandlers.successHandler;
	private StateHandler m_successUpdateHandler = SimpleStateHandlers.successUpdateHandler;
	private StateHandler m_activityCountHandler = SimpleStateHandlers.activityCountHandler;
	private ErrorStateHandler m_errorHandler = SimpleStateHandlers.failureHandler;

	public ConsoleDisplay (Globals globals)
	{
		m_globals = globals;
		m_pw = new PrintWriter (System.out, true);
		m_console = System.console ();
		boolean interactive = m_console != null;
		if (!interactive)
		{
			m_echo = Echo.on;
			m_colorEnabled = false;
		}
		else
		{
			m_echo = Echo.auto;
			m_colorEnabled = true;
		}
		VariableManager varManager = globals.getVarManager ();
		varManager.setVariable (m_displayVar);
		varManager.setVariable (m_escapeVar);
	}

	@Override
	public String getPassword (JaqyInterpreter interpreter, String prompt)
	{
		if (m_console == null)
		{
			error (interpreter, "Cannot get password in non-interactive mode.");
			return null;
		}
		m_pw.print (prompt);
		m_pw.flush ();
		char[] chars = m_console.readPassword ("");
		return new String (chars);
	}

	@Override
	public void print (JaqyInterpreter interpreter, String s)
	{
		m_pw.print (s);
	}

	@Override
	public void println (JaqyInterpreter interpreter, String s)
	{
		m_pw.println (s);
	}

	@Override
	public void errorParsingArgument (JaqyInterpreter interpreter)
	{
		error (interpreter, "error parsing argument.");
	}

	@Override
	public void error (JaqyInterpreter interpreter, Throwable t)
	{
		assert Debug.debug (t);
		String msg = getErrorHandler().getString (t, null, interpreter);
		m_pw.println (msg);
	}

	@Override
	public void error (JaqyInterpreter interpreter, String msg)
	{
		msg = getErrorHandler().getString (null, msg, interpreter);
		m_pw.println (msg);
	}

	@Override
	public void echo (JaqyInterpreter interpreter, String msg, boolean interactive)
	{
		if ((m_echo == Echo.on ||
			 (m_echo == Echo.auto && !interactive)) &&
			m_initiated)
			m_pw.println (msg);
	}

	@Override
	public PrintWriter getPrintWriter ()
	{
		return m_pw;
	}

	@Override
	public void showPrompt (JaqyInterpreter interpreter)
	{
		if (!m_initiated)
			return;
		String prompt = getPromptHandler().getString (interpreter);
		m_pw.print (prompt);
		m_pw.flush ();
	}

	@Override
	public void showTitle (JaqyInterpreter interpreter)
	{
		// we do not have a console
		if (m_console == null)
			return;
		// we do not update title until we are initiated.
		if (!m_initiated)
			return;

		String title = getTitleHandler().getString (interpreter);
		TitleUtils.showTitle (title, this, m_globals.getOs ());
	}

	@Override
	public Echo getEcho ()
	{
		return m_echo;
	}

	@Override
	public void setEcho (Echo echo)
	{
		m_echo = echo;
	}

	/**
	 * Checking if we are running in interactive mode or
	 * reading input from a file.
	 *
	 * @return	true if we are running in interactive mode.
	 *			false if we are reading input from a file.
	 */
	@Override
	public boolean isInteractive ()
	{
		return m_console != null;
	}

	void setInitiated ()
	{
		m_initiated = true;
	}

	/**
	 * Check if we are currently supporting ANSI color output.
	 *
	 * @return	whether or not ANSI color output is enabled.
	 */
	public boolean isColorEnabled ()
	{
		return m_colorEnabled;
	}

	/**
	 * Enable / disable ANSI color support.
	 *
	 * @param	colorEnabled
	 *			whether ANSI color support is enabled.
	 */
	public void setColorEnabled (boolean colorEnabled)
	{
		m_colorEnabled = colorEnabled;
	}

	/**
	 * Print a color only if ANSI color is enabled.
	 *
	 * @param	color
	 *			the color string.
	 */
	public void color (String color)
	{
		if (m_colorEnabled)
			m_pw.print (color);
	}

	/**
	 * Gets the current active session.
	 *
	 * @return	the current active session.
	 */
	@Override
	public JaqyInterpreter getInterpreter ()
	{
		synchronized (m_interpreterLock)
		{
			return m_interpreter;
		}
	}

	/**
	 * Set the current active session.
	 *
	 * @param	session
	 *			the session to be set as active
	 */
	@Override
	public void setInterpreter (JaqyInterpreter interpreter)
	{
		synchronized (m_interpreterLock)
		{
			m_interpreter = interpreter;
		}
	}

	public String fill (String str)
	{
		int size = 75;
		if (str.length () < size)
		{
			char[] chars = new char[size - str.length ()];
			for (int i = 0; i < chars.length; ++i)
				chars[i] = '-';
			return str + new String (chars);
		}
		return str;
	}

	/**
	 * @return	the escape
	 */
	public Escape getEscape ()
	{
		return m_escape;
	}

	/**
	 * @return	the promptHandler
	 */
	public StateHandler getPromptHandler ()
	{
		return m_promptHandler;
	}

	/**
	 * @param	promptHandler
	 *			the promptHandler to set
	 */
	public void setPromptHandler (StateHandler promptHandler)
	{
		m_promptHandler = promptHandler;
	}

	/**
	 * @return	the titleHandler
	 */
	public StateHandler getTitleHandler ()
	{
		return m_titleHandler;
	}

	/**
	 * @param	titleHandler
	 *			the titleHandler to set
	 */
	public void setTitleHandler (StateHandler titleHandler)
	{
		m_titleHandler = titleHandler;
	}

	/**
	 * @return	the errorHandler
	 */
	public ErrorStateHandler getErrorHandler ()
	{
		return m_errorHandler;
	}

	/**
	 * @param	errorHandler
	 *			the errorHandler to set
	 */
	public void setErrorHandler (ErrorStateHandler errorHandler)
	{
		m_errorHandler = errorHandler;
	}

	@Override
	public void showSuccess (JaqyInterpreter interpreter)
	{
		m_pw.println (m_successHandler.getString (interpreter));
	}

	@Override
	public void showSuccessUpdate (JaqyInterpreter interpreter)
	{
		m_pw.println (m_successUpdateHandler.getString (interpreter));
	}

	@Override
	public void showActivityCount (JaqyInterpreter interpreter)
	{
		m_pw.println (m_activityCountHandler.getString (interpreter));
	}
}

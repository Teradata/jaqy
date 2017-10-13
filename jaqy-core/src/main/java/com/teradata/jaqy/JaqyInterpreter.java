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
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.helper.DummyHelper;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.interfaces.ParseAction;
import com.teradata.jaqy.interfaces.Variable;
import com.teradata.jaqy.lineinput.ReaderLineInput;
import com.teradata.jaqy.lineinput.StackedLineInput;
import com.teradata.jaqy.parser.CommandParser;
import com.teradata.jaqy.printer.QuietPrinter;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.utils.FixedVariable;
import com.teradata.jaqy.utils.ResultSetUtils;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.StringUtils;
import com.teradata.jaqy.utils.VariableContext;

/**
 * @author Heng Yuan
 */
public class JaqyInterpreter
{
	private final static String DEFAULT_ENGINE = "javascript";

	private final Globals m_globals;
	private final Display m_display;
	private final CommandManager m_commandManager;
	private final AliasManager m_aliasManager;

	private Session m_session;
	private int m_sqlCount;
	private int m_commandCount;

	private int m_repeatCount = 1;
	private String m_prevSQL;

	private ParseAction m_parseAction;
	private Object m_parseActionValue;
	private final Stack<String> m_actionStack = new Stack<String> ();

	private final HashMap<String, ScriptEngine> m_engines = new HashMap<String, ScriptEngine> ();
	private final ScriptEngine m_engine;

	private final StackedLineInput m_input = new StackedLineInput ();

	private boolean m_quiet;
	private JaqyPrinter m_printer;
	private JaqyExporter m_exporter;
	private JaqyImporter<?> m_importer;

	private QueryMode m_queryMode = QueryMode.Regular;

	private final VariableContext m_scriptContext = new VariableContext ();

	private final VariableManager m_varManager;
	private final Variable m_interpreterVar = new FixedVariable ("interpreter", this, "Interpreter object");
	private final Variable m_sessionVar = new Variable ()
	{
		@Override
		public Object get ()
		{
			return getSession ();
		}

		@Override
		public boolean set (Object value)
		{
			return false;
		}

		@Override
		public String getName ()
		{
			return "session";
		}

		@Override
		public String getDescription ()
		{
			return "Current session object";
		}
	};

	private final Variable m_activityCountVar = new Variable ()
	{
		@Override
		public Object get ()
		{
			return getSession ().getActivityCount ();
		}

		@Override
		public boolean set (Object value)
		{
			if (value instanceof Number)
			{
				getSession ().setActivityCount (((Number) value).longValue ());
				return true;
			}
			return false;
		}

		@Override
		public String getName ()
		{
			return "activityCount";
		}

		@Override
		public String getDescription ()
		{
			return "The number of rows in the query result";
		}
	};

	/**
	 * Temporary buffer for command executor.
	 */
	private final StringBuffer m_verbatimBuffer = new StringBuffer ();

	public JaqyInterpreter (Globals globals, Display display, Session initialSession)
	{
		m_globals = globals;
		m_display = display;
		m_session = initialSession;
		m_commandManager = globals.getCommandManager ();
		m_aliasManager = globals.getAliasManager ();
		m_varManager = new VariableManager (globals.getVarManager ());

		setupScriptEngine (display);
		m_engine = getScriptEngine (DEFAULT_ENGINE, false);

		try
		{
			m_printer = globals.getPrinterManager ().getHandler ("table", new String[0], this);
			assert m_printer != null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace ();
		}
	}

	public Globals getGlobals ()
	{
		return m_globals;
	}

	public void push (LineInput input)
	{
		m_input.push (input);
	}

	public void interpret (boolean interactive)
	{
		interpret (m_input, interactive);
	}

	public void interpret (LineInput input, boolean interactive)
	{
		Display display = m_display;
		display.showPrompt (this);

		String line;
		boolean first = true;
		StringBuffer buffer = new StringBuffer ();
		while ((line = input.getLine ()) != null)
		{
//			Log.log (Level.INFO, "interpret: " + line);
			interactive = input.isInteractive ();
//			Log.log (Level.INFO, "flags: first = " + first + ", isVerbatim() = " + isVerbatim ());
			if (first)
			{
				if (line.startsWith ("."))
				{
					buffer.setLength (0);
					boolean silent = executeCommand (line, interactive);
					if (!isVerbatim () && !silent)
						display.showPrompt (this);
					continue;
				}
				if (line.length () == 0 || line.startsWith ("--"))
				{
					buffer.setLength (0);
					if (!isVerbatim ())
						display.echo (this, line, interactive);
					continue;
				}
			}
			if (line.length () > 0)
				first = false;
			String trimmedLine = StringUtils.trimEnd (line);
			if (trimmedLine.endsWith (";"))
			{
				buffer.append (trimmedLine);

				String sql = buffer.toString ();
				buffer.setLength (0);

				if (isVerbatim ())
				{
					appendVerbatimBuffer (sql);
				}
				else
				{
					Session session = m_session;
					if (SessionUtils.checkOpen (this))
					{
						try
						{
							display.echo (this, sql, interactive);
							m_prevSQL = null;
							switch (m_queryMode)
							{
								case Regular:
								{
									m_prevSQL = sql;
									session.executeQuery (sql, this, m_repeatCount);
									break;
								}
								case Prepare:
								{
									JaqyPreparedStatement stmt = session.prepareQuery (sql, this);
									stmt.close ();
									break;
								}
								case Import:
								{
									session.importQuery (sql, this);
									break;
								}
							}
						}
						catch (Exception ex)
						{
							display.error (this, ex);
						}
						m_repeatCount = 1;
						m_queryMode = QueryMode.Regular;
						display.showPrompt (this);
					}
				}
				first = true;
			}
			else
			{
				buffer.append (line).append ('\n');
			}
		}
	}

	private boolean executeCommand (String cmdLine, boolean interactive)
	{
		Display display = m_display;
		int space = cmdLine.indexOf (' ');
		int tab = cmdLine.indexOf ('\t');

		if (space < 0)
			space = cmdLine.length ();
		if (tab < 0)
			tab = cmdLine.length ();

		int index;
		if (space < tab)
			index = space;
		else
			index = tab;

		String cmd = cmdLine.substring (1, index);
		String arguments;
		if (index < cmdLine.length ())
			arguments = cmdLine.substring (index + 1);
		else
			arguments = "";
		boolean silent = false;
		if (cmd.startsWith ("@"))
		{
			cmd = cmd.substring (1);
			silent = true;
		}
		else
		{
			try
			{
				// just to check if the string can be parsed as int.
				Integer.parseInt (cmd);

				arguments = cmd;
				cmd = "#";
			}
			catch (Exception ex)
			{
			}
		}

		if (isVerbatim ())
		{
			JaqyCommand.Type type = JaqyCommand.Type.none;
			JaqyCommand call = null;
			// existing aliases as not parse actions
			if (m_aliasManager.getAlias (cmd) != null)
				type = JaqyCommand.Type.none;
			else
			{
				call = m_commandManager.getCommand (cmd);
				m_globals.log (Level.INFO, "call = " + call);

				// unknown commands are not parse actions either
				if (call == null)
					type = JaqyCommand.Type.none;
				else
					type = call.getType (arguments);
			}
			m_globals.log (Level.INFO, "CommandType: " + type);
			switch (type)
			{
				case none:
					appendVerbatimBuffer (cmdLine);
					break;
				case begin:
					pushParseAction (cmd, cmdLine);
					break;
				case end:
				{
					CommandParser parser = CommandParser.getFileParser ();
					String[] args = parser.parse (arguments);
					popParseAction (m_session, args[0], cmdLine);
					break;
				}
			}
			return silent;
		}

		if (!silent)
		{
			incCommandCount ();
			display.echo (this, cmdLine, interactive);
		}

		String alias = m_aliasManager.getAlias (cmd);
		if (alias != null)
		{
			m_globals.log (Level.INFO, "Run alias for " + cmd);
			m_globals.log (Level.INFO, alias);
			m_globals.log (Level.INFO, "End alias for " + cmd);
			// okay, we are dealing with an alias. Let's run it
			CommandParser parser = CommandParser.getFileParser ();
			String[] args = parser.parse (arguments);
			if (args == null)
				display.errorParsingArgument (this);
			alias = AliasManager.replaceArgs (alias, args);
			try
			{
				push (new ReaderLineInput (new StringReader (alias), getDirectory (), false));
				interpret (false);
			}
			catch (Throwable t)
			{
				display.error (this, t);
				silent = false;
			}
			return silent;
		}

		JaqyCommand call = m_commandManager.getCommand (cmd);
		if (call == null)
		{
			display.error (this, "unknown command: " + cmd);
			return false;
		}

		try
		{
			String[] args = null;
			CommandArgumentType argType = call.getArgumentType ();
			switch (argType)
			{
				case none:
					args = new String[1];
					args[0] = arguments;
					break;
				case file:
				{
					CommandParser parser = CommandParser.getFileParser ();
					args = parser.parse (arguments);
					break;
				}
				case sql:
				{
					CommandParser parser = CommandParser.getSQLParser ();
					args = parser.parse (arguments);
					break;
				}
			}
			if (args == null)
				display.errorParsingArgument (this);
			call.execute (args, silent, m_globals, this);
		}
		catch (Throwable t)
		{
			display.error (this, t);
			silent = false;
		}
		return silent;
	}

	/**
	 * The verbatim status.
	 *
	 * @return the verbatim status
	 */
	public boolean isVerbatim ()
	{
		return m_parseAction != null;
	}

	private void appendVerbatimBuffer (String line)
	{
		StringBuffer buffer = m_verbatimBuffer;
		if (buffer.length () > 0)
			buffer.append ('\n');
//		m_globals.log (Level.INFO, "append verbatim: " + line);
		buffer.append (line);
	}

	public void setParseAction (ParseAction action, Object value)
	{
		m_parseAction = action;
		m_parseActionValue = value;
	}

	private void pushParseAction (String type, String cmdLine)
	{
		m_globals.log (Level.INFO, "push ParseAction " + type);
		appendVerbatimBuffer (cmdLine);
		m_actionStack.push (type);
	}

	public void popParseAction (Session session, String type, String cmdLine)
	{
		m_globals.log (Level.INFO, "pop ParseAction " + type);
		Display display = m_display;
		if (m_actionStack.isEmpty ())
		{
			if (m_parseAction != null && m_parseAction.getName ().equals (type))
			{
				ParseAction parseAction = m_parseAction;
				String action = m_verbatimBuffer.toString ();
				Object value = m_parseActionValue;
				m_parseAction = null;
				m_verbatimBuffer.setLength (0);
				m_parseActionValue = null;
				try
				{
					m_globals.log (Level.INFO, "run ParseAction " + type);
					m_globals.log (Level.INFO, "[");
					m_globals.log (Level.INFO, action);
					m_globals.log (Level.INFO, "]");
					parseAction.parse (action, value, m_globals, this);
				}
				catch (Throwable t)
				{
					display.error (this, t);
				}
				return;
			}
			display.error (this, "no matching statement to end.");
			return;
		}
		String name = m_actionStack.peek ();
		if (!name.equals (type))
		{
			display.error (this, "end " + type + " does not match " + name + ".");
			return;
		}
		appendVerbatimBuffer (cmdLine);
		m_actionStack.pop ();
	}

	/**
	 * The current sql count
	 * 
	 * @return the current sql count
	 */
	public int getSqlCount ()
	{
		return m_sqlCount;
	}

	public void incSqlCount ()
	{
		++m_sqlCount;
	}

	public void resetSqlCount ()
	{
		m_sqlCount = 0;
	}

	/**
	 * Gets the current count of commands.
	 * 
	 * @return the current count of commands.
	 */
	public int getCommandCount ()
	{
		return m_commandCount;
	}

	private void incCommandCount ()
	{
		++m_commandCount;
	}

	public void resetCommandCount ()
	{
		m_commandCount = 0;
	}

	/**
	 * @return the active session
	 */
	public Session getSession ()
	{
		return m_session;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession (Session session)
	{
		m_session = session;
	}

	public ScriptEngine getScriptEngine ()
	{
		return m_engine;
	}

	private void setupScriptEngine (Display display)
	{
		VariableManager varManager = m_varManager;
		varManager.setVariable (m_interpreterVar);
		varManager.setVariable (m_sessionVar);
		varManager.setVariable (m_activityCountVar);
		m_scriptContext.setBindings (varManager, ScriptContext.ENGINE_SCOPE);
	}

	/**
	 * Get the session script engine for a particular script type.
	 *
	 * @param type
	 *            script engine type.
	 * @param temp
	 *            is the script engine temporary
	 * @return script engine for the type.
	 */
	public ScriptEngine getScriptEngine (String type, boolean temp)
	{
		synchronized (m_engines)
		{
			ScriptEngine engine = null;
			if (!temp)
				engine = m_engines.get (type);
			if (engine == null)
			{
				engine = m_globals.getScriptManager ().createEngine (type);
				if (engine == null)
				{
					return null;
				}

				ScriptContext context;
				if (temp)
				{
					context = new SimpleScriptContext ();
					VariableManager varManager = new VariableManager (getVariableManager ());
					context.setBindings (varManager, ScriptContext.ENGINE_SCOPE);
				}
				else
				{
					context = getVariableHandler ();
					m_engines.put (type, engine);
				}
				engine.setContext (context);
			}
			return engine;
		}
	}

	public Display getDisplay ()
	{
		return m_display;
	}

	public void print (String msg)
	{
		m_display.print (this, msg);
	}

	public void println (String msg)
	{
		m_display.println (this, msg);
	}

	public long print (JaqyResultSet rs)
	{
		Display display = m_display;
		try
		{
			m_globals.log (Level.INFO, "ResultSet Type: " + ResultSetUtils.getResultSetType (rs.getType ()));
			JaqyExporter exporter = getExporter ();
			if (exporter != null)
			{
				setExporter (null);
				return exporter.export (rs, m_globals);
			}
			if (m_quiet)
			{
				return QuietPrinter.getInstance ().print (rs, m_globals, display, display.getPrintWriter ());
			}
			else
			{
				return m_printer.print (rs, m_globals, display, display.getPrintWriter ());
			}
		}
		catch (Exception ex)
		{
			display.error (this, ex);
			return -1;
		}
	}

	public void print (PropertyTable pt) throws SQLException
	{
		InMemoryResultSet rs = new InMemoryResultSet (pt);
		print (DummyHelper.getInstance ().getResultSet (rs));
	}

	/**
	 * @return the printer
	 */
	public JaqyPrinter getPrinter ()
	{
		return m_printer;
	}

	public boolean isQuiet ()
	{
		return m_quiet;
	}

	public void setQuiet (boolean quiet)
	{
		m_quiet = quiet;
	}

	public void error (String msg)
	{
		m_display.error (this, msg);
	}

	public void error (Throwable t)
	{
		m_display.error (this, t);
	}

	public void errorParsingArgument ()
	{
		m_display.errorParsingArgument (this);
	}

	public void echo (String msg, boolean interactive)
	{
		m_display.echo (this, msg, interactive);
	}

	/**
	 * @param	printer
	 *			the printer to set
	 */
	public void setPrinter (JaqyPrinter printer)
	{
		m_printer = printer;
	}

	/**
	 * @return the exporter
	 */
	public JaqyExporter getExporter ()
	{
		return m_exporter;
	}

	/**
	 * @param	exporter
	 *			the exporter to set
	 */
	public void setExporter (JaqyExporter exporter)
	{
		m_exporter = exporter;
	}

	/**
	 * @return the exporter
	 */
	public JaqyImporter<?> getImporter ()
	{
		return m_importer;
	}

	/**
	 * @param	exporter
	 *			the exporter to set
	 */
	public void setImporter (JaqyImporter<?> importer)
	{
		m_importer = importer;
		setQueryMode (QueryMode.Import);
	}

	/**
	 * @return	the queryMode
	 */
	public QueryMode getQueryMode ()
	{
		return m_queryMode;
	}

	/**
	 * @param	queryMode
	 *			the queryMode to set
	 */
	public void setQueryMode (QueryMode queryMode)
	{
		m_queryMode = queryMode;
	}

	public File getDirectory ()
	{
		return m_input.getDirectory ();
	}

	public File getFile (String name)
	{
		if ('/' == File.separatorChar && name.startsWith ("/"))
			return new File (name);
		return new File (getDirectory (), name);
	}

	public VariableManager getVariableManager ()
	{
		return m_varManager;
	}

	public VariableContext getVariableHandler ()
	{
		return m_scriptContext;
	}

	public String getPrevSQL ()
	{
		return m_prevSQL;
	}

	public void setRepeatCount (int repeatCount)
	{
		if (repeatCount < 1)
		{
			throw new IllegalArgumentException ("Invalid repeat count: " + repeatCount);
		}
		m_repeatCount = repeatCount;
	}
}

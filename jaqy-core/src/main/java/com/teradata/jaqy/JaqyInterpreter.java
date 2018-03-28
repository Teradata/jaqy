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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyFilterResultSet;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.*;
import com.teradata.jaqy.lineinput.ReaderLineInput;
import com.teradata.jaqy.lineinput.StackedLineInput;
import com.teradata.jaqy.parser.CommandParser;
import com.teradata.jaqy.parser.ExpressionParser;
import com.teradata.jaqy.printer.QuietPrinter;
import com.teradata.jaqy.utils.*;

/**
 * @author Heng Yuan
 */
public class JaqyInterpreter implements ExpressionHandler
{
	public final static int BUFFER_SIZE = 32000;

	private static class ParseAction
	{
		JaqyCommand cmd;
		JaqyCommand.Type type;
		boolean silent;
		Object value;
	}

	private static class ParseCommand
	{
		String cmd;
		String alias;
		boolean silent;
		JaqyCommand call;
		String[] args;
	}

	private final static String DEFAULT_ENGINE = "javascript";

	private final Globals m_globals;
	private final Display m_display;
	private final ObjectManager<JaqyCommand> m_commandManager;
	private final AliasManager m_aliasManager;

	private int m_failureCount;
	private int m_errorCount;

	private Session m_session;
	private int m_sqlCount;
	private int m_commandCount;

	private long m_limit = 0;
	private long m_repeatCount = 1;
	private String m_prevSQL;

	private int m_cacheSize = 4096;
	/** temp byte buffer */
	private byte[] m_byteBuffer;
	private char[] m_charBuffer;

	// client side ResultSet handling
	private SortInfo[] m_sortInfos;
	private Predicate m_predicate;
	private ProjectColumnList m_projectList;
	private boolean m_caseInsensitive;

	private final Stack<ParseAction> m_actionStack = new Stack<ParseAction> ();

	private final HashMap<String, ScriptEngine> m_engines = new HashMap<String, ScriptEngine> ();
	private final ScriptEngine m_engine;

	private final StackedLineInput m_input = new StackedLineInput ();

	private boolean m_quiet;
	private JaqyPrinter m_printer;
	private JaqyExporter m_exporter;
	private JaqyImporter<?> m_importer;

	private QueryMode m_queryMode = QueryMode.Regular;
	private boolean m_expansion = true;
	private boolean m_saveResultSet;

	private final VariableContext m_scriptContext = new VariableContext ();

	private final VariableManager m_varManager;
	private final Variable m_interpreterVar = new FixedVariable ("interpreter", this);
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
	};

	private final Variable m_iterationVar = new Variable ()
	{
		@Override
		public Object get ()
		{
			return getSession ().getIteration ();
		}

		@Override
		public boolean set (Object value)
		{
			return false;
		}

		@Override
		public String getName ()
		{
			return "iteration";
		}
	};

	/**
	 * Temporary buffer for command executor.
	 */
	private final StringBuffer m_multiLineBuffer = new StringBuffer ();

	public JaqyInterpreter (Globals globals, Display display, Session initialSession)
	{
		m_globals = globals;
		m_display = display;
		m_session = initialSession;
		m_commandManager = globals.getCommandManager ();
		m_aliasManager = globals.getAliasManager ();
		m_varManager = new VariableManager (globals.getVarManager ());

		setupScriptEngine (display);
		m_engine = getScriptEngine (DEFAULT_ENGINE);

		try
		{
			m_printer = globals.getPrinterManager ().getHandler ("table", new String[0], this);
//			assert m_printer != null;
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

	/**
	 * Check if a command is a .end command.
	 *
	 * @param	cmdLine
	 * @return	null if it is not .end command.
	 * 			string if it is .end command and 
	 */
	private String checkEndCmd (String cmdLine)
	{
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
		if (!"end".equals (cmd))
			return null;
		String arguments;
		if (index < cmdLine.length ())
			arguments = cmdLine.substring (index + 1);
		else
			arguments = "";

		CommandParser parser = CommandParser.getFileParser ();
		String[] args = parser.parse (arguments);
		if (args.length > 0)
			return args[0];
		return null;
	}

	/**
	 * Parse a command line.
	 *
	 * @param	parseCmd
	 * 			the structure to be returned.
	 * @param	cmdLine
	 * 			the command line
	 * @param	parseArgs
	 * 			parse command line arguments.  If it is false, such parsing
	 * 			avoided as much as possible.
	 * @return	true if the command is a multi-line command.
	 */
	private boolean parseCommand (ParseCommand parseCmd, String cmdLine, boolean parseArgs)
	{
		parseCmd.alias = null;
		parseCmd.silent = false;
		parseCmd.cmd = null;

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
		if (cmd.startsWith ("@"))
		{
			cmd = cmd.substring (1);
			parseCmd.silent = true;
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

		parseCmd.cmd = cmd;
		String alias = m_aliasManager.getAlias (cmd);
		if (alias != null)
		{
			parseCmd.alias = alias;
			if (parseArgs)
			{
				CommandParser parser = CommandParser.getFileParser ();
				parseCmd.args = parser.parse (arguments);
			}
			return false;
		}
		else
		{
			JaqyCommand call = m_commandManager.getObject (cmd);
			parseCmd.call = call;
			boolean multi = false;
			if (call != null && call.getType () != JaqyCommand.Type.none)
			{
				parseCmd.args = StringUtils.parseArgs (call.getArgumentType (), arguments, this);
				multi = call.isMultiLine (parseCmd.args);
			}
			else if (parseArgs && call != null)
			{
				parseCmd.args = StringUtils.parseArgs (call.getArgumentType (), arguments, this);
			}
			return multi;
		}
	}

	public void interpret (LineInput lineInput, boolean interactive)
	{
		Display display = m_display;
		display.showPrompt (this);

		ParseAction dummyAction = new ParseAction ();
		dummyAction.cmd = null;
		dummyAction.type = JaqyCommand.Type.none;
		ParseCommand parseCmd = new ParseCommand ();

		boolean first = true;
		StringBuffer buffer = new StringBuffer ();
		LineInput.Input input = new LineInput.Input ();
		while (lineInput.getLine (input))
		{
			interactive = input.interactive;
			String line = input.line;

			ParseAction action;
			if (m_actionStack.isEmpty ())
				action = dummyAction;
			else
				action = m_actionStack.peek ();

			if (action.type == JaqyCommand.Type.exclusive)
			{
				String match;
				if (line.startsWith (".end") &&
					(match = checkEndCmd (line)) != null)
				{
					if (!match.equals (action.cmd.getName ()))
					{
						++m_errorCount;
						display.error (this, "end " + match + " does not match " + action.cmd.getName () + ".");
						continue;
					}
					m_actionStack.pop ();
					if (m_actionStack.isEmpty ())
					{
						popParseAction (action);
						first = true;
						if (!action.silent)
							display.showPrompt (this);
						continue;
					}
				}
				appendMultiLineBuffer (line);
				continue;
			}

			if (first)
			{
				if (line.startsWith ("."))
				{
					boolean multi = parseCommand (parseCmd, line, !isVerbatim ());
					if (isVerbatim ())
					{
						if ("end".equals (parseCmd.cmd))
						{
							action = m_actionStack.pop ();
							if (m_actionStack.empty ())
							{
								popParseAction (action);
								first = true;
								if (!action.silent)
									display.showPrompt (this);
								continue;
							}
						}
						appendMultiLineBuffer (line);
						if (multi)
						{
							setParseAction (parseCmd.call, null);
						}
					}
					else
					{
						if (!parseCmd.silent)
						{
							display.echo (this, line, interactive);
						}
						executeCommand (parseCmd, interactive);
						// Check if the command setParseAction.  If so, update
						// the silent flag.
						if (isVerbatim ())
						{
							m_actionStack.peek ().silent = parseCmd.silent;
						}
						else
						{
							if (!parseCmd.silent)
								display.showPrompt (this);
						}
					}
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
					appendMultiLineBuffer (sql);
				}
				else
				{
					Session session = m_session;
					boolean errorCleanup = false;
					try
					{
						display.echo (this, sql, interactive);
						m_prevSQL = null;
						SessionUtils.checkOpen (this);
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
					catch (SQLException ex)
					{
						++m_failureCount;
						errorCleanup = true;
						display.error (this, ex);
					}
					catch (Throwable t)
					{
						++m_errorCount;
						errorCleanup = true;
						display.error (this, t);
					}

					if (errorCleanup)
					{
						if (m_importer != null)
						{
							setImporter (null);
						}
						if (m_exporter != null)
						{
							setExporter (null);
						}
					}

					// Now reset the status of some states
					m_repeatCount = 1;
					m_queryMode = QueryMode.Regular;
					m_sortInfos = null;
					m_predicate = null;
					m_projectList = null;
					m_limit = 0;

					display.showPrompt (this);
				}
				first = true;
			}
			else
			{
				buffer.append (line).append ('\n');
			}
		}
	}

	private void executeCommand (ParseCommand cmd, boolean interactive)
	{
		if (!cmd.silent)
		{
			incCommandCount ();
		}

		if (cmd.alias != null)
		{
			if (cmd.args == null)
			{
				error ("error parsing argument.");
			}
			String alias = AliasManager.replaceArgs (cmd.alias, cmd.args);
			try
			{
				push (new ReaderLineInput (new StringReader (alias), getDirectory (), false));
				interpret (false);
			}
			catch (Throwable t)
			{
				m_display.error (this, t);
			}
		}
		else
		{
			JaqyCommand call = cmd.call;
			if (call == null)
			{
				++m_errorCount;
				m_display.error (this, "unknown command: " + cmd.cmd);
				return;
			}
	
			try
			{
				call.execute (cmd.args, cmd.silent, this);
			}
			catch (Throwable t)
			{
				++m_errorCount;
				m_display.error (this, t);
			}
		}
	}

	/**
	 * The verbatim status.
	 *
	 * @return the verbatim status
	 */
	public boolean isVerbatim ()
	{
		return !m_actionStack.isEmpty ();
	}

	private void appendMultiLineBuffer (String line)
	{
		StringBuffer buffer = m_multiLineBuffer;
		if (buffer.length () > 0)
			buffer.append ('\n');
		buffer.append (line);
	}

	public void setParseAction (JaqyCommand cmd, Object value)
	{
		ParseAction action = new ParseAction ();
		action.cmd = cmd;
		action.type = cmd.getType ();
		action.value = value;
		m_actionStack.push (action);
	}

	public void popParseAction (ParseAction action)
	{
		String str = m_multiLineBuffer.toString ();
		Object value = action.value;
		m_multiLineBuffer.setLength (0);
		try
		{
			action.cmd.parse (str, value, action.silent, m_globals, this);
		}
		catch (Throwable t)
		{
			++m_errorCount;
			m_display.error (this, t);
		}
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
		varManager.setVariable (m_iterationVar);
		m_scriptContext.setBindings (varManager, ScriptContext.ENGINE_SCOPE);
	}

	/**
	 * Get the session script engine for a particular script type.
	 *
	 * @param	type
	 *			script engine type.
	 * @return script engine for the type.
	 */
	public ScriptEngine getScriptEngine (String type)
	{
		synchronized (m_engines)
		{
			ScriptEngine engine = null;
			engine = m_engines.get (type);
			if (engine == null)
			{
				engine = m_globals.getScriptManager ().createEngine (type);
				if (engine == null)
				{
					return null;
				}

				m_engines.put (type, engine);
				engine.setContext (m_scriptContext);
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

			// add client side projection / predicate handling
			if (m_predicate != null ||
				m_projectList != null)
			{
				@SuppressWarnings ("resource")
				JaqyFilterResultSet newRS = new JaqyFilterResultSet (rs, rs.getHelper (), this);
				newRS.setStatement (rs.getStatement ());
				newRS.setPredicate (m_predicate);
				newRS.setProjection (m_projectList);
				m_predicate = null;
				m_projectList = null;
				rs = newRS;
			}

			boolean rewind = false;
			if (m_saveResultSet)
			{
				rs = ResultSetUtils.copyResultSet (rs, 0, this);
				getVariableManager ().put ("save", rs);
				rewind = true;
				m_saveResultSet = false;
			}
			JaqyExporter exporter = getExporter ();
			if (exporter != null)
			{
				setExporter (null);
				return exporter.export (rs, this);
			}
			long activityCount;
			if (m_quiet)
			{
				activityCount = QuietPrinter.getInstance ().print (rs, display.getPrintWriter (), m_limit, this);
			}
			else
			{
				activityCount = m_printer.print (rs, display.getPrintWriter (), m_limit, this);
			}
			if (rewind)
				rs.beforeFirst ();
			return activityCount;
		}
		catch (Exception ex)
		{
			display.error (this, ex);
			return -1;
		}
	}

	public void print (PropertyTable pt) throws SQLException
	{
		print (ResultSetUtils.getResultSet (pt));
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

	/**
	 * Throws a {@link JaqyException}.
	 *
	 * @param	msg
	 *			the error message.
	 */
	public void error (String msg)
	{
//		m_display.error (this, msg);
		throw new JaqyException (msg);
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
		if (exporter != null && m_exporter != null)
		{
			try
			{
				m_exporter.close ();
			}
			catch (Exception ex)
			{
				m_globals.log (Level.INFO, ex);
			}
		}
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
		JaqyImporter<?> oldImporter = m_importer;
		if (oldImporter != null)
		{
			try
			{
				oldImporter.close ();
			}
			catch (IOException ex)
			{
				m_globals.log (Level.INFO, ex);
			}
		}
		m_importer = importer;
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
		if (queryMode != QueryMode.Import)
		{
			setImporter (null);
		}
	}

	public File getFileDirectory ()
	{
		File file = m_input.getFileDirectory ();
		if (file == null)
			return m_globals.getDirectory ();
		return file;
	}

	public Path getDirectory ()
	{
		return m_input.getDirectory ();
	}

	public Path getPath (String name) throws IOException
	{
		PathHandler handler = m_globals.getPathHandler (name);
		if (handler == null)
		{
			return getDirectory ().getRelativePath (name);
		}
		return handler.getPath (name, this);
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

	public long getRepeatCount ()
	{
		return m_repeatCount;
	}

	public void setRepeatCount (long repeatCount)
	{
		if (repeatCount < 1)
		{
			throw new IllegalArgumentException ("Invalid repeat count: " + repeatCount);
		}
		m_repeatCount = repeatCount;
	}

	public long getLimit ()
	{
		return m_limit;
	}

	public void setLimit (long limit)
	{
		m_limit = limit;
	}

	public int getFailureCount ()
	{
		return m_failureCount;
	}

	public int getErrorCount ()
	{
		return m_errorCount;
	}

	public int getExitCode ()
	{
		int code = 0;
		if (m_failureCount > 0)
			code |= 2;
		if (m_errorCount > 0)
			code |= 4;
		return code;
	}

	public SortInfo[] getSortInfos ()
	{
		return m_sortInfos;
	}

	public void setSortInfos (SortInfo[] sortInfos)
	{
		m_sortInfos = sortInfos;
	}


	public Predicate getPredicate ()
	{
		return m_predicate;
	}

	public void setPredicate (Predicate predicate)
	{
		m_predicate = predicate;
	}

	public ProjectColumnList getProjectList ()
	{
		return m_projectList;
	}

	public void setProjectList (ProjectColumnList projectList)
	{
		m_projectList = projectList;
	}

	public byte[] getByteBuffer ()
	{
		if (m_byteBuffer == null)
		{
			m_byteBuffer = new byte[BUFFER_SIZE];
		}
		return m_byteBuffer;
	}

	public char[] getCharBuffer ()
	{
		if (m_charBuffer == null)
		{
			m_charBuffer = new char[BUFFER_SIZE];
		}
		return m_charBuffer;
	}

	public int getCacheSize ()
	{
		return m_cacheSize;
	}

	public void setCacheSize (int cacheSize)
	{
		m_cacheSize = cacheSize;
	}

	public String getQueryString (String sql, int column) throws SQLException
	{
		SessionUtils.checkOpen (this);
		JaqyConnection conn = m_session.getConnection ();
		return QueryUtils.getQueryString (conn, sql, column, this);
	}

	public JaqyResultSet getResultSet (String sql) throws SQLException
	{
		return QueryUtils.getResultSet (m_globals, m_session.getConnection (), sql, this);
	}

	@Override
	public Object eval (String exp) throws IOException
	{
		try
		{
			return m_engine.eval (exp);
		}
		catch (ScriptException ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
	}

	public Object eval (Reader reader) throws IOException
	{
		try
		{
			return m_engine.eval (reader);
		}
		catch (ScriptException ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
		finally
		{
			reader.close ();
		}
	}

	public boolean isExpansion ()
	{
		return m_expansion;
	}

	public void setExpansion (boolean expansion)
	{
		m_expansion = expansion;
	}

	public String expand (String str) throws IOException
	{
		return m_expansion ? ExpressionParser.getString (str, this) : str;
	}

	public boolean isSaveResultSet ()
	{
		return m_saveResultSet;
	}

	public void setSaveResultSet (boolean saveResultSet)
	{
		m_saveResultSet = saveResultSet;
	}

	public boolean isCaseInsensitive ()
	{
		return m_caseInsensitive;
	}

	public void setCaseInsensitive (boolean caseInsensitive)
	{
		m_caseInsensitive = caseInsensitive;
	}
}

/*
 * Copyright (c) 2017-2021 Teradata
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
package com.teradata.jaqy.command;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.*;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.utils.OptionsUtils;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public abstract class JaqyCommandAdapter implements JaqyCommand
{
	/** The command name being registered. */
	private final String m_name;
	/** The globals. */
	private Globals m_globals;
	/** Resource. */
	private final String m_rc;
	/** Description read from the resource. */
	private String m_longDescription;
	/** Command options */
	private final Options m_options = new Options ();
	/** A helper flag to check whether child class added any options. */
	private boolean m_hasOptions;

	public JaqyCommandAdapter (String name)
	{
		m_name = name;
		m_rc = null;
	}

	public JaqyCommandAdapter (String name, String rc)
	{
		m_name = name;
		m_rc = rc;
	}

	public Globals getGlobals ()
	{
		return m_globals;
	}

	protected void addOption(String opt, String longOpt, boolean hasArg, String description)
	{
		m_options.addOption (opt, longOpt, hasArg, description);
		m_hasOptions = true;
	}

	protected void addOption(Option option)
	{
		m_options.addOption (option);
		m_hasOptions = true;
	}

	protected void addOptionGroup (OptionGroup group)
	{
		m_options.addOptionGroup (group);
		m_hasOptions = true;
	}

	public CommandLine getCommandLine (String[] args) throws ParseException
	{
		return new DefaultParser ().parse (m_options, args);
	}

	@Override
	public void init (Globals globals)
	{
		m_globals = globals;
	}

	@Override
	public String getName ()
	{
		return m_name;
	}

	public String getCommand ()
	{
		return "."+  m_name;
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.none;
	}

	@Override
	public String getLongDescription ()
	{
		if (m_rc != null)
		{
			if (m_longDescription != null)
				return m_longDescription;

			try
			{
				m_longDescription = StringUtils.getStringFromStream (getClass ().getResourceAsStream (m_rc));
			}
			catch (Exception ex)
			{
				m_longDescription = "";
			}
			return m_longDescription;
		}
		if (!m_hasOptions)
			return null;
		Options options = m_options;
		StringWriter sw = new StringWriter ();
		PrintWriter pw = new PrintWriter (sw);
		OptionsUtils.printHelp (pw, options, getSyntax (), getHeader (), getFooter ());
		pw.close ();
		return sw.toString ();
	}

	protected String getSyntax ()
	{
		return "usage: " + getCommand ();
	}

	protected String getHeader ()
	{
		return "options:";
	}

	protected String getFooter ()
	{
		return null;
	}

	@Override
	public Type getType ()
	{
		return Type.none;
	}

	@Override
	public boolean isMultiLine (String[] args)
	{
		return false;
	}

	@Override
	public void parse (String action, Object value, boolean silent, boolean interactive, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
	}
}

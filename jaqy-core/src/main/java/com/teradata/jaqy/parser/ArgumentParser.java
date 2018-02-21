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
package com.teradata.jaqy.parser;

import java.io.IOException;
import java.io.StringReader;

import org.yuanheng.cookcc.CookCCOption;
import org.yuanheng.cookcc.Lex;
import org.yuanheng.cookcc.Lexs;

/**
 * @author	Heng Yuan
 */
@CookCCOption (unicode = true)
public class ArgumentParser extends GeneratedArgumentParser
{
	private final String[] m_args;
	private final StringBuffer m_buffer = new StringBuffer ();

	private ArgumentParser (String[] args)
	{
		m_args = args;
	}

	@Lex (pattern = "'$'[0-9]+")
	void scanArgument1 ()
	{
		String strIndex = yyText ().substring (1);
		int index = Integer.parseInt (strIndex);
		if (index < m_args.length)
			m_buffer.append (m_args[index]);
	}

	@Lex (pattern = "'$\\('[0-9]+'\\)'")
	void scanArgument2 ()
	{
		String str = yyText ();
		String strIndex = str.substring (2, str.length () - 1);
		int index = Integer.parseInt (strIndex);
		if (index < m_args.length)
			m_buffer.append (m_args[index]);
	}

	@Lex (pattern = "'$\\('[0-9]+'-\\)'")
	void scanArgument3 ()
	{
		String str = yyText ();
		String strIndex = str.substring (2, str.length () - 2);
		int index = Integer.parseInt (strIndex);
		boolean first = true;
		for (; index < m_args.length; ++index)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				m_buffer.append (' ');
			}
			m_buffer.append (m_args[index]);
		}
	}

	@Lex (pattern = "'$\\('[0-9]+'-'[0-9]+'\\)'")
	void scanArgument4 ()
	{
		String str = yyText ();
		str = str.substring (2, str.length () - 1);
		int dashIndex = str.indexOf ('-');
		int startIndex = Integer.parseInt (str.substring (0, dashIndex));
		int endIndex = Integer.parseInt (str.substring (dashIndex + 1));
		boolean first = true;
		if (startIndex <= endIndex)
		{
			for (int index = startIndex;
				 index <= endIndex && index < m_args.length;
				 ++index)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					m_buffer.append (' ');
				}
				m_buffer.append (m_args[index]);
			}
		}
		else
		{
			if (startIndex >= m_args.length)
				startIndex = m_args.length - 1;
			for (int index = startIndex;
				 index >= endIndex;
				 --index)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					m_buffer.append (' ');
				}
				m_buffer.append (m_args[index]);
			}
		}
	}

	@Lexs (patterns = {
		@Lex (pattern = "'$*'"),
		@Lex (pattern = "'$@'")
	})
	void scanArgument5 ()
	{
		boolean first = true;
		for (int index = 0; index < m_args.length; ++index)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				m_buffer.append (' ');
			}
			m_buffer.append (m_args[index]);
		}
	}

	@Lexs (patterns = {
		@Lex (pattern = "[^$]+"),
		@Lex (pattern = ".")
	})
	void scanText ()
	{
		m_buffer.append (yyText ());
	}

	@Lex (pattern = "<<EOF>>")
	int scanEof ()
	{
		return 0;
	}

	String getString ()
	{
		return m_buffer.toString ();
	}

	public static String replaceArgs (String str, String[] args)
	{
		ArgumentParser parser = new ArgumentParser (args);
		parser.setInput (new StringReader (str));
		try
		{
			parser.yyLex ();
			return parser.getString ();
		}
		catch (IOException ex)
		{
		}
		return str;
	}
}

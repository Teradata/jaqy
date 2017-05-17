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
package com.teradata.jaqy.parser;

import java.io.IOException;
import java.io.StringReader;

import org.yuanheng.cookcc.CookCCOption;
import org.yuanheng.cookcc.Lex;
import org.yuanheng.cookcc.Lexs;

import com.teradata.jaqy.interfaces.VariableHandler;

/**
 * @author	Heng Yuan
 */
@CookCCOption (unicode = true)
public class VariableParser extends VariableGeneratedParser
{
	private final StringBuffer m_buffer = new StringBuffer ();
	private final VariableHandler m_varHandler;
	private final VariableHandler m_fieldHandler;

	private VariableParser (VariableHandler varHandler, VariableHandler fieldHandler)
	{
		m_varHandler = varHandler;
		m_fieldHandler = fieldHandler;
	}

	@Override
	public String toString ()
	{
		return m_buffer.toString ();
	}

	@Lex (pattern = "'${'[a-zA-Z_][a-zA-Z0-9_]*'}'")
	void scanVariable () throws IOException
	{
		String name = yyText ();
		name = name.substring (2, name.length () - 1);
		String value = m_varHandler.getVariable (name);
		if (value != null)
			m_buffer.append (value);
	}

	@Lex (pattern = "'{{'[^{}]+'}}'")
	void scanField () throws IOException
	{
		if (m_fieldHandler == null)
		{
			m_buffer.append (yyText ());
		}
		else
		{
			String name = yyText ();
			name = name.substring (2, name.length () - 2);
			String value = m_fieldHandler.getVariable (name);
			if (value != null)
				m_buffer.append (value);
		}
	}

	@Lexs (patterns = {
		@Lex (pattern = "[^${]+"),
		@Lex (pattern = ".")
	})
	void scanText () throws IOException
	{
		m_buffer.append (yyText ());
	}

	@Lex (pattern = "<<EOF>>")
	int scanEof ()
	{
		return 0;
	}

	public static String getString (String str, VariableHandler varHandler, VariableHandler fieldHandler) throws IOException
	{
		VariableParser parser = new VariableParser (varHandler, fieldHandler);
		parser.setInput (new StringReader (str));
		if (parser.yyLex () != 0)
			throw new IOException ("parsing error.");
		return parser.toString ();
	}
}

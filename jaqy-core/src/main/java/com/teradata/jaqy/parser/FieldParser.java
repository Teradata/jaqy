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
package com.teradata.jaqy.parser;

import java.io.IOException;
import java.io.StringReader;

import org.yuanheng.cookcc.CookCCOption;
import org.yuanheng.cookcc.Lex;
import org.yuanheng.cookcc.Lexs;

import com.teradata.jaqy.interfaces.ExpressionHandler;

/**
 * @author	Heng Yuan
 */
@CookCCOption (unicode = true)
public class FieldParser extends GeneratedFieldParser
{
	private final StringBuffer m_buffer = new StringBuffer ();
	private final ExpressionHandler m_expHandler;

	private FieldParser (ExpressionHandler fieldHandler)
	{
		m_expHandler = fieldHandler;
	}

	@Override
	public String toString ()
	{
		return m_buffer.toString ();
	}

	@Lex (pattern = "'{{'[^{}]*'}}'")
	void scanField () throws IOException
	{
		if (m_expHandler == null)
		{
			m_buffer.append (yyText ());
		}
		else
		{
			String exp = yyText ();
			exp = exp.substring (2, exp.length () - 2);
			Object o = m_expHandler.eval (exp);
			if (o != null)
				m_buffer.append (o.toString ());
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

	public static String getString (String str, ExpressionHandler expHandler) throws IOException
	{
		FieldParser parser = new FieldParser (expHandler);
		parser.setInput (new StringReader (str));
		if (parser.yyLex () != 0)
			throw new IOException ("parsing error.");
		return parser.toString ();
	}
}

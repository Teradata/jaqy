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
import java.util.ArrayList;

import org.yuanheng.cookcc.*;

import com.teradata.jaqy.utils.SortInfo;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
@CookCCOption (unicode = true, start = "start")
public class OrderByParser extends GeneratedOrderByParser
{
	@CookCCToken
	static enum Token
	{
		IDENTIFIER, DESC, ASC, INTEGER, COMMA
	}

	private static IOException getErrorException ()
	{
		return new IOException ("invalid ORDER BY syntax.");
	}

	private ArrayList<SortInfo> m_sortInfos;

	private OrderByParser ()
	{
	}

	public SortInfo[] getSortInfo ()
	{
		return m_sortInfos.toArray (new SortInfo[m_sortInfos.size ()]);
	}

	@Lexs (patterns = {
		@Lex (pattern = "desc", nocase = true, token = "DESC"),
		@Lex (pattern = "asc", nocase = true, token = "ASC"),
		@Lex (pattern = "','", token = "COMMA")
	})
	void scanToken ()
	{
	}

	@Lex (pattern = "[0-9]+", token = "INTEGER")
	Integer scanColumnId ()
	{
		return Integer.parseInt (yyText ());
	}

	@Lex (pattern = "[_A-Za-z][_A-Za-z0-9]*", token = "IDENTIFIER")
	String scanColumnName ()
	{
		return yyText ();
	}

	@Lex (pattern = "'\"'([^\"]|('\"\"'))*'\"'", token = "IDENTIFIER")
	String scanQuotedColumnName ()
	{
		return StringUtils.stripQuote (yyText (), '"');
	}

	@Lex (pattern = "[ \\t\\r\\n]")
	void scanSpace ()
	{
	}

	@Lex (pattern = ".")
	void scanInvalid () throws IOException
	{
		throw getErrorException ();
	}

	@Lex (pattern = "<<EOF>>")
	int scanEOF ()
	{
		return 0;
	}

	@Rule (lhs = "start", rhs = "rules", args = "1")
	void parseStart (ArrayList<SortInfo> rules)
	{
		m_sortInfos = rules;
	}

	@Rule (lhs = "rules", rhs = "rule", args = "1")
	ArrayList<SortInfo> parseRules (SortInfo rule)
	{
		ArrayList<SortInfo> rules = new ArrayList<SortInfo> ();
		rules.add (rule);
		return rules;
	}

	@Rule (lhs = "rules", rhs = "rules COMMA rule", args = "1 3")
	ArrayList<SortInfo> parseRules (ArrayList<SortInfo> rules, SortInfo rule)
	{
		rules.add (rule);
		return rules;
	}

	@Rule (lhs = "rule", rhs = "IDENTIFIER order", args = "1 2")
	SortInfo parseNameCol (String name, Boolean asc)
	{
		SortInfo sortInfo = new SortInfo ();
		sortInfo.asc = asc;
		sortInfo.name = name;
		return sortInfo;
	}

	@Rule (lhs = "rule", rhs = "INTEGER order", args = "1 2")
	SortInfo parseIntegerCol (Integer column, Boolean asc)
	{
		SortInfo sortInfo = new SortInfo ();
		sortInfo.asc = asc;
		sortInfo.column = column;
		return sortInfo;
	}

	@Rules (rules = {
		@Rule (lhs = "order", rhs = ""),
		@Rule (lhs = "order", rhs = "ASC")
	})
	Boolean parseAscOrder ()
	{
		return Boolean.TRUE;
	}

	@Rule (lhs = "order", rhs = "DESC")
	Boolean parseDescOrder ()
	{
		return Boolean.FALSE;
	}

	public static SortInfo[] getSortInfo (String str) throws IOException
	{
		OrderByParser parser = new OrderByParser ();
		parser.setInput (new StringReader (str));
		if (parser.yyParse () != 0)
			throw getErrorException ();
		return parser.getSortInfo ();
	}
}

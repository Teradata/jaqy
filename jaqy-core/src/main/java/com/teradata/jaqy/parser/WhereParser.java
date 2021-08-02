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
import java.util.ArrayList;

import org.yuanheng.cookcc.*;

import com.teradata.jaqy.utils.StringUtils;
import com.teradata.jaqy.utils.exp.*;

/**
 * @author	Heng Yuan
 */
@CookCCOption (unicode = true, start = "start")
public class WhereParser extends GeneratedWhereParser
{
	@CookCCToken
	static enum Token
	{
		IDENTIFIER, QUOTEDNAME, QUOTEDLITERAL, NUMBER, LPAREN, RPAREN, COMMA, IS, NULL,
		@TokenGroup (type = TokenType.LEFT)
		OR, ALL, ANY, IN,
		@TokenGroup (type = TokenType.LEFT)
		AND,
		@TokenGroup
		NOT,
		@TokenGroup
		BETWEEN,
		@TokenGroup (type = TokenType.LEFT)
		BIT_AND, BIT_OR, BIT_XOR,
		@TokenGroup (type = TokenType.LEFT)
		EQUAL, NOTEQUAL, GT, GE, LT, LE, LIKE,
		@TokenGroup (type = TokenType.LEFT)
		PLUS, MINUS, CONCAT,
		@TokenGroup (type = TokenType.LEFT)
		MUL, DIV, MOD,
		@TokenGroup
		BIT_NOT
	}

	private static IOException getErrorException ()
	{
		return new IOException ("invalid WHERE syntax.");
	}

	private ExpNode m_exp;

	private WhereParser ()
	{
	}

	public ExpNode getExp ()
	{
		return m_exp;
	}

	@Lexs (patterns = {
		@Lex (pattern = "IS", nocase = true, token = "IS"),
		@Lex (pattern = "NULL", nocase = true, token = "NULL"),
		@Lex (pattern = "AND", nocase = true, token = "AND"),
		@Lex (pattern = "OR", nocase = true, token = "OR"),
		@Lex (pattern = "NOT", nocase = true, token = "NOT"),
		@Lex (pattern = "IN", nocase = true, token = "IN"),
		@Lex (pattern = "ALL", nocase = true, token = "ALL"),
		@Lex (pattern = "SOME", nocase = true, token = "ANY"),	// treat SOME as ANY since they are the same
		@Lex (pattern = "ANY", nocase = true, token = "ANY"),
		@Lex (pattern = "LIKE", nocase = true, token = "LIKE"),
		@Lex (pattern = "BETWEEN", nocase = true, token = "BETWEEN"),
		@Lex (pattern = "'*'", token = "MUL"),
		@Lex (pattern = "'/'", token = "DIV"),
		@Lex (pattern = "'%'", token = "MOD"),
		@Lex (pattern = "'+'", token = "PLUS"),
		@Lex (pattern = "'-'", token = "MINUS"),
		@Lex (pattern = "'||'", token = "CONCAT"),
		@Lex (pattern = "'='", token = "EQUAL"),
		@Lex (pattern = "'<>'", token = "NOTEQUAL"),
		@Lex (pattern = "'>'", token = "GT"),
		@Lex (pattern = "'>='", token = "GE"),
		@Lex (pattern = "'<'", token = "LT"),
		@Lex (pattern = "'<='", token = "LE"),
		@Lex (pattern = "'&'", token = "BIT_AND"),
		@Lex (pattern = "'|'", token = "BIT_OR"),
		@Lex (pattern = "'~'", token = "BIT_NOT"),
		@Lex (pattern = "'^'", token = "BIT_XOR"),
		@Lex (pattern = "'('", token = "LPAREN"),
		@Lex (pattern = "')'", token = "RPAREN"),
		@Lex (pattern = "','", token = "COMMA"),
	})
	void scanToken ()
	{
	}

	@Lexs (patterns = {
		@Lex (pattern = "([0-9]+)('.'[0-9]+)*([eE]('+'|'-')?[0-9]+)?", token = "NUMBER"),
		@Lex (pattern = "[_A-Za-z][_A-Za-z0-9]*([ \\t]*'.'[ \\t]*[_A-Za-z][_A-Za-z0-9]*)*", token = "IDENTIFIER")
	})
	String scanText ()
	{
		return yyText ();
	}

	@Lex (pattern = "'\"'([^\"]|('\"\"'))*'\"'", token = "QUOTEDNAME")
	String scanQuotedName ()
	{
		return StringUtils.stripQuote (yyText (), '"');
	}

	@Lex (pattern = "[']([^\']|(['][']))*[']", token = "QUOTEDLITERAL")
	String scanQuotedLiteral ()
	{
		return StringUtils.stripQuote (yyText (), '\'');
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

	@Rule (lhs = "start", rhs = "exp", args = "1")
	void parseStart (ExpNode exp)
	{
		m_exp = exp;
	}

	@Rule (lhs = "exp", rhs = "exp AND exp", args = "1 3")
	ExpNode parseAndExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("&&", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp OR exp", args = "1 3")
	ExpNode parseOrExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("||", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp EQUAL exp", args = "1 3")
	ExpNode parseEqExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("==", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp EQUAL ANY LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseEqAny (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", "==", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp EQUAL ALL LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseEqAll (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "every", "==", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp NOTEQUAL exp", args = "1 3")
	ExpNode parseNotEqExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("!=", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp NOTEQUAL ANY LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseNotEqAny (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", "!=", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp NOTEQUAL ALL LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseNotEqAll (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "every", "!=", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp GT exp", args = "1 3")
	ExpNode parseGTExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode (">", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp GT ANY LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseGTAny (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", ">", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp GT ALL LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseGTAll (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "every", ">", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp GE exp", args = "1 3")
	ExpNode parseGEExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode (">=", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp GE ANY LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseGEAny (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", ">=", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp GE ALL LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseGEAll (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "every", ">=", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp LT exp", args = "1 3")
	ExpNode parseLTExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("<", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp LT ANY LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseLTAny (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", "<", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp LT ALL LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseLTAll (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "every", "<", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp LE exp", args = "1 3")
	ExpNode parseLEExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("<=", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp LE ANY LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseLEAny (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", "<=", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp LE ALL LPAREN paramlist RPAREN", args = "1 5")
	ExpNode parseLEAll (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "every", "<=", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "exp LIKE exp", args = "1 3")
	ExpNode parseLikeExp (ExpNode left, ExpNode right) throws IOException
	{
		return new LikeNode (left, right);
	}

	@Rule (lhs = "exp", rhs = "exp BETWEEN NUMBER AND NUMBER", args = "1 3 5", precedence = "BETWEEN")
	ExpNode parseBetweenExp (ExpNode exp, String lower, String upper) throws IOException
	{
		ExpNode left = new BinaryNode (">=", exp, new LiteralNode (lower));
		ExpNode right = new BinaryNode ("<=", exp, new LiteralNode (upper));
		return new BinaryNode ("&&", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp MUL exp", args = "1 3")
	ExpNode parseMulExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("*", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp DIV exp", args = "1 3")
	ExpNode parseDivExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("/", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp MOD exp", args = "1 3")
	ExpNode parseModExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("%", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp PLUS exp", args = "1 3")
	ExpNode parsePlusExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("+", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp MINUS exp", args = "1 3")
	ExpNode parseMinusExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("-", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp BIT_AND exp", args = "1 3")
	ExpNode parseBitAndExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("&", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp BIT_OR exp", args = "1 3")
	ExpNode parseBitOrExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("|", left, right);
	}

	@Rule (lhs = "exp", rhs = "exp BIT_XOR exp", args = "1 3")
	ExpNode parseBitXorExp (ExpNode left, ExpNode right)
	{
		return new BinaryNode ("^", left, right);
	}


	@Rule (lhs = "exp", rhs = "exp CONCAT exp", args = "1 3")
	ExpNode parseConcatExp (ExpNode left, ExpNode right)
	{
		return new ConcatNode (left, right);
	}

	@Rule (lhs = "exp", rhs = "BIT_NOT exp", args = "2")
	ExpNode parseBitNotExp (ExpNode exp)
	{
		return new UnaryNode ("~", exp);
	}

	@Rule (lhs = "exp", rhs = "MINUS exp", args = "2", precedence = "BIT_NOT")
	ExpNode parseNegExp (ExpNode exp)
	{
		return new UnaryNode ("-", exp);
	}


	@Rule (lhs = "exp", rhs = "exp IS NULL", args = "1")
	ExpNode parseIsNullExp (ExpNode exp)
	{
		return new IsNullNode (exp, true);
	}

	@Rule (lhs = "exp", rhs = "exp IS NOT NULL", args = "1")
	ExpNode parseIsNotNullExp (ExpNode exp)
	{
		return new IsNullNode (exp, false);
	}

	@Rule (lhs = "exp", rhs = "LPAREN exp RPAREN", args = "2")
	ExpNode parseParenExp (ExpNode exp)
	{
		return exp;
	}

	@Rule (lhs = "exp", rhs = "NUMBER", args = "1")
	ExpNode parseLiteral (String literal)
	{
		return new LiteralNode (literal);
	}

	@Rule (lhs = "exp", rhs = "QUOTEDLITERAL", args = "1")
	ExpNode parseStringLiteral (String literal)
	{
		return new StringLiteralNode (literal);
	}

	@Rules (rules = {
		@Rule (lhs = "exp", rhs = "IDENTIFIER", args = "1"),
		@Rule (lhs = "exp", rhs = "QUOTEDNAME", args = "1")
	})
	ExpNode parseColumnName (String name)
	{
		return new ColumnNode (name);
	}

	@Rule (lhs = "exp", rhs = "exp IN LPAREN paramlist RPAREN", args = "1 4")
	ExpNode parseIn (ExpNode exp, ArrayList<ExpNode> paramList)
	{
		return new ArrayOpNode (exp, "some", "==", paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "exp", rhs = "IDENTIFIER LPAREN RPAREN", args = "1")
	ExpNode parseFunction (String name)
	{
		return new FunctionNode (name, new ExpNode[0]);
	}

	@Rule (lhs = "exp", rhs = "IDENTIFIER LPAREN paramlist RPAREN", args = "1 3")
	ExpNode parseFunction (String name, ArrayList<ExpNode> paramList)
	{
		return new FunctionNode (name, paramList.toArray (new ExpNode[paramList.size ()]));
	}

	@Rule (lhs = "paramlist", rhs = "exp")
	ArrayList<ExpNode> parseParamList (ExpNode exp)
	{
		ArrayList<ExpNode> paramList = new ArrayList<ExpNode> ();
		paramList.add (exp);
		return paramList;
	}

	@Rule (lhs = "paramlist", rhs = "paramlist COMMA exp", args = "1 3")
	ArrayList<ExpNode> parseParamList (ArrayList<ExpNode> paramList, ExpNode exp)
	{
		paramList.add (exp);
		return paramList;
	}

	public static ExpNode getExp (String str) throws IOException
	{
		WhereParser parser = new WhereParser ();
		parser.setInput (new StringReader (str));
		if (parser.yyParse () != 0)
			throw getErrorException ();
		return parser.getExp ();
	}
}

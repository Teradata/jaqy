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

import org.yuanheng.cookcc.*;

import com.teradata.jaqy.utils.ProjectColumnList;
import com.teradata.jaqy.utils.ProjectColumn;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author  Heng Yuan
 */
@CookCCOption (unicode = true, start = "start")
public class ProjectParser extends GeneratedProjectParser
{
    @CookCCToken
    static enum Token
    {
        NAME, COMMA, AS
    }

    private static IOException getErrorException ()
    {
        return new IOException ("invalid WHERE syntax.");
    }

    private ProjectColumnList m_expList;

    private ProjectParser ()
    {
    }

    public ProjectColumnList getExpList ()
    {
        return m_expList;
    }

    @Lexs (patterns = {
        @Lex (pattern = "AS", nocase = true, token = "AS"),
        @Lex (pattern = "','", token = "COMMA")
    })
    void scanToken ()
    {
    }

    @Lex (pattern = "[_A-Za-z][_A-Za-z0-9]*([ \\t]*'.'[ \\t]*[_A-Za-z][_A-Za-z0-9]*)*", token = "NAME")
    String scanIdentifier ()
    {
        return yyText ();
    }

    @Lex (pattern = "'\"'([^\"]|('\"\"'))*'\"'", token = "NAME")
    String scanQuotedName ()
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

    @Rule (lhs = "start", rhs = "explist", args = "1")
    void parseStart (ProjectColumnList expList)
    {
        m_expList = expList;
    }

    @Rule (lhs = "explist", rhs = "exp")
    ProjectColumnList parseExpList (ProjectColumn exp)
    {
        ProjectColumnList expList = new ProjectColumnList ();
        expList.add (exp);
        return expList;
    }

    @Rule (lhs = "explist", rhs = "explist COMMA exp", args = "1 3")
    ProjectColumnList parseParamList (ProjectColumnList expList, ProjectColumn exp)
    {
        expList.add (exp);
        return expList;
    }

    @Rule (lhs = "exp", rhs = "NAME", args = "1")
    ProjectColumn parseName (String name)
    {
        ProjectColumn col = new ProjectColumn ();
        col.name = name;
        col.asName = name;
        return col;
    }

    @Rule (lhs = "exp", rhs = "NAME AS NAME", args = "1 3")
    ProjectColumn parseNameAsName (String name, String asName)
    {
        ProjectColumn col = new ProjectColumn ();
        col.name = name;
        col.asName = asName;
        return col;
    }

    public static ProjectColumnList getExpList (String str) throws IOException
    {
        ProjectParser parser = new ProjectParser ();
        parser.setInput (new StringReader (str));
        if (parser.yyParse () != 0)
            throw getErrorException ();
        return parser.getExpList ();
    }
}

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

import java.util.ArrayList;

/**
 * @author  Heng Yuan
 */
public class CommandParser
{
    public static enum ParserType
    {
        /**
         * In File type parser, \ is the escape character, but we only
         * escape \', \", and \\ characters.  In other cases, such as
         * "\a", no characters are excaped.  This is to make it easier
         * to enter Windows directory and file names.
         */
        File,
        /**
         * In SQL type parser, ' and " are escaped by repeating itself
         * as typical SQL string and identifier escape.  This is to make
         * it easier to enter SQL string and identifiers in commands.
         */
        SQL
    }

    private final static CommandParser s_sqlParser = new CommandParser (ParserType.SQL);
    private final static CommandParser s_fileParser = new CommandParser (ParserType.File);

    public static CommandParser getSQLParser ()
    {
        return s_sqlParser;
    }

    public static CommandParser getFileParser ()
    {
        return s_fileParser;
    }

    private final static String[] s_emptyArgs = new String[0];

    private final ParserType m_type;

    private CommandParser (ParserType type)
    {
        m_type = type;
    }

    /**
     * Parse a command line and break it down to arguments.
     * @param   arguments
     *          the command line arguments (not including the command itself)
     * @return  the parsed arguments.
     *          null in case of error.
     */
    public String[] parse (String arguments)
    {
        if (arguments == null)
            return s_emptyArgs;
        arguments = arguments.trim ();
        if (arguments.length () == 0)
            return s_emptyArgs;

        ArrayList<String> args = new ArrayList<String> ();
        char[] chars = arguments.toCharArray ();
        StringBuffer buffer = new StringBuffer (chars.length);

        char inQuote = 0;
        for (int i = 0; i < chars.length; ++i)
        {
            char ch = chars[i];
            switch (ch)
            {
                // quote characters
                case '"':
                case '\'':
                    if (inQuote == 0)
                    {
                        inQuote = ch;
                    }
                    else if (inQuote == ch)
                    {
                        if (m_type == ParserType.SQL &&
                            i < (chars.length - 1) &&
                            chars[i + 1] == ch)
                        {
                            // the next char is also a quote
                            buffer.append (ch);
                            ++i;
                        }
                        else
                        {
                            inQuote = 0;
                        }
                    }
                    buffer.append (ch);
                    break;
                case '\\':
                    if (m_type == ParserType.File &&
                        i < (chars.length - 1) &&
                        (chars[i + 1] == '"' ||
                         chars[i + 1] == '\\' ||
                         chars[i + 1] == '\''))
                    {
                        buffer.append (ch);
                        buffer.append (chars[i + 1]);
                        ++i;
                    }
                    else
                    {
                        buffer.append (ch);
                    }
                    break;
                case ' ':
                case '\t':
                    if (inQuote > 0)
                    {
                        buffer.append (ch);
                    }
                    else
                    {
                        if (buffer.length () > 0)
                        {
                            String arg = buffer.toString ();
                            buffer.setLength (0);
                            args.add (arg);
                        }
                    }
                    break;
                default:
                    buffer.append (ch);
                    break;
            }
        }

        if (inQuote > 0)
            return null;        // unterminated quoted string

        if (buffer.length () > 0)
        {
            String arg = buffer.toString ();
            buffer.setLength (0);
            args.add (arg);
        }

        return args.toArray (new String[args.size ()]);
    }
}

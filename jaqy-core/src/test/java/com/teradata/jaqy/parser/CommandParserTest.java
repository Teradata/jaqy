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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  Heng Yuan
 */
public class CommandParserTest
{
    @Test
    public void testSQLParsing ()
    {
        CommandParser parser = CommandParser.getSQLParser ();

        String[] args;
        // simple test
        args = parser.parse (null);
        Assert.assertArrayEquals (new String[0], args);

        // simple test
        args = parser.parse ("This is a test.");
        Assert.assertArrayEquals (new String[] { "This", "is", "a", "test." }, args);

        // quote parsing
        args = parser.parse ("This\"is \"a test.");
        Assert.assertArrayEquals (new String[] { "This\"is \"a", "test." }, args);

        // quote parsing
        args = parser.parse ("This \"is a\" test.");
        Assert.assertArrayEquals (new String[] { "This", "\"is a\"", "test." }, args);

        // quote parsing
        args = parser.parse ("This'is 'a test.");
        Assert.assertArrayEquals (new String[] { "This'is 'a", "test." }, args);

        // quote parsing
        args = parser.parse ("This 'is a' test.");
        Assert.assertArrayEquals (new String[] { "This", "'is a'", "test." }, args);

        // quote escape
        args = parser.parse ("This 'is'' a' test.");
        Assert.assertArrayEquals (new String[] { "This", "'is'' a'", "test." }, args);

        // mixed quotes
        args = parser.parse ("This \"is' a\" test.");
        Assert.assertArrayEquals (new String[] { "This", "\"is' a\"", "test." }, args);

        // escapes
        args = parser.parse ("This C:\\Test\\Temp");
        Assert.assertArrayEquals (new String[] { "This", "C:\\Test\\Temp" }, args);

        // quote parsing failure
        args = parser.parse ("This\"is \\a test.");
        Assert.assertNull (args);

        // quote parsing failure
        args = parser.parse ("This \"is \\a test.");
        Assert.assertNull (args);

        // quote parsing failure
        args = parser.parse ("This 'is'' a test.");
        Assert.assertNull (args);
    }
}

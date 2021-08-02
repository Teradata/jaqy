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
package com.teradata.jaqy.utils;

import java.io.PrintWriter;

/**
 * @author  Heng Yuan
 */
public class AttributeUtils
{
    private static String INDENT = "  ";
    private static int NAME_SIZE = 45;

    public static void print (PrintWriter pw, int indentLevel, String name, String property)
    {
        if (property == null)
            property = "";
        for (int i = 0; i < indentLevel; ++i)
            pw.print (INDENT);
        if (name.length () > NAME_SIZE)
            name = name.substring (0, NAME_SIZE);
        pw.print (name);
        int len = name.length ();
        if (len > 0)
        {
            char[] spaces = new char[NAME_SIZE - len];
            for (int i = 0; i < spaces.length; ++i)
                spaces[i] = ' ';
            pw.print (spaces);
        }
        pw.println (property);
    }

    public static void print (PrintWriter pw, int indentLevel, String name, String property, boolean notSupported)
    {
        if (notSupported)
            print (pw, indentLevel, name, "not supported");
        else
            print (pw, indentLevel, name, property);
    }

    public static void print (PrintWriter pw, int indentLevel, String name, boolean b)
    {
        print (pw, indentLevel, name, b ? "Y" : "N");
    }

    public static void print (PrintWriter pw, int indentLevel, String name, boolean b, boolean notSupported)
    {
        if (notSupported)
            print (pw, indentLevel, name, "not supported");
        else
            print (pw, indentLevel, name, b ? "Y" : "N");
    }

    public static void print (PrintWriter pw, int indentLevel, String name, int value)
    {
        print (pw, indentLevel, name, Integer.toString (value));
    }

    public static void print (PrintWriter pw, int indentLevel, String name, int value, boolean notSupported)
    {
        if (notSupported)
            print (pw, indentLevel, name, "not supported");
        else
            print (pw, indentLevel, name, Integer.toString (value));
    }
}

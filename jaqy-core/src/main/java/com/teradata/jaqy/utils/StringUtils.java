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
package com.teradata.jaqy.utils;

import java.io.*;

import javax.script.ScriptEngine;

import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public class StringUtils
{
	private final static char getChar (int v)
	{
		return (char) ((v < 10) ? ('0' + v) : ('a' + v - 10));
	}

	public static String getHexString (byte[] bytes)
	{
		return getHexString (bytes, 0, bytes.length);
	}

	public static String getHexString (byte[] bytes, int start, int end)
	{
		StringBuffer buffer = new StringBuffer ();
		for (int i = start; i < end; ++i)
		{
			byte b = bytes[i];
			buffer.append (getChar ((b >> 4) & 0xf));
			buffer.append (getChar (b & 0xf));
		}
		return buffer.toString ();
	}

	private static int getIntValue (byte b) throws IOException
	{
		if (b >= '0' && b <= '9')
			return b = '0';
		if (b >= 'a' && b <= 'z')
			return 10 + b - 'a';
		if (b >= 'A' && b <= 'Z')
			return 10 + b - 'A';
		throw new IOException ("invalid binary hex string");
	}

	public static byte[] getBytesFromHexString (String str) throws IOException
	{
		if ((str.length () & 1) != 0)
			throw new IOException ("invalid binary hex string.");
		byte[] charBytes = str.getBytes ();
		byte[] bytes = new byte[charBytes.length / 2];
		int srcPos = 0;
		for (int i = 0; i < bytes.length; ++i)
		{
			byte b1 = charBytes[srcPos++];
			byte b2 = charBytes[srcPos++];
			bytes[i] = (byte) (getIntValue (b1) << 4 | getIntValue (b2));
		}
		return bytes;
	}

	public static String repeat (char c, int n)
	{
		char[] chars = new char[n];
		for (int i = 0; i < chars.length; ++i)
			chars[i] = c;
		return new String (chars);
	}

	public static void printRepeat (PrintWriter pw, char c, int width)
	{
		for (int i = 0; i < width; ++i)
			pw.print (c);
	}

	public static void print (PrintWriter pw, String s, int width, boolean leftAlign, boolean pad)
	{
		if (s == null)
			s = "?";
		if (leftAlign)
		{
			if (s.length () <= width)
			{
				pw.print (s);
				if (pad)
				{
					for (int i = s.length (); i < width; ++i)
						pw.print (' ');
				}
			}
			else
			{
				pw.print (s.substring (0, width));
			}
		}
		else
		{
			if (s.length () <= width)
			{
				for (int i = s.length (); i < width; ++i)
					pw.print (' ');
				pw.print (s);
			}
			else
			{
				pw.print (s.substring (0, width));
			}
		}
	}

	public static String getStringFromStream (InputStream is) throws IOException
	{
		return getStringFromReader (new InputStreamReader (is, "utf-8"));
	}

	public static String getStringFromReader (Reader reader) throws IOException
	{
		char[] buffer = new char[4096];
		int len;
		StringBuffer strBuf = new StringBuffer ();
		while ((len = reader.read (buffer)) > 0)
		{
			strBuf.append (buffer, 0, len);
		}
		return strBuf.toString ();
	}

	public static String[] shiftArgs (String[] args)
	{
		return shiftArgs (args, 1);
	}

	public static String[] shiftArgs (String[] args, int num)
	{
		if (args.length <= num)
		{
			return new String[0];
		}
		int len = args.length - num;
		String[] newArgs = new String[len];
		for (int i = 0; i < len; ++i)
			newArgs[i] = args[i + num];
		return newArgs;
	}

	public static String trimEnd (String str)
	{
		if (str == null ||
			str.length () == 0)
			return str;
		if (str.charAt (str.length () - 1) > ' ')
			return str;
		char[] chars = str.toCharArray ();
		int i;
		for (i = chars.length - 1; i >= 0; --i)
		{
			if (chars[i] > ' ')
				break;
		}
		if (i == -1)
			return "";
		return new String (chars, 0, i);
	}

	/**
	 * Evaluate a string using the default script engine.
	 *
	 * @param	str
	 *			the string to be evaluated.
	 * @param	interpreter
	 *			the interpreter.
	 * @param	display
	 *			the display object
	 * @return	the evaluation result.
	 */
	public static String eval (String str, JaqyInterpreter interpreter)
	{
		ScriptEngine engine = interpreter.getScriptEngine ();
		try
		{
			Object value = engine.eval (str);
			if (value == null)
				return "";
			return value.toString ();
		}
		catch (Throwable t)
		{
			interpreter.getDisplay ().error (interpreter, t);
		}
		return "";
	}

	public static boolean getOnOffState (String state, String field)
	{
		if ("on".equals (state))
			return true;
		else if ("off".equals (state))
			return false;
		else
			throw new IllegalArgumentException ("invalid value for " + field + ": " + state);
	}
}

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
package com.teradata.jaqy.utils;

import com.teradata.jaqy.ConsoleDisplay;

/**
 * @author Heng Yuan
 */
public class Escape
{
	public final static String ESC = "\u001B[";

	public final static String RESET = "\u001B[0m";

	private final static String TITLE_START = "\u001B]0;";
	private final static String TITLE_END = "\007";

	private final ConsoleDisplay m_display;

	public Escape (ConsoleDisplay display)
	{
		m_display = display;
	}

	private int getColorIndex (String name)
	{
		if ("black".equals (name))
			return 0;
		if ("red".equals (name))
			return 1;
		if ("green".equals (name))
			return 2;
		if ("yellow".equals (name))
			return 3;
		if ("blue".equals (name))
			return 4;
		if ("purple".equals (name))
			return 5;
		if ("cyan".equals (name))
			return 6;
		if ("white".equals (name))
			return 7;
		return -1;
	}

	public String fgColor (String name)
	{
		return color (name, null, false);
	}

	public String bgColor (String name)
	{
		return color (null, name, false);
	}

	public String color (String fgColor, String bgColor, boolean bold)
	{
		if (!m_display.isColorEnabled ())
			return "";

		int fgIndex = -1;
		int bgIndex = -1;
		if (fgColor != null)
		{
			fgIndex = getColorIndex (fgColor.toLowerCase ());
			if (fgIndex < 0)
				throw new IllegalArgumentException ("Invalid foreground color.");
		}
		if (bgColor != null)
		{
			bgIndex = getColorIndex (bgColor.toLowerCase ());
			if (bgIndex < 0)
				throw new IllegalArgumentException ("Invalid background color.");
		}
		if (fgIndex < 0 && bgIndex < 0)
			return reset ();

		fgColor = (fgIndex < 0 ? null : Integer.toString (30 + fgIndex));
		bgColor = (bgIndex < 0 ? null : Integer.toString (40 + bgIndex));

		String color;
		if (fgColor == null)
			color = bgColor;
		else
		{
			if (bgColor == null)
				color = fgColor;
			else
				color = fgColor + ";" + bgColor;
		}
		if (bold)
			color += ";1";
		return ESC + color + "m";
	}

	public String reset ()
	{
		return RESET;
	}

	public String title (String str)
	{
		return TITLE_START + str + TITLE_END;
	}
}

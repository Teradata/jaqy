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
package com.teradata.jaqy.lineinput;

import java.io.IOException;

import com.teradata.jaqy.interfaces.LineInput;

import jline.console.ConsoleReader;

/**
 * @author	Heng Yuan
 */
public class JLineConsoleLineInput implements LineInput
{
	private ConsoleReader m_jline;

	public JLineConsoleLineInput () throws IOException
	{
		m_jline = new ConsoleReader ();
	}

	public String getLine ()
	{
		try
		{
			return m_jline.readLine ();
		}
		catch (IOException ex)
		{
			return null;
		}
	}

	@Override
	public boolean isInteractive ()
	{
		return true;
	}
}

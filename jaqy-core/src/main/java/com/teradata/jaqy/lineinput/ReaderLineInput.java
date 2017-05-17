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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.teradata.jaqy.interfaces.LineInput;

/**
 * @author	Heng Yuan
 */
public class ReaderLineInput implements LineInput
{
	private BufferedReader m_reader;
	private final boolean m_interactive;

	public ReaderLineInput (Reader reader, boolean interactive) throws IOException
	{
		m_reader = new BufferedReader (reader);
		m_interactive = interactive;
	}

	public String getLine ()
	{
		if (m_reader == null)
			return null;

		try
		{
			return m_reader.readLine ();
		}
		catch (IOException ex)
		{
			try
			{
				m_reader.close ();
			}
			catch (IOException ex2)
			{
			}
			m_reader = null;
			return null;
		}
	}

	@Override
	public boolean isInteractive ()
	{
		return m_interactive;
	}
}

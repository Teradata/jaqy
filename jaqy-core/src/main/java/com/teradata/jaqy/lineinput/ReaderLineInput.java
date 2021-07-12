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
package com.teradata.jaqy.lineinput;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePath;

/**
 * @author	Heng Yuan
 */
public class ReaderLineInput implements LineInput
{
	private BufferedReader m_reader;
	private final Path m_dir;
	private final boolean m_interactive;

	public ReaderLineInput (Reader reader, Path dir, boolean interactive) throws IOException
	{
		m_reader = new BufferedReader (reader);
		m_dir = dir;
		m_interactive = interactive;
	}

	@Override
	public boolean getLine (Input input)
	{
		if (m_reader == null)
			return false;

		try
		{
			input.interactive = m_interactive;
			input.line = m_reader.readLine ();
			if (input.line == null)
			{
				m_reader.close ();
				m_reader = null;
			}
			return input.line != null;
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
			return false;
		}
	}

	@Override
	public Path getDirectory ()
	{
		return m_dir;
	}

	@Override
	public File getFileDirectory ()
	{
		if (m_dir instanceof FilePath)
			return ((FilePath)m_dir).getFile ();
		return null;
	}
}

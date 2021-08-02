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
package com.teradata.jaqy.lineinput;

import java.io.File;
import java.util.LinkedList;

import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePath;

/**
 * @author	Heng Yuan
 */
public class CommandLineInput implements LineInput
{
	private static class StringComposer
	{
		private final StringBuilder m_builder = new StringBuilder ();
		private boolean m_padSpace;

		public void add (String arg)
		{
			if (m_padSpace)
				m_builder.append (' ');
			else
				m_padSpace = true;
			m_builder.append (arg);
		}

		public String toString ()
		{
			return m_builder.toString ();
		}
	}

	private final LinkedList<String> m_lines = new LinkedList<String> ();
	private final Path m_dir;

	public CommandLineInput (String[] args, Path dir)
	{
		m_dir = dir;
		StringComposer comp = new StringComposer ();

		for (String arg : args)
		{
			if (";".equals (arg))
			{
				m_lines.add (comp.toString ());
				comp = new StringComposer ();
			}
			else
			{
				comp.add (arg);
			}
		}
		m_lines.add (comp.toString ());
	}

	@Override
	public boolean getLine (Input input)
	{
		if (m_lines.size () > 0)
		{
			input.line = m_lines.removeFirst ();
			input.interactive = false;
			return true;
		}
		return false;
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

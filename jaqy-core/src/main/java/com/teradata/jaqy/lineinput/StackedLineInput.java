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

import java.io.File;
import java.util.Stack;

import com.teradata.jaqy.interfaces.LineInput;

/**
 * @author	Heng Yuan
 */
public class StackedLineInput implements LineInput
{
	private final Object m_lock = new Object ();
	private final Stack<LineInput> m_inputs = new Stack<LineInput> ();
	private LineInput m_input;

	public StackedLineInput ()
	{
	}

	public void push (LineInput input)
	{
		synchronized (m_lock)
		{
			if (m_input == null)
			{
				m_input = input;
			}
			else
			{
				m_inputs.push (m_input);
				m_input = input;
			}
		}
	}

	private LineInput getInput ()
	{
		synchronized (m_lock)
		{
			if (m_input != null)
				return m_input;
			if (m_inputs.isEmpty ())
				return null;
			LineInput input = m_inputs.pop ();
			m_input = input;
			return input;
		}
	}

	private LineInput pop ()
	{
		synchronized (m_lock)
		{
			if (m_input == null)
				return null;
			if (m_inputs.isEmpty ())
			{
				m_input = null;
				return null;
			}
			LineInput input = m_inputs.pop ();
			m_input = input;
			return input;
		}
	}

	@Override
	public boolean getLine (Input input)
	{
		LineInput lineInput = getInput ();

		while (lineInput != null &&
			   !lineInput.getLine (input))
		{
			lineInput = pop ();
		}
		return lineInput != null;
	}

	@Override
	public File getDirectory ()
	{
		LineInput input = getInput ();
		if (input == null)
			return new File (".");
		return input.getDirectory ();
	}
}

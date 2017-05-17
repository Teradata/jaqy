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

import com.teradata.jaqy.interfaces.Display;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author	Heng Yuan
 */
@SuppressWarnings ("restriction")
public class ConsoleSignalHandler implements SignalHandler
{
	private final Display m_display;

	public ConsoleSignalHandler (Display display)
	{
		m_display = display;
	}

	public void register ()
	{
		Signal.handle (new Signal ("INT"), this);
	}

	@Override
	public void handle (Signal signal)
	{
		m_display.println (null, "Ctrl-C pressed...");
	}
}

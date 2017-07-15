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
package com.teradata.jaqy;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @author	Heng Yuan
 */
public class Os
{
	private final String m_os;
	private final boolean m_windows;

	public Os ()
	{
		m_os = System.getProperty ("os.name");
		m_windows = m_os.indexOf ("Win") >= 0 || m_os.indexOf ("win") >= 0;
	}

	public void shell (File dir, String cmd) throws Exception
	{
		CommandLine commandLine;
		if (m_windows)
		{
			commandLine = new CommandLine ("cmd");
			commandLine.addArgument ("/C");
			commandLine.addArgument (cmd, false);
		}
		else
		{
			commandLine = new CommandLine ("/bin/bash");
			commandLine.addArgument ("-c");
			commandLine.addArgument (cmd, false);
		}
		Executor executor = new DefaultExecutor ();
		executor.setWorkingDirectory (dir);
		executor.setStreamHandler (new PumpStreamHandler (System.out, System.out));
		executor.execute(commandLine);
	}

	public boolean isWindows ()
	{
		return m_windows;
	}

	public boolean isMac ()
	{
		return (m_os.indexOf ("mac") >= 0);
	}

	public boolean isUnix ()
	{
		return (m_os.indexOf ("nix") >= 0 || m_os.indexOf ("nux") >= 0 || m_os.indexOf ("aix") > 0);
	}

	public boolean isSolaris ()
	{
		return (m_os.indexOf ("sunos") >= 0);
	}

	public String getOS ()
	{
		return m_os;
	}
}

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
package com.teradata.jaqy;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @author  Heng Yuan
 */
public class Os
{
    private final static String s_os = System.getProperty ("os.name").toLowerCase ();
    private final static boolean s_windows = s_os.indexOf ("win") >= 0;

    Os ()
    {
    }

    public void shell (File dir, String cmd) throws Exception
    {
        CommandLine commandLine;
        if (s_windows)
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

    public static boolean isWindows ()
    {
        return s_windows;
    }
}

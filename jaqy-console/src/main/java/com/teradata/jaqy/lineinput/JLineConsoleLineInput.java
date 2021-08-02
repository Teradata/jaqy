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
import java.io.IOException;

import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePath;

import jline.console.ConsoleReader;

/**
 * @author  Heng Yuan
 */
public class JLineConsoleLineInput implements LineInput
{
    private ConsoleReader m_jline;
    private final Path m_dir;

    public JLineConsoleLineInput (Path dir) throws IOException
    {
        m_dir = dir;
        m_jline = new ConsoleReader ();
        m_jline.setExpandEvents (false);
    }

    @Override
    public boolean getLine (Input input)
    {
        try
        {
            input.interactive = true;
            input.line = m_jline.readLine ();
            return true;
        }
        catch (IOException ex)
        {
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

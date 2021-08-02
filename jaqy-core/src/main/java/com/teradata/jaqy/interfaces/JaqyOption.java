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
package com.teradata.jaqy.interfaces;

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.Globals;

/**
 * A command line option class.
 *
 * @author  Heng Yuan
 */
public abstract class JaqyOption
{
    private String m_option;
    private String m_longOption;
    private boolean m_hasArg;
    private String m_description;
    private String m_argName;

    public JaqyOption (String option, String longOption, boolean hasArg, String description)
    {
        m_option = option;
        m_longOption = longOption;
        m_hasArg = hasArg;
        m_description = description;
    }

    public String getOption ()
    {
        return m_option;
    }

    public String getLongOption ()
    {
        return m_longOption;
    }

    public boolean isHasArg ()
    {
        return m_hasArg;
    }

    public String getDescription ()
    {
        return m_description;
    }

    public String getArgName ()
    {
        return m_argName;
    }

    public void setArgName (String argName)
    {
        m_argName = argName;
    }

    public abstract void handleOption (Globals globals, Display display, CommandLine cmdLine) throws Exception;
}

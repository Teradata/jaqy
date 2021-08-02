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

import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyOption;
import com.teradata.jaqy.utils.OptionsUtils;

/**
 * @author  Heng Yuan
 */
public class OptionManager
{
    private final Options m_options = new Options ();
    private final HashMap<String, JaqyOption> m_optionHandlers = new HashMap<String, JaqyOption> ();

    OptionManager ()
    {
    }

    public CommandLine getCommandLine (String[] args) throws Exception
    {
        return new DefaultParser ().parse (m_options, args);
    }

    public void addOption (JaqyOption option)
    {
        Option o = new Option (option.getOption (),
                               option.getLongOption (),
                               option.isHasArg (),
                               option.getDescription ());
        o.setArgName (option.getArgName ());
        m_options.addOption (o);
        m_optionHandlers.put (option.getLongOption (), option);
    }

    public String[] handleOptions (Globals globals, Display display, String[] args) throws Exception
    {
        CommandLine cmdLine = null;
        try
        {
            cmdLine = new DefaultParser ().parse (m_options, args);
        }
        catch (Exception ex)
        {
            display.println (null, ex.getMessage ());
            System.exit (1);
        }

        for (Option option : cmdLine.getOptions ())
        {
            String longOpt = option.getLongOpt ();
            JaqyOption handler = m_optionHandlers.get (longOpt);
            handler.handleOption (globals, display, cmdLine);
        }
        return cmdLine.getArgs ();
    }

    public void printHelp (PrintWriter pw)
    {
        String syntax = "java -jar jaqy.jar [options] [commands]";
        String header = "options:";
        String footer = "commands:\n  a series of commands separated by ;";
        OptionsUtils.printHelp (pw, m_options, syntax, header, footer);
    }
}

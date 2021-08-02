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
package com.teradata.jaqy.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyHandlerFactory;

/**
 * @author  Heng Yuan
 */
public abstract class JaqyHandlerFactoryImpl<E> implements JaqyHandlerFactory<E>
{
    private final Options m_options = new Options ();

    public JaqyHandlerFactoryImpl ()
    {
    }

    protected void addOption(String opt, String longOpt, boolean hasArg, String description)
    {
        m_options.addOption (opt, longOpt, hasArg, description);
    }

    protected void addOption(Option option)
    {
        m_options.addOption (option);
    }

    @Override
    public String getLongDescription ()
    {
        StringWriter sw = new StringWriter ();
        PrintWriter pw = new PrintWriter (sw);
        OptionsUtils.printHelpNoUsage (pw, m_options, getName () + " type options:", null, null);
        pw.close ();
        return sw.toString ();
    }

    @Override
    public E getHandler (String[] args, JaqyInterpreter interpreter) throws Exception
    {
        return getHandler (new DefaultParser ().parse (m_options, args), interpreter);
    }

    abstract protected E getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception;
}

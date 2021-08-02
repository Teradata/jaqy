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
package com.teradata.jaqy.command;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.lineinput.LineInputFactory;

/**
 * @author  Heng Yuan
 */
public class RunCommand extends JaqyCommandAdapter
{
    public RunCommand ()
    {
        super ("run");

        addOption ("c", "charset", true, "sets the file character set");
    }

    @Override
    protected String getSyntax ()
    {
        return ".run [options] [path]";
    }

    @Override
    public String getDescription ()
    {
        return "runs a Jaqy script.";
    }

    @Override
    public CommandArgumentType getArgumentType ()
    {
        return CommandArgumentType.file;
    }

    @Override
    public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
    {
        CommandLine cmdLine = getCommandLine (args);

        String encoding = cmdLine.getOptionValue ('c');
        args = cmdLine.getArgs ();
        if (args.length == 0)
        {
            interpreter.error ("missing file name.");
        }
        LineInput input = null;
        try
        {
            input = LineInputFactory.getLineInput (interpreter.getPath (args[0]), encoding, false);
        }
        catch (IOException e)
        {
            interpreter.error ("invalid file: " + args[0]);
        }
        if (!silent)
            interpreter.println ("-- Running script: " + args[0]);
        interpreter.push (input);
    }
}

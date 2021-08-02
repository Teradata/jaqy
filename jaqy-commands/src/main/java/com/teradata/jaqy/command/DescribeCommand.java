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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author  Heng Yuan
 */
public class DescribeCommand extends JaqyCommandAdapter
{
    public DescribeCommand ()
    {
        super ("desc");

        addOption ("s", "sql", false, "display schema in SQL");
    }

    @Override
    public String getDescription ()
    {
        return "describes a table schema.";
    }

    @Override
    protected String getSyntax ()
    {
        return "usage: " + getCommand () + " [options] [table name]";
    }

    @Override
    public CommandArgumentType getArgumentType ()
    {
        return CommandArgumentType.sql;
    }

    @Override
    public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
    {
        SessionUtils.checkOpen (interpreter);
        CommandLine cmdLine = getCommandLine (args);
        args = cmdLine.getArgs ();
        if (args.length == 0)
        {
            interpreter.error ("Missing table name.");
        }
        boolean displaySQL = false;
        for (Option option : cmdLine.getOptions ())
        {
            switch (option.getOpt ().charAt (0))
            {
                case 's':
                {
                    displaySQL = true;
                    break;
                }
            }
        }
        String tableName = args[0];
        for (int i = 1; i < args.length; ++i)
            tableName += args[i];
        if (displaySQL)
        {
            String schema = interpreter.getSession ().getConnection ().getHelper ().getTableSchema (tableName, interpreter);
            interpreter.println (schema);
        }
        else
        {
            JaqyResultSet rs = interpreter.getSession ().getConnection ().getHelper ().getTableColumns (tableName, interpreter);
            interpreter.print (rs);
            rs.close ();
        }
    }
}

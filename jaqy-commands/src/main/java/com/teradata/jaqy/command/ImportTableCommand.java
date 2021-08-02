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

import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.QueryMode;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.schema.SchemaUtils;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author  Heng Yuan
 */
public class ImportTableCommand extends JaqyCommandAdapter
{
    public ImportTableCommand ()
    {
        super ("importtable");

        addOption ("c", "check", false, "check if the table already exists");
    }

    @Override
    public String getDescription ()
    {
        return "creates a staging table and imports data into it.";
    }

    @Override
    protected String getSyntax ()
    {
        return getCommand () + " [options] [tablename]";
    }

    @Override
    public CommandArgumentType getArgumentType ()
    {
        return CommandArgumentType.sql;
    }

    @Override
    public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
    {
        CommandLine cmdLine = getCommandLine (args);
        args = cmdLine.getArgs ();
        boolean checkExist = false;

        for (Option option : cmdLine.getOptions ())
        {
            switch (option.getOpt ().charAt (0))
            {
                case 'c':
                {
                    checkExist = true;
                    break;
                }
            }
        }

        if (args.length == 0)
        {
            interpreter.error ("Staging table name is not specified.");
        }
        String tableName = args[0];

        SessionUtils.checkOpen (interpreter);
        Session session = interpreter.getSession ();
        JaqyConnection conn = session.getConnection ();
        JaqyHelper helper = conn.getHelper ();

        boolean tableExists = false;
        if (checkExist)
        {
            tableExists = helper.checkTableExists (tableName, interpreter);
        }
        int columnCount = 0;

        JaqyImporter importer = interpreter.getImporter ();
        if (importer == null)
        {
            interpreter.error ("There is no current import.");
        }
        if (!tableExists)
        {
            SchemaInfo schemaInfo = importer.getSchema ();
            if (schemaInfo == null)
            {
                interpreter.error ("Current import schema is not available.");
            }

            String sql = SchemaUtils.getTableSchema (helper, schemaInfo, tableName, false, true);

            boolean prevCommit = conn.getAutoCommit ();
            if (!prevCommit)
                conn.setAutoCommit (true);

            interpreter.println ("-- Table Schema --");
            interpreter.println (sql);
            session.executeQuery (sql, interpreter, 1);

            if (!prevCommit)
                conn.setAutoCommit (false);

            columnCount = schemaInfo.columns.length;
        }
        else
        {
            try
            {
                columnCount = helper.getNumColumns (tableName, interpreter);
            }
            catch (Exception ex)
            {
                interpreter.getGlobals ().log (Level.INFO, ex);
            }
        }

        if (columnCount < 1)
        {
            interpreter.error ("Error determining the number of import column.");
        }

        StringBuilder buffer = new StringBuilder ();
        buffer.append ("INSERT INTO ").append (tableName).append (" VALUES (");
        for (int i = 0; i < columnCount; ++i)
        {
            if (i > 0)
                buffer.append (',');
            buffer.append ('?');
        }
        buffer.append (')');
        String sql = buffer.toString ();
        interpreter.println ("-- INSERTION --");
        interpreter.println (sql);
        try
        {
            session.importQuery (sql, interpreter);
        }
        finally
        {
            interpreter.setQueryMode (QueryMode.Regular);
        }
    }
}

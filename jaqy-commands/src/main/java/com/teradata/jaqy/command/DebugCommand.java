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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.DebugManager;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author  Heng Yuan
 */
public class DebugCommand extends JaqyCommandAdapter
{
    /**
     * A a debug option handler interface.
     */
    public static interface DebugOptionHandler
    {
        public String getName ();
        public String getSyntax ();
        public void handleOption (Globals globals, JaqyInterpreter interpreter, String[] args);
        public String getOption (Globals globals, JaqyInterpreter interpreter);
    }

    private HashMap<String, DebugOptionHandler> m_optionMap = new HashMap<String, DebugOptionHandler> ();

    public DebugCommand ()
    {
        super ("debug");

        addOption (new DebugOptionHandler ()
        {
            @Override
            public String getName ()
            {
                return "resultset";
            }

            @Override
            public String getSyntax ()
            {
                return getName () + " [on | off]";
            }

            @Override
            public void handleOption (Globals globals, JaqyInterpreter interpreter, String[] args)
            {
                DebugManager debug = globals.getDebugManager ();
                debug.setDumpResultSet (StringUtils.getOnOffState (args[0], getName ()));
            }

            @Override
            public String getOption (Globals globals, JaqyInterpreter interpreter)
            {
                DebugManager debug = globals.getDebugManager ();
                if (!debug.isDumpResultSet ())
                    return null;
                return getCommand () + " " + getName () + " on";
            }
        });

        addOption (new DebugOptionHandler ()
        {
            @Override
            public String getName ()
            {
                return "preparedstatement";
            }

            @Override
            public String getSyntax ()
            {
                return getName () + " [on | off]";
            }

            @Override
            public void handleOption (Globals globals, JaqyInterpreter interpreter, String[] args)
            {
                DebugManager debug = globals.getDebugManager ();
                debug.setDumpPreparedStatement (StringUtils.getOnOffState (args[0], getName ()));
            }

            @Override
            public String getOption (Globals globals, JaqyInterpreter interpreter)
            {
                DebugManager debug = globals.getDebugManager ();
                if (!debug.isDumpPreparedStatement ())
                    return null;
                return getCommand () + " " + getName () + " on";
            }
        });

        addOption (new DebugOptionHandler ()
        {
            @Override
            public String getName ()
            {
                return "log";
            }

            @Override
            public String getSyntax ()
            {
                return getName () + " [info | warning | all | off]";
            }

            @Override
            public void handleOption (Globals globals, JaqyInterpreter interpreter, String[] args)
            {
                globals.setLevel (args[0]);
            }

            @Override
            public String getOption (Globals globals, JaqyInterpreter interpreter)
            {
                if ("off".equals (globals.getLevel ()))
                    return null;
                return getCommand () + " " + getName () + " " + globals.getLevel ();
            }
        });
    }

    @Override
    public String getDescription ()
    {
        return "debug dump.";
    }

    @Override
    public CommandArgumentType getArgumentType ()
    {
        return CommandArgumentType.file;
    }

    @Override
    public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter)
    {
        Globals globals = interpreter.getGlobals ();
        if (args.length == 0)
        {
            interpreter.println (getCurrentOptions (globals, interpreter));
            return;
        }

        if (args.length < 2)
        {
            interpreter.error ("error parsing argument.");
        }

        DebugOptionHandler handler = m_optionMap.get (args[0]);
        if (handler == null)
        {
            interpreter.error ("Unknown debug option: " + args[0]);
        }
        handler.handleOption (globals, interpreter, StringUtils.shiftArgs (args));
    }

    @Override
    public String getLongDescription ()
    {
        TreeMap<String, DebugOptionHandler> map = new TreeMap<String, DebugOptionHandler> ();
        synchronized (m_optionMap)
        {
            map.putAll (m_optionMap);
        }
        StringBuilder builder = new StringBuilder ();
        builder.append ("usage:\n");
        for (Map.Entry<String, DebugOptionHandler> entry : map.entrySet ())
        {
            builder.append ("    ").append (getCommand ()).append (' ').append (entry.getValue ().getSyntax ()).append ('\n');
        }
        return builder.toString ();
    }

    /**
     * Gets the current active debug options
     */
    private String getCurrentOptions (Globals globals, JaqyInterpreter interpreter)
    {
        TreeMap<String, DebugOptionHandler> map = new TreeMap<String, DebugOptionHandler> ();
        synchronized (m_optionMap)
        {
            map.putAll (m_optionMap);
        }
        StringBuilder builder = new StringBuilder ();
        for (Map.Entry<String, DebugOptionHandler> entry : map.entrySet ())
        {
            String value = entry.getValue ().getOption (globals, interpreter);
            if (value == null)
                continue;
            if (builder.length () > 0)
                builder.append ('\n');
            builder.append (value);
        }
        return builder.toString ();
    }

    /**
     * Add a new debug option
     *
     * @param   option
     *          a DebugOption handler
     */
    public void addOption (DebugOptionHandler option)
    {
        m_optionMap.put (option.getName (), option);
    }
}

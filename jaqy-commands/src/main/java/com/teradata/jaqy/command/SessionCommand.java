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

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;

/**
 * @author  Heng Yuan
 */
public class SessionCommand extends JaqyCommandAdapter
{
    public SessionCommand ()
    {
        super ("session", "session.txt");
    }

    @Override
    public String getDescription ()
    {
        return "session related controls.";
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
            // list the sessions
            for (Session sess : globals.getSessions ())
            {
                interpreter.println (sess.getDescription ());
            }
        }
        else if ("new".equals (args[0]))
        {
            // create a new session and set the session number to it.
            Session sess = globals.createSession (interpreter.getDisplay ());
            interpreter.setSession (sess);
        }
        else
        {
            int sessionNumber = 0;
            try
            {
                sessionNumber = Integer.parseInt (args[0]);
            }
            catch (Exception ex)
            {
                interpreter.error ("invalid session id.");
            }

            Session sess = globals.getSession (sessionNumber);
            if (sess == null)
            {
                interpreter.error ("invalid session id.");
            }

            interpreter.setSession (sess);
        }
    }
}

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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;

/**
 * @author  Heng Yuan
 */
public class RemarkCommand extends JaqyCommandAdapter
{
    public RemarkCommand ()
    {
        super ("rem");
    }

    @Override
    public String getDescription ()
    {
        return "does block comment.";
    }

    @Override
    public JaqyCommand.Type getType ()
    {
        return JaqyCommand.Type.exclusive;
    }

    @Override
    public boolean isMultiLine (String[] args)
    {
        return true;
    }

    @Override
    public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter)
    {
        interpreter.setParseAction (this, null);
    }

    @Override
    public void parse (String action, Object value, boolean silent, boolean interactive, Globals globals, JaqyInterpreter interpreter) throws IOException
    {
        if (!silent)
        {
            Display display = interpreter.getDisplay ();
            display.echo (interpreter, action, interactive);
            display.echo (interpreter, ".end " + getName (), interactive);
        }
    }
}

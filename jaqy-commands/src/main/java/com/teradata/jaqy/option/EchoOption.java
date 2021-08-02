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
package com.teradata.jaqy.option;

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.Echo;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyOption;

/**
 * @author  Heng Yuan
 */
public class EchoOption extends JaqyOption
{
    public EchoOption ()
    {
        super ("e", "echo", true, "turns echo on / off / auto");
        setArgName ("on | off | auto");
    }

    @Override
    public void handleOption (Globals globals, Display display, CommandLine cmdLine)
    {
        String value = cmdLine.getOptionValue ("echo");
        if ("auto".equals (value))
            display.setEcho (Echo.auto);
        else if ("on".equals (value))
            display.setEcho (Echo.on);
        else if ("off".equals (value))
            display.setEcho (Echo.off);
        else
        {
            display.println (null, "invalid option value for --echo");
            System.exit (1);
        }
    }
}

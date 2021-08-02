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
package com.teradata.jaqy.exporter;

import java.io.OutputStream;

import org.apache.avro.file.CodecFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author  Heng Yuan
 */
public class AvroExporterFactory extends JaqyHandlerFactoryImpl<JaqyExporter>
{
    public AvroExporterFactory ()
    {
        addOption ("c", "compression", true, "sets the compression codec");
    }

    @Override
    public String getName ()
    {
        return "avro";
    }

    @Override
    public JaqyExporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
    {
        CodecFactory codecFactory = null;
        for (Option option : cmdLine.getOptions ())
        {
            switch (option.getOpt ().charAt (0))
            {
                case 'c':
                {
                    String value = option.getValue ();
                    codecFactory = CodecFactory.fromString (value);
                    break;
                }
            }
        }

        String[] args = cmdLine.getArgs ();
        if (args.length == 0)
            throw new IllegalArgumentException ("missing file name.");
        OutputStream os = interpreter.getPath (args[0]).getOutputStream ();

        return new AvroExporter (os, codecFactory);
    }
}

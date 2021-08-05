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

import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.CSVExportInfo;
import com.teradata.jaqy.utils.CSVNameGen;
import com.teradata.jaqy.utils.CSVUtils;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author  Heng Yuan
 */
public class CSVExporterFactory extends JaqyHandlerFactoryImpl<JaqyExporter>
{
    public static String DEFAULT_NAME_PATTERN = "%08d.bin";

    public CSVExporterFactory ()
    {
        addOption ("c", "charset", true, "sets the file character set");
        addOption ("d", "delimiter", true, "specifies the delimiter");
        Option option = new Option ("t", "type", true, "sets the csv type.");
        option.setArgName ("default | excel | rfc4180 | mysql | tdf");
        addOption (option);
        addOption ("n", "name", true, "specifies the external file name pattern");
        addOption ("e", "encoding", true, "specifies the external file character set");
        addOption ("f", "file", true, "specifies the external file column");
    }

    @Override
    public String getName ()
    {
        return "csv";
    }

    @Override
    public JaqyExporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
    {
        CSVExporterOptions exportOptions = new CSVExporterOptions ();
        CSVNameGen nameGen = new CSVNameGen (DEFAULT_NAME_PATTERN);
        Charset encoding = CSVUtils.DEFAULT_CHARSET;

        for (Option option : cmdLine.getOptions ())
        {
            switch (option.getOpt ().charAt (0))
            {
                case 'c':
                {
                    exportOptions.charset = Charset.forName (option.getValue ());
                    break;
                }
                case 'd':
                {
                    char delimiter = CSVUtils.getChar (option.getValue ());
                    if (delimiter == 0)
                        throw new IllegalArgumentException ("invalid delimiter: " + option.getValue ());
                    exportOptions.format = exportOptions.format.builder().setDelimiter (delimiter).build ();
                    break;
                }
                case 't':
                {
                    exportOptions.format = CSVUtils.getFormat (option.getValue ());
                    break;
                }
                case 'n':
                {
                    String fmt = option.getValue ();
                    nameGen = new CSVNameGen (fmt);
                    // now check if the name is a valid format.
                    if (fmt.equals (nameGen.getName (1)))
                        interpreter.error ("Invalid name pattern: " + fmt);
                    break;
                }
                case 'e':
                {
                    encoding = Charset.forName (option.getValue ());
                    break;
                }
                case 'f':
                {
                    int column = Integer.parseInt (option.getValue ());
                    if (column < 1)
                    {
                        interpreter.error ("Column index cannot be smaller than 1.");
                    }
                    CSVExportInfo info = new CSVExportInfo (nameGen, encoding);
                    exportOptions.fileInfoMap.put (column, info);
                    break;
                }
            }
        }
        String[] args = cmdLine.getArgs ();
        if (args.length == 0)
            throw new IllegalArgumentException ("missing file name.");
        Path file = interpreter.getPath (args[0]);
        return new CSVExporter (file, exportOptions);
    }
}

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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;
import com.teradata.jaqy.utils.ParquetUtils;

/**
 * @author  Heng Yuan
 */
public class ParquetExporterFactory extends JaqyHandlerFactoryImpl<JaqyExporter>
{
    public ParquetExporterFactory ()
    {
        addOption ("c", "compression", true, "sets the compression codec");
        addOption ("b", "blocksize", true, "sets the row group / block size");
        addOption ("p", "pagesize", true, "sets the page size");
        addOption ("r", "rowcount", true, "sets the row count limit");
        addOption ("d", "padding", true, "sets the maximum padding size");
    }

    @Override
    public String getName ()
    {
        return "pq";
    }

    @Override
    public JaqyExporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
    {
        ParquetExporterOptions options = new ParquetExporterOptions ();
        boolean specifiedCodec = false;

        for (Option option : cmdLine.getOptions ())
        {
            switch (option.getOpt ().charAt (0))
            {
                case 'c':
                {
                    options.codec = ParquetUtils.getCompressionCodec (option.getValue ());
                    specifiedCodec = true;
                    break;
                }
                case 'b':
                {
                    String value = option.getValue ();
                    options.blockSize = (int)ParquetUtils.getByteSize (value);
                    if (options.blockSize < 1)
                    {
                        throw new JaqyException ("Invalid block size.");
                    }
                    break;
                }
                case 'p':
                {
                    String value = option.getValue ();
                    options.pageSize = (int)ParquetUtils.getByteSize (value);
                    if (options.pageSize < 1)
                    {
                        throw new JaqyException ("Invalid page size.");
                    }
                    break;
                }
                case 'r':
                {
                    String value = option.getValue ();
                    try
                    {
                        options.rowCount = Integer.parseInt (value);
                    }
                    catch (Exception ex)
                    {
                        options.rowCount = -1;
                    }
                    if (options.rowCount < 1)
                    {
                        throw new JaqyException ("Invalid row count size.");
                    }
                    break;
                }
                case 'd':
                {
                    String value = option.getValue ();
                    try
                    {
                        options.paddingSize = Integer.parseInt (value);
                    }
                    catch (Exception ex)
                    {
                        options.paddingSize = -1;
                    }
                    if (options.paddingSize < 1)
                    {
                        throw new JaqyException ("Invalid padding size.");
                    }
                    break;
                }
            }
        }

        String[] args = cmdLine.getArgs ();
        if (args.length == 0)
            throw new IllegalArgumentException ("missing file name.");

        String file = args[0];

        if (!specifiedCodec)
        {
            options.codec = ParquetUtils.getCompressionCodecFromExtension (file);
        }

        Path path = interpreter.getPath (file);

        return new ParquetExporter (path, options);
    }
}

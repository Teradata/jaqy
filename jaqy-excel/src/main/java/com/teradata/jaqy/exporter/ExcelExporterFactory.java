/*
 * Copyright (c) 2021 Teradata
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
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author	Heng Yuan
 */
public class ExcelExporterFactory extends JaqyHandlerFactoryImpl<JaqyExporter>
{
	public static String DEFAULT_NAME_PATTERN = "%08d.bin";
	public static Charset DEFAULT_CHARSET = Charset.forName ("utf-8");

	public ExcelExporterFactory ()
	{
		addOption ("n", "name", true, "specifies the worksheet name");
		addOption ("h", "horizon", false, "dumps the data horizontally");
	}

	@Override
	public String getName ()
	{
		return "excel";
	}

	@Override
	public JaqyExporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		ExcelOptions options = new ExcelOptions ();

		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'h':
				{
					options.horizon = true;
					break;
				}
				case 'n':
				{
					options.sheetName = option.getValue ();
					break;
				}
			}
		}
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		Path file = interpreter.getPath (args[0]);
		return new ExcelExporter (file, options, interpreter);
	}
}

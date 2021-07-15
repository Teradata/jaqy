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
package com.teradata.jaqy.importer;

import java.sql.Types;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author	Heng Yuan
 */
public class ExcelImporterFactory extends JaqyHandlerFactoryImpl<JaqyImporter>
{
	public ExcelImporterFactory ()
	{
		addOption ("n", "name", true, "specifies the worksheet to import from using name");
		addOption ("i", "index", true, "specifies the worksheet to import from using index");
		addOption ("h", "header", false, "indicates that there is a header row/column");
		addOption ("d", "date", true, "imports the specified the column as date");
		addOption ("t", "time", true, "imports the specified the column as time");
		addOption ("s", "timestamp", true, "imports the specified the column as timestamp");
	}

	@Override
	public String getName ()
	{
		return "excel";
	}

	@Override
	public JaqyImporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		ExcelImporterOptions options = new ExcelImporterOptions ();

		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'i':
				{
					options.sheetId = Integer.parseInt (option.getValue ());
					if (options.sheetId < 0)
					{
						throw new IllegalArgumentException ("Invalid sheet index: " + options.sheetId);
					}
					break;
				}
				case 'n':
				{
					options.sheetName = option.getValue ();
					break;
				}
				case 'h':
				{
					options.header = true;
					break;
				}
				case 'd':
				case 't':
				case 's':
				{
					int column = Integer.parseInt (option.getValue ());
					if (column < 0)
					{
						throw new IllegalArgumentException ("Invalid column index: " + column);
					}
					int type = Types.NULL;
					switch (option.getOpt ().charAt (0))
					{
						case 'd':
							type = Types.DATE;
							break;
						case 't':
							type = Types.TIME;
							break;
						case 's':
							type = Types.TIMESTAMP;
							break;
					}
					options.setType (column, type);
				}
			}
		}
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		if (options.sheetName != null && options.sheetId >= 0)
		{
			throw new IllegalArgumentException ("Both sheet name and index are specified.");
		}
		if (options.sheetName == null && options.sheetId < 0)
		{
			options.sheetId = 0;
		}
		Path file = interpreter.getPath (args[0]);
		return new ExcelImporter (file, options);
	}
}

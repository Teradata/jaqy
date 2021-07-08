/*
 * Copyright (c) 2017-2018 Teradata
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

import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.csv.CSVFormat;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.CSVImportInfo;
import com.teradata.jaqy.utils.CSVUtils;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author	Heng Yuan
 */
public class CSVImporterFactory extends JaqyHandlerFactoryImpl<CSVImporter>
{
	public static Charset DEFAULT_CHARSET = Charset.forName ("utf-8");

	public CSVImporterFactory ()
	{
		addOption ("c", "charset", true, "sets the file character set");
		addOption ("d", "delimiter", true, "specifies the delimiter");
		Option option = new Option ("t", "type", true, "sets the csv type.");
		option.setArgName ("default | excel | rfc4180 | mysql | tdf");
		addOption (option);
		addOption ("f", "nafilter", false, "enables N/A value filtering");
		addOption ("v", "navalues", true, "specifies a comma delimited list of N/A values.  If it is not specified and --nafilter is enabled, then the default list is used.");
		addOption ("p", "precise", false, "Obtain precise decimal points if possible.  This option is only meaningful in generating a table schema.  By default, floating values are treated as DOUBLE PRECISION.");
		addOption ("h", "header", false, "indicates the file has a header");
		addOption ("r", "rowthreshold", true, "sets row threshold in schema determination.");
		addOption ("e", "encoding", true, "specifies the external file character set");
		addOption ("j", "clob", true, "specifies the external text file column");
		addOption ("k", "blob", true, "specifies the external binary file column");
	}

	@Override
	public String getName ()
	{
		return "csv";
	}

	@Override
	public CSVImporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		String charset = null;
		CSVFormat format = CSVFormat.DEFAULT;
		HashMap<Integer, CSVImportInfo> importInfoMap = new HashMap<Integer, CSVImportInfo> ();
		boolean naFilter = false;
		String[] naValues = null;
		boolean precise = false;
		long scanThreshold = -1;	// -1 indicates internal algorithm
		Charset encoding = DEFAULT_CHARSET;

		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'c':
				{
					Charset cs = Charset.forName (option.getValue ());
					if (cs != null)
					{
						charset = cs.toString ();
					}
					break;
				}
				case 'd':
				{
					char delimiter = CSVUtils.getChar (option.getValue ());
					if (delimiter == 0)
						throw new IllegalArgumentException ("invalid delimiter: " + option.getValue ());
					format = format.withDelimiter (delimiter);
					break;
				}
				case 'h':
				{
					format = format.withHeader ();
					break;
				}
				case 't':
				{
					format = CSVUtils.getFormat (option.getValue ());
					break;
				}
				case 'f':
				{
					naFilter = true;
					break;
				}
				case 'v':
				{
					naValues = option.getValue ().split (",");
					naFilter = true;
					break;
				}
				case 'p':
				{
					precise = true;
					break;
				}
				case 'r':
				{
					scanThreshold = Long.parseLong (option.getValue ());
					if (scanThreshold <= 0)
						scanThreshold = 0;
					break;
				}
				case 'e':
				{
					encoding = Charset.forName (option.getValue ());
					break;
				}
				case 'j':
				{
					int column = Integer.parseInt (option.getValue ());
					if (column < 1)
					{
						interpreter.error ("Column index cannot be smaller than 1.");
					}
					CSVImportInfo info = new CSVImportInfo (encoding);
					importInfoMap.put (column - 1, info);
					break;
				}
				case 'k':
				{
					int column = Integer.parseInt (option.getValue ());
					if (column < 1)
					{
						interpreter.error ("Column index cannot be smaller than 1.");
					}
					CSVImportInfo info = new CSVImportInfo (null);
					importInfoMap.put (column - 1, info);
					break;
				}
			}
		}
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		CSVImporter importer = new CSVImporter (interpreter.getPath (args[0]), charset, format, importInfoMap, precise, scanThreshold);
		if (naFilter == true)
		{
			importer.setNaFilter (true);
			importer.setNaValues (naValues);
		}
		return importer;
	}
}

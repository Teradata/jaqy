/*
 * Copyright (c) 2017 Teradata
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.csv.CSVFormat;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.CSVUtils;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;
import com.teradata.jaqy.utils.StringUtils;

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

		option = new Option ("h", "header", true, "indicates the file has a header or not");
		option.setArgName ("on | off");
		addOption (option);
	}

	@Override
	public String getName ()
	{
		return "csv";
	}

	@Override
	public CSVImporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		Charset charset = DEFAULT_CHARSET;
		CSVFormat format = CSVFormat.DEFAULT;
		boolean naFilter = false;
		String[] naValues = null;

		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'c':
				{
					charset = Charset.forName (option.getValue ());
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
					String value = option.getValue ();
					boolean header = StringUtils.getOnOffState (value, "header");
					if (header)
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
			}
		}
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		CSVImporter importer = new CSVImporter (interpreter.getFile (args[0]), charset, format);
		if (naFilter == true)
		{
			importer.setNaFilter (true);
			importer.setNaValues (naValues);
		}
		return importer;
	}
}

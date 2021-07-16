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
package com.teradata.jaqy.printer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;
import com.teradata.jaqy.utils.JsonBinaryFormat;
import com.teradata.jaqy.utils.OptionsUtils;

/**
 * @author	Heng Yuan
 */
public class JsonPrinterFactory extends JaqyHandlerFactoryImpl<JaqyPrinter>
{
	public JsonPrinterFactory ()
	{
		addOption (OptionsUtils.getOnOffOption ("p", "pretty", "turns pretty print on / off."));
		Option option;
		option = new Option ("b", "binary", true, "sets the binary format.");
		option.setArgName ("base64 | hex");
		addOption (option);
	}

	@Override
	public String getName ()
	{
		return "json";
	}

	@Override
	public JaqyPrinter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		JsonPrinterOptions printOptions = new JsonPrinterOptions ();

		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'b':
				{
					String value = option.getValue ();
					if ("hex".equals (value))
						printOptions.binaryFormat = JsonBinaryFormat.Hex;
					else if ("base64".equals (value))
						printOptions.binaryFormat = JsonBinaryFormat.Base64;
					else
						throw new IllegalArgumentException ("invalid binary option value: " + value);
					break;
				}
				case 'p':
				{
					String value = option.getValue ();
					if ("on".equals (value))
						printOptions.pretty = true;
					else if ("off".equals (value))
						printOptions.pretty = false;
					else
						throw new IllegalArgumentException ("invalid pretty option value: " + value);
					break;
				}
			}
		}
		return new JsonPrinter (printOptions);
	}
}

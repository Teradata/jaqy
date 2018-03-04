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
package com.teradata.jaqy.exporter;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;
import com.teradata.jaqy.utils.JsonBinaryFormat;
import com.teradata.jaqy.utils.JsonFormat;
import com.teradata.jaqy.utils.OptionsUtils;

/**
 * @author	Heng Yuan
 */
public class JsonExporterFactory extends JaqyHandlerFactoryImpl<JaqyExporter>
{
	public static Charset DEFAULT_CHARSET = Charset.forName ("utf-8");
	public static JsonBinaryFormat DEFAULT_BINARY_FORMAT = JsonBinaryFormat.Base64;
	public static JsonFormat DEFAULT_FORMAT = JsonFormat.Text;

	public JsonExporterFactory ()
	{
		addOption ("c", "charset", true, "sets the file character set");
		addOption (OptionsUtils.getOnOffOption ("p", "pretty", "turns pretty print on / off."));
		Option option;
		option = new Option ("b", "binary", true, "sets the binary format.");
		option.setArgName ("base64 | hex");
		addOption (option);

		option = new Option ("f", "format", true, "sets the JSON format.");
		option.setArgName ("text | bson");
		addOption (option);
	}

	@Override
	public String getName ()
	{
		return "json";
	}

	@Override
	public JaqyExporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		Charset charset = DEFAULT_CHARSET;
		boolean pretty = false;
		JsonBinaryFormat binaryFormat = DEFAULT_BINARY_FORMAT;
		JsonFormat format = DEFAULT_FORMAT;
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'c':
				{
					charset = Charset.forName (option.getValue ());
					break;
				}
				case 'f':
				{
					String value = option.getValue ();
					if ("text".equals (value))
						format = JsonFormat.Text;
					else if ("bson".equals (value))
						format = JsonFormat.Bson;
					else
						throw new IllegalArgumentException ("invalid format option value: " + value);
					break;
				}
				case 'b':
				{
					String value = option.getValue ();
					if ("hex".equals (value))
						binaryFormat = JsonBinaryFormat.Hex;
					else if ("base64".equals (value))
						binaryFormat = JsonBinaryFormat.Base64;
					else
						throw new IllegalArgumentException ("invalid binary option value: " + value);
					break;
				}
				case 'p':
				{
					String value = option.getValue ();
					if ("on".equals (value))
						pretty = true;
					else if ("off".equals (value))
						pretty = false;
					else
						throw new IllegalArgumentException ("invalid pretty option value: " + value);
				}
			}
		}
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		OutputStream os = interpreter.getPath (args[0]).getOutputStream ();

		// In case of BSON, we use the default handling of byte array since
		// BSON supports it natively.
		if (format == JsonFormat.Bson)
			binaryFormat = JsonBinaryFormat.Base64;

		return new JsonExporter (os, charset, format, pretty, binaryFormat);
	}
}

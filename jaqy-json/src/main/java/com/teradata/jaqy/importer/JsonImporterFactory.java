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

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;
import com.teradata.jaqy.utils.JsonBinaryFormat;
import com.teradata.jaqy.utils.JsonFormat;

/**
 * @author	Heng Yuan
 */
public class JsonImporterFactory extends JaqyHandlerFactoryImpl<JsonImporter>
{
	public static Charset DEFAULT_CHARSET = Charset.forName ("utf-8");
	public static JsonBinaryFormat DEFAULT_BINARY_FORMAT = JsonBinaryFormat.Base64;
	public static JsonFormat DEFAULT_FORMAT = JsonFormat.Text;

	public JsonImporterFactory ()
	{
		addOption ("c", "charset", true, "sets the file character set");
		addOption ("a", "array", false, "treats BSON root document as array.");
		addOption ("r", "rowexp", true, "sets the row expression");

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
	public JsonImporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		Charset charset = DEFAULT_CHARSET;
		JsonBinaryFormat binaryFormat = DEFAULT_BINARY_FORMAT;
		JsonFormat format = DEFAULT_FORMAT;
		boolean rootAsArray = false;
		String rowExp = null;
		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'a':
				{
					rootAsArray = true;
					break;
				}
				case 'c':
				{
					try
					{
						charset = Charset.forName (option.getValue ());
					}
					catch (Exception ex)
					{
						charset = null;
					}
					if (charset == null)
						throw new IllegalArgumentException ("invalid character set: " + option.getValue ());
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
				case 'r':
				{
					rowExp = option.getValue ();
					break;
				}
			}
		}
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		InputStream is = interpreter.getPath (args[0]).getInputStream ();

		// In case of BSON, we use the default handling of byte array since
		// BSON supports it natively.
		if (format == JsonFormat.Bson)
			binaryFormat = JsonBinaryFormat.Base64;

		return new JsonImporter (interpreter.getGlobals (), interpreter.getSession ().getConnection (), is, charset, format, binaryFormat, rowExp, rootAsArray);
	}
}

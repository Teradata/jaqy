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

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author Heng Yuan
 */
public class AvroImporterFactory extends JaqyHandlerFactoryImpl<AvroImporter>
{
	public AvroImporterFactory ()
	{
	}

	@Override
	public String getName ()
	{
		return "avro";
	}

	@Override
	public AvroImporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		Path file = interpreter.getPath (args[0]);
		return new AvroImporter (interpreter.getSession ().getConnection (), file);
	}
}

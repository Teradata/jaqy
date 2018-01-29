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
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.exporter.PipeExporter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author	Heng Yuan
 */
public class PipeImporterFactory extends JaqyHandlerFactoryImpl<PipeImporter>
{
	public PipeImporterFactory ()
	{
	}

	@Override
	public String getName ()
	{
		return "pipe";
	}

	@Override
	public PipeImporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		JaqyExporter exporter = interpreter.getExporter ();
		if (exporter == null)
			interpreter.error ("No current pipe export.");
		else if (!(exporter instanceof PipeExporter))
			interpreter.error ("Current export is not a pipe export.");

		JaqyResultSet rs = ((PipeExporter)exporter).getResultSet ();
		interpreter.setExporter (null);
		return new PipeImporter (rs, interpreter.getGlobals ());
	}
}

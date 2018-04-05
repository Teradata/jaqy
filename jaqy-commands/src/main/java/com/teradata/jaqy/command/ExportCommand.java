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
package com.teradata.jaqy.command;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class ExportCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "exports a ResultSet.";
	}

	@Override
	public String getLongDescription ()
	{
		Globals globals = getGlobals ();
		StringBuffer buffer = new StringBuffer ();
		buffer.append ("usage: " + getCommand () + " [type] [type options] [path]\ntype:");
		String[] names = globals.getExporterManager ().getNames ();
		for (String name : names)
			buffer.append ("\n  ").append (name);
		buffer.append ("\n");
		for (String name : names)
		{
			String syntax = globals.getExporterManager ().getHandlerFactory (name).getLongDescription ();
			if (syntax != null)
				buffer.append ('\n').append (syntax);
		}
		return buffer.toString ();
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		if (args.length == 0)
		{
			JaqyExporter exporter = interpreter.getExporter ();
			if (exporter == null)
			{
				interpreter.println ("No current exports.");
			}
			else
			{
				interpreter.println (getCommand () + " " + exporter.getName ());
			}
		}
		else
		{
			String name = args[0];
			args = StringUtils.shiftArgs (args);
			JaqyExporter exporter = interpreter.getGlobals ().getExporterManager ().getHandler (name, args, interpreter);
			if (exporter == null)
			{
				interpreter.error ("invalid format name: " + name);
			}
			interpreter.setExporter (exporter);
		}
	}
}

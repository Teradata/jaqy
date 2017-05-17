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
package com.teradata.jaqy.command;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class FormatCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "chooses the ResultSet output format.";
	}

	@Override
	public String getLongDescription ()
	{
		Globals globals = getGlobals ();
		StringBuffer buffer = new StringBuffer ();
		buffer.append ("usage: " + getCommand () + " [type] [type options]\ntype:");
		String[] names = globals.getPrinterManager ().getNames ();
		for (String name : names)
			buffer.append ("\n  ").append (name);
		buffer.append ("\n");
		for (String name : names)
		{
			String syntax = globals.getPrinterManager ().getHandlerFactory (name).getLongDescription ();
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
	public void execute (String[] args, Globals globals, JaqyInterpreter interpreter) throws Exception
	{
		if (args.length == 0)
		{
			interpreter.println (getCommand () + " " + interpreter.getPrinter ().getName ());
		}
		else
		{
			String name = args[0];
			args = StringUtils.shiftArgs (args);
			JaqyPrinter printer = globals.getPrinterManager ().getHandler (name, args);
			if (printer == null)
			{
				interpreter.error ("invalid format name: " + name);
				return;
			}
			interpreter.setPrinter (printer);
		}
	}
}

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

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;

import javax.script.ScriptEngine;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.lineinput.ReaderLineInput;

/**
 * @author	Heng Yuan
 */
public class IfCommand extends JaqyCommandAdapter
{
	public IfCommand ()
	{
		super ("if.txt");
	}

	@Override
	public String getDescription ()
	{
		return "conditional statement";
	}

	@Override
	public void execute (String[] args, boolean silent, Globals globals, JaqyInterpreter interpreter)
	{
		ScriptEngine engine = interpreter.getScriptEngine ();
		Boolean b = null;
		try
		{
			b = (Boolean)engine.eval (args[0]);
		}
		catch (Throwable t)
		{
			interpreter.getDisplay ().error (interpreter, t);
		}

//		if (b == null)
//		{
//			interpreter.error ("invalid condition");
//			b = Boolean.FALSE;
//		}
		globals.log (Level.INFO, "if condition = " + b);
		interpreter.setParseAction (this, b);
	}

	@Override
	public JaqyCommand.Type getType ()
	{
		return JaqyCommand.Type.mixed;
	}

	@Override
	public void parse (String action, Object value, Globals globals, JaqyInterpreter interpreter) throws IOException
	{
		globals.log (Level.INFO, "if condition = " + value);
		Display display = interpreter.getDisplay ();
		if (value != null && (Boolean)value)
		{
			globals.log (Level.INFO, "running if statement");
			globals.log (Level.INFO, action);
			globals.log (Level.INFO, "end if statement");
			interpreter.interpret (new ReaderLineInput (new StringReader (action), globals.getDirectory (), false), false);
			display.echo (interpreter, ".end " + getName (), false);
		}
		else
		{
			String[] lines = action.split ("\n");
			for (String line : lines)
			{
				display.echo (interpreter, "-- skip: " + line, false);
			}
			display.echo (interpreter, ".end " + getName (), false);
		}
	}

	@Override
	public boolean isMultiLine (String[] args)
	{
		return true;
	}
}

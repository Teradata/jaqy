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

import javax.script.ScriptEngine;

import com.teradata.jaqy.Debug;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyCommand;
import com.teradata.jaqy.interfaces.ParseAction;
import com.teradata.jaqy.lineinput.ReaderLineInput;

/**
 * @author	Heng Yuan
 */
public class IfCommand extends JaqyCommandAdapter implements ParseAction
{
	@Override
	public String getDescription ()
	{
		return "conditional statement";
	}

	@Override
	public String getLongDescription ()
	{
		return "usage: " + getCommand () + " [expression] then .goto [label]";
	}

	@Override
	public void execute (String[] args, Globals globals, JaqyInterpreter interpreter)
	{
		ScriptEngine engine = interpreter.getScriptEngine ();
		Boolean b = null;
		try
		{
			b = (Boolean)engine.eval (args[0]);
		}
		catch (Throwable t)
		{
			interpreter.error (t);
		}

		if (b == null)
		{
			interpreter.error ("invalid condition");
			b = Boolean.FALSE;
		}
		assert Debug.debug ("if condition = " + b);
		interpreter.setParseAction (this, b);
	}

	@Override
	public JaqyCommand.Type getType (String arguments)
	{
		return JaqyCommand.Type.begin;
	}

	@Override
	public void parse (String action, Object value, Globals globals, JaqyInterpreter interpreter) throws IOException
	{
		assert Debug.debug ("if condition = " + value);
		Display display = interpreter.getDisplay ();
		if ((Boolean)value)
		{
			assert Debug.debug ("running if statement");
			assert Debug.debug (action);
			assert Debug.debug ("end if statement");
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
}

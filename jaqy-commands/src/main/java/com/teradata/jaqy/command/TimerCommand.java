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

import java.sql.SQLException;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.interfaces.Variable;

/**
 * @author	Heng Yuan
 */
public class TimerCommand extends JaqyCommandAdapter
{
	private final static String TIMER = "timer";

	@Override
	public String getDescription ()
	{
		return "sets a timer.";
	}

	@Override
	public String getLongDescription ()
	{
		return getCommand () + " [set]";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.file;
	}

	private long getStartTime (JaqyInterpreter interpreter)
	{
		VariableManager vm = interpreter.getVariableManager ();
		Variable var = vm.getVariable ("timer");
		if (var == null)
			return 0;
		Object o = var.get ();
		if (o == null)
			return 0;
		if (!(o instanceof Number))
			return 0;
		return ((Number)o).longValue ();
	}

	private void printTime (long start, JaqyInterpreter interpreter)
	{
		long current = System.nanoTime ();
		long diff = current - start;
		long seconds = diff / 1000000000;
		long remain = diff % 1000000000;
		long hours = seconds / 3600;
		seconds = seconds % 3600;
		int min = (int) (seconds / 60);
		seconds = seconds % 60;

		String hh;
		if (hours < 10)
			hh = "0" + hours;
		else
			hh = Long.toString (hours);
		String mm;
		if (min < 10)
			mm = "0" + min;
		else
			mm = Integer.toString (min);
		String ss;
		if (seconds < 10)
			ss = "0" + seconds;
		else
			ss = Integer.toString ((int)seconds);
		String nano = Long.toString (remain);
		if (nano.length () < 9)
		{
			nano = "000000000".substring (nano.length ()) + nano;
		}
		interpreter.println ("-- timer: " + hh + ':' + mm + ':' + ss + '.' + nano);
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws SQLException
	{
		if (args.length == 0)
		{
			long startTime = getStartTime (interpreter);
			if (startTime == 0)
			{
				interpreter.println ("-- timer not yet set.");
			}
			else
			{
				printTime (startTime, interpreter);
			}
		}
		else
		{
			if ("set".equals (args[0]))
			{
				VariableManager vm = interpreter.getVariableManager ();
				vm.setVariable (TIMER, System.nanoTime ());
				interpreter.println ("-- timer started.");
			}
			else
			{
				interpreter.error ("Invalid argument: " + args[0]);
			}
		}
	}
}

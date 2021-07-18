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
package com.teradata.jaqy.command;

import java.sql.SQLException;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public class TimerCommand extends JaqyCommandAdapter
{
	private final static String TIMER = "timer";

	public TimerCommand ()
	{
		super ("timer");
	}

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
		Object timerValue = interpreter.getVariableValue (TIMER);
		if (timerValue == null)
			return 0;
		if (!(timerValue instanceof Number))
			return 0;
		return ((Number)timerValue).longValue ();
	}

	private void printTime (long start, JaqyInterpreter interpreter)
	{
		String timerString = getTimerString (System.nanoTime () - start);
		interpreter.println ("-- timer: " + timerString);
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
				interpreter.setVariableValue (TIMER, System.nanoTime ());
				interpreter.println ("-- timer started.");
			}
			else
			{
				interpreter.error ("Invalid argument: " + args[0]);
			}
		}
	}

	public static String getTimerString (long diff)
	{
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
		return hh + ':' + mm + ':' + ss + '.' + nano;
	}

}

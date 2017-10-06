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
package com.teradata.jaqy.utils;

import java.sql.SQLException;

import com.teradata.jaqy.ConsoleDisplay;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.interfaces.ErrorStateHandler;
import com.teradata.jaqy.interfaces.StateHandler;

/**
 * @author	Heng Yuan
 */
public class SimpleStateHandlers
{
	public final static StateHandler promptHandler = new StateHandler ()
	{
		@Override
		public String getString (JaqyInterpreter interpreter)
		{
			ConsoleDisplay display = (ConsoleDisplay) interpreter.getDisplay ();
			StringBuffer buffer = new StringBuffer ();
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().color ("green", null, true));
			Session session = interpreter.getSession ();
			if (display.isInteractive ())
			{
				String description = "none";
				if (session != null)
				{
					description = session.getDescription ();
				}
				buffer.append ("-- " + interpreter.getSqlCount () + "/" + interpreter.getCommandCount () + " - " + description + " --");
			}
			else
			{
				int id = -1;
				if (session != null)
				{
					id = session.getId ();
				}
				buffer.append (display.fill ("-- " + interpreter.getSqlCount () + "/" + interpreter.getCommandCount () + " - " + id + " "));
			}
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().reset ());
			buffer.append ('\n');
			return buffer.toString ();
		}
	};
	public final static StateHandler titleHandler = new StateHandler ()
	{
		@Override
		public String getString (JaqyInterpreter interpreter)
		{
			Session session = interpreter.getSession ();
			if (session == null)
				return "Jaqy Console";
			return "jaqy - " + session.getDescription ();
		}
	};
	public final static StateHandler successHandler = new StateHandler ()
	{
		@Override
		public String getString (JaqyInterpreter interpreter)
		{
			ConsoleDisplay display = (ConsoleDisplay) interpreter.getDisplay ();
			StringBuffer buffer = new StringBuffer ();
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().fgColor ("green"));
			buffer.append ("-- success --");
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().reset ());
			return buffer.toString ();
		}
	};
	public final static StateHandler successUpdateHandler = new StateHandler ()
	{
		@Override
		public String getString (JaqyInterpreter interpreter)
		{
			ConsoleDisplay display = (ConsoleDisplay) interpreter.getDisplay ();
			StringBuffer buffer = new StringBuffer ();
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().fgColor ("green"));
			buffer.append ("-- success. update count = " + interpreter.getSession ().getActivityCount ());
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().reset ());
			return buffer.toString ();
		}
	};
	public final static StateHandler activityCountHandler = new StateHandler ()
	{
		@Override
		public String getString (JaqyInterpreter interpreter)
		{
			ConsoleDisplay display = (ConsoleDisplay) interpreter.getDisplay ();
			StringBuffer buffer = new StringBuffer ();
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().fgColor ("green"));
			buffer.append ("-- activity count = " + interpreter.getSession ().getActivityCount ());
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().reset ());
			return buffer.toString ();
		}
	};
	public final static ErrorStateHandler failureHandler = new ErrorStateHandler ()
	{
		@Override
		public String getString (Throwable t, String msg, JaqyInterpreter interpreter)
		{
			ConsoleDisplay display = (ConsoleDisplay) interpreter.getDisplay ();
			if (t == null)
			{
				return "-- error: " + msg;
			}

			StringBuffer buffer = new StringBuffer ();
			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().fgColor ("red"));

			if (t instanceof SQLException)
			{
				SQLException ex = (SQLException)t;
				String state = ex.getSQLState ();
				state = (state == null) ? "" : "[" + state + "] ";
				buffer.append ("-- failure " + ex.getErrorCode () + ": " + state + ex.getMessage ());
				while ((ex = ex.getNextException ()) != null)
				{
					buffer.append ("\n-- cause   " + ex.getErrorCode () + ": " + state + ex.getMessage ());
				}
			}
			else
			{
				buffer.append ("-- error: " + t.getMessage ());
			}

			if (display.isColorEnabled ())
				buffer.append (display.getEscape ().reset ());
			return buffer.toString ();
		}
	};
}

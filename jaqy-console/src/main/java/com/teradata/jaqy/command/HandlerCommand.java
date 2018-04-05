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

import com.teradata.jaqy.ConsoleDisplay;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.ErrorStateHandler;
import com.teradata.jaqy.interfaces.StateHandler;
import com.teradata.jaqy.utils.ScriptErrorStateHandler;
import com.teradata.jaqy.utils.ScriptStateHandler;
import com.teradata.jaqy.utils.DefaultStateHandlers;

/**
 * @author	Heng Yuan
 */
public class HandlerCommand extends JaqyCommandAdapter
{
	public HandlerCommand ()
	{
		super ("handler.txt");
	}

	@Override
	public String getDescription ()
	{	
		return "displays / sets handlers.";
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter)
	{
		String argument = args[0].trim ();
		int i1 = argument.indexOf (' ');
		int i2 = argument.indexOf ('\t');
		if (i1 < 0)
			i1 = i2;
		else if (i2 >= 0 && i1 > i2)
		{
			i1 = i2;
		}
		String type;
		if (i1 < 0)
		{
			type = argument;
			argument = "";
		}
		else
		{
			type = argument.substring (0, i1);
			argument = argument.substring (i1 + 1).trim ();
		}

		if ("prompt".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = ((ConsoleDisplay)interpreter.getDisplay ()).getPromptHandler ();
				String script = null;
				if (handler == DefaultStateHandlers.promptHandler)
					script = "default";
				else if (handler instanceof ScriptStateHandler)
					script = ((ScriptStateHandler)handler).getScript ();
				if (script != null)
				{
					interpreter.println (getCommand () + " " + type + " " + script);
					return;
				}
			}
			else
			{
				ScriptStateHandler handler;
				if ("default".equals (argument))
					handler = null;
				else
				{
					handler = new ScriptStateHandler ();
					handler.setScript (argument);
				}
				((ConsoleDisplay)interpreter.getDisplay ()).setPromptHandler (handler);
				return;
			}
		}
		else if ("title".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = ((ConsoleDisplay)interpreter.getDisplay ()).getTitleHandler ();
				String script = null;
				if (handler == DefaultStateHandlers.titleHandler)
					script = "default";
				else if (handler instanceof ScriptStateHandler)
					script = ((ScriptStateHandler)handler).getScript ();
				if (script != null)
				{
					interpreter.println (getCommand () + " " + type + " " + script);
					return;
				}
			}
			else
			{
				ScriptStateHandler handler;
				if ("default".equals (argument))
					handler = null;
				else
				{
					handler = new ScriptStateHandler ();
					handler.setScript (argument);
				}
				((ConsoleDisplay)interpreter.getDisplay ()).setTitleHandler (handler, interpreter);
				return;
			}
		}
		else if ("success".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = ((ConsoleDisplay)interpreter.getDisplay ()).getSuccessHandler ();
				String script = null;
				if (handler == DefaultStateHandlers.successHandler)
					script = "default";
				else if (handler instanceof ScriptStateHandler)
					script = ((ScriptStateHandler)handler).getScript ();
				if (script != null)
				{
					interpreter.println (getCommand () + " " + type + " " + script);
					return;
				}
			}
			else
			{
				ScriptStateHandler handler;
				if ("default".equals (argument))
					handler = null;
				else
				{
					handler = new ScriptStateHandler ();
					handler.setScript (argument);
				}
				((ConsoleDisplay)interpreter.getDisplay ()).setSuccessHandler (handler);
				return;
			}
		}
		else if ("update".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = ((ConsoleDisplay)interpreter.getDisplay ()).getUpdateHandler ();
				String script = null;
				if (handler == DefaultStateHandlers.updateHandler)
					script = "default";
				else if (handler instanceof ScriptStateHandler)
					script = ((ScriptStateHandler)handler).getScript ();
				if (script != null)
				{
					interpreter.println (getCommand () + " " + type + " " + script);
					return;
				}
			}
			else
			{
				ScriptStateHandler handler;
				if ("default".equals (argument))
					handler = null;
				else
				{
					handler = new ScriptStateHandler ();
					handler.setScript (argument);
				}
				((ConsoleDisplay)interpreter.getDisplay ()).setUpdateHandler (handler);
				return;
			}
		}
		else if ("error".equals (type))
		{
			if (argument.length () == 0)
			{
				ErrorStateHandler handler = ((ConsoleDisplay)interpreter.getDisplay ()).getErrorHandler ();
				String script = null;
				if (handler == DefaultStateHandlers.errorHandler)
					script = "default";
				else if (handler instanceof ScriptErrorStateHandler)
					script = ((ScriptErrorStateHandler)handler).getScript ();
				if (script != null)
				{
					interpreter.println (getCommand () + " " + type + " " + script);
					return;
				}
			}
			else
			{
				ScriptErrorStateHandler handler;
				if ("default".equals (argument))
					handler = null;
				else
				{
					handler = new ScriptErrorStateHandler ();
					handler.setScript (argument);
				}
				((ConsoleDisplay)interpreter.getDisplay ()).setErrorHandler (handler);
				return;
			}
		}
		else if ("activity".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = ((ConsoleDisplay)interpreter.getDisplay ()).getActivityCountHandler ();
				String script = null;
				if (handler == DefaultStateHandlers.activityCountHandler)
					script = "default";
				else if (handler instanceof ScriptStateHandler)
					script = ((ScriptStateHandler)handler).getScript ();
				if (script != null)
				{
					interpreter.println (getCommand () + " " + type + " " + script);
					return;
				}
			}
			else
			{
				ScriptStateHandler handler;
				if ("default".equals (argument))
					handler = null;
				else
				{
					handler = new ScriptStateHandler ();
					handler.setScript (argument);
				}
				((ConsoleDisplay)interpreter.getDisplay ()).setActivityCountHandler (handler);
				return;
			}
		}
		else
		{
			if (type.length () == 0)
				interpreter.error ("missing handler type");
			else
				interpreter.error ("unknown handler type: " + type);
		}
		interpreter.println ("unknown " + type + " handler.");
	}
}

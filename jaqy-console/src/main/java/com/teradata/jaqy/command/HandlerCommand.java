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

import com.teradata.jaqy.ConsoleDisplay;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.StateHandler;
import com.teradata.jaqy.utils.DefaultStateHandlers;
import com.teradata.jaqy.utils.ScriptStateHandler;

/**
 * @author	Heng Yuan
 */
public class HandlerCommand extends JaqyCommandAdapter
{
	public HandlerCommand ()
	{
		super ("handler", "handler.txt");
	}

	@Override
	public String getDescription ()
	{
		return "displays / sets handlers.";
	}

	private void printHandler (StateHandler currentHandler, StateHandler defaultHandler, String type, JaqyInterpreter interpreter)
	{
		String script = null;
		if (currentHandler == defaultHandler)
			script = "default";
		else if (currentHandler == DefaultStateHandlers.noneHandler)
			script = "none";
		else if (currentHandler instanceof ScriptStateHandler)
			script = ((ScriptStateHandler)currentHandler).getScript ();
		if (script != null)
		{
			interpreter.println (getCommand () + " " + type + " " + script);
		}
	}

	private StateHandler getStateHandler (String argument)
	{
		StateHandler handler;
		if ("default".equals (argument))
		{
			handler = null;
		}
		else if ("none".equals (argument))
		{
			handler = DefaultStateHandlers.noneHandler;
		}
		else
		{
			handler = new ScriptStateHandler (argument);
		}
		return handler;
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

		ConsoleDisplay display = (ConsoleDisplay)interpreter.getDisplay ();
		if ("prompt".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getPromptHandler ();
				printHandler (handler, DefaultStateHandlers.promptHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setPromptHandler (handler);
			}
		}
		else if ("title".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getTitleHandler ();
				printHandler (handler, DefaultStateHandlers.titleHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setTitleHandler (handler, interpreter);
			}
		}
		else if ("success".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getSuccessHandler ();
				printHandler (handler, DefaultStateHandlers.successHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setSuccessHandler (handler);
			}
		}
		else if ("update".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getUpdateHandler ();
				printHandler (handler, DefaultStateHandlers.updateHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setUpdateHandler (handler);
			}
		}
		else if ("error".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getErrorHandler ();
				printHandler (handler, DefaultStateHandlers.errorHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setErrorHandler (handler);
			}
		}
		else if ("activity".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getActivityCountHandler ();
				printHandler (handler, DefaultStateHandlers.activityCountHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setActivityCountHandler (handler);
			}
		}
		else if ("iteration".equals (type))
		{
			if (argument.length () == 0)
			{
				StateHandler handler = display.getIterationHandler ();
				printHandler (handler, DefaultStateHandlers.iterationHandler, type, interpreter);
			}
			else
			{
				StateHandler handler = getStateHandler (argument);
				display.setIterationHandler (handler);
			}
		}
		else if ("none".equals (type))
		{
			display.setPromptHandler (DefaultStateHandlers.noneHandler);
			display.setSuccessHandler (DefaultStateHandlers.noneHandler);
			display.setUpdateHandler (DefaultStateHandlers.noneHandler);
			display.setErrorHandler (DefaultStateHandlers.noneHandler);
			display.setActivityCountHandler (DefaultStateHandlers.noneHandler);
			display.setTitleHandler (DefaultStateHandlers.noneHandler, interpreter);
			display.setIterationHandler (DefaultStateHandlers.noneHandler);
		}
		else if ("default".equals (type))
		{
			display.setPromptHandler (DefaultStateHandlers.promptHandler);
			display.setSuccessHandler (DefaultStateHandlers.successHandler);
			display.setUpdateHandler (DefaultStateHandlers.updateHandler);
			display.setErrorHandler (DefaultStateHandlers.errorHandler);
			display.setActivityCountHandler (DefaultStateHandlers.activityCountHandler);
			display.setIterationHandler (DefaultStateHandlers.iterationHandler);
		}
		else
		{
			if (type.length () == 0)
				interpreter.error ("missing handler type");
			else
				interpreter.error ("unknown handler type: " + type);
		}
	}
}

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
package com.teradata.jaqy.setting;

import com.teradata.jaqy.Echo;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Display;

/**
 * @author	Heng Yuan
 */
public class EchoSetting extends JaqySettingAdapter
{
	public EchoSetting ()
	{
		super ("echo");
	}

	@Override
	public String getDescription ()
	{
		return "turns echo on / off";
	}

	@Override
	public Object get (JaqyInterpreter interpreter) throws Exception
	{
		return interpreter.getDisplay ().getEcho ();
	}

	@Override
	public void set (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception
	{
		Display display = interpreter.getDisplay ();
		if ("auto".equals (args[0]))
		{
			display.setEcho (Echo.auto);
		}
		else if ("on".equals (args[0]))
		{
			display.setEcho (Echo.on);
		}
		else if ("off".equals (args[0]))
		{
			display.setEcho (Echo.off);
		}
		else
		{
			interpreter.error ("invalid setting value");
		}
	}
}

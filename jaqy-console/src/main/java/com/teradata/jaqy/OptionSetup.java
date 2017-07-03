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
package com.teradata.jaqy;

import com.teradata.jaqy.option.ColorOption;
import com.teradata.jaqy.option.EchoOption;
import com.teradata.jaqy.option.HelpOption;
import com.teradata.jaqy.option.NoRCOption;
import com.teradata.jaqy.option.RCFileOption;
import com.teradata.jaqy.option.VersionOption;

/**
 * @author	Heng Yuan
 */
class OptionSetup
{
	public static void init (Globals globals)
	{
		globals.addOption (new HelpOption ());;
		globals.addOption (new VersionOption ());;
		globals.addOption (new EchoOption ());;

		// console specific options
		globals.addOption (new RCFileOption ());;
		globals.addOption (new NoRCOption ());;
		globals.addOption (new ColorOption ());;
	}
}

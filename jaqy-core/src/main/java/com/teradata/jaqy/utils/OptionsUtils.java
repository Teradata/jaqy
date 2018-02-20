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
package com.teradata.jaqy.utils;

import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @author	Heng Yuan
 */
public class OptionsUtils
{
	public static Option getOnOffOption (String opt, String longOpt, String description)
	{
		Option option = new Option (opt, longOpt, true, description);
		option.setArgName ("on | off");
		return option;
	}

	public static void printHelp (PrintWriter pw, Options options, String syntax, String header, String footer)
	{
		HelpFormatter formatter = new HelpFormatter ();
		formatter.printHelp (pw, 78, syntax, header, options, 2, HelpFormatter.DEFAULT_DESC_PAD, footer);
		pw.flush ();
	}

	public static void printHelpNoUsage (PrintWriter pw, Options options, String syntax, String header, String footer)
	{
		HelpFormatter formatter = new HelpFormatter ();
		formatter.setSyntaxPrefix ("");
		formatter.printHelp (pw, 78, syntax, header, options, 2, HelpFormatter.DEFAULT_DESC_PAD, footer);
		pw.flush ();
	}
}

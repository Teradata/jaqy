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
package com.teradata.jaqy.printer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;
import com.teradata.jaqy.utils.OptionsUtils;

/**
 * @author	Heng Yuan
 */
public class TablePrinterFactory extends JaqyHandlerFactoryImpl<JaqyPrinter>
{
	public static int DEFAULT_MAX_COLUMN_SIZE = 100;
	public static boolean DEFAULT_AUTO_SIZE = true;
	public static int DEFAULT_SCAN_THRESHOLD = 1000;
	public static int DEFAULT_COLUMN_THRESHOLD = 1;

	public TablePrinterFactory ()
	{
		addOption (OptionsUtils.getOnOffOption ("a", "autosize", "turns auto column size determination on / off."));
		addOption (OptionsUtils.getOnOffOption ("b", "border", "turns border on / off."));
		addOption ("r", "rowthreshold", true, "sets row threshold.  Scan up to this number of rows to determine the size of the column.");
		addOption ("c", "columnthreshold", true, "sets column size threshold.  If a column size is less than the threshold, then no auto size.");
		addOption ("m", "maxsize", true, "sets the maximum size of a column.");
	}

	@Override
	public String getName ()
	{
		return "table";
	}

	@Override
	public JaqyPrinter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
	{
		boolean autoSize = DEFAULT_AUTO_SIZE;
		boolean border = false;
		long scanThreshold = DEFAULT_SCAN_THRESHOLD;
		int columnThreshold = DEFAULT_COLUMN_THRESHOLD;
		int maxColumnSize = DEFAULT_MAX_COLUMN_SIZE;

		for (Option option : cmdLine.getOptions ())
		{
			switch (option.getOpt ().charAt (0))
			{
				case 'a':
				{
					String value = option.getValue ();
					if ("on".equals (value))
						autoSize = true;
					else if ("off".equals (value))
						autoSize = false;
					else
					{
						throw new IllegalArgumentException ("invalid option value for --autosize");
					}
					break;
				}
				case 'b':
				{
					String value = option.getValue ();
					if ("on".equals (value))
						border = true;
					else if ("off".equals (value))
						border = false;
					else
					{
						throw new IllegalArgumentException ("invalid option value for --border");
					}
					break;
				}
				case 'r':
				{
					scanThreshold = Long.parseLong (option.getValue ());
					if (scanThreshold <= 0)
						scanThreshold = Long.MAX_VALUE;
					break;
				}
				case 'c':
				{
					columnThreshold = Integer.parseInt (option.getValue ());
					if (columnThreshold <= 0)
						columnThreshold = 1;
					break;
				}
				case 'm':
				{
					maxColumnSize = Integer.parseInt (option.getValue ());
					if (maxColumnSize <= 0)
						maxColumnSize = DEFAULT_MAX_COLUMN_SIZE;
					break;
				}
			}
		}
		return new TablePrinter (border, autoSize, scanThreshold, columnThreshold, maxColumnSize);
	}
}

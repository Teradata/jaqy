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
package com.teradata.jaqy.printer;

import java.io.PrintWriter;
import java.sql.SQLException;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyPrinter;

/**
 * @author	Heng Yuan
 */
public class QuietPrinter implements JaqyPrinter
{
	private static JaqyPrinter s_instance = new QuietPrinter ();

	public static JaqyPrinter getInstance ()
	{
		return s_instance;
	}

	private QuietPrinter ()
	{
	}

	public long print (JaqyResultSet rs, Globals globals, Display display, PrintWriter pw) throws SQLException
	{
		int count = 0;
		while (rs.next ())
		{
			++count;
		}
		return count;
	}

	@Override
	public String getName ()
	{
		return "quiet";
	}
}

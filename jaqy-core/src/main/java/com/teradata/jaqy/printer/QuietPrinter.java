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

import java.io.PrintWriter;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
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

	public long print (JaqyResultSet rs, PrintWriter pw, long limit, JaqyInterpreter interpreter) throws SQLException
	{
		long count = 0;
		if (limit == 0)
			limit = Long.MAX_VALUE;
		while (rs.next () && count < limit)
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

	@Override
	public boolean isForwardOnly ()
	{
		return true;
	}
}

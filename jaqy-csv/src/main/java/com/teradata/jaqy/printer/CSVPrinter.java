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
package com.teradata.jaqy.printer;

import java.io.PrintWriter;

import org.apache.commons.csv.CSVFormat;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.typehandler.TypeHandler;

/**
 * @author	Heng Yuan
 */
class CSVPrinter implements JaqyPrinter
{
	private final CSVFormat m_format;

	public CSVPrinter (CSVFormat format)
	{
		m_format = format;
	}

	@Override
	public String getName ()
	{
		return "csv";
	}

	@Override
	public long print (JaqyResultSet rs, PrintWriter pw, long limit, JaqyInterpreter interpreter) throws Exception
	{
		JaqyHelper helper = rs.getHelper ();
		JaqyResultSetMetaData metaData = rs.getMetaData ();
		int columns = metaData.getColumnCount ();
		TypeHandler[] handlers = new TypeHandler[columns];

		@SuppressWarnings ("resource")
		org.apache.commons.csv.CSVPrinter printer = new org.apache.commons.csv.CSVPrinter (pw, m_format);

		for (int i = 0; i < columns; ++i)
		{
			// print the header row
			printer.print (metaData.getColumnLabel (i + 1));

			handlers[i] = helper.getTypeHandler (rs, i + 1);
		}
		printer.println ();

		long count = 0;
		if (limit == 0)
			limit = Long.MAX_VALUE;
		while (rs.next () && count < limit)
		{
			++count;
			for (int i = 0; i < columns; ++i)
			{
				printer.print (handlers[i].getString (rs, i + 1, interpreter));
			}
			printer.println ();
		}
		printer.flush ();
		return count;
	}

	@Override
	public boolean isForwardOnly ()
	{
		return true;
	}
}

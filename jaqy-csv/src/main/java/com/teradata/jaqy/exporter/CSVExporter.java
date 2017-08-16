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
package com.teradata.jaqy.exporter;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.typehandler.TypeHandlerRegistry;

/**
 * @author	Heng Yuan
 */
class CSVExporter implements JaqyExporter
{
	private final CSVFormat m_format;
	private final Writer m_out;

	public CSVExporter (CSVFormat format, Writer out)
	{
		m_format = format;
		m_out = out;
	}

	@Override
	public String getName ()
	{
		return "csv";
	}

	@Override
	public long export (JaqyResultSet rs, Globals globals) throws Exception
	{
		PrintWriter pw = new PrintWriter (new BufferedWriter (m_out));
		JaqyResultSetMetaData metaData = rs.getMetaData ();
		int columns = metaData.getColumnCount ();
		TypeHandler[] handlers = new TypeHandler[columns];

		CSVPrinter printer = new CSVPrinter (pw, m_format);
		for (int i = 0; i < columns; ++i)
		{
			// print the header row
			printer.print (metaData.getColumnLabel (i + 1));

			int type = metaData.getColumnType (i + 1);
			handlers[i] = TypeHandlerRegistry.getTypeHandler (type);
		}
		printer.println ();

		long count = 0;
		while (rs.next ())
		{
			++count;
			for (int i = 0; i < columns; ++i)
			{
				printer.print (handlers[i].getString (rs, i + 1));
			}
			printer.println ();
		}
		printer.close ();
		return count;
	}
}

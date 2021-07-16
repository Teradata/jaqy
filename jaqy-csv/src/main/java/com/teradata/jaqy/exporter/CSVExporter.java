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
package com.teradata.jaqy.exporter;

import java.io.*;

import org.apache.commons.csv.CSVPrinter;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.CSVExportInfo;
import com.teradata.jaqy.utils.FileHandler;

/**
 * @author	Heng Yuan
 */
class CSVExporter implements JaqyExporter
{
	private final Path m_file;
	private final Writer m_out;
	private final CSVExporterOptions m_options;

	public CSVExporter (Path file, CSVExporterOptions options) throws IOException
	{
		m_file = file;
		m_options = options;
		m_out = new OutputStreamWriter (file.getOutputStream (), options.charset);
	}

	@Override
	public String getName ()
	{
		return "csv";
	}

	@Override
	public long export (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		PrintWriter pw = new PrintWriter (new BufferedWriter (m_out));
		JaqyResultSetMetaData metaData = rs.getMetaData ();
		int columns = metaData.getColumnCount ();
		TypeHandler[] handlers = new TypeHandler[columns];

		JaqyHelper helper = rs.getHelper ();
		CSVPrinter printer = new CSVPrinter (pw, m_options.format);
		for (int i = 0; i < columns; ++i)
		{
			// print the header row
			printer.print (metaData.getColumnLabel (i + 1));

			CSVExportInfo fileInfo = m_options.fileInfoMap.get (i + 1);
			if (fileInfo == null)
				handlers[i] = helper.getTypeHandler (rs, i + 1);
			else
				handlers[i] = new FileHandler (m_file, fileInfo);
		}
		printer.println ();

		long count = 0;
		while (rs.next ())
		{
			++count;
			for (int i = 0; i < columns; ++i)
			{
				printer.print (handlers[i].getString (rs, i + 1, interpreter));
			}
			printer.println ();
		}
		printer.close ();
		return count;
	}

	@Override
	public void close ()
	{
		try
		{
			m_out.close ();
		}
		catch (Exception ex)
		{
		}
	}
}

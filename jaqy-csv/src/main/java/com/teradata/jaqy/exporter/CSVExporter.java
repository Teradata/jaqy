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
package com.teradata.jaqy.exporter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.CSVExportInfo;

/**
 * @author	Heng Yuan
 */
class CSVExporter implements JaqyExporter
{
	private final Path m_file;
	private final CSVFormat m_format;
	private final Writer m_out;
	private final HashMap<Integer, CSVExportInfo> m_fileInfoMap;

	public CSVExporter (Path file, Charset charset, CSVFormat format, HashMap<Integer, CSVExportInfo> fileInfoMap) throws IOException
	{
		m_file = file;
		m_format = format;
		m_out = new OutputStreamWriter (file.getOutputStream (), charset);
		m_fileInfoMap = fileInfoMap;
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
		CSVPrinter printer = new CSVPrinter (pw, m_format);
		for (int i = 0; i < columns; ++i)
		{
			// print the header row
			printer.print (metaData.getColumnLabel (i + 1));

			CSVExportInfo fileInfo = m_fileInfoMap.get (i + 1);
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

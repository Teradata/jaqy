/*
 * Copyright (c) 2021 Teradata
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;

/**
 * @author	Heng Yuan
 */
class ExcelExporter implements JaqyExporter
{
	private final static int WIDTH_ADJUST = 2;

	private final ExcelExporterOptions m_options;
	private Workbook m_wb;

	public ExcelExporter (Path file, ExcelExporterOptions options, JaqyInterpreter interpreter) throws IOException
	{
		m_options = options;

		String name = interpreter.getGlobals ().getName ();

		// convert Jaqy version in XX.XX.XX format to Excel compatible version
		// which is in XX.XXXX format.
		String version = interpreter.getGlobals ().getVersion ();
		String[] versions = version.split ("[.]");
		int sub1 = Integer.parseInt (versions[1]);
		int sub2 = Integer.parseInt (versions[2]);
		StringWriter sw = new StringWriter ();
		PrintWriter pw = new PrintWriter (sw);
		pw.printf ("%s.%02d%02d", versions[0], sub1, sub2);
		pw.flush ();
		pw.close ();
		m_wb = new Workbook (file.getOutputStream (), name, sw.toString ());
	}

	@Override
	public String getName ()
	{
		return "excel";
	}

	private void writeHeader (Worksheet ws, SchemaInfo schemaInfo) throws SQLException
	{
		int row = 0;
		int col = 0;

		int numColumns = schemaInfo.getNumColumns ();

		for (int i = 0; i < numColumns; ++i)
		{
			ws.value (row, col, schemaInfo.getLabel (i + 1));

			if (m_options.swap)
			{
				++row;
			}
			else
			{
				++col;
			}
		}

		if (m_options.swap)
		{
			ws.freezePane (1, 0);
		}
		else
		{
			ws.freezePane (0, 1);
		}
	}

	private int writeData (Worksheet ws, JaqyResultSet rs, SchemaInfo schemaInfo, int[] headerTypes, JaqyInterpreter interpreter) throws Exception
	{
		int numColumns = schemaInfo.getNumColumns ();

		JaqyHelper helper = rs.getHelper ();
		TypeHandler[] handlers = new TypeHandler[numColumns];
		for (int i = 0; i < numColumns; ++i)
		{
			handlers[i] = helper.getTypeHandler (rs, i + 1);
		}

		int row = 0;
		int col = 0;
		if (m_options.swap)
			++col;
		else
			++row;

		int max = Worksheet.MAX_ROWS - 1;
		if (m_options.swap)
			max = Worksheet.MAX_COLS - 1;

		int count = 0;
		while (rs.next () && count < max)
		{
			++count;
			for (int i = 0; i < numColumns; ++i)
			{
				Object obj = rs.getObject (i + 1);
				if (obj instanceof Number)
				{
					ws.value (row, col, (Number)obj);
				}
				else if (obj instanceof Date)
				{
					headerTypes[i] = Types.TIMESTAMP;
					ws.value (row, col, (Date)obj);
				}
				else if (obj instanceof LocalDate)
				{
					headerTypes[i] = Types.TIMESTAMP;
					ws.value (row, col, (LocalDate)obj);
				}
				else if (obj instanceof LocalDateTime)
				{
					headerTypes[i] = Types.TIMESTAMP;
					ws.value (row, col, (LocalDateTime)obj);
				}
				else if (obj instanceof Boolean)
				{
					ws.value (row, col, (Boolean)obj);
				}
				else if (obj != null)
				{
					ws.value (row, col, handlers[i].getString (rs, i + 1, interpreter));
				}

				if (m_options.swap)
				{
					++row;
				}
				else
				{
					++col;
				}
			}

			if (m_options.swap)
			{
				++col;
				row = 0;
			}
			else
			{
				++row;
				col = 0;
			}
		}
		return count;
	}

	private void writeFormat (Worksheet ws, SchemaInfo schemaInfo, int count, int[] headerTypes) throws SQLException
	{
		int rowMin = 0;
		int colMin = 0;
		int rowMax = 0;
		int colMax = 0;

		if (m_options.swap)
		{
			colMin = 1;
			colMax = count + 1;
		}
		else
		{
			rowMin = 1;
			rowMax = count + 1;
		}

		int numColumns = headerTypes.length;
		for (int i = 0; i < numColumns; ++i)
		{
			if (m_options.swap)
			{
				rowMin = i;
				rowMax = i;
			}
			else
			{
				colMin = i;
				colMax = i;
			}

			if (headerTypes[i] == Types.TIMESTAMP)
			{
				String format;
				switch (schemaInfo.getType (i + 1))
				{
					case Types.DATE:
					{
						format = "yyyy-MM-dd";
						break;
					}
					case Types.TIME:
					case Types.TIME_WITH_TIMEZONE:
					{
						format = "hh:mm:ss";
						break;
					}
					default:
					{
						format = "yyyy-MM-dd hh:mm:ss";
						break;
					}
				}
				ws.range (rowMin, colMin, rowMax, colMax).style ().format (format).set ();
				if (!m_options.swap)
				{
					ws.width (i, format.length () + WIDTH_ADJUST);
				}
			}
			else
			{
				int type = schemaInfo.getType (i + 1);
				if (type == Types.DECIMAL ||
					type == Types.NUMERIC)
				{
					int scale = schemaInfo.getScale (i + 1);
					if (scale > 0)
					{
						StringBuilder builder = new StringBuilder (scale + 2);
						builder.append ("0.");
						for (int j = 0; j < scale; ++j)
						{
							builder.append ('0');
						}
						ws.range (rowMin, colMin, rowMax, colMax).style ().format (builder.toString ()).set ();
						if (!m_options.swap)
						{
							int precision = schemaInfo.getPrecision (i + 1);
							ws.width (i, precision + WIDTH_ADJUST);
						}
					}
				}
			}
		}

		ws.setFitToPage (Boolean.TRUE);
		ws.fitToWidth ((short)2);
		ws.fitToHeight ((short)999);
	}

	@Override
	public long export (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		if (m_options.sheetName == null)
		{
			m_options.sheetName = "sheet 1";
		}
		Worksheet ws = m_wb.newWorksheet (m_options.sheetName);

		JaqyHelper helper = rs.getHelper ();
		SchemaInfo schemaInfo = ResultSetMetaDataUtils.getColumnInfo (rs.getMetaData (), helper);

		int numColumns = schemaInfo.getNumColumns ();
		int[] headerTypes = new int[numColumns];

		writeHeader (ws, schemaInfo);
		int count = writeData (ws, rs, schemaInfo, headerTypes, interpreter);
		writeFormat (ws, schemaInfo, count, headerTypes);

		m_wb.finish ();
		m_wb = null;
		return count;
	}

	@Override
	public void close ()
	{
		try
		{
			if (m_wb != null)
			{
				m_wb.finish ();
			}
		}
		catch (Exception ex)
		{
		}
	}
}

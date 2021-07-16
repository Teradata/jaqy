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
package com.teradata.jaqy.importer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;

import org.dhatim.fastexcel.reader.*;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePath;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
public class ExcelImporter implements JaqyImporter
{
	private final Path m_file;
	private final ExcelImporterOptions m_options;
	private int[] m_exps;
	private ReadableWorkbook m_wb;
	private Sheet m_ws;
	private Iterator<Row> m_rowIter;
	private Row m_row;
	private String m_headers[];
	private SchemaInfo m_schemaInfo;

	public ExcelImporter (Path file, ExcelImporterOptions options) throws IOException
	{
		m_file = file;
		m_options = options;
		openFile ();
	}

	private void openFile () throws IOException
	{
		try
		{
			if (m_file instanceof FilePath)
			{
				m_wb = new ReadableWorkbook (((FilePath)m_file).getFile ());
			}
			else
			{
				try (InputStream is = m_file.getInputStream ())
				{
					m_wb = new ReadableWorkbook (is);
				}
			}
			try
			{
				if (m_options.sheetName != null)
					m_ws = m_wb.findSheet (m_options.sheetName).get ();
				else
					m_ws = m_wb.getSheet (m_options.sheetId).get ();
			}
			catch (Exception ex)
			{
				if (m_options.sheetName != null)
					throw new IOException ("Invalid worksheet name: " + m_options.sheetName);
				else
					throw new IOException ("Invalid worksheet index: " + m_options.sheetId);
			}
			m_rowIter = m_ws.openStream ().iterator ();

			if (!m_rowIter.hasNext ())
			{
				m_wb.close ();
				throw new IOException ("Missing data in the sheet.");
			}
			if (m_options.header)
			{
				Row headerRow = m_rowIter.next ();
				int numCols = headerRow.getCellCount ();
				m_headers = new String[numCols];
				for (int i = 0; i < numCols; ++i)
				{
					m_headers[i] = headerRow.getCellText (i);
					if (m_headers[i] == null ||
						m_headers[i].isEmpty ())
					{
						throw new IOException ("Empty header column: " + i);
					}
				}
			}
			else
			{
				m_headers = null;
			}
		}
		catch (Exception ex)
		{
			if (m_wb != null)
			{
				m_wb.close ();
				m_wb = null;
			}
			throw ex;
		}
	}

	@Override
	public void close () throws IOException
	{
		if (m_wb != null)
		{
			m_wb.close ();
			m_wb = null;
		}
	}

	@Override
	public String getName ()
	{
		return "excel";
	}

	@Override
	public SchemaInfo getSchema () throws Exception
	{
		if (m_schemaInfo != null)
		{
			return m_schemaInfo;
		}
		m_schemaInfo = ExcelImporterUtils.getSchemaInfo (m_headers, m_rowIter, m_options);
		m_wb.close ();
		openFile ();
		return m_schemaInfo;
	}

	@Override
	public boolean next () throws Exception
	{
		if (m_rowIter.hasNext ())
		{
			m_row = m_rowIter.next ();
			// The code below is to avoid the situation when the last row
			// has nothing.
			if (!m_row.hasCell (0) || m_row.getCell (0).getText ().isEmpty ())
			{
				// Well, we do not have any header row, so having the first
				// cell being null means that we cannot do any inputs.
				if (m_headers == null)
				{
					return false;
				}
				for (int i = 1; i < m_headers.length; ++i)
				{
					if (m_row.hasCell (i) && m_row.getCell (i).getText ().length () > 0)
						return true;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void setParameters (String[] exps)
	{
		if (exps == null)
		{
			m_exps = null;
			return;
		}
		else
		{
			m_exps = new int[exps.length];
			int i = 0;
			for (String name : exps)
			{
				if (m_headers != null)
				{
					int index = -1;
					for (int j = 0; j < m_headers.length; ++j)
					{
						if (name.equalsIgnoreCase (m_headers[j]))
						{
							index = j;
							break;
						}
					}
					if (index < 0)
					{
						throw new IllegalArgumentException ("field not found: " + name);
					}
					m_exps[i] = index;
				}
				else if (name.startsWith ("col"))
				{
					String str = name.substring (3);
					int index = -1;
					try
					{
						index = Integer.valueOf (str) - 1;
					}
					catch (Exception ex)
					{
					}
					if (index < 0)
						throw new IllegalArgumentException ("Invalid column name: " + name);
					m_exps[i] = index;
				}
				else
				{
					throw new IllegalArgumentException ("Invalid column name: " + name);
				}

				++i;
			}
		}
	}

	private int getIndex (int index)
	{
		return m_exps == null ? index : m_exps[index];
	}

	@Override
	public Object importColumn (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception
	{
		JaqyHelper	helper = stmt.getHelper();
		int 		index = getIndex (column - 1);
		Object 		value = null;

		try
		{
			Cell cell = m_row.getCell (index);
			if (cell.getText ().isEmpty ())
			{
				value = null;
			}
			CellType cellType = cell.getType ();
			switch (cellType)
			{
				case NUMBER:
				{
					if (paramInfo.type == Types.DATE ||
						paramInfo.type == Types.TIME ||
						paramInfo.type == Types.TIME_WITH_TIMEZONE ||
						paramInfo.type == Types.TIMESTAMP ||
						paramInfo.type == Types.TIMESTAMP_WITH_TIMEZONE)
					{
						value = cell.asDate ();
					}
					else
					{
						value = cell.asNumber ();
					}
					break;
				}
				case STRING:
				case FORMULA:
				case ERROR:
				{
					value = cell.getText ();
					break;
				}
				case BOOLEAN:
				{
					if (paramInfo.type == Types.BOOLEAN)
					{
						value = cell.asBoolean ();
					}
					else if (TypesUtils.isNumber (paramInfo.type))
					{
						value = (cell.asBoolean () ? 1 : 0);
					}
					else
					{
						value = cell.getText ();
					}
					break;
				}
				case EMPTY:
				{
					value = null;
					break;
				}
			}
		}
		catch (Exception ex)
		{
		}
		if (value == null)
		{
			helper.setCSVNull (stmt, column, paramInfo, interpreter);
		}
		else
		{
			helper.setCSVObject (stmt, column, paramInfo, value, freeList, interpreter);
		}

		return null;
	}
}

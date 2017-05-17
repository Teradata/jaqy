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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.typeprinter.TypePrinter;
import com.teradata.jaqy.typeprinter.TypePrinterRegistry;
import com.teradata.jaqy.utils.ResultSetUtils;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
class TablePrinter implements JaqyPrinter
{
	private boolean m_autoShrink;
	private int m_scanThreshold;
	private int m_columnThreshold;
	private int m_maxColumnSize;

	public TablePrinter (boolean autoShrink, int scanThreshold, int columnThreshold, int maxColumnSize)
	{
		m_autoShrink = autoShrink;
		m_scanThreshold = scanThreshold;
		m_columnThreshold = columnThreshold;
		m_maxColumnSize = maxColumnSize;
	}

	public long print (JaqyResultSet rs, Globals globals, Display display, PrintWriter pw) throws SQLException
	{
		JaqyResultSetMetaData metaData = rs.getMetaData ();
		int columns = metaData.getColumnCount ();

		boolean[] leftAligns = new boolean[columns];
		int[] widths = new int[columns];
		TypePrinter[] printers = new TypePrinter[columns];

		for (int i = 0; i < columns; ++i)
		{
			int columnIndex = i + 1;
			int type = metaData.getColumnType (columnIndex);
			leftAligns[i] = isLeftAlign (type);

			printers[i] = TypePrinterRegistry.getTypePrinter (type);
		}

		for (int i = 0; i < columns; ++i)
		{
			widths[i] = ResultSetUtils.getDisplayWidth (rs, i + 1);
			if (widths[i] > m_maxColumnSize)
				widths[i] = m_maxColumnSize;
		}

		if (m_autoShrink && rs.getType () != ResultSet.TYPE_FORWARD_ONLY)
		{
			shrink (columns, widths, rs, metaData);
		}

		for (int i = 0; i < columns; ++i)
		{
			int columnIndex = i + 1;

			// Print the title
			String title = metaData.getColumnLabel (columnIndex);
			if (widths[i] < title.length ())
				widths[i] = title.length ();
			if (i > 0)
				pw.print (' ');
			StringUtils.print (pw, title, widths[i], leftAligns[i], i < (columns - 1));
		}
		pw.println ();

		// print the dashes
		for (int i = 0; i < columns; ++i)
		{
			if (i > 0)
				pw.print (' ');
			StringUtils.printRepeat (pw, '-', widths[i]);
		}
		pw.println ();

		long count = 0;
		while (rs.next ())
		{
			++count;
			for (int i = 0; i < columns; ++i)
			{
				if (i > 0)
					pw.print (' ');
				printers[i].print (pw, rs, i + 1, widths[i], leftAligns[i], i < (columns - 1));
			}
			pw.println ();
		}
		return count;
	}

	private boolean isLeftAlign (int type)
	{
		switch (type)
		{
			case Types.DECIMAL:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.NUMERIC:
				return false;
			default:
				return true;
		}
	}

	private void shrink (int columns, int[] widths, JaqyResultSet rs, JaqyResultSetMetaData metaData)
	{
		boolean[] skip = new boolean[columns];
		boolean[] binCol = new boolean[columns];
		int skipCount = 0;
		for (int i = 0; i < columns; ++i)
		{
			if (widths[i] <= m_columnThreshold)
			{
				skip[i] = false;
				++skipCount;
			}
			else
			{
				widths[i] = 1;
			}
			int colType = 0;
			try
			{
				colType = metaData.getColumnType (i + 1);
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			if (colType == Types.BINARY || colType == Types.VARBINARY)
				binCol[i] = true;
		}
		// all columns are pretty small.
		if (skipCount == columns)
			return;

		try
		{
			int lineCount = 0;
			while (rs.next () && lineCount < m_scanThreshold)
			{
				for (int i = 0; i < columns; ++i)
				{
					if (skip[i])
						continue;
					int w = 0;
					if (binCol[i])
					{
						byte[] bytes = rs.getBytes (i + 1);
						if (bytes != null)
							w = bytes.length * 2;
					}
					else
					{
						String str = rs.getString (i + 1);
						if (str != null)
							w = str.length ();
					}
					widths[i] = Math.max (widths[i], w);
				}
				++lineCount;
			}
			rs.beforeFirst ();
		}
		catch (SQLException ex)
		{
		}
	}

	@Override
	public String getName ()
	{
		return "table";
	}

	public boolean isAutoShrink ()
	{
		return m_autoShrink;
	}

	public void setAutoShrink (boolean autoShrink)
	{
		m_autoShrink = autoShrink;
	}

	public int getScanThreshold ()
	{
		return m_scanThreshold;
	}

	public void setScanThreshold (int scanThreshold)
	{
		m_scanThreshold = scanThreshold;
	}

	public int getColumnThreshold ()
	{
		return m_columnThreshold;
	}

	public void setColumnThreshold (int columnThreshold)
	{
		m_columnThreshold = columnThreshold;
	}
}

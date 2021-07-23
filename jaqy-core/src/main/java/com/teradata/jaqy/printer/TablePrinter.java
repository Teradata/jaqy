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
import java.sql.ResultSet;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.ResultSetUtils;
import com.teradata.jaqy.utils.StringUtils;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
class TablePrinter implements JaqyPrinter
{
	private boolean m_border;
	private boolean m_autoShrink;
	private long m_scanThreshold;
	private int m_columnThreshold;
	private int m_maxColumnSize;

	public TablePrinter (boolean border, boolean autoShrink, long scanThreshold, int columnThreshold, int maxColumnSize)
	{
		m_border = border;
		m_autoShrink = autoShrink;
		m_scanThreshold = scanThreshold;
		m_columnThreshold = columnThreshold;
		m_maxColumnSize = maxColumnSize;
	}

	private void printBorder (PrintWriter pw, int[] widths)
	{
		pw.print ("+-");
		// print the dashes
		for (int i = 0; i < widths.length; ++i)
		{
			if (i > 0)
				pw.print ("-+-");
			StringUtils.printRepeat (pw, '-', widths[i]);
		}
		pw.print ("-+");
		pw.println ();
	}

	private void printDash (PrintWriter pw, int[] widths)
	{
		for (int i = 0; i < widths.length; ++i)
		{
			if (i > 0)
				pw.print (' ');
			StringUtils.printRepeat (pw, '-', widths[i]);
		}
		pw.println ();
	}

	@Override
	public long print (JaqyResultSet rs, PrintWriter pw, long limit, JaqyInterpreter interpreter) throws Exception
	{
		JaqyHelper helper = rs.getHelper ();
		// If the ResultSet is forward only, make an in-memory which allows
		// rewind operation.
		if (m_autoShrink && rs.getType () == ResultSet.TYPE_FORWARD_ONLY)
		{
			JaqyResultSet newRS = ResultSetUtils.copyResultSet (rs, limit, interpreter);
			rs.close ();
			rs = newRS;
		}

		JaqyResultSetMetaData metaData = rs.getMetaData ();
		int columns = metaData.getColumnCount ();

		boolean[] leftAligns = new boolean[columns];
		int[] widths = new int[columns];
		TypeHandler[] handlers = new TypeHandler[columns];

		for (int i = 0; i < columns; ++i)
		{
			int columnIndex = i + 1;
			int type = metaData.getColumnType (columnIndex);
			leftAligns[i] = isLeftAlign (type);

			handlers[i] = helper.getTypeHandler (rs, i + 1);
		}

		if (m_autoShrink)
		{
			//
			// If we use auto shrink, we determine the maximum width of a column
			// by scanning all the rows and cols.
			//
			for (int i = 0; i < columns; ++i)
			{
				widths[i] = 0;

				int dispSize = ResultSetUtils.getDisplayWidth (rs, i + 1);
				if (dispSize <= m_columnThreshold)
					widths[i] = dispSize;
			}
			shrink (columns, widths, rs, handlers, limit, interpreter);
		}
		else
		{
			//
			// Without using auto shrink, we rely on the display size reported
			// by the database.  However, a lot of them report values that are
			// simply lies.
			//
			for (int i = 0; i < columns; ++i)
			{
				widths[i] = ResultSetUtils.getDisplayWidth (rs, i + 1);
				if (widths[i] > m_maxColumnSize)
					widths[i] = m_maxColumnSize;
			}
		}

		// Make sure the title length is considered as well.
		for (int i = 0; i < columns; ++i)
		{
			String title = metaData.getColumnLabel (i + 1);
			if (widths[i] < title.length ())
				widths[i] = title.length ();
		}

		if (m_border)
			printBorder (pw, widths);

		// Print the title
		if (m_border)
			pw.print ("| ");
		for (int i = 0; i < columns; ++i)
		{
			String title = metaData.getColumnLabel (i + 1);
			if (i > 0)
			{
				if (m_border)
					pw.print (" | ");
				else
					pw.print (' ');
			}
			StringUtils.print (pw, title, widths[i], leftAligns[i], (i < (columns - 1)) || m_border);
		}
		if (m_border)
			pw.print (" |");
		pw.println ();

		// print the dashes
		if (m_border)
			printBorder (pw, widths);
		else
			printDash (pw, widths);

		long count = 0;
		if (limit == 0)
			limit = Long.MAX_VALUE;
		while (rs.next () && count < limit)
		{
			++count;
			if (m_border)
				pw.print ("| ");
			for (int i = 0; i < columns; ++i)
			{
				if (i > 0)
				{
					if (m_border)
						pw.print (" | ");
					else
						pw.print (' ');
				}
				StringUtils.print (pw, handlers[i].getString (rs, i + 1, interpreter), widths[i], leftAligns[i], (i < (columns - 1)) || m_border);
			}
			if (m_border)
				pw.print (" |");
			pw.println ();
		}

		if (m_border)
			printBorder (pw, widths);

		return count;
	}

	private boolean isLeftAlign (int type)
	{
		return !TypesUtils.isNumber (type);
	}

	private void shrink (int columns, int[] widths, JaqyResultSet rs, TypeHandler[] handlers, long limit, JaqyInterpreter interpreter) throws Exception
	{
		boolean[] skipShrink = new boolean[columns];
		boolean doScan = false;
		for (int i = 0; i < columns; ++i)
		{
			skipShrink[i] = (widths[i] != 0);
			if (!skipShrink[i])
				doScan = true;
		}
		if (!doScan)
			return;
		long threshold = m_scanThreshold;
		if (limit != 0 && threshold > limit)
			threshold = limit;

		long lineCount = 0;
		while (rs.next () && lineCount < threshold)
		{
			for (int i = 0; i < columns; ++i)
			{
				if (skipShrink[i])
					continue;
				int len = handlers[i].getLength (rs, i + 1, interpreter);
				if (len == -1)
				{
					len = 4;
				}
				widths[i] = Math.max (widths[i], len);
			}
			++lineCount;
		}
		rs.beforeFirst ();
	}

	@Override
	public String getName ()
	{
		return "table";
	}

	@Override
	public boolean isForwardOnly ()
	{
		return false;
	}
}

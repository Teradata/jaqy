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
package com.teradata.jaqy.utils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Comparator;

import com.teradata.jaqy.JaqyException;

/**
 * @author	Heng Yuan
 */
public class RSSorter implements Comparator<Object[]>
{
	private final SortInfo[] m_sortInfos;

	public RSSorter (SortInfo[] sortInfo)
	{
		m_sortInfos = sortInfo;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public int compare (Object[] row1, Object[] row2)
	{
		for (SortInfo sortInfo : m_sortInfos)
		{
			Object o1 = row1[sortInfo.column - 1];
			Object o2 = row2[sortInfo.column - 1];
			if (o1 == o2)
			{
				continue;
			}
			int v;
			if (o1 == null)
			{
				v = sortInfo.nullLow ? -1 : 1;
			}
			else if (o2 == null)
			{
				v = sortInfo.nullLow ? 1 : -1;
			}
			else if (o1 instanceof Comparable &&
					 o2 instanceof Comparable)
			{
				v = ((Comparable<Object>)o1).compareTo (o2);
			}
			else if (o1 instanceof byte[] &&
					 o2 instanceof byte[])
			{
				v = ByteArrayUtils.compare ((byte[])o1, (byte[])o2);
			}
			else
			{
				// we can only sort two objects using their string representation.
				v = o1.toString ().compareTo (o2.toString ());
			}
			if (v != 0)
			{
				return sortInfo.asc ? v : -v;
			}
		}
		return 0;
	}

	public static Comparator<Object[]> createSorter (ResultSetMetaData rsmd, SortInfo[] sortInfos) throws SQLException
	{
		int columnCount = rsmd.getColumnCount ();
		for (SortInfo sortInfo : sortInfos)
		{
			if (sortInfo.column <= 0)
			{
				for (int i = 1; i <= columnCount; ++i)
				{
					String name = rsmd.getColumnName (i);
					if (name.equalsIgnoreCase (sortInfo.name))
					{
						sortInfo.column = i;
						break;
					}
				}
				if (sortInfo.column <= 0)
				{
					throw new JaqyException ("unknown sort column: " + sortInfo.name);
				}
			}
		}
		return new RSSorter (sortInfos);
	}
}

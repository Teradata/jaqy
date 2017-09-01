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
package com.teradata.jaqy;

import java.util.ArrayList;

/**
 * This class is used to store and display internally generated data.
 *
 * @author	Heng Yuan
 */
public class PropertyTable
{
	private String[] m_titles;
	private ArrayList<Object[]> m_rows = new ArrayList<Object[]> ();
	private int[] m_lengths;

	public PropertyTable (String[] titles)
	{
		m_titles = titles;
		m_lengths = new int[titles.length];
		for (int i = 0; i < titles.length; ++i)
		{
			m_lengths[i] = m_titles[i].length ();
		}
	}

	public void addRow (String[] row)
	{
		if (row.length != m_titles.length)
			throw new IllegalArgumentException ("Invalid number of columns.");
		for (int i = 0; i < row.length; ++i)
		{
			int l = (row[i] == null) ? 0 : row[i].length ();
			if (m_lengths[i] < l)
				m_lengths[i] = l;
		}
		m_rows.add (row);
	}

	public String[] getTitles ()
	{
		return m_titles;
	}

	public ArrayList<Object[]> getRows ()
	{
		return m_rows;
	}

	public int[] getLengths ()
	{
		return m_lengths;
	}
}

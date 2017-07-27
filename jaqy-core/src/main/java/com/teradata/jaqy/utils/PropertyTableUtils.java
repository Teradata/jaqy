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
package com.teradata.jaqy.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import com.teradata.jaqy.PropertyTable;

/**
 * @author	Heng Yuan
 */
public class PropertyTableUtils
{
	public static PropertyTable createPropertyTable (Map<String,String> map, String[] titles)
	{
		PropertyTable pt = new PropertyTable (titles);
		for (Map.Entry<String,String> entry : map.entrySet ())
		{
			pt.addRow (new String[]{ entry.getKey (), entry.getValue () });
		}
		return pt;
	}

	public static PropertyTable createPropertyTable (ResultSet rs) throws SQLException
	{
		ResultSetMetaData meta = rs.getMetaData ();
		int numCols = meta.getColumnCount ();
		String[] titles = new String[numCols];
		for (int i = 0; i < numCols; ++i)
			titles[i] = meta.getColumnName (i + 1);
		PropertyTable pt = new PropertyTable (titles);
		while (rs.next ())
		{
			String[] row = new String[numCols];
			for (int i = 0; i < numCols; ++i)
				row[i] = rs.getString (i + 1);
			pt.addRow (row);
		}
		return pt;
	}
}

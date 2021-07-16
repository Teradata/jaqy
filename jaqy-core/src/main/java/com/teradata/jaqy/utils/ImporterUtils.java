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
package com.teradata.jaqy.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author	Heng Yuan
 */
public class ImporterUtils
{
	public static String[] getHeaders (Map<String, Integer> headerMap)
	{
		if (headerMap == null ||
			headerMap.size () == 0)
		{
			return null;
		}
		TreeMap<Integer, String> map2 = new TreeMap<Integer, String> ();
		for (Map.Entry<String, Integer> entry : headerMap.entrySet ())
		{
			map2.put (entry.getValue (), entry.getKey ());
		}
		Integer largest = map2.lastKey ();
		String[] headers = new String[largest + 1];
		for (int i = 0; i < headers.length; ++i)
		{
			headers[i] = map2.get (i);
		}
		return headers;
	}

	public static int[] getParameterIndexes (String[] headers, String[] exps)
	{
		if (exps == null)
		{
			return null;
		}
		else
		{
			int[] indexes = new int[exps.length];
			int i = 0;
			for (String name : exps)
			{
				if (headers != null)
				{
					int index = -1;
					for (int j = 0; j < headers.length; ++j)
					{
						if (name.equalsIgnoreCase (headers[j]))
						{
							index = j;
							break;
						}
					}
					if (index < 0)
					{
						throw new IllegalArgumentException ("Invalid column name: " + name);
					}
					indexes[i] = index;
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
					{
						throw new IllegalArgumentException ("Invalid column name: " + name);
					}
					indexes[i] = index;
				}
				else
				{
					throw new IllegalArgumentException ("Invalid column name: " + name);
				}

				++i;
			}
			return indexes;
		}
	}
}

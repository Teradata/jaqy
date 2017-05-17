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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.teradata.jaqy.connection.JaqyConnection;

/**
 * @author	Heng Yuan
 */
public class SQLUtils
{
	static String[] parseCatalogSchemaTablePattern (String quote, String separator, String[] args)
	{
		ArrayList<String> strings = new ArrayList<String> ();

		char quoteChar = 0;
		if (quote != null &&
			quote.length () > 0)
			quoteChar = quote.charAt (0);

		char separatorChar = 0;
		if (separator != null &&
			separator.length () > 0)
			separatorChar = separator.charAt (0);

		for (String arg : args)
		{
			arg = arg.trim ();
			if (arg.length () == 0)
				continue;

			StringBuffer buffer = new StringBuffer ();
			boolean start = true;
			boolean inQuote = false;
			char[] chars = arg.toCharArray ();
			for (int i = 0; i < chars.length; ++i)
			{
				if (chars[i] == quoteChar)
				{
					if (start)
					{
						inQuote = true;
						start = false;
					}
					else if (!inQuote)
					{
						return null;
					}
					else
					{
						if (i < (chars.length - 1) &&
							chars[i + 1] == quoteChar)
						{
							// this is a quote escape
							buffer.append (quoteChar);
							++i;
						}
						else
						{
							inQuote = false;
							strings.add (buffer.toString ());
							buffer.setLength (0);
						}
					}
				}
				else if (chars[i] == separatorChar)
				{
					strings.add (buffer.toString ());
					buffer.setLength (0);
				}
				else
				{
					buffer.append (chars[i]);
				}
			}
		}
		return strings.toArray (new String[strings.size ()]);
	}

	public static String[] getSchemaOptions (JaqyConnection conn, String[] args) throws SQLException
	{
		DatabaseMetaData metaData = conn.getMetaData ();

		String separator = metaData.getCatalogSeparator ();
		String quote = metaData.getIdentifierQuoteString ();

		String[] options = parseCatalogSchemaTablePattern (quote, separator, args);

		if (options.length == 1)
		{
			String catalog = null;
			try
			{
				catalog = conn.getCatalog ();
			}
			catch (SQLException ex)
			{
			}
			String[] newOptions = new String[2];
			newOptions[0] = catalog;
			newOptions[1] = options[0];
			options = newOptions;
		}
		else if (options.length != 2)
		{
			return null;
		}

		return options;
	}
}

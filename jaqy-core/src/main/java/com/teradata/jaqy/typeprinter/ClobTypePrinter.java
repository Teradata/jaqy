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
package com.teradata.jaqy.typeprinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
class ClobTypePrinter implements TypePrinter
{
	private final static int BUFFER_SIZE = 4096;
	private final static TypePrinter s_instance = new ClobTypePrinter ();

	public static TypePrinter getInstance ()
	{
		return s_instance;
	}

	private ClobTypePrinter ()
	{
	}

	@Override
	public void print (PrintWriter pw, JaqyResultSet rs, int columnIndex, int width, boolean leftAlign, boolean pad) throws SQLException
	{
		Clob clob = (Clob) rs.getObject (columnIndex);
		String value = null;
		if (clob != null)
		{
			long length = clob.length ();
			int buffSize = (length > BUFFER_SIZE ? BUFFER_SIZE : (int) length);
			if (buffSize > 0)
			{
				Reader reader = clob.getCharacterStream ();
				StringBuilder builder = new StringBuilder ();
				char[] buf = new char[buffSize];
				while (length > 0)
				{
					int len = (length > BUFFER_SIZE ? BUFFER_SIZE : (int) length);
					try
					{
						len = reader.read (buf, 0, len);
						builder.append (buf, 0, len);
						length -= len;
					}
					catch (IOException ex)
					{
						throw new SQLException (ex);
					}
				}
				try
				{
					reader.close ();
				}
				catch (IOException ex)
				{
					throw new SQLException (ex);
				}
				value = builder.toString ();
			}
			else
			{
				value = "";
			}
		}
		StringUtils.print (pw, value, width, leftAlign, pad);
	}
}

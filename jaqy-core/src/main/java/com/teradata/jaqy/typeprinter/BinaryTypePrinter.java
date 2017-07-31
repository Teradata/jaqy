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
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.SQLException;

import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
class BinaryTypePrinter implements TypePrinter
{
	private static int MAX_LEN = 64000;
	private final static TypePrinter s_instance = new BinaryTypePrinter ();

	public static TypePrinter getInstance ()
	{
		return s_instance;
	}

	private BinaryTypePrinter ()
	{
	}

	@Override
	public void print (PrintWriter pw, JaqyResultSet rs, int columnIndex, int width, boolean leftAlign, boolean pad) throws SQLException
	{
		Object obj = rs.getObject (columnIndex);
		String value;
		if (obj == null)
			value = null;
		else if (obj instanceof byte[])
		{
			value = StringUtils.getHexString ((byte[])obj);
		}
		else if (obj instanceof Blob)
		{
			Blob blob = ((Blob)obj);
			byte[] bytes = blob.getBytes (1, (int)blob.length ());
			value = StringUtils.getHexString ((byte[])bytes);
		}
		else
		{
			InputStream is = rs.getBinaryStream (columnIndex);
			byte[] bytes = new byte[4096];
			int len;
			try
			{
				value = "";
				while ((len = is.read (bytes)) > 0)
				{
					value += StringUtils.getHexString (bytes, 0, len);
					if (value.length () > width ||
						value.length () > MAX_LEN)
					{
						int smaller = Math.min (width, MAX_LEN);
						value = value.substring (0, smaller);
						break;
					}
				}
			}
			catch (IOException ex)
			{
				value = ex.getMessage ();
			}
		}
		StringUtils.print (pw, value, width, leftAlign, pad);
	}
}

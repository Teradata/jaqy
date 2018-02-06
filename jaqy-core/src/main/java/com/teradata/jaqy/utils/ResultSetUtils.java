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
package com.teradata.jaqy.utils;

import java.sql.*;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.resultset.*;

/**
 * @author	Heng Yuan
 */
public class ResultSetUtils
{
	public static int getDisplayWidth (JaqyResultSet rs, int column) throws SQLException
	{
		JaqyResultSetMetaData meta = rs.getMetaData ();
		// some JDBC drivers (notably SQLite JDBC does not have
		// the correct display size settings.  So here we force
		// the correct calculation.
		int displaySize = meta.getColumnDisplaySize (column);
		int type = meta.getColumnType (column);
		int computeSize = displaySize;
		switch (type)
		{
			case Types.INTEGER:
			{
				computeSize = 10;
				if (meta.isSigned (column))
					++computeSize;
				break;
			}
			case Types.REAL:
			{
				int precision = meta.getPrecision (column);
				if (precision <= 7)
				{
					// we are dealing a 4-byte real
					computeSize = precision + 2;
					if (meta.isSigned (column))
						++computeSize;
				}
				else if (precision <= 53)
				{
					computeSize = precision + 2;
					if (meta.isSigned (column))
						++computeSize;
				}
				break;
			}
			case Types.BINARY:
			case Types.VARBINARY:
			{
				int size = meta.getColumnDisplaySize (column);
				computeSize = size * 2;
				break;
			}
			default:
			{
				computeSize = displaySize;
				break;
			}
		}
		return Math.min (computeSize, displaySize);
	}

	public static String getResultSetType (int type)
	{
		switch (type)
		{
			case ResultSet.TYPE_FORWARD_ONLY:
				return "FORWARD_ONLY";
			case ResultSet.TYPE_SCROLL_INSENSITIVE:
				return "SCROLL_INSENSITIVE";
			case ResultSet.TYPE_SCROLL_SENSITIVE:
				return "SCROLL_SENSITIVE";
			default:
				return "Unknown";
		}
	}

	public static Clob copyClob (Clob clob, int threshold, char[] charBuffer) throws SQLException
	{
		if (clob.length () < threshold)
		{
			if (clob instanceof NClob)
				return new InMemNClob ((NClob)clob);
			return new InMemClob (clob);
		}
		if (clob instanceof NClob)
			return new FileNClob ((NClob)clob, charBuffer);
		return new FileClob (clob, charBuffer);
	}

	public static Blob copyBlob (Blob blob, int threshold, byte[] byteBuffer) throws SQLException
	{
		if (blob.length () < threshold)
			return new InMemBlob (blob);
		return new FileBlob (blob, byteBuffer);
	}

	public static SQLXML copySQLXML (SQLXML xml, int threshold, char[] charBuffer) throws SQLException
	{
		return new FileSQLXML (xml, charBuffer);
	}

	public static Object copyIfNecessary (Object o, JaqyInterpreter interpreter) throws SQLException
	{
		if (o == null)
			return o;
		int threshold = interpreter.getCopyThreshold ();
		if (o instanceof Clob)
			return copyClob ((Clob)o, threshold, interpreter.getCharBuffer ());
		if (o instanceof Blob)
			return copyBlob ((Blob)o, threshold, interpreter.getByteBuffer ());
		if (o instanceof SQLXML)
			return copySQLXML ((SQLXML)o, threshold, interpreter.getCharBuffer ());
		return o;
	}
}

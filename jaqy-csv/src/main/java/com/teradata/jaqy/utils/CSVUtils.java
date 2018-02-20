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

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.StringEscapeUtils;

import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.SchemaInfo;

/**
 * @author	Heng Yuan
 */
public class CSVUtils
{
	public final static int AUTO_STOP_MINIMUM = 1000;

	public static class ScanColumnType
	{
		boolean nullable;
		int type;
		int minLength;
		int maxLength;
		int precision;
		int scale;

		int notNullCount;
	}

	public static char getChar (String str)
	{
		String charStr = StringEscapeUtils.unescapeJava (str);
		if (charStr.length () != 1)
			return 0;
		return charStr.charAt (0);
	}

	public static CSVFormat getFormat (String format)
	{
		if ("default".equals (format))
			return CSVFormat.DEFAULT;
		else if ("excel".equals (format))
			return CSVFormat.EXCEL;
		else if ("rfc4180".equals (format))
			return CSVFormat.RFC4180;
		else if ("mysql".equals (format))
			return CSVFormat.MYSQL;
		else if ("tdf".equals (format))
			return CSVFormat.TDF;
		throw new IllegalArgumentException ("unknown csv format: " + format);
	}

	public static SchemaInfo getSchemaInfo (String[] headers, Iterator<CSVRecord> iterator, String[] naValues, boolean precise, long limit)
	{
		int count = -1;
		ScanColumnType[] columns = null;
		int rowCount = 0;
		boolean autoStop = false;
		if (limit < 0)
		{
			limit = Long.MAX_VALUE;
			autoStop = true;
		}
		else if (limit == 0)
			limit = Long.MAX_VALUE;
		boolean needScan;
		while (iterator.hasNext () && rowCount < limit)
		{
			CSVRecord record = iterator.next ();
			++rowCount;
			int size = record.size ();
			needScan = false;
			if (count == -1)
			{
				count = size;
				columns = new ScanColumnType[count];
				for (int i = 0; i < count; ++i)
				{
					columns[i] = new ScanColumnType ();
					columns[i].type = Types.NULL;
					columns[i].nullable = false;
					columns[i].minLength = Integer.MAX_VALUE;
					columns[i].maxLength = -1;
				}
				needScan = true;
			}
			for (int i = 0; i < count; ++i)
			{
				String s = record.get (i);
				boolean isNa = false;
				if (naValues != null)
				{
					for (String na : naValues)
					{
						if (s.equals (na))
						{
							isNa = true;
							break;
						}
					}
				}
				if (isNa)
				{
					columns[i].nullable = true;
				}
				else
				{
					int len = s.length ();
					if (columns[i].maxLength < len)
						columns[i].maxLength = len;
					if (columns[i].minLength > len)
						columns[i].minLength = len;

					if (columns[i].type == Types.NUMERIC ||
						columns[i].type == Types.NULL)
					{
						try
						{
							BigDecimal dec = new BigDecimal (s);
							int precision = dec.precision ();
							int scale = dec.scale ();
							// if precision is smaller than or equal to scale, then we have leading "0."
							if (precision <= scale)
								precision = scale + 1;
							if (columns[i].type == Types.NULL)
							{
								columns[i].type = Types.NUMERIC;
								columns[i].precision = precision;
								columns[i].scale = scale;
							}
							else
							{
								if (columns[i].scale != scale)
								{
									columns[i].scale = Integer.MAX_VALUE;
								}
								if (columns[i].precision < precision)
								{
									columns[i].precision = precision;
								}
							}
							++columns[i].notNullCount;
						}
						catch (Exception ex)
						{
							if (columns[i].minLength == columns[i].maxLength)
							{
								// Check if we are in a fixed char column.
								columns[i].type = Types.CHAR;
								++columns[i].notNullCount;
							}
							else
							{
								columns[i].type = Types.VARCHAR;
								// For varchar columns, we basically have to scan
								// all the rows to find the maximum string length.
								autoStop = false;
							}
						}
					}
					else if (columns[i].type == Types.CHAR)
					{
						if (columns[i].minLength == columns[i].maxLength)
							++columns[i].notNullCount;
						else
						{
							columns[i].type = Types.VARCHAR;
							// For varchar columns, we basically have to scan
							// all the rows to find the maximum string length.
							autoStop = false;
						}
					}
				}

				if (autoStop &&
					columns[i].notNullCount < AUTO_STOP_MINIMUM)
				{
					// For each number column, we basically need enough
					// confidence to say that additional scan is not
					// necessary.
					needScan = true;
				}
			}

			if (autoStop && !needScan)
			{
				// Automatically stop if we just have numbers.
				break;
			}
		}

		if (rowCount == 0)
			return null;

		FullColumnInfo[] columnInfos = new FullColumnInfo[count];
		for (int i = 0; i < count; ++i)
		{
			columnInfos[i] = new FullColumnInfo ();
			if (headers != null)
			{
				columnInfos[i].name = headers[i];
			}
			if (columnInfos[i].name == null ||
				columnInfos[i].name.trim ().length () == 0)
			{
				columnInfos[i].name = "col" + (i + 1);
			}
			columnInfos[i].label = columnInfos[i].name;

			columnInfos[i].nullable = columns[i].nullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;

			if (columns[i].type == Types.CHAR ||
				columns[i].type == Types.VARCHAR)
			{
				columnInfos[i].type = columns[i].type;
				columnInfos[i].precision = columns[i].maxLength;
			}
			else
			{
				columnInfos[i].precision = columns[i].precision;
				if (columns[i].scale == Integer.MAX_VALUE)
				{
					columnInfos[i].type = Types.DOUBLE;
					columnInfos[i].scale = 0;
				}
				else if (columns[i].scale <= 0 &&
				         columns[i].precision < 11)
				{
					columnInfos[i].type = Types.INTEGER;
					columnInfos[i].scale = 0;
				}
				else if (precise && columns[i].scale > 0)
				{
					columnInfos[i].type = Types.DECIMAL;
					columnInfos[i].scale = columns[i].scale;
				}
				else
				{
					columnInfos[i].type = Types.DOUBLE;
					columnInfos[i].scale = 0;
				}
			}
		}
		return new SchemaInfo (columnInfos);
	}
}

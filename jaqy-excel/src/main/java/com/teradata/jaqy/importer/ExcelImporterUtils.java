/*
 * Copyright (c) 2021 Heng Yuan
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
package com.teradata.jaqy.importer;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Iterator;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.Row;

import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
class ExcelImporterUtils
{
	public static class ScanColumnInfo
	{
		boolean nullable;
		int 	type;
		int 	minLength;
		int 	maxLength;
		int 	precision;
		int 	scale;

		boolean	ascii = true;
		boolean	varlength;

		boolean	first = true;

		BigDecimal	maxValue = BigDecimal.ZERO;
		BigDecimal	minValue = BigDecimal.ZERO;
	}

	private static SchemaInfo getSchemaInfo (String[] headers, ScanColumnInfo[] scanColumnInfos, ExcelImporterOptions option)
	{
		int numColumns = scanColumnInfos.length;
		FullColumnInfo[] columnInfos = new FullColumnInfo[numColumns];
		for (int i = 0; i < numColumns; ++i)
		{
			FullColumnInfo columnInfo = new FullColumnInfo ();
			columnInfos[i] = columnInfo;
			ScanColumnInfo scanColumnInfo = scanColumnInfos[i];
			int type = option.getType (i);

			if (headers != null &&
				i < headers.length)
			{
				columnInfo.name = headers[i];
			}
			if (columnInfo.name == null ||
				columnInfo.name.trim ().length () == 0)
			{
				columnInfo.name = "col" + (i + 1);
			}
			columnInfo.label = columnInfo.name;

			columnInfo.nullable = scanColumnInfo.nullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;

			if (scanColumnInfo.type == Types.CHAR)
			{
				if (scanColumnInfo.maxLength < 1)
				{
					scanColumnInfo.maxLength = 1;
					scanColumnInfo.varlength = true;
				}
				else if (scanColumnInfo.minLength != scanColumnInfo.maxLength)
				{
					scanColumnInfo.varlength = true;
				}

				if (scanColumnInfo.ascii)
				{
					columnInfo.type = scanColumnInfo.varlength ? Types.VARCHAR : Types.CHAR;
				}
				else
				{
					columnInfo.type = scanColumnInfo.varlength ? Types.NVARCHAR : Types.NCHAR;
				}
				columnInfo.precision = scanColumnInfo.maxLength;
			}
			else if (type != Types.NULL)
			{
				columnInfo.precision = 0;
				columnInfo.scale = 0;
				columnInfo.type = type;
			}
			else
			{
				columnInfo.precision = scanColumnInfo.precision;
				if (scanColumnInfo.scale == Integer.MAX_VALUE)
				{
					columnInfo.type = Types.DOUBLE;
					columnInfo.scale = 0;
				}
				else if (scanColumnInfo.scale <= 0 &&
				         scanColumnInfo.precision < 11 &&
				         scanColumnInfo.maxValue.compareTo (new BigDecimal (Integer.MAX_VALUE)) <= 0 &&
				         scanColumnInfo.minValue.compareTo (new BigDecimal (Integer.MIN_VALUE)) >= 0)
				{
					columnInfo.type = Types.INTEGER;
					columnInfo.scale = 0;
				}
				else if (scanColumnInfo.scale > 0)
				{
					columnInfo.type = Types.DECIMAL;
					columnInfo.scale = scanColumnInfo.scale;
				}
				else
				{
					columnInfo.type = Types.DOUBLE;
					columnInfo.scale = 0;
				}
			}
		}
		return new SchemaInfo (columnInfos);
	}

	private static ScanColumnInfo[] createColumnInfos (int numColumns)
	{
		ScanColumnInfo[] scanColumnInfos = new ScanColumnInfo[numColumns];
		for (int i = 0; i < numColumns; ++i)
		{
			scanColumnInfos[i] = new ScanColumnInfo ();
			scanColumnInfos[i].type = Types.NULL;
			scanColumnInfos[i].nullable = false;
			scanColumnInfos[i].minLength = Integer.MAX_VALUE;
			scanColumnInfos[i].maxLength = -1;
		}
		return scanColumnInfos;
	}

	public static SchemaInfo getSchemaInfo (String[] headers, Iterator<Row> iterator, ExcelImporterOptions options)
	{
		ScanColumnInfo[] scanColumnInfos = null;
		int rowCount = 0;
		int numColumns = -1;
		if (headers != null)
		{
			numColumns = headers.length;
		}
		while (iterator.hasNext ())
		{
			Row row = iterator.next ();
			++rowCount;
			int size = row.getCellCount ();
			if (numColumns == -1)
			{
				numColumns = size;
			}
			if (scanColumnInfos == null)
			{
				scanColumnInfos = createColumnInfos (numColumns);
			}

			for (int i = 0; i < numColumns; ++i)
			{
				ScanColumnInfo scanColumnInfo = scanColumnInfos[i];
				String text;
				Cell cell;
				try
				{
					cell = row.getCell (i);
					text = cell.getText ();
				}
				catch (Exception ex)
				{
					cell = null;
					text = "";
				}
				if (text.isEmpty ())
				{
					scanColumnInfo.nullable = true;
				}
				else
				{
					int len = text.length ();
					if (scanColumnInfo.maxLength < len)
						scanColumnInfo.maxLength = len;
					if (scanColumnInfo.minLength > len)
						scanColumnInfo.minLength = len;

					CellType cellType = cell.getType ();
					int dbType;
					if (cellType == CellType.NUMBER)
					{
						dbType = Types.DECIMAL;
					}
					else if (cellType == CellType.BOOLEAN)
					{
						dbType = Types.BOOLEAN;
					}
					else
					{
						dbType = Types.CHAR;
					}
					if (scanColumnInfo.type == Types.NULL)
					{
						scanColumnInfo.type = dbType;
					}
					else if (dbType != scanColumnInfo.type)
					{
						scanColumnInfo.type = Types.CHAR;
					}
					if (scanColumnInfo.type == Types.DECIMAL)
					{
						BigDecimal dec = cell.asNumber ();
						int precision = dec.precision ();
						int scale = dec.scale ();
						if (scanColumnInfo.maxValue.compareTo (dec) < 0)
						{
							scanColumnInfo.maxValue = dec;
						}
						else if (scanColumnInfo.minValue.compareTo (dec) > 0)
						{
							scanColumnInfo.minValue = dec;
						}
						// if precision is smaller than or equal to scale, then we have leading "0."
						if (precision <= scale)
							precision = scale + 1;

						if (scanColumnInfo.first)
						{
							scanColumnInfo.scale = scale;
							scanColumnInfo.first = false;
						}

						if (scanColumnInfo.scale != scale)
						{
							scanColumnInfo.scale = Integer.MAX_VALUE;
						}
						if (scanColumnInfo.precision < precision)
						{
							scanColumnInfo.precision = precision;
						}
					}
					else if (cellType == CellType.BOOLEAN)
					{
						if (scanColumnInfo.type == Types.NULL)
						{
							scanColumnInfo.type = Types.BOOLEAN;
						}
					}
					else
					{
						if (scanColumnInfo.ascii)
						{
							scanColumnInfo.ascii = StringUtils.isAscii (text);
						}
					}
				}
			}
		}

		if (rowCount == 0 || numColumns <= 0)
			return null;

		if (headers == null)
		{
			headers = new String[numColumns];
			for (int i = 0; i < numColumns; ++i)
			{
				headers[i] = "col" + (i + 1);
			}
		}
		return getSchemaInfo (headers, scanColumnInfos, options);
	}
}

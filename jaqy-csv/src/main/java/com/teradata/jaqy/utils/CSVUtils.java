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

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.SchemaInfo;

/**
 * @author  Heng Yuan
 */
public class CSVUtils
{
    public static Charset DEFAULT_CHARSET = Charset.forName ("utf-8");

    public final static int AUTO_STOP_MINIMUM = 1000;

    public static class ScanColumnInfo
    {
        boolean nullable;
        int     type;
        int     minLength;
        int     maxLength;
        int     precision;
        int     scale;

        int     notNullCount;
        boolean ascii = true;
        boolean varlength;

        BigDecimal  maxValue = BigDecimal.ZERO;
        BigDecimal  minValue = BigDecimal.ZERO;
    }

    public static char getChar (String str)
    {
        String charStr = StringEscapeUtils.unescapeJava (str);
        if (charStr.length () != 1)
            return 0;
        return charStr.charAt (0);
    }

    public static CSVFormat getDefaultFormat ()
    {
        // Instead of always using CRLF, use LF on Unix systems
        if (SystemUtils.IS_OS_UNIX)
        {
            return CSVFormat.DEFAULT.builder().setRecordSeparator ('\n').build ();
        }
        return CSVFormat.DEFAULT;
    }

    public static CSVFormat getFormat (String format)
    {
        if ("default".equals (format))
        {
            return getDefaultFormat ();
        }
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

    private static SchemaInfo getSchemaInfo (String[] headers, ScanColumnInfo[] scanColumnInfos, boolean precise)
    {
        int numColumns = scanColumnInfos.length;
        FullColumnInfo[] columnInfos = new FullColumnInfo[numColumns];
        for (int i = 0; i < numColumns; ++i)
        {
            FullColumnInfo columnInfo = new FullColumnInfo ();
            columnInfos[i] = columnInfo;
            ScanColumnInfo scanColumnInfo = scanColumnInfos[i];
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
                else if (precise && scanColumnInfo.scale > 0)
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

    public static SchemaInfo getSchemaInfo (String[] headers, Iterator<CSVRecord> iterator, String[] naValues, boolean precise, long limit)
    {
        int numColumns = -1;
        ScanColumnInfo[] scanColumnInfos = null;
        int rowCount = 0;
        boolean autoStop = false;
        if (limit < 0)
        {
            limit = Long.MAX_VALUE;
            autoStop = true;
        }
        else if (limit == 0)
        {
            limit = Long.MAX_VALUE;
        }
        boolean needScan;
        while (iterator.hasNext () && rowCount < limit)
        {
            CSVRecord record = iterator.next ();
            ++rowCount;
            int size = record.size ();
            needScan = false;
            if (numColumns == -1)
            {
                numColumns = size;
                scanColumnInfos = new ScanColumnInfo[numColumns];
                for (int i = 0; i < numColumns; ++i)
                {
                    scanColumnInfos[i] = new ScanColumnInfo ();
                    scanColumnInfos[i].type = Types.NULL;
                    scanColumnInfos[i].nullable = false;
                    scanColumnInfos[i].minLength = Integer.MAX_VALUE;
                    scanColumnInfos[i].maxLength = -1;
                }
                needScan = true;
            }
            for (int i = 0; i < numColumns; ++i)
            {
                ScanColumnInfo scanColumnInfo = scanColumnInfos[i];
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
                    scanColumnInfo.nullable = true;
                }
                else
                {
                    int len = s.length ();
                    if (scanColumnInfo.maxLength < len)
                        scanColumnInfo.maxLength = len;
                    if (scanColumnInfo.minLength > len)
                        scanColumnInfo.minLength = len;

                    if (scanColumnInfo.type == Types.NUMERIC ||
                        scanColumnInfo.type == Types.NULL)
                    {
                        try
                        {
                            BigDecimal dec = new BigDecimal (s);
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
                            if (scanColumnInfo.type == Types.NULL)
                            {
                                scanColumnInfo.type = Types.NUMERIC;
                                scanColumnInfo.precision = precision;
                                scanColumnInfo.scale = scale;
                            }
                            else
                            {
                                if (scanColumnInfo.scale != scale)
                                {
                                    scanColumnInfo.scale = Integer.MAX_VALUE;
                                }
                                if (scanColumnInfo.precision < precision)
                                {
                                    scanColumnInfo.precision = precision;
                                }
                            }
                            ++scanColumnInfo.notNullCount;
                        }
                        catch (Exception ex)
                        {
                            scanColumnInfo.ascii = StringUtils.isAscii (s);
                            scanColumnInfo.type = Types.CHAR;
                            if (scanColumnInfo.minLength == scanColumnInfo.maxLength)
                            {
                                // Check if we are in a fixed char column.
                                ++scanColumnInfo.notNullCount;
                            }
                            else
                            {
                                scanColumnInfo.varlength = true;
                                // For varchar columns, we basically have to scan
                                // all the rows to find the maximum string length.
                                autoStop = false;
                            }
                        }
                    }
                    else if (scanColumnInfo.type == Types.CHAR)
                    {
                        if (scanColumnInfo.ascii)
                        {
                            scanColumnInfo.ascii = StringUtils.isAscii (s);
                        }
                        if (scanColumnInfo.minLength == scanColumnInfo.maxLength)
                        {
                            ++scanColumnInfo.notNullCount;
                        }
                        else
                        {
                            scanColumnInfo.varlength = true;
                            // For varchar columns, we basically have to scan
                            // all the rows to find the maximum string length.
                            autoStop = false;
                        }
                    }
                }

                if (autoStop &&
                    scanColumnInfo.notNullCount < AUTO_STOP_MINIMUM)
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

        return getSchemaInfo (headers, scanColumnInfos, precise);
    }
}

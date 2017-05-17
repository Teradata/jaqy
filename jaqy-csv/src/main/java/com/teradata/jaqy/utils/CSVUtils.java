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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author	Heng Yuan
 */
public class CSVUtils
{
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
}

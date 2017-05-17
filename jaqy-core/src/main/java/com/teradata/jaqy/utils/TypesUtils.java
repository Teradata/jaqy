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

import java.sql.Types;

/**
 * @author	Heng Yuan
 */
public class TypesUtils
{
	public static String getTypeName (int type)
	{
		switch (type)
		{
			case Types.ARRAY:					// Java 1.2
				return "ARRAY";
			case Types.BIGINT:
				return "BIGINT";
			case Types.BINARY:
				return "BINARY";
			case Types.BIT:
				return "BIT";
			case Types.BLOB:					// Java 1.2
				return "BLOB";
			case Types.BOOLEAN:					// Java 1.4
				return "BOOLEAN";
			case Types.CHAR:
				return "CHAR";
			case Types.DATALINK:				// Java 1.4
				return "DATALINK";
			case Types.DATE:
				return "DATE";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.DISTINCT:				// Java 1.2
				return "DISTINCT";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.FLOAT:
				return "FLOAT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.JAVA_OBJECT:				// Java 1.2
				return "JAVA_OBJECT";
			case Types.LONGNVARCHAR:			// Java 1.6
				return "LONGNVARCHAR";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.NCHAR:					// Java 1.6
				return "NCHAR";
			case Types.NCLOB:					// Java 1.6
				return "NCLOB";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.NVARCHAR:				// Java 1.6
				return "NVARCHAR";
			case Types.OTHER:
				return "OTHER";
			case Types.REAL:
				return "REAL";
			case Types.REF:						// Java 1.2
				return "REF";
			case Types.REF_CURSOR:				// Java 1.8
				return "REF_CURSOR";
			case Types.ROWID:					// Java 1.6
				return "ROWID";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.SQLXML:					// Java 1.6
				return "SQLXML";
			case Types.STRUCT:					// Java 1.2
				return "STRUCT";
			case Types.TIME:
				return "TIME";
			case Types.TIME_WITH_TIMEZONE:		// Java 1.8
				return "TIME_WITH_TIMEZONE";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.TIMESTAMP_WITH_TIMEZONE:	// Java 1.8
				return "TIMESTAMP_WITH_TIMEZONE";
			case Types.TINYINT:
				return "TINYINT";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.VARCHAR:
				return "VARCHAR";
			default:
				return "Type " + type;
		}
	}

	public static boolean isString (int type)
	{
		switch (type)
		{
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.NCLOB:
				return true;
			default:
				return false;
		}
	}

	public static boolean isNumber (int type)
	{
		switch (type)
		{
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.REAL:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
				return true;
			default:
				return false;
		}
	}

	public static boolean isBinary (int type)
	{
		switch (type)
		{
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
				return true;
			default:
				return false;
		}
	}

}

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

import java.sql.Types;
import java.util.HashMap;

/**
 * @author	Heng Yuan
 */
public class TypesUtils
{
	private final static HashMap<String, Integer> s_nameMap = new HashMap<String, Integer> ();
	static
	{
		s_nameMap.put ("ARRAY", Types.ARRAY);
		s_nameMap.put ("BIGINT", Types.BIGINT);
		s_nameMap.put ("BINARY", Types.BINARY);
		s_nameMap.put ("BIT", Types.BIT);
		s_nameMap.put ("BLOB", Types.BLOB);
		s_nameMap.put ("BOOLEAN", Types.BOOLEAN);
		s_nameMap.put ("CHAR", Types.CHAR);
		s_nameMap.put ("CLOB", Types.CLOB);
		s_nameMap.put ("DATALINK", Types.DATALINK);
		s_nameMap.put ("DATE", Types.DATE);
		s_nameMap.put ("DECIMAL", Types.DECIMAL);
		s_nameMap.put ("DISTINCT", Types.DISTINCT);
		s_nameMap.put ("DOUBLE", Types.DOUBLE);
		s_nameMap.put ("FLOAT", Types.FLOAT);
		s_nameMap.put ("INTEGER", Types.INTEGER);
		s_nameMap.put ("JAVA_OBJECT", Types.JAVA_OBJECT);
		s_nameMap.put ("LONGNVARCHAR", Types.LONGNVARCHAR);
		s_nameMap.put ("LONGVARBINARY", Types.LONGVARBINARY);
		s_nameMap.put ("LONGVARCHAR", Types.LONGVARCHAR);
		s_nameMap.put ("NCHAR", Types.NCHAR);
		s_nameMap.put ("NCLOB", Types.NCLOB);
		s_nameMap.put ("NULL", Types.NULL);
		s_nameMap.put ("NUMERIC", Types.NUMERIC);
		s_nameMap.put ("NVARCHAR", Types.NVARCHAR);
		s_nameMap.put ("OTHER", Types.OTHER);
		s_nameMap.put ("REAL", Types.REAL);
		s_nameMap.put ("REF", Types.REF);
		s_nameMap.put ("REF_CURSOR", Types.REF_CURSOR);
		s_nameMap.put ("ROWID", Types.ROWID);
		s_nameMap.put ("SMALLINT", Types.SMALLINT);
		s_nameMap.put ("SQLXML", Types.SQLXML);
		s_nameMap.put ("STRUCT", Types.STRUCT);
		s_nameMap.put ("TIME", Types.TIME);
		s_nameMap.put ("TIME_WITH_TIMEZONE", Types.TIME_WITH_TIMEZONE);
		s_nameMap.put ("TIMESTAMP", Types.TIMESTAMP);
		s_nameMap.put ("TIMESTAMP_WITH_TIMEZONE", Types.TIMESTAMP_WITH_TIMEZONE);
		s_nameMap.put ("TINYINT", Types.TINYINT);
		s_nameMap.put ("VARBINARY", Types.VARBINARY);
		s_nameMap.put ("VARCHAR", Types.VARCHAR);
	}

	public static int getType (String name)
	{
		name = name.toUpperCase ();
		Integer value = s_nameMap.get (name.toUpperCase ());
		if (value == null)
		{
			return Types.NULL;
		}
		return value;
	}

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

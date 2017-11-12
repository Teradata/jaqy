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
package com.teradata.jaqy.schema;

import java.sql.Types;
import java.util.Map;

/**
 * @author	Heng Yuan
 */
public class TypeMap
{
	private final static int[] STRING_CAST = { Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.CLOB };
	private final static int[] NSTRING_CAST = { Types.NCHAR, Types.NVARCHAR, Types.LONGNVARCHAR, Types.NCLOB };
	private final static int[] BINARY_CAST = { Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY, Types.BLOB };
	private final static int[] NUMBER_CAST = { Types.BOOLEAN, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.DECIMAL, Types.NUMERIC, Types.FLOAT, Types.REAL, Types.DOUBLE };

	private final Map<Integer, TypeInfo> m_map; 

	public TypeMap (Map<Integer, TypeInfo> map)
	{
		m_map = map;
	}

	private TypeInfo getCast (int target, int precision, int[] castArray)
	{
		int index;
		// First, locate the target type location
		for (index = 0; index < castArray.length; ++index)
		{
			if (castArray[index] == target)
				break;
		}
		// Now for each subsequent cast, check if type info exists.
		TypeInfo bestCandidate = null;
		for (int i = index; i < castArray.length; ++i)
		{
			TypeInfo typeInfo = m_map.get (castArray[i]);
			if (typeInfo != null)
			{
				bestCandidate = typeInfo;
				// check if the candidate the enough precision
				if (typeInfo.maxPrecision == 0 &&
					typeInfo.maxPrecision < precision)
					continue;
				m_map.put (target, typeInfo);
				return typeInfo;
			}
		}
		if (bestCandidate != null)
			return bestCandidate;
		// Hmm, casting downward didn't find any, try upward.
		// Note that we are not trying to check the precision
		// this time.
		for (int i = index; i >= 0; --i)
		{
			TypeInfo typeInfo = m_map.get (castArray[i]);
			if (typeInfo != null)
			{
				m_map.put (target, typeInfo);
				return typeInfo;
			}
		}
		return null;
	}

	static int switchCharType (int type)
	{
		switch (type)
		{
			case Types.CHAR:
				return Types.NCHAR;
			case Types.VARCHAR:
				return Types.NVARCHAR;
			case Types.LONGVARCHAR:
				return Types.LONGNVARCHAR;
			case Types.CLOB:
				return Types.NCLOB;
			case Types.NCHAR:
				return Types.CHAR;
			case Types.NVARCHAR:
				return Types.VARCHAR;
			case Types.LONGNVARCHAR:
				return Types.LONGVARCHAR;
			case Types.NCLOB:
				return Types.CLOB;
		}
		return type;
	}

	public TypeInfo getType (int type, int precision, boolean exact)
	{
		TypeInfo typeInfo = m_map.get (type);
		if (exact || typeInfo != null)
			return typeInfo;

		switch (type)
		{
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
				return getCast (type, precision, BINARY_CAST);
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
			{
				typeInfo = getCast (type, precision, STRING_CAST);
				if (typeInfo == null)
					typeInfo = getCast (switchCharType (type), precision, NSTRING_CAST);
				if (typeInfo != null)
					m_map.put (type, typeInfo);
				return typeInfo;
			}
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.NCLOB:
			{
				typeInfo = getCast (type, precision, NSTRING_CAST);
				if (typeInfo == null)
					typeInfo = getCast (switchCharType (type), precision, STRING_CAST);
				if (typeInfo != null)
					m_map.put (type, typeInfo);
				return typeInfo;
			}
			case Types.BOOLEAN:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.REAL:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
			{
				// for numeric types, we ignore the precision (set it to 0)
				return getCast (type, 0, NUMBER_CAST);
			}
		}
		return null;
	}

	/**
	 * Sets the custom type map for a database.  It overrides the type info
	 * obtained from database.
	 *
	 * @param	customMap
	 * 			sets the custom type map for a database.
	 */
	public void setCustomMap (Map<Integer, TypeInfo> customMap)
	{
		synchronized (this)
		{
			m_map.putAll (customMap);
		}
	}

	/**
	 * Given the type, precision, and scale, return the type name that matches
	 * the need.
	 *
	 * @param	type
	 * 			See {@link Types}.
	 * @param	precision
	 * 			the precision for the type.
	 * @param	scale
	 * 			the scale for the type.
	 * @param	exact
	 * 			if exact is false, the closest type (based on casting rule)
	 * 			is used.
	 * @return	a type name.
	 */
	public String getTypeName (int type, int precision, int scale, boolean exact)
	{
		TypeInfo typeInfo = getType (type, precision, exact);
		if (typeInfo == null)
			return null;
		if (typeInfo.typeFormat != null)
		{
			return typeInfo.typeFormat.format (new Object[]{ precision, scale });
		}

		String typeName = typeInfo.typeName;
		boolean varType = (typeInfo.maxPrecision > 0);

		switch (type)
		{
			case Types.VARCHAR:
			case Types.CHAR:
			case Types.NVARCHAR:
			case Types.NCHAR:
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.CLOB:
			case Types.NCLOB:
			case Types.BLOB:
				/* Prevent the case of VARCHAR(0)
				 */
				if (precision == 0)
				{
					precision = 1;
				}
				/* If the size is quite big, it may be the default size.
				 * In that case, just return the type name itself.
				 */
				if (varType &&
					precision < 0x7fff0000)
					return typeName + "(" + precision + ")";
				return typeName;
			case Types.DECIMAL:
			case Types.NUMERIC:
				return typeName + "(" + precision + "," + scale + ")";
			default:
				return typeName;
		}
	}

	/**
	 * Check if a string refers to the same type as the other string.
	 * <p>
	 * For example, the following two are the same type.
	 * VARCHAR CHARACTER SET LATIN vs VARCHAR(1000) CHARACTER SET LATIN
	 * <p>
	 * Likewise, VARCHAR(1000) CHARACTER SET LATIN is the same as VARCHAR.
	 * <p>
	 * The purpose of this function is to detect if a name obtained from
	 * TypeMap matches the name obtained from ResultSetMetaData.
	 *
	 * @param	src
	 * 			src type string
	 * @param	sub
	 * 			the string to check.  It needs to be a sub common string
	 * 			of src.
	 * @return	true if sub is a substring of src.
	 */
	public static boolean isSameType (String src, String sub)
	{
		if (src.length () < sub.length ())
			return false;
		if (src.length () == sub.length ())
			return src.equalsIgnoreCase (sub);

		src = src.toLowerCase ();
		sub = sub.toLowerCase ();

		// check the simple case where sub is a substring of src.
		if (src.indexOf (sub) >= 0)
			return true;

		// we now have to deal with more complicated cases such as
		// VARCHAR CHARACTER SET LATIN vs VARCHAR(1000) CHARACTER SET LATIN
		// we make a major assumption that at least beginning part of
		// src and sub are the same.
		int i;
		for (i = 0; i < sub.length (); ++i)
		{
			if (src.charAt (i) != sub.charAt (i))
				break;
		}
		if (i == 0)
			return false;
		return src.substring (i).indexOf (sub.substring (i)) >= 0;
	}
}

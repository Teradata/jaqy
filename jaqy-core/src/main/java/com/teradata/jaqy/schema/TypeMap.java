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

	private TypeInfo getCast (int target, int[] castArray)
	{
		int index;
		for (index = 0; index < castArray.length; ++index)
		{
			if (castArray[index] == target)
				break;
		}
		for (int i = index; i < castArray.length; ++i)
		{
			TypeInfo typeInfo = m_map.get (castArray[i]);
			if (typeInfo != null)
			{
				m_map.put (target, typeInfo);
				return typeInfo;
			}
		}
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

	private static int switchCharType (int type)
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

	public TypeInfo getType (int type)
	{
		TypeInfo typeInfo = m_map.get (type);
		if (typeInfo != null)
			return typeInfo;

		switch (type)
		{
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
				return getCast (type, BINARY_CAST);
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
			{
				typeInfo = getCast (type, STRING_CAST);
				if (typeInfo == null)
					typeInfo = getCast (switchCharType (type), NSTRING_CAST);
				if (typeInfo != null)
					m_map.put (type, typeInfo);
				return typeInfo;
			}
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.NCLOB:
			{
				typeInfo = getCast (type, NSTRING_CAST);
				if (typeInfo == null)
					typeInfo = getCast (switchCharType (type), STRING_CAST);
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
				return getCast (type, NUMBER_CAST);
		}
		return null;
	}
}

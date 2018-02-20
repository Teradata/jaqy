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
package com.teradata.jaqy.typehandler;

import java.sql.Clob;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
class ClobTypeHandler implements TypeHandler
{
	private final static TypeHandler s_instance = new ClobTypeHandler ();

	public static TypeHandler getInstance ()
	{
		return s_instance;
	}

	private ClobTypeHandler ()
	{
	}

	@Override
	public String getString (JaqyResultSet rs, int columnIndex, JaqyInterpreter interpreter) throws SQLException
	{
		Object o = rs.getObject (columnIndex);
		if (o == null)
			return null;
		if (o instanceof Clob)
		{
			Clob clob = (Clob)o;
			String value = clob.getSubString (1, (int)clob.length ());
			clob.free ();
			return value;
		}
		return o.toString ();
	}

	@Override
	public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
	{
		Clob clob = (Clob) rs.getObject (column);
		if (clob == null)
			return -1;
		return (int)clob.length ();
	}
}

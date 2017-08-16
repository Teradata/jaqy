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
package com.teradata.jaqy.typehandler;

import java.sql.Clob;
import java.sql.SQLException;

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
	public String getString (JaqyResultSet rs, int columnIndex) throws SQLException
	{
		Clob clob = (Clob) rs.getObject (columnIndex);
		if (clob == null)
			return null;
		String str = clob.getSubString (1, (int)clob.length ());
		clob.free ();
		return str;
	}
}

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

import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
class StringTypeHandler implements TypeHandler
{
	private final static TypeHandler s_instance = new StringTypeHandler ();

	public static TypeHandler getInstance ()
	{
		return s_instance;
	}

	private StringTypeHandler ()
	{
	}

	@Override
	public String getString (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
	{
		return rs.getString (column);
	}

	@Override
	public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
	{
		String str = rs.getString (column);
		if (str == null)
			return -1;
		return str.length ();
	}
}

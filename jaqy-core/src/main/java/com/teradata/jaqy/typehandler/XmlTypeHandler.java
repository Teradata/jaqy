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
import java.sql.SQLXML;

import com.teradata.jaqy.connection.JaqyResultSet;

/**
 * @author	Heng Yuan
 */
class XmlTypeHandler implements TypeHandler
{
	private final static TypeHandler s_instance = new XmlTypeHandler ();

	public static TypeHandler getInstance ()
	{
		return s_instance;
	}

	private XmlTypeHandler ()
	{
	}

	@Override
	public String getString (JaqyResultSet rs, int column) throws SQLException
	{
		SQLXML xml = (SQLXML) rs.getObject (column);
		if (xml  == null)
			return null;
		String value = xml.getString ();
		xml.free ();
		return value;
	}

	@Override
	public int getLength (JaqyResultSet rs, int column) throws SQLException
	{
		SQLXML xml = (SQLXML) rs.getObject (column);
		if (xml  == null)
			return 0;
		return xml.getString ().length ();
	}
}

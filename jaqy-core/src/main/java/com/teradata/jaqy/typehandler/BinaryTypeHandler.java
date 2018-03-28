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

import java.sql.Blob;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
class BinaryTypeHandler implements TypeHandler
{
	private final static TypeHandler s_instance = new BinaryTypeHandler ();

	public static TypeHandler getInstance ()
	{
		return s_instance;
	}

	private BinaryTypeHandler ()
	{
	}

	@Override
	public String getString (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
	{
		Object obj = rs.getObject (column);
		if (obj == null)
			return null;
		else if (obj instanceof byte[])
		{
			return StringUtils.getHexString ((byte[])obj);
		}
		else if (obj instanceof Blob)
		{
			Blob blob = (Blob)obj;
			byte[] bytes = blob.getBytes (1, (int)blob.length ());
			blob.free ();
			return StringUtils.getHexString (bytes);
		}
		return "Unable to handle " + obj.getClass ();
	}

	@Override
	public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
	{
		Object obj = rs.getObject (column);
		if (obj == null)
			return -1;
		else if (obj instanceof byte[])
		{
			return ((byte[])obj).length * 2;
		}
		else if (obj instanceof Blob)
		{
			Blob blob = (Blob)obj;
			return (int)blob.length () * 2;
		}
		throw new RuntimeException ("Unable to handle " + obj.getClass ());
	}
}

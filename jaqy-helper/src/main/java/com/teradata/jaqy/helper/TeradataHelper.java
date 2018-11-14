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
package com.teradata.jaqy.helper;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.Collection;
import java.util.logging.Level;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyDefaultResultSet;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.resultset.CachedClob;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.ResultSetUtils;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
class TeradataHelper extends DefaultHelper
{
	private final static String getPDTString (Struct s) throws SQLException
	{
		if (s == null)
			return null;
		StringBuilder builder = new StringBuilder ();
		builder.append ("('");
		for (Object obj : s.getAttributes ())
		{
			if (builder.length () > 2)
				builder.append ("', '");
			builder.append (obj);
		}
		builder.append ("')");
		return builder.toString ();
	}

	private final static TypeHandler s_pdtHandler = new TypeHandler ()
	{
		@Override
		public String getString (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
		{
			return getPDTString ((Struct)rs.getObject (column));
		}

		@Override
		public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
		{
			Struct str = (Struct)rs.getObject (column);
			if (str == null)
				return -1;
			int len = 8;
			for (Object obj : str.getAttributes ())
			{
				if (obj != null)
					len += obj.toString ().length ();
			}
			return len;
		}
	};

	public TeradataHelper (JdbcFeatures features, JaqyConnection conn, Globals globals)
	{
		super (features, conn, globals);
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs, JaqyInterpreter interpreter) throws SQLException
	{
		if (rs == null)
			return null;
		ResultSetMetaData meta = rs.getMetaData ();

		// guess if the output is from SHOW statement.
		if (meta != null &&
			meta.getColumnCount () == 1)
		{
			int type = meta.getColumnType (1);
			if (TypesUtils.isString (type))
			{
				getGlobals ().log (Level.INFO, "Potential SHOW ResultSet.");
				//
				// Teradata SHOW statements due to legacy, use '\r' instead of '\n'
				// characters.  That can be problematic in the output.
				//
				// Per discussion with a Teradata JDBC architect, no internals of
				// Teradata JDBC should be exposed in OSS code.  So the only thing
				// we can do is to guess the output could be from, and replace
				// the EOL character that way.
				//
				// All SHOW statements are single column.  Typically, the output
				// is a row of VARCHAR.  However, I am not sure if there is ever
				// a case of having CLOB output, or just multiple rows.
				//
				// So we do generic handling here.
				//
				InMemoryResultSet newRS = ResultSetUtils.copyResultSet (rs, 0, this, interpreter);
				rs.close ();
				for (Object[] row : newRS.getRows ())
				{
					for (int i = 0; i < row.length; ++i)
					{
						if (row[i] != null)
						{
							Object o = row[i];
							if (o instanceof String)
							{
								row[i] = ((String)o).replace ('\r', '\n');
							}
							else if (o instanceof CachedClob)
							{
								CachedClob clob = (CachedClob)o;
								String s = clob.getSubString (1, (int)clob.length ());
								clob = new CachedClob (s);
							}
						}
					}
				}
				rs = newRS;
			}
		}
		return new JaqyDefaultResultSet (rs, this);
	}

	@Override
	public Object getObject (JaqyResultSet rs, int index) throws SQLException
	{
		Object o = rs.getObjectInternal (index);
		if (o instanceof Struct)
		{
			if (rs.getMetaData ().getColumnType (index) == Types.STRUCT)
			{
				String typeName = rs.getMetaData ().getColumnTypeName (index);
				if (typeName != null && typeName.startsWith ("PERIOD("))
				{
					return getPDTString ((Struct) o);
				}
			}
		}
		return o;
	}

	@Override
	public TypeHandler getTypeHandler (JaqyResultSet rs, int column) throws SQLException
	{
		if (rs.getMetaData ().getColumnType (column) == Types.STRUCT)
		{
			String typeName = rs.getMetaData ().getColumnTypeName (column);
			if (typeName != null && typeName.startsWith ("PERIOD("))
			{
				return s_pdtHandler;
			}
		}
		return super.getTypeHandler (rs, column);
	}

	@Override
	public Struct createStruct (ParameterInfo paramInfo, Object[] elements) throws SQLException
	{
		/*
		 * Handle Teradata PERIOD data types here. 
		 */
		String typeName = paramInfo.typeName;
		if (elements.length == 2 &&
			typeName.startsWith ("PERIOD") &&
			elements[0] != null &&
			elements[0] instanceof String &&
			elements[1] != null &&
			elements[1] instanceof String)
		{
			if ("PERIOD(DATE)".equals (typeName))
			{
				elements[0] = Date.valueOf ((String) elements[0]);
				elements[1] = Date.valueOf ((String) elements[1]);
			}
			else if ("PERIOD(TIME)".equals (typeName) ||
					 "PERIOD(TIME WITH TIME ZONE)".equals (typeName))
			{
				elements[0] = Time.valueOf ((String) elements[0]);
				elements[1] = Time.valueOf ((String) elements[1]);
			}
			else if ("PERIOD(TIMESTAMP)".equals (typeName) ||
					 "PERIOD(TIMESTAMP WITH TIME ZONE)".equals (typeName))
			{
				elements[0] = Timestamp.valueOf ((String) elements[0]);
				elements[1] = Timestamp.valueOf ((String) elements[1]);
			}
		}

		return getConnection ().createStruct (typeName, elements);
	}

	@Override
	public void fixColumnInfo (FullColumnInfo info)
	{
		if (info.type == Types.STRUCT &&
			info.typeName != null &&
			info.typeName.startsWith ("PERIOD("))
		{
			info.type = Types.VARCHAR;
			info.precision = 100;
		}
		else if (info.type == Types.OTHER &&
				 info.className != null)
		{
			fixOtherType (info);
		}
	}

	public void setObject (JaqyPreparedStatement stmt, int columnIndex, ParameterInfo paramInfo, Object o, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception
	{
		/*
		 * Teradata JDBC driver does not support connection.createClob()
		 * or connection.createBlob ().  As the result, it is necessary
		 * to use setBinaryStream / setCharacterStream to set the values.
		 */
		switch (paramInfo.type)
		{
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			{
				stmt.setObject (columnIndex, o, paramInfo.type);
				return;
			}
			case Types.BLOB:
			{
				if (o instanceof Blob)
				{
					Blob blob = (Blob)o;
					stmt.setBinaryStream (columnIndex, blob.getBinaryStream ());
					return;
				}
				else if (o instanceof byte[] ||
						 o instanceof ByteBuffer)
				{
					byte[] bytes;
					if (o instanceof byte[])
					{
						bytes = (byte[])o;
					}
					else
					{
						ByteBuffer bb = (ByteBuffer)o;
						int size = bb.remaining ();
						bytes = new byte[size];
						bb.get (bytes);
					}
					stmt.setBinaryStream (columnIndex, new ByteArrayInputStream (bytes));
					return;
				}
				// go to the default handling.
				break;
			}
			case Types.CLOB:
			case Types.NCLOB:
			{
				if (o instanceof Clob)
				{
					Clob clob = (Clob)o;
					stmt.setCharacterStream (columnIndex, clob.getCharacterStream ());
					return;
				}
				else if (o instanceof CharSequence)
				{
					stmt.setCharacterStream (columnIndex, new StringReader (o.toString ()));
					return;
				}
				// go to the default handling.
				break;
			}
		}
	}
}

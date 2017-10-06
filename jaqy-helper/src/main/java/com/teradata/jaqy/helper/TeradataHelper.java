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
package com.teradata.jaqy.helper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Level;

import com.teradata.jaqy.Log;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.resultset.InMemClob;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
class TeradataHelper extends DefaultHelper
{
	private final static TypeHandler s_pdtHandler = new TypeHandler ()
	{
		@Override
		public String getString (JaqyResultSet rs, int column) throws SQLException
		{
			Struct str = (Struct)rs.getObject (column);
			if (str == null)
				return null;
			StringBuilder builder = new StringBuilder ();
			builder.append ("('");
			for (Object obj : str.getAttributes ())
			{
				if (builder.length () > 2)
					builder.append ("', '");
				builder.append (obj);
			}
			builder.append ("')");
			return builder.toString ();
		}

		@Override
		public int getLength (JaqyResultSet rs, int column) throws SQLException
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
	public JaqyResultSet getResultSet (ResultSet rs) throws SQLException
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
				Log.log (Level.INFO, "Potential SHOW ResultSet.");
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
				@SuppressWarnings ("resource")
				InMemoryResultSet newRS = new InMemoryResultSet (rs);
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
							else if (o instanceof InMemClob)
							{
								InMemClob clob = (InMemClob)o;
								String s = clob.getSubString (1, (int)clob.length ());
								clob.replace (s);
							}
						}
					}
				}
				rs = newRS;
			}
		}
		return new JaqyResultSet (rs, this);
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
			info.typeName.startsWith ("PERIOD"))
		{
			if ("PERIOD(DATE)".equals (info.typeName))
			{
				FullColumnInfo[] childrenInfo = new FullColumnInfo[2];
				childrenInfo[0] = new FullColumnInfo ();
				childrenInfo[0].label = "BEGIN";
				childrenInfo[1] = new FullColumnInfo ();
				childrenInfo[1].label = "END";
				for (int i = 0; i < 2; ++i)
				{
					childrenInfo[i].type = Types.DATE;
					childrenInfo[i].typeName = "DATE";
				}
			}
			else if ("PERIOD(TIME)".equals (info.typeName) ||
					 "PERIOD(TIME WITH TIME ZONE)".equals (info.typeName))
			{
				FullColumnInfo[] childrenInfo = new FullColumnInfo[2];
				childrenInfo[0] = new FullColumnInfo ();
				childrenInfo[0].label = "BEGIN";
				childrenInfo[1] = new FullColumnInfo ();
				childrenInfo[1].label = "END";
				for (int i = 0; i < 2; ++i)
				{
					childrenInfo[i].type = Types.TIME;
					childrenInfo[i].typeName = "TIME";
				}
			}
			else if ("PERIOD(TIMESTAMP)".equals (info.typeName) ||
					 "PERIOD(TIMESTAMP WITH TIME ZONE)".equals (info.typeName))
			{
				FullColumnInfo[] childrenInfo = new FullColumnInfo[2];
				childrenInfo[0] = new FullColumnInfo ();
				childrenInfo[0].label = "BEGIN";
				childrenInfo[1] = new FullColumnInfo ();
				childrenInfo[1].label = "END";
				for (int i = 0; i < 2; ++i)
				{
					childrenInfo[i].type = Types.TIMESTAMP;
					childrenInfo[i].typeName = "TIMESTAMP";
				}
			}
		}
	}
}

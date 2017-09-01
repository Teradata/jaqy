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

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Struct;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.resultset.InMemoryResultSet;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.typehandler.TypeHandlerRegistry;
import com.teradata.jaqy.utils.ParameterInfo;

/**
 * @author	Heng Yuan
 */
public class DefaultHelper implements JaqyHelper
{
	public final static String getYesNo (boolean b)
	{
		return b ? "Yes" : "No";
	}

	private final JaqyConnection m_conn;
	private final Globals m_globals;
	private final JdbcFeatures m_features;

	public DefaultHelper (JdbcFeatures features, JaqyConnection conn, Globals globals)
	{
		m_features = features;
		m_conn = conn;
		m_globals = globals;
	}

	public JdbcFeatures getFeatures ()
	{
		return m_features;
	}

	public Globals getGlobals ()
	{
		return m_globals;
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs) throws SQLException
	{
		return new JaqyResultSet (rs, this);
	}

	@Override
	public JaqyConnection getConnection ()
	{
		return m_conn;
	}

	@Override
	public JaqyStatement createStatement () throws SQLException
	{
		Connection conn = m_conn.getConnection ();
		if (m_features.forwardOnlyRS)
			return new JaqyStatement (conn.createStatement (), m_conn);
		else
		{
			try
			{
				return new JaqyStatement (conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY), m_conn);
			}
			catch (SQLException ex)
			{
				// we are catching all SQLException rather than just
				// SQLFeatureNotSupportedException because some JDBC drivers
				// throw SQLException in all cases.
				getFeatures().forwardOnlyRS = true;
				return new JaqyStatement (conn.createStatement (), m_conn);
			}
		}
	}

	@Override
	public JaqyPreparedStatement preparedStatement (String sql) throws SQLException
	{
		Connection conn = m_conn.getConnection ();
		if (m_features.forwardOnlyRS)
			return new JaqyPreparedStatement (conn.prepareStatement (sql), m_conn);
		else
		{
			try
			{
				return new JaqyPreparedStatement (conn.prepareStatement (sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY), m_conn);
			}
			catch (SQLException ex)
			{
				// we are catching all SQLException rather than just
				// SQLFeatureNotSupportedException because some JDBC drivers
				// throw SQLException in all cases.
				getFeatures().forwardOnlyRS = true;
				return new JaqyPreparedStatement (conn.prepareStatement (sql), m_conn);
			}
		}
	}

	private String getCatalogInternal () throws SQLException
	{
		if (m_features.noCatalog)
			return null;
		try
		{
			return m_conn.getConnection ().getCatalog ();
		}
		catch (SQLFeatureNotSupportedException ex)
		{
			m_features.noCatalog = true;
			return null;
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			m_features.noCatalog = true;
			return null;
		}
	}

	private String getSchemaInternal () throws SQLException
	{
		if (m_features.noSchema)
			return null;
		try
		{
			return m_conn.getConnection ().getSchema ();
		}
		catch (SQLFeatureNotSupportedException ex)
		{
			getFeatures().noSchema = true;
			return null;
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			m_features.noSchema = true;
			return null;
		}
	}

	@Override
	public String getCatalog () throws SQLException
	{
		return getCatalogInternal ();
	}

	@Override
	public String getSchema () throws SQLException
	{
		return getSchemaInternal ();
	}

	@Override
	public String getURL () throws SQLException
	{
		String url = m_conn.getMetaData ().getURL ();
		if (url.startsWith ("jdbc:"))
			url = url.substring (5);
		return url;
	}

	@Override
	public String getPath () throws SQLException
	{
		boolean hasUser = false;
		StringBuilder buffer = new StringBuilder ();
		String user = m_conn.getMetaData ().getUserName ();
		if (user != null && user.length () > 0)
		{
			hasUser = true;
			buffer.append (user);
		}

		boolean hasHost = false;
		String url = m_conn.getMetaData ().getURL ();
		int start = url.indexOf ("//");
		if (start > 0)
		{
			start += 2;
			hasHost = true;
			int end = url.indexOf ('/', start);
			if (end < 0)
				end = url.length ();
			String host = url.substring (start, end);
			if (hasUser)
				buffer.append (" @ ");
			buffer.append (host);
		}

		String path = null;
		String catalog = getCatalogInternal ();
		String schema = getSchemaInternal ();
		if (catalog == null || catalog.length () == 0)
		{
			path = schema; 
		}
		else
		{
			if (schema == null || schema.length () == 0)
				path = catalog;
			else
				path = catalog + m_conn.getCatalogSeparator () + schema;
		}
		if (path != null)
		{
			if (hasUser || hasHost)
			{
				buffer.append (" - ");
			}
			buffer.append (path);
		}
		if (buffer.length () > 0)
			return buffer.toString ();
		return getURL ();
	}

	@Override
	public boolean isJsonColumn (JaqyResultSetMetaData meta, int column) throws SQLException
	{
		if ("JSON".equalsIgnoreCase (meta.getColumnTypeName (column)))
			return true;
		return false;
	}

	@Override
	public boolean isSpatialColumn (JaqyResultSetMetaData meta, int column) throws SQLException
	{
		if ("ST_GEOMETRY".equalsIgnoreCase (meta.getColumnTypeName (column)))
			return true;
		return false;
	}

	@Override
	public TypeHandler getTypeHandler (JaqyResultSet rs, int column) throws SQLException
	{
		return TypeHandlerRegistry.getTypeHandler (rs.getMetaData ().getColumnType (column));
	}

	@Override
	public Array createArrayOf (ParameterInfo paramInfo, Object[] elements) throws SQLException
	{
		return m_conn.createArrayOf ("VARCHAR", elements);
	}

	@Override
	public Struct createStruct (ParameterInfo paramInfo, Object[] elements) throws SQLException
	{
		return m_conn.createStruct (paramInfo.typeName, elements);
	}

	/**
	 * Guess the column type based on the ResultSetMetaData.
	 */
	@Override
	public String getColumnType (JaqyResultSetMetaData meta, int column) throws SQLException
	{
		String type = meta.getColumnTypeName (column);
		String upperType = type.toUpperCase ();
		if ("VARCHAR".equals (upperType) ||
			"CHAR".equals (upperType) ||
			"BYTE".equals (upperType) ||
			"VARBYTE".equals (upperType) ||
			"CLOB".equals (upperType) ||
			"BLOB".equals (upperType))
		{
			int size = meta.getPrecision (column);
			return type + "(" + size + ")";
		}
		if ("DECIMAL".equals (upperType))
		{
			int precision = meta.getPrecision (column);
			int scale = meta.getScale (column);
			return type + "(" + precision + "," + scale + ")";
		}
		return type;
	}

	/**
	 * Guess the schema for a table.
	 */
	@Override
	public String getSchema (String tableName) throws Exception
	{
		String query = "SELECT * FROM " + tableName + " WHERE 1 = 0";
		JaqyStatement stmt = null;
		try
		{
			stmt = createStatement ();
			stmt.execute (query);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				throw new RuntimeException ("Table was not found.");
			JaqyResultSetMetaData meta = rs.getMetaData ();
			int count = meta.getColumnCount ();

			StringBuilder schema = new StringBuilder ();
			schema.append ("CREATE TABLE ").append (tableName).append (" (");
			for (int i = 0; i < count; ++i)
			{
				String columnName = meta.getColumnName (i + 1);
				String columnType = getColumnType (meta, i + 1);
				if (i == 0)
					schema.append ('\n');
				else
					schema.append (",\n");
				schema.append ('\t');
				schema.append (columnName).append (" ").append (columnType);
				if (meta.isNullable (i + 1) == ResultSetMetaData.columnNoNulls)
					schema.append (" NOT NULL");
			}
			schema.append ("\n)");
			rs.close ();

			return schema.toString ();
		}
		finally
		{
			try
			{
				stmt.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	@Override
	public JaqyResultSet getColumns (String tableName) throws Exception
	{
		String query = "SELECT * FROM " + tableName + " WHERE 1 = 0";
		JaqyStatement stmt = null;
		try
		{
			stmt = createStatement ();
			stmt.execute (query);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				throw new RuntimeException ("Table was not found.");
			JaqyResultSetMetaData meta = rs.getMetaData ();
			int count = meta.getColumnCount ();

			PropertyTable pt = new PropertyTable (new String[]{ "Column", "Type", "Nullable" });
			for (int i = 0; i < count; ++i)
			{
				String columnName = meta.getColumnName (i + 1);
				String columnType = getColumnType (meta, i + 1);
				int nullableInt = meta.isNullable (i + 1);
				String nullable = (nullableInt == ResultSetMetaData.columnNoNulls) ? "No" : (nullableInt == ResultSetMetaData.columnNullable ? "Yes" : "Unknown");
				pt.addRow (new String[]{ columnName, columnType, nullable });
			}
			rs.close ();

			InMemoryResultSet columnRS = new InMemoryResultSet (pt);
			return DummyHelper.getInstance ().getResultSet (columnRS);
		}
		finally
		{
			try
			{
				stmt.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}
}

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
import java.sql.Types;
import java.text.MessageFormat;

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
import com.teradata.jaqy.schema.BasicColumnInfo;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.schema.SchemaUtils;
import com.teradata.jaqy.schema.TypeInfo;
import com.teradata.jaqy.schema.TypeMap;
import com.teradata.jaqy.typehandler.TypeHandler;
import com.teradata.jaqy.typehandler.TypeHandlerRegistry;
import com.teradata.jaqy.utils.ExceptionUtils;
import com.teradata.jaqy.utils.QueryUtils;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;
import com.teradata.jaqy.utils.SimpleQuery;

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

	private SimpleQuery m_catalogQuery;
	private SimpleQuery m_schemaQuery;
	private SimpleQuery m_tableSchemaQuery;
	private MessageFormat m_tableSchemaFormat;
	private MessageFormat m_tableColumnFormat;

	private TypeMap m_typeMap;

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

	@Override
	public TypeMap getTypeMap () throws SQLException
	{
		if (m_typeMap != null)
			return m_typeMap;
		m_typeMap = SchemaUtils.getTypeMap (m_conn);
		return m_typeMap;
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
		if (m_catalogQuery == null)
			return getCatalogInternal ();
		return QueryUtils.getQueryString (getGlobals (), m_conn, m_catalogQuery.sql, m_catalogQuery.columnIndex);
	}

	@Override
	public String getSchema () throws SQLException
	{
		if (m_schemaQuery == null)
			return getSchemaInternal ();
		return QueryUtils.getQueryString (getGlobals (), m_conn, m_schemaQuery.sql, m_schemaQuery.columnIndex);
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
			int end = url.indexOf (':', start);
			if (end > 0)
				url = url.substring (start, end);
			else
				url = url.substring (start);
			end = url.indexOf ('/');
			if (end < 0)
				end = url.length ();
			String host = url.substring (0, end);
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

	@Override
	public String getTypeName (BasicColumnInfo columnInfo) throws SQLException
	{
		boolean varType = true;
		if (columnInfo.typeName == null)
		{
			TypeMap typeMap = getTypeMap ();
			if (typeMap == null)
				return null;
			TypeInfo typeInfo = typeMap.getType (columnInfo.type);
			if (typeInfo == null)
				return null;
			columnInfo.typeName = typeInfo.typeName;
			varType = (typeInfo.maxPrecision > 0);
		}

		switch (columnInfo.type)
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
				if (columnInfo.precision == 0)
				{
					columnInfo.precision = 1;
				}
				/* If the size is quite big, it may be the default size.
				 * In that case, just return the type name itself.
				 */
				if (varType &&
					columnInfo.precision < 0x7fff0000)
					return columnInfo.typeName + "(" + columnInfo.precision + ")";
				return columnInfo.typeName;
			case Types.DECIMAL:
			case Types.NUMERIC:
				return columnInfo.typeName + "(" + columnInfo.precision + "," + columnInfo.scale + ")";
			default:
				return columnInfo.typeName;
		}
	}

	/**
	 * Guess the schema for a table.
	 */
	@Override
	public String getTableSchema (String tableName) throws Exception
	{
		if (m_tableSchemaQuery != null)
		{
			String value = QueryUtils.getQueryString (getGlobals (), m_conn, m_tableSchemaFormat.format (new Object[]{ tableName }), m_tableSchemaQuery.columnIndex);
			if (value == null || value.length () == 0)
				throw ExceptionUtils.getTableNotFound ();
			return value;
		}

		/*
		 * Creates a dummy query that gets no actual results.  We just need
		 * the resulting ResultSetMetaData to infer the table schema.
		 */
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
			SchemaInfo schemaInfo = ResultSetMetaDataUtils.getColumnInfo (meta.getMetaData ());
			String schema = SchemaUtils.getTableSchema (this, schemaInfo, tableName);
			rs.close ();

			return schema;
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
	public JaqyResultSet getTableColumns (String tableName) throws Exception
	{
		if (m_tableColumnFormat != null)
		{
			JaqyResultSet rs = QueryUtils.getResultSet (getGlobals (), m_conn, m_tableColumnFormat.format (new Object[]{ tableName }));
			if (rs == null)
				throw ExceptionUtils.getTableNotFound ();
			InMemoryResultSet inmemrs = (InMemoryResultSet) rs.getResultSet ();
			if (inmemrs.getRows ().size () == 0)
				throw ExceptionUtils.getTableNotFound ();
			return rs;
		}

		String query = "SELECT * FROM " + tableName + " WHERE 1 = 0";
		JaqyStatement stmt = null;
		try
		{
			stmt = createStatement ();
			stmt.execute (query);
			JaqyResultSet rs = stmt.getResultSet ();
			if (rs == null)
				throw ExceptionUtils.getTableNotFound ();
			JaqyResultSetMetaData meta = rs.getMetaData ();
			SchemaInfo schemaInfo = ResultSetMetaDataUtils.getColumnInfo (meta.getMetaData ());
			int count = schemaInfo.columns.length;

			PropertyTable pt = new PropertyTable (new String[]{ "Column", "Type", "Nullable" });
			for (int i = 0; i < count; ++i)
			{
				String columnName = schemaInfo.columns[i].name;
				String columnType = getTypeName (schemaInfo.columns[i]);
				String nullable = (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNoNulls) ? "No" : (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNullable ? "Yes" : "Unknown");
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

	private int guessType (String className)
	{
		if ("java.lang.Integer".equals (className))
			return Types.INTEGER;
		if ("java.lang.String".equals (className))
			return Types.VARCHAR;
		// Can't guess it
		return Types.OTHER;
	}

	protected FullColumnInfo[] createElementType (int type, String typeName)
	{
		FullColumnInfo[] infos = new FullColumnInfo[1];
		infos[0] = new FullColumnInfo ();
		infos[0].type = type;
		infos[0].typeName = typeName;
		return infos;
	}

	private void setVarCharType (FullColumnInfo info)
	{
		info.type = Types.VARCHAR;
		info.nullable = ResultSetMetaData.columnNullableUnknown;
		info.displaySize = Integer.MAX_VALUE;
		info.precision = Integer.MAX_VALUE;
		info.className = "java.lang.String";
	}

	@Override
	public void fixColumnInfo (FullColumnInfo info)
	{
		if (info.type == Types.OTHER &&
			info.className != null &&
			info.className.startsWith ("java.lang."))
		{
			info.type = guessType (info.className);
		}
		else if (info.type == Types.STRUCT ||
				 info.type == Types.ARRAY)
		{
			/* By default, we have no way of knowing the Array / Struct
			 * element types.  JDBC API unfortunately relies on actually
			 * getting an instance of Array to get more information.
			 * And there is basically nothing in JDBC API for getting the
			 * child data.
			 *
			 * So the simplest approach is to treat Struct / Array
			 * as array of string. 
			 */
			info.type = Types.ARRAY;
			info.children = createElementType (Types.VARCHAR, "varchar");
			setVarCharType (info.children[0]);
		}
	}

	@Override
	public void fixParameterInfo (ParameterInfo info)
	{
		if (info.type == Types.OTHER &&
			info.className != null &&
			info.className.startsWith ("java.lang."))
		{
			info.type = guessType (info.className);
		}
	}

	public void setCatalogQuery (SimpleQuery catalogQuery)
	{
		m_catalogQuery = catalogQuery;
	}

	public void setSchemaQuery (SimpleQuery schemaQuery)
	{
		m_schemaQuery = schemaQuery;
	}

	public void setTableSchemaQuery (SimpleQuery tableSchemaQuery)
	{
		m_tableSchemaQuery = tableSchemaQuery;
		if (tableSchemaQuery == null)
		{
			m_tableSchemaFormat = null;
		}
		else
		{
			m_tableSchemaFormat = new MessageFormat (tableSchemaQuery.sql);
		}
	}

	public void setTableColumnQuery (SimpleQuery tableSchemaQuery)
	{
		if (tableSchemaQuery == null)
			m_tableColumnFormat = null;
		else
			m_tableColumnFormat = new MessageFormat (tableSchemaQuery.sql);
	}
}

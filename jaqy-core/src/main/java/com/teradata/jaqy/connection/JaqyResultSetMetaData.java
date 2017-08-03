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
package com.teradata.jaqy.connection;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
public class JaqyResultSetMetaData
{
	private final ResultSetMetaData m_metaData;
	private final JaqyHelper m_helper;
	private final JdbcFeatures m_features;

	JaqyResultSetMetaData (ResultSetMetaData metaData, JaqyHelper helper)
	{
		m_metaData = metaData;
		m_helper = helper;
		m_features = m_helper.getFeatures ();
	}

	/**
	 * @return	the metaData
	 */
	public ResultSetMetaData getMetaData ()
	{
		return m_metaData;
	}

	/**
	 * @return	the connection
	 */
	public JaqyConnection getConnection ()
	{
		return m_helper.getConnection ();
	}

	/**
	 * Gets the features not supported by the JDBC driver.
	 * @return	the features not supported by the JDBC driver.
	 */
	public JdbcFeatures getFeatures ()
	{
		return m_features;
	}

	public boolean isNumber (int column) throws SQLException
	{
		return TypesUtils.isNumber (m_metaData.getColumnType (column));
	}

	public boolean isBinary (int column) throws SQLException
	{
		return TypesUtils.isBinary (m_metaData.getColumnType (column));
	}

	public boolean isSigned (int column) throws SQLException
	{
		if (m_features.noRSMDSigned)
		{
			return isNumber (column);
		}
		else
		{
			try
			{
				return getMetaData().isSigned (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDSigned = true;
				return isNumber (column);
			}
		}
	}

	public int getColumnCount () throws SQLException
	{
		return m_metaData.getColumnCount ();
	}

	public String getCatalogName (int column) throws SQLException
	{
		if (m_features.noRSMDCatalog)
		{
			return null;
		}
		else
		{
			try
			{
				return m_metaData.getCatalogName (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDCatalog = true;
				return null;
			}
		}
	}

	public String getSchemaName (int column) throws SQLException
	{
		if (m_features.noRSMDSchema)
		{
			return null;
		}
		else
		{
			try
			{
				return m_metaData.getSchemaName (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDSchema = true;
				return null;
			}
		}
	}

	public String getTableName (int column) throws SQLException
	{
		if (m_features.noRSMDTable)
		{
			return null;
		}
		else
		{
			try
			{
				return m_metaData.getTableName (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDTable = true;
				return null;
			}
		}
	}

	public String getColumnName (int column) throws SQLException
	{
		return m_metaData.getColumnName (column);
	}

	public String getColumnLabel (int column) throws SQLException
	{
		return m_metaData.getColumnLabel (column);
	}

	public int getColumnType (int column) throws SQLException
	{
		return m_metaData.getColumnType (column);
	}

	public String getColumnTypeName (int column) throws SQLException
	{
		return m_metaData.getColumnTypeName (column);
	}

	public int getColumnDisplaySize (int column) throws SQLException
	{
		return m_metaData.getColumnDisplaySize (column);
	}

	public boolean isReadOnly (int column) throws SQLException
	{
		return m_metaData.isReadOnly (column);
	}

	public boolean isCurrency (int column) throws SQLException
	{
		return m_metaData.isCurrency (column);
	}

	public boolean isSearchable (int column) throws SQLException
	{
		if (m_features.noRSMDSearchable)
		{
			return false;
		}
		else
		{
			try
			{
				return m_metaData.isSearchable (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDSearchable = true;
				return false;
			}
		}
	}

	public boolean isAutoIncrement (int column) throws SQLException
	{
		return m_metaData.isAutoIncrement (column);
	}

	public boolean isCaseSensitive (int column) throws SQLException
	{
		return m_metaData.isCaseSensitive (column);
	}

	public boolean isWritable (int column) throws SQLException
	{
		if (m_features.noRSMDWritable)
		{
			return false;
		}
		else
		{
			try
			{
				return m_metaData.isWritable (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDWritable = true;
				return false;
			}
		}
	}

	public boolean isDefinitelyWritable (int column) throws SQLException
	{
		if (m_features.noRSMDDefinitelyWritable)
		{
			return false;
		}
		else
		{
			try
			{
				return m_metaData.isDefinitelyWritable (column);
			}
			catch (SQLException ex)
			{
				m_features.noRSMDDefinitelyWritable = true;
				return false;
			}
		}
	}

	public int isNullable (int column) throws SQLException
	{
		return m_metaData.isNullable (column);
	}

	public int getScale (int column) throws SQLException
	{
		return m_metaData.getScale (column);
	}

	public int getPrecision (int column) throws SQLException
	{
		return m_metaData.getPrecision (column);
	}
}

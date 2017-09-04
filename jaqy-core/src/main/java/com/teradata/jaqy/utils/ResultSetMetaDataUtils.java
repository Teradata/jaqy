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
package com.teradata.jaqy.utils;

import java.io.PrintWriter;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.interfaces.Display;

/**
 * @author	Heng Yuan
 */
public class ResultSetMetaDataUtils
{
	public static ColumnInfo[] getColumnInfo (ResultSetMetaData meta) throws SQLException
	{
		int columnCount = meta.getColumnCount ();
		ColumnInfo[] columnInfos = new ColumnInfo[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			ColumnInfo columnInfo = new ColumnInfo ();
			columnInfos[i] = columnInfo;

			columnInfo.autoIncrement = meta.isAutoIncrement (i + 1);
			columnInfo.caseSensitive = meta.isCaseSensitive (i + 1);
			columnInfo.searchable = meta.isSearchable (i + 1);
			columnInfo.currency = meta.isCurrency (i + 1);
			columnInfo.nullable = meta.isNullable (i + 1);
			columnInfo.signed = meta.isSigned (i + 1);
			columnInfo.displaySize = meta.getColumnDisplaySize (i + 1);
			columnInfo.label = meta.getColumnLabel (i + 1);
			columnInfo.name = meta.getColumnName (i + 1);
			columnInfo.schemaName = meta.getSchemaName (i + 1);
			columnInfo.precision = meta.getPrecision (i + 1);
			columnInfo.scale = meta.getScale (i + 1);
			columnInfo.tableName = meta.getTableName (i + 1);
			columnInfo.catalogName = meta.getCatalogName (i + 1);
			columnInfo.type = meta.getColumnType (i + 1);
			columnInfo.typeName = meta.getColumnTypeName (i + 1);
			columnInfo.className = meta.getColumnClassName (i + 1);
			columnInfo.readOnly = meta.isReadOnly (i + 1);
			columnInfo.writable = meta.isWritable (i + 1);
			columnInfo.definitelyWritable= meta.isDefinitelyWritable (i + 1);
		}
		return columnInfos;
	}

	private static void dumpColumn (PrintWriter pw, JaqyResultSetMetaData metaData, int column)
	{
		JdbcFeatures features = metaData.getFeatures ();
		try
		{
			AttributeUtils.print (pw, 1, "Index", column);
			AttributeUtils.print (pw, 2, "Name", metaData.getColumnName (column));
			AttributeUtils.print (pw, 2, "Label", metaData.getColumnLabel (column));
			AttributeUtils.print (pw, 2, "Type", TypesUtils.getTypeName (metaData.getColumnType (column)));
			AttributeUtils.print (pw, 2, "SQL Type", metaData.getColumnTypeName (column));
			AttributeUtils.print (pw, 2, "Java Class", metaData.getColumnClassName (column));
			AttributeUtils.print (pw, 2, "Catalog", metaData.getCatalogName (column), features.noRSMDCatalog);
			AttributeUtils.print (pw, 2, "Schema", metaData.getSchemaName (column), features.noRSMDSchema);
			AttributeUtils.print (pw, 2, "Table Name", metaData.getTableName (column), features.noRSMDTable);
			AttributeUtils.print (pw, 2, "Display Size", metaData.getColumnDisplaySize (column));
			AttributeUtils.print (pw, 2, "Precision", metaData.getPrecision (column));
			AttributeUtils.print (pw, 2, "Scale", metaData.getScale (column));

			AttributeUtils.print (pw, 2, "Auto Increment", metaData.isAutoIncrement (column));
			AttributeUtils.print (pw, 2, "Case Sensitive", metaData.isCaseSensitive (column));
			AttributeUtils.print (pw, 2, "Currency", metaData.isCurrency (column));
			AttributeUtils.print (pw, 2, "Definitely Writable", metaData.isDefinitelyWritable (column), features.noRSMDDefinitelyWritable);
			AttributeUtils.print (pw, 2, "Nullable", metaData.isNullable (column));
			AttributeUtils.print (pw, 2, "Read Only", metaData.isReadOnly (column));
			AttributeUtils.print (pw, 2, "Searchable", metaData.isSearchable (column), features.noRSMDSearchable);
			AttributeUtils.print (pw, 2, "Signed", metaData.isSigned (column), features.noRSMDSigned);
			AttributeUtils.print (pw, 2, "Writable", metaData.isWritable (column), features.noRSMDWritable);
		}
		catch (Throwable t)
		{
		}
	}

	public static void dump (Display display, Session session, JaqyResultSetMetaData metaData) throws SQLException
	{
		PrintWriter pw = display.getPrintWriter ();
		int columnCount = metaData.getColumnCount ();
		AttributeUtils.print (pw, 0, "ResultSet Column Count", columnCount);
		for (int i = 1; i <= columnCount; ++i)
		{
			dumpColumn (pw, metaData, i);
		}
	}
}

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
package com.teradata.jaqy.utils;

import java.io.PrintWriter;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.SchemaInfo;

/**
 * @author	Heng Yuan
 */
public class ResultSetMetaDataUtils
{
	public static SchemaInfo getColumnInfo (ResultSetMetaData meta, JaqyHelper helper) throws SQLException
	{
		int columnCount = meta.getColumnCount ();
		FullColumnInfo[] columnInfos = new FullColumnInfo[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			FullColumnInfo columnInfo = new FullColumnInfo ();
			columnInfos[i] = columnInfo;

			int column = i + 1;
			columnInfo.autoIncrement = meta.isAutoIncrement (column);
			columnInfo.caseSensitive = meta.isCaseSensitive (column);
			columnInfo.searchable = meta.isSearchable (column);
			columnInfo.currency = meta.isCurrency (column);
			columnInfo.nullable = meta.isNullable (column);
			columnInfo.signed = meta.isSigned (column);
			columnInfo.displaySize = meta.getColumnDisplaySize (column);
			columnInfo.label = meta.getColumnLabel (column);
			columnInfo.name = meta.getColumnName (column);
			columnInfo.schemaName = meta.getSchemaName (column);
			columnInfo.precision = meta.getPrecision (column);
			columnInfo.scale = meta.getScale (column);
			columnInfo.tableName = meta.getTableName (column);
			columnInfo.catalogName = meta.getCatalogName (column);
			columnInfo.type = meta.getColumnType (column);
			columnInfo.typeName = meta.getColumnTypeName (column);
			columnInfo.className = meta.getColumnClassName (column);
			columnInfo.readOnly = meta.isReadOnly (column);
			columnInfo.writable = meta.isWritable (column);
			columnInfo.definitelyWritable= meta.isDefinitelyWritable (column);

			if (helper != null)
			{
				helper.fixColumnInfo (columnInfo);
			}
		}
		return new SchemaInfo (columnInfos);
	}

	private static void dumpColumn (Display display, PrintWriter pw, JaqyResultSetMetaData metaData, int column)
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
			display.getGlobals ().log (Level.INFO, t);
		}
	}

	public static void dump (Display display, Session session, JaqyResultSetMetaData metaData) throws SQLException
	{
		PrintWriter pw = display.getPrintWriter ();
		int columnCount = metaData.getColumnCount ();
		AttributeUtils.print (pw, 0, "ResultSet Column Count", columnCount);
		for (int i = 1; i <= columnCount; ++i)
		{
			dumpColumn (display, pw, metaData, i);
		}
	}
}

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
import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.Log;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyParameterMetaData;
import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.schema.ParameterInfo;

/**
 * @author	Heng Yuan
 */
public class ParameterMetaDataUtils
{
	private final static String[] MODES = { "Unknown", "IN", "IN/OUT", null, "OUT" };

	private static void dumpColumn (PrintWriter pw, JaqyParameterMetaData metaData, int column, Display display)
	{
//		JdbcFeatures features = metaData.getFeatures ();
		try
		{
			AttributeUtils.print (pw, 1, "Index", column);
			AttributeUtils.print (pw, 2, "Type", TypesUtils.getTypeName (metaData.getParameterType (column)));
			AttributeUtils.print (pw, 2, "SQL Type", metaData.getParameterTypeName (column));
			AttributeUtils.print (pw, 2, "Java Class", metaData.getParameterClassName (column));
			AttributeUtils.print (pw, 2, "Precision", metaData.getPrecision (column));
			AttributeUtils.print (pw, 2, "Scale", metaData.getScale (column));
			AttributeUtils.print (pw, 2, "Nullable", metaData.isNullable (column));
			AttributeUtils.print (pw, 2, "Signed", metaData.isSigned (column));
			AttributeUtils.print (pw, 2, "Mode", MODES[metaData.getParameterMode (column)]);
		}
		catch (Throwable t)
		{
			Log.log (Level.INFO, t);
		}
	}

	public static void dump (Display display, Session session, JaqyParameterMetaData metaData) throws SQLException
	{
		PrintWriter pw = display.getPrintWriter ();
		int columnCount = metaData.getParameterCount ();
		AttributeUtils.print (pw, 0, "Parameter Count", columnCount);
		for (int i = 1; i <= columnCount; ++i)
		{
			dumpColumn (pw, metaData, i, display);
		}
	}

	public static ParameterInfo[] getParameterInfos (JaqyParameterMetaData meta) throws SQLException
	{
		int count = meta.getParameterCount ();
		ParameterInfo[] parameterInfos = new ParameterInfo[count];
		for (int i = 0; i < count; ++i)
		{
			parameterInfos[i] = new ParameterInfo ();
			parameterInfos[i].type = meta.getParameterType (i + 1);
			parameterInfos[i].typeName = meta.getParameterTypeName (i + 1);
			parameterInfos[i].className = meta.getParameterClassName (i + 1);
			parameterInfos[i].precision = meta.getPrecision (i + 1);
			parameterInfos[i].scale = meta.getScale (i + 1);
			parameterInfos[i].nullable = meta.isNullable (i + 1);
			parameterInfos[i].signed = meta.isSigned (i + 1);
			parameterInfos[i].mode = meta.getParameterMode (i + 1);
		}
		return parameterInfos;
	}
}

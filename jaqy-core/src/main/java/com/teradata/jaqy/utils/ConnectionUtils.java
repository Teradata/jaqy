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
import java.sql.DatabaseMetaData;

import com.teradata.jaqy.Session;
import com.teradata.jaqy.interfaces.Display;

/**
 * @author	Heng Yuan
 */
public class ConnectionUtils
{
	public static void dump (Display display, Session session, DatabaseMetaData metaData)
	{
		try
		{
			PrintWriter pw = display.getPrintWriter ();
			AttributeUtils.print (pw, 0, "Database Product Name", metaData.getDatabaseProductName ());
			AttributeUtils.print (pw, 0, "Database Product Version", metaData.getDatabaseProductVersion ());
		}
		catch (Exception ex)
		{
		}
	}
}

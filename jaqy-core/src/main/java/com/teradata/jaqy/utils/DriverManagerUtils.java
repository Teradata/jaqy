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

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import com.teradata.jaqy.JaqyDriverManager;

/**
 * @author	Heng Yuan
 */
public class DriverManagerUtils
{
	private static Object s_lock = new Object ();

	public static Collection<Driver> getDrivers ()
	{
		ArrayList<Driver> drivers = new ArrayList<Driver> ();
		synchronized (s_lock)
		{
			for (Enumeration<Driver> e = DriverManager.getDrivers ();
				 e.hasMoreElements ();)
			{
				drivers.add (e.nextElement ());
			}
		}
		return drivers;
	}

	public static String getProtocol (JaqyDriverManager driverManager, String url)
	{
		if (url.startsWith ("jdbc:"))
			url = url.substring ("jdbc:".length ());

		int index = 0;
		while ((index = url.indexOf (':', index + 1)) > 0)
		{
			String protocol = url.substring (0, index);
			if (driverManager.getDriverClass (protocol) != null)
				return protocol;
		}
		return null;
	}
}

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
package com.teradata.jaqy;

import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.utils.DriverManagerUtils;

/**
 * @author	Heng Yuan
 */
public class Logging
{
	private static PrintWriter s_originalLogWriter;
	private static Level s_logLevel = Level.OFF;
	private static Object s_lock = new Object ();

	public static void init ()
	{
		s_originalLogWriter = DriverManager.getLogWriter ();
	}

	public static Level getLevel ()
	{
		return s_logLevel;
	}

	public static void setLogLevel (Display display, Level level)
	{
		synchronized (s_lock)
		{
			if (s_logLevel == level)
				return;

			if (level != Level.OFF)
			{
				if (s_logLevel == Level.OFF)
				{
					DriverManager.setLogWriter (display.getPrintWriter ());
				}
			}

			for (Driver driver : DriverManagerUtils.getDrivers ())
			{
				try
				{
					Logger logger = driver.getParentLogger ();
					if (logger != null)
						logger.setLevel (level);
				}
				catch (SQLFeatureNotSupportedException ex)
				{
				}
			}

			if (level == Level.OFF)
			{
				if (s_logLevel != Level.OFF)
				{
					DriverManager.setLogWriter (s_originalLogWriter);
				}
			}
			s_logLevel = level;
		}
	}

	static void setDriverLog (Driver driver)
	{
		synchronized (s_lock)
		{
			if (s_logLevel != Level.OFF)
			{
				try
				{
					Logger logger = driver.getParentLogger ();
					if (logger != null)
						logger.setLevel (s_logLevel);
				}
				catch (SQLFeatureNotSupportedException ex)
				{
				}
			}
		}
	}
}

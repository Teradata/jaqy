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

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.utils.DriverManagerUtils;
import com.teradata.jaqy.utils.PathUtils;
import com.teradata.jaqy.utils.URLUtils;

/**
 * This class manages all the JDBC drivers loaded, and the current JDBC driver
 * to be used.
 *
 * @author	Heng Yuan
 */
public class JaqyDriverManager
{
	/**
	 * This is the protocolName -> Driver mapping for internally loaded
	 * JDBC drivers.
	 */
	private final HashMap<String,WeakReference<Driver>> m_drivers = new HashMap<String, WeakReference<Driver>> ();
	/**
	 * This is the protocolName -> className mapping for JDBC driver
	 * name aliases.
	 */
	private final HashMap<String,String> m_driverProtocolMap = new HashMap<String, String> ();
	/**
	 * This is the protocolName -> jarLocation mapping for JDBC driver
	 * locations.
	 */
	private final HashMap<String,String> m_driverLocationMap = new HashMap<String, String> ();

	JaqyDriverManager ()
	{
	}

	public boolean isDriverLoaded (String protocolName)
	{
		synchronized (m_drivers)
		{
			WeakReference<Driver> driver = m_drivers.get (protocolName);
			return driver != null && driver.get () != null;
		}
	}

	public void addDriverProtocol (String protocolName, String className)
	{
		synchronized (m_driverProtocolMap)
		{
			m_driverProtocolMap.put (protocolName, className);
		}
	}

	public Map<String,String> getDriverProtocolMap ()
	{
		TreeMap<String, String> map = new TreeMap<String, String> ();
		synchronized (m_driverProtocolMap)
		{
			map.putAll (m_driverProtocolMap);
		}
		return map;
	}

	public String getDriverClass (String protocolName)
	{
		synchronized (m_driverProtocolMap)
		{
			return m_driverProtocolMap.get (protocolName);
		}
	}

	/**
	 * This function is to register JDBC driver location
	 * @param	protocolName
	 *			the name of the JDBC driver.  It can be the shorthand name
	 *			or the complete driver class name.
	 * @param	location
	 *			jar file locations
	 */
	public void addDriverLocation (String protocolName, String location)
	{
		synchronized (m_driverLocationMap)
		{
			m_driverLocationMap.put (protocolName, location);
		}
	}

	public String getDriverLocation (String protocolName)
	{
		synchronized (m_driverProtocolMap)
		{
			return m_driverLocationMap.get (protocolName);
		}
	}

	public Map<String,String> getDriverLocationMap ()
	{
		TreeMap<String,String> map = new TreeMap<String, String> ();
		synchronized (m_driverLocationMap)
		{
			map.putAll (m_driverLocationMap);
		}
		return map;
	}

	/**
	 * Load a JDBC driver using the protocol name.
	 *
	 * @param	display
	 *			the current display.
	 * @param	interpreter
	 *			the current interpreter.
	 * @param	protocolName
	 *			the JDBC protocol.
	 * @return	true if the driver has been loaded successfully.
	 *			false otherwise.
	 */
	public boolean loadDriver (Display display, JaqyInterpreter interpreter, String protocolName)
	{
		// Check if we already have the driver for the protocol loaded.
		if (isDriverLoaded (protocolName))
			return true;

		// get the class name
		String className = getDriverClass (protocolName);
		if (className == null)
		{
			interpreter.error ("driver class name not specified for " + protocolName);
			return false;
		}

		ClassLoader cl = null;

		// get the loading path for the driver
		//
		// It is possible path is null if is part of the Jaqy start up path.
		String path = getDriverLocation (protocolName);

		if (path != null)
		{
			try
			{
				String[] paths = PathUtils.split (path);
				URL[] urls = new URL[paths.length];
				for (int i = 0; i < paths.length; ++i)
				{
					urls[i] = URLUtils.getFileURL (paths[i]);
				}
				cl = new URLClassLoader (urls);
			}
			catch (Exception ex)
			{
				ex.printStackTrace (display.getPrintWriter ());
				interpreter.error ("invalid jar file path: " + path);
				return false;
			}
		}

		// Now load the driver
		try
		{
			Class<?> c;
			if (cl == null)
				c = Class.forName (className);
			else
				c = Class.forName (className, true, cl);

			if (!Driver.class.isAssignableFrom (c))
			{
				interpreter.error ("not a JDBC driver: " + className);
				return false;
			}
			if (cl != null)
			{
				// we need to wrap the Driver
				//
				// JDBC DriverManager will not load a driver that is
				// loaded by a different class loader than the one
				// that we are using.
				JDBCWrapperDriver wrapperDriver = new JDBCWrapperDriver ((Driver)c.newInstance ());
				DriverManager.registerDriver (wrapperDriver);
				registerDriver (protocolName, wrapperDriver);
			}
			else
			{
				for (Driver driver : DriverManagerUtils.getDrivers ())
				{
					if (className.equals (driver.getClass ().getCanonicalName ()))
					{
						registerDriver (protocolName, driver);
						break;
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			interpreter.error ("driver not found: " + className);
			return false;
		}
	}

	private void registerDriver (String protocolName, Driver driver)
	{
		Logging.setDriverLog (driver);
		synchronized (m_drivers)
		{
			m_drivers.put (protocolName, new WeakReference<Driver> (driver));
		}
	}
}

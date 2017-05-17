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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This wrapper JDBC Driver class is used to allow loading JDBC drivers
 * dynamically
 * (i.e. JDBC driver loaded by a different ClassLoader than the system).
 *
 * This code is taken from http://www.kfu.com/~nsayer/Java/dyn-jdbc.html, which
 * is presumably in the public domain.
 *
 * @author	Heng Yuan
 */
public class JDBCWrapperDriver implements Driver
{
	private Driver m_driver;

	public JDBCWrapperDriver (Driver driver)
	{
		m_driver = driver;
	}

	@Override
	public boolean acceptsURL (String u) throws SQLException
	{
		return m_driver.acceptsURL (u);
	}

	@Override
	public Connection connect (String u, Properties p) throws SQLException
	{
		return m_driver.connect (u, p);
	}

	@Override
	public int getMajorVersion ()
	{
		return m_driver.getMajorVersion ();
	}

	@Override
	public int getMinorVersion ()
	{
		return m_driver.getMinorVersion ();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo (String u, Properties p) throws SQLException
	{
		return m_driver.getPropertyInfo (u, p);
	}

	@Override
	public boolean jdbcCompliant ()
	{
		return m_driver.jdbcCompliant ();
	}

	@Override
	public Logger getParentLogger () throws SQLFeatureNotSupportedException
	{
		return m_driver.getParentLogger ();
	}

	public Driver getInternalDriver ()
	{
		return m_driver;
	}
}

/*
 * Copyright (c) 2017-2021 Teradata
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
package com.teradata.jaqy.command;

import java.sql.Driver;
import java.sql.SQLException;

import com.teradata.jaqy.JDBCWrapperDriver;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.utils.DriverManagerUtils;

/**
 * @author	Heng Yuan
 */
public class DriverCommand extends JaqyCommandAdapter
{
	public DriverCommand ()
	{
		super ("driver");
	}

	@Override
	public String getDescription ()
	{
		return "displays currently loaded JDBC drivers.";
	}

	@Override
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
	{
		listDrivers (interpreter);
	}

	private void listDrivers (JaqyInterpreter interpreter) throws SQLException
	{
		PropertyTable pt = new PropertyTable (new String[] { "Driver", "Version", "Compliant" } );
		for (Driver driver : DriverManagerUtils.getDrivers ())
		{
			String name;
			if (driver instanceof JDBCWrapperDriver)
			{
				name = ((JDBCWrapperDriver)driver).getInternalDriver ().getClass ().getCanonicalName ();
			}
			else
			{
				name = driver.getClass ().getCanonicalName ();
			}
			String version = "";
			if (driver.getMajorVersion () < 10)
				version = " ";
			version += driver.getMajorVersion () + "." + driver.getMinorVersion ();
			String jdbcCompliant = driver.jdbcCompliant () ? "T" : "F";
			pt.addRow (new String[] { name, version, jdbcCompliant });
		}
		interpreter.print (pt);
	}
}

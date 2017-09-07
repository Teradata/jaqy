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
package com.teradata.jaqy.db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

/**
 * @author	Heng Yuan
 */
public class PostgreSQLTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/ansi_types.sql", "../tests/unittests/postgresql/control/ansi_types.control");
	}

	@Test
	public void test2 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/transaction.sql", "../tests/unittests/postgresql/control/transaction.control");
	}

	@Test
	public void test3 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/data_types.sql", "../tests/unittests/postgresql/control/data_types.control");
	}

	@Test
	public void test4 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/json_type.sql", "../tests/unittests/postgresql/control/json_type.control");
	}

	@Test
	public void test5 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/array_types.sql", "../tests/unittests/postgresql/control/array_types.control");
	}

	@Test
	public void test6 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/composite_types.sql", "../tests/unittests/postgresql/control/composite_types.control");
	}

	@Test
	public void test7 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/postgresql/gis.sql", "../tests/unittests/postgresql/control/gis.control");
	}
}

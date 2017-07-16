package com.teradata.jaqy.db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class SQLiteTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/sqlite/ansi_types.sql", "../tests/unittests/sqlite/control/ansi_types.control");
	}

	@Test
	public void test2 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/sqlite/transaction.sql", "../tests/unittests/sqlite/control/transaction.control");
	}

	@Test
	public void test3 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/sqlite/types.sql", "../tests/unittests/sqlite/control/types.control");
	}
}

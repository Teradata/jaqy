package com.teradata.jaqy.db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class MySQLTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/mysql/ansi_types.sql", "../tests/unittests/mysql/control/ansi_types.control");
	}

	@Test
	public void test2 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/mysql/transaction.sql", "../tests/unittests/mysql/control/transaction.control");
	}
}

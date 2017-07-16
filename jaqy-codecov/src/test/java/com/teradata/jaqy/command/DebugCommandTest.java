package com.teradata.jaqy.command;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class DebugCommandTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/debug/preparedstatement.sql", "../tests/unittests/debug/control/preparedstatement.control");
	}

	@Test
	public void test2 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/debug/resultset.sql", "../tests/unittests/debug/control/resultset.control");
	}
}

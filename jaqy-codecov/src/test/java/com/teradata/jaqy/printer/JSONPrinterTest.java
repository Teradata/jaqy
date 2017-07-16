package com.teradata.jaqy.printer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class JSONPrinterTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/format/json.sql", "../tests/unittests/format/control/json.control");
	}
}

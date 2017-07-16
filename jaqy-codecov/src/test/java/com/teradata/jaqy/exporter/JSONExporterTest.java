package com.teradata.jaqy.exporter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class JSONExporterTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/json/json_export_1.sql", "../tests/unittests/json/control/json_export_1.control");
	}
}

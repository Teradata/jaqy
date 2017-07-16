package com.teradata.jaqy.exporter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class CSVExporterTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/csv/csv_export_1.sql", "../tests/unittests/csv/control/csv_export_1.control");
	}
}

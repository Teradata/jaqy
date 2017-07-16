package com.teradata.jaqy.importer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class CSVImporterTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/csv/csv_import_1.sql", "../tests/unittests/csv/control/csv_import_1.control");
	}

	@Test
	public void test2 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/csv/csv_import_2.sql", "../tests/unittests/csv/control/csv_import_2.control");
	}
}

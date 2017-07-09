package com.teradata.jaqy.importer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class AvroExporterTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void testImport1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/avro/avro_export_1.sql", "../tests/unittests/avro/control/avro_export_1.control");
	}
}

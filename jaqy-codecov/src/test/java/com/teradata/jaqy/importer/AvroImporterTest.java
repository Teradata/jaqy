package com.teradata.jaqy.importer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.utils.TestUtils;

public class AvroImporterTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void testImport1 () throws Exception
	{
		TestUtils.jaqyTest (testFolder, "../tests/unittests/avro/avro_import_1.sql", "../tests/unittests/avro/control/avro_import_1.control");
	}
}

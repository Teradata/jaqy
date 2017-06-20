package com.teradata.jaqy.importer;

import java.io.File;

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

public class AvroImporterFactory extends JaqyHandlerFactoryImpl<AvroImporter>
{
	public AvroImporterFactory ()
	{
	}

	@Override
	public String getName ()
	{
		return "avro";
	}

	@Override
	public AvroImporter getHandler (CommandLine cmdLine) throws Exception
	{
		String[] args = cmdLine.getArgs ();
		if (args.length == 0)
			throw new IllegalArgumentException ("missing file name.");
		File file = new File (args[0]);

		return new AvroImporter (file);
	}
}

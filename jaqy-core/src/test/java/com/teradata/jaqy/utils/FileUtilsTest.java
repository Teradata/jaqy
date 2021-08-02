/*
 * Copyright (c) 2021 Teradata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teradata.jaqy.utils;

import java.io.*;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.path.FilePath;


/**
 * @author	Heng Yuan
 */
public class FileUtilsTest
{
	@Rule
    public TemporaryFolder m_folder = new TemporaryFolder();

	@Test
	public void testReaderCompare () throws Exception
	{
		String s1 = "This is a test a";
		String s2 = "This is a test b";
		String s3 = "This is a test a ";

		StringReader r1;
		StringReader r2;

		r1 = new StringReader (s1);
		r2 = new StringReader (s2);
		Assert.assertEquals (0, FileUtils.compare (r1, r1));
		r1.close ();
		r2.close ();

		r1 = new StringReader (s1);
		r2 = new StringReader (s2);
		Assert.assertEquals (-1, FileUtils.compare (r1, r2));
		r1.close ();
		r2.close ();

		r1 = new StringReader (s1);
		r2 = new StringReader (s2);
		Assert.assertEquals (1, FileUtils.compare (r2, r1));
		r1.close ();
		r2.close ();

		r1 = new StringReader (s1);
		r2 = new StringReader (s3);
		Assert.assertEquals (-1, FileUtils.compare (r1, r2));
		r1.close ();
		r2.close ();

		r1 = new StringReader (s1);
		r2 = new StringReader (s3);
		Assert.assertEquals (1, FileUtils.compare (r2, r1));
		r1.close ();
		r2.close ();
	}

	@Test
	public void testInputStreamCompare () throws Exception
	{
		byte[] s1 = "This is a test a".getBytes ();
		byte[] s2 = "This is a test b".getBytes ();
		byte[] s3 = "This is a test a ".getBytes ();

		ByteArrayInputStream r1;
		ByteArrayInputStream r2;

		r1 = new ByteArrayInputStream (s1);
		r2 = new ByteArrayInputStream (s2);
		Assert.assertEquals (0, FileUtils.compare (r1, r1));
		r1.close ();
		r2.close ();

		r1 = new ByteArrayInputStream (s1);
		r2 = new ByteArrayInputStream (s2);
		Assert.assertEquals (-1, FileUtils.compare (r1, r2));
		r1.close ();
		r2.close ();

		r1 = new ByteArrayInputStream (s1);
		r2 = new ByteArrayInputStream (s2);
		Assert.assertEquals (1, FileUtils.compare (r2, r1));
		r1.close ();
		r2.close ();

		r1 = new ByteArrayInputStream (s1);
		r2 = new ByteArrayInputStream (s3);
		Assert.assertEquals (-1, FileUtils.compare (r1, r2));
		r1.close ();
		r2.close ();

		r1 = new ByteArrayInputStream (s1);
		r2 = new ByteArrayInputStream (s3);
		Assert.assertEquals (1, FileUtils.compare (r2, r1));
		r1.close ();
		r2.close ();
	}

	@Test
	public void testCopyReader () throws Exception
	{
		String s1 = StringUtils.repeat ('a', 10000);
		StringReader sr = new StringReader (s1);

		StringWriter sw = new StringWriter ();
		FileUtils.copy (sw, sr, new char[100]);

		Assert.assertEquals (s1, sw.toString ());
	}

	@Test
	public void testCopyStream () throws Exception
	{
		byte[] s1 = StringUtils.repeat ('a', 10000).getBytes ();
		ByteArrayInputStream is = new ByteArrayInputStream (s1);

		ByteArrayOutputStream os = new ByteArrayOutputStream ();
		FileUtils.copy (os, is, new byte[100]);

		Assert.assertArrayEquals (s1, os.toByteArray ());
	}

	@Test
	public void writeFileTest () throws Exception
	{
		String s1 = "This is a test";

		File file = m_folder.newFile ("writeFile.txt");
		FilePath filePath = new FilePath (file);

		FileUtils.writeFile (filePath, s1);

		String s2 = "This" + FileUtils.readString (filePath, 4, s1.length () - 4);

		Assert.assertEquals (s1, s2);
	}
}

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
package com.teradata.jaqy.resultset;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.path.FilePath;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class FileBlobTest
{
	@Rule
    public TemporaryFolder m_folder = new TemporaryFolder ();

	@Test
	public void testBlob () throws Exception
	{
		String s1 = "This is a test";
		byte[] b1 = s1.getBytes ();
		ByteArrayInputStream is1;
		InputStream is2;

		File file = new File (m_folder.getRoot (), "b1.txt");
		FilePath filePath = new FilePath (file);
		FileUtils.writeFile (filePath, b1);

		FileBlob blob = new FileBlob (filePath);
		Assert.assertEquals (s1.length (), blob.length ());

		Assert.assertArrayEquals (b1, blob.getBytes (1, (int)blob.length ()));

		is1 = new ByteArrayInputStream (b1);
		is2 = blob.getBinaryStream ();
		Assert.assertEquals (0, FileUtils.compare (is1, is2));
		is1.close ();
		is2.close ();
	}

	public void testBlob2 () throws Exception
	{
		File file = new File (m_folder.getRoot (), "b2.txt");
		FilePath filePath = new FilePath (file);
		FileBlob blob = new FileBlob (filePath);
		Assert.assertEquals (0, blob.length ());
	}

	@Test (expected = SQLException.class)
	public void testBlob3 () throws Exception
	{
		File file = new File (m_folder.getRoot (), "b3.txt");
		FilePath filePath = new FilePath (file);
		FileBlob blob = new FileBlob (filePath);
		blob.getBinaryStream ();
	}

	@Test (expected = SQLException.class)
	public void testBlob4 () throws Exception
	{
		File file = new File (m_folder.getRoot (), "b4.txt");
		FilePath filePath = new FilePath (file);
		FileBlob blob = new FileBlob (filePath);
		blob.getBytes (0, 1);
	}

	@Test (expected = SQLException.class)
	public void testBlob5 () throws Exception
	{
		File file = new File (m_folder.getRoot (), "b5.txt");
		FilePath filePath = new FilePath (file);
		FileBlob blob = new FileBlob (filePath);
		blob.getBytes (1, -1);
	}

	@Test (expected = SQLException.class)
	public void testBlob6 () throws Exception
	{
		File file = m_folder.newFile ("b6.txt");
		FilePath filePath = new FilePath (file);
		FileBlob blob = new FileBlob (filePath);
		blob.getBytes (1, 2);
	}
}

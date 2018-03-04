/*
 * Copyright (c) 2017-2018 Teradata
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePathHandler;

/**
 * @author	Heng Yuan
 */
public class FilePathHandlerTest
{
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder ();

	@Test
	public void test1 () throws Exception
	{
		FilePathHandler handler = new FilePathHandler ();

		File file = testFolder.newFile ();
		Path path;

		path = handler.getPath (file.getAbsolutePath ());
		Assert.assertNotNull (path);
		OutputStream os = path.getOutputStream ();
		String str = "abcdefghijklmnopqrstuvwxyz";
		os.write (str.getBytes ("UTF-8"));
		os.close ();
		Assert.assertEquals (file.getName (), path.getName ());
		Assert.assertEquals (file.getCanonicalPath (), path.getFullName ());
		Assert.assertTrue (path.exists ());
		Assert.assertFalse (path.isDirectory ());
		Assert.assertTrue (path.isFile ());
		Assert.assertEquals (str.length (), path.length ());
		Assert.assertEquals (0, FileUtils.compare (new ByteArrayInputStream (str.getBytes ("UTF-8")), path.getInputStream ()));

		path = path.getParent ();
		Assert.assertNotNull (path);
		Assert.assertTrue (path.exists ());
		Assert.assertTrue (path.isDirectory ());
		Assert.assertFalse (path.isFile ());
	}
}

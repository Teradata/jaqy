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
package com.teradata.jaqy.path;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.Path;

/**
 * @author	Heng Yuan
 */
public class URLPathTest
{
	@Test
	public void test1 () throws Exception
	{
		HttpPathHandler handler = new HttpPathHandler ();

		String url = "https://introcs.cs.princeton.edu/java/data/DJIA.csv";
		String parent = "https://introcs.cs.princeton.edu/java/data";
		Path path = handler.getPath (url, null);
		Assert.assertNotNull (path);
		Assert.assertEquals (url, path.getPath ());
		Assert.assertEquals (url, path.getCanonicalPath ());
		Assert.assertTrue (path.exists ());
		Assert.assertEquals (1071673, path.length ());
		Assert.assertEquals (1071673, path.length ());
		Assert.assertTrue (path.isFile ());

		path = path.getParent ();
		Assert.assertEquals (parent, path.getPath ());
		path = path.getRelativePath ("elements.csv");
		Assert.assertTrue (path.isFile ());

		path = path.getParent ();
		path = path.getRelativePath ("/java/data/DJIA.csv");
		Assert.assertEquals (url, path.getPath ());

		path = path.getParent ();
		path = path.getRelativePath ("../../java/data/DJIA.csv");
		Assert.assertEquals (url, path.getPath ());

		path = path.getParent ();
		path = path.getRelativePath ("../../java/data/abcdefghijklmnopqrstuvw.csv");
		Assert.assertFalse (path.exists ());
		Assert.assertFalse (path.exists ());
	}

	@Test (expected = IOException.class)
	public void test2 () throws Exception
	{
		HttpPathHandler handler = new HttpPathHandler ();

		String url = "https://www.example.com/test.csv";
		Path path = handler.getPath (url, null);
		path.getOutputStream ();
	}
}

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

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.Os;

/**
 * @author	Heng Yuan
 */
public class PathUtilsTest
{
	@Test
	public void testPathSplit ()
	{
		String[] paths;

		paths = PathUtils.split ("a:b");
		Assert.assertArrayEquals (new String[] { "a", "b" }, paths);

		paths = PathUtils.split ("a;b");
		Assert.assertArrayEquals (new String[] { "a", "b" }, paths);

		Os os = new Os ();
		if (os.isWindows ())
		{
			// Windows tests
			paths = PathUtils.split ("/abc/def:lib/def");
			Assert.assertArrayEquals (new String[] { "\\abc\\def", "lib\\def" }, paths);
	
			paths = PathUtils.split ("C:\\abc");
			Assert.assertArrayEquals (new String[] { "C:\\abc" }, paths);
	
			paths = PathUtils.split ("a:C:\\abc");
			Assert.assertArrayEquals (new String[] { "a", "C:\\abc" }, paths);
	
			paths = PathUtils.split ("a;C:\\abc");
			Assert.assertArrayEquals (new String[] { "a", "C:\\abc" }, paths);
		}
		else
		{
			// Unix tests
			paths = PathUtils.split ("/abc/def:lib/def");
			Assert.assertArrayEquals (new String[] { "/abc/def", "lib/def" }, paths);

			paths = PathUtils.split ("/abc/def;lib/def");
			Assert.assertArrayEquals (new String[] { "/abc/def", "lib/def" }, paths);
		}
	}
}

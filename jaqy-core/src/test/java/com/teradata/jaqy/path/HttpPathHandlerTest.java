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

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.Path;

/**
 * @author	Heng Yuan
 */
public class HttpPathHandlerTest
{
	@Test
	public void test1 () throws Exception
	{
		HttpPathHandler handler = new HttpPathHandler ();

		String url = "http://introcs.cs.princeton.edu/java/data/DJIA.csv";
		Assert.assertTrue (handler.canHandle (url));
		Assert.assertTrue (handler.canHandle ("https://introcs.cs.princeton.edu/java/data/DJIA.csv"));
		Assert.assertFalse (handler.canHandle ("C:\\abc"));
		Assert.assertFalse (handler.canHandle ("C:\\temp\\abc"));
		Assert.assertFalse (handler.canHandle ("/tmp/abc"));

		Path path = handler.getPath (url);
		Assert.assertEquals (url, path.getPath ());
	}
}

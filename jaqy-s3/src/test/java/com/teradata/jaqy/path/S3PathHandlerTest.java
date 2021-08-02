/*
 * Copyright (c) 2017-2021 Teradata
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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public class S3PathHandlerTest
{
	@Test
	public void test1 () throws Exception
	{
		S3PathHandler handler = new S3PathHandler ();

		String url = "s3://abc/test.csv";
		Assert.assertTrue (handler.canHandle (url));
		Assert.assertFalse (handler.canHandle ("C:\\abc"));
		Assert.assertFalse (handler.canHandle ("C:\\temp\\abc"));
		Assert.assertFalse (handler.canHandle ("/tmp/abc"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test2 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		S3PathHandler handler = new S3PathHandler ();

		handler.getPath ("s3", interpreter);
	}
}
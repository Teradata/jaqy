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
package com.teradata.jaqy.azure;

import java.io.*;

import org.junit.Assert;
import org.junit.Test;

import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;

/**
 * @author	Heng Yuan
 */
public class AzurePathHandlerTest
{
	@Test
	public void testCanHandle ()
	{
		AzurePathHandler handler = new AzurePathHandler ();

		Assert.assertTrue (handler.canHandle ("wasb:///examples/"));
		Assert.assertTrue (handler.canHandle ("wasbs:///examples"));
		Assert.assertFalse (handler.canHandle ("wasba:///examples"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test2 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		AzurePathHandler handler = new AzurePathHandler ();

		handler.getPath ("asdf", interpreter);
	}

	@Test
	public void testGetPath () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		AzurePathHandler handler = new AzurePathHandler ();
		AzureUtils.setAccount ("devstoreaccount1", interpreter);
		AzureUtils.setKey ("Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==", interpreter);
		AzureUtils.setEndPoint ("http://127.0.0.1:10000/devstoreaccount1", interpreter);

		CloudBlobClient client = AzureUtils.getBlobClient (interpreter, "devstoreaccount1");
		CloudBlobContainer container = client.getContainerReference ("testcontainer");
		container.create ();

		Path path = handler.getPath ("wasb://testcontainer@/abc/test.txt", interpreter);
		Assert.assertNotNull (path);
		OutputStream os = path.getOutputStream ();
		os.write ("Hello World".getBytes ("utf-8"));
		os.close ();

		path = handler.getPath ("wasbs://testcontainer@/abc/test.txt", interpreter);
		Assert.assertNotNull (path);
		Assert.assertTrue (path.exists ());
		Assert.assertEquals (11, path.length ());
		Assert.assertTrue (path.isFile ());
		InputStream is = path.getInputStream ();
		BufferedReader r = new BufferedReader (new InputStreamReader (is, "utf-8"));
		String line = r.readLine ();
		Assert.assertEquals ("Hello World", line);
		is.close ();

		container.delete ();
	}

	@Test(expected = IOException.class)
	public void testGetPathError1 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		AzurePathHandler handler = new AzurePathHandler ();
		AzureUtils.setAccount ("devstoreaccount1", interpreter);
		AzureUtils.setKey ("Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==", interpreter);
		AzureUtils.setEndPoint ("http://127.0.0.1:10000/devstoreaccount1", interpreter);

		handler.getPath ("wasb://abcdefg@/abc/test.txt", interpreter);
	}

	@Test(expected = IOException.class)
	public void testGetPathError2 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		AzurePathHandler handler = new AzurePathHandler ();
		handler.getPath ("http://abcdefg@example.com/abc/test.txt", interpreter);
	}
}

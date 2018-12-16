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
package com.teradata.jaqy.azure;

import org.junit.Assert;
import org.junit.Test;

import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.azure.AzurePathInfo;
import com.teradata.jaqy.azure.AzureUtils;

/**
 * @author	Heng Yuan
 */
public class AzureUtilsTest
{
	@Test
	public void testGetPathInfo () throws Exception
	{
		AzurePathInfo info;

		info = AzureUtils.getPathInfo ("wasb:///examples/abc.json");
		Assert.assertEquals ("wasb", info.protocol);
		Assert.assertNull (info.account);
		Assert.assertNull (info.container);
		Assert.assertEquals ("examples/abc.json", info.file);

		info = AzureUtils.getPathInfo ("wasbs:///examples/abc.json");
		Assert.assertEquals ("wasbs", info.protocol);
		Assert.assertNull (info.account);
		Assert.assertNull (info.container);
		Assert.assertEquals ("examples/abc.json", info.file);

		info = AzureUtils.getPathInfo ("wasbs://a@b/examples.json");
		Assert.assertEquals ("wasbs", info.protocol);
		Assert.assertEquals ("b", info.account);
		Assert.assertEquals ("a", info.container);
		Assert.assertEquals ("examples.json", info.file);

		info = AzureUtils.getPathInfo ("wasbs://a@b.blob.core.windows.net/examples.json");
		Assert.assertEquals ("wasbs", info.protocol);
		Assert.assertEquals ("b", info.account);
		Assert.assertEquals ("a", info.container);
		Assert.assertEquals ("examples.json", info.file);
	}

	@Test
	public void testGetBlobClient () throws Exception
	{
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		AzureUtils.setAccount ("devstoreaccount1", interpreter);
		AzureUtils.setKey ("Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==", interpreter);
		AzureUtils.setEndPoint ("http://127.0.0.1:10000/devstoreaccount1", interpreter);

		CloudBlobClient client = AzureUtils.getBlobClient (interpreter, "devstoreaccount1");
		CloudBlobContainer container = client.getContainerReference ("testcontainer");
		if (!container.exists ())
		{
			container.create ();
		}

		CloudBlobClient client2 = AzureUtils.getBlobClient (interpreter, "devstoreaccount1");
		Assert.assertSame (client2, client);

		client2 = AzureUtils.getBlobClient (interpreter, "abcdefg");
		Assert.assertNotSame (client2, client);

		container.delete ();
	}
}

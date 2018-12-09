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

import com.teradata.jaqy.azure.AzurePathInfo;
import com.teradata.jaqy.azure.AzureUtils;

/**
 * @author	Heng Yuan
 */
public class AzureUtilsTest
{
	@Test
	public void testGetPath () throws Exception
	{
		AzurePathInfo info;

		info = AzureUtils.getPathInfo ("wasbs:///examples/abc.json");
		Assert.assertEquals ("wasbs", info.protocol);
		Assert.assertNull (info.account);
		Assert.assertNull (info.container);
		Assert.assertEquals ("/examples/abc.json", info.file);

		info = AzureUtils.getPathInfo ("wasbs://a@b/examples.json");
		Assert.assertEquals ("wasbs", info.protocol);
		Assert.assertEquals ("b", info.account);
		Assert.assertEquals ("a", info.container);
		Assert.assertEquals ("examples.json", info.file);
	}
}

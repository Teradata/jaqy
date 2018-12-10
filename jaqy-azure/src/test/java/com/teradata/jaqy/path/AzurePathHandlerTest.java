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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;

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
		Globals globals = new Globals ();
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);

		AzurePathHandler handler = new AzurePathHandler ();

		handler.getPath ("asdf", interpreter);
	}
}

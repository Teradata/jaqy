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
package com.teradata.jaqy;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.interfaces.PathHandler;

/**
 * @author	Heng Yuan
 */
public class PathHandlerManagerTest
{
	@Test
	public void test1 ()
	{
		PathHandlerManager manager = new PathHandlerManager ();

		PathHandler handler;
		String path = "/vagrant/tests/unittests/commands/help.sql";

		handler = manager.getFilePathHandler ();
		Assert.assertNotNull (handler);
		Assert.assertTrue (handler.canHandle (path));

		handler = manager.getHandler (path);
		Assert.assertNotNull (handler);
		Assert.assertTrue (handler.canHandle (path));

		Assert.assertTrue (manager.hasPathHandler ("com.teradata.jaqy.path.FilePathHandler"));

		manager.removePathHandler ("com.teradata.jaqy.path.FilePathHandler");
		Assert.assertFalse (manager.hasPathHandler ("com.teradata.jaqy.path.FilePathHandler"));
	}
}

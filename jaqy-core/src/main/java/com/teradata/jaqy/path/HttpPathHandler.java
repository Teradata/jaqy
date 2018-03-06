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
import java.net.URL;

import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.interfaces.PathHandler;

/**
 * An extremely simple PathHandler for loading data from http URL links.
 *
 * @author	Heng Yuan
 */
public class HttpPathHandler implements PathHandler
{
	@Override
	public Path getPath (String path) throws IOException
	{
		return new URLPath (new URL (path));
	}

	@Override
	public boolean canHandle (String path)
	{
		return path.startsWith ("http://") || path.startsWith ("https://");
	}
}

/*
 * Copyright (c) 2017 Teradata
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * @author	Heng Yuan
 */
public class URLUtils
{
	public static URL getFileURL (String name) throws IOException
	{
		if (name.startsWith ("file:") ||
			name.startsWith ("http:") ||
			name.startsWith ("https:"))
		{
			return new URL (name);
		}
		File file = new File (name);
		if (!file.isFile ())
			throw new FileNotFoundException (name);
		return file.toURI ().toURL ();
	}
}

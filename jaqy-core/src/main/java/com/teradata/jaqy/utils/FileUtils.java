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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

/**
 * @author	Heng Yuan
 */
public class FileUtils
{
	public static Reader getReader (InputStream is, String encoding) throws IOException
	{
		if (encoding != null)
			return new InputStreamReader (is, encoding);

		PushbackInputStream pis = new PushbackInputStream (is, ByteOrderMarkUtils.BOM_LEN);
		encoding = ByteOrderMarkUtils.detectEncoding (pis);
		if (encoding == null)
		{
			return new InputStreamReader (pis);
		}
		else
		{
			return new InputStreamReader (pis, encoding);
		}
	}
}

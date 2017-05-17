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

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author	Heng Yuan
 */
public class FileUtils
{
	public static void printEncoding (PrintWriter pw)
	{
		Charset charset = getCharset ();
		if (charset == null)
		{
			pw.println ("Output encoding is unknown.");
		}
		else
		{
			pw.println ("Output encoding is " + charset);
		}
	}

	public static Charset getCharset ()
	{
		try
		{
			String encoding = System.getProperty ("file.encoding");
			return Charset.forName (encoding);
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public static byte[] getBOM ()
	{
		return null;
	}

	public static PrintWriter getPrintStream (File file, boolean append, boolean bom) throws IOException
	{
		// We do not generate BOM if we are appending an existing file
		if (bom && append && file.exists ())
			bom = false;

		if (bom)
		{
			return null;
		}
		else
		{
			FileWriter fw = new FileWriter (file, append);
			return new PrintWriter (fw);
		}
	}

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

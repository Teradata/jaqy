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
package com.teradata.jaqy.utils;

import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * See
 * https://en.wikipedia.org/wiki/Byte_order_mark
 *
 * @author	Heng Yuan
 */
public class ByteOrderMarkUtils
{
	public static int BOM_LEN = 4;

	public static String detectEncoding (PushbackInputStream is) throws IOException
	{
		byte[] bom = new byte[BOM_LEN];
		int len = 0;
		int off = 0;

		try
		{
			len = is.read (bom);

			if (bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF)
			{
				off = 3;
				return "UTF-8";
			}
			if (bom[0] == (byte)0xFE && bom[1] == (byte)0xFF)
			{
				off = 2;
				return "UTF-16BE";
			}
			// UTF-32LE check needs to come before UTF-16LE since the first two bytes are the same
			if (bom[0] == (byte)0xFF && bom[1] == (byte)0xFE && bom[2] == 0 && bom[3] == 0 && len == 4)
			{
				off = 4;
				return "UTF-32LE";
			}
			if (bom[0] == (byte)0xFF && bom[1] == (byte)0xFE)
			{
				off = 2;
				return "UTF-16LE";
			}
			if (bom[0] == 0 && bom[1] == 0 && bom[2] == (byte)0xFE && bom[3] == (byte)0xFF)
			{
				off = 4;
				return "UTF-32BE";
			}
/*
			if (bom[0] == (byte)0xF7 && bom[1] == 0x64 && bom[2] == 0x4C)
			{
				off = 3;
				return "UTF-1";
			}
			if (bom[0] == (byte)0xDD && bom[1] == 0x73 && bom[2] == 0x66 && bom[3] == 0x73)
			{
				off = 4;
				return "UTF-EBCDIC";
			}
*/
			return null;
		}
		catch (Exception ex)
		{
			// no need to push anything back
			off = len;
			return null;
		}
		finally
		{
			if (off < len)
				is.unread (bom, off, len - off);
		}
	}
}

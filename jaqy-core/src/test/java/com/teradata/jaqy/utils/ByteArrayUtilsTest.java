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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan
 */
public class ByteArrayUtilsTest
{
	@Test
	public void testPrint () throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream ();
		PrintStream pw = new PrintStream (bos);

		byte[] bytes = new byte[]
		{
			(byte)0x0, (byte)0x1, (byte)0x2, (byte)0x3, (byte)0x4, (byte)0x5, (byte)0x6, (byte)0x7,
			(byte)0x8, (byte)0x9, (byte)0xa, (byte)0xb, (byte)0xc, (byte)0xd, (byte)0xe, (byte)0xf,
			(byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0xff, (byte)'a'
		};

		ByteArrayUtils.print (pw, bytes, 0);
		pw.close ();

		String nl = System.lineSeparator ();
		String str = new String (bos.toByteArray (), "UTF-8");
		String expected = "";
		expected += "00000000  00 01 02 03 04 05 06 07  08 09 0a 0b 0c 0d 0e 0f  |........ ........|" + nl;
		expected += "00000010  10 11 12 13 14 ff 61                              |......a          |" + nl;
		Assert.assertEquals (expected, str);
	}
}

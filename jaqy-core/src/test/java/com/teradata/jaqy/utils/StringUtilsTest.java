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
package com.teradata.jaqy.utils;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public class StringUtilsTest
{
	@Test
	public void getIntValueTest () throws IOException
	{
		Assert.assertArrayEquals (new byte[] { (byte)1, (byte)2, (byte)3, (byte)4 }, StringUtils.getBytesFromHexString ("01020304"));
		Assert.assertArrayEquals (new byte[] { (byte)0x1e, (byte)0xef, (byte)0x3c, (byte)0x41 }, StringUtils.getBytesFromHexString ("1eef3c41"));
		Assert.assertArrayEquals (new byte[] { (byte)0x1e, (byte)0xef, (byte)0x3c, (byte)0x41 }, StringUtils.getBytesFromHexString ("1EEF3C41"));
	}

	@Test (expected = IOException.class)
	public void getIntValueTest2 () throws IOException
	{
		StringUtils.getBytesFromHexString ("0102030");
	}

	@Test (expected = IOException.class)
	public void getIntValueTest3 () throws IOException
	{
		StringUtils.getBytesFromHexString ("0102030Z");
	}

	@Test
	public void repeatTest ()
	{
		Assert.assertEquals ("", StringUtils.repeat ('a', 0));
		Assert.assertEquals ("aaaa", StringUtils.repeat ('a', 4));
	}

	@Test
	public void  getStringFromReaderTest () throws IOException
	{
		StringReader sr;
		sr = new StringReader ("");
		Assert.assertEquals ("", StringUtils.getStringFromReader (sr));
		sr = new StringReader ("asdfasd");
		Assert.assertEquals ("asdfasd", StringUtils.getStringFromReader (sr));

		String str = StringUtils.repeat ('a', 9999);
		sr = new StringReader (str);
		Assert.assertEquals (str, StringUtils.getStringFromReader (sr));
	}

	@Test
	public void quoteJavaStringTest ()
	{
		Assert.assertEquals ("\"\"", StringUtils.quoteJavaString (""));
		Assert.assertEquals ("\"abc\"", StringUtils.quoteJavaString ("abc"));
		Assert.assertEquals ("\"ab\\\"c\"", StringUtils.quoteJavaString ("ab\"c"));
		Assert.assertEquals ("\"\\\"abc\\\"\"", StringUtils.quoteJavaString ("\"abc\""));
	}

	@Test
	public void trimEndTest ()
	{
		Assert.assertEquals (null, StringUtils.trimEnd (null));
		Assert.assertEquals ("", StringUtils.trimEnd (""));
		Assert.assertEquals ("", StringUtils.trimEnd ("	 			 	"));
		Assert.assertEquals ("a", StringUtils.trimEnd ("a"));
		Assert.assertEquals ("a", StringUtils.trimEnd ("a "));
		Assert.assertEquals ("   ab", StringUtils.trimEnd ("   ab"));
		Assert.assertEquals ("   ab", StringUtils.trimEnd ("   ab	 			 	"));
	}

	@Test
	public void evalTest ()
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		Assert.assertEquals ("", StringUtils.eval ("null", interpreter));
		Assert.assertEquals ("1", StringUtils.eval ("1", interpreter));
		Assert.assertEquals ("3", StringUtils.eval ("1 + 2", interpreter));
	}

	@Test
	public void stripQuoteTest ()
	{
		Assert.assertEquals ("", StringUtils.stripQuote ("", '"'));
		Assert.assertEquals ("abc", StringUtils.stripQuote ("abc", '"'));
		Assert.assertEquals ("\"abc", StringUtils.stripQuote ("\"abc", '"'));
		Assert.assertEquals ("abc", StringUtils.stripQuote ("\"abc\"", '"'));
	}
}

/*
 * Copyright (c) 2021 Heng Yuan
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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan
 */
public class AttributeUtilsTest
{
	@Test
	public void testPrint () throws Exception
	{
		String nl = System.lineSeparator ();
		StringWriter sw = new StringWriter ();
		PrintWriter pw;

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", "bbbb", true);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         not supported" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", "bbbb");
		pw.close ();
		Assert.assertEquals ("  aaaa                                         bbbb" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", "bbbb", false);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         bbbb" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", true, true);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         not supported" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", true);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         Y" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", true, false);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         Y" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", 1, true);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         not supported" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", 1);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         1" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "aaaa", 1, false);
		pw.close ();
		Assert.assertEquals ("  aaaa                                         1" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "01234567890123456789012345678901234567890123456789", 1);
		pw.close ();
		Assert.assertEquals ("  0123456789012345678901234567890123456789012341" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);

		pw = new PrintWriter (sw);
		AttributeUtils.print (pw, 1, "", 1);
		pw.close ();
		Assert.assertEquals ("  1" + nl, sw.toString ());
		sw.getBuffer ().setLength (0);
	}
}

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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan
 */
public class DoubleUtilsTest
{
	@Test
	public void testDoubleToString () throws Exception
	{
		Assert.assertEquals ("0", DoubleUtils.toString (0));
		Assert.assertEquals ("0.1", DoubleUtils.toString (0.1));
		Assert.assertEquals ("0.2", DoubleUtils.toString (0.2));
		Assert.assertEquals ("0.23", DoubleUtils.toString (0.23));
		Assert.assertEquals ("2.3", DoubleUtils.toString (2.3));

		Assert.assertEquals (Double.toString (Double.NaN), DoubleUtils.toString (Double.NaN));
		Assert.assertEquals (Double.toString (Double.POSITIVE_INFINITY), DoubleUtils.toString (Double.POSITIVE_INFINITY));
		Assert.assertEquals (Double.toString (Double.NEGATIVE_INFINITY), DoubleUtils.toString (Double.NEGATIVE_INFINITY));
		Assert.assertEquals ("0", DoubleUtils.toString (0.0));
		Assert.assertEquals ("1", DoubleUtils.toString (1.0));
		Assert.assertEquals ("12", DoubleUtils.toString (12.0));
		Assert.assertEquals ("1234", DoubleUtils.toString (1234.0));
		Assert.assertEquals ("1234.1234", DoubleUtils.toString (1234.1234));
		Assert.assertEquals ("0.1234", DoubleUtils.toString (0.1234));
		Assert.assertEquals ("0.01234", DoubleUtils.toString (0.01234));
		Assert.assertEquals ("0.001234", DoubleUtils.toString (0.001234));
		Assert.assertEquals ("0.0001234", DoubleUtils.toString (0.0001234));
		Assert.assertEquals ("1.234e-5", DoubleUtils.toString (0.00001234));
		Assert.assertEquals ("1.1234", DoubleUtils.toString (1.1234));
		Assert.assertEquals ("5e-100", DoubleUtils.toString (5e-100));
		Assert.assertEquals ("5e100", DoubleUtils.toString (5e100));
		Assert.assertEquals ("12345678980123456", DoubleUtils.toString (12345678980123456L));
		Assert.assertEquals ("1.2345678980123456e17", DoubleUtils.toString (123456789801234567L));
		Assert.assertEquals ("-1", DoubleUtils.toString (-1.0));
		Assert.assertEquals ("-12", DoubleUtils.toString (-12.0));
		Assert.assertEquals ("-1234", DoubleUtils.toString (-1234.0));
		Assert.assertEquals ("-1234.1234", DoubleUtils.toString (-1234.1234));
		Assert.assertEquals ("-0.1234", DoubleUtils.toString (-0.1234));
		Assert.assertEquals ("-0.01234", DoubleUtils.toString (-0.01234));
		Assert.assertEquals ("-0.001234", DoubleUtils.toString (-0.001234));
		Assert.assertEquals ("-0.0001234", DoubleUtils.toString (-0.0001234));
		Assert.assertEquals ("-1.1234", DoubleUtils.toString (-1.1234));
		Assert.assertEquals ("-5e-100", DoubleUtils.toString (-5e-100));
		Assert.assertEquals ("-5e100", DoubleUtils.toString (-5e100));
		Assert.assertEquals ("-12345678980123456", DoubleUtils.toString (-12345678980123456L));
		Assert.assertEquals ("-1.2345678980123456e17", DoubleUtils.toString (-123456789801234567L));
		Assert.assertEquals (Integer.toString (Integer.MIN_VALUE), DoubleUtils.toString (Integer.MIN_VALUE));
		Assert.assertEquals (Integer.toString (Integer.MAX_VALUE), DoubleUtils.toString (Integer.MAX_VALUE));
		Assert.assertEquals ("1.7976931348623157e308", DoubleUtils.toString (Double.MAX_VALUE));
		Assert.assertEquals ("2.2250738585072014e-308", DoubleUtils.toString (Double.MIN_NORMAL));
		Assert.assertEquals ("-1.7976931348623157e308", DoubleUtils.toString (-Double.MAX_VALUE));
		Assert.assertEquals ("-2.2250738585072014e-308", DoubleUtils.toString (-Double.MIN_NORMAL));
		// it should be noted that 5e-324 is the same as 4.9e-324 in binary
		// representation
		Assert.assertEquals ("5e-324", DoubleUtils.toString (Double.MIN_VALUE));
	}

	@Test
	public void testFloatToString () throws Exception
	{
		Assert.assertEquals ("0", DoubleUtils.floatToString (0f));
		Assert.assertEquals ("0.1", DoubleUtils.floatToString (0.1f));
		Assert.assertEquals ("0.2", DoubleUtils.floatToString (0.2f));
		Assert.assertEquals ("0.23", DoubleUtils.floatToString (0.23f));
		Assert.assertEquals ("0.233", DoubleUtils.floatToString (0.233f));
		Assert.assertEquals ("0.2333", DoubleUtils.floatToString (0.2333f));
		Assert.assertEquals ("0.23333", DoubleUtils.floatToString (0.23333f));
		Assert.assertEquals ("0.233333", DoubleUtils.floatToString (0.233333f));
		Assert.assertEquals ("0.2333333", DoubleUtils.floatToString (0.2333333f));
		Assert.assertEquals ("0.23333333", DoubleUtils.floatToString (0.23333333f));
		Assert.assertEquals ("0.23333333", DoubleUtils.floatToString (0.233333333f));
		Assert.assertEquals ("2.3", DoubleUtils.floatToString (2.3f));

		Assert.assertEquals ("4.999997", DoubleUtils.floatToString (4.999997f));
		Assert.assertEquals ("4.999998", DoubleUtils.floatToString (4.999998f));
		Assert.assertEquals ("4.999999", DoubleUtils.floatToString (4.999999f));
		Assert.assertEquals ("5", DoubleUtils.floatToString (4.9999999f));
		Assert.assertEquals ("5", DoubleUtils.floatToString (5f));
		Assert.assertEquals ("5.000001", DoubleUtils.floatToString (5.000001f));
		Assert.assertEquals ("5.000002", DoubleUtils.floatToString (5.000002f));
		Assert.assertEquals ("5.000003", DoubleUtils.floatToString (5.000003f));
		Assert.assertEquals ("5.000004", DoubleUtils.floatToString (5.000004f));
		Assert.assertEquals ("5.0000043", DoubleUtils.floatToString (5.0000043f));
		Assert.assertEquals ("5.000005", DoubleUtils.floatToString (5.000005f));

		Assert.assertEquals (Float.toString (Float.NaN), DoubleUtils.floatToString (Float.NaN));
		Assert.assertEquals (Float.toString (Float.POSITIVE_INFINITY), DoubleUtils.floatToString (Float.POSITIVE_INFINITY));
		Assert.assertEquals (Float.toString (Float.NEGATIVE_INFINITY), DoubleUtils.floatToString (Float.NEGATIVE_INFINITY));
		Assert.assertEquals ("0", DoubleUtils.floatToString (0.0f));
		Assert.assertEquals ("1", DoubleUtils.floatToString (1.0f));
		Assert.assertEquals ("12", DoubleUtils.floatToString (12.0f));
		Assert.assertEquals ("1234", DoubleUtils.floatToString (1234.0f));
		Assert.assertEquals ("1234.1234", DoubleUtils.floatToString (1234.1234f));
		Assert.assertEquals ("12345", DoubleUtils.floatToString (12345f));
		Assert.assertEquals ("12345", DoubleUtils.floatToString (12345f));
		Assert.assertEquals ("123456", DoubleUtils.floatToString (123456f));
		Assert.assertEquals ("1234567", DoubleUtils.floatToString (1234567f));
		Assert.assertEquals ("12345678", DoubleUtils.floatToString (12345678f));
		Assert.assertEquals ("0.1234", DoubleUtils.floatToString (0.1234f));
		Assert.assertEquals ("0.01234", DoubleUtils.floatToString (0.01234f));
		Assert.assertEquals ("0.001234", DoubleUtils.floatToString (0.001234f));
		Assert.assertEquals ("0.0001234", DoubleUtils.floatToString (0.0001234f));
		Assert.assertEquals ("1.234e-5", DoubleUtils.floatToString (0.00001234f));
		Assert.assertEquals ("1.1234", DoubleUtils.floatToString (1.1234f));
		Assert.assertEquals ("5e-30", DoubleUtils.floatToString (5e-30f));
		Assert.assertEquals ("5e30", DoubleUtils.floatToString (5e30f));
		Assert.assertEquals ("12000000", DoubleUtils.floatToString (1.2e7f));
		Assert.assertEquals ("1.2345679e8", DoubleUtils.floatToString (123456789));
		Assert.assertEquals ("1.2345679e17", DoubleUtils.floatToString (123456789801234567L));
		Assert.assertEquals ("-1", DoubleUtils.floatToString (-1.0f));
		Assert.assertEquals ("-12", DoubleUtils.floatToString (-12.0f));
		Assert.assertEquals ("-1234", DoubleUtils.floatToString (-1234.0f));
		Assert.assertEquals ("-1234.1234", DoubleUtils.floatToString (-1234.1234f));
		Assert.assertEquals ("-0.1234", DoubleUtils.floatToString (-0.1234f));
		Assert.assertEquals ("-0.01234", DoubleUtils.floatToString (-0.01234f));
		Assert.assertEquals ("-0.001234", DoubleUtils.floatToString (-0.001234f));
		Assert.assertEquals ("-0.0001234", DoubleUtils.floatToString (-0.0001234f));
		Assert.assertEquals ("-1.1234", DoubleUtils.floatToString (-1.1234f));
		Assert.assertEquals ("-5e-30", DoubleUtils.floatToString (-5e-30f));
		Assert.assertEquals ("-5e30", DoubleUtils.floatToString (-5e30f));
		Assert.assertEquals ("-1.234568e16", DoubleUtils.floatToString (-12345678980123456L));
		Assert.assertEquals ("-1.2345679e17", DoubleUtils.floatToString (-123456789801234567L));
		Assert.assertEquals (Short.toString (Short.MIN_VALUE), DoubleUtils.floatToString (Short.MIN_VALUE));
		Assert.assertEquals (Short.toString (Short.MAX_VALUE), DoubleUtils.floatToString (Short.MAX_VALUE));
		Assert.assertEquals ("3.4028235e38", DoubleUtils.floatToString (Float.MAX_VALUE));
		Assert.assertEquals ("1.1754944e-38", DoubleUtils.floatToString (Float.MIN_NORMAL));
		Assert.assertEquals ("-3.4028235e38", DoubleUtils.floatToString (-Float.MAX_VALUE));
		Assert.assertEquals ("-1.1754944e-38", DoubleUtils.floatToString (-Float.MIN_NORMAL));

		// Well 1.4e-15 is the same as 1e-45 floating point representation wise
		Assert.assertEquals ("1e-45", DoubleUtils.floatToString (Float.MIN_VALUE));
		Assert.assertEquals ("-1e-45", DoubleUtils.floatToString (-Float.MIN_VALUE));
	}
}

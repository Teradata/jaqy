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
package com.teradata.jaqy.parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.utils.SortInfo;

/**
 * @author	Heng Yuan 
 */
public class OrderByParserTest
{
	@Test
	public void test1 () throws IOException
	{
		SortInfo[] sortInfo;
		sortInfo = OrderByParser.getSortInfo ("1");
		Assert.assertEquals (1, sortInfo[0].column);
		Assert.assertTrue (sortInfo[0].asc);

		sortInfo = OrderByParser.getSortInfo ("1 asc");
		Assert.assertEquals (1, sortInfo[0].column);
		Assert.assertTrue (sortInfo[0].asc);

		sortInfo = OrderByParser.getSortInfo ("a");
		Assert.assertEquals ("a", sortInfo[0].name);
		Assert.assertTrue (sortInfo[0].asc);

		sortInfo = OrderByParser.getSortInfo ("abc desc");
		Assert.assertEquals ("abc", sortInfo[0].name);
		Assert.assertFalse (sortInfo[0].asc);

		sortInfo = OrderByParser.getSortInfo ("1 desc, 2 asc");
		Assert.assertEquals (1, sortInfo[0].column);
		Assert.assertFalse (sortInfo[0].asc);
		Assert.assertEquals (2, sortInfo[1].column);
		Assert.assertTrue (sortInfo[1].asc);

		sortInfo = OrderByParser.getSortInfo ("1 desc, abc asc");
		Assert.assertEquals (1, sortInfo[0].column);
		Assert.assertFalse (sortInfo[0].asc);
		Assert.assertEquals ("abc", sortInfo[1].name);
		Assert.assertTrue (sortInfo[1].asc);

		sortInfo = OrderByParser.getSortInfo ("1 desc, \"abc\" asc");
		Assert.assertEquals (1, sortInfo[0].column);
		Assert.assertFalse (sortInfo[0].asc);
		Assert.assertEquals ("abc", sortInfo[1].name);
		Assert.assertTrue (sortInfo[1].asc);

		sortInfo = OrderByParser.getSortInfo ("1 desc, \"a\"\"bc \"\"def\" asc");
		Assert.assertEquals (1, sortInfo[0].column);
		Assert.assertFalse (sortInfo[0].asc);
		Assert.assertEquals ("a\"bc \"def", sortInfo[1].name);
		Assert.assertTrue (sortInfo[1].asc);
	}

	@Test (expected = IOException.class)
	public void testError1 () throws IOException
	{
		OrderByParser.getSortInfo ("");
	}

	@Test (expected = IOException.class)
	public void testError2 () throws IOException
	{
		OrderByParser.getSortInfo ("1 desc 2 asc");
	}

	@Test (expected = IOException.class)
	public void testError3 () throws IOException
	{
		OrderByParser.getSortInfo ("desc asc");
	}
}

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
package com.teradata.jaqy.importer;

import java.io.StringReader;

import javax.json.stream.JsonParser;

import org.junit.Assert;
import org.junit.Test;
import org.yuanheng.cookjson.TextJsonParser;

/**
 * @author	Heng Yuan
 */
public class JsonExpFactoryTest
{
	static class TestRowEndListener implements JsonRowEndListener
	{
		private boolean m_rowEnd;

		@Override
		public void setRowEnd ()
		{
			m_rowEnd = true;
		}

		public boolean isRowEnd ()
		{
			return m_rowEnd;
		}

		public void setRowEnd (boolean rowEnd)
		{
			m_rowEnd = rowEnd;
		}
	}

	@Test
	public void test1 ()
	{
		JsonValueVisitor[] vvs;
		String[] colExps;
		colExps = new String[] { "a", "b" };
		vvs = new JsonValueVisitor[colExps.length];
		TestRowEndListener listener = new TestRowEndListener ();
		JsonEventVisitor ev = JsonExpFactory.createVisitor ("", colExps, vvs, listener, false);

		for (int i = 0; i < vvs.length; ++i)
		{
			Assert.assertNotNull(vvs[i]);
		}

		TextJsonParser p = new TextJsonParser (new StringReader ("[{\"a\":123,\"b\":234}]"));
		int depth = 0;
		while (p.hasNext ())
		{
			JsonParser.Event e = p.next ();
			switch (e)
			{
				case START_OBJECT:
				case START_ARRAY:
					ev.visit (e, p, depth);
					++depth;
					break;
				case END_OBJECT:
				case END_ARRAY:
					--depth;
					ev.visit (e, p, depth);
					break;
				default:
					ev.visit (e, p, depth);
					break;
			}
		}
		Assert.assertTrue (listener.isRowEnd ());
		Assert.assertEquals ("123", vvs[0].getValue ().toString ());
		Assert.assertEquals ("234", vvs[1].getValue ().toString ());
	}

	@Test
	public void test2 ()
	{
		JsonValueVisitor[] vvs;
		String[] colExps;
		colExps = new String[] { "a", "b" };
		vvs = new JsonValueVisitor[colExps.length];
		TestRowEndListener listener = new TestRowEndListener ();
		JsonEventVisitor ev = JsonExpFactory.createVisitor ("", colExps, vvs, listener, false);

		for (int i = 0; i < vvs.length; ++i)
		{
			Assert.assertNotNull(vvs[i]);
		}

		TextJsonParser p = new TextJsonParser (new StringReader ("[{\"a\":123,\"b\":234},{\"a\":123,\"b\":234},{\"a\":1234,\"b\":2345}]"));
		int depth = 0;
		int rowCount = 0;
		while (p.hasNext ())
		{
			JsonParser.Event e = p.next ();
			switch (e)
			{
				case START_OBJECT:
				case START_ARRAY:
					ev.visit (e, p, depth);
					++depth;
					break;
				case END_OBJECT:
				case END_ARRAY:
					--depth;
					ev.visit (e, p, depth);
					break;
				default:
					ev.visit (e, p, depth);
					break;
			}

			if (listener.isRowEnd ())
			{
				++rowCount;
				listener.setRowEnd (false);
			}
		}
		Assert.assertEquals (3, rowCount);
		Assert.assertEquals ("1234", vvs[0].getValue ().toString ());
		Assert.assertEquals ("2345", vvs[1].getValue ().toString ());
	}

	@Test
	public void test3 ()
	{
		JsonValueVisitor[] vvs;
		String[] colExps;
		colExps = new String[] { "a", "b" };
		vvs = new JsonValueVisitor[colExps.length];
		TestRowEndListener listener = new TestRowEndListener ();
		JsonEventVisitor ev = JsonExpFactory.createVisitor ("items", colExps, vvs, listener, false);

		for (int i = 0; i < vvs.length; ++i)
		{
			Assert.assertNotNull(vvs[i]);
		}

		TextJsonParser p = new TextJsonParser (new StringReader ("{\"items\":[{\"a\":123,\"b\":234},{\"a\":123,\"b\":234},{\"a\":1234,\"b\":2345}]}"));
		int depth = 0;
		int rowCount = 0;
		while (p.hasNext ())
		{
			JsonParser.Event e = p.next ();
			switch (e)
			{
				case START_OBJECT:
				case START_ARRAY:
					ev.visit (e, p, depth);
					++depth;
					break;
				case END_OBJECT:
				case END_ARRAY:
					--depth;
					ev.visit (e, p, depth);
					break;
				default:
					ev.visit (e, p, depth);
					break;
			}

			if (listener.isRowEnd ())
			{
				++rowCount;
				listener.setRowEnd (false);
			}
		}
		Assert.assertEquals (3, rowCount);
		Assert.assertEquals ("1234", vvs[0].getValue ().toString ());
		Assert.assertEquals ("2345", vvs[1].getValue ().toString ());
	}

	@Test
	public void test4 ()
	{
		JsonValueVisitor[] vvs;
		String[] colExps;
		colExps = new String[] { "a", "b" };
		vvs = new JsonValueVisitor[colExps.length];
		TestRowEndListener listener = new TestRowEndListener ();
		JsonEventVisitor ev = JsonExpFactory.createVisitor ("items", colExps, vvs, listener, false);

		for (int i = 0; i < vvs.length; ++i)
		{
			Assert.assertNotNull(vvs[i]);
		}

		TextJsonParser p = new TextJsonParser (new StringReader ("{\"items\":{\"a\":123,\"b\":234}}"));
		int depth = 0;
		int rowCount = 0;
		while (p.hasNext ())
		{
			JsonParser.Event e = p.next ();
			switch (e)
			{
				case START_OBJECT:
				case START_ARRAY:
					ev.visit (e, p, depth);
					++depth;
					break;
				case END_OBJECT:
				case END_ARRAY:
					--depth;
					ev.visit (e, p, depth);
					break;
				default:
					ev.visit (e, p, depth);
					break;
			}

			if (listener.isRowEnd ())
			{
				++rowCount;
				listener.setRowEnd (false);
			}
		}
		Assert.assertEquals (0, rowCount);
	}

	@Test
	public void test5 ()
	{
		JsonValueVisitor[] vvs;
		String[] colExps;
		colExps = new String[] { "a", "b" };
		vvs = new JsonValueVisitor[colExps.length];
		TestRowEndListener listener = new TestRowEndListener ();
		JsonEventVisitor ev = JsonExpFactory.createVisitor ("", colExps, vvs, listener, true);

		for (int i = 0; i < vvs.length; ++i)
		{
			Assert.assertNotNull(vvs[i]);
		}

		TextJsonParser p = new TextJsonParser (new StringReader ("{\"1\":{\"a\":1,\"b\":2},\"2\":{\"a\":3,\"b\":4},\"3\":{\"a\":5,\"b\":6}}"));
		int depth = 0;
		int rowCount = 0;
		while (p.hasNext ())
		{
			JsonParser.Event e = p.next ();
			switch (e)
			{
				case START_OBJECT:
				case START_ARRAY:
					ev.visit (e, p, depth);
					++depth;
					break;
				case END_OBJECT:
				case END_ARRAY:
					--depth;
					ev.visit (e, p, depth);
					break;
				default:
					ev.visit (e, p, depth);
					break;
			}

			if (listener.isRowEnd ())
			{
				++rowCount;
				listener.setRowEnd (false);
			}
		}
		Assert.assertEquals (3, rowCount);
		Assert.assertEquals ("5", vvs[0].getValue ().toString ());
		Assert.assertEquals ("6", vvs[1].getValue ().toString ());
	}
}

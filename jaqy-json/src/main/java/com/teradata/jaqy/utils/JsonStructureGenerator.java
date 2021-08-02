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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Stack;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;

import org.yuanheng.cookjson.CookJsonGenerator;
import org.yuanheng.cookjson.value.*;

/**
 * @author	Heng Yuan
 */
public class JsonStructureGenerator implements CookJsonGenerator
{
	/**
	 * Current object
	 */
	private final Stack<JsonStructure> m_js = new Stack<JsonStructure> ();
	private JsonStructure m_root;

	public JsonStructureGenerator ()
	{
	}

	public JsonStructure getValue ()
	{
		return m_root;
	}

	private JsonGenerator writeValue (String name, JsonValue value)
	{
		if (m_js.size () == 0)
		{
			throw new JsonGenerationException ("Not in object");
		}
		
		JsonStructure js = m_js.peek ();
		if (!(js instanceof JsonObject))
		{
			throw new JsonGenerationException ("Not in object");
		}
		((JsonObject)js).put (name, value);
		return this;
	}

	private JsonGenerator writeValue (JsonValue value)
	{
		if (m_js.size () == 0)
		{
			throw new JsonGenerationException ("Not in array");
		}
		
		JsonStructure js = m_js.peek ();
		if (!(js instanceof JsonArray))
		{
			throw new JsonGenerationException ("Not in array");
		}
		((JsonArray)js).add (value);
		return this;
	}

	@Override
	public JsonGenerator writeStartObject ()
	{
		CookJsonObject o = new CookJsonObject ();
		if (m_js.size () > 0)
		{
			JsonStructure js = m_js.peek ();
			if (!(js instanceof JsonArray))
			{
				throw new JsonGenerationException ("Not in array");
			}
			((JsonArray)js).add (o);
		}
		else
		{
			m_root = o;
		}
		m_js.push (o);
		return this;
	}

	@Override
	public JsonGenerator writeStartArray ()
	{
		CookJsonArray a = new CookJsonArray ();
		if (m_js.size () > 0)
		{
			JsonStructure js = m_js.peek ();
			if (!(js instanceof JsonArray))
			{
				throw new JsonGenerationException ("Not in array");
			}
			((JsonArray)js).add (a);
		}
		else
		{
			m_root = a;
		}
		m_js.push (a);
		return this;
	}

	@Override
	public JsonGenerator writeStartObject (String name)
	{
		JsonObject o = new CookJsonObject ();
		writeValue (name, o);
		m_js.push (o);
		return this;
	}

	@Override
	public JsonGenerator writeStartArray (String name)
	{
		JsonArray a = new CookJsonArray ();
		writeValue (name, a);
		m_js.push (a);
		return this;
	}

	@Override
	public JsonGenerator write (String name, JsonValue value)
	{
		return writeValue (name, value);
	}

	@Override
	public JsonGenerator write (String name, byte[] value)
	{
		return writeValue (name, new CookJsonBinary (value));
	}

	@Override
	public JsonGenerator write (String name, String value)
	{
		return writeValue (name, new CookJsonString (value));
	}

	@Override
	public JsonGenerator write (String name, BigInteger value)
	{
		return writeValue (name, new CookJsonBigDecimal (value));
	}

	@Override
	public JsonGenerator write (String name, BigDecimal value)
	{
		return writeValue (name, new CookJsonBigDecimal (value));
	}

	@Override
	public JsonGenerator write (String name, int value)
	{
		return writeValue (name, new CookJsonInt (value));
	}

	@Override
	public JsonGenerator write (String name, long value)
	{
		return writeValue (name, new CookJsonLong (value));
	}

	@Override
	public JsonGenerator write (String name, double value)
	{
		return writeValue (name, new CookJsonDouble (value));
	}

	@Override
	public JsonGenerator write (String name, boolean value)
	{
		return writeValue (name, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonGenerator writeNull (String name)
	{
		return writeValue (name, JsonValue.NULL);
	}

	@Override
	public JsonGenerator writeEnd ()
	{
		m_js.pop ();
		return this;
	}

	@Override
	public JsonGenerator write (JsonValue value)
	{
		return writeValue (value);
	}

	@Override
	public JsonGenerator write (byte[] value)
	{
		return writeValue (new CookJsonBinary (value));
	}

	@Override
	public JsonGenerator write (String value)
	{
		return writeValue (new CookJsonString (value));
	}

	@Override
	public JsonGenerator write (BigDecimal value)
	{
		return writeValue (new CookJsonBigDecimal (value));
	}

	@Override
	public JsonGenerator write (BigInteger value)
	{
		return writeValue (new CookJsonBigDecimal (value));
	}

	@Override
	public JsonGenerator write (int value)
	{
		return writeValue (new CookJsonInt (value));
	}

	@Override
	public JsonGenerator write (long value)
	{
		return writeValue (new CookJsonLong (value));
	}

	@Override
	public JsonGenerator write (double value)
	{
		return writeValue (new CookJsonDouble (value));
	}

	@Override
	public JsonGenerator write (boolean value)
	{
		return writeValue (value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonGenerator writeNull ()
	{
		return writeValue (JsonValue.NULL);
	}

	@Override
	public void close ()
	{
	}

	@Override
	public void flush ()
	{
	}
}

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
package com.teradata.jaqy.importer;

import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser.Event;

import org.yuanheng.cookjson.CookJsonParser;

import com.teradata.jaqy.utils.JsonStructureGenerator;

/**
 * @author	Heng Yuan
 */
class JsonValueVisitor implements JsonEventVisitor
{
	private Object m_value;
	private JsonGenerator m_g;
	private String m_name;

	public JsonValueVisitor ()
	{
	}

	public void init ()
	{
		m_value = null;
		m_g = null;
		m_name = null;
	}

	public Object getValue ()
	{
		if (m_value instanceof JsonStructureGenerator)
		{
			return ((JsonStructureGenerator)m_value).getValue ();
		}
		return m_value;
	}

	@Override
	public void visit (Event e, CookJsonParser p, int depth)
	{
		switch (e)
		{
			case START_ARRAY:
			{
				if (depth == 0)
				{
					m_g = new JsonStructureGenerator ();
					m_value = m_g;
				}
				if (m_name != null)
				{
					m_g.writeStartArray (m_name);
					m_name = null;
				}
				else
				{
					m_g.writeStartArray ();
				}
				break;
			}
			case START_OBJECT:
			{
				if (depth == 0)
				{
					m_g = new JsonStructureGenerator ();
					m_value = m_g;
				}
				if (m_name != null)
				{
					m_g.writeStartObject (m_name);
					m_name = null;
				}
				else
				{
					m_g.writeStartObject ();
				}
				break;
			}
			case END_ARRAY:
			case END_OBJECT:
			{
				m_g.writeEnd ();
				break;
			}
			case KEY_NAME:
			{
				m_name = p.getString ();
				break;
			}
			case VALUE_FALSE:
			{
				if (depth == 0)
				{
					m_value = JsonValue.FALSE;
				}
				else
				{
					if (m_name != null)
					{
						m_g.write (m_name, false);
						m_name = null;
					}
					else
					{
						m_g.write (false);
					}
				}
				break;
			}
			case VALUE_NULL:
			{
				if (depth == 0)
				{
					m_value = null;
				}
				else
				{
					if (m_name != null)
					{
						m_g.writeNull (m_name);
						m_name = null;
					}
					else
					{
						m_g.writeNull ();
					}
				}
				break;
			}
			case VALUE_NUMBER:
			{
				if (depth == 0)
				{
					m_value = p.getValue ();
				}
				else
				{
					if (m_name != null)
					{
						m_g.write (m_name, p.getValue ());
						m_name = null;
					}
					else
					{
						m_g.write (p.getBigDecimal ());
					}
				}
				break;
			}
			case VALUE_STRING:
			{
				if (depth == 0)
				{
					m_value = p.getValue ();
				}
				else
				{
					if (m_name != null)
					{
						m_g.write (m_name, p.getString ());
						m_name = null;
					}
					else
					{
						m_g.write (p.getString ());
					}
				}
				break;
			}
			case VALUE_TRUE:
			{
				if (depth == 0)
				{
					m_value = JsonValue.TRUE;
				}
				else
				{
					if (m_name != null)
					{
						m_g.write (m_name, true);
						m_name = null;
					}
					else
					{
						m_g.write (true);
					}
				}
				break;
			}
			default:
				break;
		}
	}
}

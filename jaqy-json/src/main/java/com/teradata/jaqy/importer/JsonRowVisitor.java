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

import javax.json.stream.JsonParser;

import org.yuanheng.cookjson.CookJsonParser;

/**
 * @author	Heng Yuan
 */
class JsonRowVisitor implements JsonEventVisitor
{
	private final JsonRowEndListener m_listener;
	private final boolean m_rootAsArray;
	private JsonEventVisitor m_v;
	private boolean m_arrayRoot;

	public JsonRowVisitor (JsonRowEndListener listener, boolean rootAsArray)
	{
		m_listener = listener;
		m_rootAsArray = rootAsArray;
	}

	public void setColVisitor (JsonEventVisitor v)
	{
		m_v = v;
	}

	@Override
	public void visit (JsonParser.Event e, CookJsonParser p, int depth)
	{
		if (depth == 0)
		{
			switch (e)
			{
				case START_ARRAY:
				{
					m_arrayRoot = true;
					break;
				}
				case START_OBJECT:
				{
					if (m_rootAsArray)
					{
						m_arrayRoot = true;
					}
					else
					{
						m_arrayRoot = false;
						m_v.visit (e, p, depth);
					}
					break;
				}
				case END_OBJECT:
				{
					if (!m_arrayRoot)
					{
						m_v.visit (e, p, depth);
					}
					break;
				}
				default:
					break;
			}
			return;
		}
		if (m_arrayRoot)
			--depth;
		if (depth == 0)
		{
			switch (e)
			{
				case START_ARRAY:
				case START_OBJECT:
				{
					m_v.visit (e, p, depth);
					break;
				}
				case KEY_NAME:
				{
					break;
				}
				default:
				{
					m_v.visit (e, p, depth);
					m_listener.setRowEnd ();
					break;
				}
			}
			return;
		}
		m_v.visit (e, p, depth);
	}
}

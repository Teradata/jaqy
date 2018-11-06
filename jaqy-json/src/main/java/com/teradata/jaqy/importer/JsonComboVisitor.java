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

import javax.json.stream.JsonParser.Event;

import org.yuanheng.cookjson.CookJsonParser;

/**
 * @author	Heng Yuan
 */
class JsonComboVisitor implements JsonEventVisitor
{
	private final JsonObjectVisitor m_v1;
	private final JsonEventVisitor m_v2;

	public JsonComboVisitor (JsonObjectVisitor v1, JsonEventVisitor v2)
	{
		m_v1 = v1;
		m_v2 = v2;
	}

	public JsonObjectVisitor getObjectVisitor ()
	{
		return m_v1;
	}

	@Override
	public void visit (Event e, CookJsonParser p, int depth)
	{
		m_v1.visit (e, p, depth);
		m_v2.visit (e, p, depth);
	}
}

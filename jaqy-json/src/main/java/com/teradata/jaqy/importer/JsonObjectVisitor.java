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

import java.util.TreeMap;

import javax.json.stream.JsonParser;

import org.yuanheng.cookjson.CookJsonParser;

/**
 * @author  Heng Yuan
 */
class JsonObjectVisitor implements JsonEventVisitor
{
    private final TreeMap<String, JsonEventVisitor> m_map = new TreeMap<String, JsonEventVisitor> ();

    private boolean m_matched = false;
    private JsonEventVisitor m_v = null;

    public JsonObjectVisitor ()
    {
    }

    public JsonEventVisitor getEventVisitor (String exp)
    {
        return m_map.get (exp);
    }

    public void setExp (String exp, JsonEventVisitor v)
    {
        m_map.put (exp, v);
    }

    @Override
    public void visit (JsonParser.Event e, CookJsonParser p, int depth)
    {
        if (depth == 0)
        {
            m_matched = (e == JsonParser.Event.START_OBJECT);
            m_v = null;
        }
        else if (m_matched)
        {
            if (depth == 1 &&
                e == JsonParser.Event.KEY_NAME)
            {
                m_v = m_map.get (p.getString ());
            }
            else if (m_v != null)
            {
                m_v.visit (e, p, depth - 1);
            }
        }
    }
}

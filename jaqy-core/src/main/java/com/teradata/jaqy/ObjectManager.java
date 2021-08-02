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
package com.teradata.jaqy;

import java.util.HashMap;

import com.teradata.jaqy.interfaces.JaqyObject;

/**
 * @author  Heng Yuan
 */
public class ObjectManager<T extends JaqyObject>
{
    private final Globals m_globals;
    private final Object m_lock = new Object ();
    private final HashMap<String, T> m_objectMap = new HashMap<String, T> ();

    public ObjectManager (Globals globals)
    {
        m_globals = globals;
    }

    public void addObject (T cmd)
    {
        cmd.init (m_globals);
        synchronized (m_lock)
        {
            m_objectMap.put (cmd.getName (), cmd);
        }
    }

    public T getObject (String name)
    {
        synchronized (m_lock)
        {
            return m_objectMap.get (name);
        }
    }

    public HashMap<String, T> getObjectMap ()
    {
        HashMap<String, T> map = new HashMap<String, T> ();
        synchronized (m_lock)
        {
            map.putAll (m_objectMap);
        }
        return map;
    }
}

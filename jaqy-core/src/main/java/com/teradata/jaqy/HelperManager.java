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

import com.teradata.jaqy.helper.DefaultHelperFactory;
import com.teradata.jaqy.interfaces.JaqyHelperFactory;

/**
 * @author  Heng Yuan
 */
public class HelperManager
{
    private final Object m_lock = new Object ();
    private final HashMap<String, JaqyHelperFactory> m_helperMap = new HashMap<String, JaqyHelperFactory> ();

    HelperManager ()
    {
    }

    public void addHelperFactory (String protocol, JaqyHelperFactory helperFactory)
    {
        synchronized (m_lock)
        {
            m_helperMap.put (protocol, helperFactory);
        }
    }

    /**
     * Get the JaqyHelper for a particular protocol.
     *
     * @param   protocol
     *          name of the protocol, such as teradata, sqlite etc.
     * @return  the helper associated with the protocol.
     */
    public JaqyHelperFactory getHelperFactory (String protocol)
    {
        JaqyHelperFactory factory;
        synchronized (m_lock)
        {
            factory = m_helperMap.get (protocol);
            if (factory == null)
            {
                factory = new DefaultHelperFactory ();
                m_helperMap.put (protocol, factory);
            }
        }
        return factory;
    }
}

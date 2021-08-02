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
import java.util.Map;
import java.util.TreeMap;

import com.teradata.jaqy.parser.ArgumentParser;

/**
 * @author  Heng Yuan
 */
public class AliasManager
{
    private final Object m_aliasLock = new Object ();
    private final HashMap<String, String> m_aliasMap = new HashMap<String, String> ();

    AliasManager ()
    {
    }

    public void setAlias (String name, String str)
    {
        synchronized (m_aliasLock)
        {
            if (str == null)
            {
                m_aliasMap.remove (name);
            }
            else
            {
                m_aliasMap.put (name, str);
            }
        }
    }

    public String getAlias (String name)
    {
        synchronized (m_aliasLock)
        {
            return m_aliasMap.get (name);
        }
    }

    public Map<String, String> getAliasMap ()
    {
        TreeMap<String, String> map = new TreeMap<String, String> ();
        synchronized (m_aliasLock)
        {
            map.putAll (m_aliasMap);
        }
        return map;
    }


    public static String replaceArgs (String command, String[] args)
    {
        return ArgumentParser.replaceArgs (command, args);
    }
}

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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.Session;

/**
 * @author  Heng Yuan
 */
public class JaqyShutdownHook extends Thread
{
    public static void register (Globals globals)
    {
        Runtime.getRuntime ().addShutdownHook (new JaqyShutdownHook (globals));
    }

    private final Globals m_globals;

    private JaqyShutdownHook (Globals globals)
    {
        m_globals = globals;
    }

    public void run ()
    {
        for (Session session : m_globals.getSessions ())
        {
            session.close (null, true);
        }
    }
}

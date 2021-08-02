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
package com.teradata.jaqy.setting;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.interfaces.JaqySetting;

/**
 * @author  Heng Yuan
 */
public abstract class JaqySettingAdapter implements JaqySetting
{
    /** The setting name being registered. */
    private final String m_name;
    /** The globals. */
    private Globals m_globals;

    public JaqySettingAdapter (String name)
    {
        m_name = name;
    }

    @Override
    public void init (Globals globals)
    {
        m_globals = globals;
    }

    public Globals getGlobals ()
    {
        return m_globals;
    }

    @Override
    public String getName ()
    {
        return m_name;
    }

    @Override
    public CommandArgumentType getArgumentType ()
    {
        return CommandArgumentType.none;
    }

    @Override
    public Type getType ()
    {
        return Type.interpreter;
    }
}

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
package com.teradata.jaqy.interfaces;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author  Heng Yuan
 */
public interface JaqySetting extends JaqyObject
{
    enum Type
    {
        /** An interpreter level setting. */
        interpreter,
        /** A session level setting. */
        session
    }

    /**
     * Gets the setting name.
     *
     * @return  the setting name.
     */
    public String getName ();

    /**
     * Gets the one line command description.
     * @return  the one line command description.
     */
    public String getDescription ();

    /**
     * Gets the command parser argument type.
     * @return  the command parser argument type.
     */
    public CommandArgumentType getArgumentType ();

    /**
     * Gets the setting.
     * @param   interpreter
     *          The interpreter object.
     * @return  the setting value.
     * @throws  Exception
     *          in case of error.
     */
    public Object get (JaqyInterpreter interpreter) throws Exception;

    /**
     * Sets the setting.
     * @param   args
     *          the setting arguments.
     * @param   silent
     *          if the setting is executed silently.
     * @param   interpreter
     *          The interpreter object.
     * @throws  Exception
     *          in case of error.
     */
    public void set (String[] args, boolean silent, JaqyInterpreter interpreter) throws Exception;

    /**
     * Gets the setting type.
     *
     * @return  the setting type
     */
    public Type getType ();
}

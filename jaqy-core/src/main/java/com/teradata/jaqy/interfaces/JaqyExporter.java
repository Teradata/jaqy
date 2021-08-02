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

import java.io.Closeable;

import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author  Heng Yuan
 */
public interface JaqyExporter extends Closeable
{
    /**
     * Gets name of the exporter.
     * @return  The name of the exporter.
     */
    public String getName ();
    /**
     * Export the given ResultSet
     *
     * @param   rs
     *          the ResultSet
     * @param   interpreter
     *          the interpreter
     * @return  the activity count (i.e. row count)
     * @throws  Exception
     *          in case of error
     */
    public long export (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception;
}

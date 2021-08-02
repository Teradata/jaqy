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
import java.util.Collection;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;

/**
 * @author  Heng Yuan
 */
public interface JaqyImporter extends Closeable
{
    /**
     * Gets the name of the importer.
     * @return  the name of the importer.
     */
    public String getName ();

    /**
     * Shows the schema of the data being imported.
     * @throws  Exception
     *          in case of error
     */
    public SchemaInfo getSchema () throws Exception;

    /**
     * Move the data to the next row.
     *
     * @return  true if there is next row.
     */
    public boolean next () throws Exception;

    /**
     * Set the import field expressions.
     *
     * @param   exps
     *          expressions.  If the field is imported by position, then it is null.
     * @version 1.1
     */
    public void setParameters (String[] exps) throws Exception;

    /**
     * Get an object and set the value.
     * @param   stmt
     *          the prepared statement.
     * @param   column
     *          the column index
     * @param   paramInfo
     *          parameter information.
     * @param   freeList
     *          if any resources should be freed, add to this list.
     * @param   interpreter
     *          the interpreter.
     * @return  the object involved for additional processing.
     * @throws  Exception
     *          in case of error.
     */
    public Object importColumn (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo, Collection<Object> freeList, JaqyInterpreter interpreter) throws Exception;
}

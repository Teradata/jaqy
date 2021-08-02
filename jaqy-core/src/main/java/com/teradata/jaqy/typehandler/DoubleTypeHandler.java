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
package com.teradata.jaqy.typehandler;

import java.sql.SQLException;

import org.yuanheng.double2string.Double2String;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author  Heng Yuan
 */
class DoubleTypeHandler implements TypeHandler
{
    private final static TypeHandler s_instance = new DoubleTypeHandler ();

    public static TypeHandler getInstance ()
    {
        return s_instance;
    }

    private DoubleTypeHandler ()
    {
    }

    private String getString (Object obj)
    {
        if (obj instanceof Double)
        {
            return Double2String.getDoubleString ((Double)obj);
        }
        else if (obj instanceof Float)
        {
            return Double2String.getFloatString ((Float)obj);
        }
        return obj.toString ();
    }

    @Override
    public String getString (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
    {
        Object obj = rs.getObject (column);
        if (obj == null)
            return null;
        return getString (obj);
    }

    @Override
    public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws SQLException
    {
        Object obj = rs.getObject (column);
        if (obj == null)
            return -1;
        return getString (obj).length ();
    }
}

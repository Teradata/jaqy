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

import java.sql.Types;
import java.util.HashMap;

/**
 * @author  Heng Yuan
 */
public class TypeHandlerRegistry
{
    private final static HashMap<Integer,TypeHandler> s_typePrinterMap = new HashMap<Integer, TypeHandler> ();

    static
    {
        s_typePrinterMap.put (Types.CHAR, StringTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.VARCHAR, StringTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.LONGNVARCHAR, StringTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.NCHAR, StringTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.NVARCHAR, StringTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.LONGNVARCHAR, StringTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.CLOB, ClobTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.NCLOB, ClobTypeHandler.getInstance ());

        s_typePrinterMap.put (Types.BINARY, BinaryTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.VARBINARY, BinaryTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.LONGVARBINARY, BinaryTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.BLOB, BinaryTypeHandler.getInstance ());

        s_typePrinterMap.put (Types.REAL, DoubleTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.FLOAT, DoubleTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.DOUBLE, DoubleTypeHandler.getInstance ());

        s_typePrinterMap.put (Types.SQLXML, XmlTypeHandler.getInstance ());
        s_typePrinterMap.put (Types.OTHER, ObjectTypeHandler.getInstance ());
    }

    public static TypeHandler getTypeHandler (int type)
    {
        TypeHandler printer = s_typePrinterMap.get (type);
        if (printer == null)
            return StringTypeHandler.getInstance ();
        return printer;
    }
}

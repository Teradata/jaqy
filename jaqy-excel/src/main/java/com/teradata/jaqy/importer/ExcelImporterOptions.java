/*
 * Copyright (c) 2021 Teradata
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
package com.teradata.jaqy.importer;

import java.sql.Types;
import java.util.HashMap;

/**
 * @author  Heng Yuan
 */
class ExcelImporterOptions
{
    public String sheetName;
    public int sheetId = -1;
    public boolean swap;
    public boolean header;

    private final HashMap<Integer, Integer> m_typeMap = new HashMap<Integer, Integer> ();

    public ExcelImporterOptions ()
    {
    }

    public void setType (int column, int type)
    {
        m_typeMap.put (column, type);
    }

    public int getType (int column)
    {
        Integer t = m_typeMap.get (column);
        if (t == null)
        {
            return Types.NULL;
        }
        return t;
    }
}

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

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Variable;

/**
 * @author  Heng Yuan
 */
public class ClientRSUtils
{
    public final static String NULLSORT_VAR = "nullsort";
    private static boolean DEFAULT_NULLSORT_VALUE = true;

    public static boolean getSortNull (JaqyInterpreter interpreter)
    {
        Variable var = interpreter.getVariable (NULLSORT_VAR);
        if (var == null)
        {
            interpreter.setVariableValue (NULLSORT_VAR, DEFAULT_NULLSORT_VALUE);
            return DEFAULT_NULLSORT_VALUE;
        }
        Object v = var.get ();
        if (v instanceof Boolean)
        {
            return ((Boolean)v).booleanValue ();
        }
        return DEFAULT_NULLSORT_VALUE;
    }

    public static void setSortNull (JaqyInterpreter interpreter, boolean nullSort)
    {
        interpreter.setVariableValue (NULLSORT_VAR, Boolean.valueOf (nullSort));
    }
}

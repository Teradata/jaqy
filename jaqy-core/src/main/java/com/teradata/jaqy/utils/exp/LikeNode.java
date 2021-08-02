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
package com.teradata.jaqy.utils.exp;

import java.io.IOException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author  Heng Yuan
 */
public class LikeNode extends JSExpNode
{
    private final ExpNode m_left;
    private final String m_regex;
    private boolean m_nocase;

    public LikeNode (ExpNode left, ExpNode right) throws IOException
    {
        m_left = left;
        if (!(right instanceof StringLiteralNode))
            throw new IOException ("invalid LIKE syntax");
        m_regex = (String)((StringLiteralNode)right).get ();
    }

    @Override
    public void bind (JaqyResultSet rs, VariableManager vm, JaqyInterpreter interpreter) throws Exception
    {
        super.bind (rs, vm, interpreter);
        m_nocase = interpreter.isCaseInsensitive ();
        m_left.bind (rs, vm, interpreter);
    }

    @Override
    public String toString ()
    {
        String m = "(/" + m_regex + "/";
        if (m_nocase)
            m += "i";
        return m + ".test(" + m_left + "))";
    }
}

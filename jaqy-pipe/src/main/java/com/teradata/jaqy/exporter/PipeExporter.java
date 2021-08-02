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
package com.teradata.jaqy.exporter;

import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author  Heng Yuan
 */
public class PipeExporter implements JaqyExporter
{
    private JaqyResultSet m_rs;

    public PipeExporter ()
    {
    }

    @Override
    public String getName ()
    {
        return "pipe";
    }

    @Override
    public long export (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
    {
        m_rs = rs;
        interpreter.setExporter (this);
        interpreter.getSession ().setDoNotClose (true);
        return -1;
    }

    public JaqyResultSet getResultSet ()
    {
        return m_rs;
    }

    @Override
    public void close ()
    {
        if (m_rs != null)
        {
            try
            {
                m_rs.close ();
            }
            catch (SQLException ex)
            {
            }
            m_rs = null;
        }
    }
}

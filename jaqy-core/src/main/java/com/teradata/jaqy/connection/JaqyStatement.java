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
package com.teradata.jaqy.connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author  Heng Yuan
 */
public class JaqyStatement implements AutoCloseable
{
    private final Statement m_statement;
    private final JaqyConnection m_connection;
    private JaqyResultSet m_rs;

    public JaqyStatement (Statement stmt, JaqyConnection conn)
    {
        m_statement = stmt;
        m_connection = conn;
    }

    /**
     * @return  the connection
     */
    public JaqyConnection getConnection ()
    {
        return m_connection;
    }

    @Override
    public void close () throws SQLException
    {
        m_statement.close ();
    }

    public boolean execute (String sql) throws SQLException
    {
        m_rs = null;
        return m_statement.execute (sql);
    }

    public JaqyResultSet getResultSet (JaqyInterpreter interpreter) throws SQLException
    {
        if (m_rs == null)
        {
            ResultSet rs = m_statement.getResultSet ();
            if (rs != null)
                m_rs = m_connection.getHelper ().getResultSet (rs, interpreter);
        }
        return m_rs;
    }

    public boolean getMoreResults () throws SQLException
    {
        m_rs = null;
        return m_statement.getMoreResults ();
    }

    public long getUpdateCount () throws SQLException
    {
        return m_statement.getUpdateCount ();
    }

    public void setFetchSize (int size) throws SQLException
    {
        m_statement.setFetchSize (size);
    }

    public Statement getStatement ()
    {
        return m_statement;
    }
}

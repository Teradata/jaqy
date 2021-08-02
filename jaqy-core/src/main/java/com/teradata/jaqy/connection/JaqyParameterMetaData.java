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

import java.sql.ParameterMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author  Heng Yuan
 */
public class JaqyParameterMetaData
{
    private final ParameterMetaData m_metaData;
    private final JaqyConnection m_connection;

    JaqyParameterMetaData (ParameterMetaData metaData, JaqyConnection conn)
    {
        m_metaData = metaData;
        m_connection = conn;
    }

    /**
     * @return  the metaData
     */
    public ParameterMetaData getMetaData ()
    {
        return m_metaData;
    }

    /**
     * @return  the connection
     */
    public JaqyConnection getConnection ()
    {
        return m_connection;
    }

    public boolean isNumber (int parameter) throws SQLException
    {
        return TypesUtils.isNumber (m_metaData.getParameterType (parameter));
    }

    public boolean isBinary (int parameter) throws SQLException
    {
        return TypesUtils.isBinary (m_metaData.getParameterType (parameter));
    }

    public int getParameterCount () throws SQLException
    {
        return m_metaData.getParameterCount ();
    }

    public int getParameterType (int parameter) throws SQLException
    {
        return m_metaData.getParameterType (parameter);
    }

    public String getParameterTypeName (int parameter) throws SQLException
    {
        return m_metaData.getParameterTypeName (parameter);
    }

    public String getParameterClassName(int parameter) throws SQLException
    {
        return m_metaData.getParameterClassName(parameter);
    }

    public int isNullable (int parameter) throws SQLException
    {
        return m_metaData.isNullable (parameter);
    }

    public boolean isSigned (int parameter) throws SQLException
    {
        return m_metaData.isSigned (parameter);
    }

    public int getScale (int parameter) throws SQLException
    {
        return m_metaData.getScale (parameter);
    }

    public int getPrecision (int parameter) throws SQLException
    {
        return m_metaData.getPrecision (parameter);
    }

    public int getParameterMode (int parameter) throws SQLException
    {
        return m_metaData.getParameterMode (parameter);
    }
}

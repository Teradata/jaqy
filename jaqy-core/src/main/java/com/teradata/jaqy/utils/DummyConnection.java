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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author  Heng Yuan
 */
public class DummyConnection implements Connection
{
    @Override
    public <T> T unwrap (Class<T> iface) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public boolean isWrapperFor (Class<?> iface) throws SQLException
    {
        return false;
    }

    @Override
    public Statement createStatement () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public PreparedStatement prepareStatement (String sql) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public CallableStatement prepareCall (String sql) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public String nativeSQL (String sql) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setAutoCommit (boolean autoCommit) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public boolean getAutoCommit () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void commit () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void rollback () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void close () throws SQLException
    {
    }

    @Override
    public boolean isClosed () throws SQLException
    {
        return true;
    }

    @Override
    public DatabaseMetaData getMetaData () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setReadOnly (boolean readOnly) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public boolean isReadOnly () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setCatalog (String catalog) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public String getCatalog () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setTransactionIsolation (int level) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
        
    }

    @Override
    public int getTransactionIsolation () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public SQLWarning getWarnings () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void clearWarnings () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Statement createStatement (int resultSetType, int resultSetConcurrency) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public PreparedStatement prepareStatement (String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public CallableStatement prepareCall (String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Map<String, Class<?>> getTypeMap () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setTypeMap (Map<String, Class<?>> map) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setHoldability (int holdability) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public int getHoldability () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Savepoint setSavepoint () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Savepoint setSavepoint (String name) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void rollback (Savepoint savepoint) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void releaseSavepoint (Savepoint savepoint) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Statement createStatement (int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public PreparedStatement prepareStatement (String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public CallableStatement prepareCall (String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public PreparedStatement prepareStatement (String sql, int autoGeneratedKeys) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public PreparedStatement prepareStatement (String sql, int[] columnIndexes) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public PreparedStatement prepareStatement (String sql, String[] columnNames) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Clob createClob () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Blob createBlob () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public NClob createNClob () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public SQLXML createSQLXML () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public boolean isValid (int timeout) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setClientInfo (String name, String value) throws SQLClientInfoException
    {
    }

    @Override
    public void setClientInfo (Properties properties) throws SQLClientInfoException
    {
    }

    @Override
    public String getClientInfo (String name) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Properties getClientInfo () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Array createArrayOf (String typeName, Object[] elements) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public Struct createStruct (String typeName, Object[] attributes) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setSchema (String schema) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public String getSchema () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void abort (Executor executor) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public void setNetworkTimeout (Executor executor, int milliseconds) throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }

    @Override
    public int getNetworkTimeout () throws SQLException
    {
        throw ExceptionUtils.getNotImplemented ();
    }
}

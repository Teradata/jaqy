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

import java.sql.*;
import java.util.ArrayList;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.connection.JaqyDefaultResultSet;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.helper.DummyHelper;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.resultset.*;

/**
 * @author  Heng Yuan
 */
public class ResultSetUtils
{
    public static int getDisplayWidth (JaqyResultSet rs, int column) throws SQLException
    {
        JaqyResultSetMetaData meta = rs.getMetaData ();
        // some JDBC drivers (notably SQLite JDBC does not have
        // the correct display size settings.  So here we force
        // the correct calculation.
        int displaySize = meta.getColumnDisplaySize (column);
        int type = meta.getColumnType (column);
        int computeSize = displaySize;
        switch (type)
        {
            case Types.INTEGER:
            {
                computeSize = 10;
                if (meta.isSigned (column))
                    ++computeSize;
                break;
            }
            case Types.REAL:
            {
                int precision = meta.getPrecision (column);
                if (precision <= 7)
                {
                    // we are dealing a 4-byte real
                    computeSize = precision + 2;
                    if (meta.isSigned (column))
                        ++computeSize;
                }
                else if (precision <= 53)
                {
                    computeSize = precision + 2;
                    if (meta.isSigned (column))
                        ++computeSize;
                }
                break;
            }
            case Types.BINARY:
            case Types.VARBINARY:
            {
                int size = meta.getColumnDisplaySize (column);
                computeSize = size * 2;
                if (computeSize < 0)
                {
                    // overflow
                    computeSize = Integer.MAX_VALUE;
                }
                break;
            }
            default:
            {
                computeSize = displaySize;
                break;
            }
        }
        return Math.min (computeSize, displaySize);
    }

    public static String getResultSetType (int type)
    {
        switch (type)
        {
            case ResultSet.TYPE_FORWARD_ONLY:
                return "FORWARD_ONLY";
            case ResultSet.TYPE_SCROLL_INSENSITIVE:
                return "SCROLL_INSENSITIVE";
            case ResultSet.TYPE_SCROLL_SENSITIVE:
                return "SCROLL_SENSITIVE";
            default:
                return "Unknown";
        }
    }

    public static Object copyIfNecessary (Object o, JaqyInterpreter interpreter) throws SQLException
    {
        if (o == null)
            return o;
        int cacheSize = interpreter.getLobCacheSize ();
        if (o instanceof Clob)
        {
            if (o instanceof NClob)
                return new CachedNClob ((NClob)o, cacheSize, interpreter.getCharBuffer ());
            return new CachedClob ((Clob)o, cacheSize, interpreter.getCharBuffer ());
        }
        if (o instanceof Blob)
            return new CachedBlob ((Blob)o, cacheSize, interpreter.getByteBuffer ());
        if (o instanceof SQLXML)
            return new CachedSQLXML ((SQLXML)o, cacheSize, interpreter.getCharBuffer ());
        return o;
    }

    public static InMemoryResultSet copyResultSet (ResultSet rs, long limit, JaqyHelper helper, JaqyInterpreter interpreter) throws SQLException
    {
        InMemoryResultSetMetaData rsmd = ResultSetMetaDataUtils.copyResultSetMetaData (rs.getMetaData (), helper);
        ArrayList<Object[]> rows = new ArrayList<Object[]> ();
        Statement statement = rs.getStatement ();
        int columnCount = rsmd.getColumnCount ();
        if (limit == 0)
            limit = Long.MAX_VALUE;
        boolean hasLob = false;
        while (rs.next () && limit > 0)
        {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; ++i)
            {
                Object o = rs.getObject (i + 1);
                row[i] = ResultSetUtils.copyIfNecessary (o, interpreter);
                if (row[i] instanceof CachedClob ||
                    row[i] instanceof CachedBlob ||
                    row[i] instanceof CachedSQLXML)
                    hasLob = true;
            }
            rows.add (row);
            --limit;
        }
        InMemoryResultSet newRS = new InMemoryResultSet (rows, rsmd, statement);
        newRS.setHasLob (hasLob);
        return newRS;
    }

    public static JaqyResultSet copyResultSet (JaqyResultSet rs, long limit, JaqyInterpreter interpreter) throws SQLException
    {
        InMemoryResultSetMetaData rsmd = ResultSetMetaDataUtils.copyResultSetMetaData (rs.getMetaData ().getMetaData (), rs.getHelper ());
        ArrayList<Object[]> rows = new ArrayList<Object[]> ();
        JaqyStatement stmt = rs.getStatement ();
        int columnCount = rsmd.getColumnCount ();
        if (limit == 0)
            limit = Long.MAX_VALUE;
        boolean hasLob = false;
        while (rs.next () && limit > 0)
        {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; ++i)
            {
                Object o = rs.getObject (i + 1);
                row[i] = ResultSetUtils.copyIfNecessary (o, interpreter);
                if (row[i] instanceof CachedClob ||
                    row[i] instanceof CachedBlob ||
                    row[i] instanceof CachedSQLXML)
                    hasLob = true;
            }
            rows.add (row);
            --limit;
        }
        InMemoryResultSet newRS = new InMemoryResultSet (rows, rsmd, stmt == null ? null : stmt.getStatement ());
        newRS.setHasLob (hasLob);
        JaqyDefaultResultSet jqrs = new JaqyDefaultResultSet (newRS, DummyHelper.getInstance ());
        jqrs.setStatement (stmt);
        return jqrs;
    }

    public static JaqyDefaultResultSet getResultSet (PropertyTable pt)
    {
        InMemoryResultSetMetaData rsmd = ResultSetMetaDataUtils.copyResultSetMetaData (pt);
        InMemoryResultSet newRS = new InMemoryResultSet (pt.getRows (), rsmd, null);
        return new JaqyDefaultResultSet (newRS, DummyHelper.getInstance ());
    }
}

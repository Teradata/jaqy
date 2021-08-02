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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.connection.JaqyStatement;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.schema.SchemaUtils;

/**
 * @author  Heng Yuan
 */
public class SessionUtils
{
    public static void checkOpen (JaqyInterpreter interpreter)
    {
        if (interpreter.getSession ().isClosed ())
        {
            interpreter.error ("Current session is closed.");
        }
    }

    private static Object getTableSchema (String tableName, boolean schema, Session session, JaqyInterpreter interpreter) throws SQLException
    {
        JaqyConnection conn = session.getConnection ();
        JaqyHelper helper = conn.getHelper ();

        String query = "SELECT * FROM " + tableName + " WHERE 1 = 0";
        try (JaqyStatement stmt = helper.createStatement (true))
        {
            stmt.execute (query);
            JaqyResultSet rs = stmt.getResultSet (interpreter);
            if (rs == null)
                throw ExceptionUtils.getTableNotFound ();
            JaqyResultSetMetaData meta = rs.getMetaData ();
            SchemaInfo schemaInfo = ResultSetMetaDataUtils.getColumnInfo (meta.getMetaData (), null);

            Object returnValue;
            if (schema)
            {
                returnValue = SchemaUtils.getTableSchema (helper, schemaInfo, tableName, true, false);
            }
            else
            {
                int count = schemaInfo.columns.length;

                PropertyTable pt = new PropertyTable (new String[]{ "Column", "Type", "Nullable" });
                for (int i = 0; i < count; ++i)
                {
                    String columnName = schemaInfo.columns[i].name;
                    String columnType = helper.getTypeName (schemaInfo.columns[i], false);
                    String nullable = (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNoNulls) ? "No" : (schemaInfo.columns[i].nullable == ResultSetMetaData.columnNullable ? "Yes" : "Unknown");
                    pt.addRow (new String[]{ columnName, columnType, nullable });
                }
                returnValue = ResultSetUtils.getResultSet (pt);
            }
            rs.close ();

            return returnValue;
        }
    }

    public static String getTableSchema (String tableName, Session session, JaqyInterpreter interpreter) throws SQLException
    {
        return (String)getTableSchema (tableName, true, session, interpreter);
    }

    public static JaqyResultSet getTableColumns (String tableName, Session session, JaqyInterpreter interpreter) throws SQLException
    {
        return (JaqyResultSet)getTableSchema (tableName, false, session, interpreter);
    }

    public static boolean checkTableExists (Session session, String tableName)
    {
        JaqyConnection conn = session.getConnection ();
        JaqyHelper helper = conn.getHelper ();

        String query = "SELECT * FROM " + tableName + " WHERE 1 = 0";
        try (JaqyStatement stmt = helper.createStatement (true))
        {
            stmt.execute (query);
            return true;
        }
        catch (Exception ex)
        {
        }
        return false;
    }
}

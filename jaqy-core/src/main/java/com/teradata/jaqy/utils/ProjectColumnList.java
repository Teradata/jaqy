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
import java.util.ArrayList;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.helper.DummyHelper;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Project;
import com.teradata.jaqy.resultset.InMemoryResultSetMetaData;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.utils.exp.ColumnNode;
import com.teradata.jaqy.utils.exp.ExpNode;

/**
 * @author  Heng Yuan
 */
public class ProjectColumnList
{
    private final ArrayList<ProjectColumn> m_columnList;
    private ExpNodeProject m_project;
    private JaqyResultSetMetaData m_meta;

    public ProjectColumnList ()
    {
        m_columnList = new ArrayList<ProjectColumn> ();
    }

    public void add (ProjectColumn col)
    {
        m_columnList.add (col);
    }

    public void bind (JaqyResultSet rs, JaqyInterpreter interpreter) throws SQLException
    {
        int numCols = m_columnList.size ();
        ExpNode[] exps = new ExpNode[numCols];

        FullColumnInfo[] columnInfos = new FullColumnInfo[numCols];
        ResultSetMetaData rsmd = rs.getMetaData ().getMetaData ();
        JaqyHelper helper = rs.getHelper ();
        int columnCount = rsmd.getColumnCount ();
        for (int i = 0; i < numCols; ++i)
        {
            ProjectColumn column = m_columnList.get (i);
            int index;
            if (column.columnIndex > 0)
            {
                if (column.columnIndex > columnCount)
                {
                    throw new JaqyException ("Invalid projection column: " + column.columnIndex);
                }
                index = column.columnIndex;
            }
            else
            {
                index = rs.findColumn (column.name);
            }
            columnInfos[i] = ResultSetMetaDataUtils.getColumnInfo (rsmd, index, helper);
            if (column.name == null)
            {
                column.name = columnInfos[i].label;
            }
            if (column.asName != null)
            {
                columnInfos[i].name = column.asName;
                columnInfos[i].label = column.asName;
            }
            ColumnNode exp = new ColumnNode (column.name);
            exp.bind (rs, null, interpreter);
            exps[i] = exp;
        }
        m_project = new ExpNodeProject (exps);
        m_meta = new JaqyResultSetMetaData (new InMemoryResultSetMetaData (columnInfos), DummyHelper.getInstance ());
    }

    public Project getProject ()
    {
        return m_project;
    }

    public JaqyResultSetMetaData getMetaData ()
    {
        return m_meta;
    }
}

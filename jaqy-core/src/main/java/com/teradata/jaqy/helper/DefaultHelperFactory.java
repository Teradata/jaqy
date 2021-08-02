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
package com.teradata.jaqy.helper;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyHelperFactory;
import com.teradata.jaqy.schema.TypeInfo;
import com.teradata.jaqy.utils.SimpleQuery;

/**
 * @author  Heng Yuan
 */
public class DefaultHelperFactory implements JaqyHelperFactory
{
    public final static String CATALOG = "catalogSQL";
    public final static String SCHEMA = "schemaSQL";
    public final static String TABLE_SCHEMA = "tableSchemaSQL";
    public final static String TABLE_COLUMN = "tableColumnSQL";

    private Hashtable<String,SimpleQuery> m_sqlMap = new Hashtable<String,SimpleQuery> ();
    private JdbcFeatures m_features = new JdbcFeatures ();
    private Map<Integer, TypeInfo> m_customTypeMap;
    private Map<Integer, TypeInfo> m_customImportTypeMap;

    public DefaultHelperFactory ()
    {
    }

    protected DefaultHelper createHelper (JdbcFeatures features, JaqyConnection conn, Globals globals)
    {
        return new DefaultHelper (getFeatures(), conn, globals);
    }

    @Override
    public JaqyHelper getHelper (JaqyConnection conn, Globals globals)
    {
        DefaultHelper helper = createHelper (getFeatures (), conn, globals);
        setupHelper (helper);
        return helper;
    }

    protected void setupHelper (DefaultHelper helper)
    {
        helper.setCatalogQuery (m_sqlMap.get (CATALOG));
        helper.setSchemaQuery (m_sqlMap.get (SCHEMA));
        helper.setTableSchemaQuery (m_sqlMap.get (TABLE_SCHEMA));
        helper.setTableColumnQuery (m_sqlMap.get (TABLE_COLUMN));
        helper.setCustomTypeMap (m_customTypeMap);
        helper.setCustomImportTypeMap (m_customImportTypeMap);
    }

    public JdbcFeatures getFeatures ()
    {
        return m_features;
    }

    public void setFeatures (JdbcFeatures features)
    {
        m_features = features;
    }

    public void setCustomTypeMap (Map<Integer, TypeInfo> map)
    {
        m_customTypeMap = map;
    }


    public void setCustomImportTypeMap (Map<Integer, TypeInfo> map)
    {
        m_customImportTypeMap = map;
    }


    public void setSQLMap (HashMap<String,SimpleQuery> sqls)
    {
        m_sqlMap.putAll (sqls);
    }
}

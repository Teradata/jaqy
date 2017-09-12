/*
 * Copyright (c) 2017 Teradata
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

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.utils.SimpleQuery;

/**
 * @author	Heng Yuan
 */
public class TeradataHelperFactory extends DefaultHelperFactory
{
	public TeradataHelperFactory ()
	{
		getFeatures ().noCatalog = true;
		HashMap<String, SimpleQuery> map = new HashMap<String, SimpleQuery> ();
		map.put (SCHEMA, new SimpleQuery ("SELECT DATABASE", 1));
		map.put (TABLE_SCHEMA, new SimpleQuery ("SHOW TABLE {0}", 1));
		map.put (TABLE_COLUMN, new SimpleQuery ("HELP TABLE {0}", 1));
		setSQLMap (map);;
	}

	@Override
	protected DefaultHelper createHelper (JdbcFeatures features, JaqyConnection conn, Globals globals)
	{
		return new TeradataHelper (features, conn, globals);
	}
}

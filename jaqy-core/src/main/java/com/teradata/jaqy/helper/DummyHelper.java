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

import java.sql.ResultSet;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.DummyConnection;

/**
 * @author	Heng Yuan
 */
public class DummyHelper implements JaqyHelper
{
	private final static JaqyHelper s_instance = new DummyHelper ();

	public static JaqyHelper getInstance ()
	{
		return s_instance;
	}

	private final JaqyConnection m_connection = new JaqyConnection (new DummyConnection ());

	private DummyHelper ()
	{
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs)
	{
		return new JaqyResultSet (rs, this);
	}

	@Override
	public JaqyConnection getConnection ()
	{
		return m_connection;
	}
}

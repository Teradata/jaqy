/*
 * Copyright (c) 2017-2018 Teradata
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

import java.io.IOException;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyExporter;

/**
 * @author	Heng Yuan
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
	public long export (JaqyResultSet rs, Session session, JaqyInterpreter interpreter, Globals globals) throws Exception
	{
		m_rs = rs;
		interpreter.setExporter (this);
		session.setDoNotClose (true);
		return -1;
	}

	public JaqyResultSet getResultSet ()
	{
		return m_rs;
	}

	@Override
	public void close () throws IOException
	{
		m_rs = null;
	}
}

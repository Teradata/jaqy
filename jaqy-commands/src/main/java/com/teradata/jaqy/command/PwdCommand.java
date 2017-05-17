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
package com.teradata.jaqy.command;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class PwdCommand extends JaqyCommandAdapter
{
	@Override
	public String getDescription ()
	{
		return "prints the current catalog and schema.";
	}

	@Override
	public void execute (String[] args, Globals globals, JaqyInterpreter interpreter)
	{
		if (!SessionUtils.checkOpen (interpreter))
			return;
		Session session = interpreter.getSession ();
		String catalog = session.getCatalog ();
		String schema = session.getSchema ();

		interpreter.println ("Catalog: " + (catalog == null ? "" : catalog) + ", Schema: " + (schema == null ? "" : schema));
	}
}

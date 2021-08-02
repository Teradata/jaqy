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

import java.io.IOException;
import java.util.ArrayList;

import com.teradata.jaqy.interfaces.ExpressionHandler;

/**
 * @author	Heng Yuan
 */
public class ImportExpressionHandler implements ExpressionHandler
{
	private final ArrayList<String> m_exps = new ArrayList<String> ();

	public ImportExpressionHandler ()
	{
	}

	public String[] getExpressions ()
	{
		if (m_exps.size () == 0)
		{
			return null;
		}
		return m_exps.toArray (new String[m_exps.size ()]);
	}

	@Override
	public Object eval (String name) throws IOException
	{
		m_exps.add (name);
		return "?";
	}
}

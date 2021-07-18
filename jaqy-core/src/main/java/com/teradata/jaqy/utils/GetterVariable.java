/*
 * Copyright (c) 2021 Heng Yuan
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

import com.teradata.jaqy.interfaces.Variable;

/**
 * @author	Heng Yuan
 */
public abstract class GetterVariable implements Variable
{
	private String m_name;

	public GetterVariable (String name)
	{
		m_name = name;
	}

	@Override
	public boolean set (Object value)
	{
		throw new IllegalArgumentException ("Cannot set the variable: " + m_name);
	}

	@Override
	public String getName ()
	{
		return m_name;
	}
}

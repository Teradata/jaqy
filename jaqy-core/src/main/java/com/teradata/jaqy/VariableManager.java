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
package com.teradata.jaqy;

import java.util.*;

import javax.script.Bindings;

import com.teradata.jaqy.interfaces.VariableHook;
import com.teradata.jaqy.utils.FixedVariableHook;

/**
 * @author	Heng Yuan
 */
public class VariableManager implements Bindings
{
	private final VariableManager m_parent;

	private final Object m_lock = new Object ();
	/** all the variables in the current scope, including exports. */
	private final HashMap<String, Object> m_variables = new HashMap<String, Object> ();
	/** exported variables */
	private final HashSet<String> m_exports = new HashSet<String> ();

	VariableManager (VariableManager parent)
	{
		m_parent = parent;

		if (parent != null)
		{
			setVariable ("parent", new FixedVariableHook (parent));
		}
	}

	public VariableManager getParent ()
	{
		return m_parent;
	}

	public void addVariableHook (String name, VariableHook hook)
	{
		synchronized (m_lock)
		{
			m_variables.put (name, hook);
		}
	}

	public void export (String name)
	{
		synchronized (m_lock)
		{
			if (m_variables.get (name) != null)
			{
				m_exports.add (name);
			}
		}
	}

	public Object setVariable (String name, Object value)
	{
		if (name == null ||
			name.length () == 0)
		{
			throw new IllegalArgumentException ("Empty variable name.");
		}

		Object hook;
		synchronized (m_lock)
		{
			hook = m_variables.get (name);
			if (!(hook instanceof VariableHook))
			{
				return m_variables.put (name, value);
			}
		}
		if (!((VariableHook) hook).set (name, value))
		{
			throw new IllegalArgumentException ("Cannot set the variable: " + name);
		}
		return null;
	}

	public Object getVariable (String name)
	{
		Object value;
		synchronized (m_lock)
		{
			value = m_variables.get (name);
		}
		if (value instanceof VariableHook)
			return ((VariableHook) value).get (name);
		return value;
	}

	public String getVariableString (String name)
	{
		Object o = getVariable (name);
		if (o == null)
			return "";
		return o.toString ();
	}

	@Override
	public int size ()
	{
		synchronized (m_lock)
		{
			return m_variables.size ();
		}
	}

	@Override
	public boolean isEmpty ()
	{
		synchronized (m_lock)
		{
			return m_variables.isEmpty ();
		}
	}

	@Override
	public boolean containsValue (Object value)
	{
		synchronized (m_lock)
		{
			return m_variables.containsValue (value);
		}
	}

	@Override
	public void clear ()
	{
		synchronized (m_lock)
		{
			m_variables.clear ();
		}
	}

	@Override
	public Set<String> keySet ()
	{
		synchronized (m_lock)
		{
			return m_variables.keySet ();
		}
	}

	@Override
	public Collection<Object> values ()
	{
		synchronized (m_lock)
		{
			return m_variables.values ();
		}
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet ()
	{
		synchronized (m_lock)
		{
			return m_variables.entrySet ();
		}
	}

	@Override
	public Object put (String name, Object value)
	{
		return setVariable (name, value);
	}

	@Override
	public void putAll (Map<? extends String, ? extends Object> toMerge)
	{
		if (toMerge == null)
		{
			throw new NullPointerException ("toMerge is null");
		}

		for (Map.Entry<? extends String, ? extends Object> entry : toMerge.entrySet ())
		{
			String key = entry.getKey ();
			setVariable (key, entry.getValue ());
		}
	}

	@Override
	public boolean containsKey (Object key)
	{
		synchronized (m_lock)
		{
			return m_variables.containsKey (key);
		}
	}

	@Override
	public Object get (Object key)
	{
		if (key instanceof String)
		{
			return getVariable ((String) key);
		}
		return null;
	}

	@Override
	public Object remove (Object key)
	{
		if (key instanceof String)
		{
			synchronized (m_lock)
			{
				return m_variables.remove ((String) key);
			}
		}
		return null;
	}
}

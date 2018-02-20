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
package com.teradata.jaqy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import com.teradata.jaqy.interfaces.Variable;
import com.teradata.jaqy.utils.FixedVariable;
import com.teradata.jaqy.utils.SimpleVariable;

/**
 * @author	Heng Yuan
 */
public class VariableManager implements Bindings
{
	private final VariableManager m_parent;

	private final Object m_lock = new Object ();
	/** all the variables in the current scope, including exports. */
	private final HashMap<String, Object> m_variables = new HashMap<String, Object> ();

	VariableManager (VariableManager parent)
	{
		m_parent = parent;

		if (parent != null)
		{
			setVariable (new FixedVariable ("parent", parent, "Parent variables"));
		}
	}

	public VariableManager getParent ()
	{
		return m_parent;
	}

	public void setVariable (Variable var)
	{
		synchronized (m_lock)
		{
			String name = var.getName ();
			if (m_parent != null &&
				m_parent.containsKey (name))
			{
				m_parent.setVariable (var);
			}
			else
			{
				m_variables.put (name, var);
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

		if (value instanceof Variable)
		{
			setVariable ((Variable)value);
			return null;
		}

		if (m_parent != null &&
			m_parent.containsKey (name))
		{
			return m_parent.setVariable (name, value);
		}

		Variable var;
		synchronized (m_lock)
		{
			var = (Variable)m_variables.get (name);
			if (var == null)
			{
				var = new SimpleVariable (name);
				var.set (value);
				m_variables.put (name, var);
				return null;
			}
		}
		Object old = var.get ();
		if (!var.set (value))
		{
			throw new IllegalArgumentException ("Cannot set the variable: " + name);
		}
		return old;
	}

	public Variable getVariable (String name)
	{
		synchronized (m_lock)
		{
			Variable var = (Variable)m_variables.get (name);
			if (var != null)
				return var;
			if (m_parent != null)
				return m_parent.getVariable (name);
			return null;
		}
	}

	public String getVariableString (String name)
	{
		Object o = get (name);
		if (o == null)
			return "";
		return o.toString ();
	}

	@Override
	public int size ()
	{
		synchronized (m_lock)
		{
			int s = m_variables.size ();
			if (m_parent != null)
				s += m_parent.size ();
			return s;
		}
	}

	@Override
	public boolean isEmpty ()
	{
		synchronized (m_lock)
		{
			boolean b = m_variables.isEmpty ();
			if (!b)
				return false;
			if (m_parent != null)
				return m_parent.isEmpty ();
			return true;
		}
	}

	@Override
	public boolean containsValue (Object value)
	{
		synchronized (m_lock)
		{
			boolean b = m_variables.containsValue (value);
			if (b)
				return true;
			if (m_parent != null)
				return m_parent.containsValue (value);
			return false;
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
		if (m_parent == null)
		{
			synchronized (m_lock)
			{
				return m_variables.keySet ();
			}
		}
		else
		{
			HashSet<String> set = new HashSet<String> ();
			set.addAll (m_parent.keySet ());
			synchronized (m_lock)
			{
				set.addAll (m_variables.keySet ());
			}
			return set;
		}
	}

	@Override
	public Collection<Object> values ()
	{
		if (m_parent == null)
		{
			synchronized (m_lock)
			{
				return m_variables.values ();
			}
		}
		else
		{
			ArrayList<Object> list = new ArrayList<Object> ();
			list.addAll (m_parent.values ());
			synchronized (m_lock)
			{
				list.addAll (m_variables.values ());
			}
			return list;
		}
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet ()
	{
		if (m_parent == null)
		{
			synchronized (m_lock)
			{
				return m_variables.entrySet ();
			}
		}
		else
		{
			HashSet<Map.Entry<String, Object>> set = new HashSet<Map.Entry<String, Object>> ();
			set.addAll (m_parent.entrySet ());
			synchronized (m_lock)
			{
				set.addAll (m_variables.entrySet ());
			}
			return set;
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
			boolean b = m_variables.containsKey (key);
			if (b)
				return true;
			if (m_parent != null)
				return m_parent.containsKey (key);
			return false;
		}
	}

	@Override
	public Object get (Object key)
	{
		if (key instanceof String)
		{
			Variable var = getVariable ((String)key);
			if (var == null)
				return null;
			return var.get ();
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
				if (m_variables.containsKey (key))
					return m_variables.remove (key);
				if (m_parent != null)
					return m_parent.remove (key);
			}
		}
		return null;
	}
}

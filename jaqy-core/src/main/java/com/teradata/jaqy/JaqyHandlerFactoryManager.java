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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;

import com.teradata.jaqy.interfaces.JaqyHandlerFactory;

/**
 * @author	Heng Yuan
 */
public class JaqyHandlerFactoryManager<E>
{
	private final Globals m_globals;
	private final HashMap<String, JaqyHandlerFactory<E>> m_factoryMap = new HashMap<String, JaqyHandlerFactory<E>> ();
	private final Object m_lock = new Object ();
	private final String m_rc;

	public JaqyHandlerFactoryManager (Globals globals, String rc)
	{
		m_globals = globals;
		m_rc = rc;
		try
		{
			loadRC (ClassLoader.getSystemClassLoader ());
		}
		catch (Exception ex)
		{
			globals.log (Level.WARNING, ex);
		}
	}

	public void loadRC (ClassLoader cl) throws IOException
	{
		String name = "META-INF/services/" + m_rc;
		m_globals.log (Level.INFO, "loading service: " + name);
		Enumeration<URL> e;
		if (cl instanceof URLClassLoader)
			e = ((URLClassLoader) cl).findResources (name);
		else
			e = cl.getResources (name);
		for (; e.hasMoreElements ();)
		{
			URL url = e.nextElement ();
			m_globals.log (Level.INFO, "load service url: " + url);
			loadURL (cl, url);
		}
	}

	private void loadURL (ClassLoader cl, URL url) throws IOException
	{
		BufferedReader reader = new BufferedReader (new InputStreamReader (url.openStream (), "utf-8"));
		String line;
		while ((line = reader.readLine ()) != null)
		{
			line = line.trim ();
			if (line.length () == 0)
				continue;
			m_globals.log (Level.INFO, "load service class: " + line);
			loadClass (cl, line);
		}
		reader.close ();
	}

	private void loadClass (ClassLoader cl, String className)
	{
		try
		{
			Class<?> c = cl.loadClass (className);
			@SuppressWarnings ("unchecked")
			JaqyHandlerFactory<E> factory = (JaqyHandlerFactory<E>)c.newInstance ();
			register (factory);
		}
		catch (Exception ex)
		{
			m_globals.log (Level.WARNING, ex);
		}
	}

	public void register (JaqyHandlerFactory<E> factory)
	{
		synchronized (m_lock)
		{
			m_factoryMap.put (factory.getName (), factory);
		}
	}

	public boolean hasHandler (String name)
	{
		synchronized (m_lock)
		{
			return m_factoryMap.get (name) != null;
		}
	}

	public JaqyHandlerFactory<E> getHandlerFactory (String name)
	{
		synchronized (m_lock)
		{
			return m_factoryMap.get (name);
		}
	}

	public E getHandler (String name, String[] args, JaqyInterpreter interpreter) throws Exception
	{
		JaqyHandlerFactory<E> factory;
		synchronized (m_lock)
		{
			factory = m_factoryMap.get (name);
		}
		if (factory == null)
			return null;
		return factory.getHandler (args, interpreter);
	}

	public String[] getNames ()
	{
		TreeSet<String> names = new TreeSet<String> ();
		synchronized (m_lock)
		{
			names.addAll (m_factoryMap.keySet ());
		}
		return names.toArray (new String[names.size ()]);
	}
}

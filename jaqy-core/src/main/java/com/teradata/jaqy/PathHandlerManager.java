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

import com.teradata.jaqy.interfaces.PathHandler;
import com.teradata.jaqy.path.FilePathHandler;

/**
 * @author	Heng Yuan
 */
public class PathHandlerManager
{
	private final Object m_lock = new Object ();
	private final ArrayList<PathHandler> m_handlers = new ArrayList<PathHandler> ();
	private final PathHandler m_filePathHandler = new FilePathHandler ();

	PathHandlerManager ()
	{
		addPathHandler (m_filePathHandler);
	}

	/**
	 * Gets the default file path handler.
	 * @return	the default file path handler
	 */
	public PathHandler getFilePathHandler ()
	{
		return m_filePathHandler;
	}

	/**
	 * Register a new path handler.
	 *
	 * @param	pathHandler
	 * 			PathHandler to be registered.
	 */
	public void addPathHandler (PathHandler pathHandler)
	{
		synchronized (m_lock)
		{
			m_handlers.add (pathHandler);
		}
	}

	/**
	 * Remove a path handler based on the class name.
	 *
	 * @param	className
	 * 			PathHandler class name
	 */
	public void removePathHandler (String className)
	{
		synchronized (m_lock)
		{
			for (int i = 0; i < m_handlers.size (); ++i)
			{
				PathHandler handler = m_handlers.get (i);
				if (className.equals (handler.getClass ().getCanonicalName ()))
				{
					m_handlers.remove (i);
					break;
				}
			}
		}
	}

	/**
	 * Find a PathHandler for the path provided.
	 *
	 * @param	path
	 * 			path
	 * @return	a PathHandler that can handle the path.
	 */
	public PathHandler getHandler (String path)
	{
		synchronized (m_lock)
		{
			for (PathHandler handler : m_handlers)
			{
				if (handler.canHandle (path))
					return handler;
			}
		}
		return null;
	}

	/**
	 * Check if the registry already has a path handler.
	 *
	 * @param	className
	 * 			PathHandler class name
	 * @return	true if the handler was found.  false otherwise.
	 */
	public boolean hasPathHandler (String className)
	{
		synchronized (m_lock)
		{
			for (PathHandler handler : m_handlers)
			{
				if (className.equals (handler.getClass ().getCanonicalName ()))
					return true;
			}
		}
		return false;
	}
}

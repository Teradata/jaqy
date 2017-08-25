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
package com.teradata.jaqy.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author	Heng Yuan
 */
public class PathUtils
{
	/**
	 * Check if a ':' is not part of 'C:\....' windows type path.
	 *
	 * @param	path
	 *			a particular path
	 * @param	index
	 *			the colon position
	 * @return	true if can split at the position.  false otherwise.
	 */
	private static boolean isValid (String path, int index)
	{
		if (index != 1)
			return true;

		++index;
		if (index < path.length () &&
			path.charAt (index) == '\\')
			return false;

		return true;
	}

	private static String[] splitWindows (String path)
	{
		ArrayList<String> pathList = new ArrayList<String> ();
		int index = 0;
		while (index < path.length ())
		{
			index = path.indexOf (':', index);
			if (index < 0)
				break;
			if (isValid (path, index))
			{
				pathList.add (path.substring (0, index));
				path = path.substring (index + 1);
				index = 0;
			}
			else
			{
				++index;
			}
		}
		if (path.length () > 0)
			pathList.add (path);
		return pathList.toArray (new String[pathList.size ()]);
	}

	/**
	 * Split a classpath into individual ones.  Try to be Unix and Windows
	 * compatible.
	 *
	 * @param	path
	 *			class path
	 * @return	splitted strings
	 */
	public static String[] split (String path)
	{
		// fix any relative paths
		if ('/' == File.separatorChar)
		{
			path = path.replace ('\\', '/');
		}
		else if ('\\' == File.separatorChar)
		{
			path = path.replace ('/', '\\');
		}

		// it is possible some paths contain multiple jars
		// in order to load all the dependencies.
		String[] paths = null;
		if (':' == File.pathSeparatorChar)
		{
			paths = path.split ("[;:]");
		}
		else if (';' == File.pathSeparatorChar)
		{
			paths = path.split (";");
			ArrayList<String> pathList = new ArrayList<String> ();
			for (String p : paths)
			{
				String[] newPaths = splitWindows (p);
				for (String p2 : newPaths)
					pathList.add (p2);
			}
			paths = new String[pathList.size ()];
			pathList.toArray (paths);
		}
		return paths;
	}

	/**
	 * Convert relative paths specified to absolute paths.
	 *
	 * @param	path
	 * 			Path string.  There can be multiple paths separated by separate char.
	 * @param	dir
	 * 			The current working directory.
	 * @return	the updated path.
	 */
	public static String toAbsolutePath (String path, File dir)
	{
		String[] paths = split (path);
		char sep = File.pathSeparatorChar;
		StringBuilder builder = new StringBuilder ();
		for (String p : paths)
		{
			File f;
			if (p.startsWith ("/") && '/' == File.separatorChar)
				f = new File (p);
			else
				f = new File (dir, p);
			if (builder.length () > 0)
				builder.append (sep);
			String newPath;
			try
			{
				newPath = f.getCanonicalPath ();
			}
			catch (IOException ex)
			{
				// On windows, if path is absolute, we it will not canonize with dir specified.
				// So we will juse use path as is.
				newPath = p;
			}
			builder.append (newPath);
		}
		return builder.toString ();
	}
}

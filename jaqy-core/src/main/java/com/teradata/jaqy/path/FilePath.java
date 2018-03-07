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
package com.teradata.jaqy.path;

import java.io.*;

import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.utils.PathUtils;

/**
 * @author	Heng Yuan
 */
public class FilePath implements Path
{
	private final File m_file;

	public FilePath (File file)
	{
		m_file = file;
	}

	public File getFile ()
	{
		return m_file;
	}

	@Override
	public Path getParent ()
	{
		return new FilePath (m_file.getParentFile ());
	}

	@Override
	public Path getRelativePath (String name)
	{
		File dir = m_file.isDirectory () ? m_file : m_file.getParentFile ();

		return new FilePath (PathUtils.getRelativePath (dir, name));
	}

	@Override
	public String getPath ()
	{
		return m_file.getPath ();
	}

	@Override
	public String getCanonicalPath ()
	{
		try
		{
			return m_file.getCanonicalPath ();
		}
		catch (IOException ex)
		{
			return m_file.getAbsolutePath ();
		}
	}

	@Override
	public boolean isFile ()
	{
		return m_file.isFile ();
	}

	@Override
	public long length ()
	{
		return m_file.length ();
	}

	@Override
	public boolean exists ()
	{
		return m_file.exists ();
	}

	@Override
	public InputStream getInputStream () throws IOException
	{
		return new FileInputStream (m_file);
	}

	@Override
	public OutputStream getOutputStream () throws IOException
	{
		return new FileOutputStream (m_file);
	}

	public void delete ()
	{
		m_file.delete ();
	}
}

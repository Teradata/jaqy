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
package com.teradata.jaqy.path;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.Os;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.interfaces.PathHandler;

/**
 * @author	Heng Yuan
 */
public class FilePathHandler implements PathHandler
{
	private Pattern m_winPath;

	public FilePathHandler ()
	{
		if (Os.isWindows ())
		{
			m_winPath = Pattern.compile ("^[a-zA-Z]:\\\\.*");
		}
	}

	@Override
	public Path getPath (String path, JaqyInterpreter interpreter) throws IOException
	{
		File file;
		if (path.startsWith ("file:/"))
		{
			URL url = new URL (path);
			file = new File (url.getFile ());
		}
		else
		{
			file = new File (path);
		}
		return new FilePath (file);
	}

	@Override
	public boolean canHandle (String path)
	{
		if (path.startsWith ("/") ||
			path.startsWith ("\\") ||
			path.startsWith ("file:/"))
			return true;
		if (m_winPath != null)
		{
			Matcher m = m_winPath.matcher (path);
			if (m.matches ())
				return true;
		}
		return false;
	}
}

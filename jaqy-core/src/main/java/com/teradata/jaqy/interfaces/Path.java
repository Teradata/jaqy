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
package com.teradata.jaqy.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author	Heng Yuan
 */
public interface Path
{
	/**
	 * Get the parent path.
	 *
	 * @return	the parent path (i.e. directory).
	 * @throws	IOException
	 * 			in case of error
	 */
	public Path getParent () throws IOException;
	/**
	 * Get the relative path.
	 *
	 * @param	name
	 * 			relative path to this path.
	 * @return	relative path object.
	 * @throws	IOException
	 * 			in case of error
	 */
	public Path getRelativePath (String name) throws IOException;
	/**
	 * Get the path name.
	 *
	 * @return	the path name.
	 */
	public String getPath ();
	/**
	 * Get the fully resolved path name.
	 * 
	 * @return	the fully resolved path name.
	 */
	public String getCanonicalPath ();
	/**
	 * Check if the path points to an existing file.
	 *
	 * @return	true iff the path exists and is a file.
	 * 			false otherwise.
	 */
	public boolean isFile ();
	/**
	 * Get the length of the file.
	 * @return	the length of the file.
	 * 			0L if the file does not exist.
	 */
	public long length ();
	/**
	 * Checks if the path exists.
	 *
	 * @return	true iff the path exists.
	 * 			false otherwise.
	 */
	public boolean exists();
	/**
	 * Creates an input stream for getting the object.
	 *
	 * @return	an InputStream for reading the object.
	 * @throws	IOException
	 * 			in case of error.
	 */
	public InputStream getInputStream () throws IOException;
	/**
	 * Creates an output stream for writing data.
	 *
	 * @return	an OutputStream for writing data.
	 * @throws	IOException
	 * 			in case of error.
	 */
	public OutputStream getOutputStream () throws IOException;
}

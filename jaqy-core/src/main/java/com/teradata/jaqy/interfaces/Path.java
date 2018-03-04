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
package com.teradata.jaqy.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author	Heng Yuan
 */
public interface Path
{
	public Path getParent ();
	public Path getRelativePath (String name);
	public String getName ();
	public String getPath ();
	public String getFullName ();
	public boolean isDirectory ();
	public boolean isFile ();
	public long length ();
	public boolean exists();
	public InputStream getInputStream () throws IOException;
	public OutputStream getOutputStream () throws IOException;
}

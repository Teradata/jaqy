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

import java.io.File;

/**
 * @author	Heng Yuan
 */
public interface LineInput
{
	public static class Input
	{
		public String line;
		public boolean interactive;
	}

	/**
	 * Getting a line from input.  The input is stripped of the EOL
	 * characters.
	 *
	 * @param	Input (OUT)
	 *			where the output of line and interactive status is stored.
	 * @return	true if has input.
	 * 			false if EOF.
	 */
	public boolean getLine (Input input);

	/**
	 * Get the directory of the input.
	 * 
	 * @return	the directory of the input.
	 */
	public Path getDirectory ();
	/**
	 * Get the directory of the input.
	 * 
	 * @return	the directory of the input.
	 */
	public File getFileDirectory ();
}

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
package com.teradata.jaqy.interfaces;

/**
 * @author	Heng Yuan
 */
public interface Variable
{
	/**
	 * Gets a variable value.
	 *
	 * @return	the value of the object.
	 */
	public Object get ();
	/**
	 * Sets a variable value.
	 * @param	value
	 *			the variable value.
	 * @return	true if the variable is set successfully.  false otherwise.
	 */
	public boolean set (Object value);
	/**
	 * Gets the name of the variable.
	 *
	 * @return	the name of the variable.
	 */
	public String getName ();
	/**
	 * Gets the variable description.
	 *
	 * @return	the variable description.
	 */
	public String getDescription ();
}

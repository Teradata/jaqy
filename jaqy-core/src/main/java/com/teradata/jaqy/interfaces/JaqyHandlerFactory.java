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
public interface JaqyHandlerFactory<E>
{
	/**
	 * Gets the name of the factory.
	 * @return	the name of the factory.
	 */
	public String getName ();
	/**
	 * Gets the handler instance.
	 * @param	args
	 *			command line arguments
	 * @return	the handler instance.
	 * @throws	Exception
	 * 			in case of error.
	 */
	public E getHandler (String[] args) throws Exception;
	/**
	 * Gets the help description.
	 * @return	the help description.
	 */
	public String getLongDescription ();
}

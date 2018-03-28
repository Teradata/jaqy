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

import java.io.PrintWriter;

import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public interface JaqyPrinter
{
	/**
	 * Gets the name of the printer
	 * @return	the name of the printer.
	 */
	public String getName ();
	/**
	 * Display a ResultSet on screen.
	 *
	 * @param	rs
	 * 			the ResultSet.
	 * @param	pw
	 * 			printer writer
	 * @param	limit
	 * 			the maximum number of rows to be displayed.
	 * @param	interpreter
	 * 			the interpreter.
	 * @return	the activity count.  It may be limited by the display limit.
	 * @throws	Exception
	 * 			in case of error.
	 */
	public long print (JaqyResultSet rs, PrintWriter pw, long limit, JaqyInterpreter interpreter) throws Exception;
	/**
	 * Does this ResultSet printer only scans forward?
	 *
	 * @return	true if this ResultSet printer only scans forward.
	 * 			false otherwise.
	 */
	boolean isForwardOnly ();
}

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

import java.io.PrintWriter;

import com.teradata.jaqy.Echo;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
public interface Display
{
	// Input Functions
	public String getPassword (JaqyInterpreter interpreter, String prompt);

	// Output Functions
	public void print (JaqyInterpreter interpreter, String s);
	public void println (JaqyInterpreter interpreter, String s);

	public void errorParsingArgument (JaqyInterpreter interpreter);

	public void error (JaqyInterpreter interpreter, Throwable t);

	public void error (JaqyInterpreter interpreter, String msg);

	public void echo (JaqyInterpreter interpreter, String msg, boolean interactive);

	public PrintWriter getPrintWriter ();

	public void showPrompt (JaqyInterpreter interpreter);
	public void showTitle (JaqyInterpreter interpreter);
	public void showSuccess (JaqyInterpreter interpreter);
	public void showSuccessUpdate (JaqyInterpreter interpreter);
	public void showActivityCount (JaqyInterpreter interpreter);

	public Echo getEcho ();

	public void setEcho (Echo echo);

	/**
	 * Checking if we are running in interactive mode or
	 * reading input from a file.
	 *
	 * @return	true if we are running in interactive mode.
	 *			false if we are reading input from a file.
	 */
	public boolean isInteractive ();

	/**
	 * Gets the current interpreter.
	 *
	 * @return	the current interpreter.
	 */
	public JaqyInterpreter getInterpreter ();

	/**
	 * Set the current interpreter.
	 *
	 * @param	interpreter
	 *			the interpreter to be set as active
	 */
	public void setInterpreter (JaqyInterpreter interpreter);
}

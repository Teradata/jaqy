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
package com.teradata.jaqy.lineinput;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.teradata.jaqy.interfaces.LineInput;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author	Heng Yuan
 */
public class LineInputFactory
{
	public static LineInput getSimpleLineInput (InputStream is, File dir, boolean interactive) throws IOException
	{
		return new ReaderLineInput (new InputStreamReader (is), dir, interactive);
	}

	public static LineInput getLineInput (File file, String encoding, boolean interactive) throws IOException
	{
		return getLineInput (new FileInputStream (file), file.getParentFile (), encoding, interactive);
	}

	public static LineInput getLineInput (InputStream is, File dir, String encoding, boolean interactive) throws IOException
	{
		Reader reader = FileUtils.getReader (is, encoding);
		return new ReaderLineInput (reader, dir, interactive);
	}
}

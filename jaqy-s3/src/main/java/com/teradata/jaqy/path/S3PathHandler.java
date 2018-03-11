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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.interfaces.PathHandler;
import com.teradata.jaqy.s3.S3Utils;

/**
 * @author	Heng Yuan
 */
public class S3PathHandler implements PathHandler
{
	private final static Pattern s_pattern = Pattern.compile ("s3://([^/]+)/(.*)");

	@Override
	public Path getPath (String path, JaqyInterpreter interpreter) throws IOException
	{
		Matcher m = s_pattern.matcher (path);
		if (!m.find ())
			throw new IllegalArgumentException ("Invalid S3 path: " + path);
		String bucket = m.group (1);
		String file = m.group (2);
		return new S3Path (bucket, file, interpreter, S3Utils.getS3Client (interpreter));
	}

	@Override
	public boolean canHandle (String path)
	{
		return path.startsWith ("s3://");
	}
}

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;

/**
 * @author Heng Yuan
 */
public class S3Path implements Path
{
	private final String m_bucket;
	private final String m_file;
	private long m_length;
	private final AmazonS3 m_s3;
	private final JaqyInterpreter m_interpreter;

	public S3Path (String bucket, String file, JaqyInterpreter interpreter, AmazonS3 s3)
	{
		m_bucket = bucket;
		m_file = file;
		m_length = -1;
		m_interpreter = interpreter;
		m_s3 = s3;
	}

	public String getBucket ()
	{
		return m_bucket;
	}

	public String getFile ()
	{
		return m_file;
	}

	private void getLength ()
	{
		try
		{
			ObjectMetadata meta = m_s3.getObjectMetadata (m_bucket, m_file);
			m_length = meta.getContentLength ();
		}
		catch (Exception ex)
		{
			m_interpreter.getGlobals ().log (Level.INFO, ex);
			m_length = Long.MIN_VALUE;
		}
	}

	private String getParentFile (String file)
	{
		int index = file.lastIndexOf ('/');
		if (index < 0)
			return "";
		else if (index < (file.length () - 1))
			return file.substring (0, index);
		else
		{
			index = file.lastIndexOf ('/', index);
			if (index < 0)
				return "";
			else
				return file.substring (0, index);
		}
	}

	@Override
	public Path getParent ()
	{
		return new S3Path (m_bucket, getParentFile (m_file), m_interpreter, m_s3);
	}

	@Override
	public Path getRelativePath (String name)
	{
		String file;
		if (name.startsWith ("/"))
			file = name.substring (1);
		else
		{
			file = m_file;
			while (name.startsWith ("../"))
			{
				name = name.substring (3);
				file = getParentFile (file);
			}
	
			if (file.endsWith ("/"))
				file = file + name;
			else if (file.length () > 0)
				file = file + "/" + name;
			else
				file = name;
		}

		return new S3Path (m_bucket, file, m_interpreter, m_s3);
	}

	@Override
	public String getPath ()
	{
		return "s3://" + m_bucket + '/' + m_file;
	}

	@Override
	public String getCanonicalPath ()
	{
		return getPath ();
	}

	@Override
	public boolean isFile ()
	{
		return exists ();
	}

	@Override
	public long length ()
	{
		if (m_length >= 0)
			return m_length;
		if (m_length == Long.MIN_VALUE)
			return 0L;
		getLength ();
		if (m_length >= 0)
			return m_length;
		return 0L;
	}

	@Override
	public boolean exists ()
	{
		if (m_length >= 0)
			return true;
		if (m_length == Long.MIN_VALUE)
			return false;
		getLength ();
		return m_length >= 0;
	}

	@Override
	public InputStream getInputStream () throws IOException
	{
		S3Object o = m_s3.getObject (m_bucket, m_file);
		return o.getObjectContent ();
	}

	@Override
	public OutputStream getOutputStream () throws IOException
	{
		return new S3UploadStream (m_bucket, m_file, m_s3);
	}
}

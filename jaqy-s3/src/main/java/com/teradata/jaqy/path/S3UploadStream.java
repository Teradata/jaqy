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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author Heng Yuan
 */
class S3UploadStream extends OutputStream
{
	private final String m_bucket;
	private final String m_file;
	private final File m_tmpFile;
	private final FileOutputStream m_os;
	private final AmazonS3 m_s3;

	S3UploadStream (String bucket, String file, AmazonS3 s3) throws IOException
	{
		m_bucket = bucket;
		m_file = file;
		m_tmpFile = FileUtils.createTempFile ().getFile ();
		m_os = new FileOutputStream (m_tmpFile);
		m_s3 = s3;
	}

	@Override
	public void write (int b) throws IOException
	{
		m_os.write (b);
	}

	public void write (byte b[]) throws IOException
	{
		m_os.write (b);
	}

	public void write (byte b[], int off, int len) throws IOException
	{
		m_os.write (b, off, len);
	}

	public void flush () throws IOException
	{
		m_os.flush ();
	}

	public void close () throws IOException
	{
		m_os.close ();
		try
		{
			m_s3.putObject (m_bucket, m_file, m_tmpFile);
		}
		finally
		{
			m_tmpFile.delete ();
		}
	}
}

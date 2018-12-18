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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;

/**
 * @author Heng Yuan
 */
public class WasbPath implements Path
{
	private final CloudBlobContainer m_container;
	private final String m_blobName;
	private final CloudBlobClient m_blobClient;
	private final JaqyInterpreter m_interpreter;

	private CloudBlob m_blob;
	private long m_length;

	public WasbPath (CloudBlobContainer container, String blobName, JaqyInterpreter interpreter, CloudBlobClient blobCLient)
	{
		m_container = container;
		m_blobName = blobName;
		m_length = -1;
		m_interpreter = interpreter;
		m_blobClient = blobCLient;
	}

	private void getLength ()
	{
		try
		{
			if (m_blob == null)
			{
				m_blob = m_container.getBlobReferenceFromServer (m_blobName);
			}
			m_length = m_blob.getProperties ().getLength ();
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
		return new WasbPath (m_container, getParentFile (m_blobName), m_interpreter, m_blobClient);
	}

	@Override
	public Path getRelativePath (String name)
	{
		String file;
		if (name.startsWith ("/"))
			file = name.substring (1);
		else
		{
			file = getParentFile (m_blobName);
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

		return new WasbPath (m_container, file, m_interpreter, m_blobClient);
	}

	@Override
	public String getPath ()
	{
        String accountName = m_container.getServiceClient().getCredentials().getAccountName();
        boolean encrypted = m_container.getServiceClient().getDefaultRequestOptions ().requireEncryption();
        String protocol;
        if (encrypted)
        	protocol = "wasbs://";
        else
        	protocol = "wasb://";
		return protocol + m_container.getName () + '@' + accountName + '/' + m_blobName;
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
		try
		{
			if (m_blob == null)
			{
				m_blob = m_container.getBlobReferenceFromServer (m_blobName);
			}
			return m_blob.openInputStream ();
		}
		catch (Exception ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
	}

	@Override
	public OutputStream getOutputStream () throws IOException
	{
		try
		{
			m_blob = m_container.getBlockBlobReference (m_blobName);
			return ((CloudBlockBlob)m_blob).openOutputStream ();
		}
		catch (Exception ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
	}
}

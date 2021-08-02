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
package com.teradata.jaqy.azure;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.JaqyInterpreter;

/**
 * @author	Heng Yuan
 */
class AzureUtils
{
	public final static String WASB_CLIENT_VAR = "WasbClient";
	public final static String WASB_ACCOUNT_VAR = "WasbAccountName";
	public final static String WASB_CONTAINER_VAR = "WasbContainerName";
	public final static String WASB_KEY_VAR = "WasbKey";
	public final static String WASB_ENDPOINT_VAR = "WasbEndpoint";
	private final static String BLOB_SERVER = ".blob.core.windows.net";

	private final static Pattern s_pattern = Pattern.compile ("(http|https|wasb|wasbs|adl)://([^/@]+@)?([^/]+)?(/.*)?");

	public static void setAccount (String account, JaqyInterpreter interpreter)
	{
		interpreter.setVariableValue (WASB_ACCOUNT_VAR, account);

		// clear the current client
		interpreter.setVariableValue (WASB_CLIENT_VAR, null);
	}

	public static void setContainer (String container, JaqyInterpreter interpreter)
	{
		interpreter.setVariableValue (WASB_CONTAINER_VAR, container);

		// clear the current client
		interpreter.setVariableValue (WASB_CLIENT_VAR, null);
	}

	public static void setKey (String key, JaqyInterpreter interpreter)
	{
		interpreter.setVariableValue (WASB_KEY_VAR, key);

		// clear the current client
		interpreter.setVariableValue (WASB_CLIENT_VAR, null);
	}

	public static void setEndPoint (String endPoint, JaqyInterpreter interpreter)
	{
		interpreter.setVariableValue (WASB_ENDPOINT_VAR, endPoint);

		// clear the current client
		interpreter.setVariableValue (WASB_CLIENT_VAR, null);
	}

	private static String getAccountString (JaqyInterpreter interpreter, String account)
	{
		StringBuilder sb = new StringBuilder ();
		String o;
		if (account == null)
		{
			o = interpreter.getVariableString (WASB_ACCOUNT_VAR);
		}
		else
		{
			o = account;
		}
		if (o.length () > 0)
		{
			sb.append ("AccountName=").append (o.toString ());
		}
		o = interpreter.getVariableString (WASB_KEY_VAR);
		if (o.length () > 0)
		{
			if (sb.length () > 0)
			{
				sb.append (';');
			}
			sb.append ("AccountKey=").append (o.toString ());
		}
		o = interpreter.getVariableString (WASB_ENDPOINT_VAR);
		if (o.length () > 0)
		{
			if (sb.length () > 0)
			{
				sb.append (';');
			}
			sb.append ("BlobEndpoint=").append (o.toString ());
		}
		return sb.toString ();
	}

    public static CloudBlobClient getBlobClient(JaqyInterpreter interpreter, String account) throws IOException
	{
		Object o = interpreter.getVariableValue (WASB_CLIENT_VAR);
		if (o instanceof CloudBlobClient)
		{
			CloudBlobClient client = (CloudBlobClient)o;
			if (account == null)
			{
				return client;
			}
			StorageCredentials credential = client.getCredentials ();
			if (credential != null &&
				account.equals (credential.getAccountName ()))
			{
				return client;
			}
		}

		try
		{
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(getAccountString (interpreter, account));
			CloudBlobClient client = storageAccount.createCloudBlobClient();
			interpreter.setVariableValue (WASB_CLIENT_VAR, client);
			return client;
		}
		catch (Exception ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
    }

    public static AzurePathInfo getPathInfo (String path)
    {
		Matcher m = s_pattern.matcher (path);
		if (!m.find ())
			throw new IllegalArgumentException ("Invalid Azure path: " + path);
		AzurePathInfo info = new AzurePathInfo ();
		info.protocol = m.group (1);
		info.container = m.group (2);
		if (info.container != null)
		{
			// remove @ at then end
			info.container = info.container.substring (0, info.container.length () - 1);
		}
		info.account = m.group (3);
		if (info.account != null)
		{
			info.account = info.account.toLowerCase ();
			if (info.account.endsWith (BLOB_SERVER))
			{
				info.account = info.account.substring (0, info.account.length () - BLOB_SERVER.length ());
			}
		}
		info.file = m.group (4);
		if (info.file != null)
		{
			// remove leading /
			info.file = info.file.substring (1);
		}

		return info;
    }

	public static CloudBlobContainer getContainer (String account, String container, JaqyInterpreter interpreter) throws IOException
	{
		CloudBlobClient client = getBlobClient (interpreter, account);
		try
		{
			if (container == null ||
				container.length () == 0)
			{
				container = interpreter.getVariableString (WASB_CONTAINER_VAR);
			}
			return client.getContainerReference (container);
		}
		catch (Exception ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
	}

	public static void createContainer (String container, JaqyInterpreter interpreter) throws IOException
	{
		CloudBlobClient client = getBlobClient (interpreter, null);
		try
		{
			CloudBlobContainer c = client.getContainerReference (container);
			c.createIfNotExists ();
		}
		catch (Exception ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
	}

	public static void deleteContainer (String container, JaqyInterpreter interpreter) throws IOException
	{
		CloudBlobClient client = getBlobClient (interpreter, null);
		try
		{
			CloudBlobContainer c = client.getContainerReference (container);
			c.deleteIfExists ();
		}
		catch (JaqyException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new IOException (ex.getMessage (), ex);
		}
	}
}

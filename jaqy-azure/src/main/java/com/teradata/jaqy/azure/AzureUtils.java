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
package com.teradata.jaqy.azure;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;

/**
 * @author	Heng Yuan
 */
public class AzureUtils
{
	public final static String WASB_CLIENT_VAR = "WasbClient";
	public final static String WASB_ACCOUNT_VAR = "WasbAccountName";
	public final static String WASB_KEY_VAR = "WasbKey";
	public final static String WASB_ENDPOINT_VAR = "WasbEndpoint";
	private final static String BLOB_SERVER = ".blob.core.windows.net";

	private final static Pattern s_pattern = Pattern.compile ("(http|https|wasb|wasbs)://([^/@]+@)?([^/]+)?(/.*)");

	public static void setAccount (String access, JaqyInterpreter interpreter)
	{
		VariableManager vm = interpreter.getVariableManager ();
		vm.setVariable (WASB_ACCOUNT_VAR, access);

		// clear the current client
		vm.setVariable (WASB_CLIENT_VAR, null);
	}

	public static void setKey (String secret, JaqyInterpreter interpreter)
	{
		VariableManager vm = interpreter.getVariableManager ();
		vm.setVariable (WASB_KEY_VAR, secret);

		// clear the current client
		vm.setVariable (WASB_CLIENT_VAR, null);
	}

	public static void setEndPoint (String endPoint, JaqyInterpreter interpreter)
	{
		VariableManager vm = interpreter.getVariableManager ();
		vm.setVariable (WASB_ENDPOINT_VAR, endPoint);

		// clear the current client
		vm.setVariable (WASB_CLIENT_VAR, null);
	}

	private static String getAccountString (JaqyInterpreter interpreter, String account)
	{
		StringBuilder sb = new StringBuilder ();
		VariableManager vm = interpreter.getVariableManager ();
		String o;
		if (account == null)
		{
			o = vm.getVariableString (WASB_ACCOUNT_VAR);
		}
		else
		{
			o = account;
		}
		if (o.length () > 0)
		{
			sb.append ("AccountName=").append (o.toString ());
		}
		o = vm.getVariableString (WASB_KEY_VAR);
		if (o.length () > 0)
		{
			if (sb.length () > 0)
			{
				sb.append (';');
			}
			sb.append ("AccountKey=").append (o.toString ());
		}
		o = vm.getVariableString (WASB_ENDPOINT_VAR);
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
		VariableManager vm = interpreter.getVariableManager ();
		Object o = vm.get (WASB_CLIENT_VAR);
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
			vm.setVariable (WASB_CLIENT_VAR, client);
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
}

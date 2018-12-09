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
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;

/**
 * @author	Heng Yuan
 */
public class AzureUtils
{
	public final static String AZURECLIENT_VAR = "AzureClient";
	public final static String AZUREACCOUNTNAME_VAR = "AzureAccountName";
	public final static String AZUREKEY_VAR = "AzureKey";

	private final static Pattern s_pattern = Pattern.compile ("(http|https|wasb|wasbs|adl)://([^/@]+@)?([^/]+)?(/.*)");

	public static void setName (String access, JaqyInterpreter interpreter)
	{
		VariableManager vm = interpreter.getVariableManager ();
		vm.setVariable (AZUREACCOUNTNAME_VAR, access);

		// clear the current client
		vm.setVariable (AZURECLIENT_VAR, null);
	}

	public static void setKey (String secret, JaqyInterpreter interpreter)
	{
		VariableManager vm = interpreter.getVariableManager ();
		vm.setVariable (AZUREKEY_VAR, secret);

		// clear the current client
		vm.setVariable (AZURECLIENT_VAR, null);
	}

	private static String getAccountString (JaqyInterpreter interpreter, String account)
	{
		StringBuilder sb = new StringBuilder ();
		VariableManager vm = interpreter.getVariableManager ();
		Object o;
		if (account == null)
		{
			o = vm.getVariableString (AZUREACCOUNTNAME_VAR);
		}
		else
		{
			o = account;
		}
		if (o != null)
		{
			sb.append ("AccountName=").append (o.toString ());
		}
		o = vm.getVariableString (AZUREKEY_VAR);
		if (o != null)
		{
			if (sb.length () > 0)
			{
				sb.append (';');
			}
			sb.append ("AccountKey=").append (o.toString ());
		}
		return sb.toString ();
	}

    public static CloudBlobClient getBlobClient(JaqyInterpreter interpreter, String account) throws IOException
	{
		VariableManager vm = interpreter.getVariableManager ();
		Object o = vm.getVariableString (AZURECLIENT_VAR);
		if (o instanceof CloudBlobClient)
		{
			return (CloudBlobClient)o;
		}

		try
		{
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(getAccountString (interpreter, account));
	        CloudBlobClient client = storageAccount.createCloudBlobClient();
	        vm.setVariable (AZURECLIENT_VAR, client);
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
		info.file = m.group (4);
		if (info.file != null)
		{
			// remove leading /
			info.file = info.file.substring (1);
		}

		return info;
    }
}

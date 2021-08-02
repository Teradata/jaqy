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

import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.interfaces.PathHandler;

/**
 * @author	Heng Yuan
 */
class AzurePathHandler implements PathHandler
{
	@Override
	public Path getPath (String path, JaqyInterpreter interpreter) throws IOException
	{
		AzurePathInfo info = AzureUtils.getPathInfo (path);

		if ("wasb".equals (info.protocol) ||
			"wasbs".equals (info.protocol))
		{
			CloudBlobContainer container = AzureUtils.getContainer (info.account, info.container, interpreter);
			try
			{
				if (!container.exists ())
				{
					throw new IOException ("Invalid Azure path: " + path);
				}
			}
			catch (IOException ex)
			{
				throw ex;
			}
			catch (Exception ex)
			{
				throw new IOException (ex.getMessage (), ex);
			}
			return new WasbPath (container, info.file, interpreter);
		}
		throw new IOException ("Invalid Azure path: " + path);
	}

	@Override
	public boolean canHandle (String path)
	{
		if (path.startsWith ("wasb://") ||
		    path.startsWith ("wasbs://"))
			return true;
		return false;
	}
}

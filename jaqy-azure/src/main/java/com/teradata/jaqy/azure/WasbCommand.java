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

import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.command.JaqyCommandAdapter;

/**
 * @author  Heng Yuan
 */
class WasbCommand extends JaqyCommandAdapter
{
    public WasbCommand ()
    {
        super ("wasb", "wasb.txt");
    }

    @Override
    public String getDescription ()
    {
        return "configures Windows Azure Blob Storage client.";
    }

    @Override
    public CommandArgumentType getArgumentType ()
    {
        return CommandArgumentType.file;
    }

    @Override
    public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws Exception
    {
        if (args.length == 0)
            interpreter.error ("missing type.");
        String type = args[0];
        String setting;
        if (args.length == 1)
            setting = "";
        else
            setting = args[1];
        if ("key".equals (type))
        {
            AzureUtils.setKey (setting, interpreter);
        }
        else if ("account".equals (type))
        {
            AzureUtils.setAccount (setting, interpreter);
        }
        else if ("container".equals (type))
        {
            AzureUtils.setContainer (setting, interpreter);
        }
        else if ("endpoint".equals (type))
        {
            AzureUtils.setEndPoint (setting, interpreter);
        }
        else if ("create".equals (type))
        {
            if (args.length < 2)
            {
                interpreter.error (ErrorMessage.CONTAINER_NAME_MISSING);
            }
            AzureUtils.createContainer (args[1], interpreter);
        }
        else if ("delete".equals (type))
        {
            if (args.length < 2)
            {
                interpreter.error (ErrorMessage.CONTAINER_NAME_MISSING);
            }
            AzureUtils.deleteContainer (args[1], interpreter);
        }
        else if ("list".equals (type))
        {
            if (args.length < 2)
            {
                interpreter.error (ErrorMessage.CONTAINER_NAME_MISSING);
            }
            CloudBlobClient client = AzureUtils.getBlobClient (interpreter, null);
            CloudBlobContainer container = client.getContainerReference (args[1]);
            if (!container.exists ())
            {
                interpreter.error (ErrorMessage.CONTAINER_NOT_EXIST);
            }
            Iterable<ListBlobItem> items = container.listBlobs ();
            PropertyTable pt = new PropertyTable (new String[]{ "URIs" });
            String prefix = container.getUri ().toString ();
            int prefixLen = prefix.length () + 1;
            for (ListBlobItem item : items)
            {
                String n = item.getUri ().toString ().substring (prefixLen);
                pt.addRow (new String[]{ n });
            }
            interpreter.print (pt);
        }
        else if ("remove".equals (type))
        {
            if (args.length < 2)
            {
                interpreter.error (ErrorMessage.BLOB_NAME_MISSING);
            }
            AzurePathInfo info = AzureUtils.getPathInfo (args[1]);
            if (!("wasb".equals (info.protocol) ||
                  "wasbs".equals (info.protocol)))
            {
                interpreter.error (ErrorMessage.PROTOCOL_INVALID);
            }
            CloudBlobContainer container = AzureUtils.getContainer(info.account, info.container, interpreter);
            if (!container.exists ())
            {
                interpreter.error (ErrorMessage.CONTAINER_NOT_EXIST);
            }
            if (info.file == null ||
                info.file.length () == 0)
            {
                interpreter.error (ErrorMessage.BLOB_INVALID_NAME);
            }
            //
            // We use getBlockBlobReference call here since it does not check
            // if the blob's type
            //
            CloudBlob blob = container.getBlockBlobReference (info.file);
            if (!blob.exists ())
            {
                interpreter.error (ErrorMessage.BLOB_NOT_EXIST);
            }
            blob.delete ();
        }
        else
        {
            interpreter.error ("unknown type.");
        }
    }
}

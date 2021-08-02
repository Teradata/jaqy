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

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Path;

/**
 * @author  Heng Yuan
 */
public class WasbPathTest
{
    @Test
    public void testPath () throws Exception
    {
        Globals globals = new Globals (null, null);
        JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
        AzurePathHandler handler = new AzurePathHandler ();
        AzureUtils.setAccount ("devstoreaccount1", interpreter);
        AzureUtils.setKey ("Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==", interpreter);
        AzureUtils.setEndPoint ("http://127.0.0.1:10000/devstoreaccount1", interpreter);

        AzureUtils.createContainer ("testcontainer", interpreter);

        Path path = handler.getPath ("wasb://testcontainer@/abc/test.txt", interpreter);
        Assert.assertNotNull (path);
        Assert.assertEquals ("wasb://testcontainer@devstoreaccount1/abc/test.txt", path.getPath ());

        path = handler.getPath ("wasbs://testcontainer@/abc/test.txt", interpreter);
        Assert.assertNotNull (path);

        Path parent = path.getParent ();
        Assert.assertNotNull (parent);
        Assert.assertFalse (parent.exists ());
        Assert.assertEquals ("wasb://testcontainer@devstoreaccount1/abc", parent.getPath ());

        Path relative = path.getRelativePath ("def.txt");
        Assert.assertEquals (0L, relative.length ());
        Assert.assertFalse (relative.exists ());
        Assert.assertEquals ("wasb://testcontainer@devstoreaccount1/abc/def.txt", relative.getPath ());
        Assert.assertEquals ("wasb://testcontainer@devstoreaccount1/abc/def.txt", relative.getCanonicalPath ());

        AzureUtils.deleteContainer ("testcontainer", interpreter);
    }
}

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
package com.teradata.jaqy;

import java.io.IOException;

import com.teradata.jaqy.helper.PostgresHelperFactory;
import com.teradata.jaqy.helper.TeradataHelperFactory;

/**
 * @author  Heng Yuan
 */
class HelperSetup
{
    public static void init (Globals globals) throws IOException
    {
        HelperManager manager = globals.getHelperManager ();
        manager.addHelperFactory ("postgresql", new PostgresHelperFactory ());
        manager.addHelperFactory ("teradata", new TeradataHelperFactory ());
//      HelperConfigUtils.load (manager, DefaultHelperFactory.class.getResourceAsStream ("mysql.json"));
//      HelperConfigUtils.load (manager, DefaultHelperFactory.class.getResourceAsStream ("sqlite.json"));
    }
}

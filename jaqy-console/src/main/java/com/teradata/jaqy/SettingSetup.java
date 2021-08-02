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

import com.teradata.jaqy.interfaces.JaqySetting;
import com.teradata.jaqy.setting.*;

/**
 * Initiate the initial settings.
 *
 * @author  Heng Yuan
 */
class SettingSetup
{
    static void init (Globals globals)
    {
        ObjectManager<JaqySetting> manager = globals.getSettingManager ();
        manager.addObject (new BatchSizeSetting ());
        manager.addObject (new FetchSizeSetting ());
        manager.addObject (new AutoCommitSetting ());

        manager.addObject (new NullSortSetting ());
        manager.addObject (new ExpansionSetting ());
        manager.addObject (new EchoSetting ());
        manager.addObject (new LobCacheSizeSetting ());
    }
}

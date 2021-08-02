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
package com.teradata.jaqy.utils;

import com.teradata.jaqy.ConsoleDisplay;
import com.teradata.jaqy.Os;

/**
 * @author  Heng Yuan
 */
public class TitleUtils
{
    public static void showTitle (String title, ConsoleDisplay display, Os os)
    {
        // Use ANSI Escape sequence to set the title.
        System.out.print (display.getEscape ().title (title));
        System.out.flush ();
    }
}

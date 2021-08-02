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
package com.teradata.jaqy.importer;

import javax.json.stream.JsonParser;

import org.yuanheng.cookjson.CookJsonParser;

/**
 * @author  Heng Yuan
 */
interface JsonEventVisitor
{
    /**
     * Handle a particular event.
     * 
     * @param   e
     *          event to be handled.
     * @param   p
     *          JsonParser which can be used to retrieve the current value.
     * @param   depth
     *          Current parse tree depth relative to this visitor.
     */
    public void visit (JsonParser.Event e, CookJsonParser p, int depth);
}

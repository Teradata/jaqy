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
package com.teradata.jaqy.exporter;

import org.apache.commons.cli.CommandLine;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.utils.JaqyHandlerFactoryImpl;

/**
 * @author  Heng Yuan
 */
public class PipeExporterFactory extends JaqyHandlerFactoryImpl<JaqyExporter>
{
    public PipeExporterFactory ()
    {
    }

    @Override
    public String getName ()
    {
        return "pipe";
    }

    @Override
    public JaqyExporter getHandler (CommandLine cmdLine, JaqyInterpreter interpreter) throws Exception
    {
        return new PipeExporter ();
    }
}

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
package com.teradata.jaqy.s3;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.command.S3Command;
import com.teradata.jaqy.interfaces.JaqyPlugin;
import com.teradata.jaqy.path.S3PathHandler;

/**
 * @author	Heng Yuan
 */
public class S3Plugin implements JaqyPlugin
{
	@Override
	public void init (Globals globals)
	{
		globals.getCommandManager ().addObject (new S3Command ());
		globals.getPathHandlerManager ().addPathHandler (new S3PathHandler ());
	}
}

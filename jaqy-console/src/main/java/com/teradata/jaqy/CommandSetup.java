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

import com.teradata.jaqy.command.*;
import com.teradata.jaqy.interfaces.JaqyCommand;

/**
 * Initiate the initial commands.
 *
 * @author	Heng Yuan
 */
class CommandSetup
{
	static void init (Globals globals)
	{
		ObjectManager<JaqyCommand> manager = globals.getCommandManager ();
		manager.addObject (new EndCommand ());
		manager.addObject (new IfCommand ());

		manager.addObject (new HelpCommand (manager));
		manager.addObject (new SetCommand ());

		manager.addObject (new AliasCommand ());
		manager.addObject (new UnAliasCommand ());
		// remark (i.e.g comment) command
		manager.addObject (new RemarkCommand ());
		manager.addObject (new ListCommand ());
		manager.addObject (new PwdCommand ());
		manager.addObject (new ExecCommand ());
		manager.addObject (new DescribeCommand ());
		manager.addObject (new RepeatCommand ());
		manager.addObject (new RepeatPrevCommand ());
		manager.addObject (new LimitCommand ());

		// client side ResultSet processing
		manager.addObject (new SortCommand ());
		manager.addObject (new FilterCommand ());
		manager.addObject (new ProjectCommand ());

		// session control
		manager.addObject (new SessionCommand ());
		manager.addObject (new ExitCommand ());
		manager.addObject (new OpenCommand ());
		manager.addObject (new CloseCommand ());

		manager.addObject (new CommitCommand ());
		manager.addObject (new RollbackCommand ());

		// utilities
		manager.addObject (new ProtocolCommand ());
		manager.addObject (new ClassPathCommand ());
		manager.addObject (new DriverCommand ());
		manager.addObject (new EnvCommand ());
		manager.addObject (new InfoCommand ());
		manager.addObject (new OsCommand ());
		manager.addObject (new QuietCommand ());
		manager.addObject (new RunCommand ());
		manager.addObject (new VersionCommand ());
		manager.addObject (new ConfigCommand ());
		manager.addObject (new HandlerCommand ());
		manager.addObject (new TimerCommand ());

		// I/O
		manager.addObject (new FormatCommand ());
		manager.addObject (new ExportCommand ());
		manager.addObject (new ImportCommand ());
		manager.addObject (new ImportSchemaCommand ());
		manager.addObject (new ImportTableCommand ());
		manager.addObject (new SaveCommand ());

		// plugin
		manager.addObject (new LoadCommand ());

		// debugging
		manager.addObject (new DebugCommand ());
		manager.addObject (new PrepareCommand ());

		// scripting
		manager.addObject (new ScriptCommand ());
		manager.addObject (new EvalCommand ());
	}
}

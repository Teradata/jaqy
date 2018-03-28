/*
 * Copyright (c) 2017-2018 Teradata
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
		manager.addObject ("end", new EndCommand ());
		manager.addObject ("if", new IfCommand ());

		manager.addObject ("help", new HelpCommand (manager));
		manager.addObject ("set", new SetCommand ());

		manager.addObject ("alias", new AliasCommand ());
		manager.addObject ("unalias", new UnAliasCommand ());
		// remark (i.e.g comment) command
		manager.addObject ("rem", new RemarkCommand ());
		manager.addObject ("list", new ListCommand ());
		manager.addObject ("pwd", new PwdCommand ());
		manager.addObject ("exec", new ExecCommand ());
		manager.addObject ("desc", new DescribeCommand ());
		manager.addObject ("repeat", new RepeatCommand ());
		manager.addObject ("#", new RepeatPrevCommand ());
		manager.addObject ("limit", new LimitCommand ());

		// client side ResultSet processing
		manager.addObject ("sort", new SortCommand ());
		manager.addObject ("filter", new FilterCommand ());
		manager.addObject ("project", new ProjectCommand ());

		// session control
		manager.addObject ("session", new SessionCommand ());
		manager.addObject ("exit", new ExitCommand ());
		manager.addObject ("open", new OpenCommand ());
		manager.addObject ("close", new CloseCommand ());

		manager.addObject ("autocommit", new AutoCommitCommand ());
		manager.addObject ("commit", new CommitCommand ());
		manager.addObject ("rollback", new RollbackCommand ());

		// utilities
		manager.addObject ("protocol", new ProtocolCommand ());
		manager.addObject ("classpath", new ClassPathCommand ());
		manager.addObject ("driver", new DriverCommand ());
		manager.addObject ("echo", new EchoCommand ());
		manager.addObject ("env", new EnvCommand ());
		manager.addObject ("info", new InfoCommand ());
		manager.addObject ("os", new OsCommand ());
		manager.addObject ("quiet", new QuietCommand ());
		manager.addObject ("run", new RunCommand ());
		manager.addObject ("version", new VersionCommand ());
		manager.addObject ("config", new ConfigCommand ());
		manager.addObject ("handler", new HandlerCommand ());
		manager.addObject ("timer", new TimerCommand ());

		// I/O
		manager.addObject ("format", new FormatCommand ());
		manager.addObject ("export", new ExportCommand ());
		manager.addObject ("import", new ImportCommand ());
		manager.addObject ("importschema", new ImportSchemaCommand ());
		manager.addObject ("importtable", new ImportTableCommand ());
		manager.addObject ("save", new SaveCommand ());

		// plugin
		manager.addObject ("load", new LoadCommand ());

		// debugging
		manager.addObject ("debug", new DebugCommand ());
		manager.addObject ("prepare", new PrepareCommand ());

		// scripting
		manager.addObject ("script", new ScriptCommand ());
		manager.addObject ("eval", new EvalCommand ());
	}
}

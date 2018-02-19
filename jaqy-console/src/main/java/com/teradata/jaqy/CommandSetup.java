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

/**
 * Initiate the initial commands.
 *
 * @author	Heng Yuan
 */
class CommandSetup
{
	static void init (Globals globals)
	{
		CommandManager manager = globals.getCommandManager ();
		manager.addCommand ("end", new EndCommand ());
		manager.addCommand ("if", new IfCommand ());

		manager.addCommand ("help", new HelpCommand (manager));

		manager.addCommand ("alias", new AliasCommand ());
		manager.addCommand ("unalias", new UnAliasCommand ());
		// remark (i.e.g comment) command
		manager.addCommand ("rem", new RemarkCommand ());
		manager.addCommand ("list", new ListCommand ());
		manager.addCommand ("pwd", new PwdCommand ());
		manager.addCommand ("exec", new ExecCommand ());
		manager.addCommand ("desc", new DescribeCommand ());
		manager.addCommand ("repeat", new RepeatCommand ());
		manager.addCommand ("#", new RepeatPrevCommand ());
		manager.addCommand ("sort", new SortCommand ());
		manager.addCommand ("limit", new LimitCommand ());

		// session control
		manager.addCommand ("session", new SessionCommand ());
		manager.addCommand ("exit", new ExitCommand ());
		manager.addCommand ("open", new OpenCommand ());
		manager.addCommand ("close", new CloseCommand ());

		manager.addCommand ("autocommit", new AutoCommitCommand ());
		manager.addCommand ("commit", new CommitCommand ());
		manager.addCommand ("rollback", new RollbackCommand ());

		// utilities
		manager.addCommand ("protocol", new ProtocolCommand ());
		manager.addCommand ("classpath", new ClassPathCommand ());
		manager.addCommand ("driver", new DriverCommand ());
		manager.addCommand ("echo", new EchoCommand ());
		manager.addCommand ("env", new EnvCommand ());
		manager.addCommand ("info", new InfoCommand ());
		manager.addCommand ("os", new OsCommand ());
		manager.addCommand ("quiet", new QuietCommand ());
		manager.addCommand ("run", new RunCommand ());
		manager.addCommand ("version", new VersionCommand ());
		manager.addCommand ("config", new ConfigCommand ());
		manager.addCommand ("handler", new HandlerCommand ());
		manager.addCommand ("timer", new TimerCommand ());

		// I/O
		manager.addCommand ("format", new FormatCommand ());
		manager.addCommand ("export", new ExportCommand ());
		manager.addCommand ("import", new ImportCommand ());
		manager.addCommand ("importschema", new ImportSchemaCommand ());
		manager.addCommand ("importtable", new ImportTableCommand ());
		manager.addCommand ("batchsize", new BatchSizeCommand ());
		manager.addCommand ("fetchsize", new FetchSizeCommand ());

		// plugin
		manager.addCommand ("load", new LoadCommand ());

		// debugging
		manager.addCommand ("debug", new DebugCommand ());
		manager.addCommand ("prepare", new PrepareCommand ());

		// scripting
		manager.addCommand ("script", new ScriptCommand ());
	}
}

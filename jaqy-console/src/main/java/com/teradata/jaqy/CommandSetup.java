/*
 * Copyright (c) 2017 Teradata
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

import com.teradata.jaqy.command.AliasCommand;
import com.teradata.jaqy.command.AutoCommitCommand;
import com.teradata.jaqy.command.BatchSizeCommand;
import com.teradata.jaqy.command.ClassPathCommand;
import com.teradata.jaqy.command.CloseCommand;
import com.teradata.jaqy.command.CommitCommand;
import com.teradata.jaqy.command.DebugCommand;
import com.teradata.jaqy.command.DescribeCommand;
import com.teradata.jaqy.command.DriverCommand;
import com.teradata.jaqy.command.EchoCommand;
import com.teradata.jaqy.command.EndCommand;
import com.teradata.jaqy.command.EnvCommand;
import com.teradata.jaqy.command.ExecCommand;
import com.teradata.jaqy.command.ExitCommand;
import com.teradata.jaqy.command.ExportCommand;
import com.teradata.jaqy.command.FormatCommand;
import com.teradata.jaqy.command.HelpCommand;
import com.teradata.jaqy.command.IfCommand;
import com.teradata.jaqy.command.ImportCommand;
import com.teradata.jaqy.command.ImportSchemaCommand;
import com.teradata.jaqy.command.ImportTableCommand;
import com.teradata.jaqy.command.InfoCommand;
import com.teradata.jaqy.command.ListCommand;
import com.teradata.jaqy.command.LoadCommand;
import com.teradata.jaqy.command.OpenCommand;
import com.teradata.jaqy.command.OsCommand;
import com.teradata.jaqy.command.PrepareCommand;
import com.teradata.jaqy.command.ProtocolCommand;
import com.teradata.jaqy.command.PwdCommand;
import com.teradata.jaqy.command.QuietCommand;
import com.teradata.jaqy.command.RemarkCommand;
import com.teradata.jaqy.command.RepeatCommand;
import com.teradata.jaqy.command.RepeatPrevCommand;
import com.teradata.jaqy.command.RollbackCommand;
import com.teradata.jaqy.command.RunCommand;
import com.teradata.jaqy.command.ScriptCommand;
import com.teradata.jaqy.command.SessionCommand;
import com.teradata.jaqy.command.UnAliasCommand;
import com.teradata.jaqy.command.VarCommand;
import com.teradata.jaqy.command.VersionCommand;

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

		// I/O
		manager.addCommand ("format", new FormatCommand ());
		manager.addCommand ("export", new ExportCommand ());
		manager.addCommand ("import", new ImportCommand ());
		manager.addCommand ("importschema", new ImportSchemaCommand ());
		manager.addCommand ("importtable", new ImportTableCommand ());
		manager.addCommand ("batchsize", new BatchSizeCommand ());

		// plugin
		manager.addCommand ("load", new LoadCommand ());

		// debugging
		manager.addCommand ("debug", new DebugCommand ());
		manager.addCommand ("prepare", new PrepareCommand ());

		// scripting
		manager.addCommand ("var", new VarCommand ());
		manager.addCommand ("script", new ScriptCommand ());

//		manager.addCommand ("prompt", new PromptCommand ());
//		manager.addCommand ("title", new TitleCommand ());
	}
}

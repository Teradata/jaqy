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

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Project;
import com.teradata.jaqy.utils.exp.ExpNode;

/**
 * @author	Heng Yuan
 */
public class ExpNodeProject implements Project
{
	private ExpNode[] m_expList;
	private VariableManager m_vm;

	public ExpNodeProject (ExpNode[] expList)
	{
		m_expList = expList;
	}

	@Override
	public void bind (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		m_vm = new VariableManager (interpreter.getVariableManager (), null);
		m_vm.setVariable (ExpNodePredicate.RS_VAR, rs);
		for (ExpNode exp : m_expList)
			exp.bind (rs, m_vm, interpreter);
	}

	@Override
	public Object get (int column) throws Exception
	{
		return m_expList[column - 1].get ();
	}

	@Override
	public void close ()
	{
		m_expList = null;
	}
}

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
package com.teradata.jaqy.utils;

import com.teradata.jaqy.JaqyException;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.VariableManager;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.Predicate;
import com.teradata.jaqy.utils.exp.ExpNode;

/**
 * @author	Heng Yuan
 */
public class ExpNodePredicate implements Predicate
{
	public static String RS_VAR = "rs";
	private final ExpNode m_exp;
	private VariableManager m_vm;

	public ExpNodePredicate (ExpNode exp)
	{
		m_exp = exp;
	}

	@Override
	public void bind (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		m_vm = new VariableManager (interpreter.getVariableManager ());
		m_vm.setVariable (RS_VAR, rs);
		m_exp.bind (rs, interpreter);
	}

	@Override
	public boolean eval (JaqyResultSet rs, JaqyInterpreter interpreter)
	{
		Object o;
		try
		{
			o = m_exp.get (interpreter.getScriptEngine (), m_vm);
		}
		catch (Exception ex)
		{
			throw new JaqyException (ex);
		}
		if (o instanceof Boolean)
		{
			return ((Boolean)o).booleanValue ();
		}
		throw new JaqyException ("invalid predicate");
	}

	@Override
	public void close ()
	{
		if (m_vm != null)
		{
			m_vm.setVariable (RS_VAR, null);
			m_vm = null;
		}
	}
}

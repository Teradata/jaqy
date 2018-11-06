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
package com.teradata.jaqy.importer;

import java.util.TreeMap;

/**
 * @author	Heng Yuan
 */
class JsonExpFactory
{
	private static JsonEventVisitor combineVisitor (JsonEventVisitor v1, JsonEventVisitor v2)
	{
		if (v1 == null)
		{
			return v2;
		}
		if (v2 == null)
		{
			return v1;
		}
		if (v1 instanceof JsonObjectVisitor)
		{
			return new JsonComboVisitor ((JsonObjectVisitor)v1, v2);
		}
		if (v2 instanceof JsonObjectVisitor)
		{
			return new JsonComboVisitor ((JsonObjectVisitor)v2, v1);
		}
		throw new IllegalArgumentException ("One of the visitor must be ObjectVisitor");
	}

	private static JsonEventVisitor createVisitors (JsonEventVisitor ev, String[] exps, JsonEventVisitor v)
	{
		if (exps.length == 0)
		{
			return combineVisitor (ev, v);
		}

		JsonEventVisitor rootVisitor = ev;
		JsonObjectVisitor ov = null;

		if (ev == null)
		{
			ov = new JsonObjectVisitor ();
			rootVisitor = ov;
		}
		else if (ev instanceof JsonObjectVisitor)
		{
			ov = (JsonObjectVisitor)ev;
		}
		else if (ev instanceof JsonComboVisitor)
		{
			ov = ((JsonComboVisitor)ev).getObjectVisitor ();
		}
		else
		{
			ov = new JsonObjectVisitor ();
			ev = combineVisitor (ov, ev);
			rootVisitor = ev;
		}

		int length = exps.length;
		for (int i = 0; i < (length - 1); ++i)
		{
			String exp = exps[i];
			ev = ov.getEventVisitor (exp);

			if (ev == null)
			{
				JsonObjectVisitor newOv = new JsonObjectVisitor ();
				ov.setExp (exp, newOv);
				ov = newOv;
			}
			else if (ev instanceof JsonObjectVisitor)
			{
				ov = (JsonObjectVisitor)ev;
			}
			else if (ev instanceof JsonComboVisitor)
			{
				ov = ((JsonComboVisitor)ev).getObjectVisitor ();
			}
			else
			{
				JsonObjectVisitor newOv = new JsonObjectVisitor ();
				ev = new JsonComboVisitor (newOv, ev);
				ov.setExp (exp, ev);
				ov = newOv;
			}
		}

		{
			String exp = exps[length - 1];
			ev = ov.getEventVisitor (exp);
			if (ev == null)
			{
				ov.setExp (exp, v);
			}
			else if (ev instanceof JsonObjectVisitor)
			{
				JsonComboVisitor cv = new JsonComboVisitor ((JsonObjectVisitor)ev, v);
				ov.setExp (exp, cv);
			}
		}

		return rootVisitor;
	}

	public static JsonEventVisitor createVisitor (String rowExp, String[] colExps, JsonValueVisitor[] valueVisitors, JsonRowEndListener listener, boolean rootAsArray)
	{
		String[] exps;

		if (rowExp == null || rowExp.length () == 0)
			exps = new String[0];
		else
			exps = rowExp.split ("[.]");

		JsonEventVisitor rootVisitor;

		JsonRowVisitor rv = new JsonRowVisitor (listener, rootAsArray);
		if (exps.length == 0)
		{
			rootVisitor = rv;
		}
		else
		{
			rootVisitor = createVisitors (null, exps, rv);
		}

		TreeMap<String, Integer> expMap = new TreeMap<String, Integer> ();
		int id = 0;
		JsonEventVisitor colRootVisitor = null;
		for (int i = 0; i < colExps.length; ++i)
		{
			String colExp = colExps[i];
			Integer prevId = expMap.get (colExp);
			if (prevId != null)
			{
				valueVisitors[i] = valueVisitors[prevId];
				continue;
			}
			JsonValueVisitor vv = new JsonValueVisitor ();
			valueVisitors[id] = vv;
			expMap.put (colExp, id);
			++id;

			exps = colExp.split ("[.]");
			colRootVisitor = createVisitors (colRootVisitor, exps, vv);
		}

		rv.setColVisitor (colRootVisitor);

		return rootVisitor;
	}
}

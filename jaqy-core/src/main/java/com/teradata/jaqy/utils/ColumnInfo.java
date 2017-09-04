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
package com.teradata.jaqy.utils;

/**
 * @author	Heng Yuan
 */
public class ColumnInfo
{
	public boolean autoIncrement;
	public boolean caseSensitive;
	public boolean searchable;
	public boolean currency;
	public int nullable;
	public boolean signed;
	public int displaySize;
	public String label;
	public String name;
	public String schemaName;
	public int precision;
	public int scale;
	public String tableName;
	public String catalogName;
	public int type;
	public String typeName;
	public String className;
	public boolean readOnly;
	public boolean writable;
	public boolean definitelyWritable;

	/**
	 * For ARRAY / STRUCT types, obtain the children types.
	 */
	public ColumnInfo[] children;
}

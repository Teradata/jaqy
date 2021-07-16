/*
 * Copyright (c) 2021 Teradata
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

import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;

import com.teradata.jaqy.utils.CSVImportInfo;

/**
 * @author	Heng Yuan
 */
class CSVImporterOptions
{
	public String charset;
	public CSVFormat format;
	public HashMap<Integer, CSVImportInfo> importInfoMap;
	public boolean precise;
	public long scanThreshold;

	public CSVImporterOptions ()
	{
		charset = null;
		format = CSVFormat.DEFAULT;
		importInfoMap = new HashMap<Integer, CSVImportInfo> ();
		precise = false;
		scanThreshold = -1;	// -1 indicates internal algorithm
	}
}

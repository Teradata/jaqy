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

/**
 * @author	Heng Yuan
 */
public class JaqyException extends RuntimeException
{
	private static final long serialVersionUID = 5138632292108189946L;

	public JaqyException (String msg)
	{
		super (msg);
	}

	public JaqyException (Exception ex)
	{
		super (ex.getMessage (), ex);
	}

	public JaqyException (String msg, Exception ex)
	{
		super (msg, ex);
	}
}

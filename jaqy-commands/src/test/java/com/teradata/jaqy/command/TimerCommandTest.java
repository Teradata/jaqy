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
package com.teradata.jaqy.command;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan
 */
public class TimerCommandTest
{
	@Test
	public void testGetTimerString () throws Exception
	{
		long hour = 10;
		long mm = 20;
		long ss = 30;
		long nano = 100000;

		long ssMultiplier = 1000000000;
		long mmMultiplier = 60 * ssMultiplier;
		long hourMultiplier = 60 * mmMultiplier;

		long diff;

		hour = 10;
		mm = 20;
		ss = 30;
		nano = 1;
		diff = hour * hourMultiplier + mm * mmMultiplier + ss * ssMultiplier + nano;
		Assert.assertEquals ("10:20:30.000000001", TimerCommand.getTimerString (diff));

		hour = 8;
		mm = 20;
		ss = 30;
		nano = 1;
		diff = hour * hourMultiplier + mm * mmMultiplier + ss * ssMultiplier + nano;
		Assert.assertEquals ("08:20:30.000000001", TimerCommand.getTimerString (diff));

		hour = 8;
		mm = 2;
		ss = 30;
		nano = 1;
		diff = hour * hourMultiplier + mm * mmMultiplier + ss * ssMultiplier + nano;
		Assert.assertEquals ("08:02:30.000000001", TimerCommand.getTimerString (diff));

		hour = 8;
		mm = 2;
		ss = 01;
		nano = 1;
		diff = hour * hourMultiplier + mm * mmMultiplier + ss * ssMultiplier + nano;
		Assert.assertEquals ("08:02:01.000000001", TimerCommand.getTimerString (diff));

		hour = 8;
		mm = 2;
		ss = 01;
		nano = 123456789;
		diff = hour * hourMultiplier + mm * mmMultiplier + ss * ssMultiplier + nano;
		Assert.assertEquals ("08:02:01.123456789", TimerCommand.getTimerString (diff));
	}
}

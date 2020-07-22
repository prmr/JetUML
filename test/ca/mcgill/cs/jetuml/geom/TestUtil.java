/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.geom;

import static ca.mcgill.cs.jetuml.geom.Util.max;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestUtil
{
	@Test
	public void testMaxBottom()
	{
		assertEquals(Integer.MIN_VALUE, max(Integer.MIN_VALUE));
	}
	
	@Test
	public void testMaxTop()
	{
		assertEquals(Integer.MAX_VALUE, max(Integer.MAX_VALUE));
	}
	
	@Test
	public void testMaxSingleNegative()
	{
		assertEquals(-5, max(-5));
	}
	
	@Test
	public void testMaxSinglePositive()
	{
		assertEquals(5, max(5));
	}
	
	@Test
	public void testMaxNegativesOnly()
	{
		assertEquals(-5, max(-30, -5, -20, -13));
	}
	
	@Test
	public void testMaxPositivesOnly()
	{
		assertEquals(4000, max(20, 4000, 34, 34, 50));
	}
	
	@Test
	public void testMaxMix()
	{
		assertEquals(4000, max(-50, 20, -300, -300, 4000, 34, 4000, 34, 50));
	}
}

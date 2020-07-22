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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestDimension
{
	// Fields because immutable, no need for @Before method
	private static Dimension DIM_0 = new Dimension(0, 0);
	private static Dimension DIM_1 = new Dimension(10, 10);
	private static Dimension DIM_2 = new Dimension(5, 5);
	private static Dimension DIM_3 = new Dimension(10, 5);
	private static Dimension DIM_4 = new Dimension(5, 10);
	
	@Test
	public void testEquals_Same()
	{
		assertTrue( DIM_0.equals(DIM_0));
	}
	
	@Test
	public void testEquals_Equal()
	{
		assertTrue( DIM_0.equals(new Dimension(0,0)));
		assertTrue( DIM_1.equals(new Dimension(10,10)));
		assertTrue( DIM_2.equals(new Dimension(5,5)));
	}
	
	@Test
	public void testEquals_NotEqual()
	{
		assertFalse( DIM_0.equals(DIM_1));
		assertFalse( DIM_0.equals(DIM_2));
		assertFalse( DIM_0.equals(DIM_3));
		assertFalse( DIM_0.equals(DIM_4));
		assertFalse( DIM_1.equals(DIM_0));
	}
	
	@Test
	public void testInclude_Zero()
	{
		assertEquals(new Dimension(0,0), DIM_0.include(0, 0));
		assertEquals(new Dimension(10,10), DIM_1.include(0, 0));
		assertEquals(new Dimension(5,5), DIM_2.include(0, 0));
	}
	
	@Test
	public void testInclude_Grow()
	{
		assertEquals(new Dimension(10,10), DIM_2.include(10, 10));
		assertEquals(new Dimension(5,10), DIM_2.include(5, 10));
	}
}

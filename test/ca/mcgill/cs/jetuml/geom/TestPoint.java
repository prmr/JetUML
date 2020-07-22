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

public class TestPoint
{
	private static final Point ZERO = new Point(0,0);
	private static final Point ONE = new Point(1,1);
	private static final Point M_ONE = new Point(-1, -1);
	
	@Test
	public void testToString()
	{
		assertEquals("(x=0,y=0)", ZERO.toString());
		assertEquals("(x=-1,y=-1)", M_ONE.toString());
	}
	
	@Test
	public void testDistance()
	{
		assertEquals(0, ZERO.distance(ZERO), 0);
		assertEquals(1.4142, ZERO.distance(ONE), 0.0001);
		assertEquals(2.8284, ONE.distance(M_ONE),0.0001);
	}
	
	@Test
	public void testClone()
	{
		Point point = new Point(0,0);
		Point clone = point.clone();
		assertTrue( clone != point );
		assertEquals( point.getX(), clone.getX());
		assertEquals( point.getY(), clone.getY());
	}
	
	@Test
	public void testHashCode()
	{
		assertEquals(961, new Point(0,0).hashCode());
		assertEquals(4161, new Point(100,100).hashCode());
		assertEquals(-2239, new Point(-100,-100).hashCode());
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals()
	{
		Point point1 = new Point(0,0);
		Point point2 = new Point(0,0);
		assertTrue(point1.equals(point1));
		assertTrue(point1.equals(point2));
		assertTrue(point2.equals(point1));
		assertFalse(point1.equals(null));
		assertFalse(point1.equals(new Point(1,1)));
		assertFalse(point1.equals("Foo"));
		assertFalse(point1.equals(new Point(0,1)));
		assertFalse(point1.equals(new Point(1,0)));
	}
}

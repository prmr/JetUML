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

public class TestLine
{
	private static final Point ZERO = new Point(0,0);
	private static final Point ONE = new Point(1,1);
	private static final Line ZERO_TO_ONE = new Line(ZERO, ONE);
	
	@Test
	public void testToString()
	{
		assertEquals("[(x=0,y=0), (x=1,y=1)]", ZERO_TO_ONE.toString());
	}
	
	@Test
	public void testHashCode()
	{
		assertEquals(31745, ZERO_TO_ONE.hashCode());
	}
	
	@Test
	public void testClone()
	{
		Line clone = ZERO_TO_ONE.clone();
		assertFalse( clone == ZERO_TO_ONE);
		assertTrue( ZERO_TO_ONE.getPoint1() == clone.getPoint1() );
		assertTrue( ZERO_TO_ONE.getPoint2() == clone.getPoint2() );
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals()
	{
		assertTrue(ZERO_TO_ONE.equals(ZERO_TO_ONE));
		assertTrue(ZERO_TO_ONE.equals(ZERO_TO_ONE.clone()));
		assertFalse(ZERO_TO_ONE.equals(new Line(ONE, ZERO)));
		assertFalse(ZERO_TO_ONE.equals(null));
		assertFalse(ZERO_TO_ONE.equals("Foo"));
		assertFalse(ZERO_TO_ONE.equals(new Line(ZERO, ZERO)));
		assertFalse(ZERO_TO_ONE.equals(new Line(ONE, ONE)));
	}
	
	@Test
	public void testPositions()
	{
		assertEquals(0, ZERO_TO_ONE.getX1());
		assertEquals(0, ZERO_TO_ONE.getY1());
		assertEquals(1, ZERO_TO_ONE.getX2());
		assertEquals(1, ZERO_TO_ONE.getY2());
	}
}

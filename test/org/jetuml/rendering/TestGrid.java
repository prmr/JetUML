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
package org.jetuml.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

public class TestGrid
{
	@Test
	void testSnappedHorizontally()
	{
		assertEquals(new Point(0,2), Grid.snappedHorizontally(new Point(0,2)));
		assertEquals(new Point(0,2), Grid.snappedHorizontally(new Point(1,2)));
		assertEquals(new Point(0,2), Grid.snappedHorizontally(new Point(2,2)));
		assertEquals(new Point(0,2), Grid.snappedHorizontally(new Point(3,2)));
		assertEquals(new Point(0,2), Grid.snappedHorizontally(new Point(4,2)));
		assertEquals(new Point(10,2), Grid.snappedHorizontally(new Point(5,2)));
		assertEquals(new Point(10,2), Grid.snappedHorizontally(new Point(6,2)));
		assertEquals(new Point(10,2), Grid.snappedHorizontally(new Point(7,2)));
		assertEquals(new Point(10,2), Grid.snappedHorizontally(new Point(8,2)));
		assertEquals(new Point(10,2), Grid.snappedHorizontally(new Point(9,2)));
		assertEquals(new Point(10,2), Grid.snappedHorizontally(new Point(10,2)));
	}
	
	@Test
	void testSnappedVertically()
	{
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,8)));
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,9)));
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,10)));
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,11)));
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,12)));
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,13)));
		assertEquals(new Point(2,10), Grid.snappedVertically(new Point(2,14)));
		assertEquals(new Point(2,20), Grid.snappedVertically(new Point(2,15)));
		assertEquals(new Point(2,20), Grid.snappedVertically(new Point(2,16)));
	}
	
	@Test
	void testSnapped_ToTopLeft()
	{
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,1)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,2)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,3)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,4)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,5)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(1,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(2,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(3,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(4,0)));
		assertEquals(new Point(10,0), Grid.snapped(new Point(5,0)));
	}
	
	@Test
	void testToMultiple()
	{
		assertEquals(0,Grid.toMultiple(0));
		assertEquals(10,Grid.toMultiple(1));
		assertEquals(10,Grid.toMultiple(2));
		assertEquals(10,Grid.toMultiple(3));
		assertEquals(10,Grid.toMultiple(4));
		assertEquals(10,Grid.toMultiple(5));
		assertEquals(10,Grid.toMultiple(6));
		assertEquals(10,Grid.toMultiple(7));
		assertEquals(10,Grid.toMultiple(8));
		assertEquals(10,Grid.toMultiple(9));
		assertEquals(10,Grid.toMultiple(10));
		assertEquals(20,Grid.toMultiple(11));
		assertEquals(20,Grid.toMultiple(12));
		assertEquals(20,Grid.toMultiple(13));
		assertEquals(20,Grid.toMultiple(14));
		assertEquals(20,Grid.toMultiple(15));
		assertEquals(20,Grid.toMultiple(16));
		assertEquals(20,Grid.toMultiple(17));
		assertEquals(20,Grid.toMultiple(18));
		assertEquals(20,Grid.toMultiple(19));
		assertEquals(20,Grid.toMultiple(20));
	}
	
	@Test
	void testSnapped_ToTopRight()
	{
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,4)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,5)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,7)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,8)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,9)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,10)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(1,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(2,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(3,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(4,6)));
		assertEquals(new Point(10,10), Grid.snapped(new Point(5,6)));
	}
}

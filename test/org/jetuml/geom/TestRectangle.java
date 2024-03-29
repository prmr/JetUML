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
package org.jetuml.geom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestRectangle
{
	private static final Rectangle RECTANGLE_1 = new Rectangle(0,0,60,40);
	private static final Rectangle RECTANGLE_2 = new Rectangle(100,20,1,1);
	
	@Test
	void testToString()
	{
		assertEquals("[x=0, y=0, w=60, h=40]", RECTANGLE_1.toString());
	}
	
	@Test
	void testMaxXY()
	{
		assertEquals(60, RECTANGLE_1.getMaxX());
		assertEquals(40, RECTANGLE_1.getMaxY());
		assertEquals(101, RECTANGLE_2.getMaxX());
		assertEquals(21, RECTANGLE_2.getMaxY());
	}
	
	@Test
	void testHashCode()
	{
		assertEquals(2172821, RECTANGLE_1.hashCode());
		assertEquals(957393, RECTANGLE_2.hashCode());
	}
	
	@Test
	void tesEquals()
	{
		assertTrue(RECTANGLE_1.equals(RECTANGLE_1));
		assertFalse(RECTANGLE_1.equals(null));
		assertTrue(RECTANGLE_1.equals(new Rectangle(0,0,60,40)));
		assertFalse(RECTANGLE_1.equals(RECTANGLE_2));
	}
	
	@Test
	void testTranslated()
	{
		assertEquals(new Rectangle(10,20,60,40), RECTANGLE_1.translated(10, 20));
		assertEquals(new Rectangle(-10,-20,60,40), RECTANGLE_1.translated(-10, -20));
	}
	
	@Test
	void testContainsPoint()
	{
		assertTrue(RECTANGLE_1.contains(new Point(10,20)));
		assertTrue(RECTANGLE_1.contains(new Point(0,0)));
		assertTrue(RECTANGLE_1.contains(new Point(60,0)));
		assertTrue(RECTANGLE_1.contains(new Point(0,40)));
		assertFalse(RECTANGLE_1.contains(new Point(0,41)));
	}
	
	@Test
	void testGetCenter()
	{
		Point center = RECTANGLE_1.getCenter();
		assertEquals(30, center.getX());
		assertEquals(20, center.getY());
		center = RECTANGLE_1.translated(10, 10).getCenter();
		assertEquals(40, center.getX());
		assertEquals(30, center.getY());
		center = RECTANGLE_2.getCenter();
		assertEquals(100, center.getX());
		assertEquals(20, center.getY());
	}
	
	@Test
	void testAddPoint()
	{
		Rectangle rectangle = new Rectangle(10,10,0,0);
		rectangle = rectangle.add(new Point(20,30));
		assertEquals( new Rectangle(10,10,10,20), rectangle);
		rectangle = rectangle.add(new Point(15,15));
		assertEquals( new Rectangle(10,10,10,20), rectangle);
		rectangle = rectangle.add(new Point(100,100));
		assertEquals( new Rectangle(10,10,90,90), rectangle);
		rectangle = rectangle.add(new Point(0,0));
		assertEquals( new Rectangle(0,0,100,100), rectangle);
	}
	
	@Test
	void testAddRectangle()
	{
		Rectangle rectangle = new Rectangle(10,10,0,0);
		rectangle = rectangle.add( new Rectangle(0,0,20,20));
		assertEquals( new Rectangle(0,0,20,20), rectangle);
	}
	
	@Nested
	@DisplayName("Test method getSide(Side)")
	class TestGetSide
	{
		@Test
		@DisplayName("All sides of an empty rectangle at origin")
		void testWithEmptyRectangleAtOrigin()
		{
			Rectangle rectangle = new Rectangle(0,0,0,0);
			assertEquals(new Line(0,0,0,0), rectangle.getSide(Side.TOP));
			assertEquals(new Line(0,0,0,0), rectangle.getSide(Side.BOTTOM));
			assertEquals(new Line(0,0,0,0), rectangle.getSide(Side.RIGHT));
			assertEquals(new Line(0,0,0,0), rectangle.getSide(Side.LEFT));
		}
		
		@Test
		@DisplayName("All sides of an empty rectangle not at origin")
		void testWithEmptyRectangleNotAtOrigin()
		{
			Rectangle rectangle = new Rectangle(1,2,0,0);
			assertEquals(new Line(1,2,1,2), rectangle.getSide(Side.TOP));
			assertEquals(new Line(1,2,1,2), rectangle.getSide(Side.BOTTOM));
			assertEquals(new Line(1,2,1,2), rectangle.getSide(Side.RIGHT));
			assertEquals(new Line(1,2,1,2), rectangle.getSide(Side.LEFT));
		}
		
		@Test
		@DisplayName("For the top side")
		void testTop()
		{
			Rectangle rectangle = new Rectangle(10,10,60,40);
			assertEquals(new Line(10,10,70,10), rectangle.getSide(Side.TOP));
		}
		
		@Test
		@DisplayName("For the bottom side")
		void testBottom()
		{
			Rectangle rectangle = new Rectangle(10,10,60,40);
			assertEquals(new Line(10,50,70,50), rectangle.getSide(Side.BOTTOM));
		}
		
		@Test
		@DisplayName("For the right side")
		void testRight()
		{
			Rectangle rectangle = new Rectangle(10,10,60,40);
			assertEquals(new Line(70,10,70,50), rectangle.getSide(Side.RIGHT));
		}
		
		@Test
		@DisplayName("For the left side")
		void testLeft()
		{
			Rectangle rectangle = new Rectangle(10,10,60,40);
			assertEquals(new Line(10,10,10,50), rectangle.getSide(Side.LEFT));
		}
	}
}

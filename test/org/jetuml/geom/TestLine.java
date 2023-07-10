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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestLine
{
	private static final Point ZERO = new Point(0,0);
	private static final Point ONE = new Point(1,1);
	private static final Line ZERO_TO_ONE = new Line(ZERO, ONE);
	
	@Test
	void testToString()
	{
		assertEquals("[(x=0,y=0), (x=1,y=1)]", ZERO_TO_ONE.toString());
	}
	
	@Nested
	@DisplayName("Test instantiating new lines with the constructor that uses points")
	class TestConstructionWithPoints
	{
		@Test
		@DisplayName("Zero-length line at origin")
		void testLineAsPoint_ZeroAtOrigin()
		{
			Line line = new Line(ZERO, ZERO);
			assertEquals(0, line.getX1());
			assertEquals(0, line.getY1());
			assertEquals(0, line.getX2());
			assertEquals(0, line.getY2());	
			assertEquals(0, line.getPoint1().getX());
			assertEquals(0, line.getPoint1().getY());
			assertEquals(0, line.getPoint2().getX());
			assertEquals(0, line.getPoint2().getY());
		}
		
		@Test
		@DisplayName("Zero-length line not at origin")
		void testLineAsPoint_ZeroNotAtOrigin()
		{
			Line line = new Line(ONE, ONE);
			assertEquals(1, line.getX1());
			assertEquals(1, line.getY1());
			assertEquals(1, line.getX2());
			assertEquals(1, line.getY2());	
			assertEquals(1, line.getPoint1().getX());
			assertEquals(1, line.getPoint1().getY());
			assertEquals(1, line.getPoint2().getX());
			assertEquals(1, line.getPoint2().getY());
		}
		
		@Test
		@DisplayName("Non-Zero-length line")
		void testLineAsPoint_NonZero()
		{
			assertEquals(0, ZERO_TO_ONE.getX1());
			assertEquals(0, ZERO_TO_ONE.getY1());
			assertEquals(1, ZERO_TO_ONE.getX2());
			assertEquals(1, ZERO_TO_ONE.getY2());	
			assertEquals(0, ZERO_TO_ONE.getPoint1().getX());
			assertEquals(0, ZERO_TO_ONE.getPoint1().getY());
			assertEquals(1, ZERO_TO_ONE.getPoint2().getX());
			assertEquals(1, ZERO_TO_ONE.getPoint2().getY());
		}
	}
	
	@Nested
	@DisplayName("Test instantiating new lines with the constructor that uses coordinates")
	class TestConstructionWithCoordinates
	{
		@Test
		@DisplayName("Zero-length line at origin")
		void testLineAsPoint_ZeroAtOrigin()
		{
			Line line = new Line(0,0,0,0);
			assertEquals(0, line.getX1());
			assertEquals(0, line.getY1());
			assertEquals(0, line.getX2());
			assertEquals(0, line.getY2());	
			assertEquals(0, line.getPoint1().getX());
			assertEquals(0, line.getPoint1().getY());
			assertEquals(0, line.getPoint2().getX());
			assertEquals(0, line.getPoint2().getY());
		}
		
		@Test
		@DisplayName("Zero-length line not at origin")
		void testLineAsPoint_ZeroNotAtOrigin()
		{
			Line line = new Line(1,1,1,1);
			assertEquals(1, line.getX1());
			assertEquals(1, line.getY1());
			assertEquals(1, line.getX2());
			assertEquals(1, line.getY2());	
			assertEquals(1, line.getPoint1().getX());
			assertEquals(1, line.getPoint1().getY());
			assertEquals(1, line.getPoint2().getX());
			assertEquals(1, line.getPoint2().getY());
		}
		
		@Test
		@DisplayName("Non-Zero-length line")
		void testLineAsPoint_NonZero()
		{
			Line line = new Line(0,0,1,1);
			assertEquals(0, line.getX1());
			assertEquals(0, line.getY1());
			assertEquals(1, line.getX2());
			assertEquals(1, line.getY2());	
			assertEquals(0, line.getPoint1().getX());
			assertEquals(0, line.getPoint1().getY());
			assertEquals(1, line.getPoint2().getX());
			assertEquals(1, line.getPoint2().getY());
		}
	}
	
	@Nested
	@DisplayName("Test method spanning()")
	class TestSpanning
	{
		@Test
		@DisplayName("Zero-lenght line at origin")
		void testZeroLengthLineAtOrigin()
		{
			Rectangle spanning = new Line(0,0,0,0).spanning();
			assertEquals(0, spanning.getX());
			assertEquals(0, spanning.getY());
			assertEquals(0, spanning.getWidth());
			assertEquals(0, spanning.getHeight());
		}
		
		@Test
		@DisplayName("Zero-lenght line not at origin")
		void testZeroLengthLineNotAtOrigin()
		{
			Rectangle spanning = new Line(5,5,5,5).spanning();
			assertEquals(5, spanning.getX());
			assertEquals(5, spanning.getY());
			assertEquals(0, spanning.getWidth());
			assertEquals(0, spanning.getHeight());
		}
		
		@Test
		@DisplayName("Zero-lenght line going bottom right")
		void testZeroLengthLineGoingBottomRight()
		{
			Rectangle spanning = new Line(1,1,5,6).spanning();
			assertEquals(1, spanning.getX());
			assertEquals(1, spanning.getY());
			assertEquals(4, spanning.getWidth());
			assertEquals(5, spanning.getHeight());
		}
		
		@Test
		@DisplayName("Zero-lenght line going top left")
		void testZeroLengthLineGoingTopLeft()
		{
			Rectangle spanning = new Line(5,6,1,1).spanning();
			assertEquals(1, spanning.getX());
			assertEquals(1, spanning.getY());
			assertEquals(4, spanning.getWidth());
			assertEquals(5, spanning.getHeight());
		}
	}
	
	@Nested
	@DisplayName("Test line equality")
	class TestEquality
	{
		@Test
		@DisplayName("The same line is equal to itself")
		void testEqualsToSelf()
		{
			assertEquals(ZERO_TO_ONE, ZERO_TO_ONE);
		}
		
		@Test
		@DisplayName("A line is not equal to null")
		void testNull()
		{
			assertNotEquals(null, ZERO_TO_ONE);
			assertNotEquals(ZERO_TO_ONE, null);
		}
		
		@Test
		@DisplayName("A line is not equal to a non line object")
		void testNonLine()
		{
			assertNotEquals("Foo", ZERO_TO_ONE);
			assertNotEquals(ZERO_TO_ONE, "Foo");
		}
		
		@Test
		@DisplayName("Two distinct lines that should be equal are")
		void testEqualLines()
		{
			assertEquals(new Line(1,1,2,2), new Line(1,1,2,2));
		}
		
		@Test
		@DisplayName("Two lines that should not be equal are not")
		void testNonEqualLines()
		{
			assertNotEquals(new Line(1,1,2,2), new Line(1,1,2,3));
			assertNotEquals(new Line(2,2,3,3), new Line(1,1,3,3));
		}
		
		@Test
		@DisplayName("That two equal lines have the same hashcode")
		void testHashCode()
		{
			assertEquals(new Line(1,1,2,2).hashCode(), new Line(1,1,2,2).hashCode());
		}
	}
	
	@ParameterizedTest
	@CsvSource({"0,0","1,1", "5,10", "200,0"})
	void testDistanceBetweenPoints_SamePoint(int pX, int pY)
	{
		assertEquals(0, new Line(pX, pY, pX, pY).distanceBetweenPoints().width());
		assertEquals(0, new Line(pX, pY, pX, pY).distanceBetweenPoints().height());
	}
	
	@ParameterizedTest
	@CsvSource({"0,0, 5,10, 5,10",
		"5,10, 0,0, 5,10",
		"200,153, 100,53, 100,100"})
	void testDistanceBetweenPoints_DifferentPoint(int pX1, int pY1, int pX2, int pY2, int pWidth, int pHeight)
	{
		assertEquals(pWidth, new Line(pX1, pY1, pX2, pY2).distanceBetweenPoints().width());
		assertEquals(pHeight, new Line(pX1, pY1, pX2, pY2).distanceBetweenPoints().height());
	}
	
	@ParameterizedTest
	@CsvSource({"0,0, 0,0",
		"5,10, 20,30"})
	void testReversed(int pX1, int pY1, int pX2, int pY2)
	{
		Line line = new Line(pX1, pX2, pY2, pY2);
		Line reversed = line.reversed();
		assertEquals(line.getPoint1(), reversed.getPoint2());
		assertEquals(line.getPoint2(), reversed.getPoint1());
	}
	
	@ParameterizedTest
	@CsvSource({"0,0, 0,0, 0,0",
		"5,10, 25,30, 15,20",
		"25,30, 5,10, 15,20",
		"10,40, 40, 0, 25,20",
		"40,0, 10,40, 25,20"})
	void testCenter(int pX1, int pY1, int pX2, int pY2, int pCenterX, int pCenterY)
	{
		assertEquals(new Point(pCenterX, pCenterY), new Line(pX1, pY1, pX2, pY2).center());
	}
}

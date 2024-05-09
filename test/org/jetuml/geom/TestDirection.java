/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.geom;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestDirection
{	
	@ParameterizedTest(name = "Angle={0} deg")
	@MethodSource("angleGenerator")
	void testAllAngles(int pAngle)
	{
		int x = GeomUtils.round(sin(Math.toRadians(pAngle))*100);
		int y = -GeomUtils.round(cos(Math.toRadians(pAngle))*100);
		Direction direction = Direction.fromLine(new Point(0,0), new Point(x,y));
		assertEquals(pAngle % 360, direction.asAngle());
		assertEquals(sin(Math.toRadians(pAngle)), getX(direction), 0.000000001);
		assertEquals(-cos(Math.toRadians(pAngle)), getY(direction), 0.000000001);
	}
	
	@ParameterizedTest(name = "Angle={0} deg")
	@MethodSource("angleGeneratorMultiplesOf25")
	void testRotate(int pAngle)
	{
		Direction direction = Direction.NORTH;
		assertEquals(pAngle % 360, direction.rotatedBy(pAngle).asAngle());
	}
	
	@Test
	public void testIsCardinal()
	{
		assertTrue(Direction.NORTH.isCardinal());
		assertTrue(Direction.EAST.isCardinal());
		assertTrue(Direction.SOUTH.isCardinal());
		assertTrue(Direction.WEST.isCardinal());
		assertFalse(Direction.fromAngle(15).isCardinal());
	}
	
	@Test
	void testToString()
	{
		assertEquals( "[Direction: 0 degrees]", Direction.NORTH.toString() );
	}
	
	@Test
	void testFlyweight()
	{
		assertSame( Direction.NORTH, Direction.fromLine(new Point(0,0), new Point(0, -1)));
		assertSame( Direction.EAST, Direction.fromLine(new Point(0,0), new Point(1, 0)));
		assertSame( Direction.SOUTH, Direction.fromLine(new Point(0,0), new Point(0, 1)));
		assertSame( Direction.WEST, Direction.fromLine(new Point(0,0), new Point(-1, 0)));
	}
	
	private static IntStream angleGenerator()
	{
		return IntStream.range(0, 500);
	}
	
	private static IntStream angleGeneratorMultiplesOf25()
	{
		return IntStream.range(0, 500)
				.filter(angle -> angle % 25 == 0);
	}
	
	@ParameterizedTest
	@CsvSource({"0,5,10", 		// Quadrant 1 clockwise 
				"10,5,0", 		// Quadrant 1 counter clockwise
				"50,60,70", 	// Q2C
				"70,60,40", 	// Q2CC
				"100,101,102", 	// Q3C
				"102,101,100", 	// Q3CC
				"280,290,300", 	// Q4C
				"300,290,280", 	// Q4CC
				"5,45,80", 		// Crossing Q1Q2 C
				"80,45,5", 		// Crossing Q1Q2 CC
				"45,90,100", 	// Crossing Q2Q3 C
				"100,90,45", 	// Crossing Q2Q3 CC
				"100,180,270", 	// Crossing Q3Q4 C
				"270,180,100", 	// Crossing Q3Q4 CC
				"290,5,45", 	// Crossing Q4Q1 C
				"45,5,290",		// Crossing Q4Q1 CC
				"44,45,46",	    // Q1Q2 boundary C
				"46,45,44",	    // Q1Q2 boundary CC
				"89,90,91",	    // Q2Q3 boundary C
				"91,90,89",	    // Q2Q3 boundary CC
				"179,180,181",	// Q3Q4 boundary C
				"181,180,179",	// Q3Q4 boundary CC
				"359,0,1",	    // Q4Q1 boundary C
				"1,0,359",	    // Q4Q1 boundary CC
	})
	void testIsBetween_True(int pStart, int pTarget, int pEnd)
	{
		assertTrue(Direction.fromAngle(pTarget)
				.isBetween(Direction.fromAngle(pStart), Direction.fromAngle(pEnd)));
	}
	
	@Test
	void testIsBetween_FromOpposites()
	{
		assertFalse(Direction.fromAngle(270).isBetween(Direction.NORTH, Direction.SOUTH));
		assertFalse(Direction.fromAngle(270).isBetween(Direction.SOUTH, Direction.NORTH));
		assertFalse(Direction.fromAngle(270).isBetween(Direction.EAST, Direction.WEST));
		assertFalse(Direction.fromAngle(270).isBetween(Direction.WEST, Direction.EAST));
		for( int i =0; i < 360; i++ )
		{
			assertFalse(Direction.fromAngle((i+45)%360).isBetween(Direction.fromAngle(i), Direction.fromAngle((i+180)%360)));

		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {181,182,183,190,200,210,220,230,240,250,260,270,280,
			290,300,310,320,330,340,350,355,356,357,358,359})
	void testIsWesterly_True(int pAngle)
	{
		assertTrue(Direction.fromAngle(pAngle).isWesterly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 1,2,3,5,10,20,30,40,50,60,70,80,90,100,110,120,130,
			140,150,160,170,175,176,177,178,179,180})
	void testIsWesterly_False(int pAngle)
	{
		assertFalse(Direction.fromAngle(pAngle).isWesterly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {271,272,273,275,280,290,300,310,320,330,340,350,355,
			359,0,1,2,3,5,10,20,30,40,50,60,70,80,85,87,88,89})
	void testIsNortherly_True(int pAngle)
	{
		assertTrue(Direction.fromAngle(pAngle).isNortherly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {90, 91,92,100,110,120,130,140,150,160,170,180,190,200,
			210,220,230,240,250,260,265,268,269, 270})
	void testIsNortherly_False(int pAngle)
	{
		assertFalse(Direction.fromAngle(pAngle).isNortherly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1,2,3,5,10,20,30,40,50,60,70,80,90,100,110,120,130,
			140,150,160,17,175,178,179})
	void testIsEasterly_True(int pAngle)
	{
		assertTrue(Direction.fromAngle(pAngle).isEasterly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {180,181,190,200,210,220,230,240,250,260,270,280,290,
			300,310,320,330,340,350,355,356,358,359,0})
	void testIsEaterly_False(int pAngle)
	{
		assertFalse(Direction.fromAngle(pAngle).isEasterly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {91,92,100,110,120,130,140,150,160,170,180,190,200,
			210,220,230,240,250,260,265,266,267,268,269})
	void testIsSoutherly_True(int pAngle)
	{
		assertTrue(Direction.fromAngle(pAngle).isSoutherly());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {270,271,280,290,300,310,320,330,340,350,0,10,20,30,
			40,50,60,70,80,85,87,88,89,90})
	void testIsSoutherly_False(int pAngle)
	{
		assertFalse(Direction.fromAngle(pAngle).isSoutherly());
	}
	
	
	
	@ParameterizedTest
	@CsvSource({"0,10,5", 		// Quadrant 1 clockwise 
				"5,10,0", 		// Quadrant 1 counter clockwise
				"50,70,60", 	// Q2C
				"60,70,40", 	// Q2CC
				"100,102,101", 	// Q3C
				"101,102,100", 	// Q3CC
				"280,300,290", 	// Q4C
				"290,300,280", 	// Q4CC
				"5,80,45", 		// Crossing Q1Q2 C
				"45,80,5", 		// Crossing Q1Q2 CC
				"45,100,90", 	// Crossing Q2Q3 C
				"90,100,45", 	// Crossing Q2Q3 CC
				"100,270,180", 	// Crossing Q3Q4 C
				"180,270,100", 	// Crossing Q3Q4 CC
				"290,45,5", 	// Crossing Q4Q1 C
				"5,45,290",		// Crossing Q4Q1 CC
				"44,46,45",	    // Q1Q2 boundary C
				"45,46,44",	    // Q1Q2 boundary CC
				"89,91,90",	    // Q2Q3 boundary C
				"90,91,89",	    // Q2Q3 boundary CC
				"179,181,180",	// Q3Q4 boundary C
				"180,181,179",	// Q3Q4 boundary CC
				"359,1,0",	    // Q4Q1 boundary C
				"0,1,359",	    // Q4Q1 boundary CC
	})
	void testIsBetween_False(int pStart, int pTarget, int pEnd)
	{
		assertFalse(Direction.fromAngle(pTarget)
				.isBetween(Direction.fromAngle(pStart), Direction.fromAngle(pEnd)));
	}
	
	@Test
	void testMirrored()
	{
		assertSame(Direction.fromAngle(180), Direction.fromAngle(0).mirrored());
		assertSame(Direction.fromAngle(181), Direction.fromAngle(1).mirrored());
		assertSame(Direction.fromAngle(270), Direction.fromAngle(90).mirrored());
		assertSame(Direction.fromAngle(90), Direction.fromAngle(270).mirrored());
	}
	
	/**
	 * Gets the x-component of this direction.
	 * 
	 * @return the x-component (between -1 and 1)
	 */
	private static double getX(Direction pDirection)
	{
		return sin(Math.toRadians(pDirection.asAngle()));
	}

	/**
	 * Gets the y-component of this direction.
	 * 
	 * @return the y-component (between -1 and 1)
	 */
	private static double getY(Direction pDirection)
	{
		return -cos(toRadians(pDirection.asAngle()));
	}

}

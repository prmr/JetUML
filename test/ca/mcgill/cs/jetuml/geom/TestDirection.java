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
package ca.mcgill.cs.jetuml.geom;

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
import org.junit.jupiter.params.provider.MethodSource;

public class TestDirection
{	
	@ParameterizedTest(name = "Angle={0} deg")
	@MethodSource("angleGenerator")
	void testAllAngles(int pAngle)
	{
		int x = (int) Math.round(sin(Math.toRadians(pAngle))*100);
		int y = (int) -Math.round(cos(Math.toRadians(pAngle))*100);
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
	
	@Test
	void testIsBetween_Boundaries()
	{
		assertTrue(Direction.fromAngle(0).isBetween(Direction.fromAngle(0), Direction.fromAngle(5)));
		assertFalse(Direction.fromAngle(5).isBetween(Direction.fromAngle(0), Direction.fromAngle(5)));
	}
	
	@Test
	void testIsBetween_True()
	{
		assertTrue(Direction.fromAngle(5).isBetween(Direction.fromAngle(0), Direction.fromAngle(10)));
		assertFalse(Direction.fromAngle(25).isBetween(Direction.fromAngle(0), Direction.fromAngle(5)));
	}
	
	@Test
	void testIsBetween_False()
	{
		assertFalse(Direction.fromAngle(25).isBetween(Direction.fromAngle(0), Direction.fromAngle(5)));
		assertFalse(Direction.fromAngle(25).isBetween(Direction.fromAngle(30), Direction.fromAngle(35)));
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

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
import static org.junit.jupiter.api.Assertions.assertEquals;

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
		Direction direction = new Direction(new Point(0,0), new Point(x,y));
		assertEquals(pAngle % 360, direction.asAngle());
		assertEquals(sin(Math.toRadians(pAngle)), direction.getX(), 0.000000001);
		assertEquals(-cos(Math.toRadians(pAngle)), direction.getY(), 0.000000001);
	}
	
	@ParameterizedTest(name = "Angle={0} deg")
	@MethodSource("angleGenerator")
	void testRotate(int pAngle)
	{
		Direction direction = new Direction(0);
		assertEquals(pAngle % 360, direction.rotate(pAngle).asAngle());
	}
	
	@Test
	void testToString()
	{
		assertEquals( "[Direction: 0 degrees]", Direction.NORTH.toString() );
	}
	
	private static IntStream angleGenerator()
	{
		return IntStream.range(0, 500);
	}
}

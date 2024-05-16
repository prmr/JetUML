/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

import static org.jetuml.geom.GeomUtils.max;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestGeomUtils
{
	private static final Rectangle aRectangle = new Rectangle(0,0, 60, 40);
	private static final Rectangle aSquare = new Rectangle(0,0, 20, 20);
	
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
	
	@Test
	void testIntersectRectangle_North()
	{
		assertEquals(new Point(30,0), GeomUtils.intersectRectangle(aRectangle, Direction.NORTH));
	}
	
	@Test
	void testIntersectRectangle_East()
	{
		assertEquals(new Point(60,20), GeomUtils.intersectRectangle(aRectangle, Direction.EAST));
	}
	
	@Test
	void testIntersectRectangle_South()
	{
		assertEquals(new Point(30,40), GeomUtils.intersectRectangle(aRectangle, Direction.SOUTH));
	}
	
	@Test
	void testIntersectRectangle_West()
	{
		assertEquals(new Point(0,20), GeomUtils.intersectRectangle(aRectangle, Direction.WEST));
	}
	
	@Test
	void testIntersectRectangle_NE_to_Side()
	{
		assertEquals(new Point(60,12), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(75)));
	}
	
	@Test
	void testIntersectRectangle_SE_to_Side()
	{
		assertEquals(new Point(60,28), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(105)));
	}
	
	@Test
	void testIntersectRectangle_SE_to_Bottom()
	{
		assertEquals(new Point(35,40), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(165)));
	}
	
	@Test
	void testIntersectRectangle_SW_to_Bottom()
	{
		assertEquals(new Point(25,40), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(195)));
	}
	
	@Test
	void testIntersectRectangle_SW_to_Side()
	{
		assertEquals(new Point(0,28), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(255)));
	}
	
	@Test
	void testIntersectRectangle_NW_to_Side()
	{
		assertEquals(new Point(0,12), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(285)));
	}
	
	@Test
	void testIntersectRectangle_NW_to_Top()
	{
		assertEquals(new Point(25,0), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(345)));
	}	

	@Test
	void testIntersectRectangle_NE_to_Top()
	{
		assertEquals(new Point(35,0), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(15)));
	}
	
	@Test
	void testIntersectRectangle_NE_Corner()
	{
		assertEquals(new Point(60,0), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(56)));
	}
	
	@Test
	void testIntersectRectangle_SE_Corner()
	{
		assertEquals(new Point(60,40), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(124)));
	}
	
	@Test
	void testIntersectRectangle_SW_Corner()
	{
		assertEquals(new Point(0,40), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(236)));
	}
	
	@Test
	void testIntersectRectangle_NW_Corner()
	{
		assertEquals(new Point(0,0), GeomUtils.intersectRectangle(aRectangle, Direction.fromAngle(304)));
	}
	
	@Test
	void testIntersectCircle_North()
	{
		assertEquals(new Point(10,0), GeomUtils.intersectCircle(aSquare, Direction.NORTH));
	}
	
	@Test
	void testIntersectCircle_East()
	{
		assertEquals(new Point(20,10), GeomUtils.intersectCircle(aSquare, Direction.EAST));
	}
	
	@Test
	void tesIntersectCircle_South()
	{
		assertEquals(new Point(10,20), GeomUtils.intersectCircle(aSquare, Direction.SOUTH));
	}
	
	@Test
	void testIntersectCircle_West()
	{
		assertEquals(new Point(0,10), GeomUtils.intersectCircle(aSquare, Direction.WEST));
	}
	
	@Test
	void testIntersectCircle_NE()
	{
		assertEquals(new Point(19,7), GeomUtils.intersectCircle(aSquare, Direction.fromAngle(70)));
	}
	
	@Test
	void testIntersectCircle_SE()
	{
		assertEquals(new Point(19,13), GeomUtils.intersectCircle(aSquare, Direction.fromAngle(110)));
	}
	
	@Test
	void testIntersectCircle_SW()
	{
		assertEquals(new Point(3,17), GeomUtils.intersectCircle(aSquare, Direction.fromAngle(225)));
	}
	
	@Test
	void testIntersectCircle_NW()
	{
		assertEquals(new Point(2,4), GeomUtils.intersectCircle(aSquare, Direction.fromAngle(305)));
	}
	
	@Test
	void testIntersectEllipse_North()
	{
		assertEquals(new Point(30,0), GeomUtils.intersectEllipse(aRectangle, Direction.NORTH));
	}
	
	@Test
	void testIntersectEllipse_East()
	{
		assertEquals(new Point(60,20), GeomUtils.intersectEllipse(aRectangle, Direction.EAST));
	}
	
	@Test
	void tesIntersectEllipse_South()
	{
		assertEquals(new Point(30,40), GeomUtils.intersectEllipse(aRectangle, Direction.SOUTH));
	}
	
	@Test
	void testIntersectEllipse_West()
	{
		assertEquals(new Point(0,20), GeomUtils.intersectEllipse(aRectangle, Direction.WEST));
	}
	
	@Test
	void testIntersectEllipse_NE()
	{
		assertEquals(new Point(58,13), GeomUtils.intersectEllipse(aRectangle, Direction.fromAngle(70)));
	}
	
	@Test
	void testIntersectEllipse_SE()
	{
		assertEquals(new Point(58,27), GeomUtils.intersectEllipse(aRectangle, Direction.fromAngle(110)));
	}
	
	@Test
	void testIntersectEllipse_SW()
	{
		assertEquals(new Point(9,34), GeomUtils.intersectEllipse(aRectangle, Direction.fromAngle(225)));
	}
	
	@Test
	void testIntersectEllipse_NW()
	{
		assertEquals(new Point(5,9), GeomUtils.intersectEllipse(aRectangle, Direction.fromAngle(305)));
	}
	
	@Test
	void testIntersectRoundedRectangle_North()
	{
		assertEquals(new Point(30,0), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.NORTH));
	}
	
	@Test
	void testIntersectRoundedRectangle_East()
	{
		assertEquals(new Point(60,20), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.EAST));
	}
	
	@Test
	void testIntersectRoundedRectangle_South()
	{
		assertEquals(new Point(30,40), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.SOUTH));
	}
	
	@Test
	void testIntersectRoundedRectangle_West()
	{
		assertEquals(new Point(0,20), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.WEST));
	}
	
	@Test
	void testIntersectRoundedRectangle_NE()
	{
		assertEquals(new Point(60,9), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.fromAngle(70)));
	}
	
	@Test
	void testIntersectRoundedRectangle_SE()
	{
		assertEquals(new Point(60,31), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.fromAngle(110)));
	}
	
	@Test
	void testIntersectRoundedRectangle_SW()
	{
		assertEquals(new Point(10,40), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.fromAngle(225)));
	}
	
	@Test
	void testIntersectRoundedRectangle_NW()
	{
		assertEquals(new Point(3,3), GeomUtils.intersectRoundedRectangle(aRectangle, Direction.fromAngle(305)));
	}
	
	@Test
	void testRound_Positive_Exact()
	{
		assertEquals(5, GeomUtils.round(5.0));
	}
	
	@Test
	void testRound_Positive_Floor1()
	{
		assertEquals(5, GeomUtils.round(5.1));
	}
	
	@Test
	void testRound_Positive_Floor2()
	{
		assertEquals(5, GeomUtils.round(5.11));
	}
	
	@Test
	void testRound_Positive_FloorBoundary()
	{
		assertEquals(5, GeomUtils.round(5.4999999));
	}
	
	@Test
	void testRound_Positive_CeilingBoundary()
	{
		assertEquals(6, GeomUtils.round(5.5));
	}
	
	@Test
	void testRound_Positive_Ceiling1()
	{
		assertEquals(6, GeomUtils.round(5.6));
	}
	
	@Test
	void testRound_Positive_Ceiling2()
	{
		assertEquals(6, GeomUtils.round(5.6991));
	}
	
	@Test
	void testRound_Positive_Max()
	{
		assertEquals(Integer.MAX_VALUE, GeomUtils.round(Integer.MAX_VALUE));
	}
	
	@Test
	void testRound_Negative_Exact()
	{
		assertEquals(-5, GeomUtils.round(-5.0));
	}
	
	@Test
	void testRound_Negative_Floor1()
	{
		assertEquals(-6, GeomUtils.round(-5.6));
	}
	
	@Test
	void testRound_Negative_Floor2()
	{
		assertEquals(-6, GeomUtils.round(-5.611));
	}
	
	@Test
	void testRound_Negative_FloorBoundary()
	{
		assertEquals(-6, GeomUtils.round(-5.5000001));
	}
	
	@Test
	void testRound_Negative_CeilingBoundary()
	{
		assertEquals(-5, GeomUtils.round(-5.5));
	}
	
	@Test
	void testRound_Negative_Ceiling1()
	{
		assertEquals(-5, GeomUtils.round(-5.4));
	}
	
	@Test
	void testRound_Negative_Ceiling2()
	{
		assertEquals(-5, GeomUtils.round(-5.4991));
	}
	
	@Test
	void testRound_Negative_Min()
	{
		assertEquals(Integer.MIN_VALUE, GeomUtils.round(Integer.MIN_VALUE));
	}
	
	@Test
	void testRound_Zero()
	{
		assertEquals(0, GeomUtils.round(0));
	}
}

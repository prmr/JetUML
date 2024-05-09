/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.rendering.EdgePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the EdgePath class
 */
public class TestEdgePath 
{
	private final Point pointA = new Point(0,0);
	private final Point pointB = new Point(200,0);
	private final Point pointC = new Point(200,200);
	private final Point pointD = new Point(300,200);
	private EdgePath aEdgePath;
	
	@BeforeEach
	public void setUp()
	{
		aEdgePath = new EdgePath(pointA, pointB, pointC, pointD);
	}
	
	@Test
	public void testGetStartPoint()
	{
		assertSame(pointA, aEdgePath.getStartPoint());
	}
	
	@Test
	public void testGetEndPoint()
	{
		assertSame(pointD, aEdgePath.getEndPoint());
	}
	
	@Test
	public void testGetPointByIndex()
	{
		assertSame(pointA, aEdgePath.getPointByIndex(0));
		assertSame(pointB, aEdgePath.getPointByIndex(1));
		assertSame(pointC, aEdgePath.getPointByIndex(2));
		assertSame(pointD, aEdgePath.getPointByIndex(3));
	}
	
	
	@Test
	public void testEquals()
	{
		EdgePath samePath = new EdgePath(pointA, pointB, new Point(200, 200), pointD);
		EdgePath reversePath = new EdgePath(pointD, pointC, pointB, pointA);
		EdgePath nullEdgePath = null;
		assertTrue(samePath.equals(samePath));
		assertTrue(samePath.equals(aEdgePath));
		assertFalse(reversePath.equals(aEdgePath));
		assertFalse(samePath.equals(nullEdgePath));
		assertFalse(samePath.equals(new EdgePath(pointA, pointB, new Point(200, 200))));
	}
	
	
	
	 @Test
	 public void testSize()
	 {
		 assertTrue(aEdgePath.size() == 4);
	 }
	 
	
}
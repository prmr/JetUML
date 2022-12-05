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
package org.jetuml.rendering.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Side;
import org.junit.jupiter.api.Test;

/**
 * Test for the NodeIndex methods. 
 */
public class TestNodeIndex 
{
	private final Node aNode = new ClassNode();
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	TestNodeIndex()
	{
		aDiagram.addRootNode(aNode);
		aNode.moveTo(new Point(0, 0));
	}
	
	@Test
	void testToPoint_north()
	{
		Line nodeFace = new Line(new Point(0, 0), new Point(100, 0));
		assertEquals(new Point(30, 0), NodeIndex.MINUS_TWO.toPoint(nodeFace, Side.TOP));
		assertEquals(new Point(50, 0), NodeIndex.ZERO.toPoint(nodeFace, Side.TOP));
		assertEquals(new Point(90, 0), NodeIndex.PLUS_FOUR.toPoint(nodeFace, Side.TOP));
	}
	
	@Test
	void testToPoint_south()
	{
		Line nodeFace = new Line(new Point(0, 60), new Point(100, 60));
		assertEquals(new Point(30, 60), NodeIndex.MINUS_TWO.toPoint(nodeFace, Side.BOTTOM));
		assertEquals(new Point(50, 60), NodeIndex.ZERO.toPoint(nodeFace, Side.BOTTOM));
		assertEquals(new Point(90, 60), NodeIndex.PLUS_FOUR.toPoint(nodeFace, Side.BOTTOM));
	}
	
	@Test
	void testToPoint_west()
	{
		Line nodeFace = new Line(new Point(0, 0), new Point(0, 60));
		assertEquals(new Point(0, 10), NodeIndex.MINUS_TWO.toPoint(nodeFace, Side.LEFT));
		assertEquals(new Point(0, 30), NodeIndex.ZERO.toPoint(nodeFace, Side.LEFT));
		assertEquals(new Point(0, 40), NodeIndex.PLUS_ONE.toPoint(nodeFace, Side.LEFT));
	}
	
	@Test
	void testToPoint_east()
	{
		Line nodeFace = new Line(new Point(100, 0), new Point(100, 60));
		assertEquals(new Point(100, 10), NodeIndex.MINUS_TWO.toPoint(nodeFace, Side.RIGHT));
		assertEquals(new Point(100, 30), NodeIndex.ZERO.toPoint(nodeFace, Side.RIGHT));
		assertEquals(new Point(100, 40), NodeIndex.PLUS_ONE.toPoint(nodeFace, Side.RIGHT));
	}
	
	@Test
	void testSpaceBetweenConnectionPoints_north()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(100, 0));
		Line largerSize = new Line(new Point(0, 0), new Point(200, 0));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Side.TOP));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Side.TOP));
	}
	
	@Test
	void testSpaceBetweenConnectionPoints_south()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(100, 0));
		Line largerSize = new Line(new Point(0, 0), new Point(200, 0));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Side.BOTTOM));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Side.BOTTOM));
	}
	
	@Test
	void testSpaceBetweenConnectionPoints_east()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(0, 60));
		Line largerSize = new Line(new Point(0, 0), new Point(0, 120));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Side.RIGHT));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Side.RIGHT));
	}
	
	@Test
	void testSpaceBetweenConnectionPoints_west()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(0, 60));
		Line largerSize = new Line(new Point(0, 0), new Point(0, 120));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Side.LEFT));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Side.LEFT));
	}
	
	private static float spaceBetweenConnectionPoints(Line pNodeFace, Side pAttachmentSide)
	{
		try
		{
			Method method = NodeIndex.class.getDeclaredMethod("spaceBetweenConnectionPoints", Line.class, Side.class);
			method.setAccessible(true);
			return (float) method.invoke(null, pNodeFace, pAttachmentSide);
		}
		catch(ReflectiveOperationException e)
		{
			e.printStackTrace();
			fail();
			return -1;
		}
	}
	
}

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
package org.jetuml.viewers.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.viewers.NodeSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for the NodeIndex methods. 
 */
public class TestNodeIndex 
{
	private final Node aNode = new ClassNode();
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	
	@BeforeEach
	private void setUp()
	{
		aDiagram.addRootNode(aNode);
		aNode.moveTo(new Point(0, 0));
	}
	
	@Test
	public void testToPoint_north()
	{
		Line nodeFace = new Line(new Point(0, 0), new Point(100, 0));
		assertEquals(new Point(30, 0), NodeIndex.MINUS_TWO.toPoint(nodeFace, NodeSide.NORTH));
		assertEquals(new Point(50, 0), NodeIndex.ZERO.toPoint(nodeFace, NodeSide.NORTH));
		assertEquals(new Point(90, 0), NodeIndex.PLUS_FOUR.toPoint(nodeFace, NodeSide.NORTH));
	}
	
	@Test
	public void testToPoint_south()
	{
		Line nodeFace = new Line(new Point(0, 60), new Point(100, 60));
		assertEquals(new Point(30, 60), NodeIndex.MINUS_TWO.toPoint(nodeFace, NodeSide.SOUTH));
		assertEquals(new Point(50, 60), NodeIndex.ZERO.toPoint(nodeFace, NodeSide.SOUTH));
		assertEquals(new Point(90, 60), NodeIndex.PLUS_FOUR.toPoint(nodeFace, NodeSide.SOUTH));
	}
	
	@Test
	public void testToPoint_west()
	{
		Line nodeFace = new Line(new Point(0, 0), new Point(0, 60));
		assertEquals(new Point(0, 10), NodeIndex.MINUS_TWO.toPoint(nodeFace, NodeSide.WEST));
		assertEquals(new Point(0, 30), NodeIndex.ZERO.toPoint(nodeFace, NodeSide.WEST));
		assertEquals(new Point(0, 40), NodeIndex.PLUS_ONE.toPoint(nodeFace, NodeSide.WEST));
	}
	
	@Test
	public void testToPoint_east()
	{
		Line nodeFace = new Line(new Point(100, 0), new Point(100, 60));
		assertEquals(new Point(100, 10), NodeIndex.MINUS_TWO.toPoint(nodeFace, NodeSide.EAST));
		assertEquals(new Point(100, 30), NodeIndex.ZERO.toPoint(nodeFace, NodeSide.EAST));
		assertEquals(new Point(100, 40), NodeIndex.PLUS_ONE.toPoint(nodeFace, NodeSide.EAST));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_north()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(100, 0));
		Line largerSize = new Line(new Point(0, 0), new Point(200, 0));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, NodeSide.NORTH));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, NodeSide.NORTH));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_south()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(100, 0));
		Line largerSize = new Line(new Point(0, 0), new Point(200, 0));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, NodeSide.SOUTH));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, NodeSide.SOUTH));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_east()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(0, 60));
		Line largerSize = new Line(new Point(0, 0), new Point(0, 120));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, NodeSide.EAST));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, NodeSide.EAST));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_west()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(0, 60));
		Line largerSize = new Line(new Point(0, 0), new Point(0, 120));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, NodeSide.WEST));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, NodeSide.WEST));
	}
	
	private static float spaceBetweenConnectionPoints(Line pNodeFace, NodeSide pAttachmentSide)
	{
		try
		{
			Method method = NodeIndex.class.getDeclaredMethod("spaceBetweenConnectionPoints", Line.class, NodeSide.class);
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

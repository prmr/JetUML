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
import org.jetuml.rendering.Side;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
	
	@ParameterizedTest
	@ValueSource(ints = {60,70,80,90,95,100,110,180,190})
	void testSpaceBetweenConnectionPoints_Horizontal_noExpansion(int pWidth)
	{
		assertEquals(10, spaceBetweenConnectionPoints(new Line(0, 10, pWidth, 10), Side.TOP));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {200,250,270,280,285})
	void testSpaceBetweenConnectionPoints_Horizontal_withExpansion(int pWidth)
	{
		assertEquals(20, spaceBetweenConnectionPoints(new Line(0, 10, pWidth, 10), Side.TOP));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {60,100,115,116,117})
	void testSpaceBetweenConnectionPoints_Vertical_noExpansion(int pHeight)
	{
		assertEquals(10, spaceBetweenConnectionPoints(new Line(10, 0, 10, pHeight), Side.RIGHT));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {118,150,165,166,167})
	void testSpaceBetweenConnectionPoints_Vertical_withExpansion(int pHeight)
	{
		assertEquals(20, spaceBetweenConnectionPoints(new Line(10, 0, 10, pHeight), Side.RIGHT));
	}
	
	private static float spaceBetweenConnectionPoints(Line pNodeFace, Side pAttachmentSide)
	{
		try
		{
			Method method = NodeIndex.class.getDeclaredMethod("spaceBetweenConnectionPoints", Line.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(null, pNodeFace, pAttachmentSide);
		}
		catch(ReflectiveOperationException e)
		{
			e.printStackTrace();
			fail();
			return -1;
		}
	}
	
}

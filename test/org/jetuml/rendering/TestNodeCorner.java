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
package org.jetuml.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Direction;
import org.jetuml.rendering.edges.NodeIndex;
import org.junit.jupiter.api.Test;

/**
 * Tests for the enumerated type NodeCorner
 */
public class TestNodeCorner 
{
	private final Node aNode = new ClassNode();
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	TestNodeCorner()
	{
		aDiagram.addRootNode(aNode);
	}

	@Test
	void testGetHorizontalIndex_right()
	{
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.TOP_RIGHT), NodeIndex.PLUS_THREE);
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.BOTTOM_RIGHT), NodeIndex.PLUS_THREE);
	}
	
	@Test
	void testGetHorizontalIndex_left()
	{
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.TOP_LEFT), NodeIndex.MINUS_THREE);
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.BOTTOM_LEFT), NodeIndex.MINUS_THREE);
	}
	
	@Test
	void testGetVerticalIndex_top()
	{
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.TOP_LEFT), NodeIndex.MINUS_ONE);
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.TOP_RIGHT), NodeIndex.MINUS_ONE);
	}
	
	@Test
	void testGetVerticalIndex_bottom()
	{
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.BOTTOM_LEFT), NodeIndex.PLUS_ONE);
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.BOTTOM_RIGHT), NodeIndex.PLUS_ONE);
	}
	
	@Test
	void testHorizontalSide_top()
	{
		assertEquals(NodeCorner.horizontalSide(NodeCorner.TOP_LEFT), Direction.NORTH);
		assertEquals(NodeCorner.horizontalSide(NodeCorner.TOP_RIGHT), Direction.NORTH);
	}
	
	@Test
	void testHorizontalSide_bottom()
	{
		assertEquals(NodeCorner.horizontalSide(NodeCorner.BOTTOM_LEFT), Direction.SOUTH);
		assertEquals(NodeCorner.horizontalSide(NodeCorner.BOTTOM_RIGHT), Direction.SOUTH);
	}
	
	@Test
	void testGetVerticalSide_right()
	{
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_RIGHT), Direction.EAST);
		assertEquals(NodeCorner.verticalSide(NodeCorner.BOTTOM_RIGHT), Direction.EAST);
	}
	
	@Test
	void testGetVerticalSide_left()
	{
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_LEFT), Direction.WEST);
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_LEFT), Direction.WEST);
	}
}
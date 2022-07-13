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
package org.jetuml.viewers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.rendering.ClassDiagramRenderer;
import org.jetuml.rendering.NodeCorner;
import org.jetuml.viewers.edges.NodeIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the enumerated type NodeCorner
 */
public class TestNodeCorner 
{
	private static final Node aNode = new ClassNode();
	private static final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	@BeforeEach
	private void setUpNode()
	{
		aDiagram.addRootNode(aNode);
	}

	@Test
	public void testGetHorizontalIndex_right()
	{
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.TOP_RIGHT), NodeIndex.PLUS_THREE);
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.BOTTOM_RIGHT), NodeIndex.PLUS_THREE);
	}
	
	@Test
	public void testGetHorizontalIndex_left()
	{
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.TOP_LEFT), NodeIndex.MINUS_THREE);
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.BOTTOM_LEFT), NodeIndex.MINUS_THREE);
	}
	
	@Test
	public void testGetVerticalIndex_top()
	{
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.TOP_LEFT), NodeIndex.MINUS_ONE);
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.TOP_RIGHT), NodeIndex.MINUS_ONE);
	}
	
	@Test
	public void testGetVerticalIndex_bottom()
	{
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.BOTTOM_LEFT), NodeIndex.PLUS_ONE);
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.BOTTOM_RIGHT), NodeIndex.PLUS_ONE);
	}
	
	@Test
	public void testHorizontalSide_top()
	{
		assertEquals(NodeCorner.horizontalSide(NodeCorner.TOP_LEFT), Direction.NORTH);
		assertEquals(NodeCorner.horizontalSide(NodeCorner.TOP_RIGHT), Direction.NORTH);
	}
	
	@Test
	public void testHorizontalSide_bottom()
	{
		assertEquals(NodeCorner.horizontalSide(NodeCorner.BOTTOM_LEFT), Direction.SOUTH);
		assertEquals(NodeCorner.horizontalSide(NodeCorner.BOTTOM_RIGHT), Direction.SOUTH);
	}
	
	@Test
	public void testGetVerticalSide_right()
	{
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_RIGHT), Direction.EAST);
		assertEquals(NodeCorner.verticalSide(NodeCorner.BOTTOM_RIGHT), Direction.EAST);
	}
	
	@Test
	public void testGetVerticalSide_left()
	{
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_LEFT), Direction.WEST);
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_LEFT), Direction.WEST);
	}
	
	// TODO Migrate these tests to TestClassDiagramRenderer
	
	@Test
	public void testToPoints_topRight()
	{
		setUpNode();
		ClassDiagramRenderer renderer = new ClassDiagramRenderer(aDiagram);
		assertEquals(new Point(80, 0), renderer.toPoints(NodeCorner.TOP_RIGHT, aNode)[0]);
		assertEquals(new Point(100, 20), renderer.toPoints(NodeCorner.TOP_RIGHT, aNode)[1]);
	}
	
	@Test
	public void testToPoints_bottomRight()
	{
		setUpNode();
		ClassDiagramRenderer renderer = new ClassDiagramRenderer(aDiagram);
		assertEquals(new Point(80, 60), renderer.toPoints(NodeCorner.BOTTOM_RIGHT, aNode)[0]);
		assertEquals(new Point(100, 40), renderer.toPoints(NodeCorner.BOTTOM_RIGHT, aNode)[1]);
	}
	
	@Test
	public void testToPoints_topLeft()
	{
		setUpNode();
		ClassDiagramRenderer renderer = new ClassDiagramRenderer(aDiagram);
		assertEquals(new Point(20, 0), renderer.toPoints(NodeCorner.TOP_LEFT, aNode)[0]);
		assertEquals(new Point(0, 20), renderer.toPoints(NodeCorner.TOP_LEFT, aNode)[1]);
	}
	
	@Test
	public void testToPoints_bottomLeft()
	{
		setUpNode();
		ClassDiagramRenderer renderer = new ClassDiagramRenderer(aDiagram);
		assertEquals(new Point(20, 60), renderer.toPoints(NodeCorner.BOTTOM_LEFT, aNode)[0]);
		assertEquals(new Point(0, 40), renderer.toPoints(NodeCorner.BOTTOM_LEFT, aNode)[1]);
	}
}
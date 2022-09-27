/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package org.jetuml.rendering.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestNoteNodeViewer
{
	private NoteNode aNode; 
	private final NoteNodeRenderer aViewer = new NoteNodeRenderer(DiagramType.newRendererInstanceFor(new Diagram(DiagramType.CLASS)));
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aNode = new NoteNode();
	}
	
	/*
	 * For the testGetConnectionPoint methods, the note node has the geometry:
	 * [x=0,y=0,w=60, h=40]
	 */
	
	@Test
	void testGetConnectionPoint_North()
	{
		assertEquals(new Point(30,0), aViewer.getConnectionPoint(aNode, Direction.NORTH));
	}
	
	@Test
	void testGetConnectionPoint_East()
	{
		assertEquals(new Point(60,20), aViewer.getConnectionPoint(aNode, Direction.EAST));
	}
	
	@Test
	void testGetConnectionPoint_South()
	{
		assertEquals(new Point(30,40), aViewer.getConnectionPoint(aNode, Direction.SOUTH));
	}
	
	@Test
	void testGetConnectionPoint_West()
	{
		assertEquals(new Point(0,20), aViewer.getConnectionPoint(aNode, Direction.WEST));
	}
	
	@Test
	void testGetConnectionPoint_NE_to_Side()
	{
		assertEquals(new Point(60,12), aViewer.getConnectionPoint(aNode, Direction.fromAngle(75)));
	}
	
	@Test
	void testGetConnectionPoint_SE_to_Side()
	{
		assertEquals(new Point(60,28), aViewer.getConnectionPoint(aNode, Direction.fromAngle(105)));
	}
	
	@Test
	void testGetConnectionPoint_SE_to_Bottom()
	{
		assertEquals(new Point(35,40), aViewer.getConnectionPoint(aNode, Direction.fromAngle(165)));
	}
	
	@Test
	void testGetConnectionPoint_SW_to_Bottom()
	{
		assertEquals(new Point(25,40), aViewer.getConnectionPoint(aNode, Direction.fromAngle(195)));
	}
	
	@Test
	void testGetConnectionPoint_SW_to_Side()
	{
		assertEquals(new Point(0,28), aViewer.getConnectionPoint(aNode, Direction.fromAngle(255)));
	}
	
	@Test
	void testGetConnectionPoint_NW_to_Side()
	{
		assertEquals(new Point(0,12), aViewer.getConnectionPoint(aNode, Direction.fromAngle(285)));
	}
	
	@Test
	void testGetConnectionPoint_NW_to_Top()
	{
		assertEquals(new Point(25,0), aViewer.getConnectionPoint(aNode, Direction.fromAngle(345)));
	}	

	@Test
	void testGetConnectionPoint_NE_to_Top()
	{
		assertEquals(new Point(35,0), aViewer.getConnectionPoint(aNode, Direction.fromAngle(15)));
	}
}

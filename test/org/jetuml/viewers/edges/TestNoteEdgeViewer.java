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
package org.jetuml.viewers.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.RenderingFacade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestNoteEdgeViewer
{
	private NoteNode aNoteNode;
	private PointNode aPointNode;
	private NoteEdge aNoteEdge;
	private Diagram aGraph;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		// Bounds [x=0.0,y=0.0,w=60.0,h=40.0]
		aNoteNode = new NoteNode(); 
		
		// Bounds[x=100,y=20,w=0.0,h=0.0]
		aPointNode = new PointNode(); 
		aPointNode.translate(100, 20);
		aNoteEdge = new NoteEdge();
		
		aGraph = new Diagram(DiagramType.CLASS);
	}
	
	@Test
	public void testBasicConnection()
	{
		aNoteEdge.connect(aNoteNode, aPointNode, aGraph);
		assertTrue( aNoteEdge.getStart() == aNoteNode );
		assertTrue( aNoteEdge.getEnd() == aPointNode );
		aNoteEdge.connect(aPointNode, aNoteNode, aGraph);
		assertTrue( aNoteEdge.getStart() == aPointNode );
		assertTrue( aNoteEdge.getEnd() == aNoteNode );
	}
	
	@Test
	public void testBoundsCalculation()
	{
		aNoteEdge.connect(aNoteNode, aPointNode, aGraph);
		assertEquals(new Rectangle(59,19,42,2), RenderingFacade.getBounds(aNoteEdge));
		
		Line connectionPoints = RenderingFacade.getConnectionPoints(aNoteEdge);
		assertEquals( 60, connectionPoints.getX1());
		assertEquals( 20, connectionPoints.getY1());
		assertEquals( 100, connectionPoints.getX2());
		assertEquals( 20, connectionPoints.getY2());
		
		
		aPointNode.translate(20, 0);
		assertEquals(new Rectangle(59,19,62,2), RenderingFacade.getBounds(aNoteEdge));
		
		connectionPoints = RenderingFacade.getConnectionPoints(aNoteEdge);
		assertEquals( 60, connectionPoints.getX1());
		assertEquals( 20, connectionPoints.getY1());
		assertEquals( 120, connectionPoints.getX2());
		assertEquals( 20, connectionPoints.getY2());
		
		
		aPointNode.translate(0, 20); // Now at x=120, y = 40
		
		// The edge should intersect the note edge at x=58, y=24
		// (basic correspondence of proportions between triangles)
		// yielding bounds of [x=58,y=24,width=62,height=16]
		assertEquals(new Rectangle(58,25,62,15), RenderingFacade.getBounds(aNoteEdge));
		
		connectionPoints = RenderingFacade.getConnectionPoints(aNoteEdge);
		assertEquals( 60, connectionPoints.getX1());
		assertEquals( 27, connectionPoints.getY1());
		assertEquals( 120, connectionPoints.getX2());
		assertEquals( 40, connectionPoints.getY2());
	}
}

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
package ca.mcgill.cs.jetuml.diagram.edges;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;

public class TestNoteEdge
{
	private NoteNode aNoteNode;
	private PointNode aPointNode;
	private NoteEdge aNoteEdge;
	private Diagram aGraph;
	
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
	public void testClone()
	{
		aNoteEdge.connect(aNoteNode, aPointNode, aGraph);
		NoteEdge clonedEdge = (NoteEdge) aNoteEdge.clone();
		
		// Test that the start and end nodes are the same object
		// (shallow cloning)
		assertTrue( (NoteNode) clonedEdge.getStart() == aNoteNode );
		assertTrue( (PointNode) clonedEdge.getEnd() == aPointNode );
	}
}

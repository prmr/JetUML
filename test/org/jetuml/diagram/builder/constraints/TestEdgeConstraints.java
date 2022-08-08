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

package org.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestEdgeConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private DiagramRenderer aRenderer = DiagramType.newRendererInstanceFor(aDiagram);
	private ClassNode aNode1 = new ClassNode();
	private ClassNode aNode2 = new ClassNode();
	private PointNode aPointNode = new PointNode();
	private DependencyEdge aEdge1 = new DependencyEdge();
	private NoteEdge aNoteEdge = new NoteEdge();
	private NoteNode aNote = new NoteNode();
	private Point aPoint = new Point(0,0);
	
	private void createDiagram()
	{
		aNode2.moveTo(new Point(0,100));
		aNote.moveTo(new Point(100,100));
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		aDiagram.addRootNode(aNote);
		aPointNode.moveTo(new Point(200,200));
		aDiagram.addRootNode(aPointNode);
	}
	
	@Test
	void testNoteEdgeNotNoteEdge()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge().satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteEdgeNodeNotePoint()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNote, aPointNode, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteEdgeNodeNoteNotPoint()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNote, aNode1, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteEdgeNodeNoteNotePoint()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNode1, aPointNode, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteEdgeNodeAnyNode()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNode1, aNote, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteNodeAnyAny()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteNode().satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteNodeNoteAny()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode().satisfied(aEdge1, aNote, aNode2, aPoint, aPoint, aRenderer));
		assertTrue(EdgeConstraints.noteNode().satisfied(aNoteEdge, aNote, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteNodeAnyNote()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode().satisfied(aEdge1, aNode1, aNote,  aPoint, aPoint, aRenderer));
		assertTrue(EdgeConstraints.noteNode().satisfied(aNoteEdge, aNode1, aNote, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoteNodeNoteNote()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode().satisfied(aEdge1, aNote, aNote, aPoint, aPoint, aRenderer));
		assertTrue(EdgeConstraints.noteNode().satisfied(aNoteEdge, aNote, aNote, aPoint, aPoint, aRenderer));
	}
	
	@Test 
	void testMaxEdgesOne()
	{
		createDiagram();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		assertFalse(EdgeConstraints.maxEdges(1).satisfied(new DependencyEdge(), aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test 
	void testMaxEdgesTwo()
	{
		createDiagram();
		assertTrue(EdgeConstraints.maxEdges(2).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		assertTrue(EdgeConstraints.maxEdges(2).satisfied(new DependencyEdge(), aNode1, aNode2, aPoint, aPoint, aRenderer));
		DependencyEdge edge = aEdge1;
		edge.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(edge);
		assertFalse(EdgeConstraints.maxEdges(2).satisfied(new DependencyEdge(), aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test 
	void testMaxEdgesNodesMatchNoMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(new DependencyEdge(), aNode1, node3, aPoint, aPoint, aRenderer));
	}
	
	@Test 
	void testMaxEdgesNodesNoMatchMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(aEdge1, node3, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test 
	void testMaxEdgesNodesNoMatchNoMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(aEdge1, node3, new ClassNode(), aPoint, aPoint, aRenderer));
	}
	
	@Test 
	void testMaxEdgesNodesDifferentEdgeType()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(new NoteEdge(), aNode1, aNode2, aPoint, aPoint, aRenderer ));
	}
	
	@Test
	void testNodeSelfEdgeTrue()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noSelfEdge().satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNodeSelfEdgeFalse()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noSelfEdge().satisfied(aEdge1, aNode1, aNode1, aPoint, aPoint, aRenderer));
	}
}

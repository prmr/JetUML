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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestEdgeConstraints
{
	private Diagram aDiagram;
	private ClassNode aNode1;
	private ClassNode aNode2;
	private PointNode aPointNode;
	private DependencyEdge aEdge1;
	private NoteEdge aNoteEdge;
	private NoteNode aNote;
	private Point aPoint;
	
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.CLASS);
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aNote = new NoteNode();
		aPointNode = new PointNode();
		aEdge1 = new DependencyEdge();
		aNoteEdge = new NoteEdge();
		aPoint = new Point(0,0);
	}
	
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
	public void testNoteEdgeNotNoteEdge()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge().satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteEdgeNodeNotePoint()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNote, aPointNode, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteEdgeNodeNoteNotPoint()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNote, aNode1, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteEdgeNodeNoteNotePoint()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNode1, aPointNode, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteEdgeNodeAnyNode()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge().satisfied(aNoteEdge, aNode1, aNote, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteNodeAnyAny()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteNode().satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteNodeNoteAny()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode().satisfied(aEdge1, aNote, aNode2, aPoint, aPoint, aDiagram));
		assertTrue(EdgeConstraints.noteNode().satisfied(aNoteEdge, aNote, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteNodeAnyNote()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode().satisfied(aEdge1, aNode1, aNote,  aPoint, aPoint, aDiagram));
		assertTrue(EdgeConstraints.noteNode().satisfied(aNoteEdge, aNode1, aNote, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoteNodeNoteNote()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode().satisfied(aEdge1, aNote, aNote, aPoint, aPoint, aDiagram));
		assertTrue(EdgeConstraints.noteNode().satisfied(aNoteEdge, aNote, aNote, aPoint, aPoint, aDiagram));
	}
	
	@Test 
	public void testMaxEdgesOne()
	{
		createDiagram();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		assertFalse(EdgeConstraints.maxEdges(1).satisfied(new DependencyEdge(), aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test 
	public void testMaxEdgesTwo()
	{
		createDiagram();
		assertTrue(EdgeConstraints.maxEdges(2).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		assertTrue(EdgeConstraints.maxEdges(2).satisfied(new DependencyEdge(), aNode1, aNode2, aPoint, aPoint, aDiagram));
		DependencyEdge edge = aEdge1;
		edge.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(edge);
		assertFalse(EdgeConstraints.maxEdges(2).satisfied(new DependencyEdge(), aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test 
	public void testMaxEdgesNodesMatchNoMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(new DependencyEdge(), aNode1, node3, aPoint, aPoint, aDiagram));
	}
	
	@Test 
	public void testMaxEdgesNodesNoMatchMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(aEdge1, node3, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test 
	public void testMaxEdgesNodesNoMatchNoMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(aEdge1, node3, new ClassNode(), aPoint, aPoint, aDiagram));
	}
	
	@Test 
	public void testMaxEdgesNodesDifferentEdgeType()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge1);
		assertTrue(EdgeConstraints.maxEdges(1).satisfied(new NoteEdge(), aNode1, aNode2, aPoint, aPoint, aDiagram ));
	}
	
	@Test
	public void testNodeSelfEdgeTrue()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noSelfEdge().satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNodeSelfEdgeFalse()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noSelfEdge().satisfied(aEdge1, aNode1, aNode1, aPoint, aPoint, aDiagram));
	}
}

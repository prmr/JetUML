/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2021 by McGill University.
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
package ca.mcgill.cs.jetuml.views;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.SequenceDiagramViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

public class TestSequenceDiagramViewer
{
	private SequenceDiagramViewer aViewer = new SequenceDiagramViewer();
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	
	@Test
	void testNodeAt_NoneShallow()
	{
		aDiagram.addRootNode(new ImplicitParameterNode());
		assertTrue(aViewer.nodeAt(aDiagram, new Point(100,100)).isEmpty());
	}
	
	@Test 
	void testNodeAt_Note()
	{
		NoteNode note = new NoteNode();
		note.translate(50, 50);
		aDiagram.addRootNode(note);
		assertSame(note, aViewer.nodeAt(aDiagram, new Point(55,55)).get());
	}
	
	@Test 
	void testNodeAt_SingleImplicitParameterNode()
	{
		ImplicitParameterNode node = new ImplicitParameterNode();
		node.translate(50, 0);
		aDiagram.addRootNode(node);
		// Inside top rectangle
		assertSame(node, aViewer.nodeAt(aDiagram, new Point(60,10)).get());
		// Below top rectangle
		assertSame(node, aViewer.nodeAt(aDiagram, new Point(60,100)).get());
	}
	
	/*
	 * When two implicit parameter nodes overlap, we find the last one in the list
	 */
	@Test 
	void testNodeAt_OverlappingImplicitParameterNode()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		node2.translate(20, 0);
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		// Inside top rectangle
		assertSame(node1, aViewer.nodeAt(aDiagram, new Point(10,10)).get());
		assertSame(node2, aViewer.nodeAt(aDiagram, new Point(25,10)).get());
		assertSame(node2, aViewer.nodeAt(aDiagram, new Point(65,10)).get());
	}
	
	@Test 
	void testNodeAt_CallNode()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		node2.translate(100, 0);
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		
		CallNode callNode1 = new CallNode();
		CallNode callNode2 = new CallNode();
		callNode1.attach(aDiagram);
		callNode2.attach(aDiagram);
		node1.addChild(callNode1);
		node2.addChild(callNode2);
		CallEdge edge = new CallEdge();
		edge.connect(callNode1, callNode2, aDiagram);
		aDiagram.addEdge(edge);
		
		assertSame(callNode1, aViewer.nodeAt(aDiagram, new Point(35,85)).get());
		assertSame(callNode2, aViewer.nodeAt(aDiagram, new Point(135,105)).get());
	}
	
	@Test 
	void testNodeAt_SelfCallNode()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		aDiagram.addRootNode(node1);
		
		CallNode callNode1 = new CallNode();
		CallNode callNode2 = new CallNode();
		callNode1.attach(aDiagram);
		callNode2.attach(aDiagram);
		node1.addChild(callNode1);
		node1.addChild(callNode2);
		CallEdge edge = new CallEdge();
		edge.connect(callNode1, callNode2, aDiagram);
		aDiagram.addEdge(edge);
		
		System.out.println(NodeViewerRegistry.getBounds(callNode1));
		System.out.println(NodeViewerRegistry.getBounds(callNode2));

		assertSame(callNode1, aViewer.nodeAt(aDiagram, new Point(38,105)).get());
		assertSame(callNode2, aViewer.nodeAt(aDiagram, new Point(42,105)).get());
	}
}

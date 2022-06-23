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
package org.jetuml.viewers;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.RenderingFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSequenceDiagramViewer
{
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	
	@BeforeEach
	void setup()
	{
		RenderingFacade.prepareFor(aDiagram);
	}
	
	@Test
	void testNodeAt_NoneShallow()
	{
		aDiagram.addRootNode(new ImplicitParameterNode());
		assertTrue(RenderingFacade.nodeAt(aDiagram, new Point(100,100)).isEmpty());
	}
	
	@Test 
	void testNodeAt_Note()
	{
		NoteNode note = new NoteNode();
		note.translate(50, 50);
		aDiagram.addRootNode(note);
		assertSame(note, RenderingFacade.nodeAt(aDiagram, new Point(55,55)).get());
	}
	
	@Test 
	void testNodeAt_SingleImplicitParameterNode()
	{
		ImplicitParameterNode node = new ImplicitParameterNode();
		node.translate(50, 0);
		aDiagram.addRootNode(node);
		// Inside top rectangle
		assertSame(node, RenderingFacade.nodeAt(aDiagram, new Point(60,10)).get());
		// Below top rectangle
		assertSame(node, RenderingFacade.nodeAt(aDiagram, new Point(60,100)).get());
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
		assertSame(node1, RenderingFacade.nodeAt(aDiagram, new Point(10,10)).get());
		assertSame(node2, RenderingFacade.nodeAt(aDiagram, new Point(25,10)).get());
		assertSame(node2, RenderingFacade.nodeAt(aDiagram, new Point(65,10)).get());
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
		
		assertSame(callNode1, RenderingFacade.nodeAt(aDiagram, new Point(35,85)).get());
		assertSame(callNode2, RenderingFacade.nodeAt(aDiagram, new Point(135,105)).get());
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

		assertSame(callNode1, RenderingFacade.nodeAt(aDiagram, new Point(38,105)).get());
		assertSame(callNode2, RenderingFacade.nodeAt(aDiagram, new Point(42,105)).get());
	}
}

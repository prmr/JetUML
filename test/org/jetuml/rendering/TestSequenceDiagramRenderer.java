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
package org.jetuml.rendering;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

public class TestSequenceDiagramRenderer
{
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	private DiagramRenderer aRenderer = new SequenceDiagramRenderer(aDiagram);
	
	@Test
	void testNodeAt_NoneShallow()
	{
		aDiagram.addRootNode(new ImplicitParameterNode());
		triggerRenderingPass();
		assertTrue(aRenderer.nodeAt(new Point(100,100)).isEmpty());
	}
	
	@Test 
	void testNodeAt_Note()
	{
		NoteNode note = new NoteNode();
		note.translate(50, 50);
		aDiagram.addRootNode(note);
		triggerRenderingPass();
		assertSame(note, aRenderer.nodeAt(new Point(55,55)).get());
	}
	
	@Test 
	void testNodeAt_SingleImplicitParameterNode()
	{
		ImplicitParameterNode node = new ImplicitParameterNode();
		node.translate(50, 0);
		aDiagram.addRootNode(node);
		triggerRenderingPass();
		// Inside top rectangle
		assertSame(node, aRenderer.nodeAt(new Point(60,10)).get());
		// Below top rectangle
		assertSame(node, aRenderer.nodeAt(new Point(60,100)).get());
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
		triggerRenderingPass();
		
		// Inside top rectangle
		assertSame(node1, aRenderer.nodeAt(new Point(10,10)).get());
		assertSame(node2, aRenderer.nodeAt(new Point(25,10)).get());
		assertSame(node2, aRenderer.nodeAt(new Point(65,10)).get());
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
		
		triggerRenderingPass();
		
		assertSame(callNode1, aRenderer.nodeAt(new Point(35,85)).get());
		assertSame(callNode2, aRenderer.nodeAt(new Point(135,105)).get());
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
		
		triggerRenderingPass();

		assertSame(callNode1, aRenderer.nodeAt(new Point(38,105)).get());
		assertSame(callNode2, aRenderer.nodeAt(new Point(42,105)).get());
	}
	
	@Test
	void testFindRoot_Empty()
	{
		assertTrue(reflectivelyCallFindRoot().isEmpty());
	}
	
	@Test
	void testFindRoot_NoCallNode()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		aDiagram.addRootNode(node1);
		assertTrue(reflectivelyCallFindRoot().isEmpty());
	}
	
	@Test
	void testFindRoot_SingleCallNode()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		aDiagram.addRootNode(node1);
		
		CallNode callNode1 = new CallNode();
		callNode1.attach(aDiagram);
		node1.addChild(callNode1);
				
		assertSame(callNode1, reflectivelyCallFindRoot().get());
	}
	
	@Test
	void testFindRoot_TwoCallNodes()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		
		CallNode callNode1 = new CallNode();
		callNode1.attach(aDiagram);
		node1.addChild(callNode1);
		CallNode callNode2 = new CallNode();
		callNode2.attach(aDiagram);
		node2.addChild(callNode2);
		
		CallEdge edge = new CallEdge();
		edge.connect(callNode1, callNode2, aDiagram);
		aDiagram.addEdge(edge);
				
		assertSame(callNode1, reflectivelyCallFindRoot().get());
	}
	
	// Exercises bug #478
	@Test
	void testFindRoot_ThreeCallNodesWithConstructor()
	{
		ImplicitParameterNode node1 = new ImplicitParameterNode();
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		ImplicitParameterNode node3 = new ImplicitParameterNode();
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		
		CallNode callNode1 = new CallNode();
		callNode1.attach(aDiagram);
		node1.addChild(callNode1);
		CallNode callNode2 = new CallNode();
		callNode2.attach(aDiagram);
		node2.addChild(callNode2);
		CallNode callNode3 = new CallNode();
		callNode3.attach(aDiagram);
		node3.addChild(callNode3);
		
		CallEdge edge = new CallEdge();
		edge.connect(callNode1, callNode2, aDiagram);
		aDiagram.addEdge(edge);
		ConstructorEdge create = new ConstructorEdge();
		create.connect(callNode2, callNode3, aDiagram);
		aDiagram.addEdge(create);
				
		assertSame(callNode1, reflectivelyCallFindRoot().get());
	}
	
	private void triggerRenderingPass()
	{
		aRenderer.getBounds();
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Node> reflectivelyCallFindRoot()
	{
		try
		{
			Method findRootMethod = SequenceDiagramRenderer.class.getDeclaredMethod("findRoot");
			findRootMethod.setAccessible(true);
			return (Optional<Node>) findRootMethod.invoke(aRenderer);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}

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

package org.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramAccessor;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

/**
 * For testing any functionality that required bounds calculations, 
 * call aBuilder.renderer().getBounds() before the assertions
 * to ensure a layouting pass is done.
 */
public class TestSequenceDiagramBuilder
{
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	private SequenceDiagramBuilder aBuilder = new SequenceDiagramBuilder(aDiagram);
	private ImplicitParameterNode aParameterNode1 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameterNode2 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameterNode3 = new ImplicitParameterNode();
	private NoteNode aNoteNode = new NoteNode();
	private CallNode aCallNode1 = new CallNode();
	private CallNode aCallNode2 = new CallNode();
	private CallNode aCallNode3 = new CallNode();
	private CallNode aCallNode4 = new CallNode();
	private CallNode aCallNode5 = new CallNode();
	private CallEdge aCallEdge1 = new CallEdge();
	private CallEdge aCallEdge2 = new CallEdge();
	private CallEdge aCallEdge3 = new CallEdge();
	private CallEdge aCallEdge4 = new CallEdge();
	private ReturnEdge aReturnEdge = new ReturnEdge();
	private ConstructorEdge aConstructorEdge = new ConstructorEdge();
	private DiagramAccessor aDiagramAccessor = new DiagramAccessor(aDiagram);
	
	private boolean callCanCreateConstructorCall(Point pStart, Point pEnd)
	{
		try
		{
			Method method = SequenceDiagramBuilder.class.getDeclaredMethod("canCreateConstructorCall", Point.class, Point.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aBuilder, pStart, pEnd);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return false;
		}
		
	}

	private void createSampleDiagram()
	{
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		aDiagram.addRootNode(aParameterNode3);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(aCallNode2);
		aParameterNode2.addChild(aCallNode3);
		aParameterNode3.addChild(aCallNode4);
		aParameterNode3.addChild(aCallNode5);
		aDiagramAccessor.connectAndAdd(aConstructorEdge, aCallNode1, aCallNode2);
		aDiagramAccessor.connectAndAdd(aReturnEdge, aCallNode2, aCallNode1);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCallNode2, aCallNode3);
		aDiagramAccessor.connectAndAdd(aCallEdge2, aCallNode3, aCallNode4);
		aDiagramAccessor.connectAndAdd(aCallEdge3, aCallNode2, aCallNode5);
	}

	private int numberOfRootNodes()
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Node node : aDiagram.rootNodes() )
		{
			sum++;
		}
		return sum;
	}
	
	@Test
	void testCreateAddEdgeOperation_ConstructorEdge_Simplest()
	{
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		aParameterNode2.moveTo(new Point(100,0));
		aBuilder.createAddEdgeOperation(aConstructorEdge, new Point(40,60), new Point(120,20)).execute();
		assertTrue(aDiagram.edges().contains(aConstructorEdge));
		assertTrue(aParameterNode1.getChildren().size() == 1);
		CallNode caller = (CallNode)aParameterNode1.getChildren().get(0);
		assertTrue(aParameterNode2.getChildren().size() == 1);
		CallNode callee = (CallNode)aParameterNode2.getChildren().get(0);
		assertSame(caller, aConstructorEdge.start());
		assertSame(callee, aConstructorEdge.end());
	}
	
	@Test
	void testcreateAddNodeOperationOneImplicitParameterNode()
	{
		DiagramOperation operation = aBuilder.createAddNodeOperation(aParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aParameterNode1.position());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	void testcreateAddNodeOperationOneCallNode()
	{
		DiagramOperation operation = aBuilder.createAddNodeOperation(aCallNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aCallNode1.position());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	void testcreateAddNodeOperationSecondCallNode()
	{
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(aCallNode2);
		aParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		aCallEdge1.connect(aCallNode1, aCallNode2);
		aDiagram.addEdge(aCallEdge1);
		
		CallNode callNode = new CallNode();
		aBuilder.renderer().getBounds(); // Trigger rendering pass
		DiagramOperation operation = aBuilder.createAddNodeOperation(callNode, new Point(30, 135));
		operation.execute();
		assertEquals(2, numberOfRootNodes());
		assertEquals(2, aParameterNode1.getChildren().size());
		assertSame(aCallNode1, aParameterNode1.getChildren().get(0));
		assertSame(callNode, aParameterNode1.getChildren().get(1));
	}
	
	@Test
	void testGetCoRemovalsCallEdge()
	{
		aCallEdge1.connect(aCallNode1, aCallNode2);
		aDiagram.addEdge(aCallEdge1);
		aDiagram.addRootNode(aCallNode1);
		aDiagram.addRootNode(aCallNode2);
		Collection<DiagramElement> elements = new HashSet<>();
		elements.addAll(aBuilder.getCoRemovals(aCallEdge1));
		
		Collection<DiagramElement> elements2 = new HashSet<>();
		elements2.addAll(aBuilder.getCoRemovals(aCallNode1));
		
		assertEquals(elements2,elements);
		assertEquals(3, elements.size());
		assertTrue(elements.contains(aCallNode1));
		assertTrue(elements.contains(aCallNode2));
		assertTrue(elements.contains(aCallEdge1));
	}
	
	@Test
	void testGetCoRemovalsCallNode()
	{
		createSampleDiagram();
		CallNode callNode = new CallNode();
		aParameterNode2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCallNode1, callNode);
		Collection<DiagramElement> elements = new HashSet<>();
		elements.addAll(aBuilder.getCoRemovals(aCallNode2));
		
		assertEquals(13, elements.size());
		assertTrue(elements.contains(callNode));
		assertTrue(elements.contains(aParameterNode2));
		assertTrue(elements.contains(aReturnEdge));
		assertTrue(elements.contains(aConstructorEdge));
		assertTrue(elements.contains(aCallNode1));
		assertTrue(elements.contains(aCallNode2));
		assertTrue(elements.contains(aCallNode3));
		assertTrue(elements.contains(aCallNode4));
		assertTrue(elements.contains(aCallNode5));
		assertTrue(elements.contains(aCallEdge1));
		assertTrue(elements.contains(aCallEdge2));
		assertTrue(elements.contains(aCallEdge3));
		assertTrue(elements.contains(aCallEdge4));
	}
	
	@Test
	void testGetCoRemovalsParameterNode()
	{
		createSampleDiagram();
		Collection<DiagramElement> elements = new HashSet<>();
		elements.addAll(aBuilder.getCoRemovals(aParameterNode2));
		
		assertEquals(11, elements.size());
		assertTrue(elements.contains(aParameterNode2));
		assertTrue(elements.contains(aReturnEdge));
		assertTrue(elements.contains(aConstructorEdge));
		assertTrue(elements.contains(aCallNode1));
		assertTrue(elements.contains(aCallNode2));
		assertTrue(elements.contains(aCallNode3));
		assertTrue(elements.contains(aCallNode4));
		assertTrue(elements.contains(aCallNode5));
		assertTrue(elements.contains(aCallEdge1));
		assertTrue(elements.contains(aCallEdge2));
		assertTrue(elements.contains(aCallEdge3));
	}

	@Test
	void testGetCoRemovalsConstructorEdge()
	{
		aConstructorEdge.connect(aCallNode1, aCallNode2);
		aDiagram.addEdge(aConstructorEdge);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(aCallNode2);
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		Collection<DiagramElement> elements = new HashSet<>();
		elements.addAll(aBuilder.getCoRemovals(aConstructorEdge));
		
		assertEquals(3, elements.size());
		assertTrue(elements.contains(aConstructorEdge));
		assertTrue(elements.contains(aCallNode1));
		assertTrue(elements.contains(aCallNode2));
	}

	@Test
	void testGetEdgeDownStreamsNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge = new NoteEdge();
		noteEdge.connect(noteNode, aCallNode3);
		aDiagram.addRootNode(noteNode);
		aDiagram.addEdge(noteEdge);
		
		Collection<DiagramElement> downstreams1 = aBuilder.getEdgeDownStreams(noteEdge);
		Collection<DiagramElement> downstreams2 = aBuilder.getNodeDownStreams(noteNode);
		
		assertEquals(1, downstreams1.size());
		assertEquals(0, downstreams2.size());
		assertTrue(downstreams1.contains(noteEdge));
	}

	@Test
	void testGetEdgeDownStreamsCallEdge()
	{
		createSampleDiagram();
		Collection<DiagramElement> downstreams = aBuilder.getEdgeDownStreams(aCallEdge2);
		
		assertEquals(2, downstreams.size());
		assertTrue(downstreams.contains(aCallNode4));
		assertTrue(downstreams.contains(aCallEdge2));
	}

	@Test
	void testGetNodeDownStreamsWithConstructorCall()
	{
		createSampleDiagram();
		Collection<DiagramElement> downstreams1 = aBuilder.getNodeDownStreams(aCallNode2);
		Collection<DiagramElement> downstreams2 = aBuilder.getNodeDownStreams(aParameterNode2);
		
		assertEquals(downstreams1, downstreams2);
		assertEquals(9, downstreams1.size());
		assertTrue(downstreams1.contains(aParameterNode2));
		assertTrue(downstreams1.contains(aConstructorEdge));
		assertTrue(downstreams1.contains(aCallEdge3));
		assertTrue(downstreams1.contains(aCallNode2));
		assertTrue(downstreams1.contains(aCallNode3));
		assertTrue(downstreams1.contains(aCallNode4));
		assertTrue(downstreams1.contains(aCallNode5));
		assertTrue(downstreams1.contains(aCallEdge1));
		assertTrue(downstreams1.contains(aCallEdge2));
	}

	@Test
	void testGetNodeDownStreamsCallNode()
	{
		createSampleDiagram();
		Collection<DiagramElement> downstreams = aBuilder.getNodeDownStreams(aCallNode3);
		
		assertEquals(2, downstreams.size());
		assertTrue(downstreams.contains(aCallNode4));
		assertTrue(downstreams.contains(aCallEdge2));
	}
	
	@Test
	void testCanCreateConstructorCall_MissingBothNodes()
	{
		assertFalse(callCanCreateConstructorCall(new Point(0,0), new Point(20,20)));
	}
	
	@Test
	void testCanCreateConstructorCall_MissingStartNode()
	{
		aDiagram.addRootNode(aParameterNode1);
		assertFalse(callCanCreateConstructorCall(new Point(200,200), new Point(10,10)));
	}
	
	@Test
	void testCanCreateConstructorCall_MissingEndNode()
	{
		aDiagram.addRootNode(aParameterNode1);
		assertFalse(callCanCreateConstructorCall(new Point(10,10), new Point(200,200)));
	}
	
	@Test
	void testCanCreateConstructorCall_StartNodeNotValid()
	{
		aDiagram.addRootNode(aNoteNode);
		assertFalse(callCanCreateConstructorCall(new Point(10,10), new Point(10,10)));
	}
	
	@Test
	void testCanCreateConstructorCall_EndNodeNotImplicitParameter()
	{
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aNoteNode);
		aNoteNode.translate(200, 200);
		assertFalse(callCanCreateConstructorCall(new Point(10,10), new Point(205,205)));
	}
	
	@Test
	void testCanCreateConstructorCall_EndNodeNotInTopRectangle()
	{
		aDiagram.addRootNode(aParameterNode1);
		aParameterNode1.addChild(aCallNode1);
		aCallNode1.translate(10, 20);
		aDiagram.addRootNode(aParameterNode2);
		aParameterNode2.translate(100, 0);
		aParameterNode2.addChild(aCallNode2);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCallNode1, aCallNode2);
		aBuilder.renderer().getBounds();
		assertFalse(callCanCreateConstructorCall(new Point(15,25), new Point(105,80)));
	}
	
	@Test
	void testCanCreateConstructorCall_EndNodeNotEmpty()
	{
		aDiagram.addRootNode(aParameterNode1);
		aParameterNode1.addChild(aCallNode1);
		aCallNode1.translate(10, 20);
		aDiagram.addRootNode(aParameterNode2);
		aParameterNode2.translate(100, 0);
		aParameterNode2.addChild(aCallNode2);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCallNode1, aCallNode2);
		aBuilder.renderer().getBounds();
		assertFalse(callCanCreateConstructorCall(new Point(15,25), new Point(105,20)));
	}
	
	@Test
	void testCanCreateConstructorCall_EndNodeEmpty()
	{
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		aParameterNode2.translate(100, 0);
		assertTrue(callCanCreateConstructorCall(new Point(15,25), new Point(105,20)));
	}
	
	@Test
	void testObtainEdge_NotCallEdge()
	{
		Edge edge = new ReturnEdge();
		assertSame(edge, aBuilder.obtainEdge(edge, new Point(0,0), new Point(10,10)));
	}
	
	@Test
	void testObtainEdge_CannotCreateConstructorCall()
	{
		Edge edge = new CallEdge();
		assertSame(edge, aBuilder.obtainEdge(edge, new Point(0,0), new Point(200,200)));
	}
	
	@Test
	void testObtainEdge_True()
	{
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		aParameterNode2.translate(100, 0);
		assertSame(ConstructorEdge.class, aBuilder.obtainEdge(new CallEdge(), new Point(15,25), new Point(105,20)).getClass());
	}
}

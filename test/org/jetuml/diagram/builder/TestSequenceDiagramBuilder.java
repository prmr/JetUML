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

import java.util.Collection;
import java.util.HashSet;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramAccessor;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSequenceDiagramBuilder
{
	private Diagram aDiagram;
	private SequenceDiagramBuilder aBuilder;
	private ImplicitParameterNode aParameterNode1;
	private ImplicitParameterNode aParameterNode2;
	private ImplicitParameterNode aParameterNode3;
	private CallNode aCallNode1;
	private CallNode aCallNode2;
	private CallNode aCallNode3;
	private CallNode aCallNode4;
	private CallNode aCallNode5;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	private CallEdge aCallEdge3;
	private CallEdge aCallEdge4;
	private ReturnEdge aReturnEdge;
	private ConstructorEdge aConstructorEdge;
	private DiagramAccessor aDiagramAccessor;

	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.SEQUENCE);
		aBuilder = new SequenceDiagramBuilder(aDiagram);
		aParameterNode1 = new ImplicitParameterNode();
		aParameterNode2 = new ImplicitParameterNode();
		aParameterNode3 = new ImplicitParameterNode();
		aCallNode1 = new CallNode();
		aCallNode2 = new CallNode();
		aCallNode3 = new CallNode();
		aCallNode4 = new CallNode();
		aCallNode5 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
		aCallEdge3 = new CallEdge();
		aCallEdge4 = new CallEdge();
		aReturnEdge = new ReturnEdge();
		aConstructorEdge = new ConstructorEdge();
		aDiagramAccessor = new DiagramAccessor(aDiagram);
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
	
	private int numberOfEdges() 
	{
		return aDiagram.edges().size();
	}
	
	@Test
	public void testcreateAddNodeOperationOneImplicitParameterNode()
	{
		DiagramOperation operation = aBuilder.createAddNodeOperation(aParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aParameterNode1.position());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testcreateAddNodeOperationOneCallNode()
	{
		DiagramOperation operation = aBuilder.createAddNodeOperation(aCallNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aCallNode1.position());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testcreateAddNodeOperationSecondCallNode()
	{
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(aCallNode2);
		aCallNode1.attach(aDiagram);
		aCallNode2.attach(aDiagram);
		aParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);
		aCallEdge1.connect(aCallNode1, aCallNode2, aDiagram);
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
	public void testCompleteEdgeAdditionOperationWithConstructorCall()
	{
		aParameterNode1.translate(0, 70);
		aParameterNode2.translate(100, 70);
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);

		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 40);
		assertTrue(aBuilder.canAdd(aCallEdge1, startPoint, endPoint));
		assertTrue(aBuilder.canCreateConstructorCall(startPoint, endPoint));
		CompoundOperation result = new CompoundOperation();
		ConstructorEdge constructorEdge = new ConstructorEdge();
		aBuilder.completeEdgeAdditionOperation(result, constructorEdge, aParameterNode1, aParameterNode2, startPoint, endPoint);
		result.execute();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		assertSame(CallNode.class, aParameterNode1.getChildren().get(0).getClass());
		assertSame(CallNode.class, aParameterNode2.getChildren().get(0).getClass());
		assertSame(ConstructorEdge.class, aDiagram.edges().get(0).getClass());
	}
	
	@Test
	public void testCompleteEdgeAdditionOperationWithoutConstructorCall()
	{
		aParameterNode1.translate(0, 70);
		aParameterNode2.translate(100, 70);
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(aParameterNode2);

		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 90);
		assertTrue(aBuilder.canAdd(aCallEdge1, startPoint, endPoint));
		assertFalse( aBuilder.canCreateConstructorCall(startPoint, endPoint) );
		CompoundOperation result = new CompoundOperation();
		aBuilder.completeEdgeAdditionOperation(result, aCallEdge1, aParameterNode1, aParameterNode2, startPoint, endPoint);
		result.execute();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		assertEquals(CallNode.class, aParameterNode1.getChildren().get(0).getClass());
		assertEquals(CallNode.class, aParameterNode2.getChildren().get(0).getClass());
		assertSame(aDiagram.edges().get(0), aCallEdge1);
	}
	
	@Test
	public void testCompleteEdgeAdditionOperationWithNoteEdge()
	{
		NoteEdge noteEdge = new NoteEdge();
		NoteNode noteNode = new NoteNode();
		aParameterNode1.translate(0, 70);
		noteNode.translate(100, 70);
		aDiagram.addRootNode(aParameterNode1);
		aDiagram.addRootNode(noteNode);
		
		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 90);
		assertTrue(aBuilder.canAdd(noteEdge, startPoint, endPoint));
		CompoundOperation result = new CompoundOperation();
		aBuilder.completeEdgeAdditionOperation(result, noteEdge, aCallNode1, aCallNode2, startPoint, endPoint);
		result.execute();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertSame(aDiagram.edges().get(0), noteEdge);
	}
	
	@Test
	public void testCanCreateConstructorCallNoNodes()
	{
		assertFalse(aBuilder.canCreateConstructorCall(new Point(0, 0), new Point(20, 20)));
	}
	
	@Test
	public void testCanCreateConstructorCallWrongStartNode()
	{
		aDiagram.addRootNode(new NoteNode());
		assertFalse(aBuilder.canCreateConstructorCall(new Point(0, 0), new Point(20, 20)));
	}
	
	@Test
	public void testCanCreateConstructorCallNoEndNode()
	{
		aDiagram.addRootNode(aParameterNode1);
		assertFalse(aBuilder.canCreateConstructorCall(new Point(0, 0), new Point(150, 150)));
	}
	
	@Test
	public void testGetCoRemovalsCallEdge()
	{
		aCallEdge1.connect(aCallNode1, aCallNode2, aDiagram);
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
	public void testGetCoRemovalsConstructorEdge()
	{
		aConstructorEdge.connect(aCallNode1, aCallNode2, aDiagram);
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
		noteEdge.connect(noteNode, aCallNode3, aDiagram);
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
	
}

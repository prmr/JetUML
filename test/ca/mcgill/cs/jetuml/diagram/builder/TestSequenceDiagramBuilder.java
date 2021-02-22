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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ConstructorEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestSequenceDiagramBuilder
{
	private Diagram aDiagram;
	private SequenceDiagramBuilder aBuilder;
	private ImplicitParameterNode aImplicitParameterNode1;
	private ImplicitParameterNode aImplicitParameterNode2;
	private CallNode aDefaultCallNode1;
	private CallNode aDefaultCallNode2;
	private CallEdge aCallEdge1;
	
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
		aImplicitParameterNode1 = new ImplicitParameterNode();
		aImplicitParameterNode2 = new ImplicitParameterNode();
		aDefaultCallNode1 = new CallNode();
		aDefaultCallNode2 = new CallNode();
		aCallEdge1 = new CallEdge();
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
		DiagramOperation operation = aBuilder.createAddNodeOperation(aImplicitParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aImplicitParameterNode1.position());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testcreateAddNodeOperationSecondCallNode()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDefaultCallNode1.attach(aDiagram);
		aDefaultCallNode2.attach(aDiagram);
		aImplicitParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		
		CallNode callNode = new CallNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(callNode, new Point(30, 135));
		operation.execute();
		assertEquals(2, numberOfRootNodes());
		assertEquals(2, aImplicitParameterNode1.getChildren().size());
		assertSame(aDefaultCallNode1, aImplicitParameterNode1.getChildren().get(0));
		assertSame(callNode, aImplicitParameterNode1.getChildren().get(1));
	}
	 
	@Test
	public void testCompleteEdgeAdditionOperationWithConstructorCall()
	{
		aImplicitParameterNode1.translate(0, 70);
		aImplicitParameterNode2.translate(100, 70);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 40);
		assertTrue(aBuilder.canAdd(aCallEdge1, startPoint, endPoint));
		assertTrue(aBuilder.canCreateConstructorCall(startPoint, endPoint));
		CompoundOperation result = new CompoundOperation();
		ConstructorEdge constructorEdge = new ConstructorEdge();
		aBuilder.completeEdgeAdditionOperation(result, constructorEdge, aImplicitParameterNode1, aImplicitParameterNode2, startPoint, endPoint);
		result.execute();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(1, aImplicitParameterNode1.getChildren().size());
		assertEquals(1, aImplicitParameterNode2.getChildren().size());
		assertSame(CallNode.class, aImplicitParameterNode1.getChildren().get(0).getClass());
		assertSame(CallNode.class, aImplicitParameterNode2.getChildren().get(0).getClass());
		assertSame(ConstructorEdge.class, aDiagram.edges().get(0).getClass());
	}
	
	@Test
	public void testCompleteEdgeAdditionOperationWithoutConstructorCall()
	{
		aImplicitParameterNode1.translate(0, 70);
		aImplicitParameterNode2.translate(100, 70);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 90);
		assertTrue(aBuilder.canAdd(aCallEdge1, startPoint, endPoint));
		assertFalse( aBuilder.canCreateConstructorCall(startPoint, endPoint) );
		CompoundOperation result = new CompoundOperation();
		aBuilder.completeEdgeAdditionOperation(result, aCallEdge1, aImplicitParameterNode1, aImplicitParameterNode2, startPoint, endPoint);
		result.execute();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(1, aImplicitParameterNode1.getChildren().size());
		assertEquals(1, aImplicitParameterNode2.getChildren().size());
		assertEquals(CallNode.class, aImplicitParameterNode1.getChildren().get(0).getClass());
		assertEquals(CallNode.class, aImplicitParameterNode2.getChildren().get(0).getClass());
		assertSame(aDiagram.edges().get(0), aCallEdge1);
	}
	
	@Test
	public void testCompleteEdgeAdditionOperationWithCallNodes()
	{
		aImplicitParameterNode1.translate(0, 70);
		aImplicitParameterNode2.translate(100, 70);
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 70);
		assertTrue(aBuilder.canAdd(aCallEdge1, startPoint, endPoint));
		assertFalse( aBuilder.canCreateConstructorCall(startPoint, endPoint) );
		CompoundOperation result = new CompoundOperation();
		aBuilder.completeEdgeAdditionOperation(result, aCallEdge1, aDefaultCallNode1, aDefaultCallNode2, startPoint, endPoint);
		result.execute();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(1, aImplicitParameterNode1.getChildren().size());
		assertEquals(2, aImplicitParameterNode2.getChildren().size());
		assertSame(aDefaultCallNode1, aImplicitParameterNode1.getChildren().get(0));
		assertSame(aDefaultCallNode2, aImplicitParameterNode2.getChildren().get(0));
		assertSame(aDiagram.edges().get(0), aCallEdge1);
	}
	
	@Test
	public void testCompleteEdgeAdditionOperationWithNoteEdge()
	{
		NoteEdge noteEdge = new NoteEdge();
		NoteNode noteNode = new NoteNode();
		aImplicitParameterNode1.translate(0, 70);
		noteNode.translate(100, 70);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(noteNode);
		
		Point startPoint = new Point(40, 95);
		Point endPoint = new Point(120, 90);
		assertTrue(aBuilder.canAdd(noteEdge, startPoint, endPoint));
		CompoundOperation result = new CompoundOperation();
		aBuilder.completeEdgeAdditionOperation(result, noteEdge, aDefaultCallNode1, aDefaultCallNode2, startPoint, endPoint);
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
		aDiagram.addRootNode(aImplicitParameterNode1);
		assertFalse(aBuilder.canCreateConstructorCall(new Point(0, 0), new Point(150, 150)));
	}
	
	@Test
	public void testGetCoRemovals()
	{
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		aDiagram.addRootNode(aDefaultCallNode1);
		aDiagram.addRootNode(aDefaultCallNode2);
		Collection<DiagramElement> elements = new HashSet<>();
		elements.addAll(aBuilder.getCoRemovals(aCallEdge1));
		assertEquals(3, elements.size());
		assertTrue(elements.contains(aDefaultCallNode1));
		assertTrue(elements.contains(aDefaultCallNode2));
		assertTrue(elements.contains(aCallEdge1));
	}
}

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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ConstructorEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

/*
 * This class is used to test the methods of SequenceDiagram
 * to the exclusion of the method in the superclass Diagram.
 */
public class TestControlFlow
{
	private Diagram aDiagram;
	private DiagramAccessor aDiagramAccessor;
	private ControlFlow aFlow;
	
	private ImplicitParameterNode aParameter1;
	private ImplicitParameterNode aParameter2;
	private ImplicitParameterNode aParameter3;
	private CallNode aCall1;
	private CallNode aCall2;
	private CallNode aCall3;
	private CallNode aCall4;
	private CallNode aCall5;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	private CallEdge aCallEdge3;
	private CallEdge aCallEdge4;
	private ReturnEdge aReturnEdge;
	private ConstructorEdge aConstructorEdge;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.SEQUENCE);
		aDiagramAccessor = new DiagramAccessor(aDiagram);
		aFlow = new ControlFlow(aDiagram);
		
		aParameter1 = new ImplicitParameterNode();
		aParameter2 = new ImplicitParameterNode();
		aParameter3 = new ImplicitParameterNode();
		aCall1 = new CallNode();
		aCall2 = new CallNode();
		aCall3 = new CallNode();
		aCall4 = new CallNode();
		aCall5 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
		aCallEdge3 = new CallEdge();
		aCallEdge4 = new CallEdge();
		aReturnEdge = new ReturnEdge();
		aConstructorEdge = new ConstructorEdge();
		createSampleDiagram1();
	}
	
	/*
	 * aCall1 and aCall2 are on aParameter1
	 * aCall3 and aCall4 are on aParameter2
	 * aCall5 and aCall6 are on aParameter3
	 * aCall1 calls aCall3, then aCalls2 
	 * aCall2 calls aCall4 then returns
	 * aCall4 calls aCall5
	 * aCall1 calls aCall6
	 */
	private void createSampleDiagram1()
	{
		aDiagram.addRootNode(aParameter1);
		aDiagram.addRootNode(aParameter2);
		aDiagram.addRootNode(aParameter3);
		aParameter1.addChild(aCall1);
		aParameter2.addChild(aCall2);
		aParameter2.addChild(aCall3);
		aParameter3.addChild(aCall4);
		aParameter3.addChild(aCall5);
		aDiagramAccessor.connectAndAdd(aConstructorEdge, aCall1, aCall2);
		aDiagramAccessor.connectAndAdd(aReturnEdge, aCall2, aCall1);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCall2, aCall3);
		aDiagramAccessor.connectAndAdd(aCallEdge2, aCall3, aCall4);
		aDiagramAccessor.connectAndAdd(aCallEdge3, aCall2, aCall5);
	}
	
	@Test
	public void testGetCallerNoCaller()
	{
		assertFalse( aFlow.getCaller(aCall1).isPresent());
	}
	
	@Test
	public void testGetCallerSameParameter()
	{
		assertSame( aCall2, aFlow.getCaller(aCall3).get());
	}
	
	@Test
	public void testGetCallerDifferentParameter()
	{
		assertSame( aCall1, aFlow.getCaller(aCall2).get());
		assertSame( aCall2, aFlow.getCaller(aCall3).get());
		assertSame( aCall3, aFlow.getCaller(aCall4).get());
		assertSame( aCall2, aFlow.getCaller(aCall5).get());
	}
	
	@Test
	public void testGetCalleesEmpty()
	{
		assertTrue( aFlow.getCallees(aCall4).isEmpty());
		assertTrue( aFlow.getCallees(aCall5).isEmpty());
	}
	
	@Test
	public void testGetCalleesSingle()
	{
		List<Node> callees = aFlow.getCallees(aCall1);
		assertEquals(1, callees.size());
		assertSame(aCall2, callees.get(0));
		
		callees = aFlow.getCallees(aCall3);
		assertEquals(1, callees.size());
		assertSame(aCall4, callees.get(0));
	}
	
	@Test
	public void testGetCalleesMultiple()
	{
		List<Node> callees = aFlow.getCallees(aCall2);
		assertEquals(2, callees.size());
		assertTrue(callees.contains(aCall3));
		assertTrue(callees.contains(aCall5));
	}
	
	@Test
	public void testIsNestedNoCaller()
	{
		assertFalse(aFlow.isNested(aCall1));
	}
	
	@Test
	public void testIsNestedTrue()
	{
		assertTrue(aFlow.isNested(aCall3));
	}
	
	@Test
	public void testIsNestedFalse()
	{
		assertFalse(aFlow.isNested(aCall2));
		assertFalse(aFlow.isNested(aCall4));
		assertFalse(aFlow.isNested(aCall5));
	}
	
	@Test
	public void testIsFirstCalleeTrue()
	{
		assertTrue(aFlow.isFirstCallee(aCall2));
		assertTrue(aFlow.isFirstCallee(aCall3));
		assertTrue(aFlow.isFirstCallee(aCall4));
	}
	
	@Test
	public void testIsFirstCalleeFalse()
	{
		assertFalse(aFlow.isFirstCallee(aCall5));
	}
	
	@Test
	public void testGetPreviousCallee()
	{
		CallNode callNode = new CallNode();
		aParameter2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		assertSame(aCall2, aFlow.getPreviousCallee(callNode));
	}
	
	@Test
	public void testIsConstructorExecutionInConstructorCall()
	{
		assertTrue(aFlow.isConstructorExecution(aCall2));
	}
	
	@Test
	public void testIsConstructorExecutionNotInConstructorCall()
	{
		assertFalse(aFlow.isConstructorExecution(aCall4));
	}
	
	@Test
	public void testIsInConstructorCallWithWrongNode()
	{
		assertFalse(aFlow.isConstructorExecution(aCall3));
	}
	
	@Test
	public void testIsInConstructorCallWithWrongNodeType()
	{
		assertFalse(aFlow.isConstructorExecution(aParameter2));
	}
	
	@Test
	public void testGetEdgeDownStreamsCallEdge()
	{
		Collection<DiagramElement> downstreams = aFlow.getEdgeDownStreams(aCallEdge2);
		assertEquals(2, downstreams.size());
		assertTrue( downstreams.contains(aCall4));
		assertTrue( downstreams.contains(aCallEdge2));
	}
	
	@Test
	public void testGetEdgeDownStreamsNoteEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge = new NoteEdge();
		noteEdge.connect(noteNode, aCall3, aDiagram);
		aDiagram.addRootNode(noteNode);
		aDiagram.addEdge(noteEdge);
		Collection<DiagramElement> downstreams = aFlow.getEdgeDownStreams(noteEdge);
		assertEquals(1, downstreams.size());
	}
	
	@Test
	public void testGetNodeDownStreamsWithConstructorCall()
	{
		Collection<DiagramElement> downstreams1 = aFlow.getNodeDownStreams(aCall2);
		Collection<DiagramElement> downstreams2 = aFlow.getNodeDownStreams(aParameter2);
		assertEquals(downstreams1, downstreams2);
		assertEquals(9, downstreams1.size());
		assertTrue(downstreams1.contains(aConstructorEdge));
		assertTrue(downstreams1.contains(aCallEdge1));
		assertTrue(downstreams1.contains(aCallEdge2));
		assertTrue(downstreams1.contains(aCallEdge3));	
		assertTrue(downstreams1.contains(aParameter2));
		assertTrue(downstreams1.contains(aCall2));
		assertTrue(downstreams1.contains(aCall3));
		assertTrue(downstreams1.contains(aCall4));
		assertTrue(downstreams1.contains(aCall5));
	}
	
	@Test
	public void testGetNodeDownStreamsCallNode()
	{
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aCall3);
		assertEquals(2, downstreams.size());
		assertTrue(downstreams.contains(aCall4));
		assertTrue(downstreams.contains(aCallEdge2));
	}
	
	@Test
	public void testGetNodeDownStreamsNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addRootNode(noteNode);
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(noteNode);
		assertEquals(0, downstreams.size());
	}
	
	@Test
	public void testGetNodeDownStreamsParameter()
	{
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aParameter1);
		assertEquals(10, downstreams.size());
		assertTrue(downstreams.contains(aParameter2));
		assertTrue(downstreams.contains(aCall1));
		assertTrue(downstreams.contains(aCall2));
		assertTrue(downstreams.contains(aCall3));
		assertTrue(downstreams.contains(aCall4));
		assertTrue(downstreams.contains(aCall5));
		assertTrue(downstreams.contains(aCallEdge1));
		assertTrue(downstreams.contains(aCallEdge2));
		assertTrue(downstreams.contains(aCallEdge3));
		assertTrue(downstreams.contains(aConstructorEdge));
	}
	
	@Test
	public void testGetNodeUpstreamsIfNoOtherFlows()
	{
		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
		assertEquals(1, upstreams.size());
		assertTrue(upstreams.contains(aCall1));
	}

	@Test
	public void testGetNodeUpstreamsIfHasOtherFlows()
	{
		CallNode callNode = new CallNode();
		aParameter3.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		assertEquals(0, aFlow.getNodeUpstreams(callNode).size());
	}
	
	@Test
	public void testGetNodeUpstreamsIfHasOtherFlowsInConstructorExecution()
	{
		CallNode callNode = new CallNode();
		aParameter2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
		assertEquals(3, upstreams.size());
		assertTrue(upstreams.contains(aConstructorEdge));
		assertTrue(upstreams.contains(aCall1));
		assertTrue(upstreams.contains(aCallEdge4));
	}
	
	@Test
	public void testGetNodeUpstreamsHasOtherFlowBesidesConstructorCall()
	{
		CallNode call1 = new CallNode();
		CallNode call2 = new CallNode();
		CallEdge callEdge = new CallEdge();
		aParameter2.addChild(call1);
		aParameter3.addChild(call2);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, call1);
		aDiagramAccessor.connectAndAdd(callEdge, aCall1, call2);
		
		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
		assertEquals(2, upstreams.size());
		assertTrue(upstreams.contains(aConstructorEdge));
		assertTrue(upstreams.contains(aCallEdge4));
		assertEquals(0, aFlow.getNodeUpstreams(aParameter2).size());
	}
	
	@Test
	public void testGetNodeUpstreamsWithNestedCallers()
	{
		assertEquals(0, aFlow.getNodeUpstreams(aCall4).size());
	}
	
	@Test
	public void testGetNodeUpstreamsNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addRootNode(noteNode);
		assertEquals(0, aFlow.getNodeUpstreams(noteNode).size());
	}
	
	@Test
	public void testGetNodeUpstreamsConstructedObject()
	{
		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aParameter2);
		assertEquals(1, upstreams.size());
		assertTrue(upstreams.contains(aCall1));
	}
	
	@Test
	public void testGetNodeUpstreamsUnconnectedParameter()
	{
		ImplicitParameterNode parameter = new ImplicitParameterNode();
		aDiagram.addRootNode(parameter);
		assertEquals(0, aFlow.getNodeUpstreams(parameter).size());
	}
	
	@Test
	public void testGetNodeUpstreamsParameterWithNoCaller()
	{
		assertEquals(0, aFlow.getNodeUpstreams(aParameter1).size());
	}
	
	@Test
	public void testGetNodeUpstreamsParameterWithNestedCallers()
	{
		assertEquals(0, aFlow.getNodeUpstreams(aParameter3).size());
	}
	
	@Test 
	public void testGetEdgeStartNoteEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge = new NoteEdge();
		aDiagramAccessor.connectAndAdd(noteEdge, aCall1, noteNode);
		assertTrue(aFlow.getEdgeStart(noteEdge).isEmpty());
	}
	
	@Test 
	public void testGetEdgeStartHasNoOtherFlows()
	{
		Optional<DiagramElement> start = aFlow.getEdgeStart(aConstructorEdge);
		assertTrue(start.isPresent());
		assertSame(aCall1, start.get());
	}
	
	@Test 
	public void testGetEdgeStartHasOtherFlowsInConstructorCall()
	{
		CallNode callNode = new CallNode();
		aParameter2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		
		Optional<DiagramElement> start = aFlow.getEdgeStart(aConstructorEdge);
		assertTrue(start.isPresent());
		assertSame(aCall1, start.get());
	}
	
	@Test 
	public void testGetEdgeStartHasOtherFlowsBesidesConstructorCall()
	{
		CallNode callNode = new CallNode();
		aParameter3.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		
		Optional<DiagramElement> start = aFlow.getEdgeStart(aConstructorEdge);
		assertTrue(start.isEmpty());
	}
	
	@Test 
	public void testGetEdgeStartHasOtherFlowsNestedConstructorCall()
	{
		ImplicitParameterNode parameter = new ImplicitParameterNode();
		CallNode  callNode = new CallNode();
		ConstructorEdge constructorEdge = new ConstructorEdge();
		aDiagram.addRootNode(parameter);
		parameter.addChild(callNode);
		aDiagramAccessor.connectAndAdd(constructorEdge, aCall2, callNode);
		
		Optional<DiagramElement> start = aFlow.getEdgeStart(constructorEdge);
		assertTrue(start.isEmpty());
	}
	
	@Test
	public void testGetCorrespondingReturnEdges()
	{
		ReturnEdge returnEdge1 = new ReturnEdge();
		aDiagramAccessor.connectAndAdd(returnEdge1, aCall4, aCall3);
		List<DiagramElement> elements = new ArrayList<>();
		elements.add(aConstructorEdge);
		elements.add(aCallEdge2);
		elements.add(aCallEdge3);
		elements.add(aCall1);
		
		Collection<DiagramElement> returnEdges = aFlow.getCorrespondingReturnEdges(elements);
		assertEquals(2, returnEdges.size());
		assertTrue(returnEdges.contains(aReturnEdge));
		assertTrue(returnEdges.contains(returnEdge1));
	}
	
	@Test
	public void testCanCreateConstructedObjectParameterWithChild()
	{	
		assertFalse(aFlow.canCreateConstructedObject(aParameter3, new Point(0, 0)));
	}
}

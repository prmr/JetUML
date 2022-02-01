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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ConstructorEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;

/*
 * This class is used to test the methods of SequenceDiagram
 * to the exclusion of the method in the superclass Diagram.
 */
public class TestControlFlow
{
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	private DiagramAccessor aDiagramAccessor = new DiagramAccessor(aDiagram);
	private ControlFlow aFlow = new ControlFlow(aDiagram);
	
	private ImplicitParameterNode aParameter1 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameter2 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameter3 = new ImplicitParameterNode();
	private CallNode aCall1 = new CallNode();
	private CallNode aCall2 = new CallNode();
	private CallNode aCall3 = new CallNode();
	private CallNode aCall4 = new CallNode();
	private CallNode aCall5 = new CallNode();
	private CallEdge aCallEdge1 = new CallEdge();
	private CallEdge aCallEdge2 = new CallEdge();
	private CallEdge aCallEdge3 = new CallEdge();
	private CallEdge aCallEdge4 = new CallEdge();
	private ReturnEdge aReturnEdge = new ReturnEdge();
	private ConstructorEdge aConstructorEdge = new ConstructorEdge();
	
	@BeforeEach
	public void setUp()
	{
		createSampleDiagram1();
	}
	
	/*
	 * aCall1 is on aParameter1
	 * aCall2 and aCall3 are on aParameter2
	 * aCall4 and aCall5 are on aParameter3
	 * aConstructorEdge connects aCall1 to aCall2
	 * aReturnEdge connects aCall2 to aCall1
	 * aCall2 calls aCall3
	 * aCall3 calls aCall4
	 * aCall2 calls aCall5
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
	
	/*
	 * Recursive calls.
	 * 
	 * aCall1-4 is on aParameter1
	 * aCall1 calls aCall2
	 * aCall2 calls aCall3 etc.
	 */
	private void createSampleDiagram2()
	{
		aDiagram.addRootNode(aParameter1);
		aParameter1.addChild(aCall1);
		aParameter1.addChild(aCall2);
		aParameter1.addChild(aCall3);
		aParameter1.addChild(aCall4);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCall1, aCall2);
		aDiagramAccessor.connectAndAdd(aCallEdge2, aCall2, aCall3);
		aDiagramAccessor.connectAndAdd(aCallEdge3, aCall3, aCall4);
	}
	
	@Test
	void testGetNestingDepth_0()
	{
		createSampleDiagram2();
		assertEquals(0, aFlow.getNestingDepth(aCall1));
	}
	
	@Test
	void testGetNestingDepth_1()
	{
		createSampleDiagram2();
		assertEquals(1, aFlow.getNestingDepth(aCall2));
	}
	
	@Test
	void testGetNestingDepth_2()
	{
		createSampleDiagram2();
		assertEquals(2, aFlow.getNestingDepth(aCall3));
	}
	
	@Test
	void testGetNestingDepth_3()
	{
		createSampleDiagram2();
		assertEquals(3, aFlow.getNestingDepth(aCall4));
	}
	
	@Test
	void testGetNestingDepth_DifferentParents()
	{
		assertEquals(0, aFlow.getNestingDepth(aCall2));
	}
	
	@Test
	void testHasEntryPoint_No()
	{
		assertFalse(new ControlFlow(new Diagram(DiagramType.SEQUENCE)).hasEntryPoint());
	}
	
	@Test
	void testHasEntryPoint_Yes()
	{
		assertTrue(aFlow.hasEntryPoint());
	}
	
	@Test
	void testGetCallerNoCaller()
	{
		assertFalse( aFlow.getCaller(aCall1).isPresent());
	}
	
	@Test
	void testGetCallerSameParameter()
	{
		assertSame( aCall2, aFlow.getCaller(aCall3).get());
	}
	
	@Test
	void testGetCallerDifferentParameter()
	{
		assertSame( aCall1, aFlow.getCaller(aCall2).get());
		assertSame( aCall2, aFlow.getCaller(aCall3).get());
		assertSame( aCall3, aFlow.getCaller(aCall4).get());
		assertSame( aCall2, aFlow.getCaller(aCall5).get());
	}
	
	@Test
	void testGetCalleesEmpty()
	{
		assertTrue( aFlow.getCallees(aCall4).isEmpty());
		assertTrue( aFlow.getCallees(aCall5).isEmpty());
	}
	
	@Test
	void testGetCalleesSingle()
	{
		List<Node> callees = aFlow.getCallees(aCall1);
		assertEquals(1, callees.size());
		assertSame(aCall2, callees.get(0));
		
		callees = aFlow.getCallees(aCall3);
		assertEquals(1, callees.size());
		assertSame(aCall4, callees.get(0));
	}
	
	@Test
	void testGetCalleesMultiple()
	{
		List<Node> callees = aFlow.getCallees(aCall2);
		assertEquals(2, callees.size());
		assertTrue(callees.contains(aCall3));
		assertTrue(callees.contains(aCall5));
	}
	
	@Test
	void testIsNestedNoCaller()
	{
		assertFalse(aFlow.isNested(aCall1));
	}
	
	@Test
	void testIsNestedTrue()
	{
		assertTrue(aFlow.isNested(aCall3));
	}
	
	@Test
	void testIsNestedFalse()
	{
		assertFalse(aFlow.isNested(aCall2));
		assertFalse(aFlow.isNested(aCall4));
		assertFalse(aFlow.isNested(aCall5));
	}
	
	@Test
	void testIsFirstCalleeTrue()
	{
		assertTrue(aFlow.isFirstCallee(aCall2));
		assertTrue(aFlow.isFirstCallee(aCall3));
		assertTrue(aFlow.isFirstCallee(aCall4));
	}
	
	@Test
	void testIsFirstCalleeFalse()
	{
		assertFalse(aFlow.isFirstCallee(aCall5));
	}
	
	@Test
	void testGetPreviousCallee()
	{
		CallNode callNode = new CallNode();
		aParameter2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		assertSame(aCall2, aFlow.getPreviousCallee(callNode));
	}
	
	@Test
	void testIsConstructorExecutionInConstructorCall()
	{
		assertTrue(aFlow.isConstructorExecution(aCall2));
	}
	
	@Test
	void testIsConstructorExecutionNotInConstructorCall()
	{
		assertFalse(aFlow.isConstructorExecution(aCall4));
	}
	
	@Test
	void testIsInConstructorCallWithWrongNode()
	{
		assertFalse(aFlow.isConstructorExecution(aCall3));
	}
	
	@Test
	void testIsInConstructorCallWithWrongNodeType()
	{
		assertFalse(aFlow.isConstructorExecution(aParameter2));
	}
	
	@Test
	void testGetEdgeDownStreamsCallEdge()
	{
		Collection<DiagramElement> downstreams = aFlow.getEdgeDownStreams(aCallEdge2);
		assertEquals(2, downstreams.size());
		assertTrue( downstreams.contains(aCall4));
		assertTrue( downstreams.contains(aCallEdge2));
	}
	
	@Test
	void testGetEdgeDownStreamsNoteEdge()
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
	void testGetNodeDownStreamsWithConstructorCall()
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
	void testGetNodeDownStreamsCallNode()
	{
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aCall3);
		assertEquals(2, downstreams.size());
		assertTrue(downstreams.contains(aCall4));
		assertTrue(downstreams.contains(aCallEdge2));
	}
	
	@Test
	void testGetNodeDownStreamsNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addRootNode(noteNode);
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(noteNode);
		assertEquals(0, downstreams.size());
	}
	
	@Test
	void testGetNodeDownStreamsParameter()
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
	void testGetNodeUpstreamsIfNoOtherFlows()
	{
		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
		assertEquals(1, upstreams.size());
		assertTrue(upstreams.contains(aCall1));
	}

	@Test
	void testGetNodeUpstreamsIfHasOtherFlows()
	{
		CallNode callNode = new CallNode();
		aParameter3.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		assertEquals(0, aFlow.getNodeUpstreams(callNode).size());
	}
	
	@Test
	void testGetNodeUpstreamsIfHasOtherFlowsInConstructorExecution()
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
	void testGetNodeUpstreamsHasOtherFlowBesidesConstructorCall()
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
	void testGetNodeUpstreamsWithNestedCallers()
	{
		assertEquals(0, aFlow.getNodeUpstreams(aCall4).size());
	}
	
	@Test
	void testGetNodeUpstreamsNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addRootNode(noteNode);
		assertEquals(0, aFlow.getNodeUpstreams(noteNode).size());
	}
	
	@Test
	void testGetNodeUpstreamsConstructedObject()
	{
		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aParameter2);
		assertEquals(1, upstreams.size());
		assertTrue(upstreams.contains(aCall1));
	}
	
	@Test
	void testGetNodeUpstreamsUnconnectedParameter()
	{
		ImplicitParameterNode parameter = new ImplicitParameterNode();
		aDiagram.addRootNode(parameter);
		assertEquals(0, aFlow.getNodeUpstreams(parameter).size());
	}
	
	@Test
	void testGetNodeUpstreamsParameterWithNoCaller()
	{
		assertEquals(0, aFlow.getNodeUpstreams(aParameter1).size());
	}
	
	@Test
	void testGetNodeUpstreamsParameterWithNestedCallers()
	{
		assertEquals(0, aFlow.getNodeUpstreams(aParameter3).size());
	}
	
	@Test 
	void testGetEdgeStartNoteEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge = new NoteEdge();
		aDiagramAccessor.connectAndAdd(noteEdge, aCall1, noteNode);
		assertTrue(aFlow.getEdgeStart(noteEdge).isEmpty());
	}
	
	@Test 
	void testGetEdgeStartHasNoOtherFlows()
	{
		Optional<DiagramElement> start = aFlow.getEdgeStart(aConstructorEdge);
		assertTrue(start.isPresent());
		assertSame(aCall1, start.get());
	}
	
	@Test 
	void testGetEdgeStartHasOtherFlowsInConstructorCall()
	{
		CallNode callNode = new CallNode();
		aParameter2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		
		Optional<DiagramElement> start = aFlow.getEdgeStart(aConstructorEdge);
		assertTrue(start.isPresent());
		assertSame(aCall1, start.get());
	}
	
	@Test 
	void testGetEdgeStartHasOtherFlowsBesidesConstructorCall()
	{
		CallNode callNode = new CallNode();
		aParameter3.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
		
		Optional<DiagramElement> start = aFlow.getEdgeStart(aConstructorEdge);
		assertTrue(start.isEmpty());
	}
	
	@Test 
	void testGetEdgeStartHasOtherFlowsNestedConstructorCall()
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
	void testGetCorrespondingReturnEdges()
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
}

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
package org.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	
//	@Test
//	void testGetCallerNoCaller()
//	{
//		assertFalse( aFlow.getCaller(aCall1).isPresent());
//	}
//	
//	@Test
//	void testGetCallerSameParameter()
//	{
//		assertSame( aCall2, aFlow.getCaller(aCall3).get());
//	}
//	
//	@Test
//	void testGetCallerDifferentParameter()
//	{
//		assertSame( aCall1, aFlow.getCaller(aCall2).get());
//		assertSame( aCall2, aFlow.getCaller(aCall3).get());
//		assertSame( aCall3, aFlow.getCaller(aCall4).get());
//		assertSame( aCall2, aFlow.getCaller(aCall5).get());
//	}
	
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
	
//	@Test
//	void testGetNodeUpstreamsIfNoOtherFlows()
//	{
//		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
//		assertEquals(1, upstreams.size());
//		assertTrue(upstreams.contains(aCall1));
//	}
//
//	@Test
//	void testGetNodeUpstreamsIfHasOtherFlows()
//	{
//		CallNode callNode = new CallNode();
//		aParameter3.addChild(callNode);
//		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
//		assertEquals(0, aFlow.getNodeUpstreams(callNode).size());
//	}
//	
//	@Test
//	void testGetNodeUpstreamsIfHasOtherFlowsInConstructorExecution()
//	{
//		CallNode callNode = new CallNode();
//		aParameter2.addChild(callNode);
//		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, callNode);
//		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
//		assertEquals(3, upstreams.size());
//		assertTrue(upstreams.contains(aConstructorEdge));
//		assertTrue(upstreams.contains(aCall1));
//		assertTrue(upstreams.contains(aCallEdge4));
//	}
//	
//	@Test
//	void testGetNodeUpstreamsHasOtherFlowBesidesConstructorCall()
//	{
//		CallNode call1 = new CallNode();
//		CallNode call2 = new CallNode();
//		CallEdge callEdge = new CallEdge();
//		aParameter2.addChild(call1);
//		aParameter3.addChild(call2);
//		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall1, call1);
//		aDiagramAccessor.connectAndAdd(callEdge, aCall1, call2);
//		
//		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aCall2);
//		assertEquals(2, upstreams.size());
//		assertTrue(upstreams.contains(aConstructorEdge));
//		assertTrue(upstreams.contains(aCallEdge4));
//		assertEquals(0, aFlow.getNodeUpstreams(aParameter2).size());
//	}
//	
//	@Test
//	void testGetNodeUpstreamsWithNestedCallers()
//	{
//		assertEquals(0, aFlow.getNodeUpstreams(aCall4).size());
//	}
//	
//	@Test
//	void testGetNodeUpstreamsNoteNode()
//	{
//		NoteNode noteNode = new NoteNode();
//		aDiagram.addRootNode(noteNode);
//		assertEquals(0, aFlow.getNodeUpstreams(noteNode).size());
//	}
//	
//	@Test
//	void testGetNodeUpstreamsConstructedObject()
//	{
//		Collection<DiagramElement> upstreams = aFlow.getNodeUpstreams(aParameter2);
//		assertEquals(1, upstreams.size());
//		assertTrue(upstreams.contains(aCall1));
//	}
//	
//	@Test
//	void testGetNodeUpstreamsUnconnectedParameter()
//	{
//		ImplicitParameterNode parameter = new ImplicitParameterNode();
//		aDiagram.addRootNode(parameter);
//		assertEquals(0, aFlow.getNodeUpstreams(parameter).size());
//	}
//	
//	@Test
//	void testGetNodeUpstreamsParameterWithNoCaller()
//	{
//		assertEquals(0, aFlow.getNodeUpstreams(aParameter1).size());
//	}
//	
//	@Test
//	void testGetNodeUpstreamsParameterWithNestedCallers()
//	{
//		assertEquals(0, aFlow.getNodeUpstreams(aParameter3).size());
//	}
}

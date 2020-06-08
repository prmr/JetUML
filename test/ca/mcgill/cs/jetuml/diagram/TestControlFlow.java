/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by the contributors of the JetUML project.
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

import java.util.Collection;
import java.util.List;

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
	private CallNode aCall6;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	private CallEdge aCallEdge3;
	private CallEdge aCallEdge4;
	private CallEdge aCallEdge5;
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
		aCall6 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
		aCallEdge3 = new CallEdge();
		aCallEdge4 = new CallEdge();
		aCallEdge5 = new CallEdge();
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
		aParameter1.addChild(aCall2);
		aParameter2.addChild(aCall3);
		aParameter2.addChild(aCall4);
		aParameter3.addChild(aCall5);
		aParameter3.addChild(aCall6);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCall1, aCall3);
		aDiagramAccessor.connectAndAdd(aCallEdge2, aCall1, aCall2);
		aDiagramAccessor.connectAndAdd(aCallEdge3, aCall2, aCall4);
		aDiagramAccessor.connectAndAdd(aReturnEdge, aCall4, aCall2);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall4, aCall5);
		aDiagramAccessor.connectAndAdd(aCallEdge5, aCall1, aCall6);
	}
	
	@Test
	public void testGetCallerNoCaller()
	{
		assertFalse( aFlow.getCaller(aCall1).isPresent());
	}
	
	@Test
	public void testGetCallerSameParameter()
	{
		assertSame( aCall1, aFlow.getCaller(aCall2).get());
	}
	
	@Test
	public void testGetCallerDifferentParameter()
	{
		assertSame( aCall1, aFlow.getCaller(aCall3).get());
		assertSame( aCall2, aFlow.getCaller(aCall4).get());
		assertSame( aCall4, aFlow.getCaller(aCall5).get());
		assertSame( aCall1, aFlow.getCaller(aCall6).get());
	}
	
	@Test
	public void testGetCalleesEmpty()
	{
		assertTrue( aFlow.getCallees(aCall3).isEmpty());
		assertTrue( aFlow.getCallees(aCall5).isEmpty());
		assertTrue( aFlow.getCallees(aCall6).isEmpty());
	}
	
	@Test
	public void testGetCalleesSingle()
	{
		List<Node> callees = aFlow.getCallees(aCall2);
		assertEquals(1, callees.size());
		assertSame(aCall4, callees.get(0));
		
		callees = aFlow.getCallees(aCall4);
		assertEquals(1, callees.size());
		assertSame(aCall5, callees.get(0));
	}
	
	@Test
	public void testGetCalleesMultiple()
	{
		List<Node> callees = aFlow.getCallees(aCall1);
		assertEquals(3, callees.size());
		assertTrue( callees.contains(aCall2));
		assertTrue( callees.contains(aCall3));
		assertTrue( callees.contains(aCall6));
	}
	
	@Test
	public void testGetCalleesIsNestedNoCaller()
	{
		assertFalse(aFlow.isNested(aCall1));
	}
	
	@Test
	public void testGetCalleesIsNestedTrue()
	{
		assertTrue(aFlow.isNested(aCall2));
	}
	
	@Test
	public void testGetCalleesIsNestedFalse()
	{
		assertFalse(aFlow.isNested(aCall3));
		assertFalse(aFlow.isNested(aCall4));
		assertFalse(aFlow.isNested(aCall5));
		assertFalse(aFlow.isNested(aCall6));
	}
	
	@Test
	public void testIsFirstCalleeTrue()
	{
		assertTrue(aFlow.isFirstCallee(aCall3));
		assertTrue(aFlow.isFirstCallee(aCall4));
		assertTrue(aFlow.isFirstCallee(aCall5));
	}
	
	@Test
	public void testIsFirstCalleeFalse()
	{
		assertFalse(aFlow.isFirstCallee(aCall2));
		assertFalse(aFlow.isFirstCallee(aCall6));
	}
	
	@Test
	public void testGetPreviousCallee()
	{
		assertSame(aCall3, aFlow.getPreviousCallee(aCall2));
		assertSame(aCall2, aFlow.getPreviousCallee(aCall6));
	}
	
	/*
	 * @Todo
	 */
	@Test
	public void testIsInConstructorCall()
	{
		aConstructorEdge.connect(aCall1, aCall2, aDiagram);
		aDiagram.addEdge(aConstructorEdge);
		assertTrue(aFlow.isInConstructorCall(aCall2));
	}
	
	@Test
	public void testIsInConstructorCallWithWrongEdge()
	{
		aCallEdge1.connect(aCall1, aCall2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		assertFalse(aFlow.isInConstructorCall(aCall2));
	}
	
	@Test
	public void testIsInConstructorCallWithWrongNode()
	{
		aCallEdge1.connect(aCall1, aCall2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		assertFalse(aFlow.isInConstructorCall(aCall1));
	}
	
	@Test
	public void testIsInConstructorCallWithWrongNodeType()
	{
		aCallEdge1.connect(aCall1, aParameter1, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		assertFalse(aFlow.isInConstructorCall(aParameter1));
	}
	
	@Test
	public void testGetEdgeDownStreamsCallEdge()
	{
		Collection<DiagramElement> downstreams = aFlow.getEdgeDownStreams(aCallEdge3);
		assertEquals(4, downstreams.size());
		assertTrue( downstreams.contains(aCall4));
		assertTrue( downstreams.contains(aCall5));
		assertTrue( downstreams.contains(aCallEdge3));
		assertTrue( downstreams.contains(aCallEdge4));
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
	public void testGetNodeDownStreamsCallNodeInConstructorCall()
	{
		aConstructorEdge.connect(aCall1, aCall3, aDiagram);
		aDiagram.addEdge(aConstructorEdge);
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aCall3);
		assertEquals(8, downstreams.size());
		assertTrue(downstreams.contains(aConstructorEdge));
		assertTrue(downstreams.contains(aCallEdge1));
		assertTrue(downstreams.contains(aCallEdge3));
		assertTrue(downstreams.contains(aCallEdge4));	
		assertTrue(downstreams.contains(aParameter2));
		assertTrue(downstreams.contains(aCall3));
		assertTrue(downstreams.contains(aCall4));
		assertTrue(downstreams.contains(aCall5));
	}
	
	@Test
	public void testGetNodeDownStreamsParameterInConstructorCall()
	{
		aConstructorEdge.connect(aCall1, aCall3, aDiagram);
		aDiagram.addEdge(aConstructorEdge);
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aParameter2);
		assertEquals(8, downstreams.size());
		assertTrue(downstreams.contains(aConstructorEdge));
		assertTrue(downstreams.contains(aCallEdge1));
		assertTrue(downstreams.contains(aCallEdge3));
		assertTrue(downstreams.contains(aCallEdge4));	
		assertTrue(downstreams.contains(aParameter2));
		assertTrue(downstreams.contains(aCall3));
		assertTrue(downstreams.contains(aCall4));
		assertTrue(downstreams.contains(aCall5));
	}
	
	@Test
	public void testGetNodeDownStreamsParameter()
	{
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aParameter2);
		assertEquals(4, downstreams.size());
		assertTrue(downstreams.contains(aCall3));
		assertTrue(downstreams.contains(aCall4));
		assertTrue(downstreams.contains(aCall5));
		assertTrue(downstreams.contains(aCallEdge4));	
	}
	
	@Test
	public void testGetNodeDownStreamsCallNode()
	{
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(aCall4);
		assertEquals(2, downstreams.size());
		assertTrue(downstreams.contains(aCall5));
		assertTrue(downstreams.contains(aCallEdge4));
	}
	
	@Test
	public void testGetNodeDownStreamsNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addRootNode(noteNode);
		Collection<DiagramElement> downstreams = aFlow.getNodeDownStreams(noteNode);
		assertEquals(0, downstreams.size());
	}
}

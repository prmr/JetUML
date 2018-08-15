/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;

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
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	@Before
	public void setUp()
	{
		aDiagram = new SequenceDiagram();
		aDiagramAccessor = new DiagramAccessor(aDiagram);
		aFlow = new ControlFlow((SequenceDiagram)aDiagram);
		
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
		createSampleDiagram1();
	}
	
	/*
	 * aCall1 and aCall2 are on aParameter1
	 * aCall3 and aCall4 are on aParameter2
	 * aCall5 and aCall6 are on aParameter3
	 * aCall1 calls aCall3, then aCalls2 then returns, then aCall6
	 * aCall2 calls aCall4
	 * aCall4 calls aCall5
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
		aDiagramAccessor.connectAndAdd(aReturnEdge, aCall2, aCall1);
		aDiagramAccessor.connectAndAdd(aCallEdge3, aCall1, aCall6);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCall2, aCall4);
		aDiagramAccessor.connectAndAdd(aCallEdge5, aCall4, aCall5);
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
}

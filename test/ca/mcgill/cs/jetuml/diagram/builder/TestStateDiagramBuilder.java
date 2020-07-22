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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestStateDiagramBuilder
{
	private Diagram aDiagram;
	private StateDiagramBuilder aBuilder;
	private InitialStateNode aInitial;
	private StateNode aStateNode1;
	private StateNode aStateNode2;
	private FinalStateNode aFinal;
	private NoteNode aNoteNode;
	
	private StateTransitionEdge aEdge1;
	private StateTransitionEdge aEdge2;
	private StateTransitionEdge aEdge3;
	private NoteEdge aNoteEdge;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.STATE);
		aBuilder = new StateDiagramBuilder(aDiagram);
		
		aInitial = new InitialStateNode();
		aStateNode1 = new StateNode();
		aStateNode2 = new StateNode();
		aFinal = new FinalStateNode();
		aNoteNode = new NoteNode();
		
		aEdge1 = new StateTransitionEdge();
		aEdge2 = new StateTransitionEdge();
		aEdge3 = new StateTransitionEdge();
		aNoteEdge = new NoteEdge();
	}
	
	private void connectAndAdd(Edge pEdge, Node pStart, Node pEnd)
	{
		pEdge.connect(pStart, pEnd, aDiagram);
		aDiagram.addEdge(pEdge);
	}
	
	@Test
	public void testCanAddEdgeSelfInitialStateNode()
	{
		aDiagram.addRootNode(aInitial);
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(10,10)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10,10), new Point(10,10)));
	}
	
	@Test
	public void testCanAddEdgeSelfFinalStateNode()
	{
		aDiagram.addRootNode(aFinal);
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(10,10)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10,10), new Point(10,10)));
	}
	
	@Test
	public void testCanAddEdgeSelfStateNode()
	{
		aDiagram.addRootNode(aStateNode1);
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10,10), new Point(10,10)));
		assertTrue(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(10,10)));
		connectAndAdd(aEdge1, aStateNode1, aStateNode1);
		assertTrue(aBuilder.canAdd(aEdge2, new Point(10,10), new Point(10,10)));
		connectAndAdd(aEdge2, aStateNode1, aStateNode1);
		assertFalse(aBuilder.canAdd(aEdge3, new Point(10,10), new Point(10,10)));
	}
	
	@Test
	public void testCanAddEdgeSelfNoteNode()
	{
		aDiagram.addRootNode(aNoteNode);
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10,10), new Point(10,10)));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(10,10)));
	}
	
	@Test
	public void testCanAddEdgeInitialNoteToFrom()
	{
		aDiagram.addRootNode(aInitial);
		aDiagram.addRootNode(aNoteNode);
		aNoteNode.moveTo(new Point(100,100));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10,10), new Point(110,110)));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(110,110), new Point(10,10)));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(110,110), new Point(10,10)));
	}
	
	@Test
	public void testCanAddEdgeFinalNoteToFrom()
	{
		aDiagram.addRootNode(aFinal);
		aDiagram.addRootNode(aNoteNode);
		aNoteNode.moveTo(new Point(100,100));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10,10), new Point(110,110)));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(110,110), new Point(10,10)));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(110,110), new Point(10,10)));
	}
	
	@Test
	public void testCanAddEdgeInitialStateTo()
	{
		aDiagram.addRootNode(aInitial);
		aDiagram.addRootNode(aStateNode1);
		aStateNode1.moveTo(new Point(100,100));
		assertTrue(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge1, aInitial, aStateNode1);
		assertTrue(aBuilder.canAdd(aEdge2, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge2, aInitial, aStateNode1);
		assertFalse(aBuilder.canAdd(aEdge3, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCanAddEdgeInitialStateFrom()
	{
		aDiagram.addRootNode(aInitial);
		aDiagram.addRootNode(aStateNode1);
		aInitial.moveTo(new Point(100,100));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCanAddEdgeFinalStateTo()
	{
		aDiagram.addRootNode(aFinal);
		aDiagram.addRootNode(aStateNode1);
		aFinal.moveTo(new Point(100,100));
		assertTrue(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge1, aStateNode1, aFinal);
		assertTrue(aBuilder.canAdd(aEdge2, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge2, aStateNode1, aFinal);
		assertFalse(aBuilder.canAdd(aEdge3, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCanAddEdgeFinalStateFrom()
	{
		aDiagram.addRootNode(aFinal);
		aDiagram.addRootNode(aStateNode1);
		aStateNode1.moveTo(new Point(100,100));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCanAddEdgeInitialFinalTo()
	{
		aDiagram.addRootNode(aInitial);
		aDiagram.addRootNode(aFinal);
		aFinal.moveTo(new Point(100,100));
		assertTrue(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge1, aInitial, aFinal);
		assertTrue(aBuilder.canAdd(aEdge2, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge2, aInitial, aFinal);
		assertFalse(aBuilder.canAdd(aEdge3, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCanAddEdgeInitialFinalFrom()
	{
		aDiagram.addRootNode(aInitial);
		aDiagram.addRootNode(aFinal);
		aInitial.moveTo(new Point(100,100));
		assertFalse(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCanAddEdgeStateStateToFrom()
	{
		aDiagram.addRootNode(aStateNode1);
		aDiagram.addRootNode(aStateNode2);
		aStateNode2.moveTo(new Point(100,100));
		assertTrue(aBuilder.canAdd(aEdge1, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge1, aStateNode1, aStateNode2);
		assertTrue(aBuilder.canAdd(aEdge2, new Point(10,10), new Point(110,110)));
		connectAndAdd(aEdge2, aStateNode1, aStateNode2);
		assertFalse(aBuilder.canAdd(aEdge3, new Point(10,10), new Point(110,110)));
		assertTrue(aBuilder.canAdd(aEdge3, new Point(110,110), new Point(10,10)));
		connectAndAdd(aEdge3, aStateNode2, aStateNode1);
		assertTrue(aBuilder.canAdd(new StateTransitionEdge(), new Point(110,110), new Point(10,10)));
		connectAndAdd(new StateTransitionEdge(), aStateNode2, aStateNode1);
		assertFalse(aBuilder.canAdd(new StateTransitionEdge(), new Point(110,110), new Point(10,10)));
	}
}

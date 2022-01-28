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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.builder.StateDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestUsageScenariosStateDiagram extends AbstractTestUsageScenarios
{
	private StateNode aStateNode1;
	private StateNode aStateNode2;
	private InitialStateNode aInitialNode;
	private FinalStateNode aFinalNode;
	private StateTransitionEdge aTransitionEdge1;
	private StateTransitionEdge aTransitionEdge2;
	private StateTransitionEdge aTransitionEdge3;
	private StateTransitionEdge aTransitionEdge4;
	private StateTransitionEdge aTransitionEdge5;

	@BeforeEach
	@Override
	public void setup()
	{
		super.setup();
		aDiagram = new Diagram(DiagramType.STATE);
		aBuilder = new StateDiagramBuilder(aDiagram);
		aStateNode1 = new StateNode();
		aStateNode2 = new StateNode();
		aInitialNode = new InitialStateNode();
		aFinalNode = new FinalStateNode();
		aTransitionEdge1 = new StateTransitionEdge();
		aTransitionEdge2 = new StateTransitionEdge();
		aTransitionEdge3 = new StateTransitionEdge();
		aTransitionEdge4 = new StateTransitionEdge();
		aTransitionEdge5 = new StateTransitionEdge();
	}
	
	@Test
	public void testStateDiagramCreate()
	{
		aStateNode1.setName("Node 1");
		aStateNode2.setName("Node 2");
		addNode(aStateNode1, new Point(30,30));
		addNode(aStateNode2, new Point(30, 100));
		addNode(aInitialNode, new Point(5, 5));
		addNode(aFinalNode, new Point(30, 200));
		assertEquals(4, numberOfRootNodes());
		
		aTransitionEdge1.setMiddleLabel("Edge 1");
		addEdge(aTransitionEdge1, new Point(6, 6), new Point(35, 35));
		
		aTransitionEdge2.setMiddleLabel("Edge 2");
		addEdge(aTransitionEdge2, new Point(35, 35), new Point(35, 105));
		
		aTransitionEdge3.setMiddleLabel("Edge 3");
		addEdge(aTransitionEdge3, new Point(35, 105), new Point(35, 35));
		
		aTransitionEdge4.setMiddleLabel("Edge 4");
		addEdge(aTransitionEdge4, new Point(35, 105), new Point(32, 202));
		assertEquals(4, numberOfEdges());
		
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(6, 6), new Point(35, 35))); 
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(35, 35), new Point(35, 105)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(35, 105), new Point(35, 35)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(35, 105), new Point(32, 202)));
		assertEquals(4, numberOfEdges());
		
		assertEquals(4, numberOfRootNodes());
		assertEquals(new Point(30, 30), aStateNode1.position());
		assertEquals("Node 1", aStateNode1.getName());
		assertEquals(new Point(30, 100), aStateNode2.position());
		assertEquals("Node 2", aStateNode2.getName());
		assertEquals(new Point(5, 5), aInitialNode.position());
		assertEquals(new Point(30, 200), aFinalNode.position());
		
		assertEquals(4, numberOfEdges());
		assertEquals("Edge 1", aTransitionEdge1.getMiddleLabel());
		assertSame(aInitialNode, aTransitionEdge1.getStart());
		assertSame(aStateNode1, aTransitionEdge1.getEnd());
		
		assertEquals("Edge 2", aTransitionEdge2.getMiddleLabel());
		assertSame(aStateNode1, aTransitionEdge2.getStart());
		assertSame(aStateNode2, aTransitionEdge2.getEnd());
		
		assertEquals("Edge 3", aTransitionEdge3.getMiddleLabel());
		assertSame(aStateNode2, aTransitionEdge3.getStart());
		assertSame(aStateNode1, aTransitionEdge3.getEnd());
		
		assertEquals("Edge 4", aTransitionEdge4.getMiddleLabel());
		assertSame(aStateNode2, aTransitionEdge4.getStart());
		assertSame(aFinalNode, aTransitionEdge4.getEnd());
	}
	
	@Test
	public void testStateDiagramCreateNotes()
	{
		aStateNode1.setName("Node 1");
		addNode(aStateNode1, new Point(30,30));
		addNode(aNoteNode, new Point(130,130));
		
		assertEquals(2, numberOfRootNodes());
		
		// Note edge with a point node not overlapping any nodes
		addEdge(aNoteEdge, new Point(135,135), new Point(300,300));
		assertEquals(3, numberOfRootNodes());
		assertTrue(getRootNode(2) instanceof PointNode);
		assertEquals(1, numberOfEdges());
		
		// Note edge with a point node not overlapping any nodes
		NoteEdge edge2 = new NoteEdge();
		addEdge(edge2, new Point(135,135), new Point(40,40));
		assertEquals(4, numberOfRootNodes());
		assertTrue(getRootNode(3) instanceof PointNode);
		assertEquals(2, numberOfEdges());
		
		// Note edge with a starting point on a node
		NoteEdge edge3 = new NoteEdge();
		addEdge(edge3, new Point(35,35), new Point(135,135));
		assertEquals(4, numberOfRootNodes());
		assertEquals(3, numberOfEdges());
		assertEquals(aStateNode1, edge3.getStart());
		assertEquals(aNoteNode, edge3.getEnd());
	}
	
	@Test
	public void testCreateStateDiagram()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));

		assertEquals(4, numberOfRootNodes());
		
		// test creation of edges, directly link InitialNode to FinalNode is allowed
		addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		assertEquals(5, numberOfEdges());
		
		assertFalse(aBuilder.canAdd(new StateTransitionEdge(), new Point(50, 20), new Point(20, 20)));
		assertFalse(aBuilder.canAdd(new StateTransitionEdge(), new Point(155, 25), new Point(20, 20)));
		assertTrue(aBuilder.canAdd(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20))); // Second
		addEdge(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20));
		assertFalse(aBuilder.canAdd(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20))); // Third
		assertFalse(aBuilder.canAdd(new StateTransitionEdge(), new Point(255, 25), new Point(155, 20)));
		assertFalse(aBuilder.canAdd(new StateTransitionEdge(), new Point(255, 25), new Point(25, 25)));
		assertEquals(6, numberOfEdges());
	}
	
	@Test
	public void testConnectStateNodeWithNoteEdge()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		assertFalse(aBuilder.canAdd(noteEdge1, new Point(25, 25), new Point(55, 25)));
		assertFalse(aBuilder.canAdd(noteEdge2, new Point(55, 25), new Point(155, 25)));
		assertFalse(aBuilder.canAdd(noteEdge3, new Point(155, 25), new Point(255, 25)));
		assertFalse(aBuilder.canAdd(noteEdge4, new Point(155, 25), new Point(55, 25)));
		assertFalse(aBuilder.canAdd(noteEdge5, new Point(25, 25), new Point(255, 25)));
		assertEquals(0, numberOfEdges());
	}
	
	@Test
	public void testConnectNoteNodeWithNoteEdge()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		addNode(aNoteNode, new Point(50, 200));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		addEdge(noteEdge1, new Point(50, 200), new Point(55, 25));
		addEdge(noteEdge2, new Point(50, 200), new Point(155, 25));
		addEdge(noteEdge3, new Point(50, 200), new Point(255, 25));
		addEdge(noteEdge4, new Point(50, 200), new Point(455, 125));
		addEdge(noteEdge5, new Point(50, 200), new Point(2255, -25));
		assertEquals(5, numberOfEdges());
	}
	
	@Test
	public void testConnectStateNodeWithNoteNode()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		addNode(aNoteNode, new Point(50, 200));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		// valid operations
		addEdge(noteEdge1, new Point(20, 20), new Point(50, 200));
		addEdge(noteEdge2, new Point(50, 20), new Point(50, 200));
		addEdge(noteEdge3, new Point(250, 20), new Point(50, 200));
		assertEquals(3, numberOfEdges());
		// invalid operations, cannot connect any StateNode with NoteEdges
		assertFalse(aBuilder.canAdd(noteEdge4, new Point(20, 20), new Point(-20, 200)));
		assertFalse(aBuilder.canAdd(noteEdge5, new Point(150, 20), new Point(-50, 200)));
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(20, 20), new Point(50, 49)));
		assertEquals(3, numberOfEdges());
	}
	
	@Test
	public void testIndividualNodeMovement()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		
		moveNode(aInitialNode, 3, 12);
		moveNode(aStateNode1, -5, 80);
		moveNode(aStateNode2, 15, -30);
		moveNode(aFinalNode, 40, 20);
		
		assertEquals(new Point(23, 32), aInitialNode.position());
		assertEquals(new Point(45, 100), aStateNode1.position());
		assertEquals(new Point(165, -10), aStateNode2.position());
		assertEquals(new Point(290, 40), aFinalNode.position());
	}
	
	@Test
	public void testNodesAndEdgesMovement()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		
		addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		
		select(aInitialNode, aStateNode1, aTransitionEdge1, aTransitionEdge2, aTransitionEdge3);

		moveSelection(26, 37);
		
		assertEquals(new Point(46, 57), aInitialNode.position());
		assertEquals(new Point(76, 57), aStateNode1.position());
		assertEquals(new Point(150, 20), aStateNode2.position());
		assertEquals(new Point(250, 20), aFinalNode.position());
	}
	
	@Test
	public void testRemoveStartNode()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		
		select(aInitialNode);
		deleteSelected();
		
		assertEquals(1, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}
	
	@Test
	public void testRemoveEndNode()
	{
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		
		select(aFinalNode);
		deleteSelected();
		
		assertEquals(1, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}
	
	@Test
	public void testRemoveStateNode()
	{
		addNode(aInitialNode, new Point(20,20));
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		
		addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		
		select(aStateNode2);
		deleteSelected();
		
		assertEquals(3, numberOfRootNodes());
		assertEquals(2, numberOfEdges());

		undo();
		assertEquals(4, numberOfRootNodes());
		assertEquals(5, numberOfEdges());
	}
	
	@Test
	public void testCopyStateNode()
	{
		addNode(aStateNode1, new Point(50,20));
		select(aStateNode1);
		copy();
		paste();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(new Point(50,20), (((StateNode) getRootNode(1)).position()));
	}
	
	@Test
	public void testCutStateNode()
	{
		addNode(aStateNode1, new Point(50,20));
		select(aStateNode1);
		cut();
		assertEquals(0, numberOfRootNodes());
		
		paste();
		
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(50,20), (((StateNode) getRootNode(0)).position()));
	}

	@Test
	public void testCopyNodesWithEdge()
	{
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		
		selectAll();
		copy();
		paste();

		assertEquals(4, numberOfRootNodes());
		assertEquals(2, numberOfEdges());
		assertEquals(new Point(50,20), (((StateNode) getRootNode(2)).position()));
	}
	
	@Test
	public void testCutNodesWithEdge()
	{
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		
		selectAll();
		cut();
		assertEquals(0, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		paste();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(new Point(50,20), (((StateNode) getRootNode(0)).position()));
	}
}

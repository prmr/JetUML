/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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

import org.junit.Before;
import org.junit.Test;

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

	@Before
	public void setup()
	{
		super.setup();
		aDiagram = new StateDiagram();
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
		assertEquals(4, aDiagram.getRootNodes().size());
		
		aTransitionEdge1.setMiddleLabel("Edge 1");
		addEdge(aTransitionEdge1, new Point(6, 6), new Point(35, 35));
		
		aTransitionEdge2.setMiddleLabel("Edge 2");
		addEdge(aTransitionEdge2, new Point(35, 35), new Point(35, 105));
		
		aTransitionEdge3.setMiddleLabel("Edge 3");
		addEdge(aTransitionEdge3, new Point(35, 105), new Point(35, 35));
		
		aTransitionEdge4.setMiddleLabel("Edge 4");
		addEdge(aTransitionEdge4, new Point(35, 105), new Point(32, 202));
		assertEquals(4, aDiagram.getEdges().size());
		
		assertFalse(aDiagram.builder().canAdd(aNoteEdge, new Point(6, 6), new Point(35, 35))); 
		assertFalse(aDiagram.builder().canAdd(aNoteEdge, new Point(35, 35), new Point(35, 105)));
		assertFalse(aDiagram.builder().canAdd(aNoteEdge, new Point(35, 105), new Point(35, 35)));
		assertFalse(aDiagram.builder().canAdd(aNoteEdge, new Point(35, 105), new Point(32, 202)));
		assertEquals(4, aDiagram.getEdges().size());
		
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(new Point(30, 30), aStateNode1.position());
		assertEquals("Node 1", aStateNode1.getName());
		assertEquals(new Point(30, 100), aStateNode2.position());
		assertEquals("Node 2", aStateNode2.getName());
		assertEquals(new Point(5, 5), aInitialNode.position());
		assertEquals(new Point(30, 200), aFinalNode.position());
		
		assertEquals(4, aDiagram.getEdges().size());
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
		
		assertEquals(2, aDiagram.getRootNodes().size());
		
		// Note edge with a point node not overlapping any nodes
		addEdge(aNoteEdge, new Point(135,135), new Point(300,300));
		assertEquals(3, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.getRootNodes().toArray(new Node[4])[2] instanceof PointNode);
		assertEquals(1, aDiagram.getEdges().size());
		
		// Note edge with a point node not overlapping any nodes
		NoteEdge edge2 = new NoteEdge();
		addEdge(edge2, new Point(135,135), new Point(40,40));
		assertEquals(4, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.getRootNodes().toArray(new Node[4])[3] instanceof PointNode);
		assertEquals(2, aDiagram.getEdges().size());
		
		// Note edge with a starting point on a node
		NoteEdge edge3 = new NoteEdge();
		addEdge(edge3, new Point(35,35), new Point(135,135));
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(3, aDiagram.getEdges().size());
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

		assertEquals(4, aDiagram.getRootNodes().size());
		
		// test creation of edges, directly link InitialNode to FinalNode is allowed
		addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		assertEquals(5, aDiagram.getEdges().size());
		
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(50, 20), new Point(20, 20)));
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(155, 25), new Point(20, 20)));
		assertTrue(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20))); // Second
		aDiagram.builder().addEdge(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20));
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20))); // Third
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(255, 25), new Point(155, 20)));
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(255, 25), new Point(25, 25)));
		assertEquals(6, aDiagram.getEdges().size());
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
		
		assertFalse(aDiagram.builder().canAdd(noteEdge1, new Point(25, 25), new Point(55, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge2, new Point(55, 25), new Point(155, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge3, new Point(155, 25), new Point(255, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge4, new Point(155, 25), new Point(55, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge5, new Point(25, 25), new Point(255, 25)));
		assertEquals(0, aDiagram.getEdges().size());
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
		assertEquals(5, aDiagram.getEdges().size());
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
		aDiagram.builder().addEdge(noteEdge1, new Point(20, 20), new Point(50, 200));
		aDiagram.builder().addEdge(noteEdge2, new Point(50, 20), new Point(50, 200));
		aDiagram.builder().addEdge(noteEdge3, new Point(250, 20), new Point(50, 200));
		assertEquals(3, aDiagram.getEdges().size());
		// invalid operations, cannot connect any StateNode with NoteEdges
		assertFalse(aDiagram.builder().canAdd(noteEdge4, new Point(20, 20), new Point(-20, 200)));
		assertFalse(aDiagram.builder().canAdd(noteEdge5, new Point(150, 20), new Point(-50, 200)));
		assertFalse(aDiagram.builder().canAdd(new NoteEdge(), new Point(20, 20), new Point(50, 49)));
		assertEquals(3, aDiagram.getEdges().size());
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
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	@Test
	public void testRemoveEndNode()
	{
		addNode(aStateNode2, new Point(150,20));
		addNode(aFinalNode, new Point(250,20));
		addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		
		select(aFinalNode);
		deleteSelected();
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
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
		
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());

		undo();
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(5, aDiagram.getEdges().size());
	}
	
	@Test
	public void testCopyStateNode()
	{
		addNode(aStateNode1, new Point(50,20));
		select(aStateNode1);
		copy();
		paste();
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(new Point(0, 0), (((StateNode) aDiagram.getRootNodes().toArray()[1]).position()));
	}
	
	@Test
	public void testCutStateNode()
	{
		addNode(aStateNode1, new Point(50,20));
		select(aStateNode1);
		cut();
		assertEquals(0, aDiagram.getRootNodes().size());
		
		paste();
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Point(0, 0), (((StateNode) aDiagram.getRootNodes().toArray()[0]).position()));
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

		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Point(0, 0), (((StateNode) aDiagram.getRootNodes().toArray()[2]).position()));
	}
	
	@Test
	public void testCutNodesWithEdge()
	{
		addNode(aStateNode1, new Point(50,20));
		addNode(aStateNode2, new Point(150,20));
		addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		
		selectAll();
		cut();
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		paste();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Point(0, 0), (((StateNode) aDiagram.getRootNodes().toArray()[0]).position()));
	}
}

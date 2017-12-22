/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.graph.nodes.AbstractNode;
import ca.mcgill.cs.jetuml.graph.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.graph.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.StateNode;
import ca.mcgill.cs.jetuml.gui.GraphPanel;
import ca.mcgill.cs.jetuml.gui.ToolBar;

/**
 * Tests various interactions with State Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for State Diagram.
 * 
 * @author Jiajun Chen
 * @author Martin P. Robillard - Modifications to Clipboard API
 */
public class TestUsageScenariosStateDiagram 
{
	private StateDiagramGraph aDiagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private UserCreatedNode aStateNode1;
	private UserCreatedNode aStateNode2;
	private UserCreatedNode aInitialNode;
	private UserCreatedNode aFinalNode;
	private StateTransitionEdge aTransitionEdge1;
	private StateTransitionEdge aTransitionEdge2;
	private StateTransitionEdge aTransitionEdge3;
	private StateTransitionEdge aTransitionEdge4;
	private StateTransitionEdge aTransitionEdge5;

	private static final Function<UserCreatedNode, Rectangle> GET_BOUNDS =
			(node) -> node.aNode.view().getBounds();
	private static final Function<UserCreatedNode, Point> GET_CENTER =
			GET_BOUNDS.andThen((rectangle) -> rectangle.getCenter());
	private static final Supplier<UserCreatedNode> NOTE_NODE_PROVIDER =
			() -> new UserCreatedNode(new NoteNode(), new Point(50, 200));

	private static class UserCreatedNode
	{
		private final AbstractNode aNode;
		private final Point aInitialPosition;

		private UserCreatedNode(AbstractNode pNode, Point pInitialPoint)
		{
			aNode = pNode;
			aInitialPosition = pInitialPoint;
		}

	}

	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		aDiagram = new StateDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram));
		aStateNode1 = new UserCreatedNode(new StateNode(), new Point(50, 20));
		aStateNode2 = new UserCreatedNode(new StateNode(), new Point(150, 20));
		aInitialNode = new UserCreatedNode(new InitialStateNode(), new Point(20, 20));
		aFinalNode = new UserCreatedNode(new FinalStateNode(), new Point(250, 20));
		aTransitionEdge1 = new StateTransitionEdge();
		aTransitionEdge2 = new StateTransitionEdge();
		aTransitionEdge3 = new StateTransitionEdge();
		aTransitionEdge4 = new StateTransitionEdge();
		aTransitionEdge5 = new StateTransitionEdge();
	}
	
	@Test()
	public void testReconnectStatesWithPreExistingHorizontalEdgeShouldNotInduceDivisionByZeroException()
	{
		createSampleDiagram(aStateNode1, aStateNode2);
		createInterStateTransition(aStateNode1, aStateNode2);
		aStateNode1.aNode.translate(10, 0);
		createInterStateTransition(aStateNode1, aStateNode2);
	}

	/**
	 * Below are methods testing basic nodes and edge creation
	 * for a state diagram.
	 *
	 * Testing create a state diagram.
	 */
	@Test
	public void testCreateStateDiagram()
	{
		// test creation of nodes
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		assertEquals(4, aDiagram.getRootNodes().size());
		
		// test creation of edges, directly link InitialNode to FinalNode is allowed
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		assertEquals(5, aDiagram.getEdges().size());
		
		/*
		 *  link from StateNode to InitialNode, from FinalNode to StateNode
		 *  and InitialNode are not allowed
		 */
		aDiagram.addEdge(new StateTransitionEdge(), new Point(50, 20), new Point(20, 20));
		aDiagram.addEdge(new StateTransitionEdge(), new Point(155, 25), new Point(20, 20));
		aDiagram.addEdge(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20)); // Second
		aDiagram.addEdge(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20)); // Third
		aDiagram.addEdge(new StateTransitionEdge(), new Point(255, 25), new Point(155, 20));
		aDiagram.addEdge(new StateTransitionEdge(), new Point(255, 25), new Point(25, 25));
		assertEquals(6, aDiagram.getEdges().size());
		
		// test labeling edges
		aTransitionEdge1.setMiddleLabel("start");
		aTransitionEdge2.setMiddleLabel("forward");
		aTransitionEdge3.setMiddleLabel("finish");
		aTransitionEdge4.setMiddleLabel("reverse");
		aTransitionEdge5.setMiddleLabel("crash");
		assertEquals("start", aTransitionEdge1.getMiddleLabel());
		assertEquals("forward", aTransitionEdge2.getMiddleLabel());
		assertEquals("finish", aTransitionEdge3.getMiddleLabel());
		assertEquals("reverse", aTransitionEdge4.getMiddleLabel());
		assertEquals("crash", aTransitionEdge5.getMiddleLabel());
	}
	
	/**
	 * Testing connect any StateNode with NoteEdge (not allowed).
	 */
	@Test
	public void testConnectStateNodeWithNoteEdge()
	{
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		aDiagram.addEdge(noteEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(noteEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(noteEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(noteEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(noteEdge5, new Point(25, 25), new Point(255, 25));
		assertEquals(0, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing connect NoteNode with NoteEdge.
	 */
	@Test
	public void testConnectNoteNodeWithNoteEdge()
	{
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode, NOTE_NODE_PROVIDER.get());
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		aDiagram.addEdge(noteEdge1, new Point(50, 200), new Point(55, 25));
		aDiagram.addEdge(noteEdge2, new Point(50, 200), new Point(155, 25));
		aDiagram.addEdge(noteEdge3, new Point(50, 200), new Point(255, 25));
		aDiagram.addEdge(noteEdge4, new Point(50, 200), new Point(455, 125));
		aDiagram.addEdge(noteEdge5, new Point(50, 200), new Point(2255, -25));
		assertEquals(5, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing connect any StateNode with NoteNode
	 */
	@Test
	public void testConnectStateNodeWithNoteNode()
	{
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode, NOTE_NODE_PROVIDER.get());
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		// valid operations
		aDiagram.addEdge(noteEdge1, new Point(20, 20), new Point(50, 200));
		aDiagram.addEdge(noteEdge2, new Point(50, 20), new Point(50, 200));
		aDiagram.addEdge(noteEdge3, new Point(250, 20), new Point(50, 200));
		assertEquals(3, aDiagram.getEdges().size());
		// invalid operations, cannot connect any StateNode with NoteEdges
		aDiagram.addEdge(noteEdge4, new Point(20, 20), new Point(-20, 200));
		aDiagram.addEdge(noteEdge5, new Point(150, 20), new Point(-50, 200));
		aDiagram.addEdge(new NoteEdge(), new Point(20, 20), new Point(50, 49));
		assertEquals(3, aDiagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing nodes movement.
	 * 
	 * 
	 * 
	 * Testing individual node movement.
	 */
	@Test
	public void testIndividualNodeMovement()
	{
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		aInitialNode.aNode.translate(3, 12);
		aStateNode1.aNode.translate(-5, 80);
		aStateNode2.aNode.translate(15, -30);
		aFinalNode.aNode.translate(40, 20);
		
		assertEquals(new Rectangle(23, 32, 20, 20), GET_BOUNDS.apply(aInitialNode));
		assertEquals(new Rectangle(45, 100, 80, 60), GET_BOUNDS.apply(aStateNode1));
		assertEquals(new Rectangle(165, -10, 80, 60), GET_BOUNDS.apply(aStateNode2));
		assertEquals(new Rectangle(290, 40, 20, 20), GET_BOUNDS.apply(aFinalNode));
	}
	
	/**
	 * Testing nodes and edge movement.
	 */
	@Test
	public void testNodesAndEdgesMovement()
	{
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		
		aPanel.getSelectionList().add(aInitialNode.aNode);
		aPanel.getSelectionList().add(aStateNode1.aNode);
		aPanel.getSelectionList().add(aTransitionEdge1);
		aPanel.getSelectionList().add(aTransitionEdge2);
		aPanel.getSelectionList().add(aTransitionEdge3);

		Rectangle aTransitionEdge1Bounds = aTransitionEdge1.view().getBounds();
		Rectangle aTransitionEdge2Bounds = aTransitionEdge2.view().getBounds();
		Rectangle aTransitionEdge3Bounds = aTransitionEdge3.view().getBounds();

		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		assertEquals(new Rectangle(46, 57, 20, 20), GET_BOUNDS.apply(aInitialNode));
		assertEquals(new Rectangle(76, 57, 80, 60), GET_BOUNDS.apply(aStateNode1));
		assertEquals(new Rectangle(150, 20, 80, 60), GET_BOUNDS.apply(aStateNode2));
		assertEquals(new Rectangle(250, 20, 20, 20), GET_BOUNDS.apply(aFinalNode));
		assertEquals(aInitialNode.aNode, aTransitionEdge1.getStart());
		assertEquals(aStateNode1.aNode, aTransitionEdge1.getEnd());
		assertEquals(aStateNode1.aNode, aTransitionEdge2.getStart());
		assertEquals(aStateNode2.aNode, aTransitionEdge2.getEnd());
		/*
		 *  if either start or end node is moved,
		 *  the edge bounds would be changed
		 */
		assertFalse(aTransitionEdge1Bounds == aTransitionEdge1.view().getBounds());
		assertFalse(aTransitionEdge2Bounds == aTransitionEdge2.view().getBounds());
		/*
		 *  if both the start and end node are not moved,
		 *  the edge should have the same bounds
		 */
		assertEquals(aTransitionEdge3Bounds, aTransitionEdge3.view().getBounds());
	}
	
	/**
	 * Below are methods testing deletion and undo feature for state diagram.
	 * 
	 * 
	 * Testing delete a start node with an attached edge.
	 */
	@Test
	public void testRemoveStartNode()
	{
		createSampleDiagram(aInitialNode, aStateNode1);
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aPanel.getSelectionList().add(aInitialNode.aNode);
		aPanel.removeSelected();
		drawSampleDiagram();
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.undo();
		drawSampleDiagram();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a end node with an attached edge.
	 */
	@Test
	public void testRemoveEndNode()
	{
		createSampleDiagram(aStateNode2, aFinalNode);
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aPanel.getSelectionList().add(aFinalNode.aNode);
		aPanel.removeSelected();
		drawSampleDiagram();
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.undo();
		drawSampleDiagram();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a end node with an attached edge.
	 */
	@Test
	public void testRemoveStateNode()
	{
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		aPanel.getSelectionList().add(aStateNode2.aNode);
		aPanel.removeSelected();
		drawSampleDiagram();
		
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());

		aPanel.undo();
		drawSampleDiagram();
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(5, aDiagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing copy and paste feature for state diagram.
	 * 
	 * 
	 * 
	 * Testing copy a StateNode.
	 */
	@Test
	public void testCopyStateNode()
	{
		createSampleDiagram(aStateNode1);
		aPanel.getSelectionList().add(aStateNode1.aNode);
		aPanel.copy();
		aPanel.paste();
		drawSampleDiagram();
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(0, 0, 80, 60),
				(((StateNode) aDiagram.getRootNodes().toArray()[1]).view().getBounds()));
	}
	
	/**
	 * 
	 * Testing cut a StateNode.
	 */
	@Test
	public void testCutStateNode()
	{
		createSampleDiagram(aStateNode1);
		aPanel.getSelectionList().add(aStateNode1.aNode);
		aPanel.cut();
		drawSampleDiagram();
		assertEquals(0, aDiagram.getRootNodes().size());
		
		aPanel.paste();
		drawSampleDiagram();
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(0, 0, 80, 60),
				(((StateNode) aDiagram.getRootNodes().toArray()[0]).view().getBounds()));
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge.
	 */
	@Test
	public void testCopyNodesWithEdge()
	{
		createSampleDiagram(aStateNode1, aStateNode2);
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aPanel.selectAll();
		aPanel.copy();
		aPanel.paste();

		drawSampleDiagram();
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Rectangle(0, 0, 80, 60),
				(((StateNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds()));
	}
	
	/**
	 * 
	 * Testing cut and paste two nodes with an edge.
	 */
	@Test
	public void testCutNodesWithEdge()
	{
		createSampleDiagram(aStateNode1, aStateNode2);
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		
		aPanel.selectAll();
		aPanel.cut();
		drawSampleDiagram();
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.paste();
		drawSampleDiagram();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Rectangle(0, 0, 80, 60),
				(((StateNode) aDiagram.getRootNodes().toArray()[0]).view().getBounds()));
	}

	private void createInterStateTransition(UserCreatedNode pFrom, UserCreatedNode pTo)
	{
		aDiagram.addEdge(new StateTransitionEdge(), GET_CENTER.apply(pFrom), GET_CENTER.apply(pTo));
		drawSampleDiagram();
	}

	private void createSampleDiagram(UserCreatedNode... nodes)
	{
		for (UserCreatedNode n : nodes)
		{
			aDiagram.addNode(n.aNode, n.aInitialPosition);
		}

		drawSampleDiagram();
	}

	private void drawSampleDiagram()
	{
		aDiagram.draw(aGraphics);
	}

}

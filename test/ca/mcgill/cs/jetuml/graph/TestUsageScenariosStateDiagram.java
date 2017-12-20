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

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.StateTransitionEdge;
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
	private StateNode aStateNode1;
	private StateNode aStateNode2;
	private InitialStateNode aInitialNode;
	private FinalStateNode aFinalNode;
	private StateTransitionEdge aTransitionEdge1;
	private StateTransitionEdge aTransitionEdge2;
	private StateTransitionEdge aTransitionEdge3;
	private StateTransitionEdge aTransitionEdge4;
	private StateTransitionEdge aTransitionEdge5;

	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		aDiagram = new StateDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram));
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
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.draw(aGraphics);		
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
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.addNode(noteNode, new Point(50, 200));
		aDiagram.draw(aGraphics);		
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
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.addNode(noteNode, new Point(50, 200));
		aDiagram.draw(aGraphics);		
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
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.draw(aGraphics);

		aInitialNode.translate(3, 12);
		aStateNode1.translate(-5, 80);
		aStateNode2.translate(15, -30);
		aFinalNode.translate(40, 20);
		
		assertEquals(new Rectangle(23, 32, 20, 20), aInitialNode.view().getBounds());
		assertEquals(new Rectangle(45, 100, 80, 60), aStateNode1.view().getBounds());
		assertEquals(new Rectangle(165, -10, 80, 60), aStateNode2.view().getBounds());
		assertEquals(new Rectangle(290, 40, 20, 20), aFinalNode.view().getBounds());
	}
	
	/**
	 * Testing nodes and edge movement.
	 */
	@Test
	public void testNodesAndEdgesMovement()
	{
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.draw(aGraphics);
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		
		aPanel.getSelectionList().add(aInitialNode);
		aPanel.getSelectionList().add(aStateNode1);
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
		assertEquals(new Rectangle(46, 57, 20, 20), aInitialNode.view().getBounds());
		assertEquals(new Rectangle(76, 57, 80, 60), aStateNode1.view().getBounds());
		assertEquals(new Rectangle(150, 20, 80, 60), aStateNode2.view().getBounds());
		assertEquals(new Rectangle(250, 20, 20, 20), aFinalNode.view().getBounds());
		assertEquals(aInitialNode, aTransitionEdge1.getStart());
		assertEquals(aStateNode1, aTransitionEdge1.getEnd());
		assertEquals(aStateNode1, aTransitionEdge2.getStart());
		assertEquals(aStateNode2, aTransitionEdge2.getEnd());
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
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aPanel.getSelectionList().add(aInitialNode);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a end node with an attached edge.
	 */
	@Test
	public void testRemoveEndNode()
	{
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aPanel.getSelectionList().add(aFinalNode);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a end node with an attached edge.
	 */
	@Test
	public void testRemoveStateNode()
	{
		aDiagram.addNode(aInitialNode, new Point(20, 20));
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.addNode(aFinalNode, new Point(250, 20));
		aDiagram.draw(aGraphics);
		aDiagram.addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		aPanel.getSelectionList().add(aStateNode2);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());

		aPanel.undo();
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.draw(aGraphics);
		aPanel.getSelectionList().add(aStateNode1);
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
		
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
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.draw(aGraphics);
		aPanel.getSelectionList().add(aStateNode1);
		aPanel.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		
		aPanel.paste();
		aDiagram.draw(aGraphics);
		
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
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.draw(aGraphics);
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aPanel.selectAll();
		aPanel.copy();
		aPanel.paste();

		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aStateNode1, new Point(50, 20));
		aDiagram.addNode(aStateNode2, new Point(150, 20));
		aDiagram.draw(aGraphics);
		aDiagram.addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		
		aPanel.selectAll();
		aPanel.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Rectangle(0, 0, 80, 60),
				(((StateNode) aDiagram.getRootNodes().toArray()[0]).view().getBounds()));
	}
}

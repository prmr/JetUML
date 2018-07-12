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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.AbstractNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvas;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.gui.DiagramTabToolBar;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Tests various interactions with State Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for State Diagram.
 */
public class TestUsageScenariosStateDiagram 
{
	private StateDiagram aDiagram;
	private GraphicsContext aGraphics;
	private DiagramCanvas aPanel;
	private DiagramCanvasController aController;
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
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		aDiagram = new StateDiagram();
		aGraphics = new Canvas(256, 256).getGraphicsContext2D();
		aPanel = new DiagramCanvas(aDiagram, 0, 0);
		aController = new DiagramCanvasController(aPanel, new DiagramTabToolBar(aDiagram), a ->  {});
		aPanel.setController(aController);
		aStateNode1 = new StateNode();
		aStateNode1.moveTo(new Point(50, 20));
		aStateNode2 = new StateNode();
		aStateNode2.moveTo(new Point(150, 20));
		aInitialNode = new InitialStateNode();
		aInitialNode.moveTo(new Point(20, 20));
		aFinalNode = new FinalStateNode();
		aFinalNode.moveTo(new Point(250, 20));
		aTransitionEdge1 = new StateTransitionEdge();
		aTransitionEdge2 = new StateTransitionEdge();
		aTransitionEdge3 = new StateTransitionEdge();
		aTransitionEdge4 = new StateTransitionEdge();
		aTransitionEdge5 = new StateTransitionEdge();
	}
	
	@Test
	public void testStateDiagramCreate()
	{
		// Create a state diagram with two state nodes, one start node, one end node
		StateDiagram diagram = new StateDiagram();
		StateNode node1 = new StateNode();
		node1.setName("Node 1");
		StateNode node2 = new StateNode();
		node2.setName("Node 2");
		InitialStateNode start = new InitialStateNode();
		FinalStateNode end = new FinalStateNode();
		diagram.builder().addNode(node1, new Point(30,30), Integer.MAX_VALUE, Integer.MAX_VALUE);
		diagram.builder().addNode(node2, new Point(30, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		diagram.builder().addNode(start, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		diagram.builder().addNode(end, new Point(30, 200), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(4, diagram.getRootNodes().size());
		
		// Add edges between all of these, including back-and-forth between two states. 
		StateTransitionEdge edge1 = new StateTransitionEdge();
		edge1.setMiddleLabel("Edge 1");
		diagram.builder().addEdge(edge1, new Point(6, 6), new Point(35, 35));
		
		StateTransitionEdge edge2 = new StateTransitionEdge();
		edge2.setMiddleLabel("Edge 2");
		diagram.builder().addEdge(edge2, new Point(35, 35), new Point(35, 105));
		
		StateTransitionEdge edge3 = new StateTransitionEdge();
		edge3.setMiddleLabel("Edge 3");
		diagram.builder().addEdge(edge3, new Point(35, 105), new Point(35, 35));
		
		StateTransitionEdge edge4 = new StateTransitionEdge();
		edge4.setMiddleLabel("Edge 4");
		diagram.builder().addEdge(edge4, new Point(35, 105), new Point(32, 202));
		assertEquals(4, diagram.getEdges().size());
		
		NoteEdge noteEdge = new NoteEdge();
		assertFalse(diagram.builder().canAdd(noteEdge, new Point(6, 6), new Point(35, 35))); 
		assertFalse(diagram.builder().canAdd(noteEdge, new Point(35, 35), new Point(35, 105)));
		assertFalse(diagram.builder().canAdd(noteEdge, new Point(35, 105), new Point(35, 35)));
		assertFalse(diagram.builder().canAdd(noteEdge, new Point(35, 105), new Point(32, 202)));
		assertEquals(4, diagram.getEdges().size());
		
		// VALIDATION NODES
		assertEquals(4, diagram.getRootNodes().size());
		assertEquals(new Rectangle(30, 30, 80, 60), node1.view().getBounds());
		assertEquals("Node 1", node1.getName());
		assertEquals(new Rectangle(30, 100, 80, 60), node2.view().getBounds());
		assertEquals("Node 2", node2.getName());
		assertEquals(new Rectangle(5, 5, 20, 20), start.view().getBounds());
		assertEquals(new Rectangle(30, 200, 20, 20), end.view().getBounds());
		
		// VALIDATION EDGES
		assertEquals(4, diagram.getEdges().size());
		
		assertEquals(new Rectangle(21, 19, 18, 11), edge1.view().getBounds());
		assertEquals("Edge 1", edge1.getMiddleLabel());
		assertEquals(start, edge1.getStart());
		assertEquals(node1, edge1.getEnd());
		
		assertEquals(new Rectangle(70, 88, 2, 12), edge2.view().getBounds());
		assertEquals("Edge 2", edge2.getMiddleLabel());
		assertEquals(node1, edge2.getStart());
		assertEquals(node2, edge2.getEnd());
		
		assertEquals(new Rectangle(65, 88, 2, 12), edge3.view().getBounds());
		assertEquals("Edge 3", edge3.getMiddleLabel());
		assertEquals(node2, edge3.getStart());
		assertEquals(node1, edge3.getEnd());
		
		assertEquals(new Rectangle(42, 158, 19, 43), edge4.view().getBounds());
		assertEquals("Edge 4", edge4.getMiddleLabel());
		assertEquals(node2, edge4.getStart());
		assertEquals(end, edge4.getEnd());
	}
	
	@Test
	public void testStateDiagramCreateNotes() throws Exception
	{
		// Create a state diagram with two state nodes, one start node, one end node
		StateDiagram diagram = new StateDiagram();
		StateNode node1 = new StateNode();
		node1.setName("Node 1");
		diagram.builder().addNode(node1, new Point(30,30), Integer.MAX_VALUE, Integer.MAX_VALUE);
		NoteNode note = new NoteNode();
		diagram.builder().addNode(note, new Point(130,130), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(2, diagram.getRootNodes().size());
		// Note edge with a point node not overlapping any nodes
		NoteEdge edge1 = new NoteEdge();
		diagram.builder().addEdge(edge1, new Point(135,135), new Point(300,300));
		assertEquals(3, diagram.getRootNodes().size());
		assertTrue(diagram.getRootNodes().toArray(new Node[4])[2] instanceof PointNode);
		assertEquals(1, diagram.getEdges().size());
		
		// Note edge with a point node overlapping any nodes
		NoteEdge edge2 = new NoteEdge();
		diagram.builder().addEdge(edge2, new Point(135,135), new Point(40,40));
		assertEquals(4, diagram.getRootNodes().size());
		assertTrue(diagram.getRootNodes().toArray(new Node[4])[3] instanceof PointNode);
		assertEquals(2, diagram.getEdges().size());
		
		// Note edge with a starting point on a node
		NoteEdge edge3 = new NoteEdge();
		diagram.builder().addEdge(edge3, new Point(35,35), new Point(135,135));
		assertEquals(4, diagram.getRootNodes().size());
		assertTrue(diagram.getRootNodes().toArray(new Node[4])[3] instanceof PointNode);
		assertEquals(3, diagram.getEdges().size());
		assertEquals(node1, edge3.getStart());
		assertEquals(note, edge3.getEnd());
	}
	
	@Test
	public void testReconnectStatesWithPreExistingHorizontalEdgeShouldNotInduceDivisionByZeroException()
	{
		createSampleDiagram(aStateNode1, aStateNode2);
		aDiagram.restoreEdge(new StateTransitionEdge(), aStateNode1, aStateNode2);
		aDiagram.draw(aGraphics);
		aDiagram.restoreEdge(new StateTransitionEdge(), aStateNode1, aStateNode2);
		aDiagram.draw(aGraphics);
	}

	@Test
	public void testDuplicateTransitionEdgeNotAllowed()
	{
		createSampleDiagram(aStateNode1, aFinalNode);
		assertEquals(0, aDiagram.getEdges().size());
		aDiagram.restoreEdge(new StateTransitionEdge(), aStateNode1, aFinalNode);
		assertEquals(1, aDiagram.getEdges().size());
		aFinalNode.translate(0, 100);
		aDiagram.restoreEdge(new StateTransitionEdge(), aStateNode1, aFinalNode);
		assertEquals(2, aDiagram.getEdges().size());
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
		aDiagram.builder().addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.builder().addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.builder().addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.builder().addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.builder().addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		assertEquals(5, aDiagram.getEdges().size());
		
		/*
		 *  link from StateNode to InitialNode, from FinalNode to StateNode
		 *  and InitialNode are not allowed
		 */
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(50, 20), new Point(20, 20)));
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(155, 25), new Point(20, 20)));
		assertTrue(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20))); // Second
		aDiagram.builder().addEdge(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20));
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(50, 25), new Point(155, 20))); // Third
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(255, 25), new Point(155, 20)));
		assertFalse(aDiagram.builder().canAdd(new StateTransitionEdge(), new Point(255, 25), new Point(25, 25)));
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
		
		assertFalse(aDiagram.builder().canAdd(noteEdge1, new Point(25, 25), new Point(55, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge2, new Point(55, 25), new Point(155, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge3, new Point(155, 25), new Point(255, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge4, new Point(155, 25), new Point(55, 25)));
		assertFalse(aDiagram.builder().canAdd(noteEdge5, new Point(25, 25), new Point(255, 25)));
		assertEquals(0, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing connect NoteNode with NoteEdge.
	 */
	@Test
	public void testConnectNoteNodeWithNoteEdge()
	{
		NoteNode note = new NoteNode();
		note.moveTo(new Point(50, 200));
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode, note);
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		
		aDiagram.builder().addEdge(noteEdge1, new Point(50, 200), new Point(55, 25));
		aDiagram.builder().addEdge(noteEdge2, new Point(50, 200), new Point(155, 25));
		aDiagram.builder().addEdge(noteEdge3, new Point(50, 200), new Point(255, 25));
		aDiagram.builder().addEdge(noteEdge4, new Point(50, 200), new Point(455, 125));
		aDiagram.builder().addEdge(noteEdge5, new Point(50, 200), new Point(2255, -25));
		assertEquals(5, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing connect any StateNode with NoteNode
	 */
	@Test
	public void testConnectStateNodeWithNoteNode()
	{
		NoteNode note = new NoteNode();
		note.moveTo(new Point(50, 200));
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode, note);
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
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		aDiagram.builder().addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.builder().addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.builder().addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.builder().addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.builder().addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		
		aController.getSelectionModel().addToSelection(aInitialNode);
		aController.getSelectionModel().addToSelection(aStateNode1);
		aController.getSelectionModel().addToSelection(aTransitionEdge1);
		aController.getSelectionModel().addToSelection(aTransitionEdge2);
		aController.getSelectionModel().addToSelection(aTransitionEdge3);

		Rectangle aTransitionEdge1Bounds = aTransitionEdge1.view().getBounds();
		Rectangle aTransitionEdge2Bounds = aTransitionEdge2.view().getBounds();
		Rectangle aTransitionEdge3Bounds = aTransitionEdge3.view().getBounds();

		for(DiagramElement element: aController.getSelectionModel())
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
		createSampleDiagram(aInitialNode, aStateNode1);
		aDiagram.builder().addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aController.getSelectionModel().addToSelection(aInitialNode);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aController.undo();
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
		createSampleDiagram(aStateNode2, aFinalNode);
		aDiagram.builder().addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aController.getSelectionModel().addToSelection(aFinalNode);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aController.undo();
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
		createSampleDiagram(aInitialNode, aStateNode1, aStateNode2, aFinalNode);
		aDiagram.builder().addEdge(aTransitionEdge1, new Point(25, 25), new Point(55, 25));
		aDiagram.builder().addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aDiagram.builder().addEdge(aTransitionEdge3, new Point(155, 25), new Point(255, 25));
		aDiagram.builder().addEdge(aTransitionEdge4, new Point(155, 25), new Point(55, 25));
		aDiagram.builder().addEdge(aTransitionEdge5, new Point(25, 25), new Point(255, 25));
		aController.getSelectionModel().addToSelection(aStateNode2);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());

		aController.undo();
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
		createSampleDiagram(aStateNode1);
		aController.getSelectionModel().addToSelection(aStateNode1);
		aController.copy();
		aController.paste();
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
		createSampleDiagram(aStateNode1);
		aController.getSelectionModel().addToSelection(aStateNode1);
		aController.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		
		aController.paste();
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
		createSampleDiagram(aStateNode1, aStateNode2);
		aDiagram.builder().addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		aController.selectAll();
		aController.copy();
		aController.paste();

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
		createSampleDiagram(aStateNode1, aStateNode2);
		aDiagram.builder().addEdge(aTransitionEdge2, new Point(55, 25), new Point(155, 25));
		
		aController.selectAll();
		aController.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aController.paste();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Rectangle(0, 0, 80, 60),
				(((StateNode) aDiagram.getRootNodes().toArray()[0]).view().getBounds()));
	}

	private void createSampleDiagram(AbstractNode... pNodes)
	{
		for (AbstractNode n : pNodes)
		{
			aDiagram.restoreRootNode(n);
		}
		aDiagram.draw(aGraphics);
	}

}

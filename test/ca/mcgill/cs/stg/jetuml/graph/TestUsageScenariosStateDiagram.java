package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * Tests various interactions with State Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for State Diagram.
 * 
 * @author Jiajun Chen
 *
 */

public class TestUsageScenariosStateDiagram 
{
	private StateDiagramGraph diagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard clipboard;
	private StateNode stateNode1;
	private StateNode stateNode2;
	private CircularStateNode initialNode;
	private CircularStateNode finalNode;
	private StateTransitionEdge edge1;
	private StateTransitionEdge edge2;
	private StateTransitionEdge edge3;
	private StateTransitionEdge edge4;
	private StateTransitionEdge edge5;

	
	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		diagram = new StateDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGrid = new Grid();
		clipboard = new Clipboard();
		stateNode1 = new StateNode();
		stateNode2 = new StateNode();
		initialNode = new CircularStateNode();
		initialNode.setFinal(false);
		finalNode = new CircularStateNode();
		finalNode.setFinal(true);
		edge1 = new StateTransitionEdge();
		edge2 = new StateTransitionEdge();
		edge3 = new StateTransitionEdge();
		edge4 = new StateTransitionEdge();
		edge5 = new StateTransitionEdge();

	}
	
	/**
	 * Below are methods testing basic nodes and edge creation
	 * for a state diagram
	 * 
	 * 
	 * test create a state diagram
	 */
	@Test
	public void testCreateStateDiagram()
	{
		// test creation of nodes
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.draw(aGraphics, aGrid);
		assertEquals(4, diagram.getRootNodes().size());
		
		// test creation of edges, directly link InitialNode to FinalNode is allowed
		diagram.addEdge(edge1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
		diagram.addEdge(edge2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		diagram.addEdge(edge3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
		diagram.addEdge(edge4, new Point2D.Double(155, 25), new Point2D.Double(55, 25));
		diagram.addEdge(edge5, new Point2D.Double(25, 25), new Point2D.Double(255, 25));
		assertEquals(5, diagram.getEdges().size());
		
		/*
		 *  link from StateNode to InitialNode, from FinalNode to StateNode
		 *  and InitialNode are not allowed
		 */
		diagram.addEdge(new StateTransitionEdge(), new Point2D.Double(50, 20), new Point2D.Double(20, 20));
		diagram.addEdge(new StateTransitionEdge(), new Point2D.Double(155, 25), new Point2D.Double(20, 20));
		diagram.addEdge(new StateTransitionEdge(), new Point2D.Double(50, 25), new Point2D.Double(155, 20));
		diagram.addEdge(new StateTransitionEdge(), new Point2D.Double(255, 25), new Point2D.Double(155, 20));
		diagram.addEdge(new StateTransitionEdge(), new Point2D.Double(255, 25), new Point2D.Double(25, 25));
		assertEquals(5, diagram.getEdges().size());
		
		// test labeling edges
		edge1.setLabel("start");
		edge2.setLabel("forward");
		edge3.setLabel("finish");
		edge4.setLabel("reverse");
		edge5.setLabel("crash");
		assertEquals("start", edge1.getLabel());
		assertEquals("forward", edge2.getLabel());
		assertEquals("finish", edge3.getLabel());
		assertEquals("reverse", edge4.getLabel());
		assertEquals("crash", edge5.getLabel());
	}
	
	/**
	 * Testing connect any StateNode with NoteEdge (not allowed).
	 */
	@Test
	public void testConnectStateNodeWithNoteEdge()
	{
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.draw(aGraphics, aGrid);		
		NoteEdge note1 = new NoteEdge();
		NoteEdge note2 = new NoteEdge();
		NoteEdge note3 = new NoteEdge();
		NoteEdge note4 = new NoteEdge();
		NoteEdge note5 = new NoteEdge();
		
		diagram.addEdge(note1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
		diagram.addEdge(note2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		diagram.addEdge(note3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
		diagram.addEdge(note4, new Point2D.Double(155, 25), new Point2D.Double(55, 25));
		diagram.addEdge(note5, new Point2D.Double(25, 25), new Point2D.Double(255, 25));
		assertEquals(0, diagram.getEdges().size());
	}
	
	/**
	 * Testing connect any NoteNode with NoteEdge (not allowed).
	 */
	@Test
	public void testConnectNoteNodeWithNoteEdge()
	{
		NoteNode noteNode = new NoteNode();
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.addNode(noteNode, new Point2D.Double(50, 200));
		diagram.draw(aGraphics, aGrid);		
		NoteEdge note1 = new NoteEdge();
		NoteEdge note2 = new NoteEdge();
		NoteEdge note3 = new NoteEdge();
		NoteEdge note4 = new NoteEdge();
		NoteEdge note5 = new NoteEdge();
		
		diagram.addEdge(note1, new Point2D.Double(50, 200), new Point2D.Double(55, 25));
		diagram.addEdge(note2, new Point2D.Double(50, 200), new Point2D.Double(155, 25));
		diagram.addEdge(note3, new Point2D.Double(50, 200), new Point2D.Double(255, 25));
		diagram.addEdge(note4, new Point2D.Double(50, 200), new Point2D.Double(455, 125));
		diagram.addEdge(note5, new Point2D.Double(50, 200), new Point2D.Double(2255, -25));
		assertEquals(5, diagram.getEdges().size());
	}
	
	/**
	 * Testing connect any NoteNode with NoteEdge (not allowed).
	 */
	@Test
	public void testConnectStateNodeWithNoteNode()
	{
		NoteNode noteNode = new NoteNode();
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.addNode(noteNode, new Point2D.Double(50, 200));
		diagram.draw(aGraphics, aGrid);		
		NoteEdge note1 = new NoteEdge();
		NoteEdge note2 = new NoteEdge();
		NoteEdge note3 = new NoteEdge();
		NoteEdge note4 = new NoteEdge();
		NoteEdge note5 = new NoteEdge();
		
		// valid operations
		diagram.addEdge(note1, new Point2D.Double(20, 20), new Point2D.Double(50, 200));
		diagram.addEdge(note2, new Point2D.Double(50, 20), new Point2D.Double(50, 200));
		diagram.addEdge(note3, new Point2D.Double(250, 20), new Point2D.Double(50, 200));
		// invalid operations
		diagram.addEdge(note4, new Point2D.Double(20, 20), new Point2D.Double(-20, 200));
		diagram.addEdge(note5, new Point2D.Double(150, 20), new Point2D.Double(-50, 200));
		diagram.addEdge(new NoteEdge(), new Point2D.Double(20, 20), new Point2D.Double(50, 49));

		assertEquals(3, diagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing nodes movement
	 * 
	 * 
	 * 
	 * Testing individual node movement
	 */
	@Test
	public void testIndividualNodeMovement()
	{
		// test creation of nodes
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.draw(aGraphics, aGrid);
	
		Rectangle2D initialNode_bond = initialNode.getBounds();
		Rectangle2D stateNode1_bond = stateNode1.getBounds();
		Rectangle2D stateNode2_bond = stateNode2.getBounds();
		Rectangle2D finalNode_bond = finalNode.getBounds();
		initialNode.translate(3, 12);
		stateNode1.translate(-5, 80);
		stateNode2.translate(15, -30);
		finalNode.translate(40, 20);
		
		assertTrue(initialNode_bond.getX() + 3 == initialNode.getBounds().getX());
		assertTrue(initialNode_bond.getY() + 12 == initialNode.getBounds().getY());
		assertTrue(stateNode1_bond.getX() - 5 == stateNode1.getBounds().getX());
		assertTrue(stateNode1_bond.getY() + 80 == stateNode1.getBounds().getY());
		assertTrue(stateNode2_bond.getX() + 15 == stateNode2.getBounds().getX());
		assertTrue(stateNode2_bond.getY() - 30 == stateNode2.getBounds().getY());
		assertTrue(finalNode_bond.getX() + 40 == finalNode.getBounds().getX());
		assertTrue(finalNode_bond.getY() + 20 == finalNode.getBounds().getY());


	}
	
	@Test
	public void testNodesAndEdgesMovement()
	{
		// test creation of nodes
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.draw(aGraphics, aGrid);
		diagram.addEdge(edge1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
		diagram.addEdge(edge2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		diagram.addEdge(edge3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
		diagram.addEdge(edge4, new Point2D.Double(155, 25), new Point2D.Double(55, 25));
		diagram.addEdge(edge5, new Point2D.Double(25, 25), new Point2D.Double(255, 25));
		
		aPanel.getSelectionList().add(initialNode);
		aPanel.getSelectionList().add(stateNode1);
		aPanel.getSelectionList().add(edge1);
		aPanel.getSelectionList().add(edge2);
		aPanel.getSelectionList().add(edge3);

		Rectangle2D initialNode_bond = initialNode.getBounds();
		Rectangle2D stateNode1_bond = stateNode1.getBounds();
		Rectangle2D stateNode2_bond = stateNode2.getBounds();	
		Rectangle2D finalNode_bond = finalNode.getBounds();
		Rectangle2D edge1_bond = edge1.getBounds();
		Rectangle2D edge2_bond = edge2.getBounds();
		Rectangle2D edge3_bond = edge3.getBounds();

		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		
		assertTrue(initialNode_bond.getX() + 26 == initialNode.getBounds().getX());
		assertTrue(initialNode_bond.getY() + 37 == initialNode.getBounds().getY());
		assertTrue(stateNode1_bond.getX() + 26 == stateNode1.getBounds().getX());
		assertTrue(stateNode1_bond.getY() + 37 == stateNode1.getBounds().getY());
		assertEquals(stateNode2_bond, stateNode2.getBounds());
		assertTrue(finalNode_bond.getX() == finalNode.getBounds().getX());
		assertTrue(finalNode_bond.getY() == finalNode.getBounds().getY());
		assertEquals(initialNode, edge1.getStart());
		assertEquals(stateNode1, edge1.getEnd());
		assertEquals(stateNode1, edge2.getStart());
		assertEquals(stateNode2, edge2.getEnd());
		/*
		 *  if either start or end node is moved,
		 *  the edge bond would be changed
		 */
		assertFalse(edge1_bond == edge1.getBounds());
		assertFalse(edge2_bond == edge2.getBounds());
		/*
		 *  if both the start and end node are not moved,
		 *  the edge should have the same bond
		 */
		assertEquals(edge3_bond, edge3.getBounds());
	}
	
	/**
	 * Below are methods testing deletion and undo feature for state diagragm.
	 * 
	 * 
	 * Testing delete a start node with an attached edge
	 */
	@Test
	public void testRemoveStartNode()
	{
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addEdge(edge1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
		aPanel.getSelectionList().add(initialNode);
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());

		aPanel.undo();
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
	}
	
	/**
	 * Testing delete a end node with an attached edge
	 */
	@Test
	public void testRemoveEndNode()
	{
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.addEdge(edge3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
		aPanel.getSelectionList().add(finalNode);
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());

		aPanel.undo();
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
	}
	
	/**
	 * Testing delete a end node with an attached edge
	 */
	@Test
	public void testRemoveStateNode()
	{
		diagram.addNode(initialNode, new Point2D.Double(20, 20));
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.addNode(finalNode, new Point2D.Double(250, 20));
		diagram.draw(aGraphics, aGrid);
		diagram.addEdge(edge1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
		diagram.addEdge(edge2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		diagram.addEdge(edge3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
		diagram.addEdge(edge4, new Point2D.Double(155, 25), new Point2D.Double(55, 25));
		diagram.addEdge(edge5, new Point2D.Double(25, 25), new Point2D.Double(255, 25));
		aPanel.getSelectionList().add(stateNode2);
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		
		assertEquals(3, diagram.getRootNodes().size());
		assertEquals(2, diagram.getEdges().size());

		aPanel.undo();
		diagram.draw(aGraphics, aGrid);
		assertEquals(4, diagram.getRootNodes().size());
		assertEquals(5, diagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing copy and paste feature for state diagram
	 * 
	 * 
	 * 
	 * Testing copy a State Node
	 */
	@Test
	public void testCopyStateNode()
	{
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.draw(aGraphics, aGrid);
		aPanel.getSelectionList().add(stateNode1);
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);
		diagram.draw(aGraphics, aGrid);
		
		assertEquals(2, diagram.getRootNodes().size());
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[1]).getBounds().getX()));
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[1]).getBounds().getY()));
	}
	
	/**
	 * 
	 * Testing cut a State Node
	 */
	@Test
	public void testCutStateNode()
	{
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.draw(aGraphics, aGrid);
		aPanel.getSelectionList().add(stateNode1);
		clipboard.copy(aPanel.getSelectionList());
		
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		assertEquals(0, diagram.getRootNodes().size());

		
		clipboard.paste(aPanel);
		diagram.draw(aGraphics, aGrid);
		
		assertEquals(1, diagram.getRootNodes().size());
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[0]).getBounds().getX()));
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[0]).getBounds().getY()));
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge
	 */
	@Test
	public void testCopyNodesWithEdge()
	{
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.draw(aGraphics, aGrid);
		diagram.addEdge(edge2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);

		diagram.draw(aGraphics, aGrid);
		assertEquals(4, diagram.getRootNodes().size());
		assertEquals(2, diagram.getEdges().size());
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[2]).getBounds().getX()));
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[2]).getBounds().getY()));
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge
	 */
	@Test
	public void testCutNodesWithEdge()
	{
		diagram.addNode(stateNode1, new Point2D.Double(50, 20));
		diagram.addNode(stateNode2, new Point2D.Double(150, 20));
		diagram.draw(aGraphics, aGrid);
		diagram.addEdge(edge2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		assertEquals(0, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());

		clipboard.paste(aPanel);
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[0]).getBounds().getX()));
		assertTrue(0 == (((StateNode) diagram.getRootNodes().toArray()[0]).getBounds().getY()));
	}
	
	
	
}

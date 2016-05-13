package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * Tests various interactions with Sequence Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Sequence Diagram.
 * 
 * @author Jiajun Chen
 *
 */


public class TestUsageScenariosSequenceDiagram 
{
	private SequenceDiagramGraph aDiagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard aClipboard;
	private SelectionList aList;
	private ImplicitParameterNode aParameterNode1;
	private ImplicitParameterNode aParameterNode2;
	private CallNode aCallNode1;
	private CallNode aCallNode2;
	private CallEdge aCallEdge1;
	
	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		aDiagram = new SequenceDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram));
		aGrid = new Grid();
		aClipboard = new Clipboard();
		aList = new SelectionList();
		aParameterNode1 = new ImplicitParameterNode();
		aParameterNode2 = new ImplicitParameterNode();
		aCallNode1 = new CallNode();
		aCallNode2 = new CallNode();
		aCallEdge1 = new CallEdge();
	}
	
	/**
	 * Test the creation of ParameterNode and link them with edges (not allowed).
	 */
	@Test
	public void testCreateAndLinkParameterNode()
	{
		aParameterNode1.getName().setText("client");
		aParameterNode2.getName().setText("platform");
		aDiagram.addNode(aParameterNode1, new Point2D.Double(5, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(25, 0));
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("client", aParameterNode1.getName().getText());
		assertEquals("platform", aParameterNode2.getName().getText());
		
		CallEdge edge1 = new CallEdge();
		ReturnEdge edge2 = new ReturnEdge();
		NoteEdge edge3 = new NoteEdge();
		aDiagram.addEdge(edge1, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		aDiagram.addEdge(edge2, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		aDiagram.addEdge(edge3, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		assertEquals(0, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing create the CallNode and link it to the right Parameter's life line
	 * except CallEdge (not allowed).
	 */
	@Test
	public void testCreateCallNodeAndLinkParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(5, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(25, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(7, 75));
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aParameterNode1.getChildren().size());
		
		ReturnEdge reEdge = new ReturnEdge();
		NoteEdge noEdge = new NoteEdge();
		aDiagram.addEdge(reEdge, new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		aDiagram.addEdge(noEdge, new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		assertEquals(0, aDiagram.getEdges().size());
		
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing link CallNode to ParameterNode life line and other CallNode.
	 */
	@Test
	public void testLinkCallNodeToLifeLineAndCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(5, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(25, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(7, 75));
		
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(7, 75), new Point2D.Double(25,75));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		aDiagram.draw(aGraphics, aGrid);
		
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(62,85), new Point2D.Double(64,88));
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(2, aParameterNode2.getChildren().size());
	}
	
	/**
	 * Testing link CallNode to ParameterNode's top box. A CallEdge with 
	 * "<<create>>" should appear.
	 */
	@Test
	public void testCreateCallEdgeWithCreateTag()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(5, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(105, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(7, 75));
		aDiagram.addEdge(aCallEdge1, new  Point2D.Double(7, 75), new Point2D.Double(8,85));
		aDiagram.draw(aGraphics, aGrid);

		aDiagram.addEdge(new CallEdge(), new Point2D.Double(59, 110), new Point2D.Double(116,0));
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals("«create»", ((CallEdge) aDiagram.getEdges().toArray()[1]).getMiddleLabel());
	}
	
	/**
	 * Testing adding more edges to the diagram.
	 */
	@Test
	public void testAddMoreEdges()
	{
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParaNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.draw(aGraphics, aGrid);
		
		ReturnEdge reEdge1 = new ReturnEdge();
		aDiagram.addEdge(reEdge1, new Point2D.Double(145,90), new Point2D.Double(45, 90));
		assertEquals(2, aDiagram.getEdges().size());
		
		// call edge from first CallNode to third ParameterNode life line
		CallEdge callEdge2 = new CallEdge();
		aDiagram.addEdge(callEdge2, new Point2D.Double(45, 75), new Point2D.Double(210,75));
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(3, aDiagram.getEdges().size());
		
		// call edge from first CallNode to third ParameterNode's top box
		CallEdge callEdge3 = new CallEdge();
		aDiagram.addEdge(callEdge3, new Point2D.Double(45, 75), new Point2D.Double(210,0));
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing NoteNode and NoteEdge creation in a sequence diagram.
	 */
	@Test
	public void testNoteNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(aCallNode2, new Point2D.Double(115, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(120,75));
		aDiagram.draw(aGraphics, aGrid);
		
		NoteNode noteNode = new NoteNode();
		NoteEdge edge1 = new NoteEdge();
		NoteEdge edge2 = new NoteEdge();
		NoteEdge edge3 = new NoteEdge();
		NoteEdge edge4 = new NoteEdge();
		NoteEdge edge5 = new NoteEdge();
		aDiagram.addNode(noteNode, new Point2D.Double(55, 55));
		aDiagram.addEdge(edge1, new Point2D.Double(60, 60), new Point2D.Double(87,65));
		aDiagram.addEdge(edge2, new Point2D.Double(62, 68), new Point2D.Double(47,75));
		aDiagram.addEdge(edge3, new Point2D.Double(63, 69), new Point2D.Double(47,35));
		aDiagram.addEdge(edge4, new Point2D.Double(64, 70), new Point2D.Double(17,5));
		aDiagram.addEdge(edge5, new Point2D.Double(65, 60), new Point2D.Double(67,265));
		
		assertEquals(6, aDiagram.getEdges().size());
		assertEquals(8, aDiagram.getRootNodes().size());
		
		// from ParameterNode to NoteNode
		aDiagram.addEdge(new NoteEdge(), new Point2D.Double(10, 10), new Point2D.Double(62, 68));
		// from CallNode to NoteNode 
		aDiagram.addEdge(new NoteEdge(), new Point2D.Double(10, 10), new Point2D.Double(62, 68));
		assertEquals(8, aDiagram.getRootNodes().size());
	}
	
	/**
	 * Testing Node movement for individual node. 
	 * Note edge could not be moved individually.
	 */
	@Test
	public void testIndividualNodeMoveMent()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(aCallNode2, new Point2D.Double(115, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(120,75));
		aDiagram.draw(aGraphics, aGrid);
		
		// testing moving ParameterNode
		aParameterNode1.translate(5, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(15 == aParameterNode1.getBounds().getX());
		assertTrue(0 == aParameterNode1.getBounds().getY());
		aParameterNode1.translate(25, 0);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(40 == aParameterNode1.getBounds().getX());
		assertTrue(0 == aParameterNode1.getBounds().getY());
		aParameterNode2.translate(105, 25);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(215 == aParameterNode2.getBounds().getX());
		assertTrue(0 == aParameterNode2.getBounds().getY());
		aParameterNode2.translate(0, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(215 == aParameterNode2.getBounds().getX());
		assertTrue(0 == aParameterNode2.getBounds().getY());
		
		// testing moving left call node
		double callNode1X = aCallNode1.getBounds().getX();
		double callNode1Y = aCallNode1.getBounds().getY();
		aCallNode1.translate(5, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(callNode1X, aCallNode1.getBounds().getX());
		assertTrue(callNode1Y + 15 == aCallNode1.getBounds().getY());
		aCallNode1.translate(0, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(callNode1X == aCallNode1.getBounds().getX());
		assertTrue(callNode1Y + 15 + 15 == aCallNode1.getBounds().getY());
		aCallNode1.translate(20, 0);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(callNode1X == aCallNode1.getBounds().getX());
		assertTrue(callNode1Y + 15 + 15 == aCallNode1.getBounds().getY());
		
		// testing moving right call node
		double callNode2X = aCallNode2.getBounds().getX();
		double callNode2Y = aCallNode2.getBounds().getY();
		aCallNode2.translate(5, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(callNode2X == aCallNode2.getBounds().getX());
		assertTrue(callNode2Y == aCallNode2.getBounds().getY());
		aCallNode2.translate(0, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(callNode2X == aCallNode2.getBounds().getX());
		assertTrue(callNode2Y == aCallNode2.getBounds().getY());
		aCallNode2.translate(20, 0);
		aDiagram.draw(aGraphics, aGrid);
		assertTrue(callNode2X == aCallNode2.getBounds().getX());
		assertTrue(callNode2Y == aCallNode2.getBounds().getY());
	}
	
	/**
	 * Testing moving entire graph.
	 */
	@Test
	public void testMoveEntireGraph()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(aCallNode2, new Point2D.Double(115, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(120,75));
		aDiagram.draw(aGraphics, aGrid);
		double callNode1X = aCallNode1.getBounds().getX();
		double callNode1Y = aCallNode1.getBounds().getY();
		double callNode2X = aCallNode2.getBounds().getX();
		double callNode2Y = aCallNode2.getBounds().getY();
		
		aPanel.selectAll();
		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(15, 0);
			}
		}
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		
		assertTrue(25 == aParameterNode1.getBounds().getX());
		assertTrue(0 == aParameterNode1.getBounds().getY());
		assertTrue(125 == aParameterNode2.getBounds().getX());
		assertTrue(0 == aParameterNode2.getBounds().getY());
		assertTrue(callNode1X + 15 == aCallNode1.getBounds().getX());
		assertTrue(callNode1Y == aCallNode1.getBounds().getY());
		assertTrue(callNode2X + 15 == aCallNode2.getBounds().getX());
		assertTrue(callNode2Y == aCallNode2.getBounds().getY());
		
		aPanel.selectAll();
		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(-25, 0);
			}
		}
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		
		assertTrue(0 == aParameterNode1.getBounds().getX());
		assertTrue(0 == aParameterNode1.getBounds().getY());
		assertTrue(100 == aParameterNode2.getBounds().getX());
		assertTrue(0 == aParameterNode2.getBounds().getY());
		assertTrue(callNode1X + 15 - 25 == aCallNode1.getBounds().getX());
		assertTrue(callNode1Y == aCallNode1.getBounds().getY());
		assertTrue(callNode2X + 15 - 25 == aCallNode2.getBounds().getX());
		assertTrue(callNode2Y == aCallNode2.getBounds().getY());
	}
	
	/**
	 * Testing moving entire graph with a <<create>> CallEdge.
	 */
	@Test
	public void testMoveEntireGraphWithCallEdge()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(15, 80), new Point2D.Double(116,0));
		aDiagram.draw(aGraphics, aGrid);
		
		double callNode1X = aCallNode1.getBounds().getX();
		double callNode1Y = aCallNode1.getBounds().getY();
		double paraNode2X = aParameterNode2.getBounds().getX();
		double paraNode2Y = aParameterNode2.getBounds().getY();
		
		aPanel.selectAll();
		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(15, 0);
			}
		}
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
	
		assertTrue(25 == aParameterNode1.getBounds().getX());
		assertTrue(0 == aParameterNode1.getBounds().getY());
		assertTrue(paraNode2X + 15 == aParameterNode2.getBounds().getX());
		assertTrue(paraNode2Y == aParameterNode2.getBounds().getY());
		assertTrue(callNode1X + 15 == aCallNode1.getBounds().getX());
		assertTrue(callNode1Y == aCallNode1.getBounds().getY());
		
		aPanel.selectAll();
		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(-25, 0);
			}
		}
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
	
		assertTrue(0 == aParameterNode1.getBounds().getX());
		assertTrue(0 == aParameterNode1.getBounds().getY());
		assertTrue(paraNode2X + 15 - 25 == aParameterNode2.getBounds().getX());
		assertTrue(paraNode2Y == aParameterNode2.getBounds().getY());
		assertTrue(callNode1X + 15 - 25 == aCallNode1.getBounds().getX());
		assertTrue(callNode1Y == aCallNode1.getBounds().getY());
	}
	
	/**
	 * Below are methods testing the deletion and undo feature
	 * for sequence diagram. Currently no testing for edge deletion.
	 *
	 *
	 *
	 * Testing delete single ParameterNode
	 */
	@Test
	public void testDeleteSignleParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		Rectangle2D paraNode1Bond = aParameterNode1.getBounds();
		aPanel.getSelectionList().add(aParameterNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(0, aDiagram.getRootNodes().size());
		aPanel.undo();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(paraNode1Bond, ((ImplicitParameterNode) (aDiagram.getRootNodes().toArray()[0])).getBounds());
	}
	
	/**
	 * Testing delete single CallNode.
	 */
	@Test
	public void testDeleteSignleCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.draw(aGraphics, aGrid);

		Rectangle2D callNode1Bond = aCallNode1.getBounds();
		aPanel.getSelectionList().add(aCallNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aParameterNode1.getChildren().size());
		
		aPanel.undo();
		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(callNode1Bond, ((CallNode) (aParameterNode1.getChildren().toArray()[0])).getBounds());
	}
	
	/**
	 * Testing delete a ParameterNode in call sequence.
	 */
	@Test
	public void testDeleteParameterNodeInCallSequence()
	{
		// sepcific test case set up 
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParaNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.draw(aGraphics, aGrid);
		ReturnEdge reEdge1 = new ReturnEdge();
		aDiagram.addEdge(reEdge1, new Point2D.Double(145,90), new Point2D.Double(45, 90));		
		CallEdge callEdge2 = new CallEdge();
		aDiagram.addEdge(callEdge2, new Point2D.Double(45, 75), new Point2D.Double(210,75));
		aDiagram.draw(aGraphics, aGrid);
		
		aPanel.getSelectionList().add(aParameterNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(0, newParaNode.getChildren().size());
		/*
		 *  since a return edge is added, the call node will still remain there
		 *  however the edges are still removed
		 */
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, newParaNode.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(3, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a call node in the middle Parameter Node in call sequence.
	 */
	@Test
	public void testDeleteMiddleCallNode()
	{
		// sepcific test case set up 
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		CallNode midNode = new CallNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParaNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(midNode, new Point2D.Double(115, 75));
		aDiagram.addNode(new CallNode(), new Point2D.Double(215, 75));
		
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point2D.Double(118, 75), new Point2D.Double(18,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(210,115));
		
		aPanel.getSelectionList().add(midNode);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(0, aParameterNode2.getChildren().size()); 
		assertEquals(0, newParaNode.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(2, newParaNode.getChildren().size()); 
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a return edge.
	 */
	@Test
	public void testDeleteReturnEdge()
	{
		CallNode midNode = new CallNode();
		ReturnEdge returnEdge = new ReturnEdge();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(midNode, new Point2D.Double(115, 75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.addEdge(returnEdge, new Point2D.Double(118, 75), new Point2D.Double(18,75));
		
		aPanel.getSelectionList().add(returnEdge);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(1, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(2, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a CallNode with both incoming and return edge.
	 */
	@Test
	public void testDeleteCallNodeWithIncomingAndReturnEdge()
	{
		CallNode midNode = new CallNode();
		ReturnEdge returnEdge = new ReturnEdge();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(midNode, new Point2D.Double(115, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(returnEdge, new Point2D.Double(118, 75), new Point2D.Double(18,75));
		
		aPanel.getSelectionList().add(returnEdge);
		aPanel.getSelectionList().add(aCallEdge1);
		aPanel.getSelectionList().add(midNode);

		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(0, aParameterNode2.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(2, aDiagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing the copy and paste feature
	 * for sequence diagram.
	 * 
	 * 
	 * Testing copy and paste signle Parameter Node.
	 */
	@Test
	public void testCopyPasteParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aPanel.getSelectionList().add(aParameterNode1);
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertTrue(0 == (((Node)(aDiagram.getRootNodes().toArray()[1])).getBounds().getX()));
		assertTrue(0 == (((Node)(aDiagram.getRootNodes().toArray()[1])).getBounds().getY()));
	}
	
	/**
	 * Testing cut and paste single Parameter Node.
	 */
	@Test
	public void testCutPasteParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aPanel.getSelectionList().add(aParameterNode1);
		aClipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());

		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertTrue(0 == (((Node)(aDiagram.getRootNodes().toArray()[0])).getBounds().getX()));
		assertTrue(0 == (((Node)(aDiagram.getRootNodes().toArray()[0])).getBounds().getY()));
	}
	
	/**
	 * Testing copy and paste Parameter Node with Call Node.
	 */
	@Test
	public void testCopyPasteParameterNodeWithCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aPanel.getSelectionList().add(aParameterNode1);
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertTrue(0 == (((Node)(aDiagram.getRootNodes().toArray()[1])).getBounds().getX()));
		assertTrue(0 == (((Node)(aDiagram.getRootNodes().toArray()[1])).getBounds().getX()));
		assertTrue(1 == (((ImplicitParameterNode)(aDiagram.getRootNodes().toArray()[1])).getChildren().size()));
	}
	
	/**
	 * Testing copy and paste a whole diagram.
	 */
	@Test
	public void testCopyPasteSequenceDiagram()
	{
		// sepcific test case set up 
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		CallNode midNode = new CallNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParaNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(midNode, new Point2D.Double(115, 75));
		aDiagram.addNode(new CallNode(), new Point2D.Double(215, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point2D.Double(118, 75), new Point2D.Double(18,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(210,115));
		
		aPanel.selectAll();
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(6, aDiagram.getRootNodes().size());
		assertEquals(8, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing copy and paste a whole diagram.
	 */
	@Test
	public void testCopyPartialGraph()
	{
		// sepcific test case set up 
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		CallNode midNode = new CallNode();
		CallNode endNode = new CallNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParaNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(midNode, new Point2D.Double(115, 75));
		aDiagram.addNode(endNode, new Point2D.Double(215, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point2D.Double(118, 75), new Point2D.Double(18,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(210,115));
		
		aList.add(aCallNode1);
		aList.add(midNode);
		aList.add(endNode);
		for(Edge edge: aDiagram.getEdges())
		{
			aList.add(edge);
		}
		aClipboard.copy(aList);
		SequenceDiagramGraph tempDiagram = new SequenceDiagramGraph();
		GraphPanel tempPanel = new GraphPanel(tempDiagram, new ToolBar(aDiagram));
		aClipboard.paste(tempPanel);
		tempDiagram.draw(aGraphics, aGrid);
		
		assertEquals(0, tempDiagram.getRootNodes().size());
		assertEquals(0, tempDiagram.getEdges().size());
	}	
}

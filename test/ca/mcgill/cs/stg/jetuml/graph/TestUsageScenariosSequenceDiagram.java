package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;

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
		
		CallEdge callEdge = new CallEdge();
		ReturnEdge returnEdge = new ReturnEdge();
		NoteEdge noteEdge = new NoteEdge();
		aDiagram.addEdge(callEdge, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		aDiagram.addEdge(returnEdge, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		aDiagram.addEdge(noteEdge, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
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
		
		ReturnEdge returnEdge = new ReturnEdge();
		NoteEdge noteEdge = new NoteEdge();
		aDiagram.addEdge(returnEdge, new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		aDiagram.addEdge(noteEdge, new Point2D.Double(7, 75), new Point2D.Double(26, 0));
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
		assertEquals("\u00ABcreate\u00BB", ((CallEdge) aDiagram.getEdges().toArray()[1]).getMiddleLabel());
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
		
		ReturnEdge returnEdge1 = new ReturnEdge();
		aDiagram.addEdge(returnEdge1, new Point2D.Double(145,90), new Point2D.Double(45, 90));
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
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		aDiagram.addNode(noteNode, new Point2D.Double(55, 55));
		aDiagram.addEdge(noteEdge1, new Point2D.Double(60, 60), new Point2D.Double(87,65));
		aDiagram.addEdge(noteEdge2, new Point2D.Double(62, 68), new Point2D.Double(47,75));
		aDiagram.addEdge(noteEdge3, new Point2D.Double(63, 69), new Point2D.Double(47,35));
		aDiagram.addEdge(noteEdge4, new Point2D.Double(64, 70), new Point2D.Double(17,5));
		aDiagram.addEdge(noteEdge5, new Point2D.Double(65, 60), new Point2D.Double(67,265));
		
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

		// testing moving ParameterNode, can only be moved horizontally
		aParameterNode1.translate(5, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(15, 0, 80, 155), aParameterNode1.getBounds());
		aParameterNode1.translate(25, 0);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(40, 0, 80, 155), aParameterNode1.getBounds());

		aParameterNode2.translate(105, 25);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(215, 0, 80, 155), aParameterNode2.getBounds());
		aParameterNode2.translate(0, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(215, 0, 80, 155), aParameterNode2.getBounds());
		
		// testing moving left call node, can only be moved vertically
		aCallNode1.translate(5, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(72, 90, 16, 60), aCallNode1.getBounds());
		aCallNode1.translate(0, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(72, 105, 16, 60), aCallNode1.getBounds());
		aCallNode1.translate(20, 0);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(72, 105, 16, 60), aCallNode1.getBounds());
		
		// testing moving right call node, can only be moved vertically
		aCallNode2.translate(5, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(247, 115, 16, 30), aCallNode2.getBounds());
		aCallNode2.translate(0, 15);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(247, 115, 16, 30), aCallNode2.getBounds());
		aCallNode2.translate(20, 0);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(247, 115, 16, 30), aCallNode2.getBounds());
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
		assertEquals(new Rectangle2D.Double(25, 0, 80, 155), aParameterNode1.getBounds());
		assertEquals(new Rectangle2D.Double(125, 0, 80, 155), aParameterNode2.getBounds());
		assertEquals(new Rectangle2D.Double(57, 75, 16, 60), aCallNode1.getBounds());
		assertEquals(new Rectangle2D.Double(157, 85, 16, 30), aCallNode2.getBounds());

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
		assertEquals(new Rectangle2D.Double(0, 0, 80, 155), aParameterNode1.getBounds());
		assertEquals(new Rectangle2D.Double(100, 0, 80, 155), aParameterNode2.getBounds());
		assertEquals(new Rectangle2D.Double(32, 75, 16, 60), aCallNode1.getBounds());
		assertEquals(new Rectangle2D.Double(132, 85, 16, 30), aCallNode2.getBounds());
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
		assertEquals(new Rectangle2D.Double(25, 0, 80, 165), aParameterNode1.getBounds());
		assertEquals(new Rectangle2D.Double(125, 65, 80, 100), aParameterNode2.getBounds());
		assertEquals(new Rectangle2D.Double(57, 75, 16, 70), aCallNode1.getBounds());
		
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
		assertEquals(new Rectangle2D.Double(0, 0, 80, 165), aParameterNode1.getBounds());
		assertEquals(new Rectangle2D.Double(100, 65, 80, 100), aParameterNode2.getBounds());
		assertEquals(new Rectangle2D.Double(32, 75, 16, 70), aCallNode1.getBounds());
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
	public void testDeleteSingleParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		Rectangle2D parameterNode1Bounds = aParameterNode1.getBounds();
		aPanel.getSelectionList().add(aParameterNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(0, aDiagram.getRootNodes().size());
		aPanel.undo();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(parameterNode1Bounds, ((ImplicitParameterNode) (aDiagram.getRootNodes().toArray()[0])).getBounds());
	}
	
	/**
	 * Testing delete single CallNode.
	 */
	@Test
	public void testDeleteSingleCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.draw(aGraphics, aGrid);

		Rectangle2D callNode1Bounds = aCallNode1.getBounds();
		aPanel.getSelectionList().add(aCallNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aParameterNode1.getChildren().size());
		
		aPanel.undo();
		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(callNode1Bounds, ((CallNode) (aParameterNode1.getChildren().toArray()[0])).getBounds());
	}
	
	/**
	 * Testing delete a ParameterNode in call sequence.
	 */
	@Test
	public void testDeleteParameterNodeInCallSequence()
	{
		// set up 
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParameterNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.draw(aGraphics, aGrid);
		ReturnEdge returnEdge1 = new ReturnEdge();
		aDiagram.addEdge(returnEdge1, new Point2D.Double(145,90), new Point2D.Double(45, 90));		
		CallEdge callEdge2 = new CallEdge();
		aDiagram.addEdge(callEdge2, new Point2D.Double(45, 75), new Point2D.Double(210,75));
		aDiagram.draw(aGraphics, aGrid);
		
		aPanel.getSelectionList().add(aParameterNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(0, newParameterNode.getChildren().size());
		/*
		 *  since a return edge is added, the call node will still remain there
		 *  however the edges are still removed
		 */
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, newParameterNode.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(3, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a call node in the middle Parameter Node in call sequence.
	 */
	@Test
	public void testDeleteMiddleCallNode()
	{
		// set up 
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		CallNode middleCallNode = new CallNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParameterNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(middleCallNode, new Point2D.Double(115, 75));
		aDiagram.addNode(new CallNode(), new Point2D.Double(215, 75));
		
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point2D.Double(118, 75), new Point2D.Double(18,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(210,115));
		
		aPanel.getSelectionList().add(middleCallNode);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(0, aParameterNode2.getChildren().size()); 
		assertEquals(0, newParameterNode.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(2, newParameterNode.getChildren().size()); 
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a return edge.
	 */
	@Test
	public void testDeleteReturnEdge()
	{
		CallNode middleCallNode = new CallNode();
		ReturnEdge returnEdge = new ReturnEdge();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(middleCallNode, new Point2D.Double(115, 75));
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
		CallNode middleCallNode = new CallNode();
		ReturnEdge returnEdge = new ReturnEdge();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(middleCallNode, new Point2D.Double(115, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(returnEdge, new Point2D.Double(118, 75), new Point2D.Double(18,75));
		
		aPanel.getSelectionList().add(returnEdge);
		aPanel.getSelectionList().add(aCallEdge1);
		aPanel.getSelectionList().add(middleCallNode);

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
	 * Testing copy and paste single Parameter Node.
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
		assertEquals(new Rectangle2D.Double(0, 0, 80, 80),
				((Node)(aDiagram.getRootNodes().toArray()[1])).getBounds());
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
		assertEquals(new Rectangle2D.Double(0, 0, 80, 80),
				((Node)(aDiagram.getRootNodes().toArray()[0])).getBounds());
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
		assertEquals(new Rectangle2D.Double(10, 0, 80, 125),
				((Node)(aDiagram.getRootNodes().toArray()[0])).getBounds());
		assertEquals(1, (((ImplicitParameterNode)(aDiagram.getRootNodes().toArray()[1])).getChildren().size()));
	}
	
	/**
	 * Testing copy and paste a whole diagram.
	 */
	@Test
	public void testCopyPasteSequenceDiagram()
	{
		// test case set up 
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		CallNode middleCallNode = new CallNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParameterNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(middleCallNode, new Point2D.Double(115, 75));
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
		// set up 
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		CallNode middleCallNode = new CallNode();
		CallNode endCallNode = new CallNode();
		aDiagram.addNode(aParameterNode1, new Point2D.Double(10, 0));
		aDiagram.addNode(aParameterNode2, new Point2D.Double(110, 0));
		aDiagram.addNode(newParaNode, new Point2D.Double(210, 0));
		aDiagram.addNode(aCallNode1, new Point2D.Double(15, 75));
		aDiagram.addNode(middleCallNode, new Point2D.Double(115, 75));
		aDiagram.addNode(endCallNode, new Point2D.Double(215, 75));
		aDiagram.addEdge(aCallEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point2D.Double(118, 75), new Point2D.Double(18,75));
		aDiagram.addEdge(new CallEdge(), new Point2D.Double(118, 75), new Point2D.Double(210,115));
		
		aList.add(aCallNode1);
		aList.add(middleCallNode);
		aList.add(endCallNode);
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

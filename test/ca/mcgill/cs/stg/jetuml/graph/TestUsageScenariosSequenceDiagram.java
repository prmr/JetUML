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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;

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
	private SequenceDiagramGraph diagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard clipboard;
	private SelectionList aList;
	private ImplicitParameterNode paraNode1;
	private ImplicitParameterNode paraNode2;
	private CallNode callNode1;
	
	@Before
	public void setup()
	{
		diagram = new SequenceDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGrid = new Grid();
		clipboard = new Clipboard();
		aList = new SelectionList();
		paraNode1 = new ImplicitParameterNode();
		paraNode2 = new ImplicitParameterNode();
		callNode1 = new CallNode();
	}
	
	/**
	 * Test the creation of ParameterNode and link them with edges (not allowed).
	 */
	@Test
	public void testCreateAndLinkParameterNode()
	{
		
		paraNode1.getName().setText("client");
		paraNode2.getName().setText("platform");
		diagram.addNode(paraNode1, new Point2D.Double(5, 0));
		diagram.addNode(paraNode2, new Point2D.Double(25, 0));
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals("client", paraNode1.getName().getText());
		assertEquals("platform", paraNode2.getName().getText());
		
		CallEdge edge1 = new CallEdge();
		ReturnEdge edge2 = new ReturnEdge();
		NoteEdge edge3 = new NoteEdge();
		diagram.addEdge(edge1, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		diagram.addEdge(edge2, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		diagram.addEdge(edge3, new Point2D.Double(7, 0), new Point2D.Double(26, 0));
		assertEquals(0, diagram.getEdges().size());
	}
	
	/**
	 * Testing create the CallNode and link it to the right Parameter's life line
	 * except CallEdge (not allowed).
	 */
	@Test
	public void testCreateCallNodeAndLinkParameterNode()
	{
		diagram.addNode(paraNode1, new Point2D.Double(5, 0));
		diagram.addNode(paraNode2, new Point2D.Double(25, 0));
		diagram.addNode(callNode1, new Point2D.Double(7, 75));
		
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, paraNode1.getChildren().size());
		
		ReturnEdge reEdge = new ReturnEdge();
		NoteEdge noEdge = new NoteEdge();
		diagram.addEdge(reEdge, new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		diagram.addEdge(noEdge, new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		assertEquals(0, diagram.getEdges().size());
		
		diagram.addEdge(new CallEdge(), new Point2D.Double(7, 75), new Point2D.Double(26, 0));
		assertEquals(1, diagram.getEdges().size());
	}
	
	/**
	 * Testing link CallNode to ParameterNode life line and other CallNode.
	 */
	@Test
	public void testLinkCallNodeToLifeLineAndCallNode()
	{
		diagram.addNode(paraNode1, new Point2D.Double(5, 0));
		diagram.addNode(paraNode2, new Point2D.Double(25, 0));
		diagram.addNode(callNode1, new Point2D.Double(7, 75));
		
		CallEdge callEdge = new CallEdge();
		diagram.addEdge(callEdge, new Point2D.Double(7, 75), new Point2D.Double(25,75));
		assertEquals(1, diagram.getEdges().size());
		assertEquals(1, paraNode2.getChildren().size());
		diagram.draw(aGraphics, aGrid);
		
		diagram.addEdge(new CallEdge(), new Point2D.Double(62,85), new Point2D.Double(64,88));
		assertEquals(2, diagram.getEdges().size());
		assertEquals(2, paraNode2.getChildren().size());
	}
	
	/**
	 * Testing link CallNode to ParameterNode's top box. A CallEdge with 
	 * "<<create>>" should appear.
	 */
	@Test
	public void testCreateCallEdgeWithCreateTag()
	{
		diagram.addNode(paraNode1, new Point2D.Double(5, 0));
		diagram.addNode(paraNode2, new Point2D.Double(105, 0));
		diagram.addNode(callNode1, new Point2D.Double(7, 75));
		CallEdge callEdge = new CallEdge();
		diagram.addEdge(callEdge, new  Point2D.Double(7, 75), new Point2D.Double(8,85));
		diagram.draw(aGraphics, aGrid);

		diagram.addEdge(new CallEdge(), new Point2D.Double(59, 110), new Point2D.Double(116,0));
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getEdges().size());
		assertEquals("«create»", ((CallEdge) diagram.getEdges().toArray()[1]).getMiddleLabel());
	}
	
	/**
	 * Testing adding more edges to the diagram.
	 */
	@Test
	public void testAddMoreEdges()
	{
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		diagram.addNode(paraNode1, new Point2D.Double(10, 0));
		diagram.addNode(paraNode2, new Point2D.Double(110, 0));
		diagram.addNode(newParaNode, new Point2D.Double(210, 0));
		diagram.addNode(callNode1, new Point2D.Double(15, 75));
		CallEdge callEdge1 = new CallEdge();
		diagram.addEdge(callEdge1, new Point2D.Double(18, 75), new Point2D.Double(115,75));
		diagram.draw(aGraphics, aGrid);
		
		ReturnEdge reEdge1 = new ReturnEdge();
		diagram.addEdge(reEdge1, new Point2D.Double(145,90), new Point2D.Double(45, 90));
		assertEquals(2, diagram.getEdges().size());
		
		// call edge from first CallNode to third ParameterNode life line
		CallEdge callEdge2 = new CallEdge();
		diagram.addEdge(callEdge2, new Point2D.Double(45, 75), new Point2D.Double(210,75));
		diagram.draw(aGraphics, aGrid);
		assertEquals(3, diagram.getEdges().size());
		
		// call edge from first CallNode to third ParameterNode's top box
		CallEdge callEdge3 = new CallEdge();
		diagram.addEdge(callEdge3, new Point2D.Double(45, 75), new Point2D.Double(210,0));
		diagram.draw(aGraphics, aGrid);
		assertEquals(4, diagram.getEdges().size());
	}
	
	/**
	 * Testing NoteNode and NoteEdge creation in a sequence diagram.
	 */
	@Test
	public void testNoteNode()
	{
		CallNode callNode2 = new CallNode();
		CallEdge callEdge1 = new CallEdge();
		diagram.addNode(paraNode1, new Point2D.Double(10, 0));
		diagram.addNode(paraNode2, new Point2D.Double(110, 0));
		diagram.addNode(callNode1, new Point2D.Double(15, 75));
		diagram.addNode(callNode2, new Point2D.Double(115, 75));
		diagram.addEdge(callEdge1, new Point2D.Double(18, 75), new Point2D.Double(120,75));
		diagram.draw(aGraphics, aGrid);
		
		NoteNode noteNode = new NoteNode();
		NoteEdge edge1 = new NoteEdge();
		NoteEdge edge2 = new NoteEdge();
		NoteEdge edge3 = new NoteEdge();
		NoteEdge edge4 = new NoteEdge();
		NoteEdge edge5 = new NoteEdge();
		diagram.addNode(noteNode, new Point2D.Double(55, 55));
		diagram.addEdge(edge1, new Point2D.Double(60, 60), new Point2D.Double(87,65));
		diagram.addEdge(edge2, new Point2D.Double(62, 68), new Point2D.Double(47,75));
		diagram.addEdge(edge3, new Point2D.Double(63, 69), new Point2D.Double(47,35));
		diagram.addEdge(edge4, new Point2D.Double(64, 70), new Point2D.Double(17,5));
		diagram.addEdge(edge5, new Point2D.Double(65, 60), new Point2D.Double(67,265));
		
		assertEquals(6, diagram.getEdges().size());
		assertEquals(8, diagram.getRootNodes().size());
		
		// from ParameterNode to NoteNode
		diagram.addEdge(new NoteEdge(), new Point2D.Double(10, 10), new Point2D.Double(62, 68));
		// from CallNode to NoteNode 
		diagram.addEdge(new NoteEdge(), new Point2D.Double(10, 10), new Point2D.Double(62, 68));
		assertEquals(8, diagram.getRootNodes().size());
	}
}

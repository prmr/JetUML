package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * Tests various interactions with Object Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Object Diagram.
 * 
 * @author Jiajun Chen
 *
 */

public class TestUsageScenariosObjectDiagram 
{
	private ObjectDiagramGraph aDiagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard clipboard;
	private ObjectNode aObjectNode1;
	private ObjectNode aObjectNode2;
	private FieldNode aFieldNode1;
	private FieldNode aFieldNode2;
	private FieldNode aFieldNode3;
	private ObjectReferenceEdge aReferenceEdge1;
	private ObjectReferenceEdge aReferenceEdge2;
	
	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		aDiagram = new ObjectDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram));
		aGrid = new Grid();
		clipboard = new Clipboard();
		aObjectNode1 = new ObjectNode();
		aObjectNode2 = new ObjectNode();
		aFieldNode1 = new FieldNode();
		aFieldNode2 = new FieldNode();
		aFieldNode3 = new FieldNode();
		
		aReferenceEdge1 = new ObjectReferenceEdge();
		aReferenceEdge2 = new ObjectReferenceEdge();
	}
	
	/**
	 * Below are methods testing basic nodes and edge creation
	 * for a object diagram
	 * 
	 * 
	 * Testing create a object diagram.
	 */
	@Test
	public void testCreateStateDiagram()
	{
		// create an object node
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.draw(aGraphics, aGrid);
		MultiLineString name = new MultiLineString();
		name.setText("Car");
		aObjectNode1.setName(name);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals("Car", aObjectNode1.getName().getText());
		
		// create field node outside an object node.(not allowed)
		aDiagram.addNode(aFieldNode1, new Point2D.Double(120, 80));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(230, 40));
		aDiagram.addNode(aFieldNode3, new Point2D.Double(-20, -20));
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aObjectNode1.getChildren().size());
		
		// create field nodes inside object node
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(30, 40));
		aDiagram.addNode(aFieldNode3, new Point2D.Double(40, 30));
		assertEquals(3, aObjectNode1.getChildren().size());
		
	}
	
	/**
	 * Testing connect NoteNode with Nodes in object diagram.
	 */
	@Test
	public void testNoteNodeWithNoteEdges()
	{
		// adding one ObjectNode and one NoteNode
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(30, 40));
		aDiagram.addNode(aFieldNode3, new Point2D.Double(40, 30));
		aDiagram.addNode(noteNode, new Point2D.Double(80, 80));
		aDiagram.draw(aGraphics, aGrid);		
		assertEquals(3, aDiagram.getRootNodes().size());
		
		NoteEdge edge1 = new NoteEdge();
		NoteEdge edge2 = new NoteEdge();
		NoteEdge edge3 = new NoteEdge();
		NoteEdge edge4 = new NoteEdge();
		NoteEdge edge5 = new NoteEdge();
		
		// link NoteEdge from anywhere to anywhere except to NoteNode (not allowed)
		aDiagram.addEdge(edge1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
		aDiagram.addEdge(edge2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		aDiagram.addEdge(edge3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
		aDiagram.addEdge(edge4, new Point2D.Double(155, 25), new Point2D.Double(55, 25));
		aDiagram.addEdge(edge5, new Point2D.Double(25, 25), new Point2D.Double(255, 25));
		assertEquals(0, aDiagram.getEdges().size());
		
		// create NoteEdge from NoteNode to anywhere and from ObjectNode to NoteNode
		edge1 = new NoteEdge();
		edge2 = new NoteEdge();
		aDiagram.addEdge(edge1, new Point2D.Double(80, 80), new Point2D.Double(55, 25));
		aDiagram.addEdge(edge2, new Point2D.Double(25, 25), new Point2D.Double(80, 80));
		assertEquals(2, aDiagram.getEdges().size());
		
		// create NoteEdge from FieldNode to NoteNode (not allowed)
		edge1 = new NoteEdge();
		edge2 = new NoteEdge();
		aDiagram.addEdge(edge1, new Point2D.Double(60, 80), new Point2D.Double(80, 80));
		assertEquals(2, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing general edge creations in object diagram.
	 */
	@Test
	public void testGeneralEdgeCreation()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(30, 40));
		aDiagram.addNode(aFieldNode3, new Point2D.Double(40, 30));
		aDiagram.addNode(noteNode, new Point2D.Double(80, 80));
		aDiagram.draw(aGraphics, aGrid);	
		
		// create an association edge between two ObjectNode
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
		assertEquals(1, aDiagram.getEdges().size());
		
		// create an association edge between NoteNode and ObjectNode (not allowed)
		collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point2D.Double(25, 20), new Point2D.Double(80, 80));
		assertEquals(1, aDiagram.getEdges().size());
		
		// create an ObjectRefEdge to a NoteNode. (not allowed)
		aDiagram.addEdge(aReferenceEdge1, new Point2D.Double(25, 20), new Point2D.Double(80, 80));
		assertEquals(1, aDiagram.getEdges().size());
		
		/* create an ObjectRefEdge to an ObjectNode itself. 
		 * "value" text in field node will be erased and edge will be added.
		 */
		aReferenceEdge1 = new ObjectReferenceEdge();
		aDiagram.addEdge(aReferenceEdge1, new Point2D.Double(60, 100), new Point2D.Double(20, 20));
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals("", aFieldNode1.getName().getText());
		
		// create ObjectRefEdge from the other field to a different ObjectNode
		aDiagram.addEdge(aReferenceEdge2, new Point2D.Double(60, 120), new Point2D.Double(150, 20));
		assertEquals(3, aDiagram.getEdges().size());
		assertEquals(aFieldNode2, aReferenceEdge2.getStart());
		assertEquals(aObjectNode2, aReferenceEdge2.getEnd());
		
		// change the property of a field
		MultiLineString name = new MultiLineString();
		name.setText("Car");
		aFieldNode3.setName(name);
		assertEquals("Car", aFieldNode3.getName().getText());
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
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(noteNode, new Point2D.Double(80, 80));
		aDiagram.draw(aGraphics, aGrid);	
	
		aObjectNode1.translate(3, 12);
		noteNode.translate(40, 20);
		assertEquals(new Rectangle2D.Double(23, 32, 100, 100), aObjectNode1.getBounds());
		// field node should also be moved accordingly
		assertEquals(new Rectangle2D.Double(60, 111, 56, 16), aFieldNode1.getBounds());
		assertEquals(new Rectangle2D.Double(120, 100, 60, 40), noteNode.getBounds());
	}
	
	/**
	 * Testing nodes and edges movement.
	 */
	@Test
	public void testNodesAndEdgesMovement()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(30, 40));
		aDiagram.addNode(noteNode, new Point2D.Double(80, 80));
		aDiagram.draw(aGraphics, aGrid);	

		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
		aDiagram.addEdge(aReferenceEdge1, new Point2D.Double(60, 100), new Point2D.Double(20, 20));
		aDiagram.addEdge(aReferenceEdge2, new Point2D.Double(60, 120), new Point2D.Double(150, 20));
		aPanel.selectAll();

		Rectangle2D referenceEdge1Bounds = aReferenceEdge1.getBounds();
		Rectangle2D referenceEdge2Bounds = aReferenceEdge2.getBounds();
		Rectangle2D collaborationEdge1Bounds = collaborationEdge1.getBounds();

		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		assertEquals(new Rectangle2D.Double(46, 57, 100, 120), aObjectNode1.getBounds());
		assertEquals(new Rectangle2D.Double(83, 135, 56, 16), aFieldNode1.getBounds());
		assertEquals(new Rectangle2D.Double(106, 117, 60, 40), noteNode.getBounds());
		// edges are redrawn accordingly
		assertEquals(aFieldNode1, aReferenceEdge1.getStart());
		assertEquals(aObjectNode1, aReferenceEdge1.getEnd());
		assertEquals(aFieldNode2, aReferenceEdge2.getStart());
		assertEquals(aObjectNode2, aReferenceEdge2.getEnd());
		assertEquals(aObjectNode1, collaborationEdge1.getStart());
		assertEquals(aObjectNode2, collaborationEdge1.getEnd());
		assertFalse(referenceEdge1Bounds == aReferenceEdge1.getBounds());
		assertFalse(referenceEdge2Bounds == aReferenceEdge2.getBounds());
		assertFalse(collaborationEdge1Bounds == collaborationEdge1.getBounds());
		
		referenceEdge1Bounds = aReferenceEdge1.getBounds();
		referenceEdge2Bounds = aReferenceEdge2.getBounds();
		aObjectNode1.translate(-5, -5);
		assertFalse(referenceEdge1Bounds == aReferenceEdge1.getBounds());
		assertFalse(referenceEdge2Bounds == aReferenceEdge2.getBounds());
	}
	
	/**
	 * Below are methods testing deletion and undo feature for object diagram.
	 * 
	 * 
	 * 
	 * Testing delete an ObjectNode and NoteNode.
	 */
	@Test
	public void testDeleteObjectNodeAndNoteNode()
	{
		
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aPanel.getSelectionList().add(aObjectNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());

		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
		
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(noteNode, new Point2D.Double(75, 75));
		aPanel.getSelectionList().add(noteNode);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());

		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	/**
	 * Testing delete a FieldNode
	 */
	@Test
	public void testDeleteFieldNode()
	{
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));

		aPanel.getSelectionList().add(aFieldNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aObjectNode1.getChildren().size());

		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aObjectNode1.getChildren().size());
	}
	
	/**
	 * Testing delete an edge
	 */
	@Test
	public void testDeleteEdge()
	{
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(30, 40));
		aDiagram.draw(aGraphics, aGrid);	
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
		aDiagram.addEdge(aReferenceEdge1, new Point2D.Double(60, 110), new Point2D.Double(20, 20));
		aDiagram.addEdge(aReferenceEdge2, new Point2D.Double(60, 120), new Point2D.Double(150, 20));

		// delete aReferenceEdge2 and collaborationEdge1
		aPanel.getSelectionList().add(aReferenceEdge2);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getEdges().size());
		aPanel.getSelectionList().add(collaborationEdge1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(2, aDiagram.getEdges().size());
		aPanel.undo();
		assertEquals(3, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a combination of node and edge
	 */
	@Test
	public void testDeleteCombinationNodeAndEdge()
	{
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aDiagram.addNode(aFieldNode2, new Point2D.Double(30, 40));
		aDiagram.draw(aGraphics, aGrid);	
		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(assoEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
		aDiagram.addEdge(aReferenceEdge1, new Point2D.Double(60, 110), new Point2D.Double(20, 20));
		aDiagram.addEdge(aReferenceEdge2, new Point2D.Double(60, 120), new Point2D.Double(150, 20));

		// delete aObjectNode1 and all 3 edges
		aPanel.getSelectionList().add(aObjectNode1);
		aPanel.getSelectionList().add(assoEdge1);
		aPanel.getSelectionList().add(aReferenceEdge1);
		aPanel.getSelectionList().add(aReferenceEdge2);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(3, aDiagram.getEdges().size());
		
		// now delete aFieldNode2 and aReferenceEdge2
		aPanel.getSelectionList().add(aFieldNode2);
		aPanel.getSelectionList().add(aReferenceEdge2);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aObjectNode1.getChildren().size());
		assertEquals(2, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(2, aObjectNode1.getChildren().size());
		assertEquals(3, aDiagram.getEdges().size());
	}
	/**
	 * Below are methods testing copy and paste feature for object diagram
	 * 
	 * 
	 * Testing copy a Node
	 */
	@Test
	public void testCopyNode()
	{
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aPanel.getSelectionList().add(aObjectNode1);
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, ((ObjectNode) aDiagram.getRootNodes().toArray()[1]).getChildren().size());
		assertEquals(new Rectangle2D.Double(0, 0, 100, 100), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[1]).getBounds());
		
		// paste a FieldNode itself is not allowed
		aPanel.getSelectionList().clearSelection();
		aPanel.getSelectionList().add(aFieldNode1);
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	/**
	 * 
	 * Testing cut a State Node
	 */
	@Test
	public void testCutStateNode()
	{
		aDiagram.addNode(aObjectNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aFieldNode1, new Point2D.Double(20, 40));
		aPanel.getSelectionList().add(aObjectNode1);
		clipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		clipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(1, ((ObjectNode) aDiagram.getRootNodes().toArray()[0]).getChildren().size());
		assertEquals(new Rectangle2D.Double(0, 0, 100, 100), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[0]).getBounds());
		
		// a FieldNode will be cut, but will not be pasted
		aPanel.getSelectionList().clearSelection();
		aPanel.getSelectionList().add(aFieldNode1);
		clipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aObjectNode1.getChildren().size());
		
		clipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge
	 */
	@Test
	public void testCopyNodesWithEdge()
	{
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addNode(aObjectNode1, new Point2D.Double(50, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.draw(aGraphics, aGrid);
		aDiagram.addEdge(collaborationEdge1, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);

		aDiagram.draw(aGraphics, aGrid);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Rectangle2D.Double(0, 0, 80, 60), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[2]).getBounds());
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge
	 */
	@Test
	public void testCutNodesWithEdge()
	{
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addNode(aObjectNode1, new Point2D.Double(50, 20));
		aDiagram.addNode(aObjectNode2, new Point2D.Double(150, 20));
		aDiagram.draw(aGraphics, aGrid);
		aDiagram.addEdge(collaborationEdge1, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
		
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		clipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Rectangle2D.Double(0, 0, 80, 60), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[0]).getBounds());
	}
}

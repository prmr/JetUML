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

public class TestUsageScenariosUsageDiagram 
{
	private ObjectDiagramGraph diagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard clipboard;
	private ActorNode actor1;
	private ActorNode actor2;
	private UseCaseNode useNode1;
	private UseCaseNode useNode2;
	private UseCaseAssociationEdge assoEdge;
	private UseCaseDependencyEdge depeEdge;
	private UseCaseGeneralizationEdge geneEdge;

	
	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		diagram = new ObjectDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGrid = new Grid();
		clipboard = new Clipboard();
		actor1 = new ActorNode();
		actor2 = new ActorNode();
		useNode1 = new UseCaseNode();
		useNode2 = new UseCaseNode();
		assoEdge = new UseCaseAssociationEdge();
		depeEdge = new UseCaseDependencyEdge();
		geneEdge = new UseCaseGeneralizationEdge();
	}
	
	/**
	 * Below are methods testing basic nodes and edge creation
	 * for a object diagram
	 * 
	 * 
	 * test create a object diagram
	 */
	@Test
	public void testCreateUsageDiagram()
	{
		// create an object node
		diagram.addNode(actor1, new Point2D.Double(20, 20));
		diagram.draw(aGraphics, aGrid);
		MultiLineString name = new MultiLineString();
		name.setText("Car");
		actor1.setName(name);
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals("Car", actor1.getName().getText());
		
		// create field node outside an object node.(not allowed)
		diagram.addNode(useNode1, new Point2D.Double(120, 80));
		name = new MultiLineString();
		name.setText("driving");
		useNode1.setName(name);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals("driving", useNode1.getName().getText());

		// create field nodes insdie object node
		NoteNode noteNode = new NoteNode();
		diagram.addNode(noteNode, new Point2D.Double(50, 50));
		name = new MultiLineString();
		name.setText("something...\nsomething");
		noteNode.setText(name);
		assertEquals(3, diagram.getRootNodes().size());
		assertEquals("something...\nsomething", noteNode.getText().getText());
	}
	
	/**
	 * Testing edge creation except NoteEdge
	 */
	@Test
	public void testGeneralEdgeCreation()
	{
		diagram.addNode(actor1, new Point2D.Double(20, 20));
		diagram.addNode(actor2, new Point2D.Double(200, 20));
		diagram.addNode(useNode1, new Point2D.Double(80, 20));
		diagram.addNode(useNode2, new Point2D.Double(140, 20));
		
		diagram.addEdge(assoEdge,  new Point2D.Double(20, 20), new Point2D.Double(200, 20));
		diagram.addEdge(depeEdge,  new Point2D.Double(80, 20), new Point2D.Double(200, 20));
		diagram.addEdge(geneEdge,  new Point2D.Double(20, 20), new Point2D.Double(140, 20));
		assertEquals(3, diagram.getEdges().size());
		
		// creating more edges
		diagram.addEdge(new UseCaseAssociationEdge(),  new Point2D.Double(80, 20), new Point2D.Double(140, 20));
		diagram.addEdge(new UseCaseDependencyEdge(),  new Point2D.Double(20, 20), new Point2D.Double(200, 20));
		diagram.addEdge(new UseCaseGeneralizationEdge(),  new Point2D.Double(80, 20), new Point2D.Double(140, 20));
		assertEquals(6, diagram.getEdges().size());
	}
	
	/**
	 * Testing NoteEdge creation
	 */
	public void testNoteEdgeCreation()
	{
		NoteNode noteNode = new NoteNode();
		diagram.addNode(actor1, new Point2D.Double(20, 20));
		diagram.addNode(actor2, new Point2D.Double(200, 20));
		diagram.addNode(useNode1, new Point2D.Double(80, 20));
		diagram.addNode(useNode2, new Point2D.Double(140, 20));
		diagram.addNode(noteNode, new Point2D.Double(100, 100));
		
		NoteEdge edge1 = new NoteEdge();
		NoteEdge edge2 = new NoteEdge();
		NoteEdge edge3 = new NoteEdge();
		NoteEdge edge4 = new NoteEdge();
		NoteEdge edge5 = new NoteEdge();

		
	}
	
	
	
	
	 
//	
//	/**
//	 * Testing connect NoteNode with Nodes in obejct diagram.
//	 */
//	@Test
//	public void testNoteNodeWithNoteEdges()
//	{
//		// adding one ObjectNode and one NoteNode
//		NoteNode noteNode = new NoteNode();
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		diagram.addNode(fieldNode2, new Point2D.Double(30, 40));
//		diagram.addNode(fieldNode3, new Point2D.Double(40, 30));
//		diagram.addNode(noteNode, new Point2D.Double(75, 75));
//		diagram.draw(aGraphics, aGrid);		
//		assertEquals(3, diagram.getRootNodes().size());
//		
//		NoteEdge note1 = new NoteEdge();
//		NoteEdge note2 = new NoteEdge();
//		NoteEdge note3 = new NoteEdge();
//		NoteEdge note4 = new NoteEdge();
//		NoteEdge note5 = new NoteEdge();
//		
//		// link NoteEdge from anywhere to anywhere except to NoteNode (not allowed)
//		diagram.addEdge(note1, new Point2D.Double(25, 25), new Point2D.Double(55, 25));
//		diagram.addEdge(note2, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
//		diagram.addEdge(note3, new Point2D.Double(155, 25), new Point2D.Double(255, 25));
//		diagram.addEdge(note4, new Point2D.Double(155, 25), new Point2D.Double(55, 25));
//		diagram.addEdge(note5, new Point2D.Double(25, 25), new Point2D.Double(255, 25));
//		assertEquals(0, diagram.getEdges().size());
//		
//		// create NoteEdge from NoteNode to anywhere and from ObjectNode to NoteNode
//		note1 = new NoteEdge();
//		note2 = new NoteEdge();
//		diagram.addEdge(note1, new Point2D.Double(80, 80), new Point2D.Double(55, 25));
//		diagram.addEdge(note2, new Point2D.Double(25, 25), new Point2D.Double(80, 80));
//		assertEquals(2, diagram.getEdges().size());
//		
//		// create NoteEdge from FieldNode to NoteNode (not allowed)
//		note1 = new NoteEdge();
//		note2 = new NoteEdge();
//		diagram.addEdge(note1, new Point2D.Double(60, 80), new Point2D.Double(80, 80));
//		assertEquals(2, diagram.getEdges().size());
//	}
//	
//	/**
//	 * Testing general edge creations in Object Diagram
//	 */
//	@Test
//	public void testGeneralEdgeCreation()
//	{
//		NoteNode noteNode = new NoteNode();
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		diagram.addNode(fieldNode2, new Point2D.Double(30, 40));
//		diagram.addNode(fieldNode3, new Point2D.Double(40, 30));
//		diagram.addNode(noteNode, new Point2D.Double(75, 75));
//		diagram.draw(aGraphics, aGrid);	
//		
//		// create an association edge between two ObjectNode
//		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addEdge(assoEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
//		assertEquals(1, diagram.getEdges().size());
//		
//		// create an association edge between NoteNode and ObjectNode (not allowed)
//		assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addEdge(assoEdge1, new Point2D.Double(25, 20), new Point2D.Double(80, 80));
//		assertEquals(1, diagram.getEdges().size());
//		
//		// create an ObjectRefEdge to a NoteNode. (not allowed)
//		diagram.addEdge(edge1, new Point2D.Double(25, 20), new Point2D.Double(80, 80));
//		assertEquals(1, diagram.getEdges().size());
//		
//		/* create an ObjectRefEdge to an ObjectNode itself. 
//		 * "value" text in field node will be erased and edge will be added.
//		 */
//		edge1 = new ObjectReferenceEdge();
//		diagram.addEdge(edge1, new Point2D.Double(60, 80), new Point2D.Double(20, 20));
//		assertEquals(2, diagram.getEdges().size());
//		assertEquals("", fieldNode1.getName().getText());
//		
//		// create ObjectRefEdge from the other field to a different ObjectNode
//		diagram.addEdge(edge2, new Point2D.Double(60, 110), new Point2D.Double(150, 20));
//		assertEquals(3, diagram.getEdges().size());
//		assertEquals(fieldNode2, edge2.getStart());
//		assertEquals(objNode2, edge2.getEnd());
//		
//		// change the property of a field
//		MultiLineString name = new MultiLineString();
//		name.setText("Car");
//		fieldNode3.setName(name);
//		assertEquals("Car", fieldNode3.getName().getText());
//	}
//
//	
//	/**
//	 * Below are methods testing nodes movement
//	 * 
//	 * 
//	 * 
//	 * Testing individual node movement
//	 */
//	@Test
//	public void testIndividualNodeMovement()
//	{
//		NoteNode noteNode = new NoteNode();
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		diagram.addNode(noteNode, new Point2D.Double(75, 75));
//		diagram.draw(aGraphics, aGrid);	
//	
//		Rectangle2D objNode1_bond = objNode1.getBounds();
//		Rectangle2D fieldNode1_bond = fieldNode1.getBounds();
//		Rectangle2D noteNode_bond = noteNode.getBounds();
//		objNode1.translate(3, 12);
//		noteNode.translate(40, 20);
//		// field node should also be moved accordingly
//		assertTrue(objNode1_bond.getX() + 3 == objNode1.getBounds().getX());
//		assertTrue(objNode1_bond.getY() + 12 == objNode1.getBounds().getY());
//		assertTrue(fieldNode1_bond.getX() + 3 == fieldNode1.getBounds().getX());
//		assertTrue(fieldNode1_bond.getY() + 12 == fieldNode1.getBounds().getY());
//		assertTrue(noteNode_bond.getX() + 40 == noteNode.getBounds().getX());
//		assertTrue(noteNode_bond.getY() + 20 == noteNode.getBounds().getY());
//		
////		// move a FieldNode suppoesed to not be allowed 
////		fieldNode1_bond = fieldNode1.getBounds();
////		System.out.println(fieldNode1.getBounds());
////		fieldNode1.translate(-5, 80);
////		diagram.draw(aGraphics, aGrid);
////		System.out.println(fieldNode1.getBounds());
////		System.out.println(fieldNode1.getParent().getClass().getSimpleName());
////		assertEquals(fieldNode1_bond, fieldNode1.getBounds());
//	}
//	
//	@Test
//	public void testNodesAndEdgesMovement()
//	{
//		NoteNode noteNode = new NoteNode();
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		diagram.addNode(fieldNode2, new Point2D.Double(30, 40));
//		diagram.addNode(noteNode, new Point2D.Double(75, 75));
//		diagram.draw(aGraphics, aGrid);	
//		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addEdge(assoEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
//		diagram.addEdge(edge1, new Point2D.Double(60, 80), new Point2D.Double(20, 20));
//		diagram.addEdge(edge2, new Point2D.Double(60, 110), new Point2D.Double(150, 20));
//
//		aPanel.selectAll();
//
//		Rectangle2D objNode1_bond = objNode1.getBounds();
//		Rectangle2D objNode2_bond = objNode2.getBounds();
//		Rectangle2D fieldNode1_bond = fieldNode1.getBounds();
//		Rectangle2D noteNode_bond = noteNode.getBounds();
//		Rectangle2D edge1_bond = edge1.getBounds();
//		Rectangle2D edge2_bond = edge2.getBounds();
//		Rectangle2D assoEdge_bond = assoEdge1.getBounds();
//
//		for(GraphElement element: aPanel.getSelectionList())
//		{
//			if(element instanceof Node)
//			{
//				((Node) element).translate(26, 37);
//			}
//		}
//		assertTrue(objNode1_bond.getX() + 26 == objNode1.getBounds().getX());
//		assertTrue(objNode2_bond.getY() + 37 == objNode2.getBounds().getY());
//		assertTrue(fieldNode1_bond.getX() + 26 == fieldNode1.getBounds().getX());
//		assertTrue(fieldNode1_bond.getY() + 37 == fieldNode1.getBounds().getY());
//		assertTrue(noteNode_bond.getX() + 26 == noteNode.getBounds().getX());
//		assertTrue(noteNode_bond.getY() + 37 == noteNode.getBounds().getY());
//		assertEquals(fieldNode1, edge1.getStart());
//		assertEquals(objNode1, edge1.getEnd());
//		assertEquals(fieldNode2, edge2.getStart());
//		assertEquals(objNode2, edge2.getEnd());
//		assertEquals(objNode1, assoEdge1.getStart());
//		assertEquals(objNode2, assoEdge1.getEnd());
//		assertFalse(edge1_bond == edge1.getBounds());
//		assertFalse(edge2_bond == edge2.getBounds());
//		assertFalse(assoEdge_bond == assoEdge1.getBounds());
//		
//		edge1_bond = edge1.getBounds();
//		edge2_bond = edge2.getBounds();
//		objNode1.translate(-5, -5);
//		assertFalse(edge1_bond == edge1.getBounds());
//		assertFalse(edge2_bond == edge2.getBounds());
//	}
//	
//	/**
//	 * Below are methods testing deletion and undo feature for object diagragm.
//	 * 
//	 * 
//	 * Testing delete an ObjectNode and NoteNode
//	 */
//	@Test
//	public void testDeleteObjectNodeAndNoteNode()
//	{
//		
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		aPanel.getSelectionList().add(objNode1);
//		aPanel.removeSelected();
//		aPanel.getSelectionList().clearSelection();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(0, diagram.getRootNodes().size());
//
//		aPanel.undo();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, diagram.getRootNodes().size());
//		
//		NoteNode noteNode = new NoteNode();
//		diagram.addNode(noteNode, new Point2D.Double(75, 75));
//		aPanel.getSelectionList().add(noteNode);
//		aPanel.removeSelected();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, diagram.getRootNodes().size());
//
//		aPanel.undo();
//		assertEquals(2, diagram.getRootNodes().size());
//	}
//	
//	/**
//	 * Testing delete a FieldNode
//	 */
//	@Test
//	public void testDeleteFieldNode()
//	{
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//
//		aPanel.getSelectionList().add(fieldNode1);
//		aPanel.removeSelected();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(0, objNode1.getChildren().size());
//
//		aPanel.undo();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, objNode1.getChildren().size());
//	}
//	
//	/**
//	 * Testing delete an edge
//	 */
//	@Test
//	public void testDeleteEdge()
//	{
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		diagram.addNode(fieldNode2, new Point2D.Double(30, 40));
//		diagram.draw(aGraphics, aGrid);	
//		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addEdge(assoEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
//		diagram.addEdge(edge1, new Point2D.Double(60, 80), new Point2D.Double(20, 20));
//		diagram.addEdge(edge2, new Point2D.Double(60, 110), new Point2D.Double(150, 20));
//
//		// delete edge2 and assoEdge1
//		aPanel.getSelectionList().add(edge2);
//		aPanel.removeSelected();
//		aPanel.getSelectionList().clearSelection();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(2, diagram.getEdges().size());
//		aPanel.getSelectionList().add(assoEdge1);
//		aPanel.removeSelected();
//		aPanel.getSelectionList().clearSelection();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, diagram.getEdges().size());
//		
//		aPanel.undo();
//		assertEquals(2, diagram.getEdges().size());
//		aPanel.undo();
//		assertEquals(3, diagram.getEdges().size());
//	}
//	
//	/**
//	 * Testing delete a combination of node and edge
//	 */
//	@Test
//	public void testDeleteCombinationNodeAndEdge()
//	{
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		diagram.addNode(fieldNode2, new Point2D.Double(30, 40));
//		diagram.draw(aGraphics, aGrid);	
//		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addEdge(assoEdge1, new Point2D.Double(25, 20), new Point2D.Double(165, 20));
//		diagram.addEdge(edge1, new Point2D.Double(60, 80), new Point2D.Double(20, 20));
//		diagram.addEdge(edge2, new Point2D.Double(60, 110), new Point2D.Double(150, 20));
//
//		// delete objNode1 and all 3 edges
//		aPanel.getSelectionList().add(objNode1);
//		aPanel.getSelectionList().add(assoEdge1);
//		aPanel.getSelectionList().add(edge1);
//		aPanel.getSelectionList().add(edge2);
//		aPanel.removeSelected();
//		aPanel.getSelectionList().clearSelection();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, diagram.getRootNodes().size());
//		assertEquals(0, diagram.getEdges().size());
//		
//		aPanel.undo();
//		assertEquals(2, diagram.getRootNodes().size());
//		assertEquals(3, diagram.getEdges().size());
//		
//		// now delete fieldNode2 and edge2
//		aPanel.getSelectionList().add(fieldNode2);
//		aPanel.getSelectionList().add(edge2);
//		aPanel.removeSelected();
//		aPanel.getSelectionList().clearSelection();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(2, diagram.getRootNodes().size());
//		assertEquals(1, objNode1.getChildren().size());
//		assertEquals(2, diagram.getEdges().size());
//		
//		aPanel.undo();
//		assertEquals(2, diagram.getRootNodes().size());
//		assertEquals(2, objNode1.getChildren().size());
//		assertEquals(3, diagram.getEdges().size());
//	}
//	/**
//	 * Below are methods testing copy and paste feature for object diagram
//	 * 
//	 * 
//	 * Testing copy a Node
//	 */
//	@Test
//	public void testCopyNode()
//	{
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		aPanel.getSelectionList().add(objNode1);
//		clipboard.copy(aPanel.getSelectionList());
//		clipboard.paste(aPanel);
//		diagram.draw(aGraphics, aGrid);
//		
//		assertEquals(2, diagram.getRootNodes().size());
//		assertEquals(1, ((ObjectNode) diagram.getRootNodes().toArray()[1]).getChildren().size());
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[1]).getBounds().getX()));
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[1]).getBounds().getY()));
//		
//		// paste a FieldNode is not allowed
//		aPanel.getSelectionList().clearSelection();
//		aPanel.getSelectionList().add(fieldNode1);
//		clipboard.copy(aPanel.getSelectionList());
//		clipboard.paste(aPanel);
//		diagram.draw(aGraphics, aGrid);
//		
//		assertEquals(2, diagram.getRootNodes().size());
//	}
//	
//	/**
//	 * 
//	 * Testing cut a State Node
//	 */
//	@Test
//	public void testCutStateNode()
//	{
//		diagram.addNode(objNode1, new Point2D.Double(20, 20));
//		diagram.addNode(fieldNode1, new Point2D.Double(20, 40));
//		aPanel.getSelectionList().add(objNode1);
//		clipboard.copy(aPanel.getSelectionList());
//		aPanel.removeSelected();
//		diagram.draw(aGraphics, aGrid);
//		
//		clipboard.paste(aPanel);
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, diagram.getRootNodes().size());
//		assertEquals(1, ((ObjectNode) diagram.getRootNodes().toArray()[0]).getChildren().size());
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[0]).getBounds().getX()));
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[0]).getBounds().getY()));
//		
//		// paste a FieldNode is not allowed
//		aPanel.getSelectionList().clearSelection();
//		aPanel.getSelectionList().add(fieldNode1);
//		clipboard.copy(aPanel.getSelectionList());
//		aPanel.removeSelected();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(0, objNode1.getChildren().size());
//		
//		clipboard.paste(aPanel);
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(1, diagram.getRootNodes().size());
//	}
//	
//	/**
//	 * 
//	 * Testing copy two Node with an edge
//	 */
//	@Test
//	public void testCopyNodesWithEdge()
//	{
//		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addNode(objNode1, new Point2D.Double(50, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.draw(aGraphics, aGrid);
//		diagram.addEdge(assoEdge1, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
//		aPanel.selectAll();
//		clipboard.copy(aPanel.getSelectionList());
//		clipboard.paste(aPanel);
//
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(4, diagram.getRootNodes().size());
//		assertEquals(2, diagram.getEdges().size());
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[2]).getBounds().getX()));
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[2]).getBounds().getY()));
//	}
//	
//	/**
//	 * 
//	 * Testing copy two Node with an edge
//	 */
//	@Test
//	public void testCutNodesWithEdge()
//	{
//		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
//		diagram.addNode(objNode1, new Point2D.Double(50, 20));
//		diagram.addNode(objNode2, new Point2D.Double(150, 20));
//		diagram.draw(aGraphics, aGrid);
//		diagram.addEdge(assoEdge1, new Point2D.Double(55, 25), new Point2D.Double(155, 25));
//		
//		aPanel.selectAll();
//		clipboard.copy(aPanel.getSelectionList());
//		aPanel.removeSelected();
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(0, diagram.getRootNodes().size());
//		assertEquals(0, diagram.getEdges().size());
//
//		clipboard.paste(aPanel);
//		diagram.draw(aGraphics, aGrid);
//		assertEquals(2, diagram.getRootNodes().size());
//		assertEquals(1, diagram.getEdges().size());
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[0]).getBounds().getX()));
//		assertTrue(0 == (((ObjectNode) diagram.getRootNodes().toArray()[0]).getBounds().getY()));
//	}
//	
	
	
}

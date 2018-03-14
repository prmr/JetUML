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
package ca.mcgill.cs.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.graph.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.graph.nodes.FieldNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.gui.GraphPanel;
import ca.mcgill.cs.jetuml.gui.ToolBar;

/**
 * Tests various interactions with Object Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Object Diagram.
 * 
 * @author Jiajun Chen
 * @author Martin P. Robillard - Modifications to Clipboard API
 *
 */

public class TestUsageScenariosObjectDiagram 
{
	private ObjectDiagramGraph aDiagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private ObjectNode aObjectNode1;
	private ObjectNode aObjectNode2;
	private FieldNode aFieldNode1;
	private FieldNode aFieldNode2;
	private FieldNode aFieldNode3;
	private ObjectReferenceEdge aReferenceEdge1;
	private ObjectReferenceEdge aReferenceEdge2;
	
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
		aDiagram = new ObjectDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram), null);
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
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.draw(aGraphics);
		aObjectNode1.setName("Car");
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals("Car", aObjectNode1.getName());
		
		// create field node outside an object node.(not allowed)
		aDiagram.addNode(aFieldNode1, new Point(120, 80));
		aDiagram.addNode(aFieldNode2, new Point(230, 40));
		aDiagram.addNode(aFieldNode3, new Point(-20, -20));
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aObjectNode1.getChildren().size());
		
		// create field nodes inside object node
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(aFieldNode2, new Point(30, 40));
		aDiagram.addNode(aFieldNode3, new Point(40, 30));
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
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(aFieldNode2, new Point(30, 40));
		aDiagram.addNode(aFieldNode3, new Point(40, 30));
		aDiagram.addNode(noteNode, new Point(80, 80));
		aDiagram.draw(aGraphics);		
		assertEquals(3, aDiagram.getRootNodes().size());
		
		NoteEdge edge1 = new NoteEdge();
		NoteEdge edge2 = new NoteEdge();
		NoteEdge edge3 = new NoteEdge();
		NoteEdge edge4 = new NoteEdge();
		NoteEdge edge5 = new NoteEdge();
		
		// link NoteEdge from anywhere to anywhere except to NoteNode (not allowed)
		aDiagram.addEdge(edge1, new Point(25, 25), new Point(55, 25));
		aDiagram.addEdge(edge2, new Point(55, 25), new Point(155, 25));
		aDiagram.addEdge(edge3, new Point(155, 25), new Point(255, 25));
		aDiagram.addEdge(edge4, new Point(155, 25), new Point(55, 25));
		aDiagram.addEdge(edge5, new Point(25, 25), new Point(255, 25));
		assertEquals(0, aDiagram.getEdges().size());
		
		// create NoteEdge from NoteNode to anywhere and from ObjectNode to NoteNode
		edge1 = new NoteEdge();
		edge2 = new NoteEdge();
		aDiagram.addEdge(edge1, new Point(80, 80), new Point(55, 25));
		aDiagram.addEdge(edge2, new Point(25, 25), new Point(80, 80));
		assertEquals(2, aDiagram.getEdges().size());
		
		// create NoteEdge from FieldNode to NoteNode (not allowed)
		edge1 = new NoteEdge();
		edge2 = new NoteEdge();
		aDiagram.addEdge(edge1, new Point(60, 80), new Point(80, 80));
		assertEquals(2, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing general edge creations in object diagram.
	 */
	@Test
	public void testGeneralEdgeCreation()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(aFieldNode2, new Point(30, 40));
		aDiagram.addNode(aFieldNode3, new Point(40, 30));
		aDiagram.addNode(noteNode, new Point(80, 80));
		aDiagram.draw(aGraphics);	
		
		// create an association edge between two ObjectNode
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point(25, 20), new Point(165, 20));
		assertEquals(1, aDiagram.getEdges().size());
		
		// create an association edge between NoteNode and ObjectNode (not allowed)
		collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point(25, 20), new Point(80, 80));
		assertEquals(1, aDiagram.getEdges().size());
		
		// create an ObjectRefEdge to a NoteNode. (not allowed)
		aDiagram.addEdge(aReferenceEdge1, new Point(25, 20), new Point(80, 80));
		assertEquals(1, aDiagram.getEdges().size());
		
		/* create an ObjectRefEdge to an ObjectNode itself. 
		 * "value" text in field node will be erased and edge will be added.
		 */
		aReferenceEdge1 = new ObjectReferenceEdge();
		aDiagram.addEdge(aReferenceEdge1, new Point(65, 100), new Point(20, 20));
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals("", aFieldNode1.getName());
		
		// create ObjectRefEdge from the other field to a different ObjectNode
		aDiagram.addEdge(aReferenceEdge2, new Point(65, 120), new Point(150, 20));
		assertEquals(3, aDiagram.getEdges().size());
		assertEquals(aFieldNode2, aReferenceEdge2.getStart());
		assertEquals(aObjectNode2, aReferenceEdge2.getEnd());
		
		// change the property of a field
		aFieldNode3.setName("Car");
		assertEquals("Car", aFieldNode3.getName());
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
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(noteNode, new Point(80, 80));
		aDiagram.draw(aGraphics);	
	
		aObjectNode1.translate(3, 12);
		noteNode.translate(40, 20);
		assertEquals(new Rectangle(23, 32, 100, 100), aObjectNode1.view().getBounds());
		assertEquals(new Rectangle(64, 111, 49, 16), aFieldNode1.view().getBounds());
		assertEquals(new Rectangle(120, 100, 60, 40), noteNode.view().getBounds());
	}
	
	/**
	 * Testing nodes and edges movement.
	 */
	@Test
	public void testNodesAndEdgesMovement()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(aFieldNode2, new Point(30, 40));
		aDiagram.addNode(noteNode, new Point(80, 80));
		aDiagram.draw(aGraphics);	

		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point(25, 20), new Point(165, 20));
		aDiagram.addEdge(aReferenceEdge1, new Point(65, 100), new Point(20, 20));
		aDiagram.addEdge(aReferenceEdge2, new Point(65, 120), new Point(150, 20));
		aPanel.selectAll();

		Rectangle referenceEdge1Bounds = aReferenceEdge1.view().getBounds();
		Rectangle referenceEdge2Bounds = aReferenceEdge2.view().getBounds();
		Rectangle collaborationEdge1Bounds = collaborationEdge1.view().getBounds();

		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		assertEquals(new Rectangle(46, 57, 100, 120), aObjectNode1.view().getBounds());
		assertEquals(new Rectangle(87, 135, 49, 16), aFieldNode1.view().getBounds());
		assertEquals(new Rectangle(106, 117, 60, 40), noteNode.view().getBounds());
		// edges are redrawn accordingly
		assertEquals(aFieldNode1, aReferenceEdge1.getStart());
		assertEquals(aObjectNode1, aReferenceEdge1.getEnd());
		assertEquals(aFieldNode2, aReferenceEdge2.getStart());
		assertEquals(aObjectNode2, aReferenceEdge2.getEnd());
		assertEquals(aObjectNode1, collaborationEdge1.getStart());
		assertEquals(aObjectNode2, collaborationEdge1.getEnd());
		assertFalse(referenceEdge1Bounds == aReferenceEdge1.view().getBounds());
		assertFalse(referenceEdge2Bounds == aReferenceEdge2.view().getBounds());
		assertFalse(collaborationEdge1Bounds == collaborationEdge1.view().getBounds());
		
		referenceEdge1Bounds = aReferenceEdge1.view().getBounds();
		referenceEdge2Bounds = aReferenceEdge2.view().getBounds();
		aObjectNode1.translate(-5, -5);
		assertFalse(referenceEdge1Bounds == aReferenceEdge1.view().getBounds());
		assertFalse(referenceEdge2Bounds == aReferenceEdge2.view().getBounds());
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
		
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aPanel.getSelectionList().add(aObjectNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());

		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());
		
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(noteNode, new Point(75, 75));
		aPanel.getSelectionList().add(noteNode);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));

		aPanel.getSelectionList().add(aFieldNode1);
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(0, aObjectNode1.getChildren().size());

		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(1, aObjectNode1.getChildren().size());
	}
	
	/**
	 * Testing delete an edge
	 */
	@Test
	public void testDeleteEdge()
	{
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(aFieldNode2, new Point(30, 40));
		aDiagram.draw(aGraphics);	
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(collaborationEdge1, new Point(25, 20), new Point(165, 20));
		aDiagram.addEdge(aReferenceEdge1, new Point(65, 110), new Point(20, 20));
		aDiagram.addEdge(aReferenceEdge2, new Point(65, 120), new Point(150, 20));

		// delete aReferenceEdge2 and collaborationEdge1
		aPanel.getSelectionList().add(aReferenceEdge2);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getEdges().size());
		aPanel.getSelectionList().add(collaborationEdge1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aDiagram.addNode(aFieldNode2, new Point(30, 40));
		aDiagram.draw(aGraphics);	
		
		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
		aDiagram.addEdge(assoEdge1, new Point(25, 20), new Point(165, 20));
		aDiagram.addEdge(aReferenceEdge1, new Point(65, 110), new Point(20, 20));
		aDiagram.addEdge(aReferenceEdge2, new Point(65, 120), new Point(150, 20));

		// delete aObjectNode1 and all 3 edges
		aPanel.getSelectionList().add(aObjectNode1);
		aPanel.getSelectionList().add(assoEdge1);
		aPanel.getSelectionList().add(aReferenceEdge1);
		aPanel.getSelectionList().add(aReferenceEdge2);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
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
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aPanel.getSelectionList().add(aObjectNode1);
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, ((ObjectNode) aDiagram.getRootNodes().toArray()[1]).getChildren().size());
		assertEquals(new Rectangle(0, 0, 100, 100), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[1]).view().getBounds());
		
		// paste a FieldNode itself is not allowed
		aPanel.getSelectionList().clearSelection();
		aPanel.getSelectionList().add(aFieldNode1);
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	/**
	 * 
	 * Testing cut a State Node
	 */
	@Test
	public void testCutStateNode()
	{
		aDiagram.addNode(aObjectNode1, new Point(20, 20));
		aDiagram.addNode(aFieldNode1, new Point(20, 40));
		aPanel.getSelectionList().add(aObjectNode1);
		aPanel.cut();
		aDiagram.draw(aGraphics);
		
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(1, ((ObjectNode) aDiagram.getRootNodes().toArray()[0]).getChildren().size());
		assertEquals(new Rectangle(0, 0, 100, 100), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[0]).view().getBounds());
		
		// a FieldNode will be cut, but will not be pasted
		aPanel.getSelectionList().clearSelection();
		aPanel.getSelectionList().add(aFieldNode1);
		aPanel.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aObjectNode1.getChildren().size());
		
		aPanel.paste();
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aObjectNode1, new Point(50, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.draw(aGraphics);
		aDiagram.addEdge(collaborationEdge1, new Point(55, 25), new Point(155, 25));
		aPanel.selectAll();
		aPanel.copy();
		aPanel.paste();

		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Rectangle(0, 0, 80, 60), 
				((ObjectNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds());
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge
	 */
	@Test
	public void testCutNodesWithEdge()
	{
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		aDiagram.addNode(aObjectNode1, new Point(50, 20));
		aDiagram.addNode(aObjectNode2, new Point(150, 20));
		aDiagram.draw(aGraphics);
		aDiagram.addEdge(collaborationEdge1, new Point(55, 25), new Point(155, 25));
		
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
				((ObjectNode) aDiagram.getRootNodes().toArray()[0]).view().getBounds());
	}
}

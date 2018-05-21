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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagrams.UseCaseDiagram;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.PointNode;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.gui.GraphPanel;
import ca.mcgill.cs.jetuml.gui.DiagramFrameToolBar;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Tests various interactions with Use Case Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Use Case Diagram.
 */
public class TestUsageScenariosUseCaseDiagram 
{
	private UseCaseDiagram aDiagram;
	private GraphicsContext aGraphics;
	private GraphPanel aPanel;
	private ActorNode aActorNode1;
	private ActorNode aActorNode2;
	private UseCaseNode aUseCaseNode1;
	private UseCaseNode aUseCaseNode2;
	private UseCaseAssociationEdge aAssociationEdge;
	private UseCaseDependencyEdge aDependencyEdge;
	private UseCaseGeneralizationEdge aGeneralEdge;
	
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
		aDiagram = new UseCaseDiagram();
		aGraphics = new Canvas(256, 256).getGraphicsContext2D();
		aPanel = new GraphPanel(aDiagram, new DiagramFrameToolBar(aDiagram), new Rectangle2D(0, 0, 0, 0));
		aActorNode1 = new ActorNode();
		aActorNode2 = new ActorNode();
		aUseCaseNode1 = new UseCaseNode();
		aUseCaseNode2 = new UseCaseNode();
		aAssociationEdge = new UseCaseAssociationEdge();
		aDependencyEdge = new UseCaseDependencyEdge();
		aGeneralEdge = new UseCaseGeneralizationEdge();
	}
	
	/**
	 * Below are methods testing basic nodes and edge creation
	 * for a Use Case diagram.
	 * 
	 * 
	 * 
	 * Testing create a Use Case diagram.
	 */
	@Test
	public void testCreateUseCaseDiagram()
	{
		// create an ActorNode
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		aActorNode1.setName("Car");
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals("Car", aActorNode1.getName());
		
		// create some UseCaseNode
		aDiagram.addNode(aUseCaseNode1, new Point(120, 80), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aUseCaseNode1.setName("driving");
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("driving", aUseCaseNode1.getName());

		// create field nodes inside ObjectNode
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(noteNode, new Point(50, 50), Integer.MAX_VALUE, Integer.MAX_VALUE);
		noteNode.setName("something...\nsomething");
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals("something...\nsomething", noteNode.getName());
	}
	
	/**
	 * Testing edge creation except NoteEdge.
	 */
	@Test
	public void testGeneralEdgeCreation()
	{
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		aDiagram.addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		assertEquals(3, aDiagram.getEdges().size());
		
		// create more edges
		aDiagram.addEdge(new UseCaseAssociationEdge(),  new Point(80, 20), new Point(140, 20));
		aDiagram.addEdge(new UseCaseDependencyEdge(),  new Point(20, 20), new Point(250, 20));
		aDiagram.addEdge(new UseCaseGeneralizationEdge(),  new Point(80, 20), new Point(140, 20));
		assertEquals(6, aDiagram.getEdges().size());
		
		// connect nodes with NoteEdge (not allowed)
		aDiagram.addEdge(new NoteEdge(),  new Point(80, 20), new Point(140, 20));
		aDiagram.addEdge(new NoteEdge(),  new Point(20, 20), new Point(250, 20));
		assertEquals(6, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing NoteEdge creation.
	 */
	@Test
	public void testNoteEdgeCreation()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		
		// if begin with a non-NoteNode type, both point needs to be valid
		aDiagram.addEdge(noteEdge1, new Point(9, 9), new Point(209, 162));
		assertEquals(0, aDiagram.getEdges().size());
		aDiagram.addEdge(noteEdge1, new Point(20, 20), new Point(100, 100));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(noteEdge1.getStart(), aActorNode1);
		assertEquals(noteEdge1.getEnd(), noteNode);
		aDiagram.addEdge(noteEdge2, new Point(85, 25), new Point(110, 110));
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(noteEdge2.getStart(), aUseCaseNode1);
		assertEquals(noteEdge2.getEnd(), noteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		aDiagram.addEdge(noteEdge3, new Point(100, 100), new Point(9,9));
		assertEquals(noteEdge3.getStart(), noteNode);
		assertEquals(noteEdge3.getEnd().getClass(), new PointNode().getClass());
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
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);

		aActorNode1.translate(3, 12);
		aUseCaseNode1.translate(3, 2);
		noteNode.translate(40, 20);
		assertTrue(new Rectangle(23, 32, 48, 88).equals(aActorNode1.view().getBounds()) || new Rectangle(23, 32, 48, 87).equals(aActorNode1.view().getBounds()));
		assertEquals(new Rectangle(83, 22, 110, 40), aUseCaseNode1.view().getBounds());
		assertEquals(new Rectangle(140, 120, 60, 40), noteNode.view().getBounds());
	}
	
	/**
	 * Testing nodes and edges movement.
	 */
	@Test
	public void testNodesAndEdgesMovement()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		aDiagram.addEdge(noteEdge1, new Point(85, 25), new Point(110, 110));

		aPanel.selectAll();
		for(DiagramElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		assertTrue(new Rectangle(46, 57, 48, 88).equals(aActorNode1.view().getBounds()) || new Rectangle(46, 57, 48, 87).equals(aActorNode1.view().getBounds()));
		assertTrue(new Rectangle(276, 57, 48, 88).equals(aActorNode2.view().getBounds()) || new Rectangle(276, 57, 48, 87).equals(aActorNode2.view().getBounds()));
		assertEquals(new Rectangle(106, 57, 120, 40), aUseCaseNode1.view().getBounds());
		assertEquals(new Rectangle(166, 57, 120, 40), aUseCaseNode2.view().getBounds());
		assertEquals(new Rectangle(126, 137, 60, 40), noteNode.view().getBounds());
		
		// move a node connect to another node, edge should redraw accordingly,
		aActorNode1.translate(10, 20);
		assertEquals(aActorNode1, aAssociationEdge.getStart());
		assertEquals(aUseCaseNode2, aAssociationEdge.getEnd());
	}
	
	/**
	 * Below are methods testing deletion and undo feature for Use Case diagram.
	 * 
	 * 
	 * 
	 * Testing delete an Node. 
	 */
	@Test
	public void testDeleteNode()
	{
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());
		
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(noteNode, new Point(75, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aPanel.getSelectionList().add(noteNode);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());
		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		
		aDiagram.addNode(aUseCaseNode1, new Point(420, 420), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aPanel.getSelectionList().add(aUseCaseNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getRootNodes().size());
	}

	/**
	 * Testing delete an edge.
	 */
	@Test
	public void testDeleteEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		aDiagram.addEdge(noteEdge1, new Point(85, 25), new Point(110, 110));
		
		// delete aAssociationEdge and aGeneralEdge
		aPanel.getSelectionList().add(aAssociationEdge);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getEdges().size());
		aPanel.getSelectionList().add(aGeneralEdge);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(3, aDiagram.getEdges().size());
		aPanel.undo();
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a combination of node and edge.
	 */
	@Test
	public void testDeleteCombinationNodeAndEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		aDiagram.addEdge(noteEdge1, new Point(85, 25), new Point(110, 110));

		// delete aActorNode1 and all 4 edges
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.getSelectionList().add(aAssociationEdge);
		aPanel.getSelectionList().add(aDependencyEdge);
		aPanel.getSelectionList().add(aGeneralEdge);
		aPanel.getSelectionList().add(noteEdge1);

		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(5, aDiagram.getRootNodes().size());
		assertEquals(4, aDiagram.getEdges().size());
		
		/* now delete aUseCaseNode2, aActorNode2 and aGeneralEdge
		 * aAssociationEdge and aDependencyEdge will also be deleted
		 * since they are connected to aActorNode2
		 */
		aPanel.getSelectionList().add(aUseCaseNode2);
		aPanel.getSelectionList().add(aActorNode2);
		aPanel.getSelectionList().add(aGeneralEdge);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(5, aDiagram.getRootNodes().size());
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing copy and paste feature for Use Case Diagram.
	 * 
	 * 
	 * 
	 * Testing copy a Node.
	 */
	@Test
	public void testCopyNode()
	{
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aActorNode1.view().getBounds();
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
	
		assertEquals(3, aDiagram.getRootNodes().size());
		assertTrue(new Rectangle(0, 0, 48, 88).equals((((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds())) ||
			new Rectangle(0, 0, 48, 87).equals((((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds())));
	}
	
	/**
	 * 
	 * Testing cut a Node.
	 */
	@Test
	public void testCutNode()
	{
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		
		aPanel.getSelectionList().add(aUseCaseNode1);
		aPanel.cut();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());

		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(0, 0, 120, 40), (((UseCaseNode) aDiagram.getRootNodes().toArray()[1]).view().getBounds()));
		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge.
	 */
	@Test
	public void testCopyNodesWithEdge()
	{
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aPanel.selectAll();
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertTrue(new Rectangle(0, 0, 48, 88).equals((((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds())) || new Rectangle(0, 0, 48, 87).equals((((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds())));
	}

	/**
	 * 
	 * Testing cut two Node with an edge.
	 */
	@Test
	public void testCutNodesWithEdge()
	{
		aDiagram.addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.getSelectionList().add(aUseCaseNode2);
		aPanel.getSelectionList().add(aGeneralEdge);
		
		aPanel.cut();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertTrue(new Rectangle(0, 0, 48, 88).equals((((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds())) || 
				new Rectangle(0, 0, 48, 87).equals(((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds()));
	}
}

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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperationProcessor;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvas;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.gui.DiagramTabToolBar;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Tests various interactions with Use Case Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Use Case Diagram.
 */
public class TestUsageScenariosUseCaseDiagram 
{
	private UseCaseDiagram aDiagram;
	private DiagramOperationProcessor aProcessor;
	private GraphicsContext aGraphics;
	private DiagramCanvas aPanel;
	private DiagramCanvasController aController;
	private ActorNode aActorNode1;
	private ActorNode aActorNode2;
	private UseCaseNode aUseCaseNode1;
	private UseCaseNode aUseCaseNode2;
	private NoteNode aNoteNode;
	private UseCaseAssociationEdge aAssociationEdge;
	private UseCaseDependencyEdge aDependencyEdge;
	private UseCaseGeneralizationEdge aGeneralEdge;
	private NoteEdge aNoteEdge;
	private List<DiagramElement> aSelection;
	
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
		aProcessor = new DiagramOperationProcessor();
		aGraphics = new Canvas(256, 256).getGraphicsContext2D();
		aPanel = new DiagramCanvas(aDiagram, 0, 0);
		aController = new DiagramCanvasController(aPanel, new DiagramTabToolBar(aDiagram), a ->  {});
		aPanel.setController(aController);
		aActorNode1 = new ActorNode();
		aActorNode2 = new ActorNode();
		aUseCaseNode1 = new UseCaseNode();
		aUseCaseNode2 = new UseCaseNode();
		aNoteNode = new NoteNode();
		aAssociationEdge = new UseCaseAssociationEdge();
		aDependencyEdge = new UseCaseDependencyEdge();
		aGeneralEdge = new UseCaseGeneralizationEdge();
		aNoteEdge = new NoteEdge();
		aSelection = new ArrayList<>();
	}
	
	private void addNode(Node pNode, Point pRequestedPosition)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createAddNodeOperation(pNode, pRequestedPosition, 1000, 1000));
	}
	
	private void addEdge(Edge pEdge, Point pStart, Point pEnd)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createAddEdgeOperation(pEdge, pStart, pEnd));
	}
	
	private void moveNode(Node pNode, int pX, int pY)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createMoveNodeOperation(pNode, pX, pY));
	}
	
	private void setProperty(Property pProperty, Object pValue)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createPropertyChangeOperation(pProperty, pValue));
	}
	
	private void removeSelected()
	{
		aProcessor.executeNewOperation(aDiagram.builder().createRemoveElementsOperation(aSelection));
		aSelection.clear();
	}
	
	@Test
	public void testCreateUseCaseDiagram()
	{
		addNode(aActorNode1, new Point(20, 20));
		setProperty(aActorNode1.properties().get("name"), "Car");
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals("Car", aActorNode1.getName());
		
		addNode(aUseCaseNode1, new Point(120, 80));
		setProperty(aUseCaseNode1.properties().get("name"), "driving");
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("driving", aUseCaseNode1.getName());

		addNode(aNoteNode, new Point(50, 50));
		setProperty(aNoteNode.properties().get("name"), "something...\nsomething");
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals("something...\nsomething", aNoteNode.getName());
	}
	
	@Test
	public void testGeneralEdgeCreation()
	{
		addNode(aActorNode1, new Point(20, 20));
		assertTrue(aDiagram.contains(aActorNode1));
		assertEquals(new Point(20,20), aActorNode1.position());
		
		addNode(aActorNode2, new Point(250, 20));
		assertTrue(aDiagram.contains(aActorNode2));
		assertEquals(new Point(250,20), aActorNode2.position());
		
		addNode(aUseCaseNode1, new Point(80, 20));
		assertTrue(aDiagram.contains(aUseCaseNode1));
		assertEquals(new Point(80,20), aUseCaseNode1.position());
		
		addNode(aUseCaseNode2, new Point(140, 20));
		assertTrue(aDiagram.contains(aUseCaseNode2));
		assertEquals(new Point(140,20), aUseCaseNode2.position());
		
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(aAssociationEdge));
		assertSame(aActorNode1, aAssociationEdge.getStart());
		assertSame(aUseCaseNode2, aAssociationEdge.getEnd());
		
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20)); // aUseCaseNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(aDependencyEdge));
		assertSame(aUseCaseNode1, aDependencyEdge.getStart());
		assertSame(aUseCaseNode2, aDependencyEdge.getEnd());
		
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(aGeneralEdge));
		assertSame(aActorNode1, aGeneralEdge.getStart());
		assertSame(aUseCaseNode2, aGeneralEdge.getEnd());
		
		UseCaseAssociationEdge useCaseAssociationEdge2 = new UseCaseAssociationEdge();
		addEdge(useCaseAssociationEdge2,  new Point(80, 20), new Point(140, 20)); // aUseCaseNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(useCaseAssociationEdge2));
		assertSame(aUseCaseNode1, useCaseAssociationEdge2.getStart());
		assertSame(aUseCaseNode2, useCaseAssociationEdge2.getEnd());
		
		UseCaseDependencyEdge useCaseDependencyEdge2 = new UseCaseDependencyEdge();
		addEdge(useCaseDependencyEdge2,  new Point(20, 20), new Point(250, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(useCaseDependencyEdge2));
		assertSame(aActorNode1, useCaseDependencyEdge2.getStart());
		assertSame(aUseCaseNode2, useCaseDependencyEdge2.getEnd());
		
		assertEquals(5, aDiagram.getEdges().size());
		
		// connect nodes with NoteEdge (not allowed)
		assertFalse(aDiagram.builder().canAdd(new NoteEdge(),  new Point(80, 20), new Point(140, 20)));
		assertFalse(aDiagram.builder().canAdd(new NoteEdge(),  new Point(20, 20), new Point(250, 20)));
	}
	
	@Test
	public void testNoteEdgeCreation()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		assertEquals(4, aDiagram.getRootNodes().size());
		
		NoteNode noteNode = new NoteNode();
		addNode(noteNode, new Point(100, 100));
		assertTrue(aDiagram.contains(noteNode));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		
		assertFalse(aDiagram.builder().canAdd(noteEdge1, new Point(9, 9), new Point(209, 162)));
		addEdge(noteEdge1, new Point(20, 20), new Point(100, 100));
		assertTrue(aDiagram.contains(noteEdge1));
		assertSame(noteEdge1.getStart(), aActorNode1);
		assertSame(noteEdge1.getEnd(), noteNode);

		addEdge(noteEdge2, new Point(85, 25), new Point(110, 110));
		assertTrue(aDiagram.contains(noteEdge2));
		assertSame(noteEdge2.getStart(), aUseCaseNode1);
		assertSame(noteEdge2.getEnd(), noteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		addEdge(noteEdge3, new Point(100, 100), new Point(9,9));
		assertTrue(aDiagram.contains(noteEdge3));
		assertSame(noteEdge3.getStart(), noteNode);
		Node end = noteEdge3.getEnd();
		assertEquals(PointNode.class, end.getClass());
		assertEquals(new Point(9,9), end.position());
	}
	
	@Test
	public void testIndividualNodeMovement()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aNoteNode, new Point(100, 100));

		moveNode(aActorNode1, 3, 12);
		moveNode(aUseCaseNode1, 3, 12);
		moveNode(aNoteNode, 40, 20);
		
		assertEquals( new Point(23,32), aActorNode1.position());
		assertEquals( new Point(83,32), aUseCaseNode1.position());
		assertEquals( new Point(140,120), aNoteNode.position());
	}
	
	@Test
	public void testNodesAndEdgesMovement()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));

		for(DiagramElement element: aDiagram.allElements())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		assertEquals(new Point(46, 57), aActorNode1.position());
		assertEquals(new Point(276, 57), aActorNode2.position());
		assertEquals(new Point(106, 57), aUseCaseNode1.position());
		assertEquals(new Point(166, 57), aUseCaseNode2.position());
		assertEquals(new Point(126, 137), aNoteNode.position());
	}
	
	@Test
	public void testDeleteNode()
	{
		addNode(aActorNode1, new Point(20, 20));
		assertTrue(aDiagram.contains(aActorNode1));
		aSelection.add(aActorNode1);
		removeSelected();
		assertEquals(0, aDiagram.getRootNodes().size());
		
		aProcessor.undoLastExecutedOperation();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.contains(aActorNode1));
		
		addNode(aNoteNode, new Point(75, 75));
		assertEquals(2, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.contains(aNoteNode));
		aSelection.add(aNoteNode);
		removeSelected();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.contains(aActorNode1));
		assertFalse(aDiagram.contains(aNoteNode));
		aProcessor.undoLastExecutedOperation();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.contains(aActorNode1));
		assertTrue(aDiagram.contains(aNoteNode));

		addNode(aUseCaseNode1, new Point(420, 420));
		assertTrue(aDiagram.contains(aUseCaseNode1));
		assertTrue(aDiagram.contains(aActorNode1));
		aSelection.add(aActorNode1);
		aSelection.add(aUseCaseNode1);
		removeSelected();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertFalse(aDiagram.contains(aActorNode1));
		assertTrue(aDiagram.contains(aNoteNode));
		assertFalse(aDiagram.contains(aUseCaseNode1));

		aProcessor.undoLastExecutedOperation();
		assertEquals(3, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.contains(aActorNode1));
		assertTrue(aDiagram.contains(aNoteNode));
		assertTrue(aDiagram.contains(aUseCaseNode1));
	}

	/**
	 * Testing delete an edge.
	 */
	@Test
	public void testDeleteEdgeOBSOLETE()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.builder().addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.builder().addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.builder().addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		aDiagram.builder().addEdge(noteEdge1, new Point(85, 25), new Point(110, 110));
		
		// delete aAssociationEdge and aGeneralEdge
		aController.getSelectionModel().addToSelection(aAssociationEdge);
		aController.removeSelected();
		aController.getSelectionModel().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getEdges().size());
		aController.getSelectionModel().addToSelection(aGeneralEdge);
		aController.removeSelected();
		aController.getSelectionModel().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getEdges().size());
		
		aController.undo();
		assertEquals(3, aDiagram.getEdges().size());
		aController.undo();
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a combination of node and edge.
	 */
	@Test
	public void testDeleteCombinationNodeAndEdgeOBSOLETE()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.builder().addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(noteNode, new Point(100, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.builder().addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.builder().addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		aDiagram.builder().addEdge(noteEdge1, new Point(85, 25), new Point(110, 110));

		// delete aActorNode1 and all 4 edges
		aController.getSelectionModel().addToSelection(aActorNode1);
		aController.getSelectionModel().addToSelection(aAssociationEdge);
		aController.getSelectionModel().addToSelection(aDependencyEdge);
		aController.getSelectionModel().addToSelection(aGeneralEdge);
		aController.getSelectionModel().addToSelection(noteEdge1);

		aController.removeSelected();
		aController.getSelectionModel().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		
		aController.undo();
		assertEquals(5, aDiagram.getRootNodes().size());
		assertEquals(4, aDiagram.getEdges().size());
		
		/* now delete aUseCaseNode2, aActorNode2 and aGeneralEdge
		 * aAssociationEdge and aDependencyEdge will also be deleted
		 * since they are connected to aActorNode2
		 */
		aController.getSelectionModel().addToSelection(aUseCaseNode2);
		aController.getSelectionModel().addToSelection(aActorNode2);
		aController.getSelectionModel().addToSelection(aGeneralEdge);
		aController.removeSelected();
		aController.getSelectionModel().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		
		aController.undo();
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
	public void testCopyNodeOBSOLETE()
	{
		aDiagram.builder().addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aActorNode1.view().getBounds();
		aDiagram.builder().addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		aController.getSelectionModel().addToSelection(aActorNode1);
		aController.copy();
		aController.paste();
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
	public void testCutNodeOBSOLETE()
	{
		aDiagram.builder().addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		
		aController.getSelectionModel().addToSelection(aUseCaseNode1);
		aController.cut();
		aController.getSelectionModel().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());

		aController.paste();
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(0, 0, 110, 40), (((UseCaseNode) aDiagram.getRootNodes().toArray()[1]).view().getBounds()));
		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge.
	 */
	@Test
	public void testCopyNodesWithEdgeOBSOLETE()
	{
		aDiagram.builder().addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aController.selectAll();
		aController.copy();
		aController.paste();
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
	public void testCutNodesWithEdgeOBSOLETE()
	{
		aDiagram.builder().addNode(aActorNode1, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aActorNode2, new Point(250, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode1, new Point(80, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addNode(aUseCaseNode2, new Point(140, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.builder().addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		aDiagram.builder().addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		aDiagram.builder().addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		
		aController.getSelectionModel().addToSelection(aActorNode1);
		aController.getSelectionModel().addToSelection(aUseCaseNode2);
		aController.getSelectionModel().addToSelection(aGeneralEdge);
		
		aController.cut();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		
		assertEquals(0, aDiagram.getEdges().size());

		aController.paste();
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertTrue(new Rectangle(0, 0, 48, 88).equals((((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds())) || 
				new Rectangle(0, 0, 48, 87).equals(((ActorNode) aDiagram.getRootNodes().toArray()[2]).view().getBounds()));
	}
}

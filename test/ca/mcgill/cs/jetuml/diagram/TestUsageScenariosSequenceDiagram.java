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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvas;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.gui.DiagramTabToolBar;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Tests various interactions with Sequence Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Sequence Diagram.
 */
public class TestUsageScenariosSequenceDiagram 
{
	private SequenceDiagram aDiagram;
	private GraphicsContext aGraphics;
	private DiagramCanvas aCanvas;
	private DiagramCanvasController aController;
	private SelectionList aList;
	private ImplicitParameterNode aParameterNode1;
	private ImplicitParameterNode aParameterNode2;
	private CallNode aCallNode1;
	private CallNode aCallNode2;
	private CallEdge aCallEdge1;
	
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
		aDiagram = new SequenceDiagram();
		aGraphics = new Canvas(256, 256).getGraphicsContext2D();
		aCanvas = new DiagramCanvas(aDiagram, new Rectangle2D(0, 0, 0, 0));
		aController = new DiagramCanvasController(aCanvas, new DiagramTabToolBar(aDiagram));
		aCanvas.setController(aController);
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
		aParameterNode1.setName("client");
		aParameterNode2.setName("platform");
		aDiagram.addNode(aParameterNode1, new Point(5, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(25, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("client", aParameterNode1.getName());
		assertEquals("platform", aParameterNode2.getName());
		
		CallEdge callEdge = new CallEdge();
		ReturnEdge returnEdge = new ReturnEdge();
		NoteEdge noteEdge = new NoteEdge();
		aDiagram.addEdge(callEdge, new Point(7, 0), new Point(26, 0));
		aDiagram.addEdge(returnEdge, new Point(7, 0), new Point(26, 0));
		aDiagram.addEdge(noteEdge, new Point(7, 0), new Point(26, 0));
		assertEquals(0, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing create the CallNode and link it to the right Parameter's life line
	 * except CallEdge (not allowed).
	 */
	@Test
	public void testCreateCallNodeAndLinkParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point(5, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(25, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(7, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aParameterNode1.getChildren().size());
		
		ReturnEdge returnEdge = new ReturnEdge();
		NoteEdge noteEdge = new NoteEdge();
		aDiagram.addEdge(returnEdge, new Point(7, 75), new Point(26, 0));
		aDiagram.addEdge(noteEdge, new Point(7, 75), new Point(26, 0));
		assertEquals(0, aDiagram.getEdges().size());
		
		aDiagram.addEdge(new CallEdge(), new Point(7, 75), new Point(26, 0));
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing link CallNode to ParameterNode life line and other CallNode.
	 */
	@Test
	public void testLinkCallNodeToLifeLineAndCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point(5, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(25, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(7, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		aDiagram.addEdge(aCallEdge1, new Point(7, 75), new Point(25,75));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		aDiagram.draw(aGraphics);
		
		aDiagram.addEdge(new CallEdge(), new Point(64,88), new Point(62,85));
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
		aDiagram.addNode(aParameterNode1, new Point(5, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(105, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(7, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new  Point(7, 75), new Point(8,85));
		aDiagram.draw(aGraphics);

		aDiagram.addEdge(new CallEdge(), new Point(59, 110), new Point(116,0));
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(newParaNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
		aDiagram.draw(aGraphics);
		
		ReturnEdge returnEdge1 = new ReturnEdge();
		aDiagram.addEdge(returnEdge1, new Point(145,90), new Point(45, 90));
		assertEquals(2, aDiagram.getEdges().size());
		
		// call edge from first CallNode to third ParameterNode life line
		CallEdge callEdge2 = new CallEdge();
		aDiagram.addEdge(callEdge2, new Point(45, 75), new Point(210,75));
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getEdges().size());
		
		// call edge from first CallNode to third ParameterNode's top box
		CallEdge callEdge3 = new CallEdge();
		aDiagram.addEdge(callEdge3, new Point(45, 75), new Point(210,0));
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing NoteNode and NoteEdge creation in a sequence diagram.
	 */
	@Test
	public void testNoteNode()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
		aDiagram.draw(aGraphics);
		
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		aDiagram.addNode(noteNode, new Point(55, 55), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(noteEdge1, new Point(60, 60), new Point(87,65));
		aDiagram.addEdge(noteEdge2, new Point(62, 68), new Point(47,75));
		aDiagram.addEdge(noteEdge3, new Point(63, 69), new Point(47,35));
		aDiagram.addEdge(noteEdge4, new Point(64, 70), new Point(17,5));
		aDiagram.addEdge(noteEdge5, new Point(65, 60), new Point(67,265));
		
		assertEquals(6, aDiagram.getEdges().size());
		assertEquals(8, aDiagram.getRootNodes().size());
		
		// from ParameterNode to NoteNode
		aDiagram.addEdge(new NoteEdge(), new Point(10, 10), new Point(62, 68));
		// from CallNode to NoteNode 
		aDiagram.addEdge(new NoteEdge(), new Point(10, 10), new Point(62, 68));
		assertEquals(8, aDiagram.getRootNodes().size());
	}
	
	/**
	 * Testing Node movement for individual node. 
	 * Note edge could not be moved individually.
	 */
	@Test
	public void testIndividualNodeMovement()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
		aDiagram.draw(aGraphics);

		// testing moving ParameterNode, can only be moved horizontally
		aParameterNode1.translate(5, 15);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(15, 0, 80, 157), aParameterNode1.view().getBounds());
		aParameterNode1.translate(25, 0);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(40, 0, 80, 157), aParameterNode1.view().getBounds());

		aParameterNode2.translate(105, 25);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(215, 0, 80, 157), aParameterNode2.view().getBounds());
		aParameterNode2.translate(0, 15);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(215, 0, 80, 157), aParameterNode2.view().getBounds());
		
		// testing moving left call node, can only be moved vertically
		aCallNode1.translate(5, 15);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(72, 90, 16, 62), aCallNode1.view().getBounds());
		aCallNode1.translate(0, 15);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(72, 105, 16, 62), aCallNode1.view().getBounds());
		aCallNode1.translate(20, 0);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(72, 105, 16, 62), aCallNode1.view().getBounds());
		
		// testing moving right call node, can only be moved vertically
		aCallNode2.translate(5, 15);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(247, 117, 16, 30), aCallNode2.view().getBounds());
		aCallNode2.translate(0, 15);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(247, 117, 16, 30), aCallNode2.view().getBounds());
		aCallNode2.translate(20, 0);
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(247, 117, 16, 30), aCallNode2.view().getBounds());
	}
	
	/**
	 * Testing moving entire graph.
	 */
	@Test
	public void testMoveEntireGraph()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
		aDiagram.draw(aGraphics);

		aController.selectAll();
		for(DiagramElement element: aController.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(15, 0);
			}
		}
		aController.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(25, 0, 80, 157), aParameterNode1.view().getBounds());
		assertEquals(new Rectangle(125, 0, 80, 157), aParameterNode2.view().getBounds());
		assertEquals(new Rectangle(57, 75, 16, 62), aCallNode1.view().getBounds());
		assertEquals(new Rectangle(157, 87, 16, 30), aCallNode2.view().getBounds());

		aController.selectAll();
		for(DiagramElement element: aController.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(-25, 0);
			}
		}
		aController.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(0, 0, 80, 157), aParameterNode1.view().getBounds());
		assertEquals(new Rectangle(100, 0, 80, 157), aParameterNode2.view().getBounds());
		assertEquals(new Rectangle(32, 75, 16, 62), aCallNode1.view().getBounds());
		assertEquals(new Rectangle(132, 87, 16, 30), aCallNode2.view().getBounds());
	}
	
	/**
	 * Testing moving entire graph with a <<create>> CallEdge.
	 */
	@Test
	public void testMoveEntireGraphWithCallEdge()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(15, 80), new Point(116,0));
		aDiagram.draw(aGraphics);
		
		aController.selectAll();
		for(DiagramElement element: aController.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(15, 0);
			}
		}
		aController.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(25, 0, 80, 165), aParameterNode1.view().getBounds());
		assertEquals(new Rectangle(125, 65, 80, 100), aParameterNode2.view().getBounds());
		assertEquals(new Rectangle(57, 75, 16, 70), aCallNode1.view().getBounds());
		
		aController.selectAll();
		for(DiagramElement element: aController.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(-25, 0);
			}
		}
		aController.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(new Rectangle(0, 0, 80, 165), aParameterNode1.view().getBounds());
		assertEquals(new Rectangle(100, 65, 80, 100), aParameterNode2.view().getBounds());
		assertEquals(new Rectangle(32, 75, 16, 70), aCallNode1.view().getBounds());
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 65), Integer.MAX_VALUE, Integer.MAX_VALUE);
		Rectangle parameterNode1Bounds = aParameterNode1.view().getBounds();
		aController.getSelectionList().add(aParameterNode1);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(0, aDiagram.getRootNodes().size());
		aController.undo();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(parameterNode1Bounds, ((ImplicitParameterNode) (aDiagram.getRootNodes().toArray()[0])).view().getBounds());
	}
	
	/**
	 * Testing delete single CallNode.
	 */
	@Test
	public void testDeleteSingleCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);

		Rectangle callNode1Bounds = aCallNode1.view().getBounds();
		aController.getSelectionList().add(aCallNode1);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aParameterNode1.getChildren().size());
		
		aController.undo();
		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(callNode1Bounds, ((CallNode) (aParameterNode1.getChildren().toArray()[0])).view().getBounds());
	}
	
	/**
	 * Testing delete a ParameterNode in call sequence.
	 */
	@Test
	public void testDeleteParameterNodeInCallSequence()
	{
		// set up 
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(newParameterNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
		aDiagram.draw(aGraphics);
		ReturnEdge returnEdge1 = new ReturnEdge();
		aDiagram.addEdge(returnEdge1, new Point(145,90), new Point(45, 90));		
		CallEdge callEdge2 = new CallEdge();
		aDiagram.addEdge(callEdge2, new Point(45, 75), new Point(210,75));
		aDiagram.draw(aGraphics);
		
		aController.getSelectionList().add(aParameterNode1);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(0, newParameterNode.getChildren().size());
		/*
		 *  since a return edge is added, the call node will still remain there
		 *  however the edges are still removed
		 */
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aController.undo();
		aDiagram.draw(aGraphics);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, newParameterNode.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(3, aDiagram.getEdges().size());
	}
	
	@Test
	public void testDeleteUndoParameterWithTwoCallNodes()
	{
		ImplicitParameterNode newParameterNode1 = new ImplicitParameterNode();
		ImplicitParameterNode newParameterNode2 = new ImplicitParameterNode();
		aDiagram.addNode(newParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(newParameterNode2, new Point(100, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		CallNode caller = new CallNode();
		aDiagram.addNode(caller, new Point(15, 70), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		
		CallNode callee1 = new CallNode();
		aDiagram.addNode(callee1, new Point(105, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		
		CallNode callee2 = new CallNode();
		aDiagram.addNode(callee2, new Point(105, 150), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		
		CallEdge callEdge1 = new CallEdge();
		aDiagram.addEdge(callEdge1, new Point(43, 75), new Point(133,105));
		aDiagram.draw(aGraphics);
		
		CallEdge callEdge2 = new CallEdge();
		aDiagram.addEdge(callEdge2, new Point(43, 77), new Point(133,155));
		aDiagram.draw(aGraphics);
		
		Edge[] edges = aDiagram.getEdges(caller).toArray(new Edge[aDiagram.getEdges(caller).size()]);
		assertEquals(2, edges.length);
		assertEquals(callEdge1, edges[0]);
		assertEquals(callEdge2, edges[1]);
		
		aController.getSelectionList().add(caller);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(0, newParameterNode1.getChildren().size());
		
		aController.undo();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, newParameterNode1.getChildren().size());
		
		edges = aDiagram.getEdges(caller).toArray(new Edge[aDiagram.getEdges(caller).size()]);
		assertEquals(2, edges.length);
		assertEquals(callEdge1, edges[0]);
		assertEquals(callEdge2, edges[1]);
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(newParameterNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(new CallNode(), new Point(215, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
		aDiagram.addEdge(new CallEdge(), new Point(118, 75), new Point(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point(118, 75), new Point(18,75));
		aDiagram.addEdge(new CallEdge(), new Point(118, 75), new Point(210,115));
		
		aController.getSelectionList().add(middleCallNode);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(0, aParameterNode2.getChildren().size()); 
		assertEquals(0, newParameterNode.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aController.undo();
		aDiagram.draw(aGraphics);
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(new CallEdge(), new Point(18, 75), new Point(115,75));
		aDiagram.addEdge(returnEdge, new Point(118, 75), new Point(18,75));
		
		aController.getSelectionList().add(returnEdge);
		aController.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(1, aDiagram.getEdges().size());
		
		aController.undo();
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(118,75));
		aDiagram.addEdge(returnEdge, new Point(118, 75), new Point(18,75));
		
		aController.getSelectionList().add(returnEdge);
		aController.getSelectionList().add(aCallEdge1);
		aController.getSelectionList().add(middleCallNode);

		aController.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(0, aParameterNode2.getChildren().size()); 
		assertEquals(0, aDiagram.getEdges().size());
		
		aController.undo();
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aController.getSelectionList().add(aParameterNode1);
		aController.copy();
		aController.paste();
		aDiagram.draw(aGraphics);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(0, 0, 80, 80),
				((Node)(aDiagram.getRootNodes().toArray()[1])).view().getBounds());
	}
	
	/**
	 * Testing cut and paste single Parameter Node.
	 */
	@Test
	public void testCutPasteParameterNode()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aController.getSelectionList().add(aParameterNode1);
		aController.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());

		aController.paste();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(0, 0, 80, 80),
				((Node)(aDiagram.getRootNodes().toArray()[0])).view().getBounds());
	}
	
	/**
	 * Testing copy and paste Parameter Node with Call Node.
	 */
	@Test
	public void testCopyPasteParameterNodeWithCallNode()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aController.getSelectionList().add(aParameterNode1);
		aController.copy();
		aController.paste();
		aDiagram.draw(aGraphics);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(10, 0, 80, 125),
				((Node)(aDiagram.getRootNodes().toArray()[0])).view().getBounds());
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(newParameterNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(new CallNode(), new Point(215, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
		aDiagram.addEdge(new CallEdge(), new Point(118, 75), new Point(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point(118, 75), new Point(18,75));
		aDiagram.addEdge(new CallEdge(), new Point(118, 75), new Point(210,115));
		
		aController.selectAll();
		aController.copy();
		aController.paste();
		aDiagram.draw(aGraphics);
		
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
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(newParaNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(endCallNode, new Point(215, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
		aDiagram.addEdge(new CallEdge(), new Point(118, 75), new Point(215,75));
		aDiagram.addEdge(new ReturnEdge(), new Point(118, 75), new Point(18,75));
		aDiagram.addEdge(new CallEdge(), new Point(118, 75), new Point(210,115));
		
		aList.add(aCallNode1);
		aList.add(middleCallNode);
		aList.add(endCallNode);
		for(Edge edge: aDiagram.getEdges())
		{
			aList.add(edge);
		}
		aController.setSelectionList(aList);
		aController.copy();
		SequenceDiagram tempDiagram = new SequenceDiagram();
		aController.paste();
		tempDiagram.draw(aGraphics);
		
		assertEquals(0, tempDiagram.getRootNodes().size());
		assertEquals(0, tempDiagram.getEdges().size());
	}

	/**
	 * Tests the creation of a basic diagram.
	 */
	@Test
	public void testCreateBasicGraph()
	{
		aDiagram.addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(new Rectangle(10,0,80,120), aParameterNode1.view().getBounds());
		
		aDiagram.addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(new Rectangle(110,0,80,120), aParameterNode2.view().getBounds());

		aDiagram.addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(new Rectangle(15,75,16,30), aCallNode1.view().getBounds());

		aDiagram.addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(new Rectangle(115, 75,16,30), aCallNode2.view().getBounds());
		
		aDiagram.addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
		assertEquals(new Rectangle(30,68,86,12), aCallEdge1.view().getBounds());
		
		aDiagram.draw(aGraphics);
		
		assertEquals(new Rectangle(10,0,80,157), aParameterNode1.view().getBounds());
		assertEquals(new Rectangle(110,0,80,157), aParameterNode2.view().getBounds());
		assertEquals(new Rectangle(42,75,16,62), aCallNode1.view().getBounds());
		assertEquals(new Rectangle(142,87,16,30), aCallNode2.view().getBounds());
		assertEquals(new Rectangle(57,80,86,12), aCallEdge1.view().getBounds());
	}	
}

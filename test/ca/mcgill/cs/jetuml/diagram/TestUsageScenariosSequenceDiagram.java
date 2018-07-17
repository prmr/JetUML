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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagram.builder.SequenceDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.gui.DiagramCanvas;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.gui.DiagramTabToolBar;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class TestUsageScenariosSequenceDiagram extends AbstractTestUsageScenarios
{
	private GraphicsContext aGraphics;
	private DiagramCanvas aCanvas;
	private DiagramCanvasController aController;
	private List<DiagramElement> aList;
	private ImplicitParameterNode aParameterNode1;
	private ImplicitParameterNode aParameterNode2;
	private CallNode aCallNode1;
	private CallNode aCallNode2;
	private CallEdge aCallEdge1;
	private ReturnEdge aReturnEdge;
	
	@Before
	public void setup()
	{
		super.setup();
		aDiagram = new SequenceDiagram();
		aBuilder = new SequenceDiagramBuilder(aDiagram);
		aGraphics = new Canvas(256, 256).getGraphicsContext2D();
		aCanvas = new DiagramCanvas(aDiagram, 0, 0);
		aController = new DiagramCanvasController(aCanvas, new DiagramTabToolBar(aDiagram), a ->  {});
		aCanvas.setController(aController);
		aList = new ArrayList<>();
		aParameterNode1 = new ImplicitParameterNode();
		aParameterNode2 = new ImplicitParameterNode();
		aCallNode1 = new CallNode();
		aCallNode2 = new CallNode();
		aCallEdge1 = new CallEdge();
		aReturnEdge = new ReturnEdge();
	}
	
	@Test
	public void testCreateAndLinkParameterNode()
	{
		setProperty(aParameterNode1.properties().get("name"), "client");
		setProperty(aParameterNode2.properties().get("name"), "platform");

		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(25, 0));
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("client", aParameterNode1.getName());
		assertEquals("platform", aParameterNode2.getName());
		
		assertFalse(aBuilder.canAdd(aCallEdge1, new Point(7, 0), new Point(26, 0))); 
		assertFalse(aBuilder.canAdd(aReturnEdge, new Point(7, 0), new Point(26, 0)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(7, 0), new Point(26, 0)));
	}
	
	@Test
	public void testCreateCallNodeAndLinkParameterNode()
	{
		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(125, 0));
		addNode(aCallNode1, new Point(7, 75));

		aDiagram.requestLayout();
		aDiagram.draw(aGraphics);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aParameterNode1.getChildren().size());
		
		assertFalse(aBuilder.canAdd(aReturnEdge, new Point(7, 75), new Point(26, 0)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(7, 75), new Point(26, 0)));
		
		addEdge(aCallEdge1, new Point(43, 75), new Point(130, 0));
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	private void refresh()
	{
		aDiagram.requestLayout();
		aDiagram.draw(aGraphics);
	}
	
	@Test
	public void testLinkCallNodeToLifeLine()
	{
		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(125, 0));
		refresh();
		addNode(aCallNode1, new Point(11, 75));
		refresh();
		addEdge(aCallEdge1, new Point(43, 75), new Point(132,75));
		refresh();
		
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(1, aParameterNode2.getChildren().size());
	}
	
	@Test
	public void testCreateCallEdgeWithCreateTag()
	{
		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(105, 0));
		refresh();
		addNode(aCallNode1, new Point(11, 75));
		refresh();

		addEdge(aCallEdge1, new Point(43, 75), new Point(116,2));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals("\u00ABcreate\u00BB", aCallEdge1.getMiddleLabel());
	}
//	
//	/**
//	 * Testing adding more edges to the diagram.
//	 */
//	@Test
//	public void testAddMoreEdges()
//	{
//		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(newParaNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
//		aDiagram.draw(aGraphics);
//		
//		ReturnEdge returnEdge1 = new ReturnEdge();
//		aDiagram.builder().addEdge(returnEdge1, new Point(145,90), new Point(45, 90));
//		assertEquals(2, aDiagram.getEdges().size());
//		
//		// call edge from first CallNode to third ParameterNode life line
//		CallEdge callEdge2 = new CallEdge();
//		aDiagram.builder().addEdge(callEdge2, new Point(45, 75), new Point(210,75));
//		aDiagram.draw(aGraphics);
//		assertEquals(3, aDiagram.getEdges().size());
//		
//		// call edge from first CallNode to third ParameterNode's top box
//		CallEdge callEdge3 = new CallEdge();
//		aDiagram.builder().addEdge(callEdge3, new Point(45, 75), new Point(210,0));
//		aDiagram.draw(aGraphics);
//		assertEquals(4, aDiagram.getEdges().size());
//	}
//	
//	/**
//	 * Testing NoteNode and NoteEdge creation in a sequence diagram.
//	 */
//	@Test
//	public void testNoteNode()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
//		aDiagram.draw(aGraphics);
//		
//		NoteNode noteNode = new NoteNode();
//		NoteEdge noteEdge1 = new NoteEdge();
//		NoteEdge noteEdge2 = new NoteEdge();
//		NoteEdge noteEdge3 = new NoteEdge();
//		NoteEdge noteEdge4 = new NoteEdge();
//		NoteEdge noteEdge5 = new NoteEdge();
//		aDiagram.builder().addNode(noteNode, new Point(55, 55), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(noteEdge1, new Point(60, 60), new Point(87,65));
//		aDiagram.builder().addEdge(noteEdge2, new Point(62, 68), new Point(47,75));
//		aDiagram.builder().addEdge(noteEdge3, new Point(63, 69), new Point(47,35));
//		aDiagram.builder().addEdge(noteEdge4, new Point(64, 70), new Point(17,5));
//		aDiagram.builder().addEdge(noteEdge5, new Point(65, 60), new Point(67,265));
//		
//		assertEquals(6, aDiagram.getEdges().size());
//		assertEquals(8, aDiagram.getRootNodes().size());
//		
//		// from ParameterNode to NoteNode
//		assertFalse(aDiagram.builder().canAdd(new NoteEdge(), new Point(10, 10), new Point(62, 68)));
//		// from CallNode to NoteNode 
//		assertFalse(aDiagram.builder().canAdd(new NoteEdge(), new Point(10, 10), new Point(62, 68)));
//		assertEquals(8, aDiagram.getRootNodes().size());
//	}
//	
//	/**
//	 * Testing Node movement for individual node. 
//	 * Note edge could not be moved individually.
//	 */
//	@Test
//	public void testIndividualNodeMovement()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
//		aDiagram.draw(aGraphics);
//
//		// testing moving ParameterNode, can only be moved horizontally
//		aParameterNode1.translate(5, 15);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(15, 0, 80, 157), aParameterNode1.view().getBounds());
//		aParameterNode1.translate(25, 0);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(40, 0, 80, 157), aParameterNode1.view().getBounds());
//
//		aParameterNode2.translate(105, 25);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(215, 0, 80, 157), aParameterNode2.view().getBounds());
//		aParameterNode2.translate(0, 15);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(215, 0, 80, 157), aParameterNode2.view().getBounds());
//		
//		// testing moving left call node, can only be moved vertically
//		aCallNode1.translate(5, 15);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(72, 90, 16, 62), aCallNode1.view().getBounds());
//		aCallNode1.translate(0, 15);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(72, 105, 16, 62), aCallNode1.view().getBounds());
//		aCallNode1.translate(20, 0);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(72, 105, 16, 62), aCallNode1.view().getBounds());
//		
//		// testing moving right call node, can only be moved vertically
//		aCallNode2.translate(5, 15);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(247, 117, 16, 30), aCallNode2.view().getBounds());
//		aCallNode2.translate(0, 15);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(247, 117, 16, 30), aCallNode2.view().getBounds());
//		aCallNode2.translate(20, 0);
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(247, 117, 16, 30), aCallNode2.view().getBounds());
//	}
//	
//	/**
//	 * Testing moving entire graph.
//	 */
//	@Test
//	public void testMoveEntireGraph()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
//		aDiagram.draw(aGraphics);
//
//		aController.selectAll();
//		for(DiagramElement element: aController.getSelectionModel())
//		{
//			if(element instanceof Node)
//			{
//				((Node) element).translate(15, 0);
//			}
//		}
//		aController.getSelectionModel().clearSelection();
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(25, 0, 80, 157), aParameterNode1.view().getBounds());
//		assertEquals(new Rectangle(125, 0, 80, 157), aParameterNode2.view().getBounds());
//		assertEquals(new Rectangle(57, 75, 16, 62), aCallNode1.view().getBounds());
//		assertEquals(new Rectangle(157, 87, 16, 30), aCallNode2.view().getBounds());
//
//		aController.selectAll();
//		for(DiagramElement element: aController.getSelectionModel())
//		{
//			if(element instanceof Node)
//			{
//				((Node) element).translate(-25, 0);
//			}
//		}
//		aController.getSelectionModel().clearSelection();
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(0, 0, 80, 157), aParameterNode1.view().getBounds());
//		assertEquals(new Rectangle(100, 0, 80, 157), aParameterNode2.view().getBounds());
//		assertEquals(new Rectangle(32, 75, 16, 62), aCallNode1.view().getBounds());
//		assertEquals(new Rectangle(132, 87, 16, 30), aCallNode2.view().getBounds());
//	}
//	
//	/**
//	 * Testing moving entire graph with a <<create>> CallEdge.
//	 */
//	@Test
//	public void testMoveEntireGraphWithCallEdge()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(15, 80), new Point(116,0));
//		aDiagram.draw(aGraphics);
//		
//		aController.selectAll();
//		for(DiagramElement element: aController.getSelectionModel())
//		{
//			if(element instanceof Node)
//			{
//				((Node) element).translate(15, 0);
//			}
//		}
//		aController.getSelectionModel().clearSelection();
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(25, 0, 80, 165), aParameterNode1.view().getBounds());
//		assertEquals(new Rectangle(125, 65, 80, 100), aParameterNode2.view().getBounds());
//		assertEquals(new Rectangle(57, 75, 16, 70), aCallNode1.view().getBounds());
//		
//		aController.selectAll();
//		for(DiagramElement element: aController.getSelectionModel())
//		{
//			if(element instanceof Node)
//			{
//				((Node) element).translate(-25, 0);
//			}
//		}
//		aController.getSelectionModel().clearSelection();
//		aDiagram.draw(aGraphics);
//		assertEquals(new Rectangle(0, 0, 80, 165), aParameterNode1.view().getBounds());
//		assertEquals(new Rectangle(100, 65, 80, 100), aParameterNode2.view().getBounds());
//		assertEquals(new Rectangle(32, 75, 16, 70), aCallNode1.view().getBounds());
//	}
//	
//	/**
//	 * Below are methods testing the deletion and undo feature
//	 * for sequence diagram. Currently no testing for edge deletion.
//	 *
//	 *
//	 *
//	 * Testing delete single ParameterNode
//	 */
//	@Test
//	public void testDeleteSingleParameterNode()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 65), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		Rectangle parameterNode1Bounds = aParameterNode1.view().getBounds();
//		aController.getSelectionModel().addToSelection(aParameterNode1);
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(0, aDiagram.getRootNodes().size());
//		aController.undo();
//		assertEquals(1, aDiagram.getRootNodes().size());
//		assertEquals(parameterNode1Bounds, ((ImplicitParameterNode) (aDiagram.getRootNodes().toArray()[0])).view().getBounds());
//	}
//	
//	/**
//	 * Testing delete single CallNode.
//	 */
//	@Test
//	public void testDeleteSingleCallNode()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.draw(aGraphics);
//
//		Rectangle callNode1Bounds = aCallNode1.view().getBounds();
//		aController.getSelectionModel().addToSelection(aCallNode1);
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(1, aDiagram.getRootNodes().size());
//		assertEquals(0, aParameterNode1.getChildren().size());
//		
//		aController.undo();
//		assertEquals(1, aParameterNode1.getChildren().size());
//		assertEquals(callNode1Bounds, ((CallNode) (aParameterNode1.getChildren().toArray()[0])).view().getBounds());
//	}
//	
//	/**
//	 * Testing delete a ParameterNode in call sequence.
//	 */
//	@Test
//	public void testDeleteParameterNodeInCallSequence()
//	{
//		// set up 
//		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(newParameterNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
//		aDiagram.draw(aGraphics);
//		ReturnEdge returnEdge1 = new ReturnEdge();
//		aDiagram.builder().addEdge(returnEdge1, new Point(145,90), new Point(45, 90));		
//		CallEdge callEdge2 = new CallEdge();
//		aDiagram.builder().addEdge(callEdge2, new Point(45, 75), new Point(210,75));
//		aDiagram.draw(aGraphics);
//		
//		aController.getSelectionModel().addToSelection(aParameterNode1);
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		assertEquals(2, aDiagram.getRootNodes().size());
//		assertEquals(0, newParameterNode.getChildren().size());
//		/*
//		 *  since a return edge is added, the call node will still remain there
//		 *  however the edges are still removed
//		 */
//		assertEquals(1, aParameterNode2.getChildren().size()); 
//		assertEquals(0, aDiagram.getEdges().size());
//		
//		aController.undo();
//		aDiagram.draw(aGraphics);
//		assertEquals(3, aDiagram.getRootNodes().size());
//		assertEquals(1, newParameterNode.getChildren().size());
//		assertEquals(1, aParameterNode2.getChildren().size()); 
//		assertEquals(3, aDiagram.getEdges().size());
//	}
//	
//	@Test
//	public void testDeleteUndoParameterWithTwoCallNodes()
//	{
//		ImplicitParameterNode newParameterNode1 = new ImplicitParameterNode();
//		ImplicitParameterNode newParameterNode2 = new ImplicitParameterNode();
//		aDiagram.builder().addNode(newParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(newParameterNode2, new Point(100, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		
//		CallNode caller = new CallNode();
//		aDiagram.builder().addNode(caller, new Point(15, 70), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.draw(aGraphics);
//		
//		CallNode callee1 = new CallNode();
//		aDiagram.builder().addNode(callee1, new Point(105, 100), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.draw(aGraphics);
//		
//		CallNode callee2 = new CallNode();
//		aDiagram.builder().addNode(callee2, new Point(105, 150), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.draw(aGraphics);
//		
//		CallEdge callEdge1 = new CallEdge();
//		aDiagram.builder().addEdge(callEdge1, new Point(43, 75), new Point(133,105));
//		aDiagram.draw(aGraphics);
//		
//		CallEdge callEdge2 = new CallEdge();
//		aDiagram.builder().addEdge(callEdge2, new Point(43, 77), new Point(133,155));
//		aDiagram.draw(aGraphics);
//		
//		Edge[] edges = aDiagram.getEdges(caller).toArray(new Edge[aDiagram.getEdges(caller).size()]);
//		assertEquals(2, edges.length);
//		assertEquals(callEdge1, edges[0]);
//		assertEquals(callEdge2, edges[1]);
//		
//		aController.getSelectionModel().addToSelection(caller);
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		assertEquals(0, newParameterNode1.getChildren().size());
//		
//		aController.undo();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(1, newParameterNode1.getChildren().size());
//		
//		edges = aDiagram.getEdges(caller).toArray(new Edge[aDiagram.getEdges(caller).size()]);
//		assertEquals(2, edges.length);
//		assertEquals(callEdge1, edges[0]);
//		assertEquals(callEdge2, edges[1]);
//	}
//	
//	/**
//	 * Testing delete a call node in the middle Parameter Node in call sequence.
//	 */
//	@Test
//	public void testDeleteMiddleCallNode()
//	{
//		// set up 
//		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
//		CallNode middleCallNode = new CallNode();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(newParameterNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(new CallNode(), new Point(215, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
//		aDiagram.builder().addEdge(new CallEdge(), new Point(118, 75), new Point(215,75));
//		aDiagram.builder().addEdge(new ReturnEdge(), new Point(118, 75), new Point(18,75));
//		aDiagram.builder().addEdge(new CallEdge(), new Point(118, 75), new Point(210,115));
//		
//		aController.getSelectionModel().addToSelection(middleCallNode);
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(1, aParameterNode1.getChildren().size()); 
//		assertEquals(0, aParameterNode2.getChildren().size()); 
//		assertEquals(0, newParameterNode.getChildren().size()); 
//		assertEquals(0, aDiagram.getEdges().size());
//		
//		aController.undo();
//		aDiagram.draw(aGraphics);
//		assertEquals(1, aParameterNode1.getChildren().size()); 
//		assertEquals(1, aParameterNode2.getChildren().size()); 
//		assertEquals(2, newParameterNode.getChildren().size()); 
//		assertEquals(4, aDiagram.getEdges().size());
//	}
//	
//	/**
//	 * Testing delete a return edge.
//	 */
//	@Test
//	public void testDeleteReturnEdge()
//	{
//		CallNode middleCallNode = new CallNode();
//		ReturnEdge returnEdge = new ReturnEdge();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(new CallEdge(), new Point(18, 75), new Point(115,75));
//		aDiagram.builder().addEdge(returnEdge, new Point(118, 75), new Point(18,75));
//		
//		aController.getSelectionModel().addToSelection(returnEdge);
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		assertEquals(1, aParameterNode1.getChildren().size()); 
//		assertEquals(1, aParameterNode2.getChildren().size()); 
//		assertEquals(1, aDiagram.getEdges().size());
//		
//		aController.undo();
//		assertEquals(2, aDiagram.getEdges().size());
//	}
//	
//	/**
//	 * Testing delete a CallNode with both incoming and return edge.
//	 */
//	@Test
//	public void testDeleteCallNodeWithIncomingAndReturnEdge()
//	{
//		CallNode middleCallNode = new CallNode();
//		ReturnEdge returnEdge = new ReturnEdge();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(118,75));
//		aDiagram.builder().addEdge(returnEdge, new Point(118, 75), new Point(18,75));
//		
//		aController.getSelectionModel().addToSelection(returnEdge);
//		aController.getSelectionModel().addToSelection(aCallEdge1);
//		aController.getSelectionModel().addToSelection(middleCallNode);
//
//		aController.removeSelected();
//		aDiagram.draw(aGraphics);
//		assertEquals(1, aParameterNode1.getChildren().size()); 
//		assertEquals(0, aParameterNode2.getChildren().size()); 
//		assertEquals(0, aDiagram.getEdges().size());
//		
//		aController.undo();
//		assertEquals(1, aParameterNode2.getChildren().size()); 
//		assertEquals(2, aDiagram.getEdges().size());
//	}
//	
//	/**
//	 * Below are methods testing the copy and paste feature
//	 * for sequence diagram.
//	 * 
//	 * 
//	 * Testing copy and paste single Parameter Node.
//	 */
//	@Test
//	public void testCopyPasteParameterNode()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aController.getSelectionModel().addToSelection(aParameterNode1);
//		aController.copy();
//		aController.paste();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(2, aDiagram.getRootNodes().size());
//		assertEquals(new Rectangle(0, 0, 80, 80),
//				((Node)(aDiagram.getRootNodes().toArray()[1])).view().getBounds());
//	}
//	
//	/**
//	 * Testing cut and paste single Parameter Node.
//	 */
//	@Test
//	public void testCutPasteParameterNode()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aController.getSelectionModel().addToSelection(aParameterNode1);
//		aController.cut();
//		aDiagram.draw(aGraphics);
//		assertEquals(0, aDiagram.getRootNodes().size());
//
//		aController.paste();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(1, aDiagram.getRootNodes().size());
//		assertEquals(new Rectangle(0, 0, 80, 80),
//				((Node)(aDiagram.getRootNodes().toArray()[0])).view().getBounds());
//	}
//	
//	/**
//	 * Testing copy and paste Parameter Node with Call Node.
//	 */
//	@Test
//	public void testCopyPasteParameterNodeWithCallNode()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aController.getSelectionModel().addToSelection(aParameterNode1);
//		aController.copy();
//		aController.paste();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(2, aDiagram.getRootNodes().size());
//		assertEquals(new Rectangle(10, 0, 80, 125),
//				((Node)(aDiagram.getRootNodes().toArray()[0])).view().getBounds());
//		assertEquals(1, (((ImplicitParameterNode)(aDiagram.getRootNodes().toArray()[1])).getChildren().size()));
//	}
//	
//	/**
//	 * Testing copy and paste a whole diagram.
//	 */
//	@Test
//	public void testCopyPasteSequenceDiagram()
//	{
//		// test case set up 
//		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
//		CallNode middleCallNode = new CallNode();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(newParameterNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(new CallNode(), new Point(215, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
//		aDiagram.builder().addEdge(new CallEdge(), new Point(118, 75), new Point(215,75));
//		aDiagram.builder().addEdge(new ReturnEdge(), new Point(118, 75), new Point(18,75));
//		aDiagram.builder().addEdge(new CallEdge(), new Point(118, 75), new Point(210,115));
//		
//		aController.selectAll();
//		aController.copy();
//		aController.paste();
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(6, aDiagram.getRootNodes().size());
//		assertEquals(8, aDiagram.getEdges().size());
//	}
//	
//	/**
//	 * Testing copy and paste a whole diagram.
//	 */
//	@Test
//	public void testCopyPartialGraph()
//	{
//		// set up 
//		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
//		CallNode middleCallNode = new CallNode();
//		CallNode endCallNode = new CallNode();
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(newParaNode, new Point(210, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(middleCallNode, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addNode(endCallNode, new Point(215, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(115,75));
//		aDiagram.builder().addEdge(new CallEdge(), new Point(118, 75), new Point(215,75));
//		aDiagram.builder().addEdge(new ReturnEdge(), new Point(118, 75), new Point(18,75));
//		aDiagram.builder().addEdge(new CallEdge(), new Point(118, 75), new Point(210,115));
//		
//		aList.add(aCallNode1);
//		aList.add(middleCallNode);
//		aList.add(endCallNode);
//		for(Edge edge: aDiagram.getEdges())
//		{
//			aList.add(edge);
//		}
//		aController.getSelectionModel().setSelectionTo(aList);
//		aController.copy();
//		SequenceDiagram tempDiagram = new SequenceDiagram();
//		aController.paste();
//		tempDiagram.draw(aGraphics);
//		
//		assertEquals(0, tempDiagram.getRootNodes().size());
//		assertEquals(0, tempDiagram.getEdges().size());
//	}
//
//	/**
//	 * Tests the creation of a basic diagram.
//	 */
//	@Test
//	public void testCreateBasicGraph()
//	{
//		aDiagram.builder().addNode(aParameterNode1, new Point(10, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		assertEquals(new Rectangle(10,0,80,120), aParameterNode1.view().getBounds());
//		
//		aDiagram.builder().addNode(aParameterNode2, new Point(110, 0), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		assertEquals(new Rectangle(110,0,80,120), aParameterNode2.view().getBounds());
//
//		aDiagram.builder().addNode(aCallNode1, new Point(15, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		assertEquals(new Rectangle(15,75,16,30), aCallNode1.view().getBounds());
//
//		aDiagram.builder().addNode(aCallNode2, new Point(115, 75), Integer.MAX_VALUE, Integer.MAX_VALUE);
//		assertEquals(new Rectangle(115, 75,16,30), aCallNode2.view().getBounds());
//		
//		aDiagram.builder().addEdge(aCallEdge1, new Point(18, 75), new Point(120,75));
//		assertEquals(new Rectangle(30,68,86,12), aCallEdge1.view().getBounds());
//		
//		aDiagram.draw(aGraphics);
//		
//		assertEquals(new Rectangle(10,0,80,157), aParameterNode1.view().getBounds());
//		assertEquals(new Rectangle(110,0,80,157), aParameterNode2.view().getBounds());
//		assertEquals(new Rectangle(42,75,16,62), aCallNode1.view().getBounds());
//		assertEquals(new Rectangle(142,87,16,30), aCallNode2.view().getBounds());
//		assertEquals(new Rectangle(57,80,86,12), aCallEdge1.view().getBounds());
//	}	
}

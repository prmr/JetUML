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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvas;
import ca.mcgill.cs.jetuml.gui.DiagramTabToolBar;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Tests various interactions with Class Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Class Diagram.
 */

public class TestUsageScenariosClassDiagram 
{
	private ClassDiagram aDiagram;
	private GraphicsContext aGraphics;
	private DiagramCanvas aPanel;
	private SelectionList aList;
	private ClassNode aClassNode = new ClassNode();
	private InterfaceNode aInterfaceNode = new InterfaceNode();
	private PackageNode aPackageNode = new PackageNode();
	private NoteNode aNoteNode = new NoteNode();
	private AggregationEdge aAggregationEdge = new AggregationEdge();
	private AssociationEdge aAssociationEdge = new AssociationEdge();
	private DependencyEdge aDependencyEdge = new DependencyEdge();
	private GeneralizationEdge aGeneralizationEdge = new GeneralizationEdge();
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	@Before
	public void setup()
	{
		aDiagram = new ClassDiagram();
		aGraphics = new Canvas(256, 256).getGraphicsContext2D();
		aPanel = new DiagramCanvas(aDiagram, new DiagramTabToolBar(aDiagram), new Rectangle2D(0, 0, 0, 0));
		aList = new SelectionList();
		aClassNode = new ClassNode();
		aInterfaceNode = new InterfaceNode();
		aPackageNode = new PackageNode();
		aNoteNode = new NoteNode();
		aAggregationEdge = new AggregationEdge();
		aAssociationEdge = new AssociationEdge();
		aDependencyEdge = new DependencyEdge();
		aGeneralizationEdge = new GeneralizationEdge();
	}
	
	/**
	 * Below are methods testing basic nodes and edges creation for Class Diagram.
	 * 
	 * 
	 * 
	 * Testing basic Nodes creation.
	 */
	@Test
	public void testBasicNode()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aPackageNode, new Point(87, 87), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aNoteNode, new Point(134, 132), Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertEquals(4, aDiagram.getRootNodes().size());
		
		// set up the properties for the nodes
		aClassNode.setName("truck");
		aClassNode.setMethods("setDriver()");
		aInterfaceNode.setName("vehicle");
		aInterfaceNode.setMethods("getPrice()");
		aPackageNode.setName("object");
		aPackageNode.setContents("some stuff");
		aNoteNode.setName("some text...");
		
		// test node properties
		assertEquals(aClassNode.getName(), "truck");
		assertEquals(aClassNode.getMethods(), "setDriver()");
		assertEquals(aInterfaceNode.getMethods(), "getPrice()");
		assertEquals(aPackageNode.getName(), "object");
		assertEquals(aPackageNode.getContents(), "some stuff");
		assertEquals(aNoteNode.getName(), "some text...");
	}
	
	/**
	 * Testing basic edges creation.
	 */
	@Test
	public void testEdgeCreation()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aPackageNode, new Point(87, 87), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aNoteNode, new Point(134, 132), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		// both start and end points are invalid
		aDiagram.addEdge(aAggregationEdge, new Point(70, 70), new Point(170, 170));
		assertEquals(0, aDiagram.getEdges().size());
		// one point is invalid
		aDiagram.addEdge(aAggregationEdge, new Point(6, 7), new Point(170, 170));
		assertEquals(0, aDiagram.getEdges().size());
		
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point(47, 49), new Point(9, 17));
		aDiagram.addEdge(aDependencyEdge, new Point(90, 93), new Point(44, 49));
		assertEquals(3, aDiagram.getEdges().size());
		
		aDiagram.addEdge(new AssociationEdge(), new Point(47, 49), new Point(50, 49));
		assertEquals(4, aDiagram.getEdges().size());
		
		// not every edge is a valid self-edge
		aDiagram.addEdge(aGeneralizationEdge, new Point(47, 49), new Point(50, 49));
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing self-edge creation.
	 */
	@Test 
	public void testSelfEdgeCreation()
	{
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(new AssociationEdge(), new Point(47, 49), new Point(50, 49));
		assertEquals(1, aDiagram.getEdges().size());
		
		// not every edge is a valid self-edge
		aDiagram.addEdge(aGeneralizationEdge, new Point(47, 49), new Point(50, 49));
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing NodeEdge creation.
	 */
	@Test
	public void testNoteEdgeCreation()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aPackageNode, new Point(87, 87), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aNoteNode, new Point(134, 132), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		
		// if begin with a non-NoteNode type, both point needs to be valid
		aDiagram.addEdge(noteEdge1, new Point(9, 9), new Point(209,162));
		assertEquals(0, aDiagram.getEdges().size());
		aDiagram.addEdge(noteEdge1, new Point(9, 9), new Point(139,142));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(noteEdge1.getStart(), aClassNode);
		assertEquals(noteEdge1.getEnd(), aNoteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		aDiagram.addEdge(noteEdge2, new Point(138, 140), new Point(9,9));
		assertEquals(noteEdge2.getStart(), aNoteNode);
		assertEquals(noteEdge2.getEnd().getClass(), new PointNode().getClass());
		assertEquals(2, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing Single Node Movement.
	 */
	@Test
	public void testSingleNodeMovement()
	{
		
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aPackageNode, new Point(87, 87), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aNoteNode, new Point(134, 132), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		aClassNode.translate(5, 5);
		aInterfaceNode.translate(11, 19);
		aPackageNode.translate(32, -42);
		aNoteNode.translate(-5, 19);
		
		assertEquals(new Rectangle(10, 10, 100, 60), aClassNode.view().getBounds());
		assertEquals(new Rectangle(55, 63, 100, 60), aInterfaceNode.view().getBounds());
		assertEquals(new Rectangle(119, 45, 100, 80), aPackageNode.view().getBounds());
		assertEquals(new Rectangle(129, 151, 60, 40), aNoteNode.view().getBounds());
	}
	
	/**
	 * Testing moving a selection of nodes and edges,
	 * edges will be redrawn automatically.
	 */
	@Test
	public void testSelectionNodeAndEdges()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aPackageNode, new Point(87, 87), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aNoteNode, new Point(134, 132), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point(47, 49), new Point(9, 17));
		aDiagram.addEdge(aDependencyEdge, new Point(90, 93), new Point(44, 49));
		
		aList.add(aClassNode);
		aList.add(aAggregationEdge);
		aList.add(aInterfaceNode);
		Rectangle aggregationEdgeBounds = aAggregationEdge.view().getBounds();
		for(DiagramElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
		}
		assertEquals(new Rectangle(15, 15, 100, 60), aClassNode.view().getBounds());
		assertEquals(new Rectangle(54, 54, 100, 60), aInterfaceNode.view().getBounds());
		assertFalse(aggregationEdgeBounds == aAggregationEdge.view().getBounds());
		assertTrue(aClassNode == aAggregationEdge.getStart());
		assertTrue(aInterfaceNode == aAggregationEdge.getEnd());
	}
	
	/**
	 * Testing move a node with self-edge,
	 * edges will be redraw automatically.
	 */
	@Test
	public void testMoveNodeWithSelfEdge()
	{
		aDiagram.addNode(aClassNode, new Point(5, 7), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(12, 9));
		Rectangle oldAggregationEdgeBounds = aAggregationEdge.view().getBounds();
		aList.add(aClassNode);
		for(DiagramElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
		}
		assertEquals(new Rectangle(15, 17, 100, 60), aClassNode.view().getBounds());
		assertEquals(aClassNode, aAggregationEdge.getStart());
		assertEquals(aClassNode, aAggregationEdge.getEnd());
		assertFalse(oldAggregationEdgeBounds == aAggregationEdge.view().getBounds());
	}
	
	/**
	 * Testing move a node connect with another node.
	 * Edge will be redraw automatically. The unselected note will remain unmoved.
	 */
	@Test
	public void testMoveNodeConnectWithAnotherNode()
	{
	
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aPackageNode, new Point(87, 87), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point(47, 49), new Point(9, 17));
		aDiagram.addEdge(aDependencyEdge, new Point(90, 93), new Point(44, 49));
		
		Rectangle edge1Bounds = aAggregationEdge.view().getBounds();
		Rectangle edge3Bounds = aDependencyEdge.view().getBounds();
		Rectangle packageNodeBounds = aPackageNode.view().getBounds();
		Rectangle interFaceNodeBounds = aInterfaceNode.view().getBounds();

		aClassNode.translate(20, 20);
		assertEquals(aClassNode, aAggregationEdge.getStart());
		assertEquals(aInterfaceNode, aAggregationEdge.getEnd());
		assertFalse(edge1Bounds == aAggregationEdge.view().getBounds());
		assertEquals(interFaceNodeBounds, aInterfaceNode.view().getBounds());
		
		aInterfaceNode.translate(-19, 45);
		assertEquals(aPackageNode, aDependencyEdge.getStart());
		assertEquals(aInterfaceNode, aDependencyEdge.getEnd());
		assertFalse(edge3Bounds == aDependencyEdge.view().getBounds());
		assertEquals(packageNodeBounds, aPackageNode.view().getBounds());
	}
	
	/**
	 * Testing delete single node.
	 */
	@Test
	public void testDeleteSingleNode()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.draw(aGraphics);
		Rectangle classNodeBounds = aClassNode.view().getBounds();
		Rectangle interfaceNodeBounds = aInterfaceNode.view().getBounds();
		
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());
		
		aList.set(aInterfaceNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		
		aPanel.undo();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(interfaceNodeBounds, ((Node) (aDiagram.getRootNodes().toArray()[0])).view().getBounds());
		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(classNodeBounds, ((Node) (aDiagram.getRootNodes().toArray()[1])).view().getBounds());
	}
	
	/**
	 * Testing delete single edge.
	 */
	@Test
	public void testDeleteSingleEdge()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.draw(aGraphics);
		Rectangle edgeBounds = aAggregationEdge.view().getBounds();
		
		// test deletion
		aList.set(aAggregationEdge);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getEdges().size());
		
		// test undo
		aPanel.undo();
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(edgeBounds, ((Edge) (aDiagram.getEdges().toArray()[0])).view().getBounds());
	}
	
	/**
	 * Testing multiple edges of the same type cannot exist between the same nodes.
	 */
	@Test
	public void testMultipleEdgesSameTypeCanNotExist()
	{		
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aDependencyEdge, new Point(8, 10), new Point(45, 48)); 
		aDiagram.addEdge(aGeneralizationEdge, new Point(8, 10), new Point(45, 48));
		assertEquals(4, aDiagram.getEdges().size());
		
		// new edges should not be added to the diagram's edges
		aDiagram.addEdge(aAggregationEdge, new Point(9, 11), new Point(46, 49));
		aDiagram.addEdge(aAssociationEdge, new Point(9, 11), new Point(46, 49));
		aDiagram.addEdge(aDependencyEdge, new Point(9, 11), new Point(46, 49)); 
		aDiagram.addEdge(aGeneralizationEdge, new Point(9, 11), new Point(46, 49));
		assertEquals(4, aDiagram.getEdges().size());

	}
	
	/**
	 * Testing new edges of the same type do not replace original edges.
	 */
	@Test
	public void testNewEdgesSameTypeDoNotReplaceOriginalEdges()
	{
		AggregationEdge aSecondAggregationEdge = new AggregationEdge();
		AssociationEdge aSecondAssociationEdge = new AssociationEdge();
		DependencyEdge aSecondDependencyEdge = new DependencyEdge();
		GeneralizationEdge aSecondGeneralizationEdge = new GeneralizationEdge();
		
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.addEdge(aDependencyEdge, new Point(8, 10), new Point(45, 48)); 
		aDiagram.addEdge(aGeneralizationEdge, new Point(8, 10), new Point(45, 48));
		assertEquals(4, aDiagram.getEdges().size());
		
		// new edges should not replace the current edges in the diagram
		aDiagram.addEdge(aSecondAggregationEdge, new Point(9, 111), new Point(46, 49));
		aDiagram.addEdge(aSecondAssociationEdge, new Point(9, 111), new Point(46, 49));
		aDiagram.addEdge(aSecondDependencyEdge, new Point(9, 111), new Point(46, 49));
		aDiagram.addEdge(aSecondGeneralizationEdge, new Point(9, 111), new Point(46, 49));
		assertEquals(4, aDiagram.getEdges().size());
		assertFalse(aDiagram.getEdges().contains(aSecondAggregationEdge));
		assertFalse(aDiagram.getEdges().contains(aSecondAssociationEdge));
		assertFalse(aDiagram.getEdges().contains(aSecondDependencyEdge));
		assertFalse(aDiagram.getEdges().contains(aSecondGeneralizationEdge));
	}
	
	
	/**
	 * Testing delete an edge and node combination, selecting one node in the first case.
	 */
	@Test 
	public void testDeleteNodeEdgeCombination1()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.draw(aGraphics);
		Rectangle edgeBounds = aAggregationEdge.view().getBounds();
		Rectangle classNodeBounds = aClassNode.view().getBounds();
		
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.undo();
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(edgeBounds, ((Edge) (aDiagram.getEdges().toArray()[0])).view().getBounds());
		assertEquals(classNodeBounds, ((Node) (aDiagram.getRootNodes().toArray()[1])).view().getBounds());
	}
	
	/**
	 * Testing delete an edge and node combination, selecting all.
	 */
	@Test 
	public void testDeleteNodeEdgeCombination2()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(44, 44), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.draw(aGraphics);
		Rectangle edgeBounds = aAggregationEdge.view().getBounds();
		Rectangle classNodeBounds = aClassNode.view().getBounds();
		Rectangle interfaceNodeBounds = aInterfaceNode.view().getBounds();

		aPanel.selectAll();
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		aPanel.undo();
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(edgeBounds, ((Edge) (aDiagram.getEdges().toArray()[0])).view().getBounds());
		for(Node node: aDiagram.getRootNodes())
		{
			if(node instanceof ClassNode)
			{
				assertEquals(classNodeBounds,node.view().getBounds());
			}
			else
			{
				assertEquals(interfaceNodeBounds,node.view().getBounds());
			}
		}
	}
	
	/**
	 * Testing delete one of ClassNode inside the PackageNode and undo the operation.
	 */
	@Test
	public void testDeleteNodeInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aDiagram.addNode(aPackageNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(6, 8), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(11, 12), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aList.set(node2);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(1, aPackageNode.getChildren().size());
		aPanel.undo();
		assertEquals(2, aPackageNode.getChildren().size());
	}

	/**
	 * Testing delete one of ClassNode inside the PackageNode
	 * which is nested in another PackageNode, and undo the operation.
	 */
	@Test
	public void testDeleteNodeInsideNestedPackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		PackageNode innerNode = new PackageNode();
		aDiagram.addNode(aPackageNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(innerNode, new Point(10, 10), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(10, 13), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(11, 12), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aList.set(node2);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		aDiagram.draw(aGraphics);
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(1, innerNode.getChildren().size());
		aPanel.undo();
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(2, innerNode.getChildren().size());
	}
	
	/**
	 * Testing copy and paste single node.
	 */
	@Test
	public void testCopyPasteSingleNode()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.copy();
		aPanel.paste();
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(0, 0, 100, 60), 
				((Node) aDiagram.getRootNodes().toArray()[1]).view().getBounds());
	}
	
	/**
	 * Testing cut and paste single node.
	 */
	@Test
	public void testCutPasteSingleNode()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.cut();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		aPanel.paste();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle(0, 0, 100, 60), 
				((Node) aDiagram.getRootNodes().toArray()[0]).view().getBounds());
	}
	
	/**
	 * Testing copy and paste a combination of nodes and edge.
	 */
	@Test
	public void testCopyPasteCombinationNodeAndEdge()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(45, 45), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.draw(aGraphics);
		
		aPanel.selectAll();
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		
		Object[] nodes = aDiagram.getRootNodes().toArray();
		boolean trigger1 = false;
		boolean trigger2 = false;
		for(int i = 2; i < nodes.length; i++)
		{
			if(nodes[i] instanceof ClassNode)
			{
				trigger1 = true;
				assertEquals(new Rectangle(10, 10, 100, 60), 
						((Node) aDiagram.getRootNodes().toArray()[0]).view().getBounds());
			}
			else
			{
				trigger2 = true;
			}
		}
		assertTrue(trigger1 && trigger2);
		
		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		
	}
	
	/**
	 * Testing cut and paste a combination of nodes and edge.
	 */
	@Test
	public void testCutPasteCombinationNodeAndEdge()
	{
		aDiagram.addNode(aClassNode, new Point(5, 5), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(aInterfaceNode, new Point(45, 45), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addEdge(aAggregationEdge, new Point(8, 10), new Point(45, 48));
		aDiagram.draw(aGraphics);
		
		aPanel.selectAll();
		aPanel.cut();
		aDiagram.draw(aGraphics);
		
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		
		Object[] nodes = aDiagram.getRootNodes().toArray();
		boolean trigger1 = false;
		boolean trigger2 = false;
		for(int i = 0; i < nodes.length; i++)
		{
			if(nodes[i] instanceof ClassNode)
			{
				trigger1 = true;
				assertEquals(new Rectangle(0, 0, 100, 60), 
						((Node) aDiagram.getRootNodes().toArray()[0]).view().getBounds());
			}
			else
			{
				trigger2 = true;
			}
		}
		assertTrue(trigger1 && trigger2);
		
		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		aPanel.undo();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/** 
	 * Testing Connect the inner nodes with edges.
	 */
	@Test
	public void testAddNodesToPackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		PackageNode innerNode = new PackageNode();
		aDiagram.addNode(aPackageNode, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(25, 25), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(30, 30), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(innerNode, new Point(35, 45), Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(3, aPackageNode.getChildren().size());
	}
	
	/** 
	 * Testing add classNode to the inner PackageNode.
	 */
	@Test
	public void testAddNodeToInnerPackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		PackageNode innerNode = new PackageNode();
		aDiagram.addNode(aPackageNode, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(innerNode, new Point(25, 25), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(26, 29), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(30, 31), Integer.MAX_VALUE, Integer.MAX_VALUE);
	
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(2, innerNode.getChildren().size());
	}
	
	/** 
	 * Testing Connect the inner nodes with edges.
	 */
	@Test
	public void testConnectInnerNodeWithEdges()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aDiagram.addNode(aPackageNode, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(25, 25), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(30, 30), Integer.MAX_VALUE, Integer.MAX_VALUE);
		node2.translate(100, 0);
		aDiagram.addEdge(aDependencyEdge, new Point(26, 26), new Point(131, 31));
	
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/** 
	 * Testing cut and paste PackageNode
	 */
	@Test
	public void testCutNodesAndEdgesInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aDiagram.addNode(aPackageNode, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(25, 25), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(30, 30), Integer.MAX_VALUE, Integer.MAX_VALUE);
		node2.translate(100, 0);
		aDiagram.addEdge(aDependencyEdge, new Point(26, 26), new Point(131, 31));
		
		aPanel.selectAll();
		aPanel.cut(); 
		aDiagram.draw(aGraphics);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Rectangle(5, 5, 210, 90), 
				((Node) aDiagram.getRootNodes().toArray()[0]).view().getBounds());
	}
	
	/**
	 * Testing copy and paste inside PackageNode
	 */
	@Test
	public void testCopyNodesAndEdgesInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aDiagram.addNode(aPackageNode, new Point(20, 20), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node1, new Point(25, 25), Integer.MAX_VALUE, Integer.MAX_VALUE);
		aDiagram.addNode(node2, new Point(30, 30), Integer.MAX_VALUE, Integer.MAX_VALUE);
		node2.translate(100, 0);
		aDiagram.addEdge(aDependencyEdge, new Point(26, 26), new Point(131, 31));
		
		aPanel.selectAll();
		aPanel.copy();
		aPanel.paste();
		aDiagram.draw(aGraphics);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Rectangle(5, 5, 210, 90), 
				((Node) aDiagram.getRootNodes().toArray()[1]).view().getBounds());
	}	
}

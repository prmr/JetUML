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

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * Tests various interactions with Class Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Class Diagram.
 * 
 * @author Jiajun Chen
 *
 */

public class TestUsageScenariosClassDiagram 
{
	private ClassDiagramGraph aDiagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard aClipboard;
	private SelectionList aList;
	private ClassNode aClassNode = new ClassNode();
	private InterfaceNode aInterfaceNode = new InterfaceNode();
	private PackageNode aPackageNode = new PackageNode();
	private NoteNode aNoteNode = new NoteNode();
	private ClassRelationshipEdge aAggregationEdge = new AggregationEdge();
	private ClassRelationshipEdge aAssociationEdge = new AssociationEdge();
	private ClassRelationshipEdge aDependencyEdge = new DependencyEdge();
	private ClassRelationshipEdge aGeneralizationEdge = new GeneralizationEdge();
	
	@Before
	public void setup()
	{
		aDiagram = new ClassDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram));
		aGrid = new Grid();
		aClipboard = new Clipboard();
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
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aDiagram.addNode(aNoteNode, new Point2D.Double(134, 132));

		assertEquals(4, aDiagram.getRootNodes().size());
		
		// set up the properties for the nodes
		aClassNode.getName().setText("truck");
		aClassNode.getMethods().setText("setDriver()");
		aInterfaceNode.getName().setText("vehicle");
		aInterfaceNode.getMethods().setText("getPrice()");
		aPackageNode.setName("object");
		aPackageNode.getContents().setText("some stuff");
		aNoteNode.getText().setText("some text...");
		
		// test node properties
		assertEquals(aClassNode.getName().getText(), "truck");
		assertEquals(aClassNode.getMethods().getText(), "setDriver()");
		assertEquals(aInterfaceNode.getMethods().getText(), "getPrice()");
		assertEquals(aPackageNode.getName(), "object");
		assertEquals(aPackageNode.getContents().getText(), "some stuff");
		assertEquals(aNoteNode.getText().getText(), "some text...");
	}
	
	/**
	 * Testing basic edges creation.
	 */
	@Test
	public void testEdgeCreation()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aDiagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		// both start and end points are invalid
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(70, 70), new Point2D.Double(170, 170));
		assertEquals(0, aDiagram.getEdges().size());
		// one point is invalid
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(6, 7), new Point2D.Double(170, 170));
		assertEquals(0, aDiagram.getEdges().size());
		
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		aDiagram.addEdge(aDependencyEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		assertEquals(3, aDiagram.getEdges().size());
		
		aDiagram.addEdge(new AssociationEdge(), new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, aDiagram.getEdges().size());
		
		// not every edge is a valid self-edge
		aDiagram.addEdge(aGeneralizationEdge, new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing self-edge creation.
	 */
	@Test 
	public void testSelfEdgeCreation()
	{
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addEdge(new AssociationEdge(), new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(1, aDiagram.getEdges().size());
		
		// not every edge is a valid self-edge
		aDiagram.addEdge(aGeneralizationEdge, new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(1, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing NodeEdge creation.
	 */
	@Test
	public void testNoteEdgeCreation()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aDiagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		
		// if begin with a non-NoteNode type, both point needs to be valid
		aDiagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(209,162));
		assertEquals(0, aDiagram.getEdges().size());
		aDiagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(139,142));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(noteEdge1.getStart(), aClassNode);
		assertEquals(noteEdge1.getEnd(), aNoteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		aDiagram.addEdge(noteEdge2, new Point2D.Double(138, 140), new Point2D.Double(9,9));
		assertEquals(noteEdge2.getStart(), aNoteNode);
		assertEquals(noteEdge2.getEnd().getClass(), new PointNode().getClass());
		assertEquals(2, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing Single Node Movement.
	 */
	@Test
	public void testSignelNodeMovement()
	{
		
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aDiagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		aClassNode.translate(5, 5);
		aInterfaceNode.translate(11, 19);
		aPackageNode.translate(32, -42);
		aNoteNode.translate(-5, 19);
		
		assertEquals(new Rectangle2D.Double(10, 10, 100, 60), aClassNode.getBounds());
		assertEquals(new Rectangle2D.Double(55, 63, 100, 60), aInterfaceNode.getBounds());
		assertEquals(new Rectangle2D.Double(119, 45, 100, 80), aPackageNode.getBounds());
		assertEquals(new Rectangle2D.Double(129, 151, 60, 40), aNoteNode.getBounds());
	}
	
	/**
	 * Testing moving a selection of nodes and edges,
	 * edges will be redrawn automatically.
	 */
	@Test
	public void testSelectionNodeAndEdges()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aDiagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		aDiagram.addEdge(aDependencyEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		
		aList.add(aClassNode);
		aList.add(aAggregationEdge);
		aList.add(aInterfaceNode);
		Rectangle2D aggregationEdgeBond = aAggregationEdge.getBounds();
		for(GraphElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
		}
		assertEquals(new Rectangle2D.Double(15, 15, 100, 60), aClassNode.getBounds());
		assertTrue(54 == aInterfaceNode.getBounds().getY());
		assertFalse(aggregationEdgeBond == aAggregationEdge.getBounds());
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
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 7));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(12, 9));
		Rectangle2D oldAggregationEdgeBond = aAggregationEdge.getBounds();
		aList.add(aClassNode);
		for(GraphElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
		}
		assertEquals(new Rectangle2D.Double(15, 17, 100, 60), aClassNode.getBounds());
		assertEquals(aClassNode, aAggregationEdge.getStart());
		assertEquals(aClassNode, aAggregationEdge.getEnd());
		assertFalse(oldAggregationEdgeBond == aAggregationEdge.getBounds());
	}
	
	/**
	 * Testing move a node connect with another node.
	 * Edge will be redraw automatically. The unselected note will remain unmoved.
	 */
	public void testMoveNodeConnectWithAnotherNode()
	{
	
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.addEdge(aAssociationEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		aDiagram.addEdge(aDependencyEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		
		Rectangle2D edge1Bond = aAggregationEdge.getBounds();
		Rectangle2D edge3Bond = aDependencyEdge.getBounds();
		Rectangle2D packageNodeBonds = aPackageNode.getBounds();
		Rectangle2D interFaceNodeBonds = aInterfaceNode.getBounds();

		aClassNode.translate(20, 20);
		assertEquals(aClassNode, aAggregationEdge.getStart());
		assertEquals(aInterfaceNode, aAggregationEdge.getEnd());
		assertFalse(edge1Bond == aAggregationEdge.getBounds());
		assertEquals(interFaceNodeBonds, aInterfaceNode.getBounds());

		
		aInterfaceNode.translate(-19, 45);
		assertEquals(aPackageNode, aDependencyEdge.getStart());
		assertEquals(aInterfaceNode, aDependencyEdge.getEnd());
		assertFalse(edge3Bond == aDependencyEdge.getBounds());
		assertEquals(packageNodeBonds, aPackageNode.getBounds());
	}
	
	/**
	 * Testing delete single node.
	 */
	@Test
	public void testDeleteSingleNode()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.draw(aGraphics, new Grid());
		Rectangle2D classNodeBound = aClassNode.getBounds();
		Rectangle2D interfaceNodeBound = aInterfaceNode.getBounds();
		
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics, new Grid());
		assertEquals(1, aDiagram.getRootNodes().size());
		
		aList.set(aInterfaceNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics, new Grid());
		assertEquals(0, aDiagram.getRootNodes().size());
		
		aPanel.undo();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(interfaceNodeBound, ((Node) (aDiagram.getRootNodes().toArray()[0])).getBounds());
		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(classNodeBound, ((Node) (aDiagram.getRootNodes().toArray()[1])).getBounds());
	}
	
	/**
	 * Testing delete single edge.
	 */
	@Test
	public void testDeleteSingleEdge()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.draw(aGraphics, new Grid());
		Rectangle2D edgeBond = aAggregationEdge.getBounds();
		
		// test deletion
		aList.set(aAggregationEdge);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics, new Grid());
		assertEquals(0, aDiagram.getEdges().size());
		
		// test undo
		aPanel.undo();
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(edgeBond, ((Edge) (aDiagram.getEdges().toArray()[0])).getBounds());
	}
	
	/**
	 * Testing delete an edge and node combination, selecting one node in the first case.
	 */
	@Test 
	public void testDeleteNodeEdgeCombination1()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.draw(aGraphics, new Grid());
		Rectangle2D edgeBond = aAggregationEdge.getBounds();
		Rectangle2D classNodeBond = aClassNode.getBounds();
		
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		aDiagram.draw(aGraphics, new Grid());
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());

		aPanel.undo();
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(edgeBond, ((Edge) (aDiagram.getEdges().toArray()[0])).getBounds());
		assertEquals(classNodeBond, ((Node) (aDiagram.getRootNodes().toArray()[1])).getBounds());
	}
	
	/**
	 * Testing delete an edge and node combination, selecting all.
	 */
	@Test 
	public void testDeleteNodeEdgeCombination2()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.draw(aGraphics, new Grid());
		Rectangle2D edgeBond = aAggregationEdge.getBounds();
		Rectangle2D classNodeBond = aClassNode.getBounds();
		Rectangle2D interfaceNodeBond = aInterfaceNode.getBounds();

		aPanel.selectAll();
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, new Grid());
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		aPanel.undo();
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(edgeBond, ((Edge) (aDiagram.getEdges().toArray()[0])).getBounds());
		for(Node node: aDiagram.getRootNodes())
		{
			if(node instanceof ClassNode)
			{
				assertEquals(classNodeBond,node.getBounds());
			}
			else
			{
				assertEquals(interfaceNodeBond,node.getBounds());
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
		aDiagram.addNode(aPackageNode, new Point2D.Double(5, 5));
		aDiagram.addNode(node1, new Point2D.Double(6, 8));
		aDiagram.addNode(node2, new Point2D.Double(11, 12));
		aList.set(node2);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
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
		aDiagram.addNode(aPackageNode, new Point2D.Double(5, 5));
		aDiagram.addNode(innerNode, new Point2D.Double(10, 10));
		aDiagram.addNode(node1, new Point2D.Double(10, 13));
		aDiagram.addNode(node2, new Point2D.Double(11, 12));
		aList.set(node2);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
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
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aList.set(aClassNode);
		aClipboard.copy(aList);
		aClipboard.paste(aPanel);
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle2D.Double(0, 0, 100, 60), 
				((Node) aDiagram.getRootNodes().toArray()[1]).getBounds());
	}
	
	/**
	 * Testing cut and paste single node.
	 */
	@Test
	public void testCutPasteSingleNode()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aList.set(aClassNode);
		aClipboard.copy(aList);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());
		aClipboard.paste(aPanel);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle2D.Double(0, 0, 100, 60), 
				((Node) aDiagram.getRootNodes().toArray()[0]).getBounds());
	}
	
	/**
	 * Testing copy and paste a combination of nodes and edge.
	 */
	@Test
	public void testCopyPasteCombinationNodeAndEdge()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(45, 45));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.draw(aGraphics, new Grid());
		
		aPanel.selectAll();
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, new Grid());
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
				assertEquals(new Rectangle2D.Double(10, 10, 100, 60), 
						((Node) aDiagram.getRootNodes().toArray()[0]).getBounds());
			}
			else
			{
				trigger2 = true;
			}
		}
		assertTrue(trigger1 && trigger2);
		
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		
	}
	
	/**
	 * Testing cut and paste a combination of nodes and edge.
	 */
	@Test
	public void testCutPasteCombinationNodeAndEdge()
	{
		aDiagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aDiagram.addNode(aInterfaceNode, new Point2D.Double(45, 45));
		aDiagram.addEdge(aAggregationEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		aDiagram.draw(aGraphics, new Grid());
		
		aPanel.selectAll();
		aClipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, new Grid());
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
				assertEquals(new Rectangle2D.Double(0, 0, 100, 60), 
						((Node) aDiagram.getRootNodes().toArray()[0]).getBounds());
			}
			else
			{
				trigger2 = true;
			}
		}
		assertTrue(trigger1 && trigger2);
		
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
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
		aDiagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		aDiagram.addNode(node1, new Point2D.Double(25, 25));
		aDiagram.addNode(node2, new Point2D.Double(30, 30));
		aDiagram.addNode(innerNode, new Point2D.Double(35, 45));
		
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
		aDiagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		aDiagram.addNode(innerNode, new Point2D.Double(25, 25));
		aDiagram.addNode(node1, new Point2D.Double(26, 29));
		aDiagram.addNode(node2, new Point2D.Double(30, 31));
	
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
		aDiagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		aDiagram.addNode(node1, new Point2D.Double(25, 25));
		aDiagram.addNode(node2, new Point2D.Double(30, 30));
		node2.translate(100, 0);
		aDiagram.addEdge(aDependencyEdge, new Point2D.Double(26, 26), new Point2D.Double(131, 31));
	
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
		aDiagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		aDiagram.addNode(node1, new Point2D.Double(25, 25));
		aDiagram.addNode(node2, new Point2D.Double(30, 30));
		node2.translate(100, 0);
		aDiagram.addEdge(aDependencyEdge, new Point2D.Double(26, 26), new Point2D.Double(131, 31));
		
		aPanel.selectAll();
		aClipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(new Rectangle2D.Double(5, -15, 210, 90), 
				((Node) aDiagram.getRootNodes().toArray()[0]).getBounds());
	}

	@Test
	public void testCopyNodesAndEdgesInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aDiagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		aDiagram.addNode(node1, new Point2D.Double(25, 25));
		aDiagram.addNode(node2, new Point2D.Double(30, 30));
		node2.translate(100, 0);
		aDiagram.addEdge(aDependencyEdge, new Point2D.Double(26, 26), new Point2D.Double(131, 31));
		
		aPanel.selectAll();
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		System.out.println(((Node) aDiagram.getRootNodes().toArray()[1]).getBounds());
		assertEquals(new Rectangle2D.Double(5, -15, 210, 90), 
				((Node) aDiagram.getRootNodes().toArray()[1]).getBounds());
	}
	
	
	
	
		
}

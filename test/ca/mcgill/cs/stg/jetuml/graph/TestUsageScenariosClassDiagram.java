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
	
	private ClassDiagramGraph diagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard clipboard;
	private SelectionList aList;
	private ClassNode aClassNode = new ClassNode();
	private InterfaceNode aInterfaceNode = new InterfaceNode();
	private PackageNode aPackageNode = new PackageNode();
	private NoteNode aNoteNode = new NoteNode();
	private ClassRelationshipEdge aggrEdge = new AggregationEdge();
	private ClassRelationshipEdge assoEdge = new AssociationEdge();
	private ClassRelationshipEdge depeEdge = new DependencyEdge();
	private ClassRelationshipEdge geneEdge = new GeneralizationEdge();
	
	@Before
	public void setup()
	{
		diagram = new ClassDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGrid = new Grid();
		clipboard = new Clipboard();
		aList = new SelectionList();
		aClassNode = new ClassNode();
		aInterfaceNode = new InterfaceNode();
		aPackageNode = new PackageNode();
		aNoteNode = new NoteNode();
		aggrEdge = new AggregationEdge();
		assoEdge = new AssociationEdge();
		depeEdge = new DependencyEdge();
		geneEdge = new GeneralizationEdge();
		
	}
	
	@Test
	public void testBasicNode()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));

		assertEquals(4, diagram.getRootNodes().size());
		
		// set up the properties for the nodes
		aClassNode.getName().setText("truck");
		aClassNode.getMethods().setText("setDriver()");
		aInterfaceNode.getName().setText("vehicle");
		aInterfaceNode.getMethods().setText("getPrice()");
		aPackageNode.setName("object");
		aPackageNode.getContents().setText("some stuff");
		aNoteNode.getText().setText("some text...");
		
		// 1.1 test nodes properties
		assertEquals(aClassNode.getName().getText(), "truck");
		assertEquals(aClassNode.getMethods().getText(), "setDriver()");
		assertEquals(aInterfaceNode.getMethods().getText(), "getPrice()");
		assertEquals(aPackageNode.getName(), "object");
		assertEquals(aPackageNode.getContents().getText(), "some stuff");
		assertEquals(aNoteNode.getText().getText(), "some text...");
	}
	
	@Test
	public void testEdgeCreation()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		// both start and end points are invalid
		diagram.addEdge(aggrEdge, new Point2D.Double(70, 70), new Point2D.Double(170, 170));
		// one point is invalid
		diagram.addEdge(aggrEdge, new Point2D.Double(6, 7), new Point2D.Double(170, 170));
		assertEquals(0, diagram.getEdges().size());
		
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(assoEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(depeEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		assertEquals(3, diagram.getEdges().size());
		
		diagram.addEdge(new AssociationEdge(), new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, diagram.getEdges().size());
		
		// not every edge is a valid to be self-edge
		diagram.addEdge(geneEdge, new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, diagram.getEdges().size());
	}
	
	@Test
	public void testNoteEdgeCreation()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		
		// if begin with a non-NoteNode type, both point needs to be valid
		diagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(209,162));
		assertEquals(0, diagram.getEdges().size());
		diagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(139,142));
		assertEquals(1, diagram.getEdges().size());
		assertEquals(noteEdge1.getStart(), aClassNode);
		assertEquals(noteEdge1.getEnd(), aNoteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		diagram.addEdge(noteEdge2, new Point2D.Double(138, 140), new Point2D.Double(9,9));
		assertEquals(noteEdge2.getStart(), aNoteNode);
		assertEquals(noteEdge2.getEnd().getClass(), new PointNode().getClass());
		assertEquals(2, diagram.getEdges().size());

	}
	
	/**
	 * Testing Single Node Movement
	 */
	@Test
	public void testSignelNodeMovement()
	{
		
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		aClassNode.translate(5, 5);
		aInterfaceNode.translate(11, 19);
		aPackageNode.translate(32, -42);
		aNoteNode.translate(-5, 19);
		
		assertTrue(10 == aClassNode.getBounds().getX());
		assertTrue(10 == aClassNode.getBounds().getY());
		assertTrue(55 == aInterfaceNode.getBounds().getX());
		assertTrue(63 == aInterfaceNode.getBounds().getY());
		assertTrue((87+32) == aPackageNode.getBounds().getX());
		assertTrue((87-42) == aPackageNode.getBounds().getY());
		assertTrue(129 == aNoteNode.getBounds().getX());
		assertTrue(151 == aNoteNode.getBounds().getY());
	}
	
	/**
	 * Testing move a selection of nodes and edges,
	 * edges will be redraw automatically.
	 */
	@Test
	public void testSelectionNodeAndEdges()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(assoEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(depeEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		
		SelectionList aList = new SelectionList();
		aList.add(aClassNode);
		aList.add(aggrEdge);
		aList.add(aInterfaceNode);
		Rectangle2D edge1_old_bonds = aggrEdge.getBounds();
		for(GraphElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
			// edges will move automatically
			/*
			else
			{
				((Edge) element).connect(((Edge) element).getStart(), ((Edge) element).getEnd());
			}		
			*/
		}
		assertTrue(15 == aClassNode.getBounds().getX());
		assertTrue(15 == aClassNode.getBounds().getY());
		assertTrue(54 == aInterfaceNode.getBounds().getX());
		assertTrue(54 == aInterfaceNode.getBounds().getY());
		assertFalse(edge1_old_bonds.getY() == aggrEdge.getBounds().getY());
		assertTrue(aClassNode == aggrEdge.getStart());
		assertTrue(aInterfaceNode == aggrEdge.getEnd());
	}
	
	/**
	 * Testing move a node with self-edge
	 * edges will be redraw automatically.
	 */
	@Test
	public void testMoveNodeWithSelfEdge()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 7));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(12, 9));
		double old_y = aggrEdge.getBounds().getY();
		double old_x = aggrEdge.getBounds().getX();
		aList.add(aClassNode);
		for(GraphElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
		}
		assertTrue(15 == aClassNode.getBounds().getX());
		assertTrue(17 == aClassNode.getBounds().getY());
		assertEquals(aClassNode, aggrEdge.getStart());
		assertEquals(aClassNode, aggrEdge.getEnd());
		assertTrue((10+old_x) == aggrEdge.getBounds().getX());
		assertTrue((10+old_y) == aggrEdge.getBounds().getY());
	}
	
	/**
	 * Testing move a node connect with another node.
	 * Edge will be redraw automatically. The unselected note will remain unmoved.
	 */
	public void testMoveNodeConnectWithAnotherNode()
	{
	
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(assoEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(depeEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		
		double old_x_edge1 = aggrEdge.getBounds().getX();
		double old_y_edge1 = aggrEdge.getBounds().getY();
		double old_x_edge3 = aggrEdge.getBounds().getX();
		double old_y_edge3 = aggrEdge.getBounds().getY();
		Rectangle2D packageNodeBonds = aPackageNode.getBounds();
		Rectangle2D interFaceNodeBonds = aInterfaceNode.getBounds();

		aClassNode.translate(20, 20);
		assertEquals(aClassNode, aggrEdge.getStart());
		assertEquals(aInterfaceNode, aggrEdge.getEnd());
		assertFalse(old_x_edge1 == aggrEdge.getBounds().getX());
		assertFalse(old_y_edge1 == aggrEdge.getBounds().getY());
		assertEquals(interFaceNodeBonds, aInterfaceNode.getBounds());

		
		aInterfaceNode.translate(-19, 45);
		assertEquals(aPackageNode, depeEdge.getStart());
		assertEquals(aInterfaceNode, depeEdge.getEnd());
		assertFalse(old_x_edge3 == depeEdge.getBounds().getX());
		assertFalse(old_y_edge3 == depeEdge.getBounds().getY());
		assertEquals(packageNodeBonds, aPackageNode.getBounds());
	}
	
	/**
	 * Testing delete single node
	 */
	@Test
	public void testDeleteSingleNode()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.draw(aGraphics, new Grid());
		Rectangle2D old_bound_classNode = aClassNode.getBounds();
		Rectangle2D old_bound_interfaceNode = aInterfaceNode.getBounds();
		
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(1, diagram.getRootNodes().size());
		
		aList.set(aInterfaceNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(0, diagram.getRootNodes().size());
		
		aPanel.undo();
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(old_bound_interfaceNode, ((Node) (diagram.getRootNodes().toArray()[0])).getBounds());
		aPanel.undo();
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(old_bound_classNode, ((Node) (diagram.getRootNodes().toArray()[1])).getBounds());
	}
	
	/**
	 * Testing delete single edge
	 */
	@Test
	public void testDeleteSingleEdge()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.draw(aGraphics, new Grid());
		Rectangle2D edgeBond = aggrEdge.getBounds();
		
		// test deletion
		aList.set(aggrEdge);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(0, diagram.getEdges().size());
		
		// test undo
		aPanel.undo();
		assertEquals(1, diagram.getEdges().size());
		assertEquals(edgeBond, ((Edge) (diagram.getEdges().toArray()[0])).getBounds());
	}
	
	/**
	 * Testing delete an edge and node combination, selecting one node in the first case
	 */
	@Test 
	public void testDeleteNodeEdgeCombination1()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.draw(aGraphics, new Grid());
		Rectangle2D edgeBond = aggrEdge.getBounds();
		Rectangle2D old_bound_classNode = aClassNode.getBounds();
		
		aList.set(aClassNode);
		aPanel.setSelectionList(aList);
		aPanel.removeSelected();
		aList.clearSelection();
		diagram.draw(aGraphics, new Grid());
		
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());

		aPanel.undo();
		assertEquals(1, diagram.getEdges().size());
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(edgeBond, ((Edge) (diagram.getEdges().toArray()[0])).getBounds());
		assertEquals(old_bound_classNode, ((Node) (diagram.getRootNodes().toArray()[1])).getBounds());
	}
	
	/**
	 * Testing delete an edge and node combination, selecting all
	 */
	@Test 
	public void testDeleteNodeEdgeCombination2()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.draw(aGraphics, new Grid());
		Rectangle2D edgeBond = aggrEdge.getBounds();
		Rectangle2D old_bound_classNode = aClassNode.getBounds();
		Rectangle2D old_bound_interfaceNode = aInterfaceNode.getBounds();

		aPanel.selectAll();
		aPanel.removeSelected();
		diagram.draw(aGraphics, new Grid());
		assertEquals(0, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());
		aPanel.undo();
		assertEquals(1, diagram.getEdges().size());
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(edgeBond, ((Edge) (diagram.getEdges().toArray()[0])).getBounds());
		for(Node node: diagram.getRootNodes())
		{
			if(node instanceof ClassNode)
			{
				assertEquals(old_bound_classNode,node.getBounds());
			}
			else
			{
				assertEquals(old_bound_interfaceNode,node.getBounds());
			}
		}
	}
	
	/**
	 * Testing delete one of ClassNode inside the PackageNode and undo the operation
	 */
	@Test
	public void testDeleteNodeInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		diagram.addNode(aPackageNode, new Point2D.Double(5, 5));
		diagram.addNode(node1, new Point2D.Double(6, 8));
		diagram.addNode(node2, new Point2D.Double(11, 12));
		aList.set(node2);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		assertEquals(1, aPackageNode.getChildren().size());
		aPanel.undo();
		assertEquals(2, aPackageNode.getChildren().size());
	}

	/**
	 * Testing delete one of ClassNode inside the PackageNode
	 * which is nested in another PackageNode, and undo the operation
	 */
	@Test
	public void testDeleteNodeInsideNestedPackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		PackageNode innerNode = new PackageNode();
		diagram.addNode(aPackageNode, new Point2D.Double(5, 5));
		diagram.addNode(innerNode, new Point2D.Double(10, 10));
		diagram.addNode(node1, new Point2D.Double(10, 13));
		diagram.addNode(node2, new Point2D.Double(11, 12));
		aList.set(node2);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(1, innerNode.getChildren().size());
		aPanel.undo();
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(2, innerNode.getChildren().size());
	}
	
	/**
	 * Testing copy and paste single node
	 */
	@Test
	public void testCopyPasteSingleNode()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aList.set(aClassNode);
		clipboard.copy(aList);
		clipboard.paste(aPanel);
		
		assertEquals(2, diagram.getRootNodes().size());
		assertTrue(0 == ((Node) diagram.getRootNodes().toArray()[1]).getBounds().getY());
		assertTrue(0 == ((Node) diagram.getRootNodes().toArray()[1]).getBounds().getX());
	}
	
	/**
	 * Testing cut and paste single node
	 */
	@Test
	public void testCutPasteSingleNode()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		aList.set(aClassNode);
		clipboard.copy(aList);
		aPanel.setSelectionList(aList);
		
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		assertEquals(0, diagram.getRootNodes().size());
		clipboard.paste(aPanel);
		assertEquals(1, diagram.getRootNodes().size());
		assertTrue(0 == ((Node) diagram.getRootNodes().toArray()[0]).getBounds().getY());
		assertTrue(0 == ((Node) diagram.getRootNodes().toArray()[0]).getBounds().getX());
	}
	
	/**
	 * Testing copy and paste a combination of nodes and edge
	 */
	@Test
	public void testCopyPasteCombinationNodeAndEdge()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(45, 45));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.draw(aGraphics, new Grid());
		
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);
		diagram.draw(aGraphics, new Grid());
		assertEquals(4, diagram.getRootNodes().size());
		assertEquals(2, diagram.getEdges().size());
		
		Object[] nodes = diagram.getRootNodes().toArray();
		boolean trigger1 = false;
		boolean trigger2 = false;
		for(int i = 2; i < nodes.length; i++)
		{
			if(nodes[i] instanceof ClassNode)
			{
				trigger1 = true;
				assertTrue(0 == ((Node) nodes[i]).getBounds().getX());
				assertTrue(0 == ((Node) nodes[i]).getBounds().getY());
			}
			else
			{
				trigger2 = true;
			}
		}
		assertTrue(trigger1 && trigger2);
		
		aPanel.undo();
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
		
	}
	
	/**
	 * Testing cut and paste a combination of nodes and edge
	 */
	@Test
	public void testCutPasteCombinationNodeAndEdge()
	{
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(45, 45));
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.draw(aGraphics, new Grid());
		
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		
		clipboard.paste(aPanel);
		diagram.draw(aGraphics, new Grid());
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
		
		Object[] nodes = diagram.getRootNodes().toArray();
		boolean trigger1 = false;
		boolean trigger2 = false;
		for(int i = 0; i < nodes.length; i++)
		{
			if(nodes[i] instanceof ClassNode)
			{
				trigger1 = true;
				assertTrue(0 == ((Node) nodes[i]).getBounds().getX());
				assertTrue(0 == ((Node) nodes[i]).getBounds().getY());
			}
			else
			{
				trigger2 = true;
			}
		}
		assertTrue(trigger1 && trigger2);
		
		aPanel.undo();
		diagram.draw(aGraphics, aGrid);
		assertEquals(0, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());
		aPanel.undo();
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
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
		diagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		diagram.addNode(node1, new Point2D.Double(25, 25));
		diagram.addNode(node2, new Point2D.Double(30, 30));
		diagram.addNode(innerNode, new Point2D.Double(35, 45));
		
		assertEquals(1, diagram.getRootNodes().size());
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
		diagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		diagram.addNode(innerNode, new Point2D.Double(25, 25));
		diagram.addNode(node1, new Point2D.Double(26, 29));
		diagram.addNode(node2, new Point2D.Double(30, 31));
	
		assertEquals(1, diagram.getRootNodes().size());
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
		diagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		diagram.addNode(node1, new Point2D.Double(25, 25));
		diagram.addNode(node2, new Point2D.Double(30, 30));
		node2.translate(100, 0);
		diagram.addEdge(depeEdge, new Point2D.Double(26, 26), new Point2D.Double(131, 31));
	
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
	}
	
	/** 
	 * Testing cut and paste PackageNode
	 */
	@Test
	public void testCutNodesAndEdgesInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		diagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		diagram.addNode(node1, new Point2D.Double(25, 25));
		diagram.addNode(node2, new Point2D.Double(30, 30));
		node2.translate(100, 0);
		diagram.addEdge(depeEdge, new Point2D.Double(26, 26), new Point2D.Double(131, 31));
		
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		diagram.draw(aGraphics, aGrid);
		assertEquals(0, diagram.getRootNodes().size());
		assertEquals(0, diagram.getEdges().size());
		
		clipboard.paste(aPanel);
		diagram.draw(aGraphics, aGrid);
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(1, diagram.getEdges().size());
		assertTrue(5.0 >= (((Node) diagram.getRootNodes().toArray()[0]).getBounds().getX()));
		assertTrue(5.0 >= (((Node) diagram.getRootNodes().toArray()[0]).getBounds().getY()));
	}

	@Test
	public void testCopyNodesAndEdgesInsidePackageNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		diagram.addNode(aPackageNode, new Point2D.Double(20, 20));
		diagram.addNode(node1, new Point2D.Double(25, 25));
		diagram.addNode(node2, new Point2D.Double(30, 30));
		node2.translate(100, 0);
		diagram.addEdge(depeEdge, new Point2D.Double(26, 26), new Point2D.Double(131, 31));
		
		aPanel.selectAll();
		clipboard.copy(aPanel.getSelectionList());
		clipboard.paste(aPanel);
		diagram.draw(aGraphics, aGrid);
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals(2, diagram.getEdges().size());
		assertTrue(5.0 >= (((Node) diagram.getRootNodes().toArray()[1]).getBounds().getX()));
		assertTrue(5.0 >= (((Node) diagram.getRootNodes().toArray()[1]).getBounds().getY()));
	}
	
	
	
	
		
}

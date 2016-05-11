package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.UMLEditor;
import ca.mcgill.cs.stg.jetuml.commands.DeleteNodeCommand;
import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.EditorFrame;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * This class is to test the class Diagram.
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
	public void testBasicNodeEdgeCreation()
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
		
		diagram.addEdge(aggrEdge, new Point2D.Double(70, 70), new Point2D.Double(170, 170));
		diagram.addEdge(aggrEdge, new Point2D.Double(6, 7), new Point2D.Double(170, 170));
		assertEquals(0, diagram.getEdges().size());
		
		diagram.addEdge(aggrEdge, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(assoEdge, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(depeEdge, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		assertEquals(3, diagram.getEdges().size());
		diagram.addEdge(new AssociationEdge(), new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, diagram.getEdges().size());
		diagram.addEdge(geneEdge, new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, diagram.getEdges().size());

		// 1.2.2 now testing for adding edge to NoteNode
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();

		// 1.2.2.1 from node to noteNode
		diagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(209,162));
		assertEquals(4, diagram.getEdges().size());
		diagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(139,142));
		assertEquals(noteEdge1.getStart(), aClassNode);
		assertEquals(noteEdge1.getEnd(), aNoteNode);
		assertEquals(5, diagram.getEdges().size());

		// 1.2.2.2 from noteNode to Node
		diagram.addEdge(noteEdge2, new Point2D.Double(138, 140), new Point2D.Double(9,9));
		assertEquals(noteEdge2.getStart(), aNoteNode);
		assertEquals(noteEdge2.getEnd().getClass(), new PointNode().getClass());
		assertEquals(6, diagram.getEdges().size());

	}
	
	/**
	 * Node Movement
	 */
	@Test
	public void testNodeMovement()
	{
		// setting up, this could be done with @before
		ClassDiagramGraph diagram = new ClassDiagramGraph();
		ClassNode aClassNode = new ClassNode();
		InterfaceNode aInterfaceNode = new InterfaceNode();
		PackageNode aPackageNode = new PackageNode();
		NoteNode aNoteNode = new NoteNode();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		ClassRelationshipEdge edge1 = new AggregationEdge();
		ClassRelationshipEdge edge2 = new AssociationEdge();
		ClassRelationshipEdge edge3 = new DependencyEdge();
		ClassRelationshipEdge edge4 = new GeneralizationEdge();
		
		// 2.1 testing moving an individual node of any type
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
		
		// 2.2.1 testing moving a selection of nodes and edges 
		aClassNode.translate(-5, -5);
		aInterfaceNode.translate(-11, -19);
		aPackageNode.translate(-32, 42);
		aNoteNode.translate(5, -19);
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(edge2, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(edge3, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		
		SelectionList aList = new SelectionList();
		aList.add(aClassNode);
		aList.add(edge1);
		aList.add(aInterfaceNode);
		Rectangle2D edge1_old_bonds = edge1.getBounds();
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
		assertFalse(edge1_old_bonds.getY() == edge1.getBounds().getY());
		assertTrue(aClassNode == edge1.getStart());
		assertTrue(aInterfaceNode == edge1.getEnd());
		
		// 2.2.2 testing a node + self edge
		aList.clearSelection();
		assertEquals(0, aList.size());
		diagram = new ClassDiagramGraph();
		aClassNode = new ClassNode();
		edge1 = new AggregationEdge();
		diagram.addNode(aClassNode, new Point2D.Double(5, 7));
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(12, 9));
		assertEquals(1, diagram.getEdges().size());
		double old_y = edge1.getBounds().getY();
		double old_x = edge1.getBounds().getX();
		aList.add(aClassNode);
		aList.add(edge1);
		for(GraphElement element: aList)
		{
			if(element instanceof Node)
			{
				((Node) element).translate(10, 10);
			}
		}
		assertTrue(15 == aClassNode.getBounds().getX());
		assertTrue(17 == aClassNode.getBounds().getY());
		assertEquals(aClassNode, edge1.getStart());
		assertEquals(aClassNode, edge1.getEnd());
		assertTrue((10+old_x) == edge1.getBounds().getX());
		assertTrue((10+old_y) == edge1.getBounds().getY());
		
		// 2.3 move a node connected to another node
		diagram = new ClassDiagramGraph();
		aClassNode = new ClassNode();
		aInterfaceNode = new InterfaceNode();
		aPackageNode = new PackageNode();
		aNoteNode = new NoteNode();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		edge1 = new AggregationEdge();
		edge2 = new AssociationEdge();
		edge3 = new DependencyEdge();
		edge4 = new GeneralizationEdge();
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(edge2, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(edge3, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		
		double old_x_edge1 = edge1.getBounds().getX();
		double old_y_edge1 = edge1.getBounds().getY();
		double old_x_edge3 = edge1.getBounds().getX();
		double old_y_edge3 = edge1.getBounds().getY();
		
		aClassNode.translate(20, 20);
		assertEquals(aClassNode, edge1.getStart());
		assertEquals(aInterfaceNode, edge1.getEnd());
		assertFalse(old_x_edge1 == edge1.getBounds().getX());
		assertFalse(old_y_edge1 == edge1.getBounds().getY());
		
		aInterfaceNode.translate(-19, 45);
		assertEquals(aPackageNode, edge3.getStart());
		assertEquals(aInterfaceNode, edge3.getEnd());
		assertFalse(old_x_edge3 == edge3.getBounds().getX());
		assertFalse(old_y_edge3 == edge3.getBounds().getY());
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
		aList.clearSelection();
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
	
	
	
	@Test
	public void TestCopyAndPaste()
	{
		// set up
		
		ClassDiagramGraph diagram = new ClassDiagramGraph();
		ClassNode aClassNode = new ClassNode();
//		InterfaceNode aInterfaceNode = new InterfaceNode();
//		PackageNode aPackageNode = new PackageNode();
//		NoteNode aNoteNode = new NoteNode();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
//		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
//		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
//		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		// 4.1 Copy a signle node
		Clipboard clipboard = new Clipboard();
		SelectionList aList = new SelectionList();
		aList.add(aClassNode);
		clipboard.copy(aList);
		clipboard.paste(new GraphPanel(diagram, null));
		assertEquals(2, diagram.getRootNodes().size());
		assertTrue(0 == ((Node) diagram.getRootNodes().toArray()[1]).getBounds().getX());
		assertTrue(0 == ((Node) diagram.getRootNodes().toArray()[1]).getBounds().getY());
		
		
		
	}
	
	
	
		
}

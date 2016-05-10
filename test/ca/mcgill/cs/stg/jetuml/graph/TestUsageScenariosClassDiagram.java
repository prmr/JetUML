package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Test;


import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
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
	/**
	 * Basic Nodes and Edge Creation.
	 */
	@Test
	public void testBasicNodeEdgeCreation()
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
		
		// 1.2.1 adding and testing edges except NoteEdge 
		ClassRelationshipEdge edge1 = new AggregationEdge();
		ClassRelationshipEdge edge2 = new AssociationEdge();
		ClassRelationshipEdge edge3 = new DependencyEdge();
		ClassRelationshipEdge edge4 = new GeneralizationEdge();
		
		diagram.addEdge(edge1, new Point2D.Double(70, 70), new Point2D.Double(170, 170));
		diagram.addEdge(edge1, new Point2D.Double(6, 7), new Point2D.Double(170, 170));
		assertEquals(0, diagram.getEdges().size());
		
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		diagram.addEdge(edge2, new Point2D.Double(47, 49), new Point2D.Double(9, 17));
		diagram.addEdge(edge3, new Point2D.Double(90, 93), new Point2D.Double(44, 49));
		assertEquals(3, diagram.getEdges().size());
		diagram.addEdge(new AssociationEdge(), new Point2D.Double(47, 49), new Point2D.Double(50, 49));
		assertEquals(4, diagram.getEdges().size());
		diagram.addEdge(edge4, new Point2D.Double(47, 49), new Point2D.Double(50, 49));
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
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
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
		
		Rectangle2D updated_edge1_bond = edge1.getBounds();
		
		aInterfaceNode.translate(-19, 45);
		assertEquals(aPackageNode, edge3.getStart());
		assertEquals(aInterfaceNode, edge3.getEnd());
		assertEquals(aClassNode, edge1.getStart());
		assertEquals(aInterfaceNode, edge1.getEnd());
		assertFalse(old_x_edge3 == edge3.getBounds().getX());
		assertFalse(old_y_edge3 == edge3.getBounds().getY());
		assertFalse(updated_edge1_bond == edge1.getBounds());
	}
	
	/**
	 * Deletion and Undo.
	 */
	@Test
	public void TestDeletionAndUndo()
	{
		// 3.1 set up
		ClassDiagramGraph diagram = new ClassDiagramGraph();
		GraphPanel aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		Graphics2D aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		ClassNode aClassNode = new ClassNode();
		InterfaceNode aInterfaceNode = new InterfaceNode();
		PackageNode aPackageNode = new PackageNode();
		NoteNode aNoteNode = new NoteNode();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		// 3.1.1 testing deletion for sigle node
		aPanel.getSelectionList().add(aClassNode);
		Rectangle2D old_bound_classNode = aClassNode.getBounds();
		aPanel.removeSelected(); // mimic the selection process at GUI
		aPanel.getSelectionList().clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(3, diagram.getRootNodes().size());
		
		aPanel.getSelectionList().add(aNoteNode);
		Rectangle2D old_bound_noteNode = aNoteNode.getBounds();
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(2, diagram.getRootNodes().size());

		
		// 3.1.2 testing undo single node
		aPanel.undo();
		assertEquals(3, diagram.getRootNodes().size());
		assertEquals(old_bound_noteNode,((Node) diagram.getRootNodes().toArray()[2]).getBounds());

		aPanel.undo();
		assertEquals(4, diagram.getRootNodes().size());
		assertEquals(old_bound_classNode,((Node) diagram.getRootNodes().toArray()[3]).getBounds());
		
		
		// 3.2 set up 
		diagram = new ClassDiagramGraph();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();		
		aClassNode = new ClassNode();
		aInterfaceNode = new InterfaceNode();
		ClassRelationshipEdge edge1 = new AggregationEdge();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		
		// 3.2.1 testing removing edge
		aPanel.getSelectionList().add(edge1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertTrue(diagram.getEdges().isEmpty());
		
		// 3.2.2 testing undo removing for the edge
		aPanel.undo();
		assertEquals(1, diagram.getEdges().size());
		
		
		
		// 3.3 set up
		diagram = new ClassDiagramGraph();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();		
		aClassNode = new ClassNode();
		aInterfaceNode = new InterfaceNode();
		edge1 = new AggregationEdge();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addEdge(edge1, new Point2D.Double(8, 10), new Point2D.Double(45, 48));
		
		// 3.3.1.1 delete either node, the edge should also be deleted
		aPanel.getSelectionList().add(aInterfaceNode);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertTrue(diagram.getEdges().isEmpty());
		assertEquals(1, diagram.getRootNodes().size());
		
		// 3.3.1.2 undo the above instructions, edge should also be restored
		aPanel.undo();
		assertEquals(1, diagram.getEdges().size());
		assertEquals(2, diagram.getRootNodes().size());
		
		// 3.3.2.1 now adding more components and remove them all, set up
		diagram = new ClassDiagramGraph();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();		
		aClassNode = new ClassNode();
		aInterfaceNode = new InterfaceNode();
		aPackageNode = new PackageNode();
		aNoteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		diagram.addEdge(edge1, new Point2D.Double(88, 89), new Point2D.Double(45, 48));
		diagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(139,142));
		// now total of 4 nodes and 2 edges
		int totalNodes = diagram.getRootNodes().size();
		int totalEdges = diagram.getEdges().size();
		
		// 3.3.2.1 testing deletion
		aPanel.getSelectionList().clearSelection();
		aPanel.selectAll();
		aPanel.removeSelected();
		diagram.draw(aGraphics, new Grid());
		aPanel.getSelectionList().clearSelection();
		assertTrue(diagram.getEdges().isEmpty());
		assertTrue(diagram.getRootNodes().isEmpty());
		
		// 3.3.2.2 testing undo 
		aPanel.undo();
		assertEquals(totalNodes, diagram.getRootNodes().size());
		assertEquals(totalEdges, diagram.getEdges().size());
		
		
		// 3.4 set up
		diagram = new ClassDiagramGraph();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		ClassNode classNode1 = new ClassNode();
		ClassNode classNode2 = new ClassNode();
		aPackageNode = new PackageNode();
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		aPackageNode.addChild(classNode1);
		aPackageNode.addChild(classNode2);
		
		// 3.4.1 test deleteion 
		aPanel.getSelectionList().add(classNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(1, aPackageNode.getChildren().size());
		
		// 3.4.2 testing undo
		aPanel.undo();
		assertEquals(2, aPackageNode.getChildren().size());
		assertEquals(1, diagram.getRootNodes().size());
		
		// 3.5 set up
		diagram = new ClassDiagramGraph();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		PackageNode packageNode1 = new PackageNode();
		PackageNode packageNode2 = new PackageNode();
		diagram.addNode(packageNode1, new Point2D.Double(87, 87));
		packageNode1.addChild(packageNode2);
		System.out.println(packageNode1.getChildren().size());
		
		// 3.5.1 testing deletion
		aPanel.getSelectionList().add(packageNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		diagram.draw(aGraphics, new Grid());
		assertEquals(0, diagram.getRootNodes().size());
		
		// 3.5.2 testing undo
		aPanel.undo();
		assertEquals(1, diagram.getRootNodes().size());
		assertEquals(1, ((PackageNode) diagram.getRootNodes().toArray()[0]).getChildren().size());
	}
	
	
	
	/**
	 * TODO
	 */
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

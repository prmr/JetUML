package classDiagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
import ca.mcgill.cs.stg.jetuml.graph.*;

/**
 * This class is to test the class Diagram.
 * 
 * @author jchen157
 *
 */

public class TestingClassDiagram 
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
	 * Deletion and Undo.
	 */
	@Test
	public void TestDeletionAndUndo()
	{
		
		ClassDiagramGraph diagram = new ClassDiagramGraph();
		ClassNode aClassNode = new ClassNode();
		InterfaceNode aInterfaceNode = new InterfaceNode();
		PackageNode aPackageNode = new PackageNode();
		NoteNode aNoteNode = new NoteNode();
		diagram.addNode(aClassNode, new Point2D.Double(5, 5));
		diagram.addNode(aInterfaceNode, new Point2D.Double(44, 44));
		diagram.addNode(aPackageNode, new Point2D.Double(87, 87));
		diagram.addNode(aNoteNode, new Point2D.Double(134, 132));
		
		DeleteNodeCommand deletionCommand1 = new DeleteNodeCommand(diagram, aClassNode);
		DeleteNodeCommand deletionCommand2 = new DeleteNodeCommand(diagram, aNoteNode);
		
		Rectangle2D old_bound_classNode = aClassNode.getBounds();
		Rectangle2D old_bound_noteNode = aNoteNode.getBounds();
		
		// deletion command
		deletionCommand1.execute();
//		deletionCommand2.execute();
//		System.out.println(diagram.getRootNodes().size());
//
//		assertTrue(2 == diagram.getRootNodes().size());
		// undo them
		deletionCommand1.undo();
		deletionCommand2.undo();
		System.out.println(diagram.getRootNodes().size());

		assertEquals(4+2, diagram.getRootNodes().size());
		for(Node aNode: diagram.getRootNodes())
		{
			if(aNode instanceof ClassNode)
			{
				assertEquals(old_bound_classNode, ((ClassNode) aNode).getBounds());
			}
		}		
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

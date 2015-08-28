package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;

public class TestClipboard
{
	private Clipboard aClipboard;
	private PackageNode aPackage1;
	private PackageNode aPackage2;
	private ClassNode aClass1;
	private ClassNode aClass2;
	private ClassRelationshipEdge aEdge1;
	private ClassRelationshipEdge aEdge2;
	private SelectionList aSelectionList;
	private ClassDiagramGraph aClassDiagramGraph;
	
	@Before
	public void setup()
	{
		aClipboard = new Clipboard();
		aSelectionList = new SelectionList();
		aClass1 = new ClassNode();
		MultiLineString c1 = new MultiLineString();
		c1.setText("c1");
		aClass1.setName(c1);
		aClass2 = new ClassNode();
		MultiLineString c2 = new MultiLineString();
		c2.setText("c2");
		aClass2.setName(c2);
		aEdge1 = new ClassRelationshipEdge();
		aEdge1.setMiddleLabel("e1");
		aEdge2 = new ClassRelationshipEdge();
		aEdge2.setMiddleLabel("e2");
		
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		
		aClassDiagramGraph = new ClassDiagramGraph();
	}
	
	@Test
	public void testCopySingleNode()
	{
		aSelectionList.add(aClass1);
		aClipboard.copy(aSelectionList);
		assertEquals(1, aClipboard.getNodes().size());
		assertFalse(aClipboard.getNodes().contains(aClass1));
		assertEquals("c1", ((ClassNode)aClipboard.getNodes().iterator().next()).getName().toString());
		assertEquals(0, aClipboard.getEdges().size());
		aSelectionList.clearSelection();
		aSelectionList.set(aClass2);
		aClipboard.copy(aSelectionList);
		assertEquals(1, aClipboard.getNodes().size());
		assertFalse(aClipboard.getNodes().contains(aClass2));
		assertEquals("c2", ((ClassNode)aClipboard.getNodes().iterator().next()).getName().toString());
		assertEquals(0, aClipboard.getEdges().size());
	}
	
	@Test
	public void testCopyDanglingEdge()
	{
		aEdge1.connect(aClass1, aClass2);
		aSelectionList.add(aClass1);
		aSelectionList.add(aEdge1);
		aClipboard.copy(aSelectionList);
		assertEquals(1, aClipboard.getNodes().size());
		assertEquals("c1", ((ClassNode)aClipboard.getNodes().iterator().next()).getName().toString());
		assertEquals(0, aClipboard.getEdges().size());
	}
	
	@Test
	public void testCopyCapturedEdgeTopLevel()
	{
		aEdge1.connect(aClass1, aClass2);
		aSelectionList.add(aClass1);
		aSelectionList.add(aEdge1);
		aSelectionList.add(aClass2);
		aClipboard.copy(aSelectionList);
		assertEquals(2, aClipboard.getNodes().size());
		assertEquals("c1", ((ClassNode)aClipboard.getNodes().iterator().next()).getName().toString());
		Iterator<Node> nodes = aClipboard.getNodes().iterator(); nodes.next();
		assertEquals("c2", ((ClassNode)nodes.next()).getName().toString());
		assertEquals(1, aClipboard.getEdges().size());
		assertEquals("e1", ((ClassRelationshipEdge)aClipboard.getEdges().iterator().next()).getMiddleLabel());
		assertFalse( aEdge1 == aClipboard.getEdges().iterator().next());
	}
	
	@Test
	public void testCopyDeepEdgeReassignment()
	{
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aClass1);
		aPackage2.addChild(aClass2);
		aEdge1.connect(aClass1, aClass2);
		aEdge2.connect(aClass2, aClass1);
		aSelectionList.add(aPackage1);
		aSelectionList.add(aEdge1);
		aSelectionList.add(aEdge2);
		aClipboard.copy(aSelectionList);
		assertEquals(1, aClipboard.getNodes().size());
		PackageNode p1Clone = (PackageNode)aClipboard.getNodes().iterator().next();
		assertFalse( p1Clone == aPackage1);
		List<ChildNode> children = p1Clone.getChildren();
		assertEquals(1, children.size());
		PackageNode p2Clone = (PackageNode) children.get(0);
		assertFalse( p2Clone == aPackage2);
		List<ChildNode> children2 = p2Clone.getChildren();
		assertEquals(2, children2.size());
		ClassNode cc1 = (ClassNode) children2.get(0);
		ClassNode cc2 = (ClassNode) children2.get(1);
		assertEquals("c1", cc1.getName().toString());
		assertEquals("c2", cc2.getName().toString());
		assertEquals(2, aClipboard.getEdges().size());
		Iterator<Edge> edgesIt = aClipboard.getEdges().iterator();
		ClassRelationshipEdge clonedE1 = (ClassRelationshipEdge)edgesIt.next();
		ClassRelationshipEdge clonedE2 = (ClassRelationshipEdge)edgesIt.next();
		assertEquals("e1", clonedE1.getMiddleLabel());
		assertEquals("e2", clonedE2.getMiddleLabel());
		assertEquals(cc1, clonedE1.getStart());
		assertEquals(cc2, clonedE1.getEnd());
		assertEquals(cc2, clonedE2.getStart());
		assertEquals(cc1, clonedE2.getEnd());
	}
	
	@Test
	public void testPasteSingleNode()
	{
		aSelectionList.add(aClass1);
		aClipboard.copy(aSelectionList);
		SelectionList list = aClipboard.paste(aClassDiagramGraph);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(1, rootNodes.size());
		ClassNode node = (ClassNode)rootNodes.iterator().next();
		assertEquals("c1", node.getName().toString());
		assertEquals(1, list.size());
		assertTrue(list.iterator().next() == node);
	}
	
	@Test
	public void testInvalidPaste()
	{
		aSelectionList.add(new ImplicitParameterNode());
		aClipboard.copy(aSelectionList);
		SelectionList list = aClipboard.paste(aClassDiagramGraph);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(0, rootNodes.size());
		assertEquals(0, list.size());
	}
}

/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.edges.CallEdge;
import ca.mcgill.cs.jetuml.graph.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.jetuml.graph.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.graph.nodes.PackageNode;
import ca.mcgill.cs.jetuml.gui.GraphPanel;
import ca.mcgill.cs.jetuml.gui.DiagramFrameToolBar;
import javafx.geometry.Rectangle2D;

public class TestClipboard
{
	private Clipboard aClipboard;
	private PackageNode aPackage1;
	private PackageNode aPackage2;
	private ClassNode aClass1;
	private ClassNode aClass2;
	private DependencyEdge aEdge1;
	private DependencyEdge aEdge2;
	private SelectionList aSelectionList;
	private ClassDiagramGraph aClassDiagramGraph;
	private GraphPanel aPanel;
	
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
		aClipboard = Clipboard.instance();
		aSelectionList = new SelectionList();
		aClass1 = new ClassNode();
		aClass1.setName("c1");
		aClass2 = new ClassNode();
		aClass2.setName("c2");
		aEdge1 = new DependencyEdge();
		aEdge1.setMiddleLabel("e1");
		aEdge2 = new DependencyEdge();
		aEdge2.setMiddleLabel("e2");
		
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		
		aClassDiagramGraph = new ClassDiagramGraph();
		aPanel = new GraphPanel(aClassDiagramGraph, new DiagramFrameToolBar(aClassDiagramGraph), new Rectangle2D(0, 0, 0, 0));
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
		aEdge1.connect(aClass1, aClass2, aClassDiagramGraph);
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
		aEdge1.connect(aClass1, aClass2, aClassDiagramGraph);
		aSelectionList.add(aClass1);
		aSelectionList.add(aEdge1);
		aSelectionList.add(aClass2);
		aClipboard.copy(aSelectionList);
		assertEquals(2, aClipboard.getNodes().size());
		assertEquals("c1", ((ClassNode)aClipboard.getNodes().iterator().next()).getName().toString());
		Iterator<Node> nodes = aClipboard.getNodes().iterator(); nodes.next();
		assertEquals("c2", ((ClassNode)nodes.next()).getName().toString());
		assertEquals(1, aClipboard.getEdges().size());
		assertEquals("e1", ((DependencyEdge)aClipboard.getEdges().iterator().next()).getMiddleLabel());
		assertFalse( aEdge1 == aClipboard.getEdges().iterator().next());
	}
	
	@Test
	public void testCopyDeepEdgeReassignment()
	{
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aClass1);
		aPackage2.addChild(aClass2);
		aEdge1.connect(aClass1, aClass2, aClassDiagramGraph);
		aEdge2.connect(aClass2, aClass1, aClassDiagramGraph);
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
		DependencyEdge clonedE1 = (DependencyEdge)edgesIt.next();
		DependencyEdge clonedE2 = (DependencyEdge)edgesIt.next();
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
		SelectionList list = aClipboard.paste(aPanel);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(1, rootNodes.size());
		ClassNode node = (ClassNode)rootNodes.iterator().next();
		assertEquals("c1", node.getName().toString());
		assertEquals(1, list.size());
		assertTrue(list.iterator().next() == node);
	}
	
	@Test
	public void testPasteNodeAndEdgesNoContainment()
	{
		aEdge1.connect(aClass1, aClass2, aClassDiagramGraph);
		aSelectionList.add(aClass1);
		aSelectionList.add(aClass2);
		aSelectionList.add(aEdge1);
		aClipboard.copy(aSelectionList);
		assertEquals(2, aClipboard.getNodes().size());
		assertEquals(1, aClipboard.getEdges().size());
		SelectionList list = aClipboard.paste(aPanel);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(2, rootNodes.size());
		ClassNode node = (ClassNode)rootNodes.iterator().next();
		assertEquals("c1", node.getName().toString());
		assertEquals(3, list.size());
		Collection<Edge> edges = aClassDiagramGraph.getEdges();
		assertEquals(1, edges.size());
		assertEquals("e1", ((DependencyEdge)edges.iterator().next()).getMiddleLabel());
	}
	
	@Test
	public void testPasteNodeAndEdgesWithContainment()
	{
		aEdge1.connect(aClass1, aClass2, aClassDiagramGraph);
		aPackage1.addChild(aClass1);
		aPackage1.addChild(aClass2);
		aSelectionList.add(aPackage1);
		aSelectionList.add(aEdge1);
		aClipboard.copy(aSelectionList);
		assertEquals(1, aClipboard.getNodes().size());
		assertEquals(1, aClipboard.getEdges().size());
		aClipboard.paste(aPanel);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(1, rootNodes.size());
		PackageNode packageNode = (PackageNode)rootNodes.iterator().next();
		ClassNode class1Clone = (ClassNode)packageNode.getChildren().get(0);
		assertEquals("c1", class1Clone.getName().toString());
		ClassNode class2Clone = (ClassNode)packageNode.getChildren().get(1);
		assertEquals("c2", class2Clone.getName().toString());
		Collection<Edge> edges = aClassDiagramGraph.getEdges();
		DependencyEdge edge1Clone = (DependencyEdge)edges.iterator().next();
		assertEquals(class1Clone, edge1Clone.getStart());
		assertEquals(class2Clone, edge1Clone.getEnd());
	}
	
	@Test
	public void testPasteNodeWithMissingParent()
	{
		aEdge1.connect(aClass1, aClass2, aClassDiagramGraph);
		aPackage1.addChild(aClass1);
		aPackage1.addChild(aClass2);
		aSelectionList.add(aClass1);
		aSelectionList.add(aClass2);
		aClipboard.copy(aSelectionList);
		assertEquals(2, aClipboard.getNodes().size());
		assertEquals(0, aClipboard.getEdges().size());
		aClipboard.paste(aPanel);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(2, rootNodes.size());
		Iterator<Node> it = rootNodes.iterator();
		ClassNode class1Clone = ((ClassNode)it.next());
		ClassNode class2Clone = ((ClassNode)it.next());
		assertEquals("c1", class1Clone.getName().toString());
		assertEquals("c2", class2Clone.getName().toString());
		Collection<Edge> edges = aClassDiagramGraph.getEdges();
		assertEquals(0, edges.size());
	}
		
	@Test
	public void testInvalidPasteWithNodes()
	{
		aSelectionList.add(new ImplicitParameterNode());
		aClipboard.copy(aSelectionList);
		SelectionList list = aClipboard.paste(aPanel);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(0, rootNodes.size());
		assertEquals(0, list.size());
	}
	
	@Test
	public void testInvalidPasteWithEdges()
	{
		aSelectionList.add(aClass1);
		aSelectionList.add(aClass2);
		CallEdge edge = new CallEdge();
		edge.connect(aClass1, aClass2, aClassDiagramGraph);
		aSelectionList.add(edge);
		aClipboard.copy(aSelectionList);
		assertEquals(1,aClipboard.getEdges().size());
		assertEquals(2,aClipboard.getNodes().size());
		SelectionList list = aClipboard.paste(aPanel);
		Collection<Node> rootNodes = aClassDiagramGraph.getRootNodes();
		assertEquals(0, rootNodes.size());
		assertEquals(0, list.size());
	}
}

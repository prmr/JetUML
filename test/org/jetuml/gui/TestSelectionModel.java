/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.RenderingFacade;
import org.junit.jupiter.api.Test;

public class TestSelectionModel
{
	private Edge aEdge1 = new DependencyEdge(); 
	private Edge aEdge2 = new NoteEdge();
	private ClassNode aNode1 = new ClassNode();
	private ClassNode aNode2 = new ClassNode();
	private PackageNode aPackage1 = new PackageNode();
	private PackageNode aPackage2 = new PackageNode();; 
	private Diagram aClassDiagram = new Diagram(DiagramType.CLASS);
	private SelectionModel aModel = new SelectionModel( () -> {});
	
	private int size()
	{
		int size = 0;
		for( @SuppressWarnings("unused") DiagramElement element : aModel )
		{
			size++;
		}
		return size;
	}
	
	private void assertContent(DiagramElement... pElements)
	{
		Iterator<DiagramElement> iterator = aModel.iterator();
		for( DiagramElement element : pElements)
		{
			assertTrue(iterator.hasNext());
			assertEquals(element, iterator.next());
		}
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void testEmpty()
	{
		aModel.clearSelection();
		assertFalse(aModel.getLastSelected().isPresent());
		aModel.removeFromSelection(aEdge1);
		assertFalse(aModel.iterator().hasNext());
		assertTrue(aModel.isEmpty());
		assertFalse(aModel.contains(aEdge1));
	}
	
	@Test
	void testRubberband()
	{
		assertFalse(aModel.getRubberband().isPresent());
		aModel.activateRubberband(new Line(new Point(0,0), new Point(10,10)));
		assertTrue(aModel.getRubberband().isPresent());
		
		Line result = aModel.getRubberband().get();
		assertEquals(new Point(0, 0), result.getPoint1());
		assertEquals(new Point(10, 10), result.getPoint2());
		
		aModel.activateRubberband(new Line(new Point(0,0), new Point(20,30)));
		assertTrue(aModel.getRubberband().isPresent());
		
		result = aModel.getRubberband().get();
		assertEquals(new Point(0, 0), result.getPoint1());
		assertEquals(new Point(20, 30), result.getPoint2());

		aModel.deactivateRubberband();
		assertFalse(aModel.getRubberband().isPresent());
		aModel.deactivateRubberband();
		assertFalse(aModel.getRubberband().isPresent());
	}
	
	@Test
	void testActivateLassoOneNode()
	{
		aClassDiagram.addRootNode(aNode1);
		aNode1.translate(10, 10);
		aModel.activateLasso(new Rectangle(5,5,5,5), aClassDiagram);
		assertTrue(aModel.getLasso().isPresent());
		assertEquals(new Rectangle(5,5,5,5), aModel.getLasso().get());
		assertContent();
		aModel.activateLasso(new Rectangle(0,0,200,200), aClassDiagram);
		assertEquals(new Rectangle(0,0,200,200), aModel.getLasso().get());
		assertContent(aNode1);
		aModel.deactivateLasso();
		assertFalse(aModel.getLasso().isPresent());
	}
	
	@Test 
	void testActivateLassoNodesAndEdges1()
	{
		// aNode1: [x=0, y=0, w=100, h=60]
		// aNode2: [x=200, y=0, w=100, h=60]
		// aEdge1: [x=99, y=23, w=102, h=12]
		aClassDiagram.addRootNode(aNode1);
		aClassDiagram.addRootNode(aNode2);
		aEdge1.connect(aNode1, aNode2, aClassDiagram);
		aClassDiagram.addEdge(aEdge1);
		aNode2.translate(200, 0);
		
		RenderingFacade.prepareFor(aClassDiagram);
		RenderingFacade.getBounds(aClassDiagram);
		
		// Just Node 1
		aModel.activateLasso(new Rectangle(0,0,105,61), aClassDiagram);
		assertContent(aNode1);
		// Just Node 2
		aModel.clearSelection();
		aModel.activateLasso(new Rectangle(199,0,105,61), aClassDiagram);
		assertContent(aNode2);
		// Just Edge 1
		aModel.clearSelection();
		aModel.activateLasso(new Rectangle(97,20,110,20), aClassDiagram);
		assertContent(aEdge1);
		// Node 1 and edge
		aModel.clearSelection();
		aModel.activateLasso(new Rectangle(0,0,230,63), aClassDiagram);
		assertContent(aNode1, aEdge1);
		// Node 2 and edge
		aModel.clearSelection();
		aModel.activateLasso(new Rectangle(5,0,330,70), aClassDiagram);
		assertContent(aNode2, aEdge1);
		// Everything
		aModel.clearSelection();
		aModel.activateLasso(new Rectangle(0,0,330,70), aClassDiagram);
		assertContent(aNode1, aNode2, aEdge1);
	}
	
	
	@Test 
	void testActivateLassoNodesAndEdgesAddMode()
	{
		// aNode1: [x=0, y=0, w=100, h=60]
		// aNode2: [x=200, y=0, w=100, h=60]
		// aEdge1: [x=99, y=23, w=102, h=12]
		aClassDiagram.addRootNode(aNode1);
		aClassDiagram.addRootNode(aNode2);
		ClassNode extraNode = new ClassNode();
		extraNode.translate(1000, 1000);
		aClassDiagram.addRootNode(extraNode);
		aEdge1.connect(aNode1, aNode2, aClassDiagram);
		aClassDiagram.addEdge(aEdge1);
		aNode2.translate(200, 0);
		
		RenderingFacade.prepareFor(aClassDiagram);
		RenderingFacade.getBounds(aClassDiagram);
		
		aModel.set(extraNode);
		// Just Node 1
		aModel.activateLasso(new Rectangle(0,0,105,61), aClassDiagram);
		assertContent(extraNode, aNode1);
		// Just Node 2
		aModel.set(extraNode);
		aModel.activateLasso(new Rectangle(199,0,105,61), aClassDiagram);
		assertContent(extraNode, aNode2);
		// Just Edge 1
		aModel.set(extraNode);
		aModel.activateLasso(new Rectangle(97,20,110,20), aClassDiagram);
		assertContent(extraNode, aEdge1);
		// Node 1 and edge
		aModel.set(extraNode);
		aModel.activateLasso(new Rectangle(0,0,230,63), aClassDiagram);
		assertContent(extraNode, aNode1, aEdge1);
		// Node 2 and edge
		aModel.set(extraNode);
		aModel.activateLasso(new Rectangle(5,0,330,70), aClassDiagram);
		assertContent(extraNode, aNode2, aEdge1);
		// Everything
		aModel.set(extraNode);
		aModel.activateLasso(new Rectangle(0,0,330,70), aClassDiagram);
		assertContent(extraNode, aNode1, aNode2, aEdge1);
	}
	
	@Test
	void testActivateLassoParentNotAddMode() throws Exception
	{
		// Selects three nodes, one of which is a parent of another.
		aPackage1.addChild(aNode1);
		aClassDiagram.addRootNode(aPackage1);
		aClassDiagram.addRootNode(aNode2);
		aPackage1.translate(100, 100);
		aNode2.translate(300, 0);
		aModel.activateLasso(new Rectangle(0,0,400,200), aClassDiagram);
		assertContent(aPackage1, aNode2);
	}
	
	@Test
	void testActivateLassoParentAddMode() throws Exception
	{
		// Selects three nodes, one of which is a parent of another.
		aPackage1.addChild(aNode1);
		aClassDiagram.addRootNode(aPackage1);
		aClassDiagram.addRootNode(aNode2);
		aPackage1.translate(100, 100);
		aNode2.translate(300, 0);
		ClassNode extraNode = new ClassNode();
		extraNode.translate(1000, 1000);
		aClassDiagram.addRootNode(extraNode);
		aModel.set(extraNode);
		aModel.activateLasso(new Rectangle(0,0,400,200), aClassDiagram);
		assertContent(extraNode, aPackage1, aNode2);
	}
	
	@Test
	void testActivateLassoChildInParent() throws Exception
	{
		// Selects three nodes, one of which is a parent of another.
		aPackage1.addChild(aNode1);
		aClassDiagram.addRootNode(aPackage1);
		aPackage1.translate(100, 100);
		aModel.activateLasso(new Rectangle(99,99,102,102), aClassDiagram);
		assertContent(aNode1);
	}
	
	
	@Test
	void testSetSelectionToEmpty()
	{
		List<DiagramElement> list = new ArrayList<>();
		aModel.setSelectionTo(list);
		assertContent();
	}
	
	@Test
	void testSetSelectionToNodesOnly()
	{
		List<DiagramElement> list = new ArrayList<>();
		list.add(aNode1);
		list.add(aNode2);
		aModel.setSelectionTo(list);
		assertContent(aNode1, aNode2);
	}
	
	@Test
	void testSetSelectionToNodesAndEdges()
	{
		List<DiagramElement> list = new ArrayList<>();
		list.add(aNode1);
		list.add(aNode2);
		aEdge1.connect(aNode1, aNode2, aClassDiagram);
		aClassDiagram.addEdge(aEdge1);
		list.add(aEdge1);
		aModel.setSelectionTo(list);
		assertContent(aNode1, aNode2, aEdge1);
	}
	
	@Test
	void testSetSelectionWithContainment()
	{
		List<DiagramElement> list = new ArrayList<>();
		list.add(aNode1);
		list.add(aNode2);
		list.add(aPackage1);
		aPackage1.addChild(aNode1);
		aPackage1.addChild(aNode2);
		aModel.setSelectionTo(list);
		assertContent(aPackage1);
	}
	
	@Test
	void testSetSelectionTwice()
	{
		List<DiagramElement> list = new ArrayList<>();
		list.add(aNode1);
		aModel.setSelectionTo(list);
		assertContent(aNode1);
		list.clear();
		list.add(aNode2);
		aModel.setSelectionTo(list);
		assertContent(aNode2);
	}
	
	@Test
	void testSelectAllRootNodesOnly()
	{
		aClassDiagram.addRootNode(aNode1);
		aClassDiagram.addRootNode(aNode2);
		aModel.selectAll(aClassDiagram);
		assertEquals(2, size());
		assertContent(aNode1, aNode2);
	}
	
	@Test
	void testSelectAllRootAndChildNode()
	{
		aPackage1.addChild(aNode1);
		aClassDiagram.addRootNode(aPackage1);
		aModel.selectAll(aClassDiagram);
		assertEquals(1, size());
		assertContent(aPackage1);
	}
	
	@Test
	void testSelectRootNodesAndEdges()
	{
		aClassDiagram.addRootNode(aNode1);
		aClassDiagram.addRootNode(aNode2);
		aEdge1.connect(aNode1, aNode2, aClassDiagram);
		aClassDiagram.addEdge(aEdge1);
		aModel.selectAll(aClassDiagram);
		assertEquals(3, size());
		assertContent(aNode1, aNode2, aEdge1);
	}
	
	@Test
	void testGetSelectedNodesEmpty()
	{
		assertFalse(aModel.getSelectedNodes().iterator().hasNext());
	}
	
	@Test
	void testGetSelectedNodesJustNodes()
	{
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aNode2);
		Iterator<Node> iterator = aModel.getSelectedNodes().iterator();
		assertSame(aNode1, iterator.next());
		assertSame(aNode2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void testGetSelectedNodesJustEdges()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		assertFalse(aModel.getSelectedNodes().iterator().hasNext());
	}
	
	@Test
	void testGetSelectedNodesNodesAndEdges()
	{
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aNode2);
		Iterator<Node> iterator = aModel.getSelectedNodes().iterator();
		assertSame(aNode1, iterator.next());
		assertSame(aNode2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void testGetSelectedEdgesAndNodes()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aEdge2);
		Iterator<Node> iterator = aModel.getSelectedNodes().iterator();
		assertSame(aNode1, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void testContains()
	{
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aEdge1);
		assertTrue(aModel.contains(aNode1));
		assertTrue(aModel.contains(aEdge1));
		assertFalse(aModel.contains(aEdge2));
	}
	
	@Test
	void testAdd()
	{
		aModel.addToSelection(aEdge1);
		assertEquals(1, size());
		assertEquals(aEdge1, aModel.getLastSelected().get());
		aModel.addToSelection(aNode1);
		assertEquals(2, size());
		assertEquals(aNode1, aModel.getLastSelected().get());
		aModel.addToSelection(aEdge1);
		assertEquals(2, size());
		assertEquals(aEdge1, aModel.getLastSelected().get());
	}
	
	@Test
	void testAddParentContained()
	{
		aModel.addToSelection(aEdge1);
		aPackage1.addChild(aNode1);
		aModel.addToSelection(aPackage1);
		aModel.addToSelection(aNode1);
		assertEquals(2, size());
		assertTrue(aModel.contains(aEdge1));
		assertTrue(aModel.contains(aPackage1));
	}
	
	@Test
	void testAddChildrenContained()
	{
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aNode1);
		aPackage2.addChild(aNode2);
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aNode2);
		assertEquals(2, size());
		assertTrue(aModel.contains(aNode1));
		assertTrue(aModel.contains(aNode2));
		aModel.addToSelection(aPackage1);
		assertEquals(1, size());
		assertTrue(aModel.contains(aPackage1));
	}
	
	@Test
	void testGetLastSelected()
	{
		assertFalse(aModel.getLastSelected().isPresent());
		aModel.addToSelection(aEdge1);
		assertEquals(aEdge1, aModel.getLastSelected().get());
		aModel.addToSelection(aEdge2);
		assertEquals(aEdge2, aModel.getLastSelected().get());
		aModel.addToSelection(aEdge1);
		assertEquals(aEdge1, aModel.getLastSelected().get());
	}
	
	@Test
	void testClearSelection()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.addToSelection(aNode1);
		aModel.clearSelection();
		assertEquals(0, size());
		assertFalse(aModel.getLastSelected().isPresent());
	}
	
	@Test
	void testRemove()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.addToSelection(aNode1);
		aModel.removeFromSelection(aEdge1);
		assertEquals(2,  size());
		Iterator<DiagramElement> iterator = aModel.iterator();
		assertEquals(aEdge2, iterator.next());
		assertEquals(aNode1, iterator.next());
		assertEquals(aNode1, aModel.getLastSelected().get());
	}
	
	@Test
	void testSet()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.set(aNode1);
		assertEquals(1, size());
		assertEquals(aNode1, aModel.getLastSelected().get());
		aModel.clearSelection();
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.set(aEdge1);
		assertEquals(1, size());
		assertEquals(aEdge1, aModel.getLastSelected().get());
	}
}

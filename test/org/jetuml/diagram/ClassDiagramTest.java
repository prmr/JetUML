/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.junit.jupiter.api.Test;

/*
 * This class is used to test the methods of the abstract
 * class as well.
 */
class ClassDiagramTest {

	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private DiagramAccessor aDiagramAccessor = new DiagramAccessor(aDiagram);
	private PackageNode aPackageNode1 = new PackageNode();
	private ClassNode aClassNode1 = new ClassNode();
	private ClassNode aClassNode2 = new ClassNode();
	private ClassNode aClassNode3 = new ClassNode();
	private DependencyEdge aEdge1 = new DependencyEdge();
	private DependencyEdge aEdge2 = new DependencyEdge();
	private DependencyEdge aEdge3 = new DependencyEdge();

	@Test
	void testInit() {
		assertEquals(0, aDiagramAccessor.edges().size());
		assertEquals(0, aDiagramAccessor.rootNodes().size());
	}
	
	@Test
	void testDuplicate_empty() {
		assertEquals(0, aDiagram.duplicate().edges().size());
		assertEquals(0, aDiagram.duplicate().rootNodes().size());
	}
	
	@Test
	void testDuplicate_NoEdges() {
		aDiagram.addRootNode(new ClassNode());
		Diagram copy = aDiagram.duplicate();
		assertEquals(0, aDiagram.duplicate().edges().size());
		assertEquals(1, copy.rootNodes().size());
		Node node = copy.rootNodes().get(0);
		assertNotSame(aDiagram.rootNodes().get(0), node);
	}
	
	@Test
	void testDuplicate_oneEdge() {
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2);
		aDiagram.addEdge(edge);
		Diagram copy = aDiagram.duplicate();
		assertNotSame(aDiagram.rootNodes().get(0), copy.rootNodes().get(0));
		assertNotSame(aDiagram.rootNodes().get(1), copy.rootNodes().get(1));
		assertNotSame(aDiagram.edges().get(0), copy.edges().get(0));
		assertSame(copy.rootNodes().get(0), copy.edges().get(0).start());
		assertSame(copy.rootNodes().get(1), copy.edges().get(0).end());
	}
	
	@Test
	void testDuplicate_DiagramReassignmentInNodes() {
		aDiagram.addRootNode(new ClassNode());
		Diagram copy = aDiagram.duplicate();
		assertEquals(0, copy.edges().size());
		assertEquals(1, copy.rootNodes().size());
	}
	
	@Test
	void testDuplicate_edgeInnerNodeToInnerNode() {
		PackageNode p1 = new PackageNode();
		PackageNode p2 = new PackageNode();
		ClassNode n1 = new ClassNode();
		ClassNode n2 = new ClassNode();
		p1.setName("p1");
		p2.setName("p2");
		n1.setName("n1");
		n2.setName("n2");
		DependencyEdge edge = new DependencyEdge();
		aDiagram.addRootNode(p1);
		aDiagram.addRootNode(p2);
		p1.addChild(n1);
		p2.addChild(n2);
		edge.connect(n1, n2);
		aDiagram.addEdge(edge);
		Diagram copy = aDiagram.duplicate();
		PackageNode p1Copy = (PackageNode) copy.rootNodes().get(0);
		PackageNode p2Copy = (PackageNode) copy.rootNodes().get(1);
		assertNotSame(p1, p1Copy);
		assertNotSame(p2, p2Copy);
		assertEquals("p1", p1Copy.getName());
		assertEquals("p2", p2Copy.getName());
		ClassNode n1Copy = (ClassNode) p1Copy.getChildren().get(0);
		ClassNode n2Copy = (ClassNode) p2Copy.getChildren().get(0);
		assertNotSame(n1, n1Copy);
		assertNotSame(n2, n2Copy);
		assertEquals("n1", n1Copy.getName());
		assertEquals("n2", n2Copy.getName());
		DependencyEdge edgeCopy = (DependencyEdge) copy.edges().get(0);
		assertNotSame(edge, edgeCopy);
		assertSame(n1Copy, edgeCopy.start());
		assertSame(n2Copy, edgeCopy.end());
	}

	@Test
	void testNumberOfEdgesEmpty() {
		assertEquals(0, aDiagram.edges().size());
	}

	@Test
	void testNumberOfEdgesNotEmpty() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(aEdge1);
		aEdge2.connect(aClassNode1, aClassNode2);
		aDiagram.addEdge(aEdge2);
		assertEquals(2, aDiagram.edges().size());
	}

	@Test
	void testIndexOf() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(aEdge1);
		aEdge2.connect(aClassNode1, aClassNode2);
		aDiagram.addEdge(aEdge2);
		assertEquals(0, aDiagram.indexOf(aEdge1));
		assertEquals(1, aDiagram.indexOf(aEdge2));
	}

	@Test
	void testAddEdgeIndexEmpty() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(0, aEdge1);
		assertEquals(1, aDiagram.edges().size());
		assertEquals(0, aDiagram.indexOf(aEdge1));
	}

	@Test
	void testAddEdgeIndexBefore() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(aEdge1);
		aEdge2.connect(aClassNode1, aClassNode2);
		aDiagram.addEdge(0, aEdge2);
		assertEquals(2, aDiagram.edges().size());
		assertSame(aEdge2, aDiagramAccessor.edges().get(0));
		assertSame(aEdge1, aDiagramAccessor.edges().get(1));
	}

	@Test
	void testAddEdgeIndexAfter() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(aEdge1);
		aEdge2.connect(aClassNode1, aClassNode2);
		aDiagram.addEdge(1, aEdge2);
		assertEquals(2, aDiagram.edges().size());
		assertSame(aEdge1, aDiagramAccessor.edges().get(0));
		assertSame(aEdge2, aDiagramAccessor.edges().get(1));
	}

	@Test
	void testAddRemoveRootNode() {
		aDiagram.addRootNode(aClassNode1);
		assertEquals(1, aDiagramAccessor.rootNodes().size());
		assertSame(aClassNode1, aDiagramAccessor.rootNodes().get(0));
		aDiagram.addRootNode(aClassNode2);
		assertEquals(2, aDiagramAccessor.rootNodes().size());
		assertSame(aClassNode2, aDiagramAccessor.rootNodes().get(1));

		aDiagram.removeRootNode(aClassNode2);
		assertEquals(1, aDiagramAccessor.rootNodes().size());
		assertSame(aClassNode1, aDiagramAccessor.rootNodes().get(0));

		aDiagram.removeRootNode(aClassNode1);
		assertEquals(0, aDiagramAccessor.rootNodes().size());
	}

	@Test
	void testAddRemoveEdge() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(aEdge1);
		assertEquals(1, aDiagramAccessor.edges().size());
		aEdge2.connect(aClassNode1, aClassNode2);
		aDiagram.addEdge(aEdge2);
		assertEquals(2, aDiagramAccessor.edges().size());
		assertSame(aEdge1, aDiagramAccessor.edges().get(0));
		assertSame(aEdge2, aDiagramAccessor.edges().get(1));
		aEdge3.connect(aClassNode2, aClassNode1);
		aDiagram.addEdge(aEdge3);
		assertEquals(3, aDiagramAccessor.edges().size());
		assertSame(aEdge1, aDiagramAccessor.edges().get(0));
		assertSame(aEdge2, aDiagramAccessor.edges().get(1));
		assertSame(aEdge3, aDiagramAccessor.edges().get(2));

		aDiagram.removeEdge(aEdge2);
		assertEquals(2, aDiagramAccessor.edges().size());
		assertSame(aEdge1, aDiagramAccessor.edges().get(0));
		assertSame(aEdge3, aDiagramAccessor.edges().get(1));

		aDiagram.removeEdge(aEdge1);
		assertEquals(1, aDiagramAccessor.edges().size());
		assertSame(aEdge3, aDiagramAccessor.edges().get(0));
	}

	@Test
	void testContainsEmpty() {
		assertFalse(aDiagram.contains(aClassNode1));
		assertFalse(aDiagram.contains(aEdge1));
	}

	@Test
	void testContainsEdge() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aEdge1.connect(aClassNode1, aClassNode1);
		aDiagram.addEdge(aEdge1);
		aEdge2.connect(aClassNode1, aClassNode2);
		aDiagram.addEdge(aEdge2);
		aEdge3.connect(aClassNode2, aClassNode1);
		assertTrue(aDiagram.contains(aEdge1));
		assertTrue(aDiagram.contains(aEdge2));
		assertFalse(aDiagram.contains(aEdge3));
	}

	@Test
	void testContainsNodeRoot() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		assertTrue(aDiagram.contains(aClassNode1));
		assertTrue(aDiagram.contains(aClassNode2));
		assertFalse(aDiagram.contains(aClassNode3));
	}

	@Test
	void testContainsNodeChild() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aDiagram.addRootNode(aPackageNode1);
		aPackageNode1.addChild(aClassNode3);
		assertTrue(aDiagram.contains(aClassNode1));
		assertTrue(aDiagram.contains(aClassNode2));
		assertTrue(aDiagram.contains(aClassNode3));
		assertTrue(aDiagram.contains(aPackageNode1));
	}

	@Test
	void testContainsNodeChildChild() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aDiagram.addRootNode(aPackageNode1);
		aPackageNode1.addChild(aClassNode3);
		ClassNode child = new ClassNode();
		PackageNode packageNode = new PackageNode();
		packageNode.addChild(child);
		aPackageNode1.addChild(new PackageNode());
		aPackageNode1.addChild(packageNode);
		aPackageNode1.addChild(new ClassNode());
		aPackageNode1.addChild(new PackageNode());
		packageNode.addChild(new ClassNode());
		assertTrue(aDiagram.contains(aClassNode1));
		assertTrue(aDiagram.contains(aClassNode2));
		assertTrue(aDiagram.contains(aClassNode3));
		assertTrue(aDiagram.contains(aPackageNode1));
		assertTrue(aDiagram.contains(child));
		assertTrue(aDiagram.contains(packageNode));
	}

	@Test
	void testEdgesConnectedToEmpty() {
		aDiagram.addRootNode(aClassNode1);
		assertTrue(aDiagramAccessor.edgesConnectedTo(aClassNode1).isEmpty());
	}

	@Test
	void testEdgesConnectedToTwoEdges() {
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aDiagram.addRootNode(aClassNode3);
		aEdge1.connect(aClassNode2, aClassNode2);
		aEdge2.connect(aClassNode2, aClassNode3);
		aEdge3.connect(aClassNode3, aClassNode2);
		aDiagram.addEdge(aEdge1);
		aDiagram.addEdge(aEdge2);
		aDiagram.addEdge(aEdge3);
		assertTrue(aDiagramAccessor.edgesConnectedTo(aClassNode1).isEmpty());
		List<Edge> result = aDiagramAccessor.edgesConnectedTo(aClassNode2);
		assertEquals(3, result.size());
		result = aDiagramAccessor.edgesConnectedTo(aClassNode3);
		assertEquals(2, result.size());
		assertTrue(result.contains(aEdge2));
		assertTrue(result.contains(aEdge3));
	}
}

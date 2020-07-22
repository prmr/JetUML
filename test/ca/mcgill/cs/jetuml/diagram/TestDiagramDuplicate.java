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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;

/**
 * For testing method duplicate of class Diagram.
 */
public class TestDiagramDuplicate
{
	private Diagram aClassDiagram;
	
	@BeforeEach
	public void setUp()
	{
		aClassDiagram = new Diagram(DiagramType.CLASS);
	}
	
	@Test
	public void test_empty()
	{
		assertEquals(0, aClassDiagram.duplicate().edges().size());
		assertEquals(0, aClassDiagram.duplicate().rootNodes().size());
	}
	
	@Test
	public void test_NoEdges()
	{
		aClassDiagram.addRootNode(new ClassNode());
		Diagram copy = aClassDiagram.duplicate();
		assertEquals(0, aClassDiagram.duplicate().edges().size());
		assertEquals(1, copy.rootNodes().size());
		Node node = copy.rootNodes().get(0);
		assertNotSame(aClassDiagram.rootNodes().get(0), node);
	}
	
	@Test
	public void test_oneEdge()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aClassDiagram.addRootNode(node1);
		aClassDiagram.addRootNode(node2);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2, aClassDiagram);
		aClassDiagram.addEdge(edge);
		Diagram copy = aClassDiagram.duplicate();
		assertNotSame(aClassDiagram.rootNodes().get(0), copy.rootNodes().get(0));
		assertNotSame(aClassDiagram.rootNodes().get(1), copy.rootNodes().get(1));
		assertNotSame(aClassDiagram.edges().get(0), copy.edges().get(0));
		assertSame(copy.rootNodes().get(0), copy.edges().get(0).getStart());
		assertSame(copy.rootNodes().get(1), copy.edges().get(0).getEnd());
	}
	
	@Test
	public void test_DiagramReassignmentInNodes()
	{
		aClassDiagram.addRootNode(new ClassNode());
		Diagram copy = aClassDiagram.duplicate();
		assertEquals(0, copy.edges().size());
		assertEquals(1, copy.rootNodes().size());
		Node node = copy.rootNodes().get(0);
		assertSame(copy, node.getDiagram().get());
	}
	
	@Test
	public void test_DiagramReassignmentInEdges()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aClassDiagram.addRootNode(node1);
		aClassDiagram.addRootNode(node2);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2, aClassDiagram);
		aClassDiagram.addEdge(edge);
		Diagram copy = aClassDiagram.duplicate();
		assertSame(copy, copy.edges().get(0).getDiagram());
	}
	
	@Test
	public void test_edgeInnerNodeToInnerNode()
	{
		PackageNode p1 = new PackageNode();
		PackageNode p2 = new PackageNode();
		ClassNode n1 = new ClassNode();
		ClassNode n2 = new ClassNode();
		p1.setName("p1");
		p2.setName("p2");
		n1.setName("n1");
		n2.setName("n2");
		DependencyEdge edge = new DependencyEdge();
		aClassDiagram.addRootNode(p1);
		aClassDiagram.addRootNode(p2);
		p1.addChild(n1);
		p2.addChild(n2);
		edge.connect(n1, n2, aClassDiagram);
		aClassDiagram.addEdge(edge);
		Diagram copy = aClassDiagram.duplicate();
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
		assertSame(n1Copy, edgeCopy.getStart());
		assertSame(n2Copy, edgeCopy.getEnd());
		assertSame(copy, p1Copy.getDiagram().get());
		assertSame(copy, p2Copy.getDiagram().get());
		assertSame(copy, n2Copy.getDiagram().get());
		assertSame(copy, n2Copy.getDiagram().get());
		assertSame(copy, edgeCopy.getDiagram());
	}
}

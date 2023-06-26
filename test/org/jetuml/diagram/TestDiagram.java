/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020-2023 by McGill University.
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

import static org.jetuml.testutils.CollectionAssertions.assertThat;
import static org.jetuml.testutils.CollectionAssertions.hasElementsSameAs;
import static org.jetuml.testutils.CollectionAssertions.hasSetOfElementsEqualsTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.nodes.AbstractNode;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.testutils.CollectionAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestDiagram 
{
	private Diagram aDiagram = new Diagram(null);
	private Node aNode1 = new StubNode();
	private Node aNode2 = new PackageNode();
	private Node aNode3 = new ClassNode();
	private Node aNode4 = new PackageNode();
	
	static class StubNode extends AbstractNode{ }
	
	@Test
	void testContainsAsRoot_RootNode()
	{
		aDiagram.addRootNode(aNode1);
		assertTrue(aDiagram.containsAsRoot(aNode1));
	}
	
	@Test
	void testContainsAsRoot_NonRootNode()
	{
		assertFalse(aDiagram.containsAsRoot(aNode1));
	}
	
	@ParameterizedTest
	@MethodSource("argumentsForFileExtensions")
	public void testFileExtensions(Diagram pDiagram, String pExtension)
	{
		assertEquals(pExtension, pDiagram.getFileExtension());
	}
	
	private static Stream<Arguments> argumentsForFileExtensions() {
	    return Stream.of(
	      Arguments.of(new Diagram(DiagramType.CLASS), ".class"),
	      Arguments.of(new Diagram(DiagramType.SEQUENCE), ".sequence"),
	      Arguments.of(new Diagram(DiagramType.STATE), ".state"),
	      Arguments.of(new Diagram(DiagramType.OBJECT), ".object"),
	      Arguments.of(new Diagram(DiagramType.USECASE), ".usecase")
	    );
	}
	
	@Test
	void testPlaceOnTop_NonRootNode()
	{
		aDiagram.addRootNode(aNode2);
		aDiagram.placeOnTop(aNode1);
		List<Node> rootList = aDiagram.rootNodes();
		assertSame(rootList.get(0), aNode2);
		assertTrue(rootList.size()==1);
	}
	
	@Test
	void testPlaceOnTop_ChildNodeWithParent()
	{
		aNode4.addChild(aNode2);
		aNode2.link(aNode4);
		aNode4.addChild(aNode3);
		aNode3.link(aNode4);
		
		aDiagram.addRootNode(aNode4);
		aDiagram.addRootNode(aNode1);
		aDiagram.placeOnTop(aNode2);
		
		List<Node> rootList = aDiagram.rootNodes();
		assertSame(rootList.get(0), aNode1);
		assertSame(rootList.get(1), aNode4);
		// Ensure that the moved child node is now on top of all children
		assertSame(aNode4.getChildren().get(1), aNode2);
	}
	
	@Test
	void testPlaceOnTop_SequenceDiagramWithCallNodes()
	{
		ImplicitParameterNode implicitParameterNode = new ImplicitParameterNode();
		CallNode callNode1 = new CallNode();
		CallNode callNode2 = new CallNode();
		implicitParameterNode.addChild(callNode1);
		implicitParameterNode.addChild(callNode2);
		Diagram sequenceDiagram = new Diagram(DiagramType.SEQUENCE);
		sequenceDiagram.addRootNode(implicitParameterNode);
		sequenceDiagram.placeOnTop(callNode1);
		
		// The order of the call nodes remains the same
		List<Node> childNodes = implicitParameterNode.getChildren();
		assertSame(childNodes.get(0),callNode1);
		assertSame(childNodes.get(1), callNode2);
	}
	
	@Test
	void testAllNodes_SingleRoot()
	{
		Diagram diagram = new Diagram(null);
		diagram.addRootNode(aNode1);
		assertThat(diagram.allNodes(), hasElementsSameAs, aNode1);
	}
	
	@Test
	void testAllNodes_TwoChildlessRoots()
	{
		Diagram diagram = new Diagram(null);
		diagram.addRootNode(aNode2);
		diagram.addRootNode(aNode4);
		assertThat(diagram.allNodes(), hasSetOfElementsEqualsTo, aNode2, aNode4);
	}
	
	@Test
	void testAllNodes_TwoParents()
	{
		Diagram diagram = new Diagram(null);
		ClassNode node1 = new ClassNode();
		aNode2.addChild(node1);
		aNode4.addChild(aNode3);
		diagram.addRootNode(aNode2);
		diagram.addRootNode(aNode4);
		assertThat(diagram.allNodes(), hasSetOfElementsEqualsTo, node1, aNode2, aNode3, aNode4);
	}
	
	@Test
	void testAllNodes_ThreeHierarchicalLevels()
	{
		Diagram diagram = new Diagram(null);
		aNode4.addChild(aNode2);
		aNode2.addChild(aNode3);
		diagram.addRootNode(aNode4);
		assertThat(diagram.allNodes(), hasSetOfElementsEqualsTo, aNode2, aNode3, aNode4);
	}
	
	@Test
	void testEdgesConnectedTo_NoEdges()
	{
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		assertFalse(aDiagram.edgesConnectedTo(aNode1).iterator().hasNext());
	}
	
	@Test
	void testEdgesTo_Empty()
	{
		aDiagram.addRootNode(aNode1);
		assertTrue(aDiagram.edgesTo(aNode1, DependencyEdge.class).isEmpty());
	}
	
	@Test
	void testEdgesTo_NodeSelection()
	{
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		aDiagram.addRootNode(aNode3);
		
		Edge edge1 = new DependencyEdge();
		edge1.connect(aNode1, aNode2);
		aDiagram.addEdge(edge1);
		
		Edge edge2 = new DependencyEdge();
		edge2.connect(aNode2, aNode3);
		aDiagram.addEdge(edge2);

		assertThat(aDiagram.edgesTo(aNode1, DependencyEdge.class), CollectionAssertions.isEmpty);
		assertThat(aDiagram.edgesTo(aNode2, DependencyEdge.class), hasElementsSameAs, edge1);
	}
	
	@Test
	void testEdgesTo_TypeSelection()
	{
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		
		Edge edge1 = new DependencyEdge();
		edge1.connect(aNode1, aNode2);
		aDiagram.addEdge(edge1);
		
		Edge edge2 = new AssociationEdge();
		edge2.connect(aNode1, aNode1);
		aDiagram.addEdge(edge2);

		assertThat(aDiagram.edgesTo(aNode2, DependencyEdge.class), hasElementsSameAs, edge1);
	}
}

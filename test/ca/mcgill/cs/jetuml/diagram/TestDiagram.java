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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ca.mcgill.cs.jetuml.diagram.nodes.AbstractNode;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;

public class TestDiagram 
{
	private Diagram aDiagram;
	private Node aNode1;
	private Node aNode2;
	private Node aNode3;
	private Node aNode4;
	
	static class StubNode extends AbstractNode{ }
	
	@BeforeEach
	public void setup()
	{
		aDiagram = new Diagram(null);
		aNode1 = new StubNode();
		aNode2 = new PackageNode();
		aNode3 = new ClassNode();
		aNode4 = new PackageNode();
	}
	
	@Test
	public void testContainsAsRoot_RootNode()
	{
		aDiagram.addRootNode(aNode1);
		assertTrue(aDiagram.containsAsRoot(aNode1));
	}
	
	@Test
	public void testContainsAsRoot_NonRootNode()
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
	public void testPlaceOnTop_NonRootNode()
	{
		aDiagram.addRootNode(aNode2);
		aDiagram.placeOnTop(aNode1);
		List<Node> rootList = aDiagram.rootNodes();
		assertSame(rootList.get(0), aNode2);
		assertTrue(rootList.size()==1);
	}
	
	@Test
	public void testPlaceOnTop_ChildNodeWithParent()
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
	public void testPlaceOnTop_SequenceDiagramWithCallNodes()
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
}

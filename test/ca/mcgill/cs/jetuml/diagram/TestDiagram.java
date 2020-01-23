/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ca.mcgill.cs.jetuml.diagram.nodes.AbstractNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;

public class TestDiagram 
{
	private Diagram aDiagram;
	private Node aNode1;
	private ChildNode aNode2;
	private ChildNode aNode3;
	private ParentNode aNode4;
	
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
	
	@ParameterizedTest
	@MethodSource("argumentsForDescriptions")
	public void testDescriptions(Diagram pDiagram, String pExtension)
	{
		assertEquals(pExtension, pDiagram.getDescription());
	}
	
	private static Stream<Arguments> argumentsForFileExtensions() {
	    return Stream.of(
	      Arguments.of(new Diagram(DiagramType.CLASS), RESOURCES.getString("classdiagram.file.extension")),
	      Arguments.of(new Diagram(DiagramType.SEQUENCE), RESOURCES.getString("sequencediagram.file.extension")),
	      Arguments.of(new Diagram(DiagramType.STATE), RESOURCES.getString("statediagram.file.extension")),
	      Arguments.of(new Diagram(DiagramType.OBJECT), RESOURCES.getString("objectdiagram.file.extension")),
	      Arguments.of(new Diagram(DiagramType.USECASE), RESOURCES.getString("usecasediagram.file.extension"))
	    );
	}
	
	private static Stream<Arguments> argumentsForDescriptions() {
	    return Stream.of(
	      Arguments.of(new Diagram(DiagramType.CLASS), RESOURCES.getString("classdiagram.file.name")),
	      Arguments.of(new Diagram(DiagramType.SEQUENCE), RESOURCES.getString("sequencediagram.file.name")),
	      Arguments.of(new Diagram(DiagramType.STATE), RESOURCES.getString("statediagram.file.name")),
	      Arguments.of(new Diagram(DiagramType.OBJECT), RESOURCES.getString("objectdiagram.file.name")),
	      Arguments.of(new Diagram(DiagramType.USECASE), RESOURCES.getString("usecasediagram.file.name"))
	    );
	}
	
	@Test
	public void testPlaceOnTop_NonRootNode()
	{
		aDiagram.addRootNode(aNode2);
		aDiagram.placeOnTop(aNode1);
		Iterator<Node> pIterator = aDiagram.rootNodes().iterator();
		assertSame(pIterator.next(), aNode2);
		assertFalse(pIterator.hasNext());
	}
	
	@Test
	public void testPlaceOnTop_ChildNodeWithParent()
	{
		aNode4.addChild(aNode2);
		aNode2.setParent(aNode4);
		aNode4.addChild(aNode3);
		aNode3.setParent(aNode4);
		
		aDiagram.addRootNode(aNode4);
		aDiagram.addRootNode(aNode1);
		aDiagram.placeOnTop(aNode2);
		
		Iterator<Node> pIterator = aDiagram.rootNodes().iterator();
		assertSame(pIterator.next(), aNode1);
		assertSame(pIterator.next(), aNode4);
		// Ensure that the moved child node is now on top of all children
		assertSame(aNode4.getChildren().get(1), aNode2);
	}
}

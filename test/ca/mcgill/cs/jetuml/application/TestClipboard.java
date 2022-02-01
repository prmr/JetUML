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
package ca.mcgill.cs.jetuml.application;

import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.assertThat;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.doesNotContain;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.extract;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasElementsEqualTo;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasElementsSameAs;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasSize;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.isEmpty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestClipboard
{
	private Clipboard aClipboard = Clipboard.instance();
	private Field aNodesField;
	private Field aEdgesField;
	private ClassNode aNode1 = new ClassNode();
	private ClassNode aNode2 = new ClassNode();
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	public TestClipboard() throws ReflectiveOperationException
	{
		aNodesField = Clipboard.class.getDeclaredField("aNodes");
		aNodesField.setAccessible(true);
		aEdgesField = Clipboard.class.getDeclaredField("aEdges");
		aEdgesField.setAccessible(true);
	}
	
	@SuppressWarnings("unchecked")
	private static List<Edge> copyEdges(Iterable<DiagramElement> pSelection)
	{
		try
		{
			Method method = Clipboard.class.getDeclaredMethod("copyEdges", Iterable.class);
			method.setAccessible(true);
			return (List<Edge>) method.invoke(null, pSelection);
		}
		catch(Exception e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Node> getClipboardNodes()
	{
		try 
		{
			return (List<Node>) aNodesField.get(aClipboard);
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private List<Edge> getClipboardEdges()
	{
		try
		{
			return (List<Edge>) aEdgesField.get(aClipboard);
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@Test
	void testCopyEdges_Empty()
	{
		assertTrue(copyEdges(new ArrayList<>()).isEmpty());
	}
	
	@Test
	void testCopyEdges_OnlyNodes()
	{
		assertTrue(copyEdges(List.of(aNode1, aNode2)).isEmpty());
	}
	
	@Test
	void testCopyEdges_NodesAndEdges()
	{
		Edge edge1 = new DependencyEdge();
		edge1.connect(aNode1, aNode2, aDiagram);
		Edge edge2 = new AssociationEdge();
		edge2.connect(aNode2, aNode1, aDiagram);
		List<Edge> copies = copyEdges(List.of(aNode1, edge1, aNode2, edge2));
		assertNotSame(edge1, copies.get(0));
		assertSame(DependencyEdge.class, copies.get(0).getClass());
		assertSame(aNode1, copies.get(0).getStart());
		assertSame(aNode2, copies.get(0).getEnd());
		assertNotSame(edge2, copies.get(1));
		assertSame(aNode2, copies.get(1).getStart());
		assertSame(aNode1, copies.get(1).getEnd());
	}
	
	@Test
	void testCopySingleNodeNoReposition()
	{
		aClipboard.copy(Arrays.asList(aNode1));
		List<Node> clipboardNodes = getClipboardNodes();
		assertThat(extract(clipboardNodes, Node::position), hasElementsEqualTo, new Point(0,0));
		assertThat(clipboardNodes, doesNotContain, aNode1 ); // Because it's a clone
	}
	
	@Test
	void testCopySingleNodeReposition()
	{
		aNode1.translate(10, 10);
		aClipboard.copy(Arrays.asList(aNode1));
		List<Node> clipboardNodes = getClipboardNodes();
		assertThat(extract(clipboardNodes, Node::position), hasElementsEqualTo, new Point(10,10));
		assertThat(clipboardNodes, doesNotContain, aNode1); // Because it's a clone
	}
	
	@Test
	void testCopyTwoNodesOneEdgeFlat()
	{
		aNode1.translate(10, 10);
		aNode2.translate(200, 200);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aDiagram);
		aClipboard.copy(Arrays.asList(aNode1, aNode2, edge));
		
		List<Node> nodes = getClipboardNodes();
		assertThat(extract(nodes, Node::position), hasElementsEqualTo, new Point(10,10), new Point(200,200));

		List<Edge> edges = getClipboardEdges();
		assertThat(extract(edges, Edge::getStart), hasElementsSameAs, nodes.get(0));
		assertThat(extract(edges, Edge::getEnd), hasElementsSameAs, nodes.get(1));
	}
	
	@Test
	void testCopyDanglingEdgeFlat()
	{
		aNode1.translate(10, 10);
		aNode2.translate(200, 200);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aDiagram);
		aClipboard.copy(Arrays.asList(aNode1, edge));
		
		List<Node> nodes = getClipboardNodes();
		assertThat(extract(nodes, Node::position), hasElementsEqualTo, new Point(10,10));

		assertThat(getClipboardEdges(), isEmpty );
	}
	
	@Test
	void testCopyNodeWithOneChild()
	{
		PackageNode pn = new PackageNode();
		pn.addChild(aNode1);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode1, aDiagram);
		aClipboard.copy(Arrays.asList(pn));
		
		List<Node> nodes = getClipboardNodes();
		assertThat(nodes, hasSize, 1);
		
		PackageNode node = (PackageNode)nodes.get(0);
		assertThat(node.getChildren(), hasSize, 1);
		assertThat(extract(nodes, Node::position), hasElementsEqualTo, new Point(0,0));
		
		assertThat(getClipboardEdges(), isEmpty );
	}
	
	@Test
	void testCopyNodeWithOneParent()
	{
		PackageNode packageNode = new PackageNode();
		packageNode.addChild(aNode1);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode1, aDiagram);
		aClipboard.copy(Arrays.asList(aNode1));
		
		List<Node> nodes = getClipboardNodes();
		assertThat( nodes, hasSize, 1 );
		
		ClassNode node = (ClassNode)nodes.get(0);
		assertFalse(node.hasParent());
		assertThat(extract(nodes, Node::position), hasElementsEqualTo, new Point(0,0));
		
		assertThat(getClipboardEdges(), isEmpty );
	}
	
	@Test
	void testCopyNodeMissingParent()
	{
		ObjectNode node = new ObjectNode();
		FieldNode field = new FieldNode();
		field.setName("Foo");
		field.setValue("Bar");
		node.addChild(field);
		
		aClipboard.copy(Arrays.asList(field));
		assertThat( getClipboardNodes(), isEmpty ); 
	}
	
	@Test
	void testValidPasteOfPointNode() 
	{
		PointNode node = new PointNode();
		aClipboard.copy(Arrays.asList(node));
		assertTrue(aClipboard.validPaste(new Diagram(DiagramType.CLASS)));
	}
	
	@Test
	void testValidPasteForDifferentDiagramTypes() 
	{
		ClassNode classNode = new ClassNode();
		aClipboard.copy(Arrays.asList(classNode));
		assertTrue(aClipboard.validPaste(new Diagram(DiagramType.CLASS)));
		assertFalse(aClipboard.validPaste(new Diagram(DiagramType.SEQUENCE)));
	}
}

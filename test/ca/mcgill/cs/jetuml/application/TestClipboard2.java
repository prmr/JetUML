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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestClipboard2
{
	private Clipboard aClipboard;
	private Field aNodesField;
	private Field aEdgesField;
	private ClassNode aNode1;
	private ClassNode aNode2;
	private ClassDiagram aDiagram;
	
	public TestClipboard2() throws ReflectiveOperationException
	{
		aNodesField = Clipboard.class.getDeclaredField("aNodes");
		aNodesField.setAccessible(true);
		aEdgesField = Clipboard.class.getDeclaredField("aEdges");
		aEdgesField.setAccessible(true);
	}
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
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
	
	@Before
	public void setup()
	{
		aClipboard = Clipboard.instance();		
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aDiagram = new ClassDiagram();
	}
	
	@Test
	public void testCopySingleNodeNoReposition()
	{
		aClipboard.copy(Arrays.asList(aNode1));
		assertEquals(1, getClipboardNodes().size());
		assertFalse(getClipboardNodes().contains(aNode1)); // Clone
		assertEquals(new Point(0,0), getClipboardNodes().get(0).position());
	}
	
	@Test
	public void testCopySingleNodeReposition()
	{
		aNode1.translate(10, 10);
		aClipboard.copy(Arrays.asList(aNode1));
		assertEquals(1, getClipboardNodes().size());
		assertFalse(getClipboardNodes().contains(aNode1)); // Clone
		assertEquals(new Point(0,0), getClipboardNodes().get(0).position());
	}
	
	@Test
	public void testCopyTwoNodesOneEdgeFlat()
	{
		aNode1.translate(10, 10);
		aNode2.translate(200, 200);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aDiagram);
		aClipboard.copy(Arrays.asList(aNode1, aNode2, edge));
		List<Node> nodes = getClipboardNodes();
		assertEquals(2, nodes.size());
		assertEquals(new Point(0,0), nodes.get(0).position());
		assertEquals(new Point(190,190), nodes.get(1).position());
		List<Edge> edges = getClipboardEdges();
		assertEquals(1, edges.size());
		assertTrue(edges.get(0).getStart() == nodes.get(0));
		assertTrue(edges.get(0).getEnd() == nodes.get(1));
	}
	
	@Test
	public void testCopyDanglingEdgeFlat()
	{
		aNode1.translate(10, 10);
		aNode2.translate(200, 200);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aDiagram);
		aClipboard.copy(Arrays.asList(aNode1, edge));
		List<Node> nodes = getClipboardNodes();
		assertEquals(1, nodes.size());
		assertEquals(new Point(0,0), nodes.get(0).position());
		List<Edge> edges = getClipboardEdges();
		assertEquals(0, edges.size());
	}
	
	@Test
	public void testCopyNodeWithOneChild()
	{
		PackageNode pn = new PackageNode();
		pn.addChild(aNode1);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode1, aDiagram);
		aClipboard.copy(Arrays.asList(pn));
		List<Node> nodes = getClipboardNodes();
		assertEquals(1, nodes.size());
		PackageNode node = (PackageNode)nodes.get(0);
		assertEquals(1, node.getChildren().size());
		assertEquals(new Point(0,0), nodes.get(0).position());
		List<Edge> edges = getClipboardEdges();
		assertEquals(0, edges.size());
	}
	
	@Test
	public void testCopyNodeWithOneParent()
	{
		PackageNode pn = new PackageNode();
		pn.addChild(aNode1);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode1, aDiagram);
		aClipboard.copy(Arrays.asList(aNode1));
		List<Node> nodes = getClipboardNodes();
		assertEquals(1, nodes.size());
		ClassNode node = (ClassNode)nodes.get(0);
		assertNull(node.getParent());
		assertEquals(new Point(0,0), nodes.get(0).position());
		List<Edge> edges = getClipboardEdges();
		assertEquals(0, edges.size());
	}
	
	@Test
	public void testCopyNodeMissingParent()
	{
		ObjectNode node = new ObjectNode();
		FieldNode field = new FieldNode();
		field.setName("Foo");
		field.setValue("Bar");
		node.addChild(field);
		
		aClipboard.copy(Arrays.asList(field));
		List<Node> nodes = getClipboardNodes();
		assertEquals(0, nodes.size());
	}
}

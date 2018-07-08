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

package ca.mcgill.cs.jetuml.diagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;


/**
 * Tests for the methods of class Diagram as obtainable through
 * an instance of ClassDiagram
 */
public class TestGraph
{
	private Diagram aGraph;
	private ClassNode aNode1;
	private ClassNode aNode2;
	private ClassNode aNode3;
	private DependencyEdge aEdge1;
	private AggregationEdge aEdge2;
	private AggregationEdge aEdge3;
	
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
		aGraph = new ClassDiagram();
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aNode3 = new ClassNode();
		aEdge1 = new DependencyEdge();
		aEdge2 = new AggregationEdge();
		aEdge3 = new AggregationEdge(AggregationEdge.Type.Composition);
		aGraph.insertNode(aNode1);
		aGraph.insertNode(aNode2);
		aGraph.insertNode(aNode3);
	}
	
	@Test
	public void testExistsEdge()
	{
		aGraph.restoreEdge(aEdge1, aNode1, aNode2);
		aGraph.restoreEdge(aEdge2, aNode1, aNode2);
		aGraph.restoreEdge(aEdge3, aNode2, aNode1);
		
		assertTrue(aGraph.existsEdge(DependencyEdge.class, aNode1, aNode2));
		assertTrue(aGraph.existsEdge(AggregationEdge.class, aNode1, aNode2));
		assertTrue(aGraph.existsEdge(AggregationEdge.class, aNode2, aNode1));
		assertFalse(aGraph.existsEdge(DependencyEdge.class, aNode2, aNode1));
		assertFalse(aGraph.existsEdge(GeneralizationEdge.class, aNode1, aNode2));
		assertFalse(aGraph.existsEdge(DependencyEdge.class, aNode1, aNode3));
		assertFalse(aGraph.existsEdge(DependencyEdge.class, aNode3, aNode1));
	}
	
	@Test
	public void testBasicConnect()
	{
		aNode1.translate(150, 0);
		aNode2.translate(150, 200);
		
		// A failed connection between two points, the second not in a node
		assertFalse(aGraph.canAdd(aEdge1, new Point(50, 30), new Point(1000, 1000)));
		assertFalse(aGraph.contains(aEdge1));
		assertNull(aEdge1.getStart());
		assertNull(aEdge1.getEnd());
		
		// A correct connection between two points
		assertTrue(aGraph.canAdd(aEdge1, new Point(200, 30), new Point(200, 200)));
		aGraph.addEdge(aEdge1, new Point(200, 30), new Point(200, 200));
		assertTrue(aGraph.contains(aEdge1));
		assertTrue(aEdge1.getStart() == aNode1);
		assertTrue(aEdge1.getEnd() == aNode2);
	}
	
	@Test
	public void testInsertNodeChildNodeNullParent()
	{
		ClassNode classNode = new ClassNode();
		aGraph.insertNode(classNode);
		assertTrue(aGraph.contains(classNode));
		assertTrue(aGraph.getRootNodes().contains(classNode));
	}
	
	@Test
	public void testInsertNodeChildNodeNonNullParent()
	{
		PackageNode packageNode = new PackageNode();
		aGraph.restoreRootNode(packageNode);
		ClassNode classNode = new ClassNode();
		classNode.setParent(packageNode);
		aGraph.insertNode(classNode);
		assertTrue(aGraph.contains(classNode));
		assertFalse(aGraph.getRootNodes().contains(classNode));
	}
	
	@Test
	public void testInsertNodeNotChildNode()
	{
		NoteNode note = new NoteNode();
		aGraph.insertNode(note);
		assertTrue(aGraph.contains(note));
		assertTrue(aGraph.getRootNodes().contains(note));
	}
	
	@Test
	public void testGetBoundsEmpty()
	{
		assertEquals(new Rectangle(0, 0, 0, 0), new ClassDiagram().getBounds());
	}
	
	@Test
	public void testGetBoundsSingleNode()
	{
		ClassDiagram graph = new ClassDiagram();
		graph.addNode(aNode1, new Point(0,0), Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals(new Rectangle(0,0,100,60), graph.getBounds());
	}
	
	@Test
	public void testGetBoundsNodesAndEdges()
	{
		aNode1.translate(10, 10);
		aNode2.translate(150, 200);
		aNode3.translate(20, 20);
		aGraph.restoreEdge(aEdge1, aNode1, aNode2);
		assertEquals(new Rectangle(10,10,240,250), aGraph.getBounds());
	}
	
	@Test
	public void testAddEdgeNode1Null()
	{
		assertFalse(aGraph.canAdd(aEdge1, new Point(500, 500), new Point(10, 10)));
	}
	
	@Test
	public void testAddEdgeNode2Null()
	{
		assertFalse(aGraph.canAdd(aEdge1, new Point(10, 10), new Point(500, 500)));
	}
	
	@Test
	public void testAddEdgeNode1NoteNode()
	{
		NoteNode note = new NoteNode();
		note.translate(50, 50);
		aGraph.restoreRootNode(note);
		NoteEdge edge = new NoteEdge();
		aGraph.addEdge(edge, new Point(60, 60), new Point(150, 150));
		assertTrue(aGraph.getEdges().contains(edge));
		assertEquals(note, edge.getStart());
		assertTrue(edge.getEnd() instanceof PointNode);
	}
	
	@Test
	public void testGetBoundsWithMove()
	{
		ClassDiagram graph = new ClassDiagram();
		ClassNode node = new ClassNode();
		graph.restoreRootNode(node);
		node.translate(50, 50);
		assertEquals(new Rectangle(50,50,100,60), graph.getBounds());
		node.translate(-50, -50);
		assertEquals(new Rectangle(0,0,100,60), graph.getBounds());
	}
	
	/*
	 * Tests that the point node associated with a NoteEdge is properly removed. 
	 */
	@Test
	public void testRemoveNodeEdge()
	{
		ClassDiagram graph = new ClassDiagram();
		NoteNode node = new NoteNode();
		PointNode point = new PointNode();
		NoteEdge edge = new NoteEdge();
		point.translate(100, 100);
		graph.restoreRootNode(node);
		graph.restoreRootNode(point);
		graph.restoreEdge(edge, node, point);
		assertEquals(1, graph.getEdges().size());
		assertEquals(2, graph.getRootNodes().size());
		graph.removeEdge(edge);
		graph.layout();
		assertEquals(0, graph.getEdges().size());
		Collection<Node> nodes = graph.getRootNodes();
		assertEquals(1, nodes.size());
		assertFalse(nodes.contains(point));
	}
}

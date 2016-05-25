/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Grid;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;


/**
 * Tests for the methods of class Graph as obtainable through
 * an instance of ClassDiagramGraph
 * 
 * @author Martin P. Robillard
 *
 */
public class TestGraph
{
	private Graph aGraph;
	private Grid aGrid;
	private Graphics2D aGraphics;
	private ClassNode aNode1;
	private ClassNode aNode2;
	private ClassNode aNode3;
	private DependencyEdge aEdge1;
	private AggregationEdge aEdge2;
	private AggregationEdge aEdge3;
	
	@Before
	public void setup()
	{
		aGraph = new ClassDiagramGraph();
		aGrid = new Grid();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
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
		assertFalse(aGraph.addEdge(aEdge1, new Point2D.Double(50, 30), new Point2D.Double(1000, 1000)));
		assertFalse(aGraph.contains(aEdge1));
		assertNull(aEdge1.getStart());
		assertNull(aEdge1.getEnd());
		
		// A correct connection between two points
		assertTrue(aGraph.addEdge(aEdge1, new Point2D.Double(200, 30), new Point2D.Double(200, 200)));
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
	public void testRemoveAllEdgesConnectedToNoConnection()
	{
		aGraph.removeAllEdgesConnectedTo(aNode1);
		assertTrue( aGraph.contains(aNode1 ));
	}
	
	@Test
	public void testRemoveAllEdgesConnectedToSingleConnection()
	{
		aGraph.restoreEdge(aEdge1, aNode1, aNode2);
		assertTrue( aGraph.contains(aEdge1 ));
		aGraph.removeAllEdgesConnectedTo(aNode1);
		// No layout
		assertTrue( aGraph.contains(aNode1 ));
		assertTrue( aGraph.contains(aNode2 ));
		assertTrue( aGraph.contains(aEdge1 ));
		aGraph.layout();
		aGraph.layout(aGraphics, aGrid);
		assertTrue( aGraph.contains(aNode1 ));
		assertTrue( aGraph.contains(aNode2 ));
		assertFalse( aGraph.contains(aEdge1 ));
	}
	
	@Test
	public void testRemoveAllEdgesConnectedToMultipleConnections()
	{
		aGraph.restoreEdge(aEdge1, aNode1, aNode2);
		aGraph.restoreEdge(aEdge2, aNode1, aNode3);
		aGraph.restoreEdge(aEdge3, aNode3, aNode3);
		assertTrue( aGraph.contains(aEdge1 ));
		aGraph.removeAllEdgesConnectedTo(aNode1);
		aGraph.layout();
		aGraph.layout(aGraphics, aGrid);
		assertTrue( aGraph.contains(aNode1 ));
		assertTrue( aGraph.contains(aNode2 ));
		assertFalse( aGraph.contains(aEdge1 ));
		assertFalse( aGraph.contains(aEdge2 ));
	}
	
	@Test
	public void testRemoveAllEdgesConnectedToNoLayout1()
	{
		aGraph.restoreEdge(aEdge1, aNode1, aNode2);
		aGraph.restoreEdge(aEdge2, aNode1, aNode3);
		aGraph.removeEdge(aEdge1);
		aGraph.removeAllEdgesConnectedTo(aNode1);
		aGraph.layout();
		aGraph.layout(aGraphics, aGrid);
		assertTrue( aGraph.contains(aNode1 ));
		assertTrue( aGraph.contains(aNode2 ));
		assertFalse( aGraph.contains(aEdge1 ));
		assertFalse( aGraph.contains(aEdge2 ));
	}
	
	@Test
	public void testGetBoundsEmpty()
	{
		assertEquals(new Rectangle2D.Double(), new ClassDiagramGraph().getBounds());
	}
	
	@Test
	public void testGetBoundsSingleNode()
	{
		ClassDiagramGraph graph = new ClassDiagramGraph();
		graph.addNode(aNode1, new Point2D.Double(0,0));
		assertEquals(new Rectangle2D.Double(0,0,104,64), graph.getBounds());
	}
	
	@Test
	public void testGetBoundsNodesAndEdges()
	{
		aNode1.translate(10, 10);
		aNode2.translate(150, 200);
		aNode3.translate(20, 20);
		aGraph.restoreEdge(aEdge1, aNode1, aNode2);
		assertEquals(new Rectangle2D.Double(10,10,244,254), aGraph.getBounds());
	}
	
	@Test
	public void testAddEdgeNode1Null()
	{
		aGraph.addEdge(aEdge1, new Point2D.Double(500, 500), new Point2D.Double(10, 10));
		assertFalse(aGraph.getEdges().contains(aEdge1));
	}
	
	@Test
	public void testAddEdgeNode2Null()
	{
		aGraph.addEdge(aEdge1, new Point2D.Double(10, 10), new Point2D.Double(500, 500));
		assertFalse(aGraph.getEdges().contains(aEdge1));
	}
	
	@Test
	public void testAddEdgeNode1NoteNode()
	{
		NoteNode note = new NoteNode();
		note.translate(50, 50);
		aGraph.restoreRootNode(note);
		NoteEdge edge = new NoteEdge();
		aGraph.addEdge(edge, new Point2D.Double(60, 60), new Point2D.Double(150, 150));
		assertTrue(aGraph.getEdges().contains(edge));
		assertEquals(note, edge.getStart());
		assertTrue(edge.getEnd() instanceof PointNode);
	}
	
	@Test
	public void testGetBoundsWithMove()
	{
		ClassDiagramGraph graph = new ClassDiagramGraph();
		ClassNode node = new ClassNode();
		graph.restoreRootNode(node);
		node.translate(50, 50);
		assertEquals(new Rectangle2D.Double(50,50,104,64), graph.getBounds());
		node.translate(-50, -50);
		assertEquals(new Rectangle2D.Double(0,0,104,64), graph.getBounds());
	}
}

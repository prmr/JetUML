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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;

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
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aNode3 = new ClassNode();
		aEdge1 = new DependencyEdge();
		aEdge2 = new AggregationEdge();
		aEdge3 = new AggregationEdge(AggregationEdge.Type.Composition);
		aGraph.add(aNode1, new Point2D.Double(0,0));
		aGraph.add(aNode2, new Point2D.Double(0,0));
		aGraph.add(aNode3, new Point2D.Double(0,0));
	}
	
	@Test
	public void testExistsEdge()
	{
		aGraph.connect(aEdge1, aNode1, aNode2);
		aGraph.connect(aEdge2, aNode1, aNode2);
		aGraph.connect(aEdge3, aNode2, aNode1);
		
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
		assertFalse(aGraph.connect(aEdge1, new Point2D.Double(50, 30), new Point2D.Double(1000, 1000)));
		assertFalse(aGraph.contains(aEdge1));
		assertNull(aEdge1.getStart());
		assertNull(aEdge1.getEnd());
		
		// A correct connection between two points
		assertTrue(aGraph.connect(aEdge1, new Point2D.Double(200, 30), new Point2D.Double(200, 200)));
		assertTrue(aGraph.contains(aEdge1));
		assertTrue(aEdge1.getStart() == aNode1);
		assertTrue(aEdge1.getEnd() == aNode2);
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
		graph.add(aNode1, new Point2D.Double(0,0));
		assertEquals(new Rectangle2D.Double(0,0,104,64), graph.getBounds());
	}
	
	@Test
	public void testGetBoundsNodesAndEdges()
	{
		aNode1.translate(10, 10);
		aNode2.translate(150, 200);
		aNode3.translate(20, 20);
		aGraph.connect(aEdge1, aNode1, aNode2);
		assertEquals(new Rectangle2D.Double(10,10,244,254), aGraph.getBounds());
	}
	
	@Test
	public void testGetBoundsWithMove()
	{
		ClassDiagramGraph graph = new ClassDiagramGraph();
		ClassNode node = new ClassNode();
		graph.addNode(node, new Point2D.Double(0,0));
		node.translate(50, 50);
		assertEquals(new Rectangle2D.Double(50,50,104,64), graph.getBounds());
		node.translate(-50, -50);
		assertEquals(new Rectangle2D.Double(0,0,104,64), graph.getBounds());
	}
}

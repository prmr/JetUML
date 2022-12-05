/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.rendering.edges;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.EdgePath;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

/**
 * Tests the EdgeStorage class.
 */
public class TestEdgeStorage 
{
	private EdgeStorage aEdgeStorage;
	private Diagram aDiagram;
	private Edge edge1;
	private Edge edge2;
	private Edge edge3;
	private EdgePath path1;
	private EdgePath path2;
	private EdgePath path3;
	private Node nodeA;
	private Node nodeB;
	private Node nodeC;
	
	TestEdgeStorage()
	{
		edge1 = new AggregationEdge();
		edge2 = new DependencyEdge();
		edge3 = new GeneralizationEdge();
		path1 = new EdgePath(new Point(0,0), new Point(0, 100), new Point(100, 100), new Point(200, 100));
		path2 = new EdgePath(new Point(300,300), new Point(300,350));
		path3 = new EdgePath(new Point(0,200), new Point(200, 200), new Point(200, 100), new Point(100, 100));
		aEdgeStorage = new EdgeStorage();
		aDiagram = new Diagram(null);
		nodeA = new ClassNode();
		nodeB = new ClassNode();
		nodeC = new ClassNode();
	}
	
	@Test
	void testContains()
	{
		aEdgeStorage.store(edge1, path1);
		assertTrue(aEdgeStorage.contains(edge1));
		assertFalse(aEdgeStorage.contains(edge3));
	}
	
	@Test
	void testGetEdgePath()
	{
		aEdgeStorage.store(edge1, path1);
		assertSame(aEdgeStorage.getEdgePath(edge1), path1);
	}
	
	@Test
	void testStore()
	{
		assertFalse(aEdgeStorage.contains(edge3));
		aEdgeStorage.store(edge3, path3);
		assertSame(aEdgeStorage.getEdgePath(edge3), path3);
		aEdgeStorage.store(edge3, path1);
		assertSame(aEdgeStorage.getEdgePath(edge3), path1);
	}
	
	@Test
	void testEdgesConnectedTo()
	{
		nodeA = new ClassNode();
		nodeB = new ClassNode();
		nodeC = new ClassNode();
		edge1.connect(nodeB, nodeA, aDiagram);
		edge2.connect(nodeA, nodeC, aDiagram);
		edge3.connect(nodeB, nodeC, aDiagram);
		aEdgeStorage.store(edge1, path1);
		aEdgeStorage.store(edge2, path2);
		aEdgeStorage.store(edge3, path3);
		List<Edge> edgesConnectedToNodeA = aEdgeStorage.edgesConnectedTo(nodeA);
		assertTrue(edgesConnectedToNodeA.contains(edge1));
		assertTrue(edgesConnectedToNodeA.contains(edge2));
		assertFalse(edgesConnectedToNodeA.contains(edge3));
	}
	
	@Test
	void testIsEmpty()
	{
		assertTrue(aEdgeStorage.isEmpty());
		aEdgeStorage.store(edge1, path1);
		assertFalse(aEdgeStorage.isEmpty());
	}
	
	@Test
	void testConnectionPointIsAvailable()
	{
		aEdgeStorage.store(edge1, path1);
		aEdgeStorage.store(edge2, path2);
		aEdgeStorage.store(edge3, path3);
		assertTrue(aEdgeStorage.connectionPointIsAvailable(new Point(1,1)));
		assertTrue(aEdgeStorage.connectionPointIsAvailable(new Point(200,200)));
		assertFalse(aEdgeStorage.connectionPointIsAvailable(new Point(0,0)));
		assertFalse(aEdgeStorage.connectionPointIsAvailable(new Point(100,100)));
	}
	
	@Test
	void testEdgesWithSameNodes()
	{
		nodeA = new ClassNode();
		nodeB = new ClassNode();
		edge1.connect(nodeB, nodeA, aDiagram);
		edge2.connect(nodeA, nodeB, aDiagram);
		edge3.connect(nodeC, nodeB, aDiagram);
		aEdgeStorage.store(edge1, path1);
		aEdgeStorage.store(edge2, path2);
		aEdgeStorage.store(edge3, path3);
		List<Edge> sameNodes = aEdgeStorage.getEdgesWithSameNodes(edge1);
		assertTrue(sameNodes.size() == 1);
		assertTrue(sameNodes.contains(edge2));
	}
	
	@Test
	void testClearStorage()
	{
		aEdgeStorage.store(edge1, path1);
		aEdgeStorage.store(edge2, path2);
		aEdgeStorage.store(edge3, path3);
		aEdgeStorage.clearStorage();
		assertFalse(aEdgeStorage.contains(edge1));
		assertFalse(aEdgeStorage.contains(edge2));
		assertFalse(aEdgeStorage.contains(edge3));
	}	
}
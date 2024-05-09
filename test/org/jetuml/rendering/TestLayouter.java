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
package org.jetuml.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.GeneralizationEdge.Type;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.edges.EdgeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the Layouter class methods.
 */
public class TestLayouter 
{
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private ClassDiagramRenderer aRenderer = new ClassDiagramRenderer(aDiagram);
	
	private Node aNodeA;
	private Node aNodeB;
	private Node aNodeC;
	private Node aNodeD;
	private Node aNodeE;
	private Node aNodeF;
	private Node aNodeG;
	private Node endNode;
	private Edge dependencyEdge;
	private Edge generalizationEdge;
	
	private Edge aEdgeA;
	private Edge aEdgeB;
	private Edge aEdgeC;
	private Edge aEdgeD;
	private Edge aEdgeE;
	private Edge aEdgeF;
	private Edge aEdgeG;
	
	private Rectangle aRectangleA;
	private Rectangle aRectangleB;
	private Rectangle aRectangleC;
	
	@BeforeEach
	public void setup()
	{
		aNodeA = new ClassNode();
		aNodeB = new ClassNode();
		aNodeC = new ClassNode();
		aNodeD = new ClassNode();
		aNodeE = new ClassNode();
		aNodeF = new ClassNode();
		aNodeG = new ClassNode();
		aEdgeA = new GeneralizationEdge();
		aEdgeB = new GeneralizationEdge();
		aEdgeC = new GeneralizationEdge();
		aEdgeD = new GeneralizationEdge();
		aEdgeE = new GeneralizationEdge();
		aRectangleA = new Rectangle(200, 200, 100, 60);
		aRectangleB = new Rectangle(200, 150, 100, 60);
		aRectangleC = new Rectangle(100, 200, 100, 60);
	}
	
	
	/**
	 * Sets up two class nodes in the diagram which are connected by a generalization Edge. 
	 * The required position of the nodes is unique for each test; nodes should be repositioned by individual test methods. 
	 */
	private void setUpTwoConnectedNodes()
	{
		aDiagram.addRootNode(aNodeA);//start node
		aDiagram.addRootNode(aNodeB);//end node
		aEdgeA.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aEdgeA);
	}
	
	/**
	 * Sets up 3 nodes, where both aNodeB and aNodeC are below aNodeA, and their edges point towards aNodeA.
	 * Nodes can be repositioned to suit different test cases.
	 */
	private void setUpThreeConnectedNodes()
	{
		 aDiagram.addRootNode(aNodeA);
		 aDiagram.addRootNode(aNodeB);
		 aDiagram.addRootNode(aNodeC);
		
		 aEdgeA.connect(aNodeB, aNodeA);
		 aEdgeB.connect(aNodeC, aNodeA);
		 
		 aDiagram.addEdge(aEdgeA);
		 aDiagram.addEdge(aEdgeB);
		 aNodeA.moveTo(new Point(100, 140));
		 aNodeB.moveTo(new Point(110, 300));
		 aNodeC.moveTo(new Point(200, 300));
	
	}
	
	/**
	 * Initializes 3 Aggregation Edges: aEdgeA, aEdgeB, aEdgeC
	 * which are outgoing from aNodeD and incoming on aNodeA, aNodeB, aNodeC, respectively.
	 * Nodes should be positioned by each test method. 
	 */
	private void setUpMergedStartEdges()
	{
		aDiagram.addRootNode(aNodeD);
		aEdgeA = new AggregationEdge();
		aEdgeB = new AggregationEdge();
		aEdgeC = new AggregationEdge();
		aEdgeD = new AggregationEdge();
		aEdgeA.connect(aNodeD, aNodeA);
		aEdgeB.connect(aNodeD, aNodeB);
		aEdgeC.connect(aNodeD, aNodeC);
		aDiagram.addEdge(aEdgeA);
		aDiagram.addEdge(aEdgeB);
		aDiagram.addEdge(aEdgeC);
		
	}
	
	/**
	 * Sets up three Generalization edges: aEdgeA, aEdgeB, aEdgeC. These edges start 
	 * at aNodeA, aNodeB, and aNodeC, receptively, and all end at aNodeD. 
	 * Nodes should be positioned by each test method based on the test case. 
	 */
	private void setUpMergedEndEdges()
	{
		aEdgeA.connect(aNodeA, aNodeD);
		aEdgeB.connect(aNodeB, aNodeD);
		aEdgeC.connect(aNodeC, aNodeD);
		aDiagram.addEdge(aEdgeA);
		aDiagram.addEdge(aEdgeB);
		aDiagram.addEdge(aEdgeC);
	}
	
	/**
	 * Used for LayoutDependencyEdge() test methods. 
	 * 
	 * Sets up a dependencyEdge connection aNodeB --> aNodeA 
	 * and a GeneralizationEdge connecting aNodeC --> aNodeA.
	 * Test methods should move nodes depending on the test scenario.
	 */
	private void setUpDependencyEdges()
	{
		dependencyEdge = new DependencyEdge();
		generalizationEdge = new GeneralizationEdge();
		dependencyEdge.connect(aNodeB, aNodeA);
		generalizationEdge.connect(aNodeC, aNodeA);
		aDiagram.addEdge(dependencyEdge);
		aDiagram.addEdge(generalizationEdge);
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
	}
	
	/**
	 * Sets up 5 nodes and 4 AggregationEdge edges of type pType
	 * @param pType the AggregationEdge type (either Aggregation or Composition)
	 * 
	 * 	aNodeA and aNodeB are to the West of aNodeD.
		aNodeC and aNodeE are South East of aNode.
		aEdgeA, aEdgeB,  and aEdgeC are Aggregation Edges which start at aNodeD.
		aEdgeD is a implementation edge connecting aNodeD <--- aNodeE, and is in storage.
	 */
	private void setUpLayoutMergedStartEdges(AggregationEdge.Type pType)
	{
		aEdgeA = new AggregationEdge(pType);
		aEdgeB = new AggregationEdge(pType);
		aEdgeC = new AggregationEdge(pType);
		aEdgeD = new GeneralizationEdge();
	
		for(Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD, aNodeE))
		{
			aDiagram.addRootNode(node);
		}
		
		aEdgeA.connect(aNodeD, aNodeA);
		aEdgeB.connect(aNodeD, aNodeB);
		aEdgeC.connect(aNodeD, aNodeC);
		aEdgeD.connect(aNodeE, aNodeD);
		for(Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD))
		{
			aDiagram.addEdge(edge);
		}
		
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 120));
		aNodeC.moveTo(new Point(400, 180));
		aNodeD.moveTo(new Point(200, 60));
		aNodeE.moveTo(new Point(400, 100));
		
		store(aEdgeD, new EdgePath(new Point(400, 130), new Point(350, 130), new Point(350, 90), new Point(300, 90)));	
	}
	
	/**
	 * Sets up 5 nodes and 4 edges for testing the Layouter.layoutMergedEndEdges() method.
	 * <ul>
	 * <li> aNodeA, aNodeB, aNodec, aNodeD are all South of endNode </li>
	 * <li> aEdgeA, aEdgeB are inheritance edges connecting aNodeA, aNodeB (respectively) to endNode. </li>
	 * <li> aEdgeC is an implementation edge connecting aNodeC to endNode. </li>
	 * <li> aEdgeD is an association edge connecting aEdgeD to endNode. </li>
	 * </ul>
	 */
	private void setUpLayoutMergedEndEdges()
	{
		endNode = new ClassNode();
		aEdgeA = new GeneralizationEdge(Type.Inheritance);
		aEdgeB = new GeneralizationEdge(Type.Inheritance);
		aEdgeC = new GeneralizationEdge(Type.Implementation);
		aEdgeD = new AssociationEdge();
		for(Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD,endNode))
		{
			aDiagram.addRootNode(node);
		}
		aEdgeA.connect(aNodeA, endNode);
		aEdgeB.connect(aNodeB, endNode);
		aEdgeC.connect(aNodeC, endNode);
		aEdgeD.connect(aNodeD, endNode);
		
		for(Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD))
		{
			aDiagram.addEdge(edge);
		}
		aNodeA.moveTo(new Point(0, 140));
		aNodeB.moveTo(new Point(100, 140));
		aNodeC.moveTo(new Point(200, 140));
		aNodeD.moveTo(new Point(300, 140));
		endNode.moveTo(new Point(0,0));
	}
	
	/**
	 * Sets up 7 edges of different priority types for testing Layouter.layout()
	 */
	private void setUpTestLayout()
	{
		endNode = new ClassNode();
		aEdgeA = new GeneralizationEdge(Type.Inheritance);
		aEdgeB = new GeneralizationEdge(Type.Inheritance);
		aEdgeC = new GeneralizationEdge(Type.Implementation);
		aEdgeD = new AssociationEdge();
		aEdgeE = new AggregationEdge(AggregationEdge.Type.Aggregation);
		aEdgeF = new AggregationEdge(AggregationEdge.Type.Composition);
		aEdgeG = new DependencyEdge();
		for(Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD, aNodeE, aNodeF, aNodeG, endNode))
		{
			aDiagram.addRootNode(node);
		}
		aEdgeA.connect(aNodeA, endNode);
		aEdgeB.connect(aNodeB, endNode);
		aEdgeC.connect(aNodeC, endNode);
		aEdgeD.connect(aNodeD, endNode);
		aEdgeE.connect(aNodeE, endNode);
		aEdgeF.connect(aNodeF, endNode);
		aEdgeG.connect(aNodeG, endNode);
		
		for(Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD, aEdgeE, aEdgeF, aEdgeG))
		{
			aDiagram.addEdge(edge);
		}
		aNodeA.moveTo(new Point(0, 190));
		aNodeB.moveTo(new Point(100, 190));
		aNodeC.moveTo(new Point(200, 190));
		aNodeD.moveTo(new Point(300, 190));
		aNodeE.moveTo(new Point(400, 60));
		aNodeF.moveTo(new Point(400, 0));
		aNodeG.moveTo(new Point(0, 0));
		endNode.moveTo(new Point(150,60));
	}
	
	///// TESTS /////
	
	@Test
	public void testLayout()
	{
		setUpTestLayout();
		aRenderer.layout();
		//aEdgeA
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(50, 190)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(50, 155)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(200, 155)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(200, 120)).get());
		//aEdgeB (the other segments of aEdgeB overlap with aEdgeA)
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(150, 190)).get());
		//aEdgeC
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(250, 190)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(250, 155)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(210, 155)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(210, 120)).get());
		//aEdgeD
		assertEquals(aEdgeD, aRenderer.edgeAt(new Point(350, 190)).get());
		assertEquals(aEdgeD, aRenderer.edgeAt(new Point(350, 145)).get());
		assertEquals(aEdgeD, aRenderer.edgeAt(new Point(220, 145)).get());
		assertEquals(aEdgeD, aRenderer.edgeAt(new Point(220, 120)).get());
		//aEdgeE
		assertEquals(aEdgeE, aRenderer.edgeAt(new Point(400, 90)).get());
		assertEquals(aEdgeE, aRenderer.edgeAt(new Point(250, 90)).get());
		//EdgeF
		assertEquals(aEdgeF, aRenderer.edgeAt(new Point(400, 30)).get());
		assertEquals(aEdgeF, aRenderer.edgeAt(new Point(325, 30)).get());
		assertEquals(aEdgeF, aRenderer.edgeAt(new Point(325, 80)).get());
		assertEquals(aEdgeF, aRenderer.edgeAt(new Point(250, 80)).get());
		//aEdgeG
		assertEquals(aEdgeG, aRenderer.edgeAt(new Point(100, 30)).get());
		assertEquals(aEdgeG, aRenderer.edgeAt(new Point(150, 90)).get());
	}
	
	
	@Test
	public void testLayoutMergedEndEdges()
	{
		setUpLayoutMergedEndEdges();
		//Layout aEdgeA and aEdgeB
		layoutSegmentedEdges(EdgePriority.INHERITANCE);
		assertTrue(contains(aEdgeA));
		assertTrue(contains(aEdgeB));
		assertFalse(contains(aEdgeC));
		assertFalse(contains(aEdgeD));
		//aEdgeA
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(50, 140)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(50, 100)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(50, 60)).get());
		//aEdgeB
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(150, 140)).get());
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(150, 100)).get());
		
		//Layout aEdgeC
		layoutSegmentedEdges(EdgePriority.IMPLEMENTATION);
		assertTrue(contains(aEdgeA));
		assertTrue(contains(aEdgeB));
		assertTrue(contains(aEdgeC));
		assertFalse(contains(aEdgeD));
		//aEdgeC
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(250, 140)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(250, 90)).get());
		
		//Layout aEdgeD
		layoutSegmentedEdges(EdgePriority.ASSOCIATION);
		assertTrue(contains(aEdgeA));
		assertTrue(contains(aEdgeB));
		assertTrue(contains(aEdgeC));
		assertTrue(contains(aEdgeD));
		//aEdgeD
		assertEquals(aEdgeD, aRenderer.edgeAt(new Point(350, 140)).get());
		assertEquals(aEdgeD, aRenderer.edgeAt(new Point(350, 80)).get());
		
	}
	
	@Test
	public void testLayoutSegmentedEdges_aggregation()
	{
		setUpLayoutMergedStartEdges(AggregationEdge.Type.Aggregation);
		layoutSegmentedEdges(EdgePriority.AGGREGATION);
		//aEdgeA
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(200, 90)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(150, 90)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(150, 30)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(100, 30)).get());
		//aEdgeB
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(150, 150)).get());
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(100, 150)).get());
		//aEdgeC
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(300, 100)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(340, 100)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(340, 210)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(400, 210)).get());
		
	}
	
	@Test
	public void testLayoutSegmentedEdges_composition()
	{
		setUpLayoutMergedStartEdges(AggregationEdge.Type.Composition);
		layoutSegmentedEdges(EdgePriority.COMPOSITION);
		//aEdgeA
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(200, 90)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(150, 90)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(150, 30)).get());
		assertEquals(aEdgeA, aRenderer.edgeAt(new Point(100, 30)).get());
		
		//aEdgeB
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(150, 150)).get());
		assertEquals(aEdgeB, aRenderer.edgeAt(new Point(100, 150)).get());
		
		//aEdgeC
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(300, 100)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(340, 100)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(340, 210)).get());
		assertEquals(aEdgeC, aRenderer.edgeAt(new Point(400, 210)).get());
		
	}
	
	@Test
	public void testStoreMergedEndEdges_north()
	{
		setUpMergedEndEdges();
		//aNodeA, aNodeB, and aNodeC are South of aNodeD
		//aEdgeA, aEdgeB, and aEdgeC are incoming on the South side of aNodeD
		aNodeA.moveTo(new Point(0, 100));
		aNodeB.moveTo(new Point(100, 100));
		aNodeC.moveTo(new Point(200, 100));
		aNodeD.moveTo(new Point(100, 0));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		EdgePath expectedPathA = new EdgePath(new Point(50, 100), new Point(50, 80), new Point(150, 80), new Point(150, 60));
		EdgePath expectedPathB = new EdgePath(new Point(150, 100), new Point(150, 80), new Point(150, 80), new Point(150, 60));
		EdgePath expectedPathC = new EdgePath(new Point(250, 100), new Point(250, 80), new Point(150, 80), new Point(150, 60));
		storeMergedEndEdges(Side.TOP, edgesToMerge);
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testStoreMergedEndEdges_south()
	{
		setUpMergedEndEdges();
		//aNodeA, aNodeB, and aNodeC are Nouth of aNodeD
		//aEdgeA, aEdgeB, and aEdgeC are incoming on the Nouth side of aNodeD
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(200, 0));
		aNodeD.moveTo(new Point(100, 100));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		EdgePath expectedPathA = new EdgePath(new Point(50, 60), new Point(50, 80), new Point(150, 80), new Point(150, 100));
		EdgePath expectedPathB = new EdgePath(new Point(150, 60), new Point(150, 80), new Point(150, 80), new Point(150, 100));
		EdgePath expectedPathC = new EdgePath(new Point(250, 60), new Point(250, 80), new Point(150, 80), new Point(150, 100));
		storeMergedEndEdges(Side.BOTTOM, edgesToMerge);
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testStoreMergedEndEdges_east()
	{
		setUpMergedEndEdges();
		//aNodeA, aNodeB, and aNodeC are all to the West of aNodeD.
		//aEgdeA, aEdgeB and aEdgeC are incoming on the West side of aNodeD.
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 60));
		aNodeC.moveTo(new Point(0, 120));
		aNodeD.moveTo(new Point(200, 60));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		storeMergedEndEdges(Side.RIGHT, edgesToMerge);
		EdgePath expectedPathA = new EdgePath(new Point(100, 30), new Point(150, 30), new Point(150, 90), new Point(200, 90));
		EdgePath expectedPathB = new EdgePath(new Point(100, 90), new Point(150, 90), new Point(150, 90), new Point(200, 90));
		EdgePath expectedPathC = new EdgePath(new Point(100, 150), new Point(150, 150), new Point(150, 90), new Point(200, 90));
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testStoreMergedEndEdges_west()
	{
		setUpMergedEndEdges();
		//aNodeA, aNodeB, and aNodeC are all to the East of aNodeD.
		//aEgdeA, aEdgeB and aEdgeC are incoming on the East side of aNodeD.
		aNodeA.moveTo(new Point(200, 0));
		aNodeB.moveTo(new Point(200, 60));
		aNodeC.moveTo(new Point(200, 120));
		aNodeD.moveTo(new Point(0, 60));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		storeMergedEndEdges(Side.LEFT, edgesToMerge);
		EdgePath expectedPathA = new EdgePath(new Point(200, 30), new Point(150, 30), new Point(150, 90), new Point(100, 90));
		EdgePath expectedPathB = new EdgePath(new Point(200, 90), new Point(150, 90), new Point(150, 90), new Point(100, 90));
		EdgePath expectedPathC = new EdgePath(new Point(200, 150), new Point(150, 150), new Point(150, 90), new Point(100, 90));
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testStoreMergedStartEdges_north()
	{
		setUpMergedStartEdges();
		//aNodeA, aNodeB, and aNodeC are all to the North of aNodeD.
		//aEgdeA, aEdgeB and aEdgeC are outgoing from the North side of aNodeD.
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(200, 0));
		aNodeD.moveTo(new Point(100, 100));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		storeMergedStartEdges(Side.TOP, edgesToMerge);
		EdgePath expectedPathA = new EdgePath(new Point(150, 100), new Point(150, 80), new Point(50, 80), new Point(50, 60));
		EdgePath expectedPathB = new EdgePath(new Point(150, 100), new Point(150, 80), new Point(150, 80), new Point(150, 60));
		EdgePath expectedPathC = new EdgePath(new Point(150, 100), new Point(150, 80), new Point(250, 80), new Point(250, 60));
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	

	@Test
	public void testStoreMergedStartEdges_south()
	{
		setUpMergedStartEdges();
		//aNodeA, aNodeB, and aNodeC are all to the South of aNodeD.
		//aEgdeA, aEdgeB and aEdgeC are outgoing from the South side of aNodeD.
		aNodeA.moveTo(new Point(0, 100));
		aNodeB.moveTo(new Point(100, 100));
		aNodeC.moveTo(new Point(200, 100));
		aNodeD.moveTo(new Point(100, 0));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		storeMergedStartEdges(Side.BOTTOM, edgesToMerge);
		EdgePath expectedPathA = new EdgePath(new Point(150, 60), new Point(150, 80), new Point(50, 80), new Point(50, 100));
		EdgePath expectedPathB = new EdgePath(new Point(150, 60), new Point(150, 80), new Point(150, 80), new Point(150, 100));
		EdgePath expectedPathC = new EdgePath(new Point(150, 60), new Point(150, 80), new Point(250, 80), new Point(250, 100));
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testStoreMergedStartEdges_west()
	{
		setUpMergedStartEdges();
		//aNodeA, aNodeB, and aNodeC are all to the West of aNodeD.
		//aEgdeA, aEdgeB and aEdgeC are outgoing from the West side of aNodeD.
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 100));
		aNodeC.moveTo(new Point(0, 200));
		aNodeD.moveTo(new Point(200, 100));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		storeMergedStartEdges(Side.LEFT, edgesToMerge);
		EdgePath expectedPathA = new EdgePath(new Point(200, 130), new Point(150, 130), new Point(150, 30), new Point(100, 30));
		EdgePath expectedPathB = new EdgePath(new Point(200, 130), new Point(150, 130), new Point(150, 130), new Point(100, 130));
		EdgePath expectedPathC = new EdgePath(new Point(200, 130), new Point(150, 130), new Point(150, 230), new Point(100, 230));
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testStoreMergedStartEdges_east()
	{
		setUpMergedStartEdges();
		//aNodeA, aNodeB, and aNodeC are all to the East of aNodeD.
		//aEgdeA, aEdgeB and aEdgeC are outgoing from the East side of aNodeD.
		aNodeA.moveTo(new Point(200, 0));
		aNodeB.moveTo(new Point(200, 100));
		aNodeC.moveTo(new Point(200, 200));
		aNodeD.moveTo(new Point(0, 100));
		List<Edge> edgesToMerge = Arrays.asList(aEdgeA, aEdgeB, aEdgeC);
		storeMergedStartEdges(Side.RIGHT, edgesToMerge);
		EdgePath expectedPathA = new EdgePath(new Point(100, 130), new Point(150, 130), new Point(150, 30), new Point(200, 30));
		EdgePath expectedPathB = new EdgePath(new Point(100, 130), new Point(150, 130), new Point(150, 130), new Point(200, 130));
		EdgePath expectedPathC = new EdgePath(new Point(100, 130), new Point(150, 130), new Point(150, 230), new Point(200, 230));
		assertEquals(expectedPathA, getStoredEdgePath(aEdgeA));
		assertEquals(expectedPathB, getStoredEdgePath(aEdgeB));
		assertEquals(expectedPathC, getStoredEdgePath(aEdgeC));
	}
	
	@Test
	public void testLayoutDependencyEdges_noOtherEdges()
	{
		setUpDependencyEdges();
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(100, 120));
		layoutDependencyEdges();
		assertEquals(new EdgePath(new Point(150, 120), new Point(50, 60)), getStoredEdgePath(dependencyEdge));
	}
	
	@Test
	public void testLayoutDependencyEdges_otherEdgePresent()
	{
		setUpDependencyEdges();
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(200, 0));
		store(generalizationEdge, new EdgePath(new Point(200, 90), new Point(150, 90), new Point(150, 30), new Point(100, 30)));
		layoutDependencyEdges();
		assertEquals(new EdgePath(new Point(200, 30), new Point(100, 40)), getStoredEdgePath(dependencyEdge));
	}
	
	@Test
	public void testLayoutSelfEdges()
	{
		Edge selfEdge = new AggregationEdge();
		Edge nonSelfEdge = new AggregationEdge();
		selfEdge.connect(aNodeA, aNodeA);
		nonSelfEdge.connect(aNodeA, aNodeB);
		aDiagram.addEdge(selfEdge);
		aDiagram.addEdge(nonSelfEdge);
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aNodeA.moveTo(new Point(20, 20));
		aNodeB.moveTo(new Point(20, 300));
		layoutSelfEdges();
		EdgePath expectedPath = new EdgePath(new Point(100, 20), new Point(100, 0), new Point(140, 0), new Point(140, 40), new Point(120, 40));
		assertEquals(expectedPath, getStoredEdgePath(selfEdge));
		assertFalse(contains(nonSelfEdge));
	}
	
	@Test
	public void testBuildSelfEdge_topRight()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node);
		EdgePath expected = new EdgePath(new Point(100, 20), new Point(100, 0), new Point(140, 0), new Point(140, 40), new Point(120, 40));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.TOP_RIGHT));
	}
	
	@Test
	public void testBuildSelfEdge_topLeft()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node);
		EdgePath expected = new EdgePath(new Point(40, 20), new Point(40, 0), new Point(0, 0), new Point(0, 40), new Point(20, 40));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.TOP_LEFT));
	}
	
	@Test
	public void testBuildSelfEdge_bottomLeft()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node);
		EdgePath expected = new EdgePath(new Point(40, 80), new Point(40, 100), new Point(0, 100), new Point(0, 60), new Point(20, 60));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.BOTTOM_LEFT));
	}
	
	@Test
	public void testBuildSelfEdge_bottomRight()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node);
		EdgePath expected = new EdgePath(new Point(100, 80), new Point(100, 100), new Point(140, 100), new Point(140, 60), new Point(120, 60));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.BOTTOM_RIGHT));
	}
	
	@Test
	public void testGetSelfEdgeCorner()
	{
		Node node = new ClassNode();
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node);
		node.moveTo(new Point(100, 100));
		aDiagram.addRootNode(node);
		aDiagram.addEdge(selfEdge);
		aEdgeA.connect(node, aNodeB);
		aEdgeB.connect(aNodeB, node);
		aEdgeC.connect(aNodeC, node);
		aEdgeD.connect(node, aNodeD);
		aDiagram.addEdge(aEdgeA);
		aDiagram.addEdge(aEdgeB);
		aDiagram.addEdge(aEdgeC);
		aDiagram.addEdge(aEdgeD);
		//Create EdgePaths which connect to each node corner:
		//the exact EdgePaths are irrelevant, but the edgePath must start or end at one of selfEdge's connection points
		EdgePath connectedToTopRight = new EdgePath(new Point(180, 100), new Point(180, 80), new Point(220, 80), new Point(200, 120));
		EdgePath connectedToTopLeft =  new EdgePath(new Point(120, 100), new Point(100, 100), new Point(100, 80), new Point(80, 80));
		EdgePath connectedToBottomLeft = new EdgePath(new Point(100, 140), new Point(80, 140), new Point(80, 100), new Point(60, 100));
		EdgePath connectedToBottomRight = new EdgePath(new Point(180, 160), new Point(200, 160), new Point(200, 200), new Point(220, 200));
		
		//Start out with all nodeCorner connection points being available
		assertEquals(NodeCorner.TOP_RIGHT, getSelfEdgeCorner(selfEdge));
		
		//make top-right corner unavailable: 
		store(aEdgeA, connectedToTopRight);
		assertEquals(NodeCorner.TOP_LEFT, getSelfEdgeCorner(selfEdge));
		
		//make top-left corner also unavailable:
		store(aEdgeB, connectedToTopLeft);
		assertEquals(NodeCorner.BOTTOM_LEFT, getSelfEdgeCorner(selfEdge));
		
		//make bottom-left corner unavailable:
		store(aEdgeC, connectedToBottomLeft);
		assertEquals(NodeCorner.BOTTOM_RIGHT, getSelfEdgeCorner(selfEdge));
		
		//make bottom right corner unavailable:
		store(aEdgeD, connectedToBottomRight);
		assertEquals(NodeCorner.TOP_RIGHT, getSelfEdgeCorner(selfEdge));
	}
	
	@Test
	public void testGetEdgesToMergeStart()
	{
		Node startNode = new ClassNode();
		Node nodeA = new ClassNode();
		Node nodeB = new ClassNode();
		Node nodeC = new ClassNode();
		Node nodeD = new ClassNode();
		Node nodeE = new ClassNode();
		Node nodeF = new ClassNode();
		Edge edgeA = new AggregationEdge();
		Edge edgeB = new AggregationEdge();
		Edge edgeC = new GeneralizationEdge();
		AggregationEdge edgeD = new AggregationEdge();
		Edge edgeE = new AggregationEdge();
		Edge edgeF = new AggregationEdge();
		startNode.moveTo(new Point(200, 120));
		//edgeA should not be in the resulting list of edges to merge (it is pEdge)
		nodeA.moveTo(new Point(0, 0));
		edgeA.connect(startNode, nodeA);
		
		//edgeB should merge
		nodeB.moveTo(new Point(0, 100));
		edgeB.connect(startNode, nodeB);
		
		//edgeC should not merge (its has a different EdgePriority)
		nodeC.moveTo(new Point(0, 200));
		edgeC.connect(nodeC, startNode);
		store(edgeC, new EdgePath(new Point(100, 250), new Point(150, 250), new Point(150, 250), new Point(200, 250)));
		
		//edgeD should not merge (start label)
		nodeD.moveTo(new Point(0, 300));
		edgeD.connect(startNode, nodeD);
		edgeD.setStartLabel("label");
		
		//edgeE should not merge (it is not outgoing from startNode)
		nodeE.moveTo(new Point(0, 400));
		edgeE.connect(nodeE, startNode);
		
		//edgeF should not connect (it has a different attachment side)
		nodeF.moveTo(new Point(200, 210));
		edgeF.connect(startNode, nodeF);
		
		aDiagram.addRootNode(startNode);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addRootNode(nodeC);
		aDiagram.addRootNode(nodeD);
		aDiagram.addRootNode(nodeE);
		aDiagram.addRootNode(nodeF);
		aDiagram.addEdge(edgeA);
		aDiagram.addEdge(edgeB);
		aDiagram.addEdge(edgeC);
		aDiagram.addEdge(edgeD);
		aDiagram.addEdge(edgeE);
		aDiagram.addEdge(edgeF);
		
		List<Edge> edges = new ArrayList<>();
		edges.addAll(Arrays.asList(edgeA, edgeB, edgeC, edgeD, edgeE, edgeF));
		Collection<Edge> result = getEdgesToMergeStart(edgeA, edges);
		assertEquals(1, result.size());
		assertTrue(result.contains(edgeB));
	}
	
	@Test
	public void testGetEdgesToMergeEnd()
	{
		//Create a diagram with 6 edges incoming on endNode and determine which edges should merge with edgeD
		endNode = new ClassNode();
		Node nodeA = new ClassNode();
		Node nodeB = new ClassNode();
		Node nodeC = new ClassNode();
		Node nodeD = new ClassNode();
		Node nodeE = new ClassNode();
		Node nodeF = new ClassNode();
		Edge edgeA = new GeneralizationEdge(Type.Implementation);
		Edge edgeB = new GeneralizationEdge(Type.Implementation);
		Edge edgeC = new GeneralizationEdge(Type.Implementation);
		Edge edgeD = new GeneralizationEdge(Type.Implementation);
		Edge edgeE = new GeneralizationEdge(Type.Inheritance);
		Edge edgeF = new GeneralizationEdge(Type.Implementation);
		endNode.moveTo(new Point(250, 0));
		//edgeA should not merge: different attachment side
		nodeA.moveTo(new Point(0,0));
		edgeA.connect(nodeA, endNode);
		
		//edgeB should not merge: different direction
		nodeB.moveTo(new Point(0,180));
		edgeB.connect(endNode, nodeB);
		
		//edgeC should merge
		nodeC.moveTo(new Point(100,180));
		edgeC.connect(nodeC, endNode);
		
		//edgeD is the edge to merge (so it should not be included in the resulting list)
		nodeD.moveTo(new Point(200, 180));
		edgeD.connect(nodeD, endNode);
		
		//edgeE should not merge: (it is a different priority type)
		nodeE.moveTo(new Point(300, 180));
		edgeE.connect(nodeE, endNode);
		store(edgeE, new EdgePath(new Point(350, 180), new Point(350, 120), new Point(300, 120), new Point(300, 60)));
		
		//edgeF should not merge: it there is another edge (edgeE) in between it and edgeD
		nodeF.moveTo(new Point(400, 180));
		edgeF.connect(nodeF, endNode);
		
		aDiagram.addRootNode(endNode);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addRootNode(nodeC);
		aDiagram.addRootNode(nodeD);
		aDiagram.addRootNode(nodeE);
		aDiagram.addRootNode(nodeF);
		aDiagram.addEdge(edgeA);
		aDiagram.addEdge(edgeB);
		aDiagram.addEdge(edgeC);
		aDiagram.addEdge(edgeD);
		aDiagram.addEdge(edgeE);
		aDiagram.addEdge(edgeF);
		List<Edge> edges = new ArrayList<>();
		edges.addAll(Arrays.asList(edgeA, edgeB, edgeC, edgeD, edgeE, edgeF));
		Collection<Edge> result = getEdgesToMergeEnd(edgeD, edges);
		assertEquals(1, result.size());
		assertTrue(result.contains(edgeC));
	}
	
	@Test
	public void testStoredConflictingEdges()
	{
		endNode = new ClassNode();
		for(Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD, endNode))
		{
			aDiagram.addRootNode(node);
		}
		aEdgeA = new GeneralizationEdge(Type.Implementation);
		aEdgeB = new GeneralizationEdge(Type.Inheritance);
		aEdgeC = new AssociationEdge();
		aEdgeD = new DependencyEdge();
		aEdgeA.connect(aNodeA, endNode);
		aEdgeB.connect(aNodeB, endNode);
		aEdgeC.connect(aNodeC, endNode);
		aEdgeD.connect(aNodeD, endNode);
		for(Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD))
		{
			aDiagram.addEdge(edge);
		}
		aNodeA.moveTo(new Point(0, 100));
		aNodeB.moveTo(new Point(150, 100));
		aNodeC.moveTo(new Point(270, 100));
		aNodeD.moveTo(new Point(270, 0));
		endNode.moveTo(new Point(100, 0));
		
		//aNodeA and aNodeB are South of endNode
		//aNodeC is South-East of endNode
		//aNodeD is East of endNode
		//aEgdeA, aEdgeB, and aEdgeD are stored edges incoming on endNode. pEdge is aEdgeC, which is not yet in storage. 
		store(aEdgeA, new EdgePath(new Point(50, 100), new Point(50, 80), new Point(140, 80), new Point(140, 60)));
		store(aEdgeB, new EdgePath(new Point(200, 100), new Point(200, 80), new Point(150, 80), new Point(150, 60)));
		store(aEdgeD, new EdgePath(new Point(270, 30), new Point(200, 30)));
		
		List<Edge> conflictingEdges = storedConflictingEdges(Side.BOTTOM, endNode, aEdgeC);
		assertTrue(conflictingEdges.size() == 1);
		assertTrue(conflictingEdges.contains(aEdgeB));
		
		
	}
	
	@Test
	public void testNodeIsCloserThanSegment_north()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(50, 120));
		aNodeC.moveTo(new Point(150, 120));
		aNodeD.moveTo(new Point(250, 50));
		//aNodeB and aNodeC are South of aNodeA; aNodeD is East of aNodeA
		store(aEdgeA, new EdgePath(new Point(100, 120), new Point(100, 90), new Point(50, 90), new Point(50, 60)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, Side.TOP));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, Side.TOP));
	}
	
	@Test
	public void testNodeIsCloserThanSegment_south()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(0, 110));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(200, 0));
		aNodeD.moveTo(new Point(300, 60));
		//aNodeB and aNodeC are North-East of aNodeA; aNodeD is North-North-East of aNodeA
		store(aEdgeA, new EdgePath(new Point(50, 110), new Point(50, 95), new Point(150, 95), new Point(150, 60)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, Side.BOTTOM));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, Side.BOTTOM));
	}
	
	@Test
	public void testNodeIsCloserThanSegment_east()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(220, 0));
		aNodeB.moveTo(new Point(0, 10));
		aNodeC.moveTo(new Point(0, 70));
		aNodeD.moveTo(new Point(60, 70));
		//aNodeB and aNodeC are SouthWest of aNodeA; aNodeD is South-SouthWest of aNodeA
		store(aEdgeA, new EdgePath(new Point(100, 40), new Point(160, 40), new Point(160, 30), new Point(220, 30)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, Side.RIGHT));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, Side.RIGHT));
	}
	
	@Test
	public void testNodeIsCloserThanSegment_west()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 10));
		aNodeC.moveTo(new Point(200, 40));
		aNodeD.moveTo(new Point(140, 60));
		//aNodeB and aNodeC are East of aNodeA; aNodeD is South-East of aNodeA
		store(aEdgeA, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, Side.LEFT));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, Side.LEFT));
	}
	
	
	/*
	 * Tests the condition where there is an edge in storage which shares the same two attached nodes as pEdge.
	 */
	@Test
	public void testGetHorizontalMidline_sharedNodeEdge()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		Edge storedEdge = new GeneralizationEdge();
		Edge newEdge = new AssociationEdge();
		storedEdge.connect(aNodeA, aNodeB);
		newEdge.connect(aNodeA, aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(20, 120));
		store(storedEdge, new EdgePath(new Point(50, 60), new Point(50, 90), new Point(70, 90), new Point(70, 120)));
		Point newEdgeStart = new Point(60, 60);
		Point newEdgeEnd = new Point(80, 120);
		assertEquals(100, getHorizontalMidLine(newEdgeStart, newEdgeEnd, Side.BOTTOM, newEdge));
	}
	
	

	/*
	 * Tests the condition where there are no edges in storage connected to pNode.getStart() or pNode.getEnd().
	 * In this case, getHorizontalMidLine() can return the Y-coordinate directly in between pStart and pEnd.
	 */
	@Test
	public void testGetHorizontalMidline_noSharedEdges()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(20, 120));
		Edge newEdge = new GeneralizationEdge();
		newEdge.connect(aNodeA, aNodeB);
		aDiagram.addEdge(newEdge);
		assertEquals(90, getHorizontalMidLine(new Point(50, 60), new Point(70, 120), Side.BOTTOM, newEdge));
	}
	
	@Test
	public void testGetHorizontalMidline_storedEdgePresent()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge storedEdge = new GeneralizationEdge();
		Edge associationEdge = new AssociationEdge();
		Edge newAggregationEdge = new AggregationEdge();
		storedEdge.connect(aNodeB, aNodeA);
		associationEdge.connect(aNodeC, aNodeA);
		newAggregationEdge.connect(aNodeC, aNodeA);
		aDiagram.addEdge(newAggregationEdge);
		aDiagram.addEdge(associationEdge);
		//aNodeB and aNodeC are beside each other, and below aNodeA.
		//storedEdge connects aNodeB to aNodeA
		//newSharedStartEdge connects aNodeC to aNodeA
		//newSharedEndEdge connects aNodeC to aNodeA
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(10, 120));
		aNodeC.moveTo(new Point(110, 120));
		Point startPoint = new Point(160, 120);
		Point endPoint = new Point(50, 60);
		//Because storedEdge is stored, the horizontal mid-segment of newEdge is shifted 10px 
		store(storedEdge, new EdgePath(new Point(60, 120), new Point(60, 90), new Point(50, 90), new Point(50, 60)));
		assertEquals(80, getHorizontalMidLine(startPoint, endPoint, Side.TOP, associationEdge));
		assertEquals(80, getHorizontalMidLine(startPoint, endPoint, Side.TOP, newAggregationEdge));
	}
	
	/*
	 * Tests the condition where there is an edge in storage which shares the same two attached nodes as pEdge.
	 */
	@Test
	public void getVerticalMidLine_sharedNodeEdge()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		Edge storedEdge = new GeneralizationEdge();
		Edge newEdge = new AssociationEdge();
		storedEdge.connect(aNodeA, aNodeB);
		newEdge.connect(aNodeA, aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 20));
		store(storedEdge, new EdgePath(new Point(100, 30), new Point(150, 30), new Point(150, 50), new Point(200, 50)));
		Point newEdgeStart = new Point(100, 40);
		Point newEdgeEnd = new Point(200, 60);
		assertEquals(160, getVerticalMidLine(newEdgeStart, newEdgeEnd, Side.RIGHT, newEdge));
	}
	
	/*
	 * Tests the condition where there are no edges in storage connected to pNode.getStart() or pNode.getEnd().
	 * In this case, getVerticalMidLine() can return the X-coordinate directly in between pStart and pEnd.
	 */
	@Test
	public void testGetVerticalMidLine_noStoredEdges()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(20, 120));
		Edge newEdge = new GeneralizationEdge();
		newEdge.connect(aNodeA, aNodeB);
		aDiagram.addEdge(newEdge);
		assertEquals(150, getVerticalMidLine(new Point(100, 30), new Point(200, 50), Side.RIGHT, newEdge));
	}
	
	@Test
	public void testGetVerticalMidLine_storedEdgePresent()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge storedEdge = new GeneralizationEdge();
		Edge associationEdge = new AssociationEdge();
		Edge newAggregationEdge = new AggregationEdge();
		storedEdge.connect(aNodeB, aNodeA);
		associationEdge.connect(aNodeC, aNodeA);
		newAggregationEdge.connect(aNodeC, aNodeA);
		aDiagram.addEdge(newAggregationEdge);
		aDiagram.addEdge(associationEdge);
		//aNodeB and aNodeC are both to the left of aNodeA
		//storedEdge connects aNodeA <--- aNodeB
		//newSharedStartEdge connects aNodeA <--- aNodeC 
		//newSharedEndEdge connects aNodeA <--- aNodeC 
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 10));
		aNodeC.moveTo(new Point(200, 60));
		assertEquals(150, getVerticalMidLine(new Point(200, 90), new Point(100, 40), Side.LEFT, associationEdge));
		assertEquals(150, getVerticalMidLine(new Point(200, 90), new Point(100, 40), Side.RIGHT, newAggregationEdge));
		//after storedEdge is stored, the vertical mid-segment of newEdge is shifted 10px
		store(storedEdge, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		assertEquals(140, getVerticalMidLine(new Point(200, 90), new Point(100, 40), Side.LEFT, associationEdge));
		assertEquals(140, getVerticalMidLine(new Point(200, 90), new Point(100, 40), Side.LEFT, newAggregationEdge));
	}
	
	@Test
	public void testHorizontalMidlineForSharedNodeEdges_aggregationAndGeneralizationEdges()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		Edge storedEdge = new GeneralizationEdge();
		storedEdge.connect(aNodeA, aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(20, 120));
		store(storedEdge, new EdgePath(new Point(50, 60), new Point(50, 90), new Point(70, 90), new Point(70, 120)));
		Edge associationEdgeNorth = new AssociationEdge();
		Edge associationEdgeSouth = new AssociationEdge();
		Edge aggregationEdgeNorth = new AggregationEdge();
		Edge aggregationEdgeSouth = new AggregationEdge();
		associationEdgeSouth.connect(aNodeA, aNodeB);
		associationEdgeNorth.connect(aNodeB, aNodeA);
		aggregationEdgeSouth.connect(aNodeA, aNodeB);
		aggregationEdgeNorth.connect(aNodeB, aNodeA);
		assertEquals(100, horizontalMidlineForSharedNodeEdges(storedEdge, associationEdgeSouth, Side.BOTTOM));
		assertEquals(80, horizontalMidlineForSharedNodeEdges(storedEdge, associationEdgeNorth, Side.TOP));
		assertEquals(100, horizontalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeSouth, Side.BOTTOM));
		assertEquals(80, horizontalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeNorth, Side.TOP));
	}
	
	@Test
	public void testVerticalMidlineForSharedNodeEdges_generalizationAndAssociationEdges()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		Edge storedEdge = new GeneralizationEdge();
		storedEdge.connect(aNodeA, aNodeB);
		//aNodeA is to the left of aNodeB
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 20));
		store(storedEdge, new EdgePath(new Point(100, 30), new Point(150, 30), new Point(150, 50), new Point(200, 50)));
		Edge associationEdgeEast = new AssociationEdge();
		Edge associationEdgeWest = new AssociationEdge();
		Edge aggregationEdgeEast = new AggregationEdge();
		Edge aggregationEdgeWest = new AggregationEdge();
		associationEdgeEast.connect(aNodeA, aNodeB);
		associationEdgeWest.connect(aNodeB, aNodeA);
		aggregationEdgeEast.connect(aNodeA, aNodeB);
		aggregationEdgeWest.connect(aNodeB, aNodeA);
		assertEquals(160, verticalMidlineForSharedNodeEdges(storedEdge, associationEdgeEast, Side.RIGHT));
		assertEquals(140, verticalMidlineForSharedNodeEdges(storedEdge, associationEdgeWest, Side.LEFT));
		assertEquals(160, verticalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeEast, Side.RIGHT));
		assertEquals(140, verticalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeWest, Side.LEFT));
	
	}
	

	
	@Test
	public void testClosestVerticalSegment_generalizationEdge()
	{
		setUpThreeConnectedNodes();
		//re-position the nodes so that aNodeA is to the left of aNodeB and aNodeC
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 10));
		aNodeC.moveTo(new Point(200, 60));
		//initialize a new edge which will not be in storage:
		Node startNode = new ClassNode();
		startNode.moveTo(new Point(200, 70));
		Edge newEdge = new GeneralizationEdge();
		newEdge.connect(startNode, aNodeA);
		//Without any conflicting edges in storage: returns empty
		assertEquals(Optional.empty(), closestConflictingVerticalSegment(Side.LEFT, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		store(aEdgeB, new EdgePath(new Point(200, 90), new Point(140, 90), new Point(140, 40), new Point(100, 40)));
		assertEquals(aEdgeB, closestConflictingVerticalSegment(Side.LEFT, newEdge).get());
	}
	
	@Test
	public void testClosestVerticalSegment_aggregationEdge()
	{
		setUpThreeConnectedNodes();
		//re-position the nodes so that aNodeA is to the left of aNodeB and aNodeC
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 10));
		aNodeC.moveTo(new Point(200, 60));
		//initialize a new edge which will not be in storage:
		Node startNode = new ClassNode();
		startNode.moveTo(new Point(200, 70));
		AggregationEdge newEdge = new AggregationEdge();
		newEdge.connect(startNode, aNodeA);
		//Without any conflicting edges in storage: returns empty
		assertEquals(Optional.empty(), closestConflictingVerticalSegment(Side.LEFT, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		store(aEdgeB, new EdgePath(new Point(200, 90), new Point(140, 90), new Point(140, 40), new Point(100, 40)));
		assertEquals(aEdgeA, closestConflictingVerticalSegment(Side.LEFT, newEdge).get());
	}
	
	@Test
	public void testClosestHorizontalSegment_generalizationEdge()
	{
		setUpThreeConnectedNodes();
		//Positions of nodes in this scenario: (x,y)
			//aNodeA: 100, 140
			//aNodeB: 110, 300
			//aNodeC: 200, 300
		Node startNode = new ClassNode();
		startNode.moveTo(new Point(300, 300));
		Edge newEdge = new GeneralizationEdge();
		newEdge.connect(startNode, aNodeA);
		assertEquals(Optional.empty(), closestConflictingHorizontalSegment(Side.TOP, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(160, 300), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		store(aEdgeB, new EdgePath(new Point(250, 300), new Point(250, 240), new Point(150, 240), new Point(150, 200)));
		assertEquals(aEdgeB, closestConflictingHorizontalSegment(Side.TOP, newEdge).get());
	}
	
	@Test
	public void testClosestHorizontalSegment_aggregationEdge()
	{
		setUpThreeConnectedNodes();
		//Positions of nodes in this scenario: (x,y)
			//aNodeA: 100, 140
			//aNodeB: 110, 300
			//aNodeC: 200, 300
		Node startNode = new ClassNode();
		startNode.moveTo(new Point(300, 300));
		AggregationEdge newEdge = new AggregationEdge();
		newEdge.connect(startNode, aNodeA);
		assertEquals(Optional.empty(), closestConflictingHorizontalSegment(Side.TOP, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(160, 300), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		store(aEdgeB, new EdgePath(new Point(250, 300), new Point(250, 240), new Point(150, 240), new Point(150, 200)));
		assertEquals(aEdgeA, closestConflictingHorizontalSegment(Side.TOP, newEdge).get());
	}
	
	@Test
	public void testAdjacentHorizontalMidLine_below_generalizationEdge()
	{
		//aNodeA is above and to the left of aNodeB and aNodeC.
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new GeneralizationEdge();
		Edge edge3 = new GeneralizationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(200,300));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(0, 0));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(250, 300), new Point(250, 180), new Point(150, 180), new Point(150, 60)));
		
		//edge2 is incoming on aNodeA and its EdgeDirection is BOTTOM, so it's middle segment should be 10px below pClosestStoredEdge.
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge2, Side.BOTTOM));
		
		//edge3 is outgoing from aNodeA and its EdgeDirection is TOP, so it's middle segment should be 10px below pClosestStoredEdge.
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge3, Side.TOP));
	}
	
	
	@Test
	public void testAdjacentHorizontalMidLine_above_generalizationEdge()
	{
		//aNodeA is above and to the left of aNodeB and aNodeC.
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new GeneralizationEdge();
		Edge edge3 = new GeneralizationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(100, 200));
		aNodeC.moveTo(new Point(200, 200));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(150, 200), new Point(150, 130), new Point(50, 130), new Point(50, 60)));
		
		//edge2 is incoming on aNodeA and its EdgeDirection is TOP, so it's middle segment should be 10px above pClosestStoredEdge.
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge2, Side.TOP));
		
		//edge3 is outgoing from aNodeA and its EdgeDirection is BOTTOM, so it's middle segment should be 10px above pClosestStoredEdge.
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge3, Side.BOTTOM));
	}
	
	@Test
	public void testAdjacentHorizontalMidLine_below_aggregationEdge()
	{
		//aNodeA is above and to the left of aNodeB and aNodeC.
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new AggregationEdge();
		Edge edge3 = new AggregationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(100, 200));
		aNodeC.moveTo(new Point(200, 200));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(150, 200), new Point(150, 130), new Point(50, 130), new Point(50, 60)));
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge2, Side.TOP));
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge3, Side.BOTTOM));
	}
	
	@Test
	public void testAdjacentHorizontalMidLine_above_aggregationEdge()
	{
		//aNodeA is above and to the left of aNodeB and aNodeC.
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new AggregationEdge();
		Edge edge3 = new AggregationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(200,300));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(0, 0));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(250, 300), new Point(250, 180), new Point(150, 180), new Point(150, 60)));
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge2, Side.BOTTOM));
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge3, Side.TOP));
	}
	
	/*
	 * Tests the scenario where the vertical mid-line for a GeneralizationEdge edge should be 10px to the right
	 * of the vertical middle segment of pClosestStoredEdge.
	 */
	@Test
	public void testAdjacentVerticalMidLine_right_generalizationEdge()
	{
		//aNodeB and aNodeC are to the left and slightly above from aNodeA. 
		//edge1 connects aNodeB ---> aNodeA
		//edge2 connects aNodeC ---> aNodeA
		//edge3 connects aNodeC <--- aNodeA
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new GeneralizationEdge();
		Edge edge3 = new GeneralizationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(0, 100));
		aNodeC.moveTo(new Point(0, 0));
		//Store the path for edge1:
		store(edge1, new EdgePath(new Point(300, 300), new Point(200, 330), new Point(200, 130), new Point(100, 130)));
		
		//With edge2 incoming on aNodeA: the vertical mid-line of edge2 should be 10px to the right of the vertical mid-line of edge1
		assertEquals(210, adjacentVerticalMidLine(edge1, edge2, Side.RIGHT));
		
		//With edge3 outgoing from aNodeA: the vertical mid-line of edge3 should be 10px to the right of the vertical mid-line of edge1
		assertEquals(210, adjacentVerticalMidLine(edge1, edge3, Side.LEFT));
				
	}
	
	/*
	 * Tests the situation when a GeneralizationEdge edge should have a vertical middle segment which is 10px 
	 * to the left of the vertical middle segment of pClosestStoredEdge.
	 */
	@Test
	public void testAdjacentVerticalMidLine_left_generalizationEdge()
	{
		//aNodeB and aNodeC are to the right and slightly above from aNodeA. 
		//edge1 connects aNodeA <--- aNodeB
		//edge2 connects aNodeA <--- aNodeC
		//edge3 connects aNodeA ---> aNodeC
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new GeneralizationEdge();
		Edge edge3 = new GeneralizationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0, 400));
		aNodeB.moveTo(new Point(300, 200));
		aNodeC.moveTo(new Point(300, 100));
		//Add the path of edge1 to storage
		store(edge1, new EdgePath(new Point(100, 430), new Point(200, 430), new Point(200, 230), new Point(300, 230)));
		
		//With edge2 incoming on aNodeA: the vertical mid-line of edge2 should be 10px to the left of the vertical mid-line of edge1
		assertEquals(190, adjacentVerticalMidLine(edge1, edge2, Side.LEFT));
		
		//With edge3 outgoing from aNodeA: the vertical mid-line of edge2 should be 10px to the left of the vertical mid-line of edge1
		assertEquals(190, adjacentVerticalMidLine(edge1, edge3, Side.RIGHT));
	}
	
	/*
	 * Tests the scenario when an AggregationEdge edge should have a vertical middle segment which is 
	 * 10px to the right of the vertical middle segment of pClosestStoredEdge.
	 */
	@Test
	public void testAdjacentVerticalMidLine_right_aggregationEdge()
	{
		//aNodeB and aNodeC are to the right and slightly above from aNodeA. 
		//edge1 connects aNodeA <--- aNodeB
		//edge2 connects aNodeA <--- aNodeC
		//edge3 connects aNodeA ---> aNodeC
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new AggregationEdge();
		Edge edge3 = new AggregationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0, 400));
		aNodeB.moveTo(new Point(300, 200));
		aNodeC.moveTo(new Point(300, 100));
		//Add the path of edge1 to storage
		store(edge1, new EdgePath(new Point(100, 430), new Point(200, 430), new Point(200, 230), new Point(300, 230)));
		assertEquals(190, adjacentVerticalMidLine(edge1, edge2, Side.LEFT));
		assertEquals(190, adjacentVerticalMidLine(edge1, edge3, Side.RIGHT));
	}
	
	/*
	 * Tests the scenario when an AggregationEdge edge should have a vertical middle segment which is 
	 * 10px to the left of the vertical middle segment of pClosestStoredEdge.
	 */
	@Test
	public void testAdjacentVerticalMidLine_left_aggregationEdge()
	{
		//aNodeB and aNodeC are to the left and slightly above from aNodeA. 
		//edge1 connects aNodeB ---> aNodeA
		//edge2 connects aNodeC ---> aNodeA
		//edge3 connects aNodeC <--- aNodeA
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aDiagram.addRootNode(aNodeC);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new AggregationEdge();
		Edge edge3 = new AggregationEdge();
		edge1.connect(aNodeB, aNodeA);
		edge2.connect(aNodeC, aNodeA);
		edge3.connect(aNodeA, aNodeC);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(0, 100));
		aNodeC.moveTo(new Point(0, 0));
		//Store the path for edge1:
		store(edge1, new EdgePath(new Point(300, 300), new Point(200, 330), new Point(200, 130), new Point(100, 130)));
		assertEquals(210, adjacentVerticalMidLine(edge1, edge2, Side.RIGHT));
		assertEquals(210, adjacentVerticalMidLine(edge1, edge3, Side.LEFT));
	}
	
	
	
	/*
	 * tests the getConnectionPoint() method in a scenario where edges are attached to the 
	 * North side of their start nodes, and the South side of a common end node.
	 * 
	 * Note: this scenario holds when the node is unlabeled and has no methods/fields; which can change the size 
	 * of the node and thus change the connection point coordinates. 
	 */
	@Test
	public void testGetConnectionPoint_NorthSouthSides()
	{
		//Initialize 1 end node and 6 start nodes 
		endNode = new ClassNode();
		Node startNode1 = new ClassNode();
		Node startNode2 = new ClassNode();
		Node startNode3 = new ClassNode();
		Node startNode4 = new ClassNode();
		Node startNode5 = new ClassNode();
		Node startNode6 = new ClassNode();
		aDiagram.addRootNode(endNode);
		aDiagram.addRootNode(startNode1);
		aDiagram.addRootNode(startNode2);
		aDiagram.addRootNode(startNode3);
		aDiagram.addRootNode(startNode4);
		aDiagram.addRootNode(startNode5);
		aDiagram.addRootNode(startNode6);
		//initialize 6 edges which start at each startNode and converge on the South side of endNode
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new GeneralizationEdge();
		Edge edge3 = new GeneralizationEdge();
		Edge edge4 = new GeneralizationEdge();
		Edge edge5 = new GeneralizationEdge();
		Edge edge6 = new GeneralizationEdge();
		edge1.connect(startNode1, endNode);
		edge2.connect(startNode2, endNode);
		edge3.connect(startNode3, endNode);
		edge4.connect(startNode4, endNode);
		edge5.connect(startNode5, endNode);
		edge6.connect(startNode6, endNode);
		//Position nodes so that all start nodes are below and to the right of endNode
		endNode.moveTo(new Point(200, 0));
		startNode1.moveTo(new Point(400, 300));
		startNode2.moveTo(new Point(500, 300));
		startNode3.moveTo(new Point(600, 300));
		startNode4.moveTo(new Point(700, 300));
		startNode5.moveTo(new Point(800, 300));
		startNode6.moveTo(new Point(900, 300));
		//edge1 should attach to index 0 on the South face of endNode
		assertEquals(new Point(250, 60), getConnectionPoint(endNode, edge1, Side.BOTTOM));
		store(edge1, new EdgePath(new Point(450, 300), new Point(450, 180), new Point(250, 180), new Point(250, 60)));
		
		//edge2 should attach to index +1 on the South face of endNode
		assertEquals(new Point(260, 60), getConnectionPoint(endNode, edge2, Side.BOTTOM));
		store(edge2, new EdgePath(new Point(550, 300), new Point(550, 180), new Point(260, 180), new Point(260, 60)));
		
		//edge3 should attach to index +2 on the South face of endNode
		assertEquals(new Point(270, 60), getConnectionPoint(endNode, edge3, Side.BOTTOM));
		store(edge3, new EdgePath(new Point(650, 300), new Point(650, 180), new Point(270, 180), new Point(270, 60)));
		
		//edge4 should attach to position +3 on the South face endNode
		assertEquals(new Point(280, 60), getConnectionPoint(endNode, edge4, Side.BOTTOM));
		store(edge4, new EdgePath(new Point(750, 300), new Point(750, 180), new Point(280, 180), new Point(280, 60)));
	
		//edge5 should attach to position +4 on the South face of endNode
		assertEquals(new Point(290, 60), getConnectionPoint(endNode, edge5, Side.BOTTOM));
		store(edge5, new EdgePath(new Point(850, 300), new Point(850, 180), new Point(290, 180), new Point(290, 60)));
		
		//when all other connection points on the side of pNode are taken, the default connection point for edge6 is position +4
		//even if it is already occupied
		assertEquals(new Point(290, 60), getConnectionPoint(endNode, edge6, Side.BOTTOM));
	}
	
	
	@Test
	public void testGetConnectionPoint_EastWestSides()
	{
		//Initialize 1 end node and 4 start nodes 
		endNode = new ClassNode();
		Node startNode1 = new ClassNode();
		Node startNode2 = new ClassNode();
		Node startNode3 = new ClassNode();
		Node startNode4 = new ClassNode();
		aDiagram.addRootNode(endNode);
		aDiagram.addRootNode(startNode1);
		aDiagram.addRootNode(startNode2);
		aDiagram.addRootNode(startNode3);
		aDiagram.addRootNode(startNode4);
		//initialize 4 edges which start at each startNode and converge on the West side of endNode
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new GeneralizationEdge();
		Edge edge3 = new GeneralizationEdge();
		Edge edge4 = new GeneralizationEdge();
		edge1.connect(startNode1, endNode);
		edge2.connect(startNode2, endNode);
		edge3.connect(startNode3, endNode);
		edge4.connect(startNode4, endNode);
		//Position nodes so that all start nodes are above and to the left of the end node
		startNode1.moveTo(new Point(0, 0));
		startNode2.moveTo(new Point(0, 100));
		startNode3.moveTo(new Point(0, 200));
		startNode4.moveTo(new Point(0, 300));
		endNode.moveTo(new Point(300, 400));
		
		//edge1 should connect to position 0 on the West side of endNode
		assertEquals(new Point(300, 430), getConnectionPoint(endNode, edge1, Side.LEFT));
		store(edge1, new EdgePath(new Point(100, 50), new Point(200, 50), new Point(200, 430), new Point(300, 430)));
		
		//edge2 should connect to position -1 on the West side of endNode
		assertEquals(new Point(300, 420), getConnectionPoint(endNode, edge2, Side.LEFT));
		store(edge2, new EdgePath(new Point(100, 150), new Point(200, 150), new Point(200, 420), new Point(300, 420)));
		
		//edge3 should connect to position -2 on the West side of endNode
		assertEquals(new Point(300, 410), getConnectionPoint(endNode, edge3, Side.LEFT));
		store(edge3, new EdgePath(new Point(100, 250), new Point(200, 250), new Point(200, 410), new Point(300, 410)));
		
		//since there are no other available negative-index connection points on the West side of endNode, edge4 attaches 
		//to position -2 on the West side of endNode by default
		assertEquals(new Point(300, 410), getConnectionPoint(endNode, edge4, Side.LEFT));
	}
	
	
	@Test
	public void testGetSharedNode()
	{
		setUpThreeConnectedNodes();
		//Edges share a common end node:
		assertEquals(aNodeA, getSharedNode(aEdgeA, aEdgeB));
		assertEquals(aNodeA, getSharedNode(aEdgeB, aEdgeA));
		//End node of aEdgeA is the start node of aEdgeB:
		aEdgeB.connect(aNodeA, aNodeC);
		assertEquals(aNodeA, getSharedNode(aEdgeA, aEdgeB));
		assertEquals(aNodeA, getSharedNode(aEdgeB, aEdgeA));
		//start node of aEdgeA is the start node of aEdgeB:
		aEdgeA.connect(aNodeA, aNodeB);
		assertEquals(aNodeA, getSharedNode(aEdgeA, aEdgeB));
		assertEquals(aNodeA, getSharedNode(aEdgeB, aEdgeA));
	}
	
	
	@Test
	public void testNoOtherEdgesBetween_sameEdge()
	{
		setUpTwoConnectedNodes();
		assertTrue(noOtherEdgesBetween(aEdgeA, aEdgeA, aNodeA));
	}
	
	@Test
	public void testNoOtherEdgesBetween_noOtherEdgesOnNodeFace()
	{
		setUpThreeConnectedNodes();
		//aEdgeA and aEdgeB are the only two edges connected to the south face of aNodeA
		assertTrue(noOtherEdgesBetween(aEdgeA, aEdgeB, aNodeA));
	}
	
	@Test
	public void testNoOtherEdgesBetween_edgeOnDifferentFace()
	{
		setUpThreeConnectedNodes();
		Edge storedEdge = new GeneralizationEdge();
		Node storedEdgeStartNode = new ClassNode();
		storedEdge.connect(storedEdgeStartNode, aNodeA);
		aDiagram.addEdge(storedEdge);
		storedEdgeStartNode.moveTo(new Point(300, 140));
		//Positions of nodes in this scenario: (x,y)
		//aNodeA: 100, 140
		//aNodeB: 110, 300
		//aNodeC: 200, 300
		//storedEdge: 300, 140
		//Store storedEdgePath so it connects to the East side of aNodeA
		store(storedEdge, new EdgePath(new Point(300, 170), new Point(250, 170), new Point(250, 170), new Point(200, 170)));
		assertTrue(noOtherEdgesBetween(aEdgeA, aEdgeB, aNodeA));
	}
	
	@Test
	public void testNoOtherEdgesBetween_edgeOnNodeFace()
	{
		setUpThreeConnectedNodes();
		Edge storedEdge = new GeneralizationEdge();
		Node storedEdgeStartNode = new ClassNode();
		storedEdge.connect(storedEdgeStartNode, aNodeA);
		aDiagram.addEdge(storedEdge);
		
		//Reposition nodes so that aNodeA is above all other nodes, and storedEdgeStartNode is in between aNodeB and aNodeC: 
		aNodeA.moveTo(new Point(400, 0));
		aNodeB.moveTo(new Point(300, 300));
		storedEdgeStartNode.moveTo(new Point(400, 300));
		aNodeC.moveTo(new Point(500, 300));
		storedEdgeStartNode.moveTo(new Point(300, 140));
		
		//Store storedEdgePath so it connects to the BOTTOM side of aNodeA: (which is the same side that aEdgeA and aEdgeB would connect)
		store(storedEdge, new EdgePath(new Point(450, 300), new Point(450, 180), new Point(450, 180), new Point(450, 60)));
		assertFalse(noOtherEdgesBetween(aEdgeA, aEdgeB, aNodeA));
		assertFalse(noOtherEdgesBetween(aEdgeB, aEdgeA, aNodeA));
		
		//move aNodeB so that it is beside aNodeC:
		aNodeB.moveTo(new Point(600, 300));
		assertTrue(noOtherEdgesBetween(aEdgeA, aEdgeB, aNodeA));
		assertTrue(noOtherEdgesBetween(aEdgeB, aEdgeA, aNodeA));
	}
	
	/*
	 * Common node (aNodeA) is to the left of aNodeB and aNodeC.
	 */
	@Test
	public void testNodesOnSameSideOfCommonNode_east()
	
	{
		setUpThreeConnectedNodes();
		aNodeA.moveTo(new Point(0, 300));
		aNodeB.moveTo(new Point(200, 100));
		aNodeC.moveTo(new Point(200, 200));
		//aNodeB and aNodeC are both above and to the right of aNodeA
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.RIGHT));
		
		//Move aNodec so that it is below and to the right of aNodeA:
		aNodeC.moveTo(new Point(200, 400));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.RIGHT));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, Side.RIGHT));
		
		//Move aNodeB so that it is also below and to the right of aNodeA:
		aNodeB.moveTo(new Point(200, 500));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.RIGHT));
	}
	
	/*
	 * Common node (aNodeA) is to the right of aNodeB and aNodeC.
	 */
	@Test
	public void testNodesOnSameSideOfCommonNode_west()
	{
		//aNodeB and aNodeC are both to the left and above from aNode
		setUpThreeConnectedNodes();
		aNodeB.moveTo(new Point(0, 100));
		aNodeC.moveTo(new Point(0, 200));
		aNodeA.moveTo(new Point(200, 300));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.LEFT));
		
		//move aNodeC so that it is to the left of and below aNodeA:
		aNodeC.moveTo(new Point(0, 400));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.LEFT));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, Side.LEFT));
		
		//move aNodeB so that it is also to the left of and below aNodeA:
		aNodeB.moveTo(new Point(0, 500));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.LEFT));
		
	}
	
	/*
	 * Common node (aNodeA) is above aNodeB and aNodeC
	 */
	@Test
	public void testNodesOnSameSideOfCommonNode_south()
	{
		setUpThreeConnectedNodes();
		aNodeA.moveTo(new Point(300, 0));
		aNodeB.moveTo(new Point(100, 300));
		aNodeC.moveTo(new Point(200, 300));
		//aNodeB and aNodeC are both below and to the left of aNodeA
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.BOTTOM));
		
		//Move aNodeC so that it is below and to the right side of aNodeA:
		aNodeC.moveTo(new Point(400, 300));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.BOTTOM));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, Side.BOTTOM));
		
		//Move aNodeB so that it is also below and to the right of aNodeA:
		aNodeB.moveTo(new Point(500, 300));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.BOTTOM));
	}
	
	/*
	 * Common node (aNodeA) is below aNodeB and aNodeC
	 */
	@Test
	public void testNodesOnSameSideOfCommonNode_north()
	{
		//aNodeB and aNodeC are above aNodeA and to the left
		setUpThreeConnectedNodes();
		aNodeC.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(100, 0));
		aNodeA.moveTo(new Point(200, 200));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.TOP));
		
		//move aNodeC so that it is above aNodeA and to the right:
		aNodeC.moveTo(new Point(300, 0));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.TOP));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, Side.TOP));
		
		//move aNodeB so it is also above aNodeA and to the right:
		aNodeB.moveTo(new Point(400, 0));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, Side.TOP));
	}
	
	@Test
	public void testNoConflictingStartLabels_edgeTypeHasNoStartLabel()
	{
		//aEdgeA and aEdgeB are both generalization edges, which don't have start labels
		assertTrue(noConflictingStartLabels(aEdgeA, aEdgeB));
		AggregationEdge aggregationEdge = new AggregationEdge();
		assertTrue(noConflictingStartLabels(aggregationEdge, aEdgeB));
		assertTrue(noConflictingStartLabels(aEdgeA, aggregationEdge));
		
	}
	
	@Test
	public void testNoConflictingStartLabels_sameStartLabels()
	{
		AggregationEdge aggregationEdge1 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge2 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge3 = new AggregationEdge(AggregationEdge.Type.Composition);
		aggregationEdge1.setStartLabel("Test Label");
		aggregationEdge2.setStartLabel("Test Label");
		aggregationEdge3.setStartLabel("Test Label");
		assertTrue(noConflictingStartLabels(aggregationEdge1, aggregationEdge2));
		assertTrue(noConflictingStartLabels(aggregationEdge1, aggregationEdge3));
	}
	
	@Test
	public void testNoConflictingStartLabels_differentStartLabels()
	{
		AggregationEdge aggregationEdge1 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge2 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge3 = new AggregationEdge(AggregationEdge.Type.Composition);
		aggregationEdge1.setStartLabel("Test Label");
		aggregationEdge2.setStartLabel("Different Test Label");
		aggregationEdge3.setStartLabel("Test Label");
		assertFalse(noConflictingStartLabels(aggregationEdge1, aggregationEdge2));
		assertTrue(noConflictingStartLabels(aggregationEdge1, aggregationEdge3));
		assertTrue(noConflictingStartLabels(aggregationEdge2, aggregationEdge3));
	}
	
	@Test
	public void testNoConflictingEndLabels_edgeTypeHasNoEndLabel()
	{
		//aEdgeA and aEdgeB are both generalization edges, which don't have start labels
		assertTrue(noConflictingEndLabels(aEdgeA, aEdgeB));
		AggregationEdge aggregationEdge = new AggregationEdge();
		assertTrue(noConflictingEndLabels(aggregationEdge, aEdgeB));
		assertTrue(noConflictingEndLabels(aEdgeA, aggregationEdge));
		
	}
	
	@Test
	public void testNoConflictingEndLabels_sameEndLabels()
	{
		AggregationEdge aggregationEdge1 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge2 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge3 = new AggregationEdge(AggregationEdge.Type.Composition);
		aggregationEdge1.setEndLabel("Test Label");
		aggregationEdge2.setEndLabel("Test Label");
		aggregationEdge3.setEndLabel("Test Label");
		assertTrue(noConflictingEndLabels(aggregationEdge1, aggregationEdge2));
		assertTrue(noConflictingEndLabels(aggregationEdge1, aggregationEdge3));
	}
	
	@Test
	public void testNoConflictingEndLabels_differentEndLabels()
	{
		AggregationEdge aggregationEdge1 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge2 = new AggregationEdge(AggregationEdge.Type.Aggregation);
		AggregationEdge aggregationEdge3 = new AggregationEdge(AggregationEdge.Type.Composition);
		aggregationEdge1.setEndLabel("Test Label");
		aggregationEdge2.setEndLabel("Different Test Label");
		aggregationEdge3.setEndLabel("Test Label");
		assertFalse(noConflictingEndLabels(aggregationEdge1, aggregationEdge2));
		assertTrue(noConflictingEndLabels(aggregationEdge1, aggregationEdge3));
		assertTrue(noConflictingEndLabels(aggregationEdge2, aggregationEdge3));
	}
	
	@Test
	public void testAttachedSide_aggregationEdge()
	{
		//aNodeB is 400px below and 400px to the right of aNodeA. 
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(400, 400));
		//aggregationEdge connects aEdgeA to aEdgeB
		Edge aggregationEdge = new AggregationEdge();
		aggregationEdge.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aggregationEdge);
		assertEquals(Side.RIGHT, attachedSide(aggregationEdge, aNodeA));
		assertEquals(Side.LEFT, attachedSide(aggregationEdge, aNodeB));	
	}
	
	@Test
	public void testAttachedSide_generalizationEdge()
	{
		//aNodeB is 400px below and 400px to the right of aNodeA. aEdgeA connects from aEgdeA to aEdgeB.
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(400, 400));
		assertEquals(Side.BOTTOM, attachedSide(aEdgeA, aNodeA));
		assertEquals(Side.TOP, attachedSide(aEdgeA, aNodeB));	
	}
	
	@Test
	public void testAttachedSide_storedSharedNodesEdge()
	{
		//aNodeB is 400px below and 400px to the right of aNodeA. aEdgeA connects from aEgdeA to aEdgeB.
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(400, 0));
		store(aEdgeA, new EdgePath(new Point(100, 30), new Point(250, 30), new Point(250, 30), new Point(400, 30)));
		Edge newEdge = new AggregationEdge();
		newEdge.connect(aNodeB, aNodeA);
		aDiagram.addEdge(newEdge);
		assertEquals(Side.RIGHT, attachedSide(newEdge, aNodeA));
		assertEquals(Side.LEFT, attachedSide(newEdge, aNodeB));
			
	}
	
	@Test
	public void testAttachedSidePreferringEastWest_nodesAboveAndBelowEachOther()
	{
		//aNodeA is directly above aNodeB. aEdgeA connected aNodeA to aNode B. aEdgeB connects aNodeB to aNodeA.
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 200));
		aEdgeB.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aEdgeB);
		assertEquals(Side.BOTTOM, attachedSidePreferringEastWest(aEdgeA));
		assertEquals(Side.TOP, attachedSidePreferringEastWest(aEdgeB));
	}
	
	
	@Test
	public void testAttachedSidePreferringEastWest_nodesBesideEachOther()
	{
		//aNodeA is directly to the left of aNodeA. The nodes are connected by aEdgeA and aEdgeB in both directions
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 0));
		aEdgeB.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aEdgeB);
		assertEquals(Side.RIGHT, attachedSidePreferringEastWest(aEdgeA));
		assertEquals(Side.LEFT, attachedSidePreferringEastWest(aEdgeB));
	}
	
	
	@Test
	public void testAttachedSidePreferringNorthSouth_nodesAboveAndBelowEachother()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(100, 200));
		//Also connect aEdgeB from aNodeB to aNodeA to test the method for both East and West directions
		aEdgeB.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aEdgeB);
		assertEquals(Side.BOTTOM, attachedSidePreferringNorthSouth(aEdgeA));
		assertEquals(Side.TOP, attachedSidePreferringNorthSouth(aEdgeB));
	}
	
	@Test
	public void testAttachedSidePreferringNorthSouth_nodesBesideEachOther()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 0));
		//Also connect aEdgeB aNodeA <---- aNodeB to test the method for both East and West directions
		aEdgeB.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aEdgeB);
		assertEquals(Side.RIGHT, attachedSidePreferringNorthSouth(aEdgeA));
		assertEquals(Side.LEFT, attachedSidePreferringNorthSouth(aEdgeB));
	}
	
	@Test
	public void testEastWestSideUnlessNodesTooClose_nodesTooClose()
	{
		setUpThreeConnectedNodes();
		//Reposition nodes so that aNodeB is to the left of aNodeA. aNodeB is also below and to the left of aNodeA,
		//but it is closer to aNodeA than the middle segment of aEdgeA is. Thus, aEdgeB should connect from the North side of 
		//aNodeC to the South side of aNodeA.
		aNodeB.moveTo(new Point(100, 100));
		aNodeC.moveTo(new Point(200, 200));
		aNodeA.moveTo(new Point(300, 0));
		//Store a segmented EdgePath going from pNodeB to pNode A
		store(aEdgeA, new EdgePath(new Point(200, 150), new Point(250, 150), new Point(250, 30), new Point(300, 30)));
		assertEquals(Side.TOP, eastWestSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void testEastWestSideUnlessNodesTooClose_nodesNotTooClose()
	{
		setUpThreeConnectedNodes();
		//Reposition nodes so that aNodeB and aNodeC are to the left of aNodeA (aNodeB is above aNodeC).
		aNodeB.moveTo(new Point(100, 100));
		aNodeC.moveTo(new Point(100, 200));
		aNodeA.moveTo(new Point(300, 0));
		//Store a segmented EdgePath going from pNodeB to pNode A
		store(aEdgeA, new EdgePath(new Point(200, 150), new Point(250, 150), new Point(250, 30), new Point(300, 30)));
		assertEquals(Side.RIGHT, eastWestSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void TestNorthSouthSideUnlessNodesTooClose_nodesNotTooClose()
	{
		setUpThreeConnectedNodes();
		//store the EdgePath for aEdgeA, from aNodeB to aNodeA 
		store(aEdgeA, new EdgePath(new Point(160, 330), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		assertEquals(Side.TOP, northSouthSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void testNorthSouthSideUnlessNodesTooClose_nodesTooClose()
	{
		setUpThreeConnectedNodes();
		//aNodeC is closer to aNodeA than the mid-segment of aEdgeA is
		aNodeC.moveTo(new Point(200, 210));
		store(aEdgeA, new EdgePath(new Point(160, 330), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		assertEquals(Side.LEFT, northSouthSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void testGetStoredEdgePath_edgeInStorage()
	{
		EdgePath path = new EdgePath(new Point(0,0), new Point(100, 100));
		aEdgeA.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, path);
		assertEquals(path, getStoredEdgePath(aEdgeA));
	}
	
	@Test
	public void testBuildSegmentedEdgePath_verticalEdgeDirection()
	{
		EdgePath expectedResult_north = new EdgePath(new Point(100,300), new Point(100, 150), new Point(300, 150), new Point(300, 0));
		EdgePath expectedResult_south = new EdgePath(new Point(300,0), new Point(300, 150), new Point(100, 150), new Point(100, 300));
		assertEquals(expectedResult_north, buildSegmentedEdgePath(Side.TOP, new Point(100, 300), 150, new Point(300,0)));
		assertEquals(expectedResult_south, buildSegmentedEdgePath(Side.BOTTOM, new Point(300, 0), 150, new Point(100,300)));
	}
	
	@Test
	public void testBuildSegmentedEdgePath_horizontalEdgeDirection()
	{
		EdgePath expectedResult_east = new EdgePath(new Point(100,300), new Point(200, 300), new Point(200, 200), new Point(300, 200));
		EdgePath expectedResult_west = new EdgePath(new Point(300,200), new Point(200, 200), new Point(200, 300), new Point(100, 300));
		assertEquals(expectedResult_east, buildSegmentedEdgePath(Side.RIGHT, new Point(100, 300), 200, new Point(300,200)));
		assertEquals(expectedResult_west, buildSegmentedEdgePath(Side.LEFT, new Point(300, 200), 200, new Point(100, 300)));
	}

	@Test
	public void testAttachedSideFromStorage_north()
	{
		aNodeA.moveTo(new Point(100, 0));
		aNodeB.moveTo(new Point(100, 200));
		aEdgeA.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(130, 60), new Point(130, 130), new Point(130, 130), new Point(130, 200)));
		assertEquals(Side.TOP, attachedSideFromStorage(aEdgeA, aNodeB));
	}
	
	@Test
	public void testAttachedSideFromStorage_south()
	{
		aNodeA.moveTo(new Point(100, 0));
		aNodeB.moveTo(new Point(100, 200));
		aEdgeA.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(130, 60), new Point(130, 130), new Point(130, 130), new Point(130, 200)));
		assertEquals(Side.BOTTOM, attachedSideFromStorage(aEdgeA, aNodeA));
	}
	
	@Test
	public void testAttachedSideFromStorage_east()
	{
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(200, 300));
		aEdgeA.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(260, 330), new Point(280, 330), new Point(280, 330), new Point(300, 330)));
		assertEquals(Side.RIGHT, attachedSideFromStorage(aEdgeA, aNodeB));
	}
	
	@Test
	public void testAttachedSideFromStorage_west()
	{
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(200, 300));
		aEdgeA.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(260, 330), new Point(280, 330), new Point(280, 330), new Point(300, 330)));
		assertEquals(Side.LEFT, attachedSideFromStorage(aEdgeA, aNodeA));
	}
	
	
	@Test
	public void testVerticalDistanceToNode()
	{
		aEdgeA.connect(aNodeA, aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 400));
		store(aEdgeA, new EdgePath(new Point(30, 60), new Point(30, 230), new Point(30, 230), new Point(30, 400)));
		assertEquals(170, verticalDistanceToNode(aNodeB, aEdgeA, Side.BOTTOM));		
	}
	
	@Test
	public void testHorizontalDistanceToNode()
	{
		aEdgeA.connect(aNodeA, aNodeB);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(400, 10));
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(100, 30), new Point(225, 30), new Point(225, 40), new Point(350, 40)));
		assertEquals(175, horizontalDistanceToNode(aNodeB, aEdgeA, Side.LEFT));		
	}

	@Test
	public void testGetIndexSign_edgeSharingBothNodes()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(70, 0));
		aEdgeB.connect(aNodeA, aNodeB);
		aDiagram.addEdge(aEdgeB);
		//aEdgeA connects aNodeA --> aNodeB
		//aEdgeB connects aNodeB --> aNodeA
		store(aEdgeA, new EdgePath(new Point(100, 100), new Point(70, 0)));
		assertSame( 1, getIndexSign(aEdgeB, aNodeA, Side.TOP));
		assertSame( 1, getIndexSign(aEdgeB, aNodeB, Side.BOTTOM));
	}
	
	@Test
	public void testGetIndexSign_edgeNotSharingBothNodes()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(70, 0));
		aDiagram.addEdge(aEdgeA);
		assertSame(-1, getIndexSign(aEdgeA, aNodeA, Side.TOP));
	}
	
	@Test
	public void testIndexSignOnNode_otherNodeAbove()
	{
		setUpTwoConnectedNodes();
		//Other node directly above start node
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(100, 0));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.TOP));
		
		//Other node above and to the left of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(450, 300));
		assertSame(-1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.TOP));
		
		//Other node above and to the right of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(550, 300));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.TOP));
		
	}
	
	@Test
	public void testIndexSignOnNode_otherNodeBelow()
	{
		//end node directly below start node
		setUpTwoConnectedNodes();
		aNodeB.moveTo(new Point(100, 100));
		aNodeA.moveTo(new Point(100, 400));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.BOTTOM));
		
		//End node above and to the left of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(450, 600));
		assertSame(-1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.BOTTOM));
		
		//End node above and to the right of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(550, 600));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.BOTTOM));
	}
	
	@Test
	public void testIndexSignOnNode_otherNodeOnRight()
	{
		//end node directly to right of start node
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(400, 100));
		assertSame(indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.RIGHT), 1);
		
		//End node to the right of start node and slightly up 
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(700, 450));
		assertSame(indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.RIGHT), -1);
		
		//End node to the right of start node and slightly down
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(700, 550));
		assertSame(indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.RIGHT), 1);
	}

	
	@Test
	public void testIndexSignOnNode_otherNodeOnLeft()
	{
		//end node directly to left of start node
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(200, 200));
		aNodeB.moveTo(new Point(100, 200));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.LEFT));
		
		//End node to the left of start node and slightly up 
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(400, 450));
		assertSame(-1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.LEFT));
		
		//End node to the left of start node and slightly down
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(400, 550));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, Side.LEFT));
	}
	
	@Test 
	public void testGetClosestPoint_north()
	{
		assertEquals(new Point(200, 290), getClosestPoint(getPoints(), Side.TOP));
	}
	
	@Test 
	public void testGetClosestPoint_south()
	{
		assertEquals(new Point(200, 310), getClosestPoint(getPoints(), Side.BOTTOM));
	}
	

	@Test 
	public void testGetClosestPoint_east()
	{
		assertEquals(new Point(210, 300), getClosestPoint(getPoints(), Side.RIGHT));
	}
	
	@Test 
	public void testGetClosestPoint_west()
	{
		assertEquals(new Point(190, 300), getClosestPoint(getPoints(), Side.LEFT));
	}
	
	
	@Test
	public void testGetOtherNode()
	{
		aEdgeA.connect(aNodeB, aNodeA);
		assertSame(aNodeB, getOtherNode(aEdgeA, aNodeA));
		assertSame(aNodeA, getOtherNode(aEdgeA, aNodeB));
	}
	
	@Test
	public void testIsOutgoingEdge()
	{
		aEdgeA.connect(aNodeA, aNodeB);
		assertTrue(isOutgoingEdge(aEdgeA, aNodeA));
		assertFalse(isOutgoingEdge(aEdgeA, aNodeB));
	}
	
	@Test
	public void testNorthOrSouthSide()
	{
		assertEquals(Side.TOP, northOrSouthSide(aRectangleA, aRectangleB));
		assertEquals(Side.BOTTOM, northOrSouthSide(aRectangleB, aRectangleA));
		assertEquals(Side.BOTTOM, northOrSouthSide(aRectangleA, aRectangleA));
	}
	
	@Test
	public void testEastOrWestSide()
	{
		assertEquals(Side.LEFT, eastOrWestSide(aRectangleA, aRectangleC));
		assertEquals(Side.RIGHT, eastOrWestSide(aRectangleC, aRectangleA));
		assertEquals(Side.RIGHT, eastOrWestSide(aRectangleA, aRectangleA));
	}
	
	
	
	
	
	
	/// REFLECTIVE HELPER METHODS ///
	
	/*
	 * Stores an edge path in the layouter of the active classdiagramrenderer
	 */
	private void store(Edge pEdge, EdgePath pEdgePath)
	{
		try 
		{
			Field edgeStorage = ClassDiagramRenderer.class.getDeclaredField("aEdgeStorage");
			edgeStorage.setAccessible(true);
			((EdgeStorage)edgeStorage.get(aRenderer)).store(pEdge, pEdgePath);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void layoutSegmentedEdges(EdgePriority pEdgePriority)
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("layoutSegmentedEdges", EdgePriority.class);
			method.setAccessible(true);
			method.invoke(aRenderer, pEdgePriority);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	
	private void storeMergedEndEdges(Side pDirection, List<Edge> pEdgesToMerge)
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("storeMergedEndEdges", Side.class, List.class);
			method.setAccessible(true);
			method.invoke(aRenderer, pDirection, pEdgesToMerge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void storeMergedStartEdges(Side pDirection, List<Edge> pEdgesToMerge)
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("storeMergedStartEdges", Side.class, List.class);
			method.setAccessible(true);
			method.invoke(aRenderer, pDirection, pEdgesToMerge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void layoutDependencyEdges()
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("layoutDependencyEdges");
			method.setAccessible(true);
			method.invoke(aRenderer);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void layoutSelfEdges()
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("layoutSelfEdges");
			method.setAccessible(true);
			method.invoke(aRenderer);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private EdgePath buildSelfEdge(Edge pEdge, NodeCorner pCorner)
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("buildSelfEdge", Edge.class, NodeCorner.class);
			method.setAccessible(true);
			return (EdgePath) method.invoke(aRenderer, pEdge, pCorner);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	private NodeCorner getSelfEdgeCorner(Edge pEdge)
	{
		try 
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getSelfEdgeCorner", Edge.class);
			method.setAccessible(true);
			return (NodeCorner) method.invoke(aRenderer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	

	
	@SuppressWarnings("unchecked")
	private Collection<Edge> getEdgesToMergeStart(Edge pEdge, List<Edge> pEdges)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getEdgesToMergeStart", Edge.class, List.class);
			method.setAccessible(true);
			return (Collection<Edge>) method.invoke(aRenderer, pEdge, pEdges);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Edge> getEdgesToMergeEnd(Edge pEdge, List<Edge> pEdges)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getEdgesToMergeEnd", Edge.class, List.class);
			method.setAccessible(true);
			return (Collection<Edge>) method.invoke(aRenderer, pEdge, pEdges);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Edge> storedConflictingEdges(Side pNodeFace, Node pNode, Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("storedConflictingEdges", Side.class, Node.class, Edge.class);
			method.setAccessible(true);
			return (List<Edge>) method.invoke(aRenderer, pNodeFace, pNode, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private boolean nodeIsCloserThanSegment(Edge pEdge, Node pNode, Side pAttachedSide)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("nodeIsCloserThanSegment", Edge.class, Node.class, Side.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aRenderer, pEdge, pNode, pAttachedSide);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private int getHorizontalMidLine(Point pStart, Point pEnd, Side pEdgeDirection, Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getHorizontalMidLine", Point.class, Point.class, Side.class, Edge.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pStart, pEnd, pEdgeDirection,pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	
	private int getVerticalMidLine(Point pStart, Point pEnd, Side pEdgeDirection, Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getVerticalMidLine", Point.class, Point.class, Side.class, Edge.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pStart, pEnd, pEdgeDirection, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	
	private int horizontalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, Side pEdgeDirection)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("horizontalMidlineForSharedNodeEdges", Edge.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pEdgeWithSameNodes, pNewEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int verticalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, Side pEdgeDirection) 
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("verticalMidlineForSharedNodeEdges", Edge.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pEdgeWithSameNodes, pNewEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Edge> closestConflictingVerticalSegment(Side pEdgeDirection,Edge pEdge) 
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("closestConflictingVerticalSegment", Side.class, Edge.class);
			method.setAccessible(true);
			return (Optional<Edge>) method.invoke(aRenderer, pEdgeDirection, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Edge> closestConflictingHorizontalSegment( Side pEdgeDirection, Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("closestConflictingHorizontalSegment", Side.class, Edge.class);
			method.setAccessible(true);
			return (Optional<Edge>) method.invoke(aRenderer, pEdgeDirection, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private int adjacentHorizontalMidLine(Edge pClosestStoredEdge, Edge pEdge, Side pEdgeDirection)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("adjacentHorizontalMidLine", Edge.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pClosestStoredEdge, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int adjacentVerticalMidLine(Edge pClosestStoredEdge, Edge pEdge, Side pEdgeDirection)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("adjacentVerticalMidLine", Edge.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pClosestStoredEdge, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}

	private Node getSharedNode(Edge pEdgeA, Edge pEdgeB)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getSharedNode", Edge.class, Edge.class);
			method.setAccessible(true);
			return (Node) method.invoke(aRenderer, pEdgeA, pEdgeB);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Point getConnectionPoint(Node pNode, Edge pEdge, Side pAttachmentSide)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getConnectionPoint", Node.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (Point) method.invoke(aRenderer, pNode, pEdge, pAttachmentSide);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private boolean noOtherEdgesBetween(Edge pEdge1, Edge pEdge2, Node pNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("noOtherEdgesBetween", Edge.class, Edge.class, Node.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aRenderer, pEdge1, pEdge2, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private boolean nodesOnSameSideOfCommonNode(Node pNode1, Node pNode2, Node pCommonNode, Side pAttachedSide)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("nodesOnSameSideOfCommonNode", Node.class, Node.class, Node.class, Side.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aRenderer, pNode1, pNode2, pCommonNode, pAttachedSide);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private boolean noConflictingStartLabels(Edge pEdge1, Edge pEdge2)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("noConflictingStartLabels", Edge.class, Edge.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aRenderer, pEdge1, pEdge2);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private boolean noConflictingEndLabels(Edge pEdge1, Edge pEdge2)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("noConflictingEndLabels", Edge.class, Edge.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aRenderer, pEdge1, pEdge2);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	
	private Side attachedSide(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("attachedSide", Edge.class, Node.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Side attachedSidePreferringEastWest(Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("attachedSidePreferringEastWest", Edge.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}

	private Side attachedSidePreferringNorthSouth(Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("attachedSidePreferringNorthSouth", Edge.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Side eastWestSideUnlessTooClose(Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("eastWestSideUnlessTooClose", Edge.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	
	private Side northSouthSideUnlessTooClose(Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("northSouthSideUnlessTooClose", Edge.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private EdgePath getStoredEdgePath(Edge pEdge)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getStoredEdgePath", Edge.class);
			method.setAccessible(true);
			return ((Optional<EdgePath>) method.invoke(aRenderer, pEdge)).get();
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private boolean contains(Edge pEdge)
	{
		try
		{
			Field field = ClassDiagramRenderer.class.getDeclaredField("aEdgeStorage");
			field.setAccessible(true);
			return ((EdgeStorage)field.get(aRenderer)).contains(pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private EdgePath buildSegmentedEdgePath(Side pEdgeDirection, Point pStart, int pMidLine, Point pEnd)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("buildSegmentedEdgePath", Side.class, Point.class, int.class, Point.class);
			method.setAccessible(true);
			return (EdgePath) method.invoke(aRenderer, pEdgeDirection, pStart, pMidLine, pEnd);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Side attachedSideFromStorage(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("attachedSideFromStorage", Edge.class, Node.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private int verticalDistanceToNode(Node pEndNode, Edge pEdge, Side pEdgeDirection)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("verticalDistanceToNode", Node.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pEndNode, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int horizontalDistanceToNode(Node pEndNode, Edge pEdge, Side pEdgeDirection)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("horizontalDistanceToNode", Node.class, Edge.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pEndNode, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int getIndexSign(Edge pEdge, Node pNode, Side pSideOfNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getIndexSign", Edge.class, Node.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pEdge, pNode, pSideOfNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int indexSignOnNode(Edge pEdge, Node pNode, Node pOtherNode, Side pSideOfNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("indexSignOnNode", Edge.class, Node.class, Node.class, Side.class);
			method.setAccessible(true);
			return (int) method.invoke(aRenderer, pEdge, pNode, pOtherNode, pSideOfNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	
	private static List<Point> getPoints()
	{
		List<Point> result = new ArrayList<>();
		result.add(new Point(190, 300));
		result.add(new Point(200, 310));
		result.add(new Point(210, 300));
		result.add(new Point(200, 290));
		return result;
	}
	
	private Point getClosestPoint(Collection<Point> pPoints, Side pEdgeDirection) 
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getClosestPoint", Collection.class, Side.class);
			method.setAccessible(true);
			return (Point) method.invoke(aRenderer, pPoints, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Node getOtherNode(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("getOtherNode", Edge.class, Node.class);
			method.setAccessible(true);
			return (Node) method.invoke(aRenderer, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private boolean isOutgoingEdge(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("isOutgoingEdge", Edge.class, Node.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aRenderer, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private Side northOrSouthSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("northOrSouthSide", Rectangle.class, Rectangle.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pBounds, pOtherBounds);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Side eastOrWestSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		try
		{
			Method method = ClassDiagramRenderer.class.getDeclaredMethod("eastOrWestSide", Rectangle.class, Rectangle.class);
			method.setAccessible(true);
			return (Side) method.invoke(aRenderer, pBounds, pOtherBounds);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}

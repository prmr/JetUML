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
package org.jetuml.viewers;

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
import org.jetuml.geom.EdgePath;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.ClassDiagramRenderer;
import org.jetuml.rendering.RenderingFacade;
import org.jetuml.viewers.edges.EdgeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the Layouter class methods.
 */
public class TestLayouter 
{
	private Diagram aDiagram;
	
	private static final Layouter aLayouter = new Layouter();
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
		aDiagram = new Diagram(DiagramType.CLASS);
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
		RenderingFacade.prepareFor(aDiagram);
	}
	
	
	/**
	 * Sets up two class nodes in the diagram which are connected by a generalization Edge. 
	 * The required position of the nodes is unique for each test; nodes should be repositioned by individual test methods. 
	 */
	private void setUpTwoConnectedNodes()
	{
		aDiagram.addRootNode(aNodeA);//start node
		aDiagram.addRootNode(aNodeB);//end node
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
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
		
		 aEdgeA.connect(aNodeB, aNodeA, aDiagram);
		 aEdgeB.connect(aNodeC, aNodeA, aDiagram);
		 
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
		aEdgeA.connect(aNodeD, aNodeA, aDiagram);
		aEdgeB.connect(aNodeD, aNodeB, aDiagram);
		aEdgeC.connect(aNodeD, aNodeC, aDiagram);
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
		aEdgeA.connect(aNodeA, aNodeD, aDiagram);
		aEdgeB.connect(aNodeB, aNodeD, aDiagram);
		aEdgeC.connect(aNodeC, aNodeD, aDiagram);
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
		dependencyEdge.connect(aNodeB, aNodeA, aDiagram);
		generalizationEdge.connect(aNodeC, aNodeA, aDiagram);
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
	
		for (Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD, aNodeE))
		{
			aDiagram.addRootNode(node);
		}
		
		aEdgeA.connect(aNodeD, aNodeA, aDiagram);
		aEdgeB.connect(aNodeD, aNodeB, aDiagram);
		aEdgeC.connect(aNodeD, aNodeC, aDiagram);
		aEdgeD.connect(aNodeE, aNodeD, aDiagram);
		for (Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD))
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
		for (Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD,endNode))
		{
			aDiagram.addRootNode(node);
		}
		aEdgeA.connect(aNodeA, endNode, aDiagram);
		aEdgeB.connect(aNodeB, endNode, aDiagram);
		aEdgeC.connect(aNodeC, endNode, aDiagram);
		aEdgeD.connect(aNodeD, endNode, aDiagram);
		
		for (Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD))
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
		for (Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD, aNodeE, aNodeF, aNodeG, endNode))
		{
			aDiagram.addRootNode(node);
		}
		aEdgeA.connect(aNodeA, endNode, aDiagram);
		aEdgeB.connect(aNodeB, endNode, aDiagram);
		aEdgeC.connect(aNodeC, endNode, aDiagram);
		aEdgeD.connect(aNodeD, endNode, aDiagram);
		aEdgeE.connect(aNodeE, endNode, aDiagram);
		aEdgeF.connect(aNodeF, endNode, aDiagram);
		aEdgeG.connect(aNodeG, endNode, aDiagram);
		
		for (Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD, aEdgeE, aEdgeF, aEdgeG))
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
		aLayouter.layout(aDiagram);
		//aEdgeA
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(50, 190)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(50, 155)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(200, 155)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(200, 120)).get());
		//aEdgeB (the other segments of aEdgeB overlap with aEdgeA)
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(150, 190)).get());
		//aEdgeC
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(250, 190)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(250, 155)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(210, 155)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(210, 120)).get());
		//aEdgeD
		assertEquals(aEdgeD, RenderingFacade.edgeAt(aDiagram, new Point(350, 190)).get());
		assertEquals(aEdgeD, RenderingFacade.edgeAt(aDiagram, new Point(350, 145)).get());
		assertEquals(aEdgeD, RenderingFacade.edgeAt(aDiagram, new Point(220, 145)).get());
		assertEquals(aEdgeD, RenderingFacade.edgeAt(aDiagram, new Point(220, 120)).get());
		//aEdgeE
		assertEquals(aEdgeE, RenderingFacade.edgeAt(aDiagram, new Point(400, 90)).get());
		assertEquals(aEdgeE, RenderingFacade.edgeAt(aDiagram, new Point(250, 90)).get());
		//EdgeF
		assertEquals(aEdgeF, RenderingFacade.edgeAt(aDiagram, new Point(400, 30)).get());
		assertEquals(aEdgeF, RenderingFacade.edgeAt(aDiagram, new Point(325, 30)).get());
		assertEquals(aEdgeF, RenderingFacade.edgeAt(aDiagram, new Point(325, 80)).get());
		assertEquals(aEdgeF, RenderingFacade.edgeAt(aDiagram, new Point(250, 80)).get());
		//aEdgeG
		assertEquals(aEdgeG, RenderingFacade.edgeAt(aDiagram, new Point(100, 30)).get());
		assertEquals(aEdgeG, RenderingFacade.edgeAt(aDiagram, new Point(150, 90)).get());
	}
	
	
	@Test
	public void testLayoutMergedEndEdges()
	{
		setUpLayoutMergedEndEdges();
		//Layout aEdgeA and aEdgeB
		layoutSegmentedEdges(aDiagram, EdgePriority.INHERITANCE);
		assertTrue(storageContains(aEdgeA));
		assertTrue(storageContains(aEdgeB));
		assertFalse(storageContains(aEdgeC));
		assertFalse(storageContains(aEdgeD));
		//aEdgeA
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(50, 140)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(50, 100)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(50, 60)).get());
		//aEdgeB
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(150, 140)).get());
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(150, 100)).get());
		
		//Layout aEdgeC
		layoutSegmentedEdges(aDiagram, EdgePriority.IMPLEMENTATION);
		assertTrue(storageContains(aEdgeA));
		assertTrue(storageContains(aEdgeB));
		assertTrue(storageContains(aEdgeC));
		assertFalse(storageContains(aEdgeD));
		//aEdgeC
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(250, 140)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(250, 90)).get());
		
		//Layout aEdgeD
		layoutSegmentedEdges(aDiagram, EdgePriority.ASSOCIATION);
		assertTrue(storageContains(aEdgeA));
		assertTrue(storageContains(aEdgeB));
		assertTrue(storageContains(aEdgeC));
		assertTrue(storageContains(aEdgeD));
		//aEdgeD
		assertEquals(aEdgeD, RenderingFacade.edgeAt(aDiagram, new Point(350, 140)).get());
		assertEquals(aEdgeD, RenderingFacade.edgeAt(aDiagram, new Point(350, 80)).get());
		
	}
	
	@Test
	public void testLayoutSegmentedEdges_aggregation()
	{
		setUpLayoutMergedStartEdges(AggregationEdge.Type.Aggregation);
		layoutSegmentedEdges(aDiagram, EdgePriority.AGGREGATION);
		//aEdgeA
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(200, 90)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(150, 90)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(150, 30)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(100, 30)).get());
		//aEdgeB
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(150, 150)).get());
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(100, 150)).get());
		//aEdgeC
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(300, 100)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(340, 100)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(340, 210)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(400, 210)).get());
		
	}
	
	@Test
	public void testLayoutSegmentedEdges_composition()
	{
		setUpLayoutMergedStartEdges(AggregationEdge.Type.Composition);
		layoutSegmentedEdges(aDiagram, EdgePriority.COMPOSITION);
		//aEdgeA
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(200, 90)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(150, 90)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(150, 30)).get());
		assertEquals(aEdgeA, RenderingFacade.edgeAt(aDiagram, new Point(100, 30)).get());
		
		//aEdgeB
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(150, 150)).get());
		assertEquals(aEdgeB, RenderingFacade.edgeAt(aDiagram, new Point(100, 150)).get());
		
		//aEdgeC
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(300, 100)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(340, 100)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(340, 210)).get());
		assertEquals(aEdgeC, RenderingFacade.edgeAt(aDiagram, new Point(400, 210)).get());
		
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
		storeMergedEndEdges(NodeSide.NORTH, edgesToMerge, aDiagram);
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
		storeMergedEndEdges(NodeSide.SOUTH, edgesToMerge, aDiagram);
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
		storeMergedEndEdges(NodeSide.EAST, edgesToMerge, aDiagram);
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
		storeMergedEndEdges(NodeSide.WEST, edgesToMerge, aDiagram);
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
		storeMergedStartEdges(NodeSide.NORTH, edgesToMerge, aDiagram);
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
		storeMergedStartEdges(NodeSide.SOUTH, edgesToMerge, aDiagram);
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
		storeMergedStartEdges(NodeSide.WEST, edgesToMerge, aDiagram);
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
		storeMergedStartEdges(NodeSide.EAST, edgesToMerge, aDiagram);
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
		layoutDependencyEdges(aDiagram);
		assertEquals(new EdgePath(new Point(150, 120), new Point(50, 60)), getStoredEdgePath(dependencyEdge));
	}
	
	@Test
	public void testLayoutDependencyEdges_otherEdgePresent()
	{
		setUpDependencyEdges();
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(200, 0));
		store(generalizationEdge, new EdgePath(new Point(200, 90), new Point(150, 90), new Point(150, 30), new Point(100, 30)));
		layoutDependencyEdges(aDiagram);
		assertEquals(new EdgePath(new Point(200, 30), new Point(100, 40)), getStoredEdgePath(dependencyEdge));
	}
	
	@Test
	public void testLayoutSelfEdges()
	{
		Edge selfEdge = new AggregationEdge();
		Edge nonSelfEdge = new AggregationEdge();
		selfEdge.connect(aNodeA, aNodeA, aDiagram);
		nonSelfEdge.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(selfEdge);
		aDiagram.addEdge(nonSelfEdge);
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		aNodeA.moveTo(new Point(20, 20));
		aNodeB.moveTo(new Point(20, 300));
		layoutSelfEdges(aDiagram);
		EdgePath expectedPath = new EdgePath(new Point(100, 20), new Point(100, 0), new Point(140, 0), new Point(140, 40), new Point(120, 40));
		assertEquals(expectedPath, getStoredEdgePath(selfEdge));
		assertFalse(storageContains(nonSelfEdge));
	}
	
	@Test
	public void testBuildSelfEdge_topRight()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node, aDiagram);
		EdgePath expected = new EdgePath(new Point(100, 20), new Point(100, 0), new Point(140, 0), new Point(140, 40), new Point(120, 40));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.TOP_RIGHT));
	}
	
	@Test
	public void testBuildSelfEdge_topLeft()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node, aDiagram);
		EdgePath expected = new EdgePath(new Point(40, 20), new Point(40, 0), new Point(0, 0), new Point(0, 40), new Point(20, 40));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.TOP_LEFT));
	}
	
	@Test
	public void testBuildSelfEdge_bottomLeft()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node, aDiagram);
		EdgePath expected = new EdgePath(new Point(40, 80), new Point(40, 100), new Point(0, 100), new Point(0, 60), new Point(20, 60));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.BOTTOM_LEFT));
	}
	
	@Test
	public void testBuildSelfEdge_bottomRight()
	{
		Node node = new ClassNode();
		node.moveTo(new Point(20, 20));
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node, aDiagram);
		EdgePath expected = new EdgePath(new Point(100, 80), new Point(100, 100), new Point(140, 100), new Point(140, 60), new Point(120, 60));
		assertEquals(expected, buildSelfEdge(selfEdge, NodeCorner.BOTTOM_RIGHT));
	}
	
	@Test
	public void testGetSelfEdgeCorner()
	{
		Node node = new ClassNode();
		Edge selfEdge = new AggregationEdge();
		selfEdge.connect(node, node, aDiagram);
		node.moveTo(new Point(100, 100));
		aDiagram.addRootNode(node);
		aDiagram.addEdge(selfEdge);
		aEdgeA.connect(node, aNodeB, aDiagram);
		aEdgeB.connect(aNodeB, node, aDiagram);
		aEdgeC.connect(aNodeC, node, aDiagram);
		aEdgeD.connect(node, aNodeD, aDiagram);
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
		edgeA.connect(startNode, nodeA, aDiagram);
		
		//edgeB should merge
		nodeB.moveTo(new Point(0, 100));
		edgeB.connect(startNode, nodeB, aDiagram);
		
		//edgeC should not merge (its has a different EdgePriority)
		nodeC.moveTo(new Point(0, 200));
		edgeC.connect(nodeC, startNode, aDiagram);
		store(edgeC, new EdgePath(new Point(100, 250), new Point(150, 250), new Point(150, 250), new Point(200, 250)));
		
		//edgeD should not merge (start label)
		nodeD.moveTo(new Point(0, 300));
		edgeD.connect(startNode, nodeD, aDiagram);
		edgeD.setStartLabel("label");
		
		//edgeE should not merge (it is not outgoing from startNode)
		nodeE.moveTo(new Point(0, 400));
		edgeE.connect(nodeE, startNode, aDiagram);
		
		//edgeF should not connect (it has a different attachment side)
		nodeF.moveTo(new Point(200, 210));
		edgeF.connect(startNode, nodeF, aDiagram);
		
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
		Node endNode = new ClassNode();
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
		edgeA.connect(nodeA, endNode, aDiagram);
		
		//edgeB should not merge: different direction
		nodeB.moveTo(new Point(0,180));
		edgeB.connect(endNode, nodeB, aDiagram);
		
		//edgeC should merge
		nodeC.moveTo(new Point(100,180));
		edgeC.connect(nodeC, endNode, aDiagram);
		
		//edgeD is the edge to merge (so it should not be included in the resulting list)
		nodeD.moveTo(new Point(200, 180));
		edgeD.connect(nodeD, endNode, aDiagram);
		
		//edgeE should not merge: (it is a different priority type)
		nodeE.moveTo(new Point(300, 180));
		edgeE.connect(nodeE, endNode, aDiagram);
		store(edgeE, new EdgePath(new Point(350, 180), new Point(350, 120), new Point(300, 120), new Point(300, 60)));
		
		//edgeF should not merge: it there is another edge (edgeE) in between it and edgeD
		nodeF.moveTo(new Point(400, 180));
		edgeF.connect(nodeF, endNode, aDiagram);
		
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
		for (Node node : Arrays.asList(aNodeA, aNodeB, aNodeC, aNodeD, endNode))
		{
			aDiagram.addRootNode(node);
		}
		aEdgeA = new GeneralizationEdge(Type.Implementation);
		aEdgeB = new GeneralizationEdge(Type.Inheritance);
		aEdgeC = new AssociationEdge();
		aEdgeD = new DependencyEdge();
		aEdgeA.connect(aNodeA, endNode, aDiagram);
		aEdgeB.connect(aNodeB, endNode, aDiagram);
		aEdgeC.connect(aNodeC, endNode, aDiagram);
		aEdgeD.connect(aNodeD, endNode, aDiagram);
		for (Edge edge : Arrays.asList(aEdgeA, aEdgeB, aEdgeC, aEdgeD))
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
		
		List<Edge> conflictingEdges = storedConflictingEdges(NodeSide.SOUTH, endNode, aEdgeC);
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
		aEdgeC.connect(aNodeD, aNodeA, aDiagram);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(50, 120));
		aNodeC.moveTo(new Point(150, 120));
		aNodeD.moveTo(new Point(250, 50));
		//aNodeB and aNodeC are South of aNodeA; aNodeD is East of aNodeA
		store(aEdgeA, new EdgePath(new Point(100, 120), new Point(100, 90), new Point(50, 90), new Point(50, 60)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, NodeSide.NORTH));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, NodeSide.NORTH));
	}
	
	@Test
	public void testNodeIsCloserThanSegment_south()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA, aDiagram);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(0, 110));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(200, 0));
		aNodeD.moveTo(new Point(300, 60));
		//aNodeB and aNodeC are North-East of aNodeA; aNodeD is North-North-East of aNodeA
		store(aEdgeA, new EdgePath(new Point(50, 110), new Point(50, 95), new Point(150, 95), new Point(150, 60)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, NodeSide.SOUTH));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, NodeSide.SOUTH));
	}
	
	@Test
	public void testNodeIsCloserThanSegment_east()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA, aDiagram);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(220, 0));
		aNodeB.moveTo(new Point(0, 10));
		aNodeC.moveTo(new Point(0, 70));
		aNodeD.moveTo(new Point(60, 70));
		//aNodeB and aNodeC are SouthWest of aNodeA; aNodeD is South-SouthWest of aNodeA
		store(aEdgeA, new EdgePath(new Point(100, 40), new Point(160, 40), new Point(160, 30), new Point(220, 30)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, NodeSide.EAST));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, NodeSide.EAST));
	}
	
	@Test
	public void testNodeIsCloserThanSegment_west()
	{
		//aEdgeA connects aEdgeB ---> aEdgeA
		//aEdgeB connects aEdgeC ---> aEdgeA
		//aEdgeC connects aEdgeD ---> aEdgeA
		setUpThreeConnectedNodes();
		aEdgeC.connect(aNodeD, aNodeA, aDiagram);
		aDiagram.addRootNode(aNodeD);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 10));
		aNodeC.moveTo(new Point(200, 40));
		aNodeD.moveTo(new Point(140, 60));
		//aNodeB and aNodeC are East of aNodeA; aNodeD is South-East of aNodeA
		store(aEdgeA, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		assertFalse(nodeIsCloserThanSegment(aEdgeB, aNodeA, NodeSide.WEST));
		assertTrue(nodeIsCloserThanSegment(aEdgeC, aNodeA, NodeSide.WEST));
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
		storedEdge.connect(aNodeA, aNodeB, aDiagram);
		newEdge.connect(aNodeA, aNodeB, aDiagram);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(20, 120));
		store(storedEdge, new EdgePath(new Point(50, 60), new Point(50, 90), new Point(70, 90), new Point(70, 120)));
		Point newEdgeStart = new Point(60, 60);
		Point newEdgeEnd = new Point(80, 120);
		assertEquals(100, getHorizontalMidLine(newEdgeStart, newEdgeEnd, NodeSide.SOUTH, newEdge));
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
		newEdge.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(newEdge);
		assertEquals(90, getHorizontalMidLine(new Point(50, 60), new Point(70, 120), NodeSide.SOUTH, newEdge));
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
		storedEdge.connect(aNodeB, aNodeA, aDiagram);
		associationEdge.connect(aNodeC, aNodeA, aDiagram);
		newAggregationEdge.connect(aNodeC, aNodeA, aDiagram);
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
		assertEquals(80, getHorizontalMidLine(startPoint, endPoint, NodeSide.NORTH, associationEdge));
		assertEquals(80, getHorizontalMidLine(startPoint, endPoint, NodeSide.NORTH, newAggregationEdge));
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
		storedEdge.connect(aNodeA, aNodeB, aDiagram);
		newEdge.connect(aNodeA, aNodeB, aDiagram);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 20));
		store(storedEdge, new EdgePath(new Point(100, 30), new Point(150, 30), new Point(150, 50), new Point(200, 50)));
		Point newEdgeStart = new Point(100, 40);
		Point newEdgeEnd = new Point(200, 60);
		assertEquals(160, getVerticalMidLine(newEdgeStart, newEdgeEnd, NodeSide.EAST, newEdge));
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
		newEdge.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(newEdge);
		assertEquals(150, getVerticalMidLine(new Point(100, 30), new Point(200, 50), NodeSide.EAST, newEdge));
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
		storedEdge.connect(aNodeB, aNodeA, aDiagram);
		associationEdge.connect(aNodeC, aNodeA, aDiagram);
		newAggregationEdge.connect(aNodeC, aNodeA, aDiagram);
		aDiagram.addEdge(newAggregationEdge);
		aDiagram.addEdge(associationEdge);
		//aNodeB and aNodeC are both to the left of aNodeA
		//storedEdge connects aNodeA <--- aNodeB
		//newSharedStartEdge connects aNodeA <--- aNodeC 
		//newSharedEndEdge connects aNodeA <--- aNodeC 
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 10));
		aNodeC.moveTo(new Point(200, 60));
		assertEquals(150, getVerticalMidLine(new Point(200, 90), new Point(100, 40), NodeSide.WEST, associationEdge));
		assertEquals(150, getVerticalMidLine(new Point(200, 90), new Point(100, 40), NodeSide.EAST, newAggregationEdge));
		//after storedEdge is stored, the vertical mid-segment of newEdge is shifted 10px
		store(storedEdge, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		assertEquals(140, getVerticalMidLine(new Point(200, 90), new Point(100, 40), NodeSide.WEST, associationEdge));
		assertEquals(140, getVerticalMidLine(new Point(200, 90), new Point(100, 40), NodeSide.WEST, newAggregationEdge));
	}
	
	@Test
	public void testHorizontalMidlineForSharedNodeEdges_aggregationAndGeneralizationEdges()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		Edge storedEdge = new GeneralizationEdge();
		storedEdge.connect(aNodeA, aNodeB, aDiagram);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(20, 120));
		store(storedEdge, new EdgePath(new Point(50, 60), new Point(50, 90), new Point(70, 90), new Point(70, 120)));
		Edge associationEdgeNorth = new AssociationEdge();
		Edge associationEdgeSouth = new AssociationEdge();
		Edge aggregationEdgeNorth = new AggregationEdge();
		Edge aggregationEdgeSouth = new AggregationEdge();
		associationEdgeSouth.connect(aNodeA, aNodeB, aDiagram);
		associationEdgeNorth.connect(aNodeB, aNodeA, aDiagram);
		aggregationEdgeSouth.connect(aNodeA, aNodeB, aDiagram);
		aggregationEdgeNorth.connect(aNodeB, aNodeA, aDiagram);
		assertEquals(100, horizontalMidlineForSharedNodeEdges(storedEdge, associationEdgeSouth, NodeSide.SOUTH));
		assertEquals(80, horizontalMidlineForSharedNodeEdges(storedEdge, associationEdgeNorth, NodeSide.NORTH));
		assertEquals(100, horizontalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeSouth, NodeSide.SOUTH));
		assertEquals(80, horizontalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeNorth, NodeSide.NORTH));
	}
	
	@Test
	public void testVerticalMidlineForSharedNodeEdges_generalizationAndAssociationEdges()
	{
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		Edge storedEdge = new GeneralizationEdge();
		storedEdge.connect(aNodeA, aNodeB, aDiagram);
		//aNodeA is to the left of aNodeB
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 20));
		store(storedEdge, new EdgePath(new Point(100, 30), new Point(150, 30), new Point(150, 50), new Point(200, 50)));
		Edge associationEdgeEast = new AssociationEdge();
		Edge associationEdgeWest = new AssociationEdge();
		Edge aggregationEdgeEast = new AggregationEdge();
		Edge aggregationEdgeWest = new AggregationEdge();
		associationEdgeEast.connect(aNodeA, aNodeB, aDiagram);
		associationEdgeWest.connect(aNodeB, aNodeA, aDiagram);
		aggregationEdgeEast.connect(aNodeA, aNodeB, aDiagram);
		aggregationEdgeWest.connect(aNodeB, aNodeA, aDiagram);
		assertEquals(160, verticalMidlineForSharedNodeEdges(storedEdge, associationEdgeEast, NodeSide.EAST));
		assertEquals(140, verticalMidlineForSharedNodeEdges(storedEdge, associationEdgeWest, NodeSide.WEST));
		assertEquals(160, verticalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeEast, NodeSide.EAST));
		assertEquals(140, verticalMidlineForSharedNodeEdges(storedEdge, aggregationEdgeWest, NodeSide.WEST));
	
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
		newEdge.connect(startNode, aNodeA, aDiagram);
		//Without any conflicting edges in storage: returns empty
		assertEquals(Optional.empty(), closestConflictingVerticalSegment(NodeSide.WEST, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		store(aEdgeB, new EdgePath(new Point(200, 90), new Point(140, 90), new Point(140, 40), new Point(100, 40)));
		assertEquals(aEdgeB, closestConflictingVerticalSegment(NodeSide.WEST, newEdge).get());
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
		newEdge.connect(startNode, aNodeA, aDiagram);
		//Without any conflicting edges in storage: returns empty
		assertEquals(Optional.empty(), closestConflictingVerticalSegment(NodeSide.WEST, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(200, 40), new Point(150, 40), new Point(150, 30), new Point(100, 30)));
		store(aEdgeB, new EdgePath(new Point(200, 90), new Point(140, 90), new Point(140, 40), new Point(100, 40)));
		assertEquals(aEdgeA, closestConflictingVerticalSegment(NodeSide.WEST, newEdge).get());
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
		newEdge.connect(startNode, aNodeA, aDiagram);
		assertEquals(Optional.empty(), closestConflictingHorizontalSegment(NodeSide.NORTH, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(160, 300), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		store(aEdgeB, new EdgePath(new Point(250, 300), new Point(250, 240), new Point(150, 240), new Point(150, 200)));
		assertEquals(aEdgeB, closestConflictingHorizontalSegment(NodeSide.NORTH, newEdge).get());
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
		newEdge.connect(startNode, aNodeA, aDiagram);
		assertEquals(Optional.empty(), closestConflictingHorizontalSegment(NodeSide.NORTH, newEdge));
		//store the edge paths of aEdgeA and aEdgeB:
		store(aEdgeA, new EdgePath(new Point(160, 300), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		store(aEdgeB, new EdgePath(new Point(250, 300), new Point(250, 240), new Point(150, 240), new Point(150, 200)));
		assertEquals(aEdgeA, closestConflictingHorizontalSegment(NodeSide.NORTH, newEdge).get());
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(200,300));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(0, 0));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(250, 300), new Point(250, 180), new Point(150, 180), new Point(150, 60)));
		
		//edge2 is incoming on aNodeA and its EdgeDirection is SOUTH, so it's middle segment should be 10px below pClosestStoredEdge.
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge2, NodeSide.SOUTH));
		
		//edge3 is outgoing from aNodeA and its EdgeDirection is NORTH, so it's middle segment should be 10px below pClosestStoredEdge.
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge3, NodeSide.NORTH));
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(100, 200));
		aNodeC.moveTo(new Point(200, 200));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(150, 200), new Point(150, 130), new Point(50, 130), new Point(50, 60)));
		
		//edge2 is incoming on aNodeA and its EdgeDirection is NORTH, so it's middle segment should be 10px above pClosestStoredEdge.
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge2, NodeSide.NORTH));
		
		//edge3 is outgoing from aNodeA and its EdgeDirection is SOUTH, so it's middle segment should be 10px above pClosestStoredEdge.
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge3, NodeSide.SOUTH));
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0,0));
		aNodeB.moveTo(new Point(100, 200));
		aNodeC.moveTo(new Point(200, 200));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(150, 200), new Point(150, 130), new Point(50, 130), new Point(50, 60)));
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge2, NodeSide.NORTH));
		assertEquals(120, adjacentHorizontalMidLine(edge1, edge3, NodeSide.SOUTH));
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(200,300));
		aNodeB.moveTo(new Point(100, 0));
		aNodeC.moveTo(new Point(0, 0));
		//store the EdgePath of edge1:
		store(edge1, new EdgePath(new Point(250, 300), new Point(250, 180), new Point(150, 180), new Point(150, 60)));
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge2, NodeSide.SOUTH));
		assertEquals(190, adjacentHorizontalMidLine(edge1, edge3, NodeSide.NORTH));
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(0, 100));
		aNodeC.moveTo(new Point(0, 0));
		//Store the path for edge1:
		store(edge1, new EdgePath(new Point(300, 300), new Point(200, 330), new Point(200, 130), new Point(100, 130)));
		
		//With edge2 incoming on aNodeA: the vertical mid-line of edge2 should be 10px to the right of the vertical mid-line of edge1
		assertEquals(210, adjacentVerticalMidLine(edge1, edge2, NodeSide.EAST));
		
		//With edge3 outgoing from aNodeA: the vertical mid-line of edge3 should be 10px to the right of the vertical mid-line of edge1
		assertEquals(210, adjacentVerticalMidLine(edge1, edge3, NodeSide.WEST));
				
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0, 400));
		aNodeB.moveTo(new Point(300, 200));
		aNodeC.moveTo(new Point(300, 100));
		//Add the path of edge1 to storage
		store(edge1, new EdgePath(new Point(100, 430), new Point(200, 430), new Point(200, 230), new Point(300, 230)));
		
		//With edge2 incoming on aNodeA: the vertical mid-line of edge2 should be 10px to the left of the vertical mid-line of edge1
		assertEquals(190, adjacentVerticalMidLine(edge1, edge2, NodeSide.WEST));
		
		//With edge3 outgoing from aNodeA: the vertical mid-line of edge2 should be 10px to the left of the vertical mid-line of edge1
		assertEquals(190, adjacentVerticalMidLine(edge1, edge3, NodeSide.EAST));
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(0, 400));
		aNodeB.moveTo(new Point(300, 200));
		aNodeC.moveTo(new Point(300, 100));
		//Add the path of edge1 to storage
		store(edge1, new EdgePath(new Point(100, 430), new Point(200, 430), new Point(200, 230), new Point(300, 230)));
		assertEquals(190, adjacentVerticalMidLine(edge1, edge2, NodeSide.WEST));
		assertEquals(190, adjacentVerticalMidLine(edge1, edge3, NodeSide.EAST));
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
		edge1.connect(aNodeB, aNodeA, aDiagram);
		edge2.connect(aNodeC, aNodeA, aDiagram);
		edge3.connect(aNodeA, aNodeC, aDiagram);
		aDiagram.addEdge(edge1);
		aDiagram.addEdge(edge2);
		aDiagram.addEdge(edge3);
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(0, 100));
		aNodeC.moveTo(new Point(0, 0));
		//Store the path for edge1:
		store(edge1, new EdgePath(new Point(300, 300), new Point(200, 330), new Point(200, 130), new Point(100, 130)));
		assertEquals(210, adjacentVerticalMidLine(edge1, edge2, NodeSide.EAST));
		assertEquals(210, adjacentVerticalMidLine(edge1, edge3, NodeSide.WEST));
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
		Node endNode = new ClassNode();
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
		edge1.connect(startNode1, endNode, aDiagram);
		edge2.connect(startNode2, endNode, aDiagram);
		edge3.connect(startNode3, endNode, aDiagram);
		edge4.connect(startNode4, endNode, aDiagram);
		edge5.connect(startNode5, endNode, aDiagram);
		edge6.connect(startNode6, endNode, aDiagram);
		//Position nodes so that all start nodes are below and to the right of endNode
		endNode.moveTo(new Point(200, 0));
		startNode1.moveTo(new Point(400, 300));
		startNode2.moveTo(new Point(500, 300));
		startNode3.moveTo(new Point(600, 300));
		startNode4.moveTo(new Point(700, 300));
		startNode5.moveTo(new Point(800, 300));
		startNode6.moveTo(new Point(900, 300));
		//edge1 should attach to index 0 on the South face of endNode
		assertEquals(new Point(250, 60), getConnectionPoint(endNode, edge1, NodeSide.SOUTH));
		store(edge1, new EdgePath(new Point(450, 300), new Point(450, 180), new Point(250, 180), new Point(250, 60)));
		
		//edge2 should attach to index +1 on the South face of endNode
		assertEquals(new Point(260, 60), getConnectionPoint(endNode, edge2, NodeSide.SOUTH));
		store(edge2, new EdgePath(new Point(550, 300), new Point(550, 180), new Point(260, 180), new Point(260, 60)));
		
		//edge3 should attach to index +2 on the South face of endNode
		assertEquals(new Point(270, 60), getConnectionPoint(endNode, edge3, NodeSide.SOUTH));
		store(edge3, new EdgePath(new Point(650, 300), new Point(650, 180), new Point(270, 180), new Point(270, 60)));
		
		//edge4 should attach to position +3 on the South face endNode
		assertEquals(new Point(280, 60), getConnectionPoint(endNode, edge4, NodeSide.SOUTH));
		store(edge4, new EdgePath(new Point(750, 300), new Point(750, 180), new Point(280, 180), new Point(280, 60)));
	
		//edge5 should attach to position +4 on the South face of endNode
		assertEquals(new Point(290, 60), getConnectionPoint(endNode, edge5, NodeSide.SOUTH));
		store(edge5, new EdgePath(new Point(850, 300), new Point(850, 180), new Point(290, 180), new Point(290, 60)));
		
		//when all other connection points on the side of pNode are taken, the default connection point for edge6 is position +4
		//even if it is already occupied
		assertEquals(new Point(290, 60), getConnectionPoint(endNode, edge6, NodeSide.SOUTH));
	}
	
	
	@Test
	public void testGetConnectionPoint_EastWestSides()
	{
		//Initialize 1 end node and 4 start nodes 
		Node endNode = new ClassNode();
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
		edge1.connect(startNode1, endNode, aDiagram);
		edge2.connect(startNode2, endNode, aDiagram);
		edge3.connect(startNode3, endNode, aDiagram);
		edge4.connect(startNode4, endNode, aDiagram);
		//Position nodes so that all start nodes are above and to the left of the end node
		startNode1.moveTo(new Point(0, 0));
		startNode2.moveTo(new Point(0, 100));
		startNode3.moveTo(new Point(0, 200));
		startNode4.moveTo(new Point(0, 300));
		endNode.moveTo(new Point(300, 400));
		
		//edge1 should connect to position 0 on the West side of endNode
		assertEquals(new Point(300, 430), getConnectionPoint(endNode, edge1, NodeSide.WEST));
		store(edge1, new EdgePath(new Point(100, 50), new Point(200, 50), new Point(200, 430), new Point(300, 430)));
		
		//edge2 should connect to position -1 on the West side of endNode
		assertEquals(new Point(300, 420), getConnectionPoint(endNode, edge2, NodeSide.WEST));
		store(edge2, new EdgePath(new Point(100, 150), new Point(200, 150), new Point(200, 420), new Point(300, 420)));
		
		//edge3 should connect to position -2 on the West side of endNode
		assertEquals(new Point(300, 410), getConnectionPoint(endNode, edge3, NodeSide.WEST));
		store(edge3, new EdgePath(new Point(100, 250), new Point(200, 250), new Point(200, 410), new Point(300, 410)));
		
		//since there are no other available negative-index connection points on the West side of endNode, edge4 attaches 
		//to position -2 on the West side of endNode by default
		assertEquals(new Point(300, 410), getConnectionPoint(endNode, edge4, NodeSide.WEST));
	}
	
	
	@Test
	public void testGetSharedNode()
	{
		setUpThreeConnectedNodes();
		//Edges share a common end node:
		assertEquals(aNodeA, getSharedNode(aEdgeA, aEdgeB));
		assertEquals(aNodeA, getSharedNode(aEdgeB, aEdgeA));
		//End node of aEdgeA is the start node of aEdgeB:
		aEdgeB.connect(aNodeA, aNodeC, aDiagram);
		assertEquals(aNodeA, getSharedNode(aEdgeA, aEdgeB));
		assertEquals(aNodeA, getSharedNode(aEdgeB, aEdgeA));
		//start node of aEdgeA is the start node of aEdgeB:
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
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
		storedEdge.connect(storedEdgeStartNode, aNodeA, aDiagram);
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
		storedEdge.connect(storedEdgeStartNode, aNodeA, aDiagram);
		aDiagram.addEdge(storedEdge);
		
		//Reposition nodes so that aNodeA is above all other nodes, and storedEdgeStartNode is in between aNodeB and aNodeC: 
		aNodeA.moveTo(new Point(400, 0));
		aNodeB.moveTo(new Point(300, 300));
		storedEdgeStartNode.moveTo(new Point(400, 300));
		aNodeC.moveTo(new Point(500, 300));
		storedEdgeStartNode.moveTo(new Point(300, 140));
		
		//Store storedEdgePath so it connects to the SOUTH side of aNodeA: (which is the same side that aEdgeA and aEdgeB would connect)
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
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.EAST));
		
		//Move aNodec so that it is below and to the right of aNodeA:
		aNodeC.moveTo(new Point(200, 400));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.EAST));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, NodeSide.EAST));
		
		//Move aNodeB so that it is also below and to the right of aNodeA:
		aNodeB.moveTo(new Point(200, 500));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.EAST));
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
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.WEST));
		
		//move aNodeC so that it is to the left of and below aNodeA:
		aNodeC.moveTo(new Point(0, 400));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.WEST));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, NodeSide.WEST));
		
		//move aNodeB so that it is also to the left of and below aNodeA:
		aNodeB.moveTo(new Point(0, 500));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.WEST));
		
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
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.SOUTH));
		
		//Move aNodeC so that it is below and to the right side of aNodeA:
		aNodeC.moveTo(new Point(400, 300));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.SOUTH));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, NodeSide.SOUTH));
		
		//Move aNodeB so that it is also below and to the right of aNodeA:
		aNodeB.moveTo(new Point(500, 300));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.SOUTH));
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
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.NORTH));
		
		//move aNodeC so that it is above aNodeA and to the right:
		aNodeC.moveTo(new Point(300, 0));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.NORTH));
		assertFalse(nodesOnSameSideOfCommonNode(aNodeC, aNodeB, aNodeA, NodeSide.NORTH));
		
		//move aNodeB so it is also above aNodeA and to the right:
		aNodeB.moveTo(new Point(400, 0));
		assertTrue(nodesOnSameSideOfCommonNode(aNodeB, aNodeC, aNodeA, NodeSide.NORTH));
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
		aggregationEdge.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(aggregationEdge);
		assertEquals(NodeSide.EAST, attachedSide(aggregationEdge, aNodeA));
		assertEquals(NodeSide.WEST, attachedSide(aggregationEdge, aNodeB));	
	}
	
	@Test
	public void testAttachedSide_generalizationEdge()
	{
		//aNodeB is 400px below and 400px to the right of aNodeA. aEdgeA connects from aEgdeA to aEdgeB.
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(400, 400));
		assertEquals(NodeSide.SOUTH, attachedSide(aEdgeA, aNodeA));
		assertEquals(NodeSide.NORTH, attachedSide(aEdgeA, aNodeB));	
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
		newEdge.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(newEdge);
		assertEquals(NodeSide.EAST, attachedSide(newEdge, aNodeA));
		assertEquals(NodeSide.WEST, attachedSide(newEdge, aNodeB));
			
	}
	
	@Test
	public void testAttachedSidePreferringEastWest_nodesAboveAndBelowEachOther()
	{
		//aNodeA is directly above aNodeB. aEdgeA connected aNodeA to aNode B. aEdgeB connects aNodeB to aNodeA.
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 200));
		aEdgeB.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aEdgeB);
		assertEquals(NodeSide.SOUTH, attachedSidePreferringEastWest(aEdgeA));
		assertEquals(NodeSide.NORTH, attachedSidePreferringEastWest(aEdgeB));
	}
	
	
	@Test
	public void testAttachedSidePreferringEastWest_nodesBesideEachOther()
	{
		//aNodeA is directly to the left of aNodeA. The nodes are connected by aEdgeA and aEdgeB in both directions
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 0));
		aEdgeB.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aEdgeB);
		assertEquals(NodeSide.EAST, attachedSidePreferringEastWest(aEdgeA));
		assertEquals(NodeSide.WEST, attachedSidePreferringEastWest(aEdgeB));
	}
	
	
	@Test
	public void testAttachedSidePreferringNorthSouth_nodesAboveAndBelowEachother()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(100, 200));
		//Also connect aEdgeB from aNodeB to aNodeA to test the method for both East and West directions
		aEdgeB.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aEdgeB);
		assertEquals(NodeSide.SOUTH, attachedSidePreferringNorthSouth(aEdgeA));
		assertEquals(NodeSide.NORTH, attachedSidePreferringNorthSouth(aEdgeB));
	}
	
	@Test
	public void testAttachedSidePreferringNorthSouth_nodesBesideEachOther()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(200, 0));
		//Also connect aEdgeB aNodeA <---- aNodeB to test the method for both East and West directions
		aEdgeB.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aEdgeB);
		assertEquals(NodeSide.EAST, attachedSidePreferringNorthSouth(aEdgeA));
		assertEquals(NodeSide.WEST, attachedSidePreferringNorthSouth(aEdgeB));
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
		assertEquals(NodeSide.NORTH, eastWestSideUnlessTooClose(aEdgeB)); 
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
		assertEquals(NodeSide.EAST, eastWestSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void TestNorthSouthSideUnlessNodesTooClose_nodesNotTooClose()
	{
		setUpThreeConnectedNodes();
		//store the EdgePath for aEdgeA, from aNodeB to aNodeA 
		store(aEdgeA, new EdgePath(new Point(160, 330), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		assertEquals(NodeSide.NORTH, northSouthSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void testNorthSouthSideUnlessNodesTooClose_nodesTooClose()
	{
		setUpThreeConnectedNodes();
		//aNodeC is closer to aNodeA than the mid-segment of aEdgeA is
		aNodeC.moveTo(new Point(200, 210));
		store(aEdgeA, new EdgePath(new Point(160, 330), new Point(160, 250), new Point(150, 250), new Point(150, 200)));
		assertEquals(NodeSide.WEST, northSouthSideUnlessTooClose(aEdgeB)); 
	}
	
	@Test
	public void testGetStoredEdgePath_edgeInStorage()
	{
		EdgePath path = new EdgePath(new Point(0,0), new Point(100, 100));
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(aEdgeA);
		RenderingFacade.classDiagramRenderer().store(aEdgeA, path);
		assertEquals(path, getStoredEdgePath(aEdgeA));
	}
	
	@Test
	public void testStorageContains()
	{
		setUpTwoConnectedNodes();
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA,  new EdgePath(new Point(130, 60), new Point(130, 130), new Point(130, 130), new Point(130, 200)));
		assertTrue(storageContains(aEdgeA));
	}
	
	@Test
	public void testBuildSegmentedEdgePath_verticalEdgeDirection()
	{
		EdgePath expectedResult_north = new EdgePath(new Point(100,300), new Point(100, 150), new Point(300, 150), new Point(300, 0));
		EdgePath expectedResult_south = new EdgePath(new Point(300,0), new Point(300, 150), new Point(100, 150), new Point(100, 300));
		assertEquals(expectedResult_north, buildSegmentedEdgePath(NodeSide.NORTH, new Point(100, 300), 150, new Point(300,0)));
		assertEquals(expectedResult_south, buildSegmentedEdgePath(NodeSide.SOUTH, new Point(300, 0), 150, new Point(100,300)));
	}
	
	@Test
	public void testBuildSegmentedEdgePath_horizontalEdgeDirection()
	{
		EdgePath expectedResult_east = new EdgePath(new Point(100,300), new Point(200, 300), new Point(200, 200), new Point(300, 200));
		EdgePath expectedResult_west = new EdgePath(new Point(300,200), new Point(200, 200), new Point(200, 300), new Point(100, 300));
		assertEquals(expectedResult_east, buildSegmentedEdgePath(NodeSide.EAST, new Point(100, 300), 200, new Point(300,200)));
		assertEquals(expectedResult_west, buildSegmentedEdgePath(NodeSide.WEST, new Point(300, 200), 200, new Point(100, 300)));
	}

	@Test
	public void testAttachedSideFromStorage_north()
	{
		aNodeA.moveTo(new Point(100, 0));
		aNodeB.moveTo(new Point(100, 200));
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(130, 60), new Point(130, 130), new Point(130, 130), new Point(130, 200)));
		assertEquals(NodeSide.NORTH, attachedSideFromStorage(aEdgeA, aNodeB));
	}
	
	@Test
	public void testAttachedSideFromStorage_south()
	{
		aNodeA.moveTo(new Point(100, 0));
		aNodeB.moveTo(new Point(100, 200));
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(130, 60), new Point(130, 130), new Point(130, 130), new Point(130, 200)));
		assertEquals(NodeSide.SOUTH, attachedSideFromStorage(aEdgeA, aNodeA));
	}
	
	@Test
	public void testAttachedSideFromStorage_east()
	{
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(200, 300));
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(260, 330), new Point(280, 330), new Point(280, 330), new Point(300, 330)));
		assertEquals(NodeSide.EAST, attachedSideFromStorage(aEdgeA, aNodeB));
	}
	
	@Test
	public void testAttachedSideFromStorage_west()
	{
		aNodeA.moveTo(new Point(300, 300));
		aNodeB.moveTo(new Point(200, 300));
		aEdgeA.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(260, 330), new Point(280, 330), new Point(280, 330), new Point(300, 330)));
		assertEquals(NodeSide.WEST, attachedSideFromStorage(aEdgeA, aNodeA));
	}
	
	
	@Test
	public void testVerticalDistanceToNode()
	{
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(0, 400));
		store(aEdgeA, new EdgePath(new Point(30, 60), new Point(30, 230), new Point(30, 230), new Point(30, 400)));
		assertEquals(170, verticalDistanceToNode(aNodeB, aEdgeA, NodeSide.SOUTH));		
	}
	
	@Test
	public void testHorizontalDistanceToNode()
	{
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		aNodeA.moveTo(new Point(0, 0));
		aNodeB.moveTo(new Point(400, 10));
		aDiagram.addEdge(aEdgeA);
		store(aEdgeA, new EdgePath(new Point(100, 30), new Point(225, 30), new Point(225, 40), new Point(350, 40)));
		assertEquals(175, horizontalDistanceToNode(aNodeB, aEdgeA, NodeSide.WEST));		
	}

	@Test
	public void testGetIndexSign_edgeSharingBothNodes()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(70, 0));
		aEdgeB.connect(aNodeA, aNodeB, aDiagram);
		aDiagram.addEdge(aEdgeB);
		//aEdgeA connects aNodeA --> aNodeB
		//aEdgeB connects aNodeB --> aNodeA
		store(aEdgeA, new EdgePath(new Point(100, 100), new Point(70, 0)));
		assertSame( 1, getIndexSign(aEdgeB, aNodeA, NodeSide.NORTH));
		assertSame( 1, getIndexSign(aEdgeB, aNodeB, NodeSide.SOUTH));
	}
	
	@Test
	public void testGetIndexSign_edgeNotSharingBothNodes()
	{
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(70, 0));
		aDiagram.addEdge(aEdgeA);
		assertSame(-1, getIndexSign(aEdgeA, aNodeA, NodeSide.NORTH));
	}
	
	@Test
	public void testIndexSignOnNode_otherNodeAbove()
	{
		setUpTwoConnectedNodes();
		//Other node directly above start node
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(100, 0));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.NORTH));
		
		//Other node above and to the left of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(450, 300));
		assertSame(-1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.NORTH));
		
		//Other node above and to the right of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(550, 300));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.NORTH));
		
	}
	
	@Test
	public void testIndexSignOnNode_otherNodeBelow()
	{
		//end node directly below start node
		setUpTwoConnectedNodes();
		aNodeB.moveTo(new Point(100, 100));
		aNodeA.moveTo(new Point(100, 400));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.SOUTH));
		
		//End node above and to the left of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(450, 600));
		assertSame(-1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.SOUTH));
		
		//End node above and to the right of start node
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(550, 600));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.SOUTH));
	}
	
	@Test
	public void testIndexSignOnNode_otherNodeOnRight()
	{
		//end node directly to right of start node
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(100, 100));
		aNodeB.moveTo(new Point(400, 100));
		assertSame(indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.EAST), 1);
		
		//End node to the right of start node and slightly up 
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(700, 450));
		assertSame(indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.EAST), -1);
		
		//End node to the right of start node and slightly down
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(700, 550));
		assertSame(indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.EAST), 1);
	}

	
	@Test
	public void testIndexSignOnNode_otherNodeOnLeft()
	{
		//end node directly to left of start node
		setUpTwoConnectedNodes();
		aNodeA.moveTo(new Point(200, 200));
		aNodeB.moveTo(new Point(100, 200));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.WEST));
		
		//End node to the left of start node and slightly up 
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(400, 450));
		assertSame(-1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.WEST));
		
		//End node to the left of start node and slightly down
		aNodeA.moveTo(new Point(500, 500));
		aNodeB.moveTo(new Point(400, 550));
		assertSame(1, indexSignOnNode(aEdgeA, aNodeA, aNodeB, NodeSide.WEST));
	}
	
	@Test 
	public void testGetClosestPoint_north()
	{
		assertEquals(new Point(200, 290), getClosestPoint(getPoints(), NodeSide.NORTH));
	}
	
	@Test 
	public void testGetClosestPoint_south()
	{
		assertEquals(new Point(200, 310), getClosestPoint(getPoints(), NodeSide.SOUTH));
	}
	

	@Test 
	public void testGetClosestPoint_east()
	{
		assertEquals(new Point(210, 300), getClosestPoint(getPoints(), NodeSide.EAST));
	}
	
	@Test 
	public void testGetClosestPoint_west()
	{
		assertEquals(new Point(190, 300), getClosestPoint(getPoints(), NodeSide.WEST));
	}
	
	
	@Test
	public void testGetOtherNode()
	{
		aEdgeA.connect(aNodeB, aNodeA, aDiagram);
		assertSame(aNodeB, getOtherNode(aEdgeA, aNodeA));
		assertSame(aNodeA, getOtherNode(aEdgeA, aNodeB));
	}
	
	@Test
	public void testGetNodeFace_north()
	{
		assertEquals(new Point(200, 200), getNodeFace(aRectangleA, NodeSide.NORTH).getPoint1());
		assertEquals(new Point(300, 200), getNodeFace(aRectangleA, NodeSide.NORTH).getPoint2());
	}
	
	@Test
	public void testGetNodeFace_south()
	{
		assertEquals(new Point(200, 260), getNodeFace(aRectangleA, NodeSide.SOUTH).getPoint1());
		assertEquals(new Point(300, 260), getNodeFace(aRectangleA, NodeSide.SOUTH).getPoint2());
	}
	
	@Test
	public void testGetNodeFace_east()
	{
		assertEquals(new Point(300,200), getNodeFace(aRectangleA, NodeSide.EAST).getPoint1());
		assertEquals(new Point(300,260), getNodeFace(aRectangleA, NodeSide.EAST).getPoint2());
	}
	
	@Test
	public void testGetNodeFace_west()
	{
		assertEquals(new Point(200, 200), getNodeFace(aRectangleA, NodeSide.WEST).getPoint1());
		assertEquals(new Point(200, 260), getNodeFace(aRectangleA, NodeSide.WEST).getPoint2());
	}
	
	@Test
	public void testIsOutgoingEdge()
	{
		aEdgeA.connect(aNodeA, aNodeB, aDiagram);
		assertTrue(isOutgoingEdge(aEdgeA, aNodeA));
		assertFalse(isOutgoingEdge(aEdgeA, aNodeB));
	}
	
	@Test
	public void testNorthOrSouthSide()
	{
		assertEquals(NodeSide.NORTH, northOrSouthSide(aRectangleA, aRectangleB));
		assertEquals(NodeSide.SOUTH, northOrSouthSide(aRectangleB, aRectangleA));
		assertEquals(NodeSide.SOUTH, northOrSouthSide(aRectangleA, aRectangleA));
	}
	
	@Test
	public void testEastOrWestSide()
	{
		assertEquals(NodeSide.WEST, eastOrWestSide(aRectangleA, aRectangleC));
		assertEquals(NodeSide.EAST, eastOrWestSide(aRectangleC, aRectangleA));
		assertEquals(NodeSide.EAST, eastOrWestSide(aRectangleA, aRectangleA));
	}
	
	
	
	
	
	
	/// REFLECTIVE HELPER METHODS ///
	
	
	private void layoutSegmentedEdges(Diagram pDiagram, EdgePriority pEdgePriority)
	{
		try 
		{
			Method method = Layouter.class.getDeclaredMethod("layoutSegmentedEdges", Diagram.class, EdgePriority.class);
			method.setAccessible(true);
			method.invoke(aLayouter, pDiagram, pEdgePriority);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	
	private void storeMergedEndEdges(NodeSide pDirection, List<Edge> pEdgesToMerge, Diagram pDiagram)
	{
		try 
		{
			Method method = Layouter.class.getDeclaredMethod("storeMergedEndEdges", NodeSide.class, List.class, Diagram.class);
			method.setAccessible(true);
			method.invoke(aLayouter, pDirection, pEdgesToMerge, pDiagram);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void storeMergedStartEdges(NodeSide pDirection, List<Edge> pEdgesToMerge, Diagram pDiagram)
	{
		try 
		{
			Method method = Layouter.class.getDeclaredMethod("storeMergedStartEdges", NodeSide.class, List.class, Diagram.class);
			method.setAccessible(true);
			method.invoke(aLayouter, pDirection, pEdgesToMerge, pDiagram);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void layoutDependencyEdges(Diagram pDiagram)
	{
		try 
		{
			Method method = Layouter.class.getDeclaredMethod("layoutDependencyEdges", Diagram.class);
			method.setAccessible(true);
			method.invoke(aLayouter, pDiagram);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private void layoutSelfEdges(Diagram pDiagram)
	{
		try 
		{
			Method method = Layouter.class.getDeclaredMethod("layoutSelfEdges", Diagram.class);
			method.setAccessible(true);
			method.invoke(aLayouter, pDiagram);
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
			Method method = Layouter.class.getDeclaredMethod("buildSelfEdge", Edge.class, NodeCorner.class);
			method.setAccessible(true);
			return (EdgePath) method.invoke(aLayouter, pEdge, pCorner);
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
			Method method = Layouter.class.getDeclaredMethod("getSelfEdgeCorner", Edge.class);
			method.setAccessible(true);
			return (NodeCorner) method.invoke(aLayouter, pEdge);
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
			Method method = Layouter.class.getDeclaredMethod("getEdgesToMergeStart", Edge.class, List.class);
			method.setAccessible(true);
			return (Collection<Edge>) method.invoke(aLayouter, pEdge, pEdges);
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
			Method method = Layouter.class.getDeclaredMethod("getEdgesToMergeEnd", Edge.class, List.class);
			method.setAccessible(true);
			return (Collection<Edge>) method.invoke(aLayouter, pEdge, pEdges);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Edge> storedConflictingEdges(NodeSide pNodeFace, Node pNode, Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("storedConflictingEdges", NodeSide.class, Node.class, Edge.class);
			method.setAccessible(true);
			return (List<Edge>) method.invoke(aLayouter, pNodeFace, pNode, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private boolean nodeIsCloserThanSegment(Edge pEdge, Node pNode, NodeSide pAttachedSide)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("nodeIsCloserThanSegment", Edge.class, Node.class, NodeSide.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pEdge, pNode, pAttachedSide);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private int getHorizontalMidLine(Point pStart, Point pEnd, NodeSide pEdgeDirection, Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getHorizontalMidLine", Point.class, Point.class, NodeSide.class, Edge.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pStart, pEnd, pEdgeDirection,pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	
	private int getVerticalMidLine(Point pStart, Point pEnd, NodeSide pEdgeDirection, Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getVerticalMidLine", Point.class, Point.class, NodeSide.class, Edge.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pStart, pEnd, pEdgeDirection, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	
	private int horizontalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, NodeSide pEdgeDirection)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("horizontalMidlineForSharedNodeEdges", Edge.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pEdgeWithSameNodes, pNewEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int verticalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, NodeSide pEdgeDirection) 
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("verticalMidlineForSharedNodeEdges", Edge.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pEdgeWithSameNodes, pNewEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Edge> closestConflictingVerticalSegment(NodeSide pEdgeDirection,Edge pEdge) 
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("closestConflictingVerticalSegment", NodeSide.class, Edge.class);
			method.setAccessible(true);
			return (Optional<Edge>) method.invoke(aLayouter, pEdgeDirection, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Edge> closestConflictingHorizontalSegment( NodeSide pEdgeDirection, Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("closestConflictingHorizontalSegment", NodeSide.class, Edge.class);
			method.setAccessible(true);
			return (Optional<Edge>) method.invoke(aLayouter, pEdgeDirection, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private int adjacentHorizontalMidLine(Edge pClosestStoredEdge, Edge pEdge, NodeSide pEdgeDirection)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("adjacentHorizontalMidLine", Edge.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pClosestStoredEdge, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int adjacentVerticalMidLine(Edge pClosestStoredEdge, Edge pEdge, NodeSide pEdgeDirection)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("adjacentVerticalMidLine", Edge.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pClosestStoredEdge, pEdge, pEdgeDirection);
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
			Method method = Layouter.class.getDeclaredMethod("getSharedNode", Edge.class, Edge.class);
			method.setAccessible(true);
			return (Node) method.invoke(aLayouter, pEdgeA, pEdgeB);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Point getConnectionPoint(Node pNode, Edge pEdge, NodeSide pAttachmentSide)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getConnectionPoint", Node.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (Point) method.invoke(aLayouter, pNode, pEdge, pAttachmentSide);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static boolean noOtherEdgesBetween(Edge pEdge1, Edge pEdge2, Node pNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("noOtherEdgesBetween", Edge.class, Edge.class, Node.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pEdge1, pEdge2, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private static boolean nodesOnSameSideOfCommonNode(Node pNode1, Node pNode2, Node pCommonNode, NodeSide pAttachedSide)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("nodesOnSameSideOfCommonNode", Node.class, Node.class, Node.class, NodeSide.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pNode1, pNode2, pCommonNode, pAttachedSide);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private static boolean noConflictingStartLabels(Edge pEdge1, Edge pEdge2)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("noConflictingStartLabels", Edge.class, Edge.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pEdge1, pEdge2);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private static boolean noConflictingEndLabels(Edge pEdge1, Edge pEdge2)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("noConflictingEndLabels", Edge.class, Edge.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pEdge1, pEdge2);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	
	private static NodeSide attachedSide(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("attachedSide", Edge.class, Node.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static NodeSide attachedSidePreferringEastWest(Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("attachedSidePreferringEastWest", Edge.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}

	private static NodeSide attachedSidePreferringNorthSouth(Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("attachedSidePreferringNorthSouth", Edge.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static NodeSide eastWestSideUnlessTooClose(Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("eastWestSideUnlessTooClose", Edge.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	
	private static NodeSide northSouthSideUnlessTooClose(Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("northSouthSideUnlessTooClose", Edge.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	
	private static void store(Edge pEdge, EdgePath pEdgePath)
	{
		try
		{
			Field privateField = ClassDiagramRenderer.class.getDeclaredField("aEdgeStorage");
			privateField.setAccessible(true);
			EdgeStorage storage = (EdgeStorage) privateField.get(RenderingFacade.classDiagramRenderer());
			storage.store(pEdge, pEdgePath);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private static EdgePath getStoredEdgePath(Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getStoredEdgePath", Edge.class);
			method.setAccessible(true);
			return (EdgePath) method.invoke(aLayouter, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static boolean storageContains(Edge pEdge)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("storageContains", Edge.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private static EdgePath buildSegmentedEdgePath(NodeSide pEdgeDirection, Point pStart, int pMidLine, Point pEnd)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("buildSegmentedEdgePath", NodeSide.class, Point.class, int.class, Point.class);
			method.setAccessible(true);
			return (EdgePath) method.invoke(aLayouter, pEdgeDirection, pStart, pMidLine, pEnd);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return (EdgePath) null;
		}
	}
	
	private static NodeSide attachedSideFromStorage(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("attachedSideFromStorage", Edge.class, Node.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return (NodeSide) null;
		}
	}
	
	private int verticalDistanceToNode(Node pEndNode, Edge pEdge, NodeSide pEdgeDirection)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("verticalDistanceToNode", Node.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pEndNode, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private int horizontalDistanceToNode(Node pEndNode, Edge pEdge, NodeSide pEdgeDirection)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("horizontalDistanceToNode", Node.class, Edge.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pEndNode, pEdge, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private static int getIndexSign(Edge pEdge, Node pNode, NodeSide pSideOfNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getIndexSign", Edge.class, Node.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pEdge, pNode, pSideOfNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	private static int indexSignOnNode(Edge pEdge, Node pNode, Node pOtherNode, NodeSide pSideOfNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("indexSignOnNode", Edge.class, Node.class, Node.class, NodeSide.class);
			method.setAccessible(true);
			return (int) method.invoke(aLayouter, pEdge, pNode, pOtherNode, pSideOfNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return -1;
		}
	}
	
	
	private List<Point> getPoints()
	{
		List<Point> result = new ArrayList<>();
		result.add(new Point(190, 300));
		result.add(new Point(200, 310));
		result.add(new Point(210, 300));
		result.add(new Point(200, 290));
		return result;
	}
	
	private static Point getClosestPoint(Collection<Point> pPoints, NodeSide pEdgeDirection) 
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getClosestPoint", Collection.class, NodeSide.class);
			method.setAccessible(true);
			return (Point) method.invoke(aLayouter, pPoints, pEdgeDirection);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static Node getOtherNode(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getOtherNode", Edge.class, Node.class);
			method.setAccessible(true);
			return (Node) method.invoke(aLayouter, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static Line getNodeFace(Rectangle pNodeBounds, NodeSide pSideOfNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("getNodeFace", Rectangle.class, NodeSide.class);
			method.setAccessible(true);
			return (Line) method.invoke(aLayouter, pNodeBounds, pSideOfNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}

	private static boolean isOutgoingEdge(Edge pEdge, Node pNode)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("isOutgoingEdge", Edge.class, Node.class);
			method.setAccessible(true);
			return (boolean) method.invoke(aLayouter, pEdge, pNode);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private static NodeSide northOrSouthSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("northOrSouthSide", Rectangle.class, Rectangle.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pBounds, pOtherBounds);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static NodeSide eastOrWestSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		try
		{
			Method method = Layouter.class.getDeclaredMethod("eastOrWestSide", Rectangle.class, Rectangle.class);
			method.setAccessible(true);
			return (NodeSide) method.invoke(aLayouter, pBounds, pOtherBounds);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}

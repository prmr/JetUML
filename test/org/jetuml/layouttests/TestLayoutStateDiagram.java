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
package org.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.nodes.NoteNodeRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutStateDiagram extends AbstractTestStateDiagramLayout 
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.state.jet");
	
	TestLayoutStateDiagram() throws IOException 
	{
		super(PATH);
	}

	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@ParameterizedTest
	@CsvSource({"S1, 250, 100",
				"S2, 510, 100",
				"S3, 520, 310"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@ParameterizedTest
	@CsvSource({"NoteNode, 690, 320",
		"InitialStateNode, 150, 70",
		"FinalStateNode, 640, 230"})
	void testNodePositionByType(String pClassName, int pExpectedX, int pExpectedY)
	{
		String fullyQualifiedClassName = "org.jetuml.diagram.nodes." + pClassName;
		try 
		{
			verifyPosition(nodesByType(Class.forName(fullyQualifiedClassName)).get(0), pExpectedX, pExpectedY);
		} 
		catch (ClassNotFoundException e) 
		{
			fail();
		}
	}
	
	/**
	 * Tests that all state nodes that are supposed to have the default dimension actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"S1", "S2", "S3"})
	void testStateNodesDefaultDimension(String pNodeName)
	{
		verifyStateNodeDefaultDimensions(nodeByName(pNodeName));
	}
	
	/**
	 * Tests that the note node is expanded horizontally. 
	 */
	@Test
	void testNoteNodeIsExpandedHorizontally()
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(NoteNodeRenderer.class, "DEFAULT_WIDTH");
		Rectangle bounds = aRenderer.getBounds(nodesByType(NoteNode.class).get(0));
		assertTrue(bounds.width() > DEFAULT_WIDTH);
	}
	
	/**
	 * Tests that the initial and final state nodes have the default dimensions. 
	 */
	@Test
	void testCircularStateNodesDefaultDimension()
	{
		verifyCircularStateNodeDefaultDimensions(nodesByType(InitialStateNode.class).get(0));
		verifyCircularStateNodeDefaultDimensions(nodesByType(FinalStateNode.class).get(0));
	}
	
	/**
	 * Tests that the transition edge connects to its node boundaries. 
	 */
	@Test
	void testTransitionEdgeFromInitialStateToS1()
	{
		Rectangle boundsInitialState = aRenderer.getBounds(nodesByType(InitialStateNode.class).get(0));
		Rectangle boundsS1 = aRenderer.getBounds(nodeByName("S1"));
		Line edgeLine = aRenderer.getConnectionPoints(edgeByMiddleLabel("start"));
		assertWithDefaultTolerance(boundsInitialState.maxX(), edgeLine.point1().x());
		assertWithDefaultTolerance(boundsS1.x(), edgeLine.point2().x());
	}
	
	/**
	 * Tests that the transition edge connects to its node boundaries. 
	 */
	@Test
	void testTransitionEdgesFromS1ToS2()
	{
		Rectangle boundsS1 = aRenderer.getBounds(nodeByName("S1"));
		Rectangle boundsS2 = aRenderer.getBounds(nodeByName("S2"));
		Line edgeLine = aRenderer.getConnectionPoints(edgeByMiddleLabel("e1"));
		assertWithDefaultTolerance(boundsS1.maxX(), edgeLine.point1().x());
		assertWithDefaultTolerance(boundsS2.x(), edgeLine.point2().x());
	}
	
	/**
	 * Tests that the transition edge connects to its node boundaries. 
	 */
	@Test
	void testTransitionEdgesFromS2ToS1()
	{
		Rectangle boundsS1 = aRenderer.getBounds(nodeByName("S1"));
		Rectangle boundsS2 = aRenderer.getBounds(nodeByName("S2"));
		Line edgeLine = aRenderer.getConnectionPoints(edgeByMiddleLabel("e2"));
		assertWithDefaultTolerance(boundsS1.maxX(), edgeLine.point2().x());
		assertWithDefaultTolerance(boundsS2.x(), edgeLine.point1().x());
	}
	
	/**
	 * Tests that the transition edge connects to its node boundaries. 
	 */
	@Test
	void testSelfTransitionEdgeOnS2()
	{
		Rectangle boundsS2 = aRenderer.getBounds(nodeByName("S2"));
		Point arrowBaseConnectionPoint = aRenderer.getConnectionPoints(edgeByMiddleLabel("self")).point1();
		Point arrowHeadConnectionPoint = aRenderer.getConnectionPoints(edgeByMiddleLabel("self")).point2();
		assertWithDefaultTolerance(arrowBaseConnectionPoint.y(), boundsS2.y());
		assertWithDefaultTolerance(arrowHeadConnectionPoint.x(), boundsS2.maxX());
	}
	
	/**
	 * Tests that the transition edge connects to its node boundaries. 
	 */
	@Test
	void testTransitionEdgeFromS2ToS3()
	{
		Rectangle boundsS2 = aRenderer.getBounds(nodeByName("S2"));
		Rectangle boundsS3 = aRenderer.getBounds(nodeByName("S3"));
		Edge transitionEdge = edgesByType(StateTransitionEdge.class).stream()
				.filter(edge -> edge.end().equals(nodeByName("S3")))
				.findFirst()
				.get();
		Line edgeLine = aRenderer.getConnectionPoints(transitionEdge);
		assertWithDefaultTolerance(boundsS2.maxY(), edgeLine.point1().y());
		assertWithDefaultTolerance(boundsS3.y(), edgeLine.point2().y());
	}
	
	/**
	 * Tests that the transition edge connects to S3's boundary and falls within the final state bounds. 
	 */
	@Test
	void testTransitionEdgeFromS3ToFinalState()
	{
		Rectangle boundsS3 = aRenderer.getBounds(nodeByName("S3"));
		Rectangle boundsFinalState = aRenderer.getBounds(nodesByType(FinalStateNode.class).get(0));
		Edge transitionEdge = edgesByType(StateTransitionEdge.class).stream()
				.filter(edge -> edge.end().equals(nodesByType(FinalStateNode.class).get(0)))
				.findFirst()
				.get();
		Line edgeLine = aRenderer.getConnectionPoints(transitionEdge);
		assertWithDefaultTolerance(boundsS3.y(), edgeLine.point1().y());
		assertTrue(boundsFinalState.contains(edgeLine.point2()));
	}
	
	/**
	 * Tests that the transition edge connects to the note node's boundary and falls within S3. 
	 */
	@Test
	void testNoteEdgeBetweenS3AndNoteNode()
	{
		Rectangle boundsS3 = aRenderer.getBounds(nodeByName("S3"));
		Rectangle boundsNoteNode = aRenderer.getBounds(nodesByType(NoteNode.class).get(0));
		Line edgeLine = aRenderer.getConnectionPoints(edgesByType(NoteEdge.class).get(0));
		assertWithDefaultTolerance(boundsNoteNode.x(), edgeLine.point1().x());
		assertTrue(boundsS3.contains(edgeLine.point2()));
	}
}

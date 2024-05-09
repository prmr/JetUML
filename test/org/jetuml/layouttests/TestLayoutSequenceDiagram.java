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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.PropertyName;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.geom.Line;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutSequenceDiagram extends AbstractTestSequenceDiagramLayout 
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.sequence.jet");

	TestLayoutSequenceDiagram() throws IOException 
	{
		super(PATH);
	}

	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@ParameterizedTest
	@CsvSource({"object1:Type1, 160, 0",
				":Type2, 370, 0",
				"object3:, 590, 0",
				"A note, 440, 200"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@Test
	void testCallNodePositionsBelowObject1Type1Node()
	{
		Node largerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").start()))
				.findFirst()
				.get();
		Node smallerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").end()))
				.findFirst()
				.get();
		verifyPosition(largerCallNode, 202, 77);
		verifyPosition(smallerCallNode, 210, 106);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@Test
	void testCallNodePositionsBelowType2Node()
	{
		Node callNode = nodeByName(":Type2").getChildren().get(0);
		verifyPosition(callNode, 402, 125);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@Test
	void testCallNodePositionsBelowObject3Node()
	{
		Node callNode = nodeByName("object3:").getChildren().get(0);
		verifyPosition(callNode, 622, 149);
	}
	
	/**
	 * Tests that all implicit parameter nodes that are supposed to have the default height actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"object1:Type1", ":Type2", "object3:"})
	void testImplicitParameterNodeDefaultHeight(String pNodeName)
	{
		verifyImplicitParameterNodeTopRectangleDefaultHeight(nodeByName(pNodeName));
	}
	
	/**
	 * Tests that the call nodes have the default width.
	 */
	@Test
	void testCallNodeDefaultWidthBelowObject1Type1Node()
	{
		Node largerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").start()))
				.findFirst()
				.get();
		Node smallerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").end()))
				.findFirst()
				.get();
		aRenderer.getBounds(); // Trigger rendering pass
		verifyCallNodeDefaultWidth(largerCallNode);
		verifyCallNodeDefaultWidth(smallerCallNode);
	}
	
	/**
	 * Tests that the call node has the default width.
	 */
	@Test
	void testCallNodeDefaultWidthBelowType2Node()
	{
		Node callNode = nodeByName(":Type2").getChildren().get(0);
		aRenderer.getBounds(); // Trigger rendering pass
		verifyCallNodeDefaultWidth(callNode);
	}
	
	/**
	 * Tests that the call node has the default width.
	 */
	@Test
	void testCallNodeDefaultWidthBelowObject3Node()
	{
		Node callNode = nodeByName("object3:").getChildren().get(0);
		aRenderer.getBounds(); // Trigger rendering pass
		verifyCallNodeDefaultWidth(callNode);
	}
	
	/**
	 * Tests that the note node has the default dimensions.
	 */
	@Test
	void testNoteNodeDefaultDimensions()
	{
		verifyNoteNodeDefaultDimensions(nodeByName("A note"));
	}
	
	/**
	 * Tests that the call edge connects to its node boundaries. 
	 */
	@Test
	void testSelfCallEdge()
	{
		Node largerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").start()))
				.findFirst()
				.get();
		Node smallerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").end()))
				.findFirst()
				.get();
		aRenderer.getBounds(); // Trigger rendering pass
		Line selfCallEdgeLine = aRenderer.getConnectionPoints(edgeByMiddleLabel("selfCall()"));
		Rectangle largerCallNodeBounds = aRenderer.getBounds(largerCallNode);
		Rectangle smallerCallNodeBounds = aRenderer.getBounds(smallerCallNode);
		assertEquals(largerCallNodeBounds.maxX(), selfCallEdgeLine.point1().x());
		assertEquals(smallerCallNodeBounds.maxX(), selfCallEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the call edge connects to its node boundaries. 
	 */
	@Test
	void testSignalCallEdge()
	{
		aRenderer.getBounds(); // Trigger rendering pass
		Edge signalCallEdge = edgeByMiddleLabel("signal");
		Node startNode = nodeByName("object1:Type1").getChildren().get(1);
		Node endNode = nodeByName(":Type2").getChildren().get(0);
		Line signalCallEdgeLine = aRenderer.getConnectionPoints(signalCallEdge);
		Rectangle startNodeBounds = aRenderer.getBounds(startNode);
		Rectangle endNodeBounds = aRenderer.getBounds(endNode);
		assertEquals(startNodeBounds.maxX(), signalCallEdgeLine.point1().x());
		assertEquals(endNodeBounds.x(), signalCallEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the call edge connects to its node boundaries. 
	 */
	@Test
	void testCall1CallEdge()
	{
		Edge call1Edge = edgeByMiddleLabel("call1()");
		Node startNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(call1Edge.start()))
				.findFirst()
				.get();
		Node endNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(call1Edge.end()))
				.findFirst()
				.get();
		aRenderer.getBounds(); // Trigger rendering pass
		Line call1EdgeLine = aRenderer.getConnectionPoints(call1Edge);
		Rectangle startNodeBounds = aRenderer.getBounds(startNode);
		Rectangle endNodeBounds = aRenderer.getBounds(endNode);
		assertEquals(startNodeBounds.maxX(), call1EdgeLine.point1().x());
		assertEquals(endNodeBounds.x(), call1EdgeLine.point2().x());
	}
	
	/**
	 * Tests that the return edge connects to its node boundaries. 
	 */
	@Test
	void testR1ReturnEdge()
	{
		Edge r1ReturnEdge = edgeByMiddleLabel("r1");
		Node startNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(r1ReturnEdge.start()))
				.findFirst()
				.get();
		Node endNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(r1ReturnEdge.end()))
				.findFirst()
				.get();
		aRenderer.getBounds(); // Trigger rendering pass
		Line r1ReturnEdgeLine = aRenderer.getConnectionPoints(r1ReturnEdge);
		Rectangle startNodeBounds = aRenderer.getBounds(startNode);
		Rectangle endNodeBounds = aRenderer.getBounds(endNode);
		assertEquals(startNodeBounds.x(), r1ReturnEdgeLine.point1().x());
		assertEquals(endNodeBounds.maxX(), r1ReturnEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the return edge connects to its node boundaries. 
	 */
	@Test
	void testLastReturnEdge()
	{
		Edge returnEdge = edgesByType(ReturnEdge.class)
				.stream()
				.filter(edge -> edge.properties().get(PropertyName.MIDDLE_LABEL).get().equals(""))
				.findFirst()
				.get();
		Node startNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(returnEdge.start()))
				.findFirst()
				.get();
		Node endNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(returnEdge.end()))
				.findFirst()
				.get();
		aRenderer.getBounds(); // Trigger rendering pass
		Line returnEdgeLine = aRenderer.getConnectionPoints(returnEdge);
		Rectangle startNodeBounds = aRenderer.getBounds(startNode);
		Rectangle endNodeBounds = aRenderer.getBounds(endNode);
		assertEquals(startNodeBounds.x(), returnEdgeLine.point1().x());
		assertEquals(endNodeBounds.maxX(), returnEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the return edge connects to its node boundaries. 
	 */
	@Test
	void testNoteEdge()
	{
		Node noteNode = nodeByName("A note");
		Node callNode = nodeByName(":Type2").getChildren().get(0);
		Line noteEdgeLine = aRenderer.getConnectionPoints(edgesByType(NoteEdge.class).get(0));
		aRenderer.getBounds(); // Trigger rendering pass
		Rectangle noteNodeBounds = aRenderer.getBounds(noteNode);
		Rectangle callNodeBounds = aRenderer.getBounds(callNode);
		assertEquals(noteNodeBounds.x(), noteEdgeLine.point1().x());
		assertTrue(callNodeBounds.contains(noteEdgeLine.point2()));
	}
}

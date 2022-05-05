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
package ca.mcgill.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

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
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").getStart()))
				.findFirst()
				.get();
		Node smallerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").getEnd()))
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
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").getStart()))
				.findFirst()
				.get();
		Node smallerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").getEnd()))
				.findFirst()
				.get();
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
		verifyCallNodeDefaultWidth(callNode);
	}
	
	/**
	 * Tests that the call node has the default width.
	 */
	@Test
	void testCallNodeDefaultWidthBelowObject3Node()
	{
		Node callNode = nodeByName("object3:").getChildren().get(0);
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
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").getStart()))
				.findFirst()
				.get();
		Node smallerCallNode = nodeByName("object1:Type1")
				.getChildren()
				.stream()
				.filter(node -> node.equals(edgeByMiddleLabel("selfCall()").getEnd()))
				.findFirst()
				.get();
		Line selfCallEdgeLine = EdgeViewerRegistry.getConnectionPoints(edgeByMiddleLabel("selfCall()"));
		Rectangle largerCallNodeBounds = NodeViewerRegistry.getBounds(largerCallNode);
		Rectangle smallerCallNodeBounds = NodeViewerRegistry.getBounds(smallerCallNode);
		assertEquals(largerCallNodeBounds.getMaxX(), selfCallEdgeLine.getPoint1().getX());
		assertEquals(smallerCallNodeBounds.getMaxX(), selfCallEdgeLine.getPoint2().getX());
	}
	
	/**
	 * Tests that the call edge connects to its node boundaries. 
	 */
	@Test
	void testSignalCallEdge()
	{
		Edge signalCallEdge = edgeByMiddleLabel("signal");
		Node startNode = nodeByName("object1:Type1").getChildren().get(1);
		Node endNode = nodeByName(":Type2").getChildren().get(0);
		Line signalCallEdgeLine = EdgeViewerRegistry.getConnectionPoints(signalCallEdge);
		Rectangle startNodeBounds = NodeViewerRegistry.getBounds(startNode);
		Rectangle endNodeBounds = NodeViewerRegistry.getBounds(endNode);
		assertEquals(startNodeBounds.getMaxX(), signalCallEdgeLine.getPoint1().getX());
		assertEquals(endNodeBounds.getX(), signalCallEdgeLine.getPoint2().getX());
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
				.filter(node -> node.equals(call1Edge.getStart()))
				.findFirst()
				.get();
		Node endNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(call1Edge.getEnd()))
				.findFirst()
				.get();
		Line call1EdgeLine = EdgeViewerRegistry.getConnectionPoints(call1Edge);
		Rectangle startNodeBounds = NodeViewerRegistry.getBounds(startNode);
		Rectangle endNodeBounds = NodeViewerRegistry.getBounds(endNode);
		assertEquals(startNodeBounds.getMaxX(), call1EdgeLine.getPoint1().getX());
		assertEquals(endNodeBounds.getX(), call1EdgeLine.getPoint2().getX());
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
				.filter(node -> node.equals(r1ReturnEdge.getStart()))
				.findFirst()
				.get();
		Node endNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(r1ReturnEdge.getEnd()))
				.findFirst()
				.get();
		Line r1ReturnEdgeLine = EdgeViewerRegistry.getConnectionPoints(r1ReturnEdge);
		Rectangle startNodeBounds = NodeViewerRegistry.getBounds(startNode);
		Rectangle endNodeBounds = NodeViewerRegistry.getBounds(endNode);
		assertEquals(startNodeBounds.getX(), r1ReturnEdgeLine.getPoint1().getX());
		assertEquals(endNodeBounds.getMaxX(), r1ReturnEdgeLine.getPoint2().getX());
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
				.filter(node -> node.equals(returnEdge.getStart()))
				.findFirst()
				.get();
		Node endNode = nodesByType(CallNode.class)
				.stream()
				.filter(node -> node.equals(returnEdge.getEnd()))
				.findFirst()
				.get();
		Line returnEdgeLine = EdgeViewerRegistry.getConnectionPoints(returnEdge);
		Rectangle startNodeBounds = NodeViewerRegistry.getBounds(startNode);
		Rectangle endNodeBounds = NodeViewerRegistry.getBounds(endNode);
		assertEquals(startNodeBounds.getX(), returnEdgeLine.getPoint1().getX());
		assertEquals(endNodeBounds.getMaxX(), returnEdgeLine.getPoint2().getX());
	}
	
	/**
	 * Tests that the return edge connects to its node boundaries. 
	 */
	@Test
	void testNoteEdge()
	{
		Node noteNode = nodeByName("A note");
		Node callNode = nodeByName(":Type2").getChildren().get(0);
		Line noteEdgeLine = EdgeViewerRegistry.getConnectionPoints(edgesByType(NoteEdge.class).get(0));
		Rectangle noteNodeBounds = NodeViewerRegistry.getBounds(noteNode);
		Rectangle callNodeBounds = NodeViewerRegistry.getBounds(callNode);
		assertEquals(noteNodeBounds.getX(), noteEdgeLine.getPoint1().getX());
		assertTrue(callNodeBounds.contains(noteEdgeLine.getPoint2()));
	}
}

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

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.nodes.ObjectNodeRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutObjectDiagram extends AbstractTestObjectDiagramLayout
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.object.jet");
	
	TestLayoutObjectDiagram() throws IOException
	{
		super(PATH);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value 
	 * in the file. 
	 */
	@ParameterizedTest
	@CsvSource({":Type1, 240, 130",
				"object2:, 540, 150",
				":Type3, 610, 300",
				"A note, 280, 330",
				":Type4, 440, 290"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/**
	 * Tests that all object nodes that are supposed to have the default dimension
	 * actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"object2:", ":Type3"})
	void testObjectNodesDefaultDimension(String pNodeName)
	{
		verifyObjectNodeDefaultDimensions(nodeByName(pNodeName));
	}
	
	/**
	 * Tests that the object nodes that are supposed to be expanded, actually are. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {":Type1", ":Type4"})
	void testObjectNodeExpandedVertically(String pNodeName)
	{
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(ObjectNodeRenderer.class, "DEFAULT_HEIGHT");
		Rectangle bounds = aRenderer.getBounds(nodeByName(pNodeName));
		assertTrue(bounds.height() > DEFAULT_HEIGHT);
	}
	
	/**
	 * Tests that the collaboration edge connects to its node boundaries. 
	 */
	@Test
	void testCollaborationEdge()
	{
		Rectangle boundsNodeType4 = aRenderer.getBounds(nodeByName(":Type4"));
		Rectangle boundsNodeObject2 = aRenderer.getBounds(nodeByName("object2:"));
		Rectangle edgeBounds = aRenderer.getBounds(edgeByMiddleLabel("e1"));
		assertWithDefaultTolerance(boundsNodeObject2.maxY(), edgeBounds.y());
		assertWithDefaultTolerance(boundsNodeType4.y(), edgeBounds.maxY());
	}
	
	/**
	 * Tests that the note edge connects to the note node boundary and falls within
	 *  the target node.
	 */
	@Test
	void testNoteEdgeBetweenNoteNodeAndType1Node()
	{
		Node type1Node = nodeByName(":Type1");
		Node noteNode = nodeByName("A note");
		Rectangle boundsType1Node = aRenderer.getBounds(type1Node);
		Rectangle boundsNoteNode = aRenderer.getBounds(noteNode);
		Edge noteEdge = edgesByType(NoteEdge.class).stream()
				.filter(edge -> boundsType1Node.contains(edge.start().position()) ||
						boundsType1Node.contains(edge.end().position()))
				.collect(toList()).get(0);
		Rectangle boundsNoteEdge = aRenderer.getBounds(noteEdge);
		assertWithDefaultTolerance(boundsNoteNode.y(), boundsNoteEdge.maxY());
		assertTrue(boundsType1Node.contains(noteEdge.start().position()));
	}
	
	/**
	 * Tests that the note edge connects to the note node boundary and falls within
	 *  the target node.
	 */
	@Test
	void testNoteEdgeBetweenNoteNodeAndType4Node()
	{
		Node type4Node = nodeByName(":Type4");
		Node noteNode = nodeByName("A note");
		Rectangle boundsType4Node = aRenderer.getBounds(type4Node);
		Rectangle boundsNoteNode = aRenderer.getBounds(noteNode);
		Edge noteEdge = edgesByType(NoteEdge.class).stream()
				.filter(edge -> boundsType4Node.contains(edge.start().position()) ||
						boundsType4Node.contains(edge.end().position()))
				.collect(toList()).get(0);
		Rectangle boundsNoteEdge = aRenderer.getBounds(noteEdge);
		assertWithDefaultTolerance(boundsNoteNode.maxX(), boundsNoteEdge.x());
		assertTrue(boundsType4Node.contains(noteEdge.end().position()));
	}
	
	/**
	 * Tests that the (self) reference edge connects to the node ":Type1" boundary and falls within
	 *  the node ":Type1".
	 */
	@Test
	void testSelfReferenceEdge()
	{
		Node type1Node = nodeByName(":Type1");
		Edge referenceEdge = edgesByType(ObjectReferenceEdge.class).stream()
				.filter(edge -> edge.end().equals(type1Node))
				.collect(toList()).get(0);
		Rectangle boundsType1Node = aRenderer.getBounds(type1Node);
		assertWithDefaultTolerance(boundsType1Node.x(), referenceEdge.end().position().x());
		assertTrue(boundsType1Node.contains(referenceEdge.start().position()));
	}
	
	/**
	 * Tests that the reference edge connects to the node ":Type4" boundary and falls within
	 *  the node ":Type1".
	 */
	@Test
	void testReferenceEdgeBetweenType1NodeAndType4Node()
	{
		Node type1Node = nodeByName(":Type1");
		Node type4Node = nodeByName(":Type4");
		Edge referenceEdge = edgesByType(ObjectReferenceEdge.class).stream()
				.filter(edge -> edge.end().equals(type4Node))
				.collect(toList())
				.get(0);
		Rectangle boundsType1Node = aRenderer.getBounds(type1Node);
		Rectangle boundsType4Node = aRenderer.getBounds(type4Node);
		Rectangle boundsReferenceEdge = aRenderer.getBounds(referenceEdge);
		assertWithDefaultTolerance(boundsType4Node.x(), boundsReferenceEdge.maxX());
		assertTrue(boundsType1Node.contains(referenceEdge.start().position()));
	}
	
	/**
	 * Tests that the reference edge connects to the node ":Type3" boundary and falls within
	 *  the node ":Type4".
	 */
	@Test
	void testReferenceEdgeBetweenType4NodeAndType3Node()
	{
		Node type4Node = nodeByName(":Type4");
		Node type3Node = nodeByName(":Type3");
		Edge referenceEdge = edgesByType(ObjectReferenceEdge.class).stream()
				.filter(edge -> edge.end().equals(type3Node))
				.collect(toList()).get(0);
		Rectangle boundsType4Node = aRenderer.getBounds(type4Node);
		Rectangle boundsType3Node = aRenderer.getBounds(type3Node);
		Rectangle boundsReferenceEdge = aRenderer.getBounds(referenceEdge);
		assertWithDefaultTolerance(boundsType3Node.x(), boundsReferenceEdge.maxX());
		assertTrue(boundsType4Node.contains(referenceEdge.start().position()));
	}
}

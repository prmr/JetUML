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
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.geom.Line;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutUseCaseDiagram extends AbstractTestUseCaseDiagramLayout 
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.usecase.jet");

	TestLayoutUseCaseDiagram() throws IOException 
	{
		super(PATH);
	}

	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file. 
	 */
	@ParameterizedTest
	@CsvSource({"Actor, 270, 50",
				"Actor2, 280, 230",
				"Actor3, 190, 140",
				"Use case 1, 440, 40",
				"Use case 2, 460, 130",
				"Use case 3, 460, 230",
				"Use case 4, 650, 150",
				"A note, 700, 50"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/**
	 * Tests that all use case nodes that are supposed to have the default dimension actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Use case 1", "Use case 2", "Use case 3", "Use case 4"})
	void testUseCaseNodesDefaultDimension(String pNodeName)
	{
		verifyUseCaseNodeDefaultDimensions(nodeByName(pNodeName));
	}
	
	/**
	 * Tests that all actor nodes that are supposed to have the default dimension actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Actor", "Actor2", "Actor3"})
	void testActorNodesDefaultDimension(String pNodeName)
	{
		verifyActorNodeWithNameDefaultDimensions(nodeByName(pNodeName));
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
	 * Tests that the association edge connects to its node boundaries. 
	 */
	@Test
	void testAssociationEdgeBetweenActorAndUseCase1()
	{
		Node actorNode = nodeByName("Actor");
		Edge associationEdge = edgesByType(UseCaseAssociationEdge.class)
				.stream()
				.filter(edge -> edge.end().equals(actorNode) || edge.start().equals(actorNode))
				.findFirst()
				.get();
		Rectangle actorNodeBounds = aRenderer.getBounds(actorNode);
		Rectangle useCase1NodeBounds = aRenderer.getBounds(nodeByName("Use case 1"));
		Line associationEdgeLine = aRenderer.getConnectionPoints(associationEdge);
		assertEquals(actorNodeBounds.maxX(), associationEdgeLine.point1().x());
		assertTrue(useCase1NodeBounds.contains(associationEdgeLine.point2()));
	}
	
	/**
	 * Tests that the association edge connects to its node boundaries. 
	 */
	@Test
	void testAssociationEdgeBetweenUseCase1AndUseCase4()
	{
		Node useCase4Node = nodeByName("Use case 4");
		Edge associationEdge = edgesByType(UseCaseAssociationEdge.class)
				.stream()
				.filter(edge -> edge.end().equals(useCase4Node) || edge.start().equals(useCase4Node))
				.findFirst()
				.get();
		Rectangle useCase1NodeBounds = aRenderer.getBounds(nodeByName("Use case 1"));
		Rectangle useCase4NodeBounds = aRenderer.getBounds(useCase4Node);
		Line associationEdgeLine = aRenderer.getConnectionPoints(associationEdge);
		assertTrue(useCase1NodeBounds.contains(associationEdgeLine.point1()));
		assertTrue(useCase4NodeBounds.contains(associationEdgeLine.point2()));
	}
	
	/**
	 * Tests that the association edge connects to its node boundaries. 
	 */
	@Test
	void testAssociationEdgeBetweenActor2AndUseCase2()
	{
		Node useCase2Node = nodeByName("Use case 2");
		Edge associationEdge = edgesByType(UseCaseAssociationEdge.class)
				.stream()
				.filter(edge -> edge.end().equals(useCase2Node) || edge.start().equals(useCase2Node))
				.findFirst()
				.get();
		Rectangle actor2NodeBounds = aRenderer.getBounds(nodeByName("Actor2"));
		Rectangle useCase2NodeBounds = aRenderer.getBounds(useCase2Node);
		Line associationEdgeLine = aRenderer.getConnectionPoints(associationEdge);
		assertEquals(actor2NodeBounds.maxX(), associationEdgeLine.point1().x());
		assertTrue(useCase2NodeBounds.contains(associationEdgeLine.point2()));
	}
	
	/**
	 * Tests that the association edge connects to its node boundaries. 
	 */
	@Test
	void testAssociationEdgeBetweenActor2AndUseCase3()
	{
		Node useCase3Node = nodeByName("Use case 3");
		Edge associationEdge = edgesByType(UseCaseAssociationEdge.class)
				.stream()
				.filter(edge -> edge.end().equals(useCase3Node) || edge.start().equals(useCase3Node))
				.findFirst()
				.get();
		Rectangle actor2NodeBounds = aRenderer.getBounds(nodeByName("Actor2"));
		Rectangle useCase3NodeBounds = aRenderer.getBounds(useCase3Node);
		Line associationEdgeLine = aRenderer.getConnectionPoints(associationEdge);
		assertEquals(actor2NodeBounds.maxX(), associationEdgeLine.point1().x());
		assertEquals(useCase3NodeBounds.x(), associationEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the extend edge connects to its node boundaries. 
	 */
	@Test
	void testExtendEdgeBetweenActor3AndActor2()
	{
		Node actor2Node = nodeByName("Actor2");
		Edge extendEdge = edgesByType(UseCaseDependencyEdge.class)
				.stream()
				.map(edge -> (UseCaseDependencyEdge)edge)
				.filter(edge -> edge.getType() == UseCaseDependencyEdge.Type.Extend)
				.filter(edge -> edge.end().equals(actor2Node))
				.findFirst()
				.get();
		Rectangle actor3NodeBounds = aRenderer.getBounds(nodeByName("Actor3"));
		Rectangle actor2NodeBounds = aRenderer.getBounds(actor2Node);
		Line extendEdgeLine = aRenderer.getConnectionPoints(extendEdge);
		assertEquals(actor3NodeBounds.maxX(), extendEdgeLine.point1().x());
		assertEquals(actor2NodeBounds.x(), extendEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the extend edge connects to its node boundaries. 
	 */
	@Test
	void testExtendEdgeBetweenUseCase2AndUseCase4()
	{
		Node useCase4Node = nodeByName("Use case 4");
		Edge extendEdge = edgesByType(UseCaseDependencyEdge.class)
				.stream()
				.map(edge -> (UseCaseDependencyEdge)edge)
				.filter(edge -> edge.getType() == UseCaseDependencyEdge.Type.Extend)
				.filter(edge -> edge.end().equals(useCase4Node))
				.findFirst()
				.get();
		Rectangle useCase2NodeBounds = aRenderer.getBounds(nodeByName("Use case 2"));
		Rectangle useCase4NodeBounds = aRenderer.getBounds(useCase4Node);
		Line extendEdgeLine = aRenderer.getConnectionPoints(extendEdge);
		assertEquals(useCase2NodeBounds.maxX(), extendEdgeLine.point1().x());
		assertEquals(useCase4NodeBounds.x(), extendEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the include edge connects to its node boundaries. 
	 */
	@Test
	void testIncludeEdgeBetweenUseCase2AndUseCase3()
	{
		Node useCase3Node = nodeByName("Use case 3");
		Edge includeEdge = edgesByType(UseCaseDependencyEdge.class)
				.stream()
				.map(edge -> (UseCaseDependencyEdge)edge)
				.filter(edge -> edge.getType() == UseCaseDependencyEdge.Type.Include)
				.filter(edge -> edge.end().equals(useCase3Node))
				.findFirst()
				.get();
		Rectangle useCase2NodeBounds = aRenderer.getBounds(nodeByName("Use case 2"));
		Rectangle useCase3NodeBounds = aRenderer.getBounds(useCase3Node);
		Line includeEdgeLine = aRenderer.getConnectionPoints(includeEdge);
		assertEquals(useCase2NodeBounds.maxY(), includeEdgeLine.point1().y());
		assertEquals(useCase3NodeBounds.y(), includeEdgeLine.point2().y());
	}
	
	/**
	 * Tests that the generalization edge connects to its node boundaries. 
	 */
	@Test
	void testGeneralizationEdgeBetweenActor3AndActor()
	{
		Node actorNode = nodeByName("Actor");
		Edge generalizationEdge = edgesByType(UseCaseGeneralizationEdge.class)
				.stream()
				.filter(edge -> edge.end().equals(actorNode))
				.findFirst()
				.get();
		Rectangle actor3NodeBounds = aRenderer.getBounds(nodeByName("Actor3"));
		Rectangle actorNodeBounds = aRenderer.getBounds(actorNode);
		Line generalizationEdgeLine = aRenderer.getConnectionPoints(generalizationEdge);
		assertEquals(actor3NodeBounds.maxX(), generalizationEdgeLine.point1().x());
		assertEquals(actorNodeBounds.x(), generalizationEdgeLine.point2().x());
	}
	
	/**
	 * Tests that the generalization edge connects to its node boundaries. 
	 */
	@Test
	void testGeneralizationEdgeBetweenUseCase2AndUseCase1()
	{
		Node useCase1Node = nodeByName("Use case 1");
		Edge generalizationEdge = edgesByType(UseCaseGeneralizationEdge.class)
				.stream()
				.filter(edge -> edge.end().equals(useCase1Node))
				.findFirst()
				.get();
		Rectangle useCase2NodeBounds = aRenderer.getBounds(nodeByName("Use case 2"));
		Rectangle useCase1NodeBounds = aRenderer.getBounds(useCase1Node);
		Line generalizationEdgeLine = aRenderer.getConnectionPoints(generalizationEdge);
		assertTrue(useCase2NodeBounds.contains(generalizationEdgeLine.point1()));
		assertTrue(useCase1NodeBounds.contains(generalizationEdgeLine.point2()));
	}
	
	/**
	 * Tests that the note edge connects to the note node boundaries. 
	 */
	@Test
	void testNoteEdge()
	{
		Rectangle noteNodeBounds = aRenderer.getBounds(nodeByName("A note"));
		Line noteEdgeLine = aRenderer.getConnectionPoints(edgesByType(NoteEdge.class).get(0));
		assertEquals(noteNodeBounds.x(), noteEdgeLine.point1().x());
	}
}

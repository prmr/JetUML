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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.AbstractPackageNodeViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutClassDiagram2 extends AbstractTestClassDiagramLayout 
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService2.class.jet");

	TestLayoutClassDiagram2() throws IOException 
	{
		super(PATH);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value in the file.
	 * We don't test p1, p3, and p4 because their positions are calculated from their children. 
	 */
	@ParameterizedTest
	@CsvSource({"C1, 320, 260",
				"C2, 810, 330",
				"I1, 640, 330",
				"p2, 477, 130"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/**
	 * Tests that all type nodes that are supposed to have the default dimension
	 * actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"C1", "C2", "I1"})
	void testClassNodesDefaultDimension(String pNodeName)
	{
		verifyClassNodeDefaultDimensions(nodeByName(pNodeName));
	}
	
	/**
	 * Tests that the bounds of the package node are outside of the bounds of its child. 
	 */
	@ParameterizedTest
	@CsvSource({"p1, C1",
				"p3, p4"})
	void testPackageNodeContainmentOfOneNode(String pPackageNodeName, String pInnerNodeName)
	{
		verifyPackageNodeContainmentOfSingleNode(pPackageNodeName, pInnerNodeName);
	}
	
	/**
	 * Tests that the bounds of the package node are outside the bounds of its children. 
	 */
	@Test
	void testPackageNodeP4ContainsBothNodesI1AndC2()
	{
		final int packageNodePadding = getStaticIntFieldValue(AbstractPackageNodeViewer.class, "PADDING");
		Rectangle boundsI1 = NodeViewerRegistry.getBounds(nodeByName("I1"));
		Rectangle boundsC2 = NodeViewerRegistry.getBounds(nodeByName("C2"));
		Rectangle boundsPackageNode = NodeViewerRegistry.getBounds(nodeByName("p4"));
		assertEquals(boundsI1.getX() - packageNodePadding, boundsPackageNode.getX());
		assertEquals(boundsC2.getMaxX() + packageNodePadding, boundsPackageNode.getMaxX());
		assertEquals(boundsI1.getMaxY() + packageNodePadding, boundsPackageNode.getMaxY());
		assertEquals(boundsC2.getMaxY() + packageNodePadding, boundsPackageNode.getMaxY());
		assertTrue(boundsPackageNode.getY() < boundsI1.getY());
		assertTrue(boundsPackageNode.getY() < boundsC2.getY());
	}
	
	/**
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Test
	void testDependencyEdgeBetweenC1AndI1()
	{
		Rectangle boundsC1 = NodeViewerRegistry.getBounds(nodeByName("C1"));
		Rectangle boundsI1 = NodeViewerRegistry.getBounds(nodeByName("I1"));
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edgeByMiddleLabel("e1"));
		assertWithDefaultTolerance(boundsC1.getMaxX(), edgeBounds.getX());
		assertWithDefaultTolerance(boundsI1.getX(), edgeBounds.getMaxX());
	}
	
	/**
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Test
	void testDependencyEdgeBetweenC2AndI1()
	{
		Rectangle boundsC2 = NodeViewerRegistry.getBounds(nodeByName("C2"));
		Rectangle boundsI1 = NodeViewerRegistry.getBounds(nodeByName("I1"));
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edgeByMiddleLabel("e2"));
		assertWithDefaultTolerance(boundsC2.getX(), edgeBounds.getMaxX());
		assertWithDefaultTolerance(boundsI1.getMaxX(), edgeBounds.getX());
	}
	
	/**
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Test
	void testDependencyEdgeBetweenP3AndP2()
	{
		Rectangle boundsP3 = NodeViewerRegistry.getBounds(nodeByName("p3"));
		Rectangle boundsP2 = NodeViewerRegistry.getBounds(nodeByName("p2"));
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edgeByMiddleLabel("e3"));
		assertWithDefaultTolerance(boundsP3.getY(), edgeBounds.getMaxY());
		assertWithDefaultTolerance(boundsP2.getMaxX(), edgeBounds.getX());
	}
}

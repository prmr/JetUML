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

import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.nodes.AbstractPackageNodeRenderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
		final int packageNodePadding = getStaticIntFieldValue(AbstractPackageNodeRenderer.class, "PADDING");
		Rectangle boundsI1 = aRenderer.getBounds(nodeByName("I1"));
		Rectangle boundsC2 = aRenderer.getBounds(nodeByName("C2"));
		Rectangle boundsPackageNode = aRenderer.getBounds(nodeByName("p4"));
		assertEquals(boundsI1.x() - packageNodePadding, boundsPackageNode.x());
		assertEquals(boundsC2.maxX() + packageNodePadding, boundsPackageNode.maxX());
		assertEquals(boundsI1.maxY() + packageNodePadding, boundsPackageNode.maxY());
		assertEquals(boundsC2.maxY() + packageNodePadding, boundsPackageNode.maxY());
		assertTrue(boundsPackageNode.y() < boundsI1.y());
		assertTrue(boundsPackageNode.y() < boundsC2.y());
	}
	
	/**
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Test
	void testDependencyEdgeBetweenC1AndI1()
	{
		aRenderer.getBounds(); // Triggers a layout pass
		Rectangle boundsC1 = aRenderer.getBounds(nodeByName("C1"));
		Rectangle boundsI1 = aRenderer.getBounds(nodeByName("I1"));
		Rectangle edgeBounds = aRenderer.getBounds(edgeByMiddleLabel("e1"));
		assertWithDefaultTolerance(boundsC1.maxX(), edgeBounds.x());
		assertWithDefaultTolerance(boundsI1.x(), edgeBounds.maxX());
	}
	
	/**
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Test
	void testDependencyEdgeBetweenC2AndI1()
	{
		aRenderer.getBounds(); // Triggers a layout pass
		Rectangle boundsC2 = aRenderer.getBounds(nodeByName("C2"));
		Rectangle boundsI1 = aRenderer.getBounds(nodeByName("I1"));
		Rectangle edgeBounds = aRenderer.getBounds(edgeByMiddleLabel("e2"));
		assertWithDefaultTolerance(boundsC2.x(), edgeBounds.maxX());
		assertWithDefaultTolerance(boundsI1.maxX(), edgeBounds.x());
	}
	
	/**
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Disabled
	@Test
	void testDependencyEdgeBetweenP3AndP2()
	{
		aRenderer.getBounds(); // Triggers a layout pass
		Rectangle boundsP3 = aRenderer.getBounds(nodeByName("p3"));
		Rectangle boundsP2 = aRenderer.getBounds(nodeByName("p2"));
		Rectangle edgeBounds = aRenderer.getBounds(edgeByMiddleLabel("e3"));
		assertWithDefaultTolerance(boundsP3.y(), edgeBounds.maxY());
		assertWithDefaultTolerance(boundsP2.maxX(), edgeBounds.x());
	}
}

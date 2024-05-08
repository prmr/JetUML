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
package org.jetuml.rendering.nodes;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.function.Function;

import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the NodeStorage. 
 */
public class TestNodeStorage 
{	
	private NodeStorage aNodeStorage;

	@BeforeEach
	public void setup()
	{
		aNodeStorage = new NodeStorage();
	}

	@Test
	public void testGetBoundsReturnsDifferentBoundsWhenNodeStorageIsNotActive()
	{
		Node node = new NoteNode();
		Rectangle boundsA = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		Rectangle boundsB = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		assertNotSame(boundsA, boundsB);
	}

	@Test
	public void testGetBoundsReturnsSameBoundsWhenNodeStorageIsActive()
	{
		aNodeStorage.activate();
		Node node = new NoteNode();
		Rectangle boundsA = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		Rectangle boundsB = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		assertSame(boundsA, boundsB);
	}

	@Test
	public void testGetBoundsReturnsDifferentBoundsForDifferentNodesWhenNodeStorageIsActive()
	{
		aNodeStorage.activate();
		Node node1 = new NoteNode();
		Node node2 = new NoteNode();
		Rectangle boundsA = aNodeStorage.getBounds(node1, createDefaultBoundCalculator());
		Rectangle boundsB = aNodeStorage.getBounds(node2, createDefaultBoundCalculator());
		assertNotSame(boundsA, boundsB);
	}
	
	@Test
	public void testGetBoundsReturnsDifferentBoundsBeforeAndAfterDeactivationOfNodeStorage()
	{
		aNodeStorage.activate();
		Node node = new NoteNode();
		Rectangle boundsBeforeDeactivation = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		aNodeStorage.deactivateAndClear();
		Rectangle boundsAfterDeactivation = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		assertNotSame(boundsBeforeDeactivation, boundsAfterDeactivation);
	}

	private static Function<Node, Rectangle> createDefaultBoundCalculator()
	{
		return new Function<>()
		{
			@Override
			public Rectangle apply(Node pNode) 
			{
				return new Rectangle(pNode.position().x(), pNode.position().y(), 100, 100);
			}
		};
	}
} 
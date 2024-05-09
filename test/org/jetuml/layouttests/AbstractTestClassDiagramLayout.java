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

import org.jetuml.diagram.Node;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.nodes.AbstractPackageNodeRenderer;
import org.jetuml.rendering.nodes.TypeNodeRenderer;

/**
 * Superclass for classes that test the layout of a class diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestClassDiagramLayout extends AbstractTestDiagramLayout
{
	AbstractTestClassDiagramLayout(Path pDiagramPath) throws IOException
	{
		super(pDiagramPath);
	}
	
	protected void verifyClassNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(TypeNodeRenderer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(TypeNodeRenderer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	protected void verifyPackageNodeContainmentOfSingleNode(String pPackageName, String pInnerNodeName)
	{
		final int packageNodePadding = getStaticIntFieldValue(AbstractPackageNodeRenderer.class, "PADDING");
		Rectangle boundsInnerNode = aRenderer.getBounds(nodeByName(pInnerNodeName));
		Rectangle boundsPackageNode = aRenderer.getBounds(nodeByName(pPackageName));
		assertEquals(boundsInnerNode.x() - packageNodePadding, boundsPackageNode.x());
		assertEquals(boundsInnerNode.maxX() + packageNodePadding, boundsPackageNode.maxX());
		assertEquals(boundsInnerNode.maxY() + packageNodePadding, boundsPackageNode.maxY());
		assertTrue(boundsPackageNode.y() < boundsInnerNode.y());
	}
}

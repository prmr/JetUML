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

import java.io.IOException;
import java.nio.file.Path;

import org.jetuml.diagram.Node;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.nodes.CircularStateNodeRenderer;
import org.jetuml.rendering.nodes.StateNodeRenderer;

/**
 * Superclass for classes that test the layout of a state diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestStateDiagramLayout extends AbstractTestDiagramLayout 
{
	AbstractTestStateDiagramLayout(Path pDiagramPath) throws IOException 
	{
		super(pDiagramPath);
	}
	
	protected void verifyStateNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(StateNodeRenderer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(StateNodeRenderer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	protected void verifyCircularStateNodeDefaultDimensions(Node pNode)
	{
		final int DIAMETER = getStaticIntFieldValue(CircularStateNodeRenderer.class, "DIAMETER");
		Rectangle bounds = aRenderer.getBounds(pNode);
		assertEquals(DIAMETER, bounds.width());
		assertEquals(DIAMETER, bounds.height());
	}
}

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

import java.io.IOException;
import java.nio.file.Path;

import org.jetuml.diagram.Node;
import org.jetuml.rendering.nodes.ObjectNodeRenderer;

/**
 * Superclass for classes that test the layout of an object diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestObjectDiagramLayout extends AbstractTestDiagramLayout
{
	AbstractTestObjectDiagramLayout(Path pDiagramPath) throws IOException
	{
		super(pDiagramPath);
	}
	
	protected void verifyObjectNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(ObjectNodeRenderer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(ObjectNodeRenderer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
}

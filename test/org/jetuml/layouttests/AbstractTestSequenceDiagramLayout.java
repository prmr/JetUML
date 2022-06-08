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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.jetuml.diagram.Node;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.nodes.CallNodeViewer;
import org.jetuml.viewers.nodes.ImplicitParameterNodeViewer;
import org.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * Superclass for classes that test the layout of a sequence diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestSequenceDiagramLayout extends AbstractTestDiagramLayout 
{
	AbstractTestSequenceDiagramLayout(Path pDiagramPath) throws IOException 
	{
		super(pDiagramPath);
	}
	
	protected static void verifyImplicitParameterNodeTopRectangleDefaultHeight(Node pNode)
	{
		final int TOP_HEIGHT = getStaticIntFieldValue(ImplicitParameterNodeViewer.class, "TOP_HEIGHT");
		ImplicitParameterNodeViewer instanceOfImplicitParameterNodeViewer = getInstanceOfImplicitParameterNodeViewer(pNode);
		Rectangle implicitParameterTopRectangle = instanceOfImplicitParameterNodeViewer.getTopRectangle(pNode);
		assertEquals(TOP_HEIGHT, implicitParameterTopRectangle.getHeight());
	}
	
	protected static void verifyCallNodeDefaultWidth(Node pNode)
	{
		final int WIDTH = getStaticIntFieldValue(CallNodeViewer.class, "WIDTH");
		Rectangle nodeBounds = NodeViewerRegistry.getBounds(pNode);
		assertEquals(WIDTH, nodeBounds.getWidth());
	}
	
	private static ImplicitParameterNodeViewer getInstanceOfImplicitParameterNodeViewer(Node pImplicitParameterNode)
	{
		try
		{
			Field fieldInstanceOfNodeViewerRegistry = NodeViewerRegistry.class.getDeclaredField("INSTANCE");
			fieldInstanceOfNodeViewerRegistry.setAccessible(true);
			NodeViewerRegistry instanceOfNodeViewerRegistry = (NodeViewerRegistry) fieldInstanceOfNodeViewerRegistry.get(null);
			
			Method method = NodeViewerRegistry.class.getDeclaredMethod("viewerFor", Node.class);
			method.setAccessible(true);
			ImplicitParameterNodeViewer instanceOfImplicitParameterNodeViewer =
					(ImplicitParameterNodeViewer)method.invoke(instanceOfNodeViewerRegistry, pImplicitParameterNode);
			return instanceOfImplicitParameterNodeViewer;
		}
		catch (ReflectiveOperationException e)
		{
			assert false;
			fail();
			return null;
		}
	}
}

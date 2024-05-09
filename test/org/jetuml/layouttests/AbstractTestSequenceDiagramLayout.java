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
import java.nio.file.Path;
import java.util.IdentityHashMap;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.AbstractDiagramRenderer;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.jetuml.rendering.nodes.CallNodeRenderer;
import org.jetuml.rendering.nodes.ImplicitParameterNodeRenderer;
import org.jetuml.rendering.nodes.NodeRenderer;

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
		final int TOP_HEIGHT = getStaticIntFieldValue(ImplicitParameterNodeRenderer.class, "TOP_HEIGHT");
		ImplicitParameterNodeRenderer instanceOfImplicitParameterNodeViewer = getInstanceOfImplicitParameterNodeViewer();
		Rectangle implicitParameterTopRectangle = instanceOfImplicitParameterNodeViewer.getTopRectangle(pNode);
		assertEquals(TOP_HEIGHT, implicitParameterTopRectangle.height());
	}
	
	protected void verifyCallNodeDefaultWidth(Node pNode)
	{
		final int WIDTH = getStaticIntFieldValue(CallNodeRenderer.class, "WIDTH");
		Rectangle nodeBounds = aRenderer.getBounds(pNode);
		assertEquals(WIDTH, nodeBounds.width());
	}
	
	private static ImplicitParameterNodeRenderer getInstanceOfImplicitParameterNodeViewer()
	{
		try
		{
			Field nodeViewers = AbstractDiagramRenderer.class.getDeclaredField("aRenderers");
			nodeViewers.setAccessible(true);
			@SuppressWarnings("unchecked")
			ImplicitParameterNodeRenderer instanceOfImplicitParameterNodeViewer = 
					(ImplicitParameterNodeRenderer)((IdentityHashMap<Class<? extends Node>, NodeRenderer>)nodeViewers.get(
							new SequenceDiagramRenderer(new Diagram(DiagramType.SEQUENCE)))).get(ImplicitParameterNode.class);
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

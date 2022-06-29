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
import org.jetuml.rendering.RenderingFacade;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.jetuml.viewers.nodes.CallNodeViewer;
import org.jetuml.viewers.nodes.ImplicitParameterNodeViewer;
import org.jetuml.viewers.nodes.NodeViewer;
import org.junit.jupiter.api.BeforeEach;

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
	
	@BeforeEach
	protected void setup()
	{
		RenderingFacade.prepareFor(aDiagram);
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
		Rectangle nodeBounds = RenderingFacade.getBounds(pNode);
		assertEquals(WIDTH, nodeBounds.getWidth());
	}
	
	private static ImplicitParameterNodeViewer getInstanceOfImplicitParameterNodeViewer(Node pImplicitParameterNode)
	{
		try
		{
			Field nodeViewers = AbstractDiagramRenderer.class.getDeclaredField("aRenderers");
			nodeViewers.setAccessible(true);
			@SuppressWarnings("unchecked")
			ImplicitParameterNodeViewer instanceOfImplicitParameterNodeViewer = 
					(ImplicitParameterNodeViewer)((IdentityHashMap<Class<? extends Node>, NodeViewer>)nodeViewers.get(
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

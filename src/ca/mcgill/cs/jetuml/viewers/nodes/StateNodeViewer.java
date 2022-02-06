/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package ca.mcgill.cs.jetuml.viewers.nodes;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.GeomUtils;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ViewerUtils;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a StateNode.
 */
public final class StateNodeViewer extends AbstractNodeViewer
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pNode);
		ViewerUtils.drawRoundedRectangle(pGraphics, bounds);
		NAME_VIEWER.draw(((StateNode)pNode).getName(), pGraphics, bounds);
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Dimension bounds = NAME_VIEWER.getDimension(((StateNode)pNode).getName());
		return new Rectangle(pNode.position().getX(), pNode.position().getY(), 
				Math.max(bounds.width(), DEFAULT_WIDTH), Math.max(bounds.height(), DEFAULT_HEIGHT));
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		return GeomUtils.intersectRoundedRectangle(getBounds(pNode), pDirection);
	}   	
}

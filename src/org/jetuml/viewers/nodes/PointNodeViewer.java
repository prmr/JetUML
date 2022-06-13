/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package org.jetuml.viewers.nodes;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a PointNode.
 */
public final class PointNodeViewer extends AbstractNodeViewer
{
	private static final int SELECTION_DISTANCE = 5;
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		return new Rectangle(pNode.position().getX(), pNode.position().getY(), 0, 0);
	}

	@Override
	public boolean contains(DiagramElement pElement, Point pPoint)
	{
		return ((Node)pElement).position().distance(pPoint) < SELECTION_DISTANCE;
	}

	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		return pNode.position();
	}
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics) 
	{
		// Do nothing, a point is invisible.
	}
}

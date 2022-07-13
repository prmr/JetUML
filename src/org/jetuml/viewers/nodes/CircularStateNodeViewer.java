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
import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.RenderingUtils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a initial or final node.
 */
public final class CircularStateNodeViewer extends AbstractNodeViewer
{
	private static final int DIAMETER = 20;
	private final boolean aFinal;
	
	/**
	 * @param pFinal true if this is a final node, false if it is an initial node.
	 */
	public CircularStateNodeViewer(DiagramRenderer pParent, boolean pFinal)
	{
		super(pParent);
		aFinal = pFinal;
	}

	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pElement);
		if( aFinal )
		{
			RenderingUtils.drawCircle(pGraphics, bounds.getX(), bounds.getY(), DIAMETER, Color.WHITE, true);
			int innerDiameter = DIAMETER/2;
			RenderingUtils.drawCircle(pGraphics, bounds.getX() + innerDiameter/2, 
					bounds.getY() + innerDiameter/2, innerDiameter, Color.BLACK, false);
		}
		else
		{
			RenderingUtils.drawCircle(pGraphics, bounds.getX(), bounds.getY(), DIAMETER, Color.BLACK, true);
		}
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		return GeomUtils.intersectCircle(getBounds(pNode), pDirection);
	}   	 

	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		return new Rectangle(pNode.position().getX(), pNode.position().getY(), DIAMETER, DIAMETER);
	}
}

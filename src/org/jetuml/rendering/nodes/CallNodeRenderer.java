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
package org.jetuml.rendering.nodes;

import java.util.Optional;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.GraphicsRenderingContext;
import org.jetuml.rendering.SequenceDiagramRenderer;

/**
 * An object to render a call node in a Sequence diagram.
 */
public final class CallNodeRenderer extends AbstractNodeRenderer
{
	private static final int WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	
	/* Number of pixels to shift a call node that is nested within another call on the same object. */
	private static final int NESTING_SHIFT_DISTANCE = 10;
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public CallNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void draw(DiagramElement pElement, GraphicsRenderingContext pContext)
	{
		if(((CallNode)pElement).isOpenBottom())
		{
			pContext.drawRectangle(getBounds(pElement), ColorScheme.get().fill(), 
					ColorScheme.get().fill(), Optional.of(ColorScheme.get().dropShadow()));
			final Rectangle bounds = getBounds(pElement);
			int x1 = bounds.x();
			int x2 = bounds.maxX();
			int y1 = bounds.y();
			int y3 = bounds.maxY();
			int y2 = y3 - CallNode.CALL_YGAP;
			pContext.strokeLine(x1, y1, x2, y1, ColorScheme.get().stroke(), LineStyle.SOLID);
			pContext.strokeLine(x1, y1, x1, y2, ColorScheme.get().stroke(), LineStyle.SOLID);
			pContext.strokeLine(x2, y1, x2, y2, ColorScheme.get().stroke(), LineStyle.SOLID);
			pContext.strokeLine(x1, y2, x1, y3, ColorScheme.get().stroke(), LineStyle.DOTTED);
			pContext.strokeLine(x2, y2, x2, y3, ColorScheme.get().stroke(), LineStyle.DOTTED);
		}
		else
		{
			pContext.drawRectangle(getBounds(pElement), ColorScheme.get().fill(), 
					ColorScheme.get().stroke(), Optional.of(ColorScheme.get().dropShadow()));
		}
	}

	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		if(pDirection == Direction.EAST)
		{
			return new Point(getBounds(pNode).maxX(), getBounds(pNode).y());
		}
		else
		{
			return new Point(getBounds(pNode).x(), getBounds(pNode).y());
		}
	}
	
	/*
	 * The x position is a function of the position of the implicit parameter
	 * node and the nesting depth of the call node.
	 */
	private int getX(Node pNode)
	{
		final int nestingDepth = parent().getNestingDepth((CallNode)pNode);
		final int lifelineXCoordinate = SequenceDiagramRenderer.getCenterXCoordinate((ImplicitParameterNode)pNode.getParent());
		return lifelineXCoordinate - NESTING_SHIFT_DISTANCE + (NESTING_SHIFT_DISTANCE * nestingDepth);
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		final int y = parent().getY(pNode);
		final int maxY = parent().getMaxY(pNode);
		return new Rectangle(getX(pNode), y, WIDTH, maxY-y);
	}
	
	@Override
	protected SequenceDiagramRenderer parent()
	{
		return (SequenceDiagramRenderer) super.parent();
	}
}

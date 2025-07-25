/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

import static org.jetuml.geom.GeomUtils.max;

import java.util.Optional;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.AbstractPackageNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.Alignment;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.Side;
import org.jetuml.rendering.StringRenderer;

/**
 * Common functionality to view the different types of package nodes.
 */
public abstract class AbstractPackageNodeRenderer extends AbstractNodeRenderer
{
	protected static final int PADDING = 10;
	protected static final int TOP_HEIGHT = 20;
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_BOTTOM_HEIGHT = 60;
	protected static final int DEFAULT_TOP_WIDTH = 60;
	protected static final int HORIZONTAL_PADDING = 3;
	private static final StringRenderer LABEL_RENDERER = new StringRenderer(Alignment.LEFT);
	
	/**
	 * @param pParent The rendere for the diagram that contains this package node.
	 */
	public AbstractPackageNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(DEFAULT_WIDTH, TOP_HEIGHT + DEFAULT_BOTTOM_HEIGHT);
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		assert pElement instanceof AbstractPackageNode;
		Rectangle topBounds = getTopBounds((AbstractPackageNode)pElement);
		Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pElement);
		pContext.drawRectangle(topBounds, ColorScheme.get().fill(), 
				ColorScheme.get().stroke(), Optional.of(ColorScheme.get().dropShadow()));
		pContext.drawRectangle(bottomBounds, ColorScheme.get().fill(), 
				ColorScheme.get().stroke(), Optional.of(ColorScheme.get().dropShadow()));
		LABEL_RENDERER.draw(((AbstractPackageNode)pElement).getName(), new Rectangle(topBounds.x() + HORIZONTAL_PADDING, 
				topBounds.y(), topBounds.width(), topBounds.height()), pContext);
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		assert pNode instanceof AbstractPackageNode;
		Rectangle topBounds = getTopBounds((AbstractPackageNode)pNode);
		Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pNode);
		Rectangle bounds = topBounds.add(bottomBounds);
		
		Point connectionPoint = super.getConnectionPoint(pNode, pDirection);
		if( connectionPoint.y() < bottomBounds.y() && topBounds.maxX() < connectionPoint.x() )
		{
			// The connection point falls in the empty top-right corner, re-compute it so
			// it intersects the top of the bottom rectangle (basic triangle proportions)
			int delta = topBounds.height() * (connectionPoint.x() - bounds.center().x()) * 2 / 
					bounds.height();
			int newX = connectionPoint.x() - delta;
			if( newX < topBounds.maxX() )
			{
				newX = topBounds.maxX() + 1;
			}
			return new Point(newX, bottomBounds.y());	
		}
		else
		{
			return connectionPoint;
		}
	}
	
	/*
	 * The top face of a package node is only the side of the bottom (main) rectangle.
	 */
	@Override
	public Line getFace(Node pNode, Side pSide) 
	{
		assert pNode != null && pSide != null;
		if( pSide == Side.TOP )
		{
			Rectangle topBounds = getTopBounds((AbstractPackageNode)pNode);
			Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pNode);
			return new Line(topBounds.maxX(), bottomBounds.y(), bottomBounds.maxX(), bottomBounds.y());
			
		}
		else if( pSide == Side.RIGHT )
		{
			return pSide.getCorrespondingLine(getBottomBounds((AbstractPackageNode)pNode));
		}
		else
		{
			return super.getFace(pNode, pSide);
		}
		
	}

	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		assert pNode instanceof AbstractPackageNode;
		return getTopBounds((AbstractPackageNode)pNode).add(getBottomBounds((AbstractPackageNode)pNode));
	}
	
	/**
	 * @param pNode The package node
	 * @return The point that corresponds to the actual top right
	 *     corner of the figure (as opposed to bounds).
	 */
	public Point getTopRightCorner(AbstractPackageNode pNode)
	{
		Rectangle bottomBounds = getBottomBounds(pNode);
		return new Point(bottomBounds.maxX(), bottomBounds.y());
	}
	
	protected Dimension getTopDimension(AbstractPackageNode pNode)
	{
		Dimension textDimensions = LABEL_RENDERER.getDimension(pNode.getName());
		int topWidth = max(textDimensions.width() + 2 * HORIZONTAL_PADDING, DEFAULT_TOP_WIDTH);
		int topHeight = max(textDimensions.height(), TOP_HEIGHT);
		return new Dimension(topWidth, topHeight);
	}
	
	/*
	 * By default the node's top bounds is based on its position.
	 */
	protected Rectangle getTopBounds(AbstractPackageNode pNode)
	{
		Point position = pNode.position();
		Dimension topDimension = getTopDimension(pNode);
		return new Rectangle(position.x(), position.y(), topDimension.width(), topDimension.height());
	}
	
	protected abstract Rectangle getBottomBounds(AbstractPackageNode pNode);
}

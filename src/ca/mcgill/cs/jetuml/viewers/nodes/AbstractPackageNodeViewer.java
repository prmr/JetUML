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

import static ca.mcgill.cs.jetuml.geom.GeomUtils.max;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.AbstractPackageNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ViewerUtils;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.GraphicsContext;

/**
 * Common functionality to view the different types of package nodes.
 */
public abstract class AbstractPackageNodeViewer extends AbstractNodeViewer
{
	protected static final int PADDING = 10;
	protected static final int TOP_HEIGHT = 20;
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_BOTTOM_HEIGHT = 60;
	protected static final int DEFAULT_TOP_WIDTH = 60;
	protected static final int NAME_GAP = 3;
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		assert pNode instanceof AbstractPackageNode;
		Rectangle topBounds = getTopBounds((AbstractPackageNode)pNode);
		Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pNode);
		ViewerUtils.drawRectangle(pGraphics, topBounds );
		ViewerUtils.drawRectangle(pGraphics, bottomBounds );
		NAME_VIEWER.draw(((AbstractPackageNode)pNode).getName(), pGraphics, new Rectangle(topBounds.getX() + NAME_GAP, 
				topBounds.getY(), topBounds.getWidth(), topBounds.getHeight()));
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		assert pNode instanceof AbstractPackageNode;
		Rectangle topBounds = getTopBounds((AbstractPackageNode)pNode);
		Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pNode);
		Rectangle bounds = topBounds.add(bottomBounds);
		
		Point connectionPoint = super.getConnectionPoint(pNode, pDirection);
		if( connectionPoint.getY() < bottomBounds.getY() && topBounds.getMaxX() < connectionPoint.getX() )
		{
			// The connection point falls in the empty top-right corner, re-compute it so
			// it intersects the top of the bottom rectangle (basic triangle proportions)
			int delta = topBounds.getHeight() * (connectionPoint.getX() - bounds.getCenter().getX()) * 2 / 
					bounds.getHeight();
			int newX = connectionPoint.getX() - delta;
			if( newX < topBounds.getMaxX() )
			{
				newX = topBounds.getMaxX() + 1;
			}
			return new Point(newX, bottomBounds.getY());	
		}
		else
		{
			return connectionPoint;
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
		return new Point(bottomBounds.getMaxX(), bottomBounds.getY());
	}
	
	
	protected Dimension getTopDimension(AbstractPackageNode pNode)
	{
		Dimension nameBounds = NAME_VIEWER.getDimension(pNode.getName());
		int topWidth = max(nameBounds.width() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		int topHeight = max(nameBounds.height() - 2 * NAME_GAP, TOP_HEIGHT);
		return new Dimension(topWidth, topHeight);
	}
	
	/*
	 * By default the node's top bounds is based on its position.
	 */
	protected Rectangle getTopBounds(AbstractPackageNode pNode)
	{
		Point position = pNode.position();
		Dimension topDimension = getTopDimension(pNode);
		return new Rectangle(position.getX(), position.getY(), topDimension.width(), topDimension.height());
	}
	
	protected abstract Rectangle getBottomBounds(AbstractPackageNode pNode);
}

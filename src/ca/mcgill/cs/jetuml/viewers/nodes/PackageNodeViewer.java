/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018, 2020 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import static ca.mcgill.cs.jetuml.geom.Util.max;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a package in a class diagram.
 */
public final class PackageNodeViewer extends AbstractNodeViewer
{
	private static final int PADDING = 10;
	private static final int TOP_HEIGHT = 20;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_BOTTOM_HEIGHT = 60;
	private static final int DEFAULT_TOP_WIDTH = 60;
	private static final int NAME_GAP = 3;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	private static final StringViewer CONTENTS_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		Rectangle topBounds = getTopBounds(pNode);
		Rectangle bottomBounds = getBottomBounds(pNode);
		ViewUtils.drawRectangle(pGraphics, topBounds );
		ViewUtils.drawRectangle(pGraphics, bottomBounds );
		NAME_VIEWER.draw(((PackageNode)pNode).getName(), pGraphics, new Rectangle(topBounds.getX() + NAME_GAP, 
				topBounds.getY(), topBounds.getWidth(), topBounds.getHeight()));
		CONTENTS_VIEWER.draw(((PackageNode)pNode).getContents(), pGraphics, new Rectangle(bottomBounds.getX() + NAME_GAP, 
				bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight()));
	}
	
	/**
	 * @param pNode The package node
	 * @return The point that corresponds to the actual top right
	 *     corner of the figure (as opposed to bounds).
	 */
	public Point getTopRightCorner(Node pNode)
	{
		Rectangle bottomBounds = getBottomBounds(pNode);
		return new Point(bottomBounds.getMaxX(), bottomBounds.getY());
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		Rectangle topBounds = getTopBounds(pNode);
		Rectangle bottomBounds = getBottomBounds(pNode);
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

	/*
	 * Computes the bounding box that encompasses all children.
	 */
	private Optional<Rectangle> getChildrenBounds(Node pNode)
	{
		if( ((PackageNode)pNode).getChildren().isEmpty() )
		{
			return Optional.empty();
		}
		Rectangle childBounds = null;
		for( Node child : ((PackageNode)pNode).getChildren() )
		{
			if( childBounds == null )
			{
				childBounds = NodeViewerRegistry.getBounds(child);
			}
			else
			{
				childBounds = childBounds.add(NodeViewerRegistry.getBounds(child));
			}
		}
		assert childBounds != null;
		return Optional.of(childBounds);
	}
	
	/*
	 * The node's position might have to get adjusted if there are children
	 * whose position is to the left or up of the node's position.
	 */
	private Point getPosition(Node pNode, Optional<Rectangle> pChildrenBounds)
	{
		if( !pChildrenBounds.isPresent() )
		{
			return pNode.position();
		}
		return new Point(pChildrenBounds.get().getX() - PADDING, pChildrenBounds.get().getY() - PADDING - TOP_HEIGHT);
	}
	
	private Dimension getTopDimension(Node pNode)
	{
		Dimension nameBounds = NAME_VIEWER.getDimension(((PackageNode)pNode).getName());
		int topWidth = max(nameBounds.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		return new Dimension(topWidth, TOP_HEIGHT);
	}
	
	@Override
	public Rectangle getBounds(Node pNode)
	{
		return getTopBounds(pNode).add(getBottomBounds(pNode));
	}
	
	private Rectangle getTopBounds(Node pNode)
	{
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNode);
		Point position = getPosition(pNode, childrenBounds);
		Dimension topDimension = getTopDimension(pNode);
		return new Rectangle(position.getX(), position.getY(), topDimension.getWidth(), topDimension.getHeight());
	}
	
	private Rectangle getBottomBounds(Node pNode)
	{
		Dimension contentsBounds = CONTENTS_VIEWER.getDimension(((PackageNode)pNode).getContents());
		int width = max(contentsBounds.getWidth() + 2 * PADDING, DEFAULT_WIDTH);
		int height = max(contentsBounds.getHeight() + 2 * PADDING, DEFAULT_BOTTOM_HEIGHT);
		
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNode);
		Point position = getPosition(pNode, childrenBounds);
		
		Dimension topDimension = getTopDimension(pNode);
		
		if( childrenBounds.isPresent() )
		{
			width = max( width, childrenBounds.get().getMaxX() + PADDING - position.getX());
			height = max( height, childrenBounds.get().getMaxY() + PADDING - position.getY() - topDimension.getHeight());
		}
		
		width = max( width, topDimension.getWidth()+ (DEFAULT_WIDTH - DEFAULT_TOP_WIDTH));
		
		return new Rectangle(position.getX(), position.getY() + topDimension.getHeight(), 
				width, height);
	}
}

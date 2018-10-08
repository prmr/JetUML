/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.nodes;

import static ca.mcgill.cs.jetuml.geom.Util.max;

import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
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
public final class PackageNodeView extends AbstractNodeView
{
	private static final int PADDING = 10;
	private static final int TOP_HEIGHT = 20;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_BOTTOM_HEIGHT = 60;
	private static final int DEFAULT_TOP_WIDTH = 60;
	private static final int NAME_GAP = 3;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	private static final StringViewer CONTENTS_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public PackageNodeView(PackageNode pNode)
	{
		super(pNode);
	}
	
	private String name()
	{
		return ((PackageNode)node()).getName();
	}
	
	private String contents()
	{
		return ((PackageNode)node()).getContents();
	}
	
	private List<ChildNode> children()
	{
		return ((PackageNode)node()).getChildren();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Rectangle topBounds = getTopBounds();
		Rectangle bottomBounds = getBottomBounds();
		ViewUtils.drawRectangle(pGraphics, topBounds );
		ViewUtils.drawRectangle(pGraphics, bottomBounds );
		NAME_VIEWER.draw(name(), pGraphics, new Rectangle(topBounds.getX() + NAME_GAP, 
				topBounds.getY(), topBounds.getWidth(), topBounds.getHeight()));
		CONTENTS_VIEWER.draw(contents(), pGraphics, new Rectangle(bottomBounds.getX() + NAME_GAP, 
				bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight()));
	}
	
	/**
	 * @return The point that corresponds to the actual top right
	 * corner of the figure (as opposed to bounds).
	 */
	public Point getTopRightCorner()
	{
		Rectangle bottomBounds = getBottomBounds();
		return new Point(bottomBounds.getMaxX(), bottomBounds.getY());
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle topBounds = getTopBounds();
		Rectangle bottomBounds = getBottomBounds();
		Rectangle bounds = topBounds.add(bottomBounds);
		
		Point connectionPoint = super.getConnectionPoint(pDirection);
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
	private Optional<Rectangle> getChildrenBounds()
	{
		if( children().isEmpty() )
		{
			return Optional.empty();
		}
		Rectangle childBounds = null;
		for( ChildNode child : children() )
		{
			if( childBounds == null )
			{
				childBounds = child.view().getBounds();
			}
			else
			{
				childBounds = childBounds.add(child.view().getBounds());
			}
		}
		assert childBounds != null;
		return Optional.of(childBounds);
	}
	
	/*
	 * The node's position might have to get adjusted if there are children
	 * whose position is to the left or up of the node's position.
	 */
	private Point getPosition(Optional<Rectangle> pChildrenBounds)
	{
		if( !pChildrenBounds.isPresent() )
		{
			return node().position();
		}
		int x = Math.min(node().position().getX(), pChildrenBounds.get().getX() - PADDING);
		int y = Math.min(node().position().getY(), pChildrenBounds.get().getY() - PADDING - TOP_HEIGHT);
		return new Point(x, y);
	}
	
	private Dimension getTopDimension()
	{
		Rectangle nameBounds = NAME_VIEWER.getBounds(name());
		int topWidth = max(nameBounds.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		return new Dimension(topWidth, TOP_HEIGHT);
	}
	
	@Override
	public Rectangle getBounds()
	{
		return getTopBounds().add(getBottomBounds());
	}
	
	private Rectangle getTopBounds()
	{
		Optional<Rectangle> childrenBounds = getChildrenBounds();
		Point position = getPosition(childrenBounds);
		Dimension topDimension = getTopDimension();
		return new Rectangle(position.getX(), position.getY(), topDimension.getWidth(), topDimension.getHeight());
	}
	
	private Rectangle getBottomBounds()
	{
		Rectangle contentsBounds = CONTENTS_VIEWER.getBounds(contents());
		int width = max(contentsBounds.getWidth() + 2 * PADDING, DEFAULT_WIDTH);
		int height = max(contentsBounds.getHeight() + 2 * PADDING, DEFAULT_BOTTOM_HEIGHT);
		
		Optional<Rectangle> childrenBounds = getChildrenBounds();
		Point position = getPosition(childrenBounds);
		
		Dimension topDimension = getTopDimension();
		
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

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

import static org.jetuml.geom.GeomUtils.max;

import java.util.Optional;

import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.AbstractPackageNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;

/**
 * An object to render a package in a class diagram.
 */
public final class PackageNodeRenderer extends AbstractPackageNodeRenderer
{
	/**
	 * @param pParent Renderer of the parent diagram.
	 */
	public PackageNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
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
				childBounds = parent().getBounds(child);
			}
			else
			{
				childBounds = childBounds.add(parent().getBounds(child));
			}
		}
		assert childBounds != null;
		return Optional.of(childBounds);
	}
	
	/*
	 * The node's position might have to get adjusted if there are children
	 * whose position is to the left or up of the node's position.
	 */
	private Point getPosition(AbstractPackageNode pNode, Optional<Rectangle> pChildrenBounds)
	{
		if( !pChildrenBounds.isPresent() )
		{
			return pNode.position();
		}
		return new Point(pChildrenBounds.get().x() - PADDING, 
				pChildrenBounds.get().y() - PADDING - getTopDimension(pNode).height());
	}
	
	@Override
	protected Rectangle getTopBounds(AbstractPackageNode pNode)
	{
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNode);
		Point position = getPosition(pNode, childrenBounds);
		Dimension topDimension = getTopDimension(pNode);
		return new Rectangle(position.x(), position.y(), topDimension.width(), topDimension.height());
	}
	
	@Override
	protected Rectangle getBottomBounds(AbstractPackageNode pNode)
	{
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_BOTTOM_HEIGHT;
		
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNode);
		Point position = getPosition(pNode, childrenBounds);
		
		Dimension topDimension = getTopDimension(pNode);
		
		if( childrenBounds.isPresent() )
		{
			width = max( width, childrenBounds.get().maxX() + PADDING - position.x());
			height = max( height, childrenBounds.get().maxY() + PADDING - position.y() - topDimension.height());
		}
		
		width = max( width, topDimension.width()+ (DEFAULT_WIDTH - DEFAULT_TOP_WIDTH));
		
		return new Rectangle(position.x(), position.y() + topDimension.height(), 
				width, height);
	}
}

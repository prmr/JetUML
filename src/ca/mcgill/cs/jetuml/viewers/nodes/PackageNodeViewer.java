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

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.AbstractPackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * An object to render a package in a class diagram.
 */
public final class PackageNodeViewer extends AbstractPackageNodeViewer
{
	/*
	 * Computes the bounding box that encompasses all children.
	 */
	private static Optional<Rectangle> getChildrenBounds(Node pNode)
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
	private Point getPosition(AbstractPackageNode pNode, Optional<Rectangle> pChildrenBounds)
	{
		if( !pChildrenBounds.isPresent() )
		{
			return pNode.position();
		}
		return new Point(pChildrenBounds.get().getX() - PADDING, 
				pChildrenBounds.get().getY() - PADDING - getTopDimension(pNode).height());
	}
	
	@Override
	protected Rectangle getTopBounds(AbstractPackageNode pNode)
	{
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNode);
		Point position = getPosition(pNode, childrenBounds);
		Dimension topDimension = getTopDimension(pNode);
		return new Rectangle(position.getX(), position.getY(), topDimension.width(), topDimension.height());
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
			width = max( width, childrenBounds.get().getMaxX() + PADDING - position.getX());
			height = max( height, childrenBounds.get().getMaxY() + PADDING - position.getY() - topDimension.height());
		}
		
		width = max( width, topDimension.width()+ (DEFAULT_WIDTH - DEFAULT_TOP_WIDTH));
		
		return new Rectangle(position.getX(), position.getY() + topDimension.height(), 
				width, height);
	}
}

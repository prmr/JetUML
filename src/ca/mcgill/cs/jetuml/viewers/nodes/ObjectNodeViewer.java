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
package ca.mcgill.cs.jetuml.viewers.nodes;

import static java.util.EnumSet.allOf;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an object in an object diagram.
 */
public final class ObjectNodeViewer extends AbstractNodeViewer
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int TEXT_HORIZONTAL_MARGIN = 5;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	private static final StringViewer NAME_VIEWER = StringViewer.get(StringViewer.VerticalAlign.CENTER, 
			StringViewer.HorizontalAlign.CENTER, allOf(StringViewer.TextDecorations.class));
	private static final FieldNodeViewer FIELD_NODE_VIEWER = new FieldNodeViewer();
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pNode);
		final Rectangle topRectangle = getTopRectangle(pNode);
		int dividerPosition = topRectangle.getMaxY();
		ViewUtils.drawRectangle(pGraphics, bounds);
		if( ((ObjectNode)pNode).getChildren().size() > 0 ) 
		{
			ViewUtils.drawLine(pGraphics, bounds.getX(), dividerPosition, bounds.getMaxX(), dividerPosition, LineStyle.SOLID);
		}
		NAME_VIEWER.draw(((ObjectNode)pNode).getName(), pGraphics, 
				new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), topRectangle.getHeight()));
	}
	
	private Rectangle getTopRectangle(Node pNode)
	{
		Dimension bounds = NAME_VIEWER.getDimension(((ObjectNode)pNode).getName() + TEXT_HORIZONTAL_MARGIN); 
		bounds = bounds.include(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		return new Rectangle(0, 0, bounds.width(), bounds.height()).translated(pNode.position().getX(), pNode.position().getY());
	}
	
	/**
	 * @param pNode The node
	 * @return The position that represents the split between the name and value fields.
	 */
	public int getSplitPosition(Node pNode)
	{
		int leftWidth = 0;
		for(Node field : ((ObjectNode)pNode).getChildren())
		{
			leftWidth = Math.max(leftWidth, FIELD_NODE_VIEWER.leftWidth(field));
		}
		return pNode.position().getX() + leftWidth + XGAP;
	}
	
	@Override
	public Rectangle getBounds(Node pNode)
	{
		Rectangle bounds = getTopRectangle(pNode);
		int leftWidth = 0;
		int rightWidth = 0;
		int height = 0;
		if( ((ObjectNode)pNode).getChildren().size() > 0 )
		{
			height = YGAP;
		}
		for(Node field : ((ObjectNode)pNode).getChildren())
		{
			height += FIELD_NODE_VIEWER.getHeight(field) + YGAP;   
			leftWidth = Math.max(leftWidth, FIELD_NODE_VIEWER.leftWidth(field));
			rightWidth = Math.max(rightWidth, FIELD_NODE_VIEWER.rightWidth(field));
		}
		int width = Math.max(bounds.getWidth(), leftWidth + rightWidth + 2 * XGAP);
		width = Grid.toMultiple(width);
		return new Rectangle(bounds.getX(), bounds.getY(), width, Grid.toMultiple(bounds.getHeight() + height));
	}
	
	/**
	 * @param pNode The object node.
	 * @param pFieldNode The node whose position to compute.
	 * @return The y position of a child node.
	 */
	public int getYPosition(Node pNode, FieldNode pFieldNode)
	{
		assert ((ObjectNode)pNode).getChildren().contains(pFieldNode);
		Rectangle bounds = getTopRectangle(pNode);
		int yPosition = bounds.getMaxY() + YGAP; 
		for( Node field : ((ObjectNode)pNode).getChildren() )
		{
			yPosition += YGAP;
			if( field == pFieldNode )
			{
				return yPosition;
			}
			yPosition += FIELD_NODE_VIEWER.getHeight(field);
		}
		return yPosition;
	}
}

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

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.Grid;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingUtils;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.StringRenderer.TextDecoration;

import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an object in an object diagram.
 */
public final class ObjectNodeRenderer extends AbstractNodeRenderer
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int TEXT_HORIZONTAL_MARGIN = 5;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	private static final StringRenderer NAME_VIEWER = StringRenderer.get(Alignment.CENTER_CENTER, 
			TextDecoration.BOLD, TextDecoration.UNDERLINED, TextDecoration.PADDED);
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public ObjectNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pElement);
		Node node = (Node) pElement;
		final Rectangle topRectangle = getTopRectangle(node);
		int dividerPosition = topRectangle.maxY();
		RenderingUtils.drawRectangle(pGraphics, bounds);
		if( ((ObjectNode)node).getChildren().size() > 0 ) 
		{
			RenderingUtils.drawLine(pGraphics, bounds.x(), dividerPosition, bounds.maxX(), dividerPosition, LineStyle.SOLID);
		}
		NAME_VIEWER.draw(((ObjectNode)node).getName(), pGraphics, 
				new Rectangle(bounds.x(), bounds.y(), bounds.width(), topRectangle.height()));
	}
	
	private static Rectangle getTopRectangle(Node pNode)
	{
		Dimension bounds = NAME_VIEWER.getDimension(((ObjectNode)pNode).getName() + TEXT_HORIZONTAL_MARGIN); 
		bounds = bounds.include(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		return new Rectangle(0, 0, bounds.width(), bounds.height()).translated(pNode.position().x(), pNode.position().y());
	}
	
	/**
	 * @param pNode The node
	 * @return The position that represents the split between the name and value fields.
	 */
	public static int getSplitPosition(Node pNode)
	{
		assert ObjectNode.class.isInstance(pNode);
		int leftWidth = 0;
		for(Node field : ((ObjectNode)pNode).getChildren())
		{
			leftWidth = Math.max(leftWidth, FieldNodeRenderer.leftWidth(field));
		}
		return pNode.position().x() + leftWidth + XGAP;
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
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
			height += FieldNodeRenderer.getHeight(field) + YGAP;   
			leftWidth = Math.max(leftWidth, FieldNodeRenderer.leftWidth(field));
			rightWidth = Math.max(rightWidth, FieldNodeRenderer.rightWidth(field));
		}
		int width = Math.max(bounds.width(), leftWidth + rightWidth + 2 * XGAP);
		width = Grid.toMultiple(width);
		return new Rectangle(bounds.x(), bounds.y(), width, Grid.toMultiple(bounds.height() + height));
	}
	
	/**
	 * @param pNode The object node.
	 * @param pFieldNode The node whose position to compute.
	 * @return The y position of a child node.
	 */
	public static int getYPosition(Node pNode, FieldNode pFieldNode)
	{
		assert ((ObjectNode)pNode).getChildren().contains(pFieldNode);
		Rectangle bounds = getTopRectangle(pNode);
		int yPosition = bounds.maxY() + YGAP; 
		for( Node field : ((ObjectNode)pNode).getChildren() )
		{
			yPosition += YGAP;
			if( field == pFieldNode )
			{
				return yPosition;
			}
			yPosition += FieldNodeRenderer.getHeight(field);
		}
		return yPosition;
	}
}

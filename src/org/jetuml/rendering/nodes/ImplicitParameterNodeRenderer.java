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

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingUtils;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.StringRenderer.TextDecoration;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render an implicit parameter in a Sequence diagram.
 */
public final class ImplicitParameterNodeRenderer extends AbstractNodeRenderer
{
	public static final int TOP_HEIGHT = 60;
	
	private static final int ICON_HEIGHT = 120;
	private static final int ICON_WIDTH = 80;

	
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	private static final int HORIZONTAL_PADDING = 10; // 2x the left and right padding around the name of the implicit parameter
	private static final int TAIL_HEIGHT = 20; // Piece of the life line below the last call node
	private static final StringRenderer NAME_VIEWER = 
			StringRenderer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED, TextDecoration.UNDERLINED);
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public ImplicitParameterNodeRenderer(DiagramRenderer pParent)
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
		Rectangle top = getTopRectangle((Node)pElement);
		RenderingUtils.drawRectangle(pGraphics, top);
		NAME_VIEWER.draw(((ImplicitParameterNode)pElement).getName(), pGraphics, top);
		int xmid = top.center().x();
		RenderingUtils.drawLine(pGraphics, xmid,  top.maxY(), xmid, getBounds(pElement).maxY(), LineStyle.DOTTED);
	}
	
	@Override
	public boolean contains(DiagramElement pElement, Point pPoint)
	{
		final Rectangle bounds = getBounds(pElement);
		return bounds.x() <= pPoint.x() && pPoint.x() <= bounds.x() + bounds.width();
	}

	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		Rectangle bounds = getBounds(pNode);
		if(pDirection == Direction.EAST)
		{
			return new Point(bounds.maxX(), bounds.y() + TOP_HEIGHT / 2);
		}
		else
		{
			return new Point(bounds.x(), bounds.y() + TOP_HEIGHT / 2);
		}
	}
	
	/*
	 * @return The width of the top rectangle.
	 */
	private static int getWidth(DiagramElement pElement)
	{
		assert pElement != null;
		assert pElement instanceof ImplicitParameterNode;
		return Math.max(NAME_VIEWER.getDimension(((ImplicitParameterNode)pElement).getName()).width()+ 
				HORIZONTAL_PADDING, DEFAULT_WIDTH);
	}
	
	private Point getMaxXYofChildren(Node pNode)
	{
		int maxY = 0;
		int maxX = 0;
		for( Node child : ((ImplicitParameterNode)pNode).getChildren() )
		{
			Rectangle bounds = parent().getBounds(child);
			maxX = Math.max(maxX,  bounds.maxX());
			maxY = Math.max(maxY, bounds.maxY());
		}
		return new Point(maxX, maxY);
	}
	
	/**
     * Returns the rectangle at the top of the object node.
     * @param pNode the node.
     * @return the top rectangle
	 */
	public Rectangle getTopRectangle(Node pNode)
	{
		return new Rectangle(pNode.position().x(), 				
				((SequenceDiagramRenderer)parent()).getLifelineTop((ImplicitParameterNode) pNode) - TOP_HEIGHT,
				getWidth(pNode), 									
				TOP_HEIGHT);										
	}
	
	/**
	 * @return The x-coordinate of the center of pNode.
	 * @pre pNode != null;
	 */
	public static int getCenterXCoordinate(Node pNode)
	{
		assert pNode != null;
		return Math.max(NAME_VIEWER.getDimension(((ImplicitParameterNode)pNode).getName()).width()+ 
				HORIZONTAL_PADDING, DEFAULT_WIDTH)/2 + pNode.position().x();
	}

	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Rectangle topRectangle = getTopRectangle(pNode);
		Point childrenMaxXY = getMaxXYofChildren(pNode);
		int width = max(topRectangle.width(), DEFAULT_WIDTH, childrenMaxXY.x() - pNode.position().x());
		int height = max(DEFAULT_HEIGHT, childrenMaxXY.y() + TAIL_HEIGHT) - topRectangle.y();	
		return new Rectangle(pNode.position().x(), topRectangle.y(), width, height);
	}
	
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		int width = ICON_WIDTH;
		int height = ICON_HEIGHT;
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		Rectangle top = new Rectangle(0, 0, DEFAULT_WIDTH, TOP_HEIGHT);
		RenderingUtils.drawRectangle(canvas.getGraphicsContext2D(), top);
		int xmid = DEFAULT_WIDTH/2;
		RenderingUtils.drawLine(canvas.getGraphicsContext2D(), xmid,  top.maxY(), xmid, height, LineStyle.DOTTED);
		return canvas;
	}
}

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
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Alignment;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a FieldNode.
 */
public final class FieldNodeRenderer extends AbstractNodeRenderer
{
	private static final String ICON_LABEL = "x = y";
	private static final String EQUALS = " = ";
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 20;
	private static final int XGAP = 5;
	private static final StringRenderer VALUE_VIEWER = StringRenderer.get(Alignment.TOP_LEFT);
	private static final StringRenderer NAME_VIEWER = StringRenderer.get(Alignment.TOP_LEFT);
	private static final StringRenderer EQUALS_VIEWER = StringRenderer.get(Alignment.TOP_CENTER);
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public FieldNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private ObjectNodeRenderer objectNodeViewer()
	{
		return (ObjectNodeRenderer) parent().rendererFor(ObjectNode.class);
	}
	
	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pElement);
		Node node = (Node) pElement;
		final int split = getSplitPosition(node);
		final int leftWidth = leftWidth(node);
		final int midOffset = EQUALS_VIEWER.getDimension(EQUALS).width() / 2;
		NAME_VIEWER.draw(((FieldNode)node).getName(), pGraphics, 
				new Rectangle(split - leftWidth, bounds.y(), leftWidth, bounds.height()));
		EQUALS_VIEWER.draw(EQUALS, pGraphics, new Rectangle(split - midOffset, bounds.y(), midOffset * 2, bounds.height()));
		VALUE_VIEWER.draw(((FieldNode)node).getValue(), 
				pGraphics, new Rectangle(split + midOffset, bounds.y(), rightWidth(node), bounds.height()));
	}
	
	private static int getSplitPosition(Node pNode)
	{
		ObjectNode parent = (ObjectNode)pNode.getParent();
		if( parent != null )
		{
			return ObjectNodeRenderer.getSplitPosition(parent);
		}
		else
		{
			return DEFAULT_WIDTH / 2;
		}
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		final int leftWidth = leftWidth(pNode);
		final int height = getHeight(pNode);
		if( pNode.hasParent() )
		{
			int yPosition = ObjectNodeRenderer.getYPosition(pNode.getParent(), (FieldNode) pNode);
			Rectangle parentBounds = objectNodeViewer().getBounds(pNode.getParent());
			return new Rectangle(parentBounds.x() + XGAP, yPosition, parentBounds.width() - 2*XGAP, height);
		}
		return new Rectangle(DEFAULT_WIDTH / 2 - leftWidth, 0, leftWidth + rightWidth(pNode), height);
	}
	
	/**
	 * @param pNode The node
	 * @return The width of the left (name) part of the node.
	 */
	public static int leftWidth(Node pNode)
	{
		assert FieldNode.class.isInstance(pNode);
		final int midOffset = EQUALS_VIEWER.getDimension(EQUALS).width() / 2;
		return NAME_VIEWER.getDimension(((FieldNode)pNode).getName()).width() + midOffset;
	}
	
	/**
	 * @param pNode The node.
	 * @return The width of the right (value) part of the node.
	 */
	public static int rightWidth(Node pNode)
	{
		assert FieldNode.class.isInstance(pNode);
		final int midOffset = EQUALS_VIEWER.getDimension(EQUALS).width() / 2;
		int rightWidth = VALUE_VIEWER.getDimension(((FieldNode)pNode).getValue()).width();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		return rightWidth + midOffset;
	}
	
	/**
	 * @param pNode The node
	 * @return The height of this node.
	 */
	public static int getHeight(Node pNode)
	{
		assert FieldNode.class.isInstance(pNode);
		return Math.max(DEFAULT_HEIGHT, Math.max(NAME_VIEWER.getDimension(((FieldNode)pNode).getName()).height(), 
				Math.max(VALUE_VIEWER.getDimension(((FieldNode)pNode).getValue()).height(), 
						EQUALS_VIEWER.getDimension(EQUALS).height())));
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		final Rectangle bounds = getBounds(pNode);
		return new Point(bounds.maxX() - XGAP, bounds.center().y());
	}
	
	/*
	 * Custom version because the field node cannot be drawn without a parent.
	 */
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		Dimension dimension = EQUALS_VIEWER.getDimension(ICON_LABEL);
		int width = dimension.width();
		int height = dimension.height();
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), 0);
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		EQUALS_VIEWER.draw(ICON_LABEL, graphics, 
				new Rectangle(0, BUTTON_SIZE/2 - height/2+OFFSET, width, height));
		return canvas;
	}
}

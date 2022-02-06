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

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a FieldNode.
 */
public final class FieldNodeViewer extends AbstractNodeViewer
{
	private static final String ICON_LABEL = "x = y";
	private static final String EQUALS = " = ";
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 20;
	private static final int XGAP = 5;
	private static final StringViewer VALUE_VIEWER = StringViewer.get(Alignment.TOP_LEFT);
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.TOP_LEFT);
	private static final StringViewer EQUALS_VIEWER = StringViewer.get(Alignment.TOP_CENTER);
	private static final ObjectNodeViewer OBJECT_NODE_VIEWER = new ObjectNodeViewer();
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pNode);
		final int split = getSplitPosition(pNode);
		final int leftWidth = leftWidth(pNode);
		final int midOffset = EQUALS_VIEWER.getDimension(EQUALS).width() / 2;
		NAME_VIEWER.draw(((FieldNode)pNode).getName(), pGraphics, 
				new Rectangle(split - leftWidth, bounds.getY(), leftWidth, bounds.getHeight()));
		EQUALS_VIEWER.draw(EQUALS, pGraphics, new Rectangle(split - midOffset, bounds.getY(), midOffset * 2, bounds.getHeight()));
		VALUE_VIEWER.draw(((FieldNode)pNode).getValue(), 
				pGraphics, new Rectangle(split + midOffset, bounds.getY(), rightWidth(pNode), bounds.getHeight()));
	}
	
	private static int getSplitPosition(Node pNode)
	{
		ObjectNode parent = (ObjectNode)pNode.getParent();
		if( parent != null )
		{
			return ObjectNodeViewer.getSplitPosition(parent);
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
			int yPosition = ObjectNodeViewer.getYPosition(pNode.getParent(), (FieldNode) pNode);
			Rectangle parentBounds = OBJECT_NODE_VIEWER.getBounds(pNode.getParent());
			return new Rectangle(parentBounds.getX() + XGAP, yPosition, parentBounds.getWidth() - 2*XGAP, height);
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
		return new Point(bounds.getMaxX() - XGAP, bounds.getCenter().getY());
	}
	
	/*
	 * Custom version because the field node cannot be drawn without a parent.
	 */
	@Override
	public Canvas createIcon(Node pNode)
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

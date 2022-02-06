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

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.GeomUtils;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Basic services for drawing nodes.
 */
public abstract class AbstractNodeViewer implements NodeViewer
{
	public static final int BUTTON_SIZE = 25;
	public static final int OFFSET = 3;
	
	private NodeStorage aNodeStorage = new NodeStorage();
	
	/* 
	 * The default behavior for containment is to return true if the point is
	 * within the bounding box of the node view.
	 * @see ca.mcgill.cs.jetuml.viewers.DiagramElementView#contains(ca.mcgill.cs.jetuml.geom.Point)
	 */
	@Override
	public boolean contains(Node pNode, Point pPoint)
	{
		return getBounds(pNode).contains(pPoint);
	}
	
	/* 
	 * The default behavior is to returns a point on the bounds of the node that intersects
	 * the side of the node at the point where a line in pDirection originating from the center
	 * intersects it.
	 */
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		return GeomUtils.intersectRectangle(getBounds(pNode), pDirection);
	}
	
	@Override
	public void drawSelectionHandles(Node pNode, GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getBounds(pNode));		
	}
	
	@Override
	public Canvas createIcon(Node pNode)
	{
		Rectangle bounds = getBounds(pNode);
		int width = bounds.getWidth();
		int height = bounds.getHeight();
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		draw(pNode, canvas.getGraphicsContext2D());
		return canvas;
	}
	
	@Override
	public final Rectangle getBounds(Node pNode)
	{
		return aNodeStorage.getBounds(pNode, this::internalGetBounds);
	}
	
	@Override
	public final void activateNodeStorage()
	{
		aNodeStorage.activate();
	}
	
	@Override
	public final void deactivateAndClearNodeStorage() 
	{
		aNodeStorage.deactivateAndClear();
	}
	
	/**
     * Gets the smallest rectangle that bounds this element.
     * The bounding rectangle contains all labels.
     * @param pNode The node whose bounds we want.
     * @pre pNode != null
     * @return the bounding rectangle
   	 */
	protected abstract Rectangle internalGetBounds(Node pNode);
}

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

package org.jetuml.rendering.edges;

import static org.jetuml.rendering.ArrowHead.NONE;
import static org.jetuml.rendering.ArrowHead.V;

import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.ToolGraphics;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Static functions to compute the geometry of arrow heads and render them.
 */
public final class ArrowHeadRenderer
{
	private static final double ARROW_ANGLE = Math.PI / 6; 
	private static final double ARROW_LENGTH = 10;
	
	private ArrowHeadRenderer() {}
	
	/**
	 * Draws an arrow head at the end of the axis.
	 * 
	 * @param pGraphics the graphics context
	 * @param pArrowHead The type of arrow head to draw
	 * @param pAxis A line in the direction of the arrow ending a pAxis.getPoint2()
	 */
	public static void draw(GraphicsContext pGraphics, ArrowHead pArrowHead, Line pAxis)
	{
		assert pGraphics != null && pArrowHead != null && pAxis != null;
		
		if(pArrowHead.isFilled()) 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pArrowHead, pAxis), Color.BLACK, false);
		}
		else 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pArrowHead, pAxis), Color.WHITE, false);
		}
	}
	
	/**
	 * Draws an arrow head at pPoint2 for a direction given from pPoint2.
	 * 
	 * @param pGraphics the graphics context
	 * @param pArrowHead The type of arrow head to draw
	 * @param pAxis A line in the direction of the arrow ending a pAxis.getPoint2()
	 */
	public static void draw(GraphicsContext pGraphics, ArrowHead pArrowHead, Point pPoint1, Point pPoint2)
	{
		assert pGraphics != null && pArrowHead != null && pPoint1 != null && pPoint2 != null;
		
		if(pArrowHead.isFilled()) 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pArrowHead, new Line(pPoint1, pPoint2)), Color.BLACK, false);
		}
		else 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pArrowHead, new Line(pPoint1, pPoint2)), Color.WHITE, false);
		}
	}
	
	/**
	 * Get the bounds for an arrow head pointing to the second point in the axis.
	 * 
	 * @param pArrowHead The type of arrow head.
	 * @param pAxis The axis of the arrow.
	 * @return The bounds of the arrow head.
	 */
	public static Rectangle getBounds(ArrowHead pArrowHead, Line pAxis)
	{
		return toRectangle(getPath(pArrowHead, pAxis).getBoundsInLocal());
	}
	
   	/**
     * Gets the path of the arrowhead.
     * 
     * @param pAxis The axis of the arrow. The Arrow points to pAxis.getPoint2()
     * @return the path
     */
   	private static Path getPath(ArrowHead pArrowHead, Line pAxis)
   	{
   		if(pArrowHead == NONE) 
   		{
   			return new Path();
   		}
   		
   		int dx = pAxis.x2() - pAxis.x1();
   		int dy = pAxis.y2() - pAxis.y1();
   		final double angle = Math.atan2(dy, dx);
   		int x1 = GeomUtils.round(pAxis.x2() - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE));
   		int y1 = GeomUtils.round(pAxis.y2() - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE));
   		int x2 = GeomUtils.round(pAxis.x2() - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE));
   		int y2 = GeomUtils.round(pAxis.y2() - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE));

   		MoveTo moveToOrigin = new MoveTo(pAxis.x2(), pAxis.y2());
   		LineTo lineTo1 = new LineTo(x1, y1);
   		Path path = new Path();
   		path.getElements().addAll(moveToOrigin, lineTo1);
   		if(pArrowHead == V)
   		{
   			MoveTo moveTo2 = new MoveTo(x2, y2);
   			LineTo lineTo2 = new LineTo(pAxis.x2(), pAxis.y2());
   			path.getElements().addAll(moveTo2, lineTo2);
   		}
   		else if(pArrowHead.isTriangle())
   		{
   			LineTo lineTo3 = new LineTo(x2, y2); 
   			LineTo lineTo4 = new LineTo(moveToOrigin.getX(), moveToOrigin.getY());
   			path.getElements().addAll(lineTo3, lineTo4);
   		}
   		else if(pArrowHead.isDiamond())
   		{
   			final int x3 = GeomUtils.round( x2 - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE));
   			final int y3 = GeomUtils.round( y2 - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE));
   			LineTo lineTo5 = new LineTo(x3, y3);
   			LineTo lineTo6 = new LineTo(x2, y2);
   			LineTo lineTo7 = new LineTo(moveToOrigin.getX(), moveToOrigin.getY());
   			path.getElements().addAll(lineTo5, lineTo6, lineTo7);
   		}      
   		return path;
   	}
   	
   	/*
	 * @param pBounds An input bounds object.
	 * @return A rectangle that corresponds to pBounds.
	 * @pre pBounds != null;
	 */
	private static Rectangle toRectangle(Bounds pBounds)
	{
		assert pBounds != null;
		return new Rectangle((int)pBounds.getMinX(), (int)pBounds.getMinY(), (int)pBounds.getWidth(), (int)pBounds.getHeight());
	}
}

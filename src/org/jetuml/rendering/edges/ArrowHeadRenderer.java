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

import org.jetuml.geom.Point;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.ToolGraphics;

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
	 * Draws the arrowhead.
	 * 
	 * @param pGraphics the graphics context
	 * @param pArrowHead The type of arrow head to draw
	 * @param pPoint1 a point on the axis of the arrow head
	 * @param pEnd the end point of the arrow head
	 */
	public void draw(GraphicsContext pGraphics, ArrowHead pArrowHead, Point pPoint1, Point pEnd)
	{
		assert pGraphics != null && pArrowHead != null && pPoint1 != null && pEnd != null;
		
		if(pArrowHead.isFilled()) 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pArrowHead, pPoint1, pEnd), Color.BLACK, false);
		}
		else 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pArrowHead, pPoint1, pEnd), Color.WHITE, false);
		}
	}
	
   	/**
     * Gets the path of the arrowhead.
     * 
     * @param pPoint1 a point on the axis of the arrow head
     * @param pEnd the end point of the arrow head
     * @return the path
     */
   	private Path getPath(ArrowHead pArrowHead, Point pPoint1, Point pEnd)
   	{
   		if(pArrowHead == NONE) 
   		{
   			return new Path();
   		}
   		
   		int dx = pEnd.getX() - pPoint1.getX();
   		int dy = pEnd.getY() - pPoint1.getY();
   		final double angle = Math.atan2(dy, dx);
   		int x1 = (int) Math.round(pEnd.getX() - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE));
   		int y1 = (int) Math.round(pEnd.getY() - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE));
   		int x2 = (int) Math.round(pEnd.getX() - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE));
   		int y2 = (int) Math.round(pEnd.getY() - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE));

   		MoveTo moveToOrigin = new MoveTo(pEnd.getX(), pEnd.getY());
   		LineTo lineTo1 = new LineTo(x1, y1);
   		Path path = new Path();
   		path.getElements().addAll(moveToOrigin, lineTo1);
   		if(pArrowHead == V)
   		{
   			MoveTo moveTo2 = new MoveTo(x2, y2);
   			LineTo lineTo2 = new LineTo(pEnd.getX(), pEnd.getY());
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
   			final int x3 = (int) Math.round( x2 - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE));
   			final int y3 = (int) Math.round( y2 - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE));
   			LineTo lineTo5 = new LineTo(x3, y3);
   			LineTo lineTo6 = new LineTo(x2, y2);
   			LineTo lineTo7 = new LineTo(moveToOrigin.getX(), moveToOrigin.getY());
   			path.getElements().addAll(lineTo5, lineTo6, lineTo7);
   		}      
   		return path;
   	}
}

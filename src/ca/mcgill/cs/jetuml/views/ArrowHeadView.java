/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.views;

import static ca.mcgill.cs.jetuml.views.ArrowHead.BLACK_DIAMOND;
import static ca.mcgill.cs.jetuml.views.ArrowHead.BLACK_TRIANGLE;
import static ca.mcgill.cs.jetuml.views.ArrowHead.DIAMOND;
import static ca.mcgill.cs.jetuml.views.ArrowHead.NONE;
import static ca.mcgill.cs.jetuml.views.ArrowHead.TRIANGLE;
import static ca.mcgill.cs.jetuml.views.ArrowHead.V;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Defines how to draw arrow heads.
 */
public final class ArrowHeadView
{
	private static final double ARROW_ANGLE = Math.PI / 6; 
	private static final double ARROW_LENGTH = 10;
	
	private final ArrowHead aArrowHead;
	
	/**
	 * Creates a new view for pArrowHead.
	 * 
	 * @param pArrowHead The arrowhead to wrap.
	 */
	public ArrowHeadView(ArrowHead pArrowHead)
	{
		aArrowHead = pArrowHead;
	}
	
	/**
	 * Draws the arrowhead.
	 * @param pGraphics the graphics context
	 * @param pPoint1 a point on the axis of the arrow head
	 * @param pEnd the end point of the arrow head
	 */
	public void draw(GraphicsContext pGraphics, Point2D pPoint1, Point2D pEnd)
	{
		if(aArrowHead == ArrowHead.BLACK_DIAMOND || aArrowHead == BLACK_TRIANGLE) 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pPoint1, pEnd), Color.BLACK, false);
		}
		else 
		{
			ToolGraphics.strokeAndFillSharpPath(pGraphics, getPath(pPoint1, pEnd), Color.WHITE, false);
		}
	}
	
   	/**
     * Gets the path of the arrowhead.
     * @param pPoint1 a point on the axis of the arrow head
     * @param pEnd the end point of the arrow head
     * @return the path
     */
   	public Path getPath(Point2D pPoint1, Point2D pEnd)
   	{
   		Path path = new Path();
   		if(aArrowHead == NONE) 
   		{
   			return path;
   		}
   		
   		double dx = pEnd.getX() - pPoint1.getX();
   		double dy = pEnd.getY() - pPoint1.getY();
   		double angle = Math.atan2(dy, dx);
   		double x1 = pEnd.getX() - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
   		double y1 = pEnd.getY() - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
   		double x2 = pEnd.getX() - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE);
   		double y2 = pEnd.getY() - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE);

   		MoveTo moveToOrigin = new MoveTo(pEnd.getX(), pEnd.getY());
   		LineTo lineTo1 = new LineTo(x1, y1);
   		path.getElements().addAll(moveToOrigin, lineTo1);
   		if(aArrowHead == V)
   		{
   			MoveTo moveTo2 = new MoveTo(x2, y2);
   			LineTo lineTo2 = new LineTo(pEnd.getX(), pEnd.getY());
   			path.getElements().addAll(moveTo2, lineTo2);
   		}
   		else if(aArrowHead == TRIANGLE || aArrowHead == BLACK_TRIANGLE)
   		{
   			LineTo lineTo3 = new LineTo(x2, y2); 
   			LineTo lineTo4 = new LineTo(moveToOrigin.getX(), moveToOrigin.getY());
   			path.getElements().addAll(lineTo3, lineTo4);
   		}
   		else if(aArrowHead == DIAMOND || aArrowHead == BLACK_DIAMOND)
   		{
   			double x3 = x2 - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
   			double y3 = y2 - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
   			LineTo lineTo5 = new LineTo(x3, y3);
   			LineTo lineTo6 = new LineTo(x2, y2);
   			LineTo lineTo7 = new LineTo(moveToOrigin.getX(), moveToOrigin.getY());
   			path.getElements().addAll(lineTo5, lineTo6, lineTo7);
   		}      
   		return path;
   	}
}

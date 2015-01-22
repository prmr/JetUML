/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * This class defines arrowheads of various shapes.
 */
public final class ArrowHead
{
	public static final ArrowHead NONE = new ArrowHead();
	public static final ArrowHead TRIANGLE = new ArrowHead();
	public static final ArrowHead BLACK_TRIANGLE = new ArrowHead();
	public static final ArrowHead V = new ArrowHead();
	public static final ArrowHead HALF_V = new ArrowHead();
	public static final ArrowHead DIAMOND = new ArrowHead();
	public static final ArrowHead BLACK_DIAMOND = new ArrowHead();
	   
	// CSOFF:
	private static final double ARROW_ANGLE = Math.PI / 6; 
	private static final double ARROW_LENGTH = 10;
	// CSON:
	
   private ArrowHead() {}
   
   /**
    * Draws the arrowhead.
    * @param pGraphics2D the graphics context
    * @param pPoint1 a point on the axis of the arrow head
    * @param pEnd the end point of the arrow head
    */
   public void draw(Graphics2D pGraphics2D, Point2D pPoint1, Point2D pEnd)
   {
	   GeneralPath path = getPath(pPoint1, pEnd);
	   Color oldColor = pGraphics2D.getColor();
	   if(this == BLACK_DIAMOND || this == BLACK_TRIANGLE) 
	   {
		   pGraphics2D.setColor(Color.BLACK);
	   }
	   else 
	   {
		   pGraphics2D.setColor(Color.WHITE);
	   }
	   pGraphics2D.fill(path);
	   pGraphics2D.setColor(oldColor);
	   pGraphics2D.draw(path);
   	}

   	/**
     *  Gets the path of the arrowhead.
     * @param pPoint1 a point on the axis of the arrow head
     * @param pEnd the end point of the arrow head
     * @return the path
     */
   	public GeneralPath getPath(Point2D pPoint1, Point2D pEnd)
   	{
   		GeneralPath path = new GeneralPath();
   		if(this == NONE) 
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

   		path.moveTo((float)pEnd.getX(), (float)pEnd.getY());
   		path.lineTo((float)x1, (float)y1);
   		if(this == V)
   		{
   			path.moveTo((float)x2, (float)y2);
   			path.lineTo((float)pEnd.getX(), (float)pEnd.getY());
   		}
   		else if(this == TRIANGLE || this == BLACK_TRIANGLE)
   		{
   			path.lineTo((float)x2, (float)y2);
   			path.closePath();                  
   		}
   		else if(this == DIAMOND || this == BLACK_DIAMOND)
   		{
   			double x3 = x2 - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
   			double y3 = y2 - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
   			path.lineTo((float)x3, (float)y3);
   			path.lineTo((float)x2, (float)y2);
   			path.closePath();         
   		}      
   		return path;
   	}
}

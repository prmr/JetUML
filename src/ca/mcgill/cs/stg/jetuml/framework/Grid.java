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
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A grid to which points and rectangles can be "snapped". The
 * snapping operation moves a point to the nearest grid point.
 */
public class Grid
{
	private static final Color GRID_COLOR = new Color(220, 220, 220); // Pale grey
	
	private double aGridX;
	private double aGridY;
	
	/**
     *  Constructs a grid with no grid points.
     */
	public Grid()
	{
		setGrid(0, 0);
	}
   
	/**
     *  Sets the grid point distances in x- and y-direction.
     * @param pX the grid point distance in x-direction
     * @param pY the grid point distance in y-direction
     */
	public void setGrid(double pX, double pY)
	{
		aGridX = pX;
		aGridY = pY;
	}
   
	/**
     * Draws this grid inside a rectangle.
     * @param pGraphics2D the graphics context
     * @param pBounds the bounding rectangle
     */
	public void draw(Graphics2D pGraphics2D, Rectangle2D pBounds)
	{
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(GRID_COLOR);
		Stroke oldStroke = pGraphics2D.getStroke();
		for(double x = pBounds.getX(); x < pBounds.getMaxX(); x += aGridX)
		{
			pGraphics2D.draw(new Line2D.Double(x, pBounds.getY(), x, pBounds.getMaxY()));
		}
		for(double y = pBounds.getY(); y < pBounds.getMaxY(); y += aGridY)
		{
			pGraphics2D.draw(new Line2D.Double(pBounds.getX(), y, pBounds.getMaxX(), y));
		}
		pGraphics2D.setStroke(oldStroke);
		pGraphics2D.setColor(oldColor);
	}

	/**
     * Snaps a point to the nearest grid point.
     * @param pPoint the point to snap. After the call, the 
     * coordinates of p are changed so that p falls on the grid.
     */
	public void snap(Point2D pPoint)
	{
		double x;
		if(aGridX == 0)
		{
			x = pPoint.getX();
		}
		else
		{
			x = Math.round(pPoint.getX() / aGridX) * aGridX;
		}
		double y;
		if(aGridY == 0)
		{
			y = pPoint.getY();
		}
		else
		{
			y = Math.round(pPoint.getY() / aGridY) * aGridY;
		}
		pPoint.setLocation(x, y);
   }

	/**
     * Snaps a rectangle to the nearest grid points.
     * @param pRectangle the rectangle to snap. After the call, the 
     * coordinates of r are changed so that all of its corners
     * falls on the grid.
	 */
	public void snap(Rectangle2D pRectangle)
	{
		double x;
		double w;
		if(aGridX == 0)
		{
			x = pRectangle.getX();
			w = pRectangle.getWidth();
		}
		else
		{
			x = Math.round(pRectangle.getX() / aGridX) * aGridX;
			w = Math.ceil(pRectangle.getWidth() / (2 * aGridX)) * (2 * aGridX);
		}
		double y;
		double h;
		if(aGridY == 0)
		{
			y = pRectangle.getY();
			h = pRectangle.getHeight();
		}
		else
		{
			y = Math.round(pRectangle.getY() / aGridY) * aGridY;
			h = Math.ceil(pRectangle.getHeight() / (2 * aGridY)) * (2 * aGridY);
		} 
		pRectangle.setFrame(x, y, w, h);      
	}
}

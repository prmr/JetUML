/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
import java.awt.geom.Rectangle2D;

/**
 * A grid to which points and rectangles can be "snapped". The
 * snapping operation moves a point to the nearest grid point.
 */
public class Grid
{
	private static final Color GRID_COLOR = new Color(220, 220, 220); 
	private static final double GRID_SIZE = 10.0;
	
	/**
     * Draws this grid inside a rectangle.
     * @param pGraphics2D the graphics context
     * @param pBounds the bounding rectangle
     */
	public static void draw(Graphics2D pGraphics2D, Rectangle2D pBounds)
	{
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(GRID_COLOR);
		Stroke oldStroke = pGraphics2D.getStroke();
		for(double x = pBounds.getX(); x < pBounds.getMaxX(); x += GRID_SIZE)
		{
			pGraphics2D.draw(new Line2D.Double(x, pBounds.getY(), x, pBounds.getMaxY()));
		}
		for(double y = pBounds.getY(); y < pBounds.getMaxY(); y += GRID_SIZE)
		{
			pGraphics2D.draw(new Line2D.Double(pBounds.getX(), y, pBounds.getMaxX(), y));
		}
		pGraphics2D.setStroke(oldStroke);
		pGraphics2D.setColor(oldColor);
	}

	/**
     * Snaps a rectangle to the nearest grid points.
     * @param pRectangle the rectangle to snap. After the call, the 
     * coordinates of r are changed so that all of its corners
     * falls on the grid.
	 */
	public void snap(Rectangle2D pRectangle)
	{
		double x = Math.round(pRectangle.getX() / GRID_SIZE) * GRID_SIZE;
		double w = Math.ceil(pRectangle.getWidth() / (2 * GRID_SIZE)) * (2 * GRID_SIZE);
		double y = Math.round(pRectangle.getY() / GRID_SIZE) * GRID_SIZE;
		double h = Math.ceil(pRectangle.getHeight() / (2 * GRID_SIZE)) * (2 * GRID_SIZE);
		pRectangle.setFrame(x, y, w, h);      
	}
}

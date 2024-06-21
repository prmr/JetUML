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

package org.jetuml.rendering;

import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

import javafx.scene.canvas.GraphicsContext;

/**
 * A grid to which points and rectangles can be "snapped". The
 * snapping operation moves a point to the nearest grid point.
 */
public final class Grid
{
	private static final double GRID_SIZE = 10;
	
	private Grid() {}
	
	/**
     * Draws this grid inside a rectangle.
     * @param pGraphics the graphics context
     * @param pBounds the bounding rectangle
     */
	public static void draw(GraphicsContext pGraphics, Rectangle pBounds)
	{
		pGraphics.save();
		pGraphics.setStroke(ColorScheme.getScheme().getGridColor());
		int x1 = pBounds.x();
		int y1 = pBounds.y();
		int x2 = pBounds.maxX();
		int y2 = pBounds.maxY();
		for(int x = x1; x < x2; x += GRID_SIZE)
		{
			ToolGraphics.strokeSharpLine(pGraphics, x, y1, x, y2);
		}
		for(int y = y1; y < y2; y += GRID_SIZE)
		{
			ToolGraphics.strokeSharpLine(pGraphics, x1, y, x2, y);
		}
		pGraphics.restore();
	}

	
	/**
     * Creates a rectangle that is the original rectangle, snapped to
     * the nearest grid point.
     * @param pRectangle the rectangle to snap. 
     * @return A new rectangle with the snapped coordinates.
     * @pre pRectangle != null
	 */
	public static Rectangle snapped(Rectangle pRectangle)
	{
		assert pRectangle != null;
		int x = (int)(GeomUtils.round(pRectangle.x() / GRID_SIZE) * GRID_SIZE);
		int width = (int)(Math.ceil(pRectangle.width() / GRID_SIZE) * GRID_SIZE);
		int y = (int)(GeomUtils.round(pRectangle.y() / GRID_SIZE) * GRID_SIZE);
		int height = (int)(Math.ceil(pRectangle.height() / GRID_SIZE) * GRID_SIZE);
		return new Rectangle(x, y, width, height);
	}
	
	/**
	 * Returns the point on the grid that is closest to pPoint.
	 * 
	 * @param pPoint The original point.
	 * @return The snapped point.
	 * @pre pPoint != null
	 */
	public static Point snapped(Point pPoint)
	{
		assert pPoint != null;
		int x = (int)(GeomUtils.round(pPoint.x() / GRID_SIZE) * GRID_SIZE);
		int y = (int)(GeomUtils.round(pPoint.y() / GRID_SIZE) * GRID_SIZE);
		return new Point(x, y);
	}
	
	/**
	 * @param pPoint The point to snap.
	 * @return A new point that corresponds to pPoint, but with its x-coordinate
	 *     snapped to the grid.
	 * @pre pPoint != null
	 */
	public static Point snappedHorizontally(Point pPoint)
	{
		assert pPoint != null;
		Point snapped = snapped(pPoint);
		return new Point(snapped.x(), pPoint.y());
	}
	
	/**
	 * @param pPoint The point to snap.
	 * @return A new point that corresponds to pPoint, but with its y-coordinate
	 *     snapped to the grid.
	 * @pre pPoint != null
	 */
	public static Point snappedVertically(Point pPoint)
	{
		assert pPoint != null;
		Point snapped = snapped(pPoint);
		return new Point(pPoint.x(), snapped.y());
	}
	
	/**
	 * @param pCoordinate A coordinate to place on the grid.
	 * @return The next int that lies on the grid (i.e., is a multiple of the grid size).
	 * @pre pCoordinate >= 0;
	 */
	public static int toMultiple(int pCoordinate)
	{
		assert pCoordinate >= 0;
		return (int)(Math.ceil(pCoordinate / GRID_SIZE) * GRID_SIZE);
	}
}

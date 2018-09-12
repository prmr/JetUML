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

import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A grid to which points and rectangles can be "snapped". The
 * snapping operation moves a point to the nearest grid point.
 */
public final class Grid
{
	private static final Color GRID_COLOR = Color.rgb(220, 220, 220);
	private static final double GRID_SIZE = 10;
	
	private Grid() {}
	
	/**
     * Draws this grid inside a rectangle.
     * @param pGraphics the graphics context
     * @param pBounds the bounding rectangle
     */
	public static void draw(GraphicsContext pGraphics, Rectangle pBounds)
	{
		Paint oldStroke = pGraphics.getStroke();
		pGraphics.setStroke(GRID_COLOR);
		int x1 = pBounds.getX();
		int y1 = pBounds.getY();
		int x2 = pBounds.getMaxX();
		int y2 = pBounds.getMaxY();
		for(int x = x1; x < x2; x += GRID_SIZE)
		{
			ToolGraphics.strokeSharpLine(pGraphics, x, y1, x, y2);
		}
		for(int y = y1; y < y2; y += GRID_SIZE)
		{
			ToolGraphics.strokeSharpLine(pGraphics, x1, y, x2, y);
		}
		pGraphics.setStroke(oldStroke);
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
		int x = (int)(Math.round(pRectangle.getX() / GRID_SIZE) * GRID_SIZE);
		int width = (int)(Math.ceil(pRectangle.getWidth() / GRID_SIZE) * GRID_SIZE);
		int y = (int)(Math.round(pRectangle.getY() / GRID_SIZE) * GRID_SIZE);
		int height = (int)(Math.ceil(pRectangle.getHeight() / GRID_SIZE) * GRID_SIZE);
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
		int x = (int)(Math.round(pPoint.getX() / GRID_SIZE) * GRID_SIZE);
		int y = (int)(Math.round(pPoint.getY() / GRID_SIZE) * GRID_SIZE);
		return new Point(x, y);
	}
	
	/**
	 * Creates a point that represents a distance to the origin which, when added to 
	 * pDimension, will correspond to a dimension that is rounded to align to the grid.
	 * 
	 * @param pDimension The original dimension.
	 * @return An aligned dimension.
	 * @pre pDimension != null
	 */
	public static Point toSnap(Dimension pDimension)
	{
		assert pDimension != null;
		final int size = (int) GRID_SIZE;
		int xRemainder = pDimension.getWidth() % size;
		if( xRemainder < size/2 )
		{
			xRemainder = -xRemainder;
		}
		else
		{
			xRemainder = size - xRemainder;
		}
		
		int yRemainder = pDimension.getHeight() % size;
		if( yRemainder < size/2 )
		{
			yRemainder = -yRemainder;
		}
		else
		{
			yRemainder = size - yRemainder;
		}
		return new Point(xRemainder, yRemainder);
	}
}

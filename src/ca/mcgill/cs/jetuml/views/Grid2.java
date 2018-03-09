/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A grid to which points and rectangles can be "snapped". The
 * snapping operation moves a point to the nearest grid point.
 */
public final class Grid2
{
	private static final Color GRID_COLOR = Color.rgb(220, 220, 220); 
	private static final double GRID_SIZE = 10;
	
	private Grid2() {}
	
	/**
     * Draws this grid inside a rectangle.
     * @param pGraphics the graphics context
     * @param pBounds the bounding rectangle
     */
	public static void draw(GraphicsContext pGraphics, Rectangle2D pBounds)
	{
		Paint oldFill = pGraphics.getFill();
		pGraphics.setFill(GRID_COLOR);
		for(double x = pBounds.getMinX(); x < pBounds.getMaxX(); x += GRID_SIZE)
		{
			pGraphics.strokeLine(x, pBounds.getMinY(), x, pBounds.getMaxY());
		}
		for(double y = pBounds.getMinY(); y < pBounds.getMaxY(); y += GRID_SIZE)
		{
			pGraphics.strokeLine(pBounds.getMinX(), y, pBounds.getMaxX(), y);
		}
		pGraphics.setFill(oldFill);
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
		int width = (int)(Math.ceil(pRectangle.getWidth() / (2 * GRID_SIZE)) * (2 * GRID_SIZE));
		int y = (int)(Math.round(pRectangle.getY() / GRID_SIZE) * GRID_SIZE);
		int height = (int)(Math.ceil(pRectangle.getHeight() / (2 * GRID_SIZE)) * (2 * GRID_SIZE));
		return new Rectangle(x, y, width, height);
	}
}

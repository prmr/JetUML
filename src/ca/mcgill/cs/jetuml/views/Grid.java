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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A grid to which points and rectangles can be "snapped". The
 * snapping operation moves a point to the nearest grid point.
 * 
 * @author Kaylee I. Kutschera - Migration to JavaFX
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
		for(double x = pBounds.getX(); x < pBounds.getMaxX(); x += GRID_SIZE)
		{
			pGraphics.strokeLine(x, pBounds.getY(), x, pBounds.getMaxY());
		}
		for(double y = pBounds.getY(); y < pBounds.getMaxY(); y += GRID_SIZE)
		{
			pGraphics.strokeLine(pBounds.getX(), y, pBounds.getMaxX(), y);
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
		int width = (int)(Math.ceil(pRectangle.getWidth() / (2 * GRID_SIZE)) * (2 * GRID_SIZE));
		int y = (int)(Math.round(pRectangle.getY() / GRID_SIZE) * GRID_SIZE);
		int height = (int)(Math.ceil(pRectangle.getHeight() / (2 * GRID_SIZE)) * (2 * GRID_SIZE));
		return new Rectangle(x, y, width, height);
	}
}

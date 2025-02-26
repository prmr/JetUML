/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering;

import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

/**
 * A class to draw the grid.
 */
public class GridRenderer
{
	private static final int GRID_SIZE = 10;
	
	private final RenderingContext aContext;
	
	/**
	 * Creates a new grid renderer for a given context.
	 * 
	 * @param pContext The rendering context.
	 */
	public GridRenderer(RenderingContext pContext)
	{
		assert pContext != null;
		aContext = pContext;
	}
	
	/**
     * Draws this grid inside a rectangle.
     * @param pBounds the bounding rectangle
     */
	public void draw(Rectangle pBounds)
	{
		assert pBounds != null;
		
		for (int x = pBounds.x(); x < pBounds.maxX(); x += GRID_SIZE)
		{
			aContext.strokeLine(x, pBounds.y(), x, pBounds.maxY(), ColorScheme.getScheme().getGridColor(), LineStyle.SOLID);
		}
		for (int y = pBounds.y(); y < pBounds.maxY(); y += GRID_SIZE)
		{
			aContext.strokeLine(pBounds.x(), y, pBounds.maxX(), y, ColorScheme.getScheme().getGridColor(), LineStyle.SOLID);
		}
	}
}

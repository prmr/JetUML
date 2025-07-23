/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

import java.util.Optional;

import org.jetuml.geom.Line;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

import javafx.scene.paint.Color;

/**
 * Render visual objects that are not part of the diagram.
 */
public class AccessoriesRenderer
{
	private static final int HANDLE_SIZE = 6; // The length in pixel of one side of the handle.
	private static final int GRID_SIZE = 10;
	private static final Color SELECTION_COLOR = Color.rgb(77, 115, 153);
	private static final Color SELECTION_FILL_COLOR = Color.rgb(173, 193, 214);
	private static final Color SELECTION_FILL_TRANSPARENT = Color.rgb(173, 193, 214, 0.75);
	
	private final RenderingContext aContext;
	
	/**
	 * Creates a new grid renderer for a given context.
	 * 
	 * @param pContext The rendering context.
	 */
	public AccessoriesRenderer(RenderingContext pContext)
	{
		assert pContext != null;
		aContext = pContext;
	}
	
	/**
     * Draws this grid inside a rectangle.
     * @param pBounds the bounding rectangle
     */
	public void drawGrid(Rectangle pBounds)
	{
		assert pBounds != null;
		
		for (int x = pBounds.x(); x < pBounds.maxX(); x += GRID_SIZE)
		{
			aContext.strokeLine(x, pBounds.y(), x, pBounds.maxY(), ColorScheme.get().grid(), LineStyle.SOLID);
		}
		for (int y = pBounds.y(); y < pBounds.maxY(); y += GRID_SIZE)
		{
			aContext.strokeLine(pBounds.x(), y, pBounds.maxX(), y, ColorScheme.get().grid(), LineStyle.SOLID);
		}
	}
	
	/**
	 * Draws a "rubberband" line on pGraphics. A rubberband line is a straight line
	 * in the color of the selection tools.
	 * 
	 * @param pLine The line that represents the rubberband.
	 */
	public void drawRubberband(Line pLine)
	{
		aContext.strokeLine(pLine.x1(), pLine.y1(), pLine.x2(), pLine.y2(), SELECTION_FILL_COLOR, LineStyle.SOLID);
	}
	
	/**
	 * Draws a "lasso" rectangle. A lasso rectangle is a semi-transparent
	 * rectangle in the color of the selection tools.
	 * 
	 * @param pRectangle The rectangle that defines the lasso.
	 */
	public void drawLasso(Rectangle pRectangle)
	{
		aContext.drawRectangle(pRectangle, SELECTION_FILL_TRANSPARENT, SELECTION_COLOR, Optional.empty());
	}
	
	/**
	 * Draws four handles on pGraphics centered at the four corners of 
	 * pBounds.
	 * 
	 * @param pBounds Defines the four points where to draw the handles
	 */
	public void drawHandles(Rectangle pBounds)
	{
		drawHandle(pBounds.x(), pBounds.y());
		drawHandle(pBounds.x(), pBounds.maxY());
		drawHandle(pBounds.maxX(), pBounds.y());
		drawHandle(pBounds.maxX(), pBounds.maxY());
	}
	
	/**
	 * Draws two handles on pGraphics centered at the two ends of 
	 * pBounds.
	 * 
	 * @param pBounds Defines the two points where to draw the handles
	 */
	public void drawHandles(Line pBounds)
	{
		drawHandle(pBounds.x1(), pBounds.y1());
		drawHandle(pBounds.x2(), pBounds.y2());
	}
	
	/*
	 * Draws a handle on pGraphics that is centered at the position
	 * (pX, pY).
	 * 
	 * @param pX The x-coordinate of the center of the handle.
	 * @param pY The y-coordinate of the center of the handle.
	 */
	private void drawHandle(int pX, int pY)
	{
		Rectangle handle = new Rectangle(pX - HANDLE_SIZE / 2, pY - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
		aContext.drawRectangle(handle, SELECTION_FILL_COLOR, SELECTION_COLOR, Optional.empty());
	}
}

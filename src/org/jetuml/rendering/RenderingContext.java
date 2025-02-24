/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
 ******************************************************************************/
package org.jetuml.rendering;

import org.jetuml.geom.Line;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Wrapper for the canvas to serve as a target for all
 * drawing operations.
 */
public class RenderingContext
{
	private static final int HANDLE_SIZE = 6; // The length in pixel of one side of the handle.
	private static final Color SELECTION_COLOR = Color.rgb(77, 115, 153);
	private static final Color SELECTION_FILL_COLOR = Color.rgb(173, 193, 214);
	private static final Color SELECTION_FILL_TRANSPARENT = Color.rgb(173, 193, 214, 0.75);
	
	private final GraphicsContext aContext;
	
	/**
	 * Creates a rendering context that draws on the provided
	 * graphics context.
	 * 
	 * @param pContext The graphics context on which to draw.
	 */
	public RenderingContext(GraphicsContext pContext)
	{
		aContext = pContext;
	}
	
	/**
	 * @return The graphics context on which to draw.
	 */
	public GraphicsContext context()
	{
		return aContext;
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
		aContext.save();
		aContext.setStroke(SELECTION_COLOR);
		aContext.strokeRect((int)(pX - HANDLE_SIZE / 2.0) + 0.5, (int)(pY - HANDLE_SIZE / 2.0)+ 0.5, HANDLE_SIZE, HANDLE_SIZE);
		aContext.setFill(SELECTION_FILL_COLOR);
		aContext.fillRect((int)(pX - HANDLE_SIZE / 2.0)+0.5, (int)(pY - HANDLE_SIZE / 2.0)+0.5, HANDLE_SIZE, HANDLE_SIZE);
		aContext.restore();
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
	
	/**
	 * Draws a "rubberband" line on pGraphics. A rubberband line is a straight line
	 * in the color of the selection tools.
	 * 
	 * @param pLine The line that represents the rubberband.
	 */
	public void drawRubberband(Line pLine)
	{
		aContext.save();
		aContext.setStroke(SELECTION_FILL_COLOR);
		ToolGraphics.strokeSharpLine(aContext, pLine.x1(), pLine.y1(), pLine.x2(), pLine.y2());
		aContext.restore();
	}
	
	/**
	 * Draws a "lasso" rectangle on pGraphics. A lasso rectangle is a semi-transparent
	 * rectangle in the color of the selection tools.
	 * 
	 * @param pRectangle The rectangle that defines the lasso.
	 */
	public void drawLasso(Rectangle pRectangle)
	{
		RenderingUtils.drawRectangle(aContext, SELECTION_COLOR, SELECTION_FILL_TRANSPARENT, 
				pRectangle.x(), pRectangle.y(), pRectangle.width(), pRectangle.height());
	}
}

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
import org.jetuml.gui.ColorScheme;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

/**
 * Wrapper for the canvas to serve as a target for all
 * drawing operations.
 */
public class RenderingContext
{
	private static final int HANDLE_SIZE = 6; // The length in pixel of one side of the handle.
	private static final double LINE_WIDTH = 0.6;
	private static final Color SELECTION_COLOR = Color.rgb(77, 115, 153);
	private static final Color SELECTION_FILL_COLOR = Color.rgb(173, 193, 214);
	private static final Color SELECTION_FILL_TRANSPARENT = Color.rgb(173, 193, 214, 0.75);
	private static final int GRID_SIZE = 10;
	
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
		strokeSharpLine(pLine.x1(), pLine.y1(), pLine.x2(), pLine.y2());
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
	
	/**
     * Draws this grid inside a rectangle.
     * @param pBounds the bounding rectangle
     */
	public void drawGrid(Rectangle pBounds)
	{
		aContext.save();
		aContext.setStroke(ColorScheme.getScheme().getGridColor());
		int x1 = pBounds.x();
		int y1 = pBounds.y();
		int x2 = pBounds.maxX();
		int y2 = pBounds.maxY();
		for(int x = x1; x < x2; x += GRID_SIZE)
		{
			strokeSharpLine(x, y1, x, y2);
		}
		for(int y = y1; y < y2; y += GRID_SIZE)
		{
			strokeSharpLine(x1, y, x2, y);
		}
		aContext.restore();
	}
	
	/**
	 * Strokes a line, originally in integer coordinates, so that it aligns precisely
	 * with the JavaFX coordinate system, which is 0.5 away from the pixel. See
	 * the documentation for javafx.scene.shape.Shape for details.
	 * 
	 * @param pX1 The x-coordinate of the first point.
	 * @param pY1 The y-coordinate of the first point.
	 * @param pX2 The x-coordinate of the second point.
	 * @param pY2 The y-coordinate of the second point.
	 */
	public void strokeSharpLine(int pX1, int pY1, int pX2, int pY2)
	{
		aContext.strokeLine(pX1 + 0.5, pY1 + 0.5, pX2 + 0.5, pY2 + 0.5);
	}
	
	/**
	 * Strokes a path, by converting the elements to integer coordinates and then
	 * aligning them to the center of the pixels, so that it aligns precisely
	 * with the JavaFX coordinate system. See the documentation for 
	 * javafx.scene.shape.Shape for details.
	 * 
	 * @param pPath The path to stroke
	 * @param pStyle The line style for the path.
	 */
	public void strokeSharpPath(Path pPath, LineStyle pStyle)
	{
		aContext.save();
		aContext.setStroke(ColorScheme.getScheme().getStrokeColor());
		aContext.setLineDashes(pStyle.getLineDashes());
		aContext.setLineWidth(LINE_WIDTH);
		applyPath(pPath);
		aContext.stroke();
		aContext.restore();
	}
	
	private void applyPath(Path pPath)
	{
		aContext.beginPath();
		for(PathElement element : pPath.getElements())
		{
			if(element instanceof MoveTo moveTo)
			{
				aContext.moveTo(((int)moveTo.getX()) + 0.5, ((int)moveTo.getY()) + 0.5);
			}
			else if(element instanceof LineTo lineTo)
			{
				aContext.lineTo(((int)lineTo.getX()) + 0.5, ((int)lineTo.getY()) + 0.5);
			}
			else if(element instanceof QuadCurveTo curve)
			{
				aContext.quadraticCurveTo(((int)curve.getControlX())+0.5, ((int)curve.getControlY()) + 0.5, 
						((int) curve.getX()) + 0.5, ((int) curve.getY()) + 0.5);
			}
		}
	}
	/**
	 * Strokes and fills a path, by converting the elements to integer coordinates and then
	 * aligning them to the center of the pixels, so that it aligns precisely
	 * with the JavaFX coordinate system. See the documentation for 
	 * javafx.scene.shape.Shape for details.
	 * 
	 * @param pPath The path to stroke
	 * @param pFill The fill color for the path.
	 * @param pShadow True to include a drop shadow.
	 */
	public void strokeAndFillSharpPath(Path pPath, Paint pFill, boolean pShadow)
	{
		aContext.save();
		aContext.setStroke(ColorScheme.getScheme().getStrokeColor());
		aContext.setLineWidth(LINE_WIDTH);
		aContext.setFill(pFill);
		applyPath(pPath);
		
		if( pShadow )
		{
			aContext.setEffect(ColorScheme.getScheme().getDropShadow());
		}
		aContext.fill();
		aContext.stroke();
		aContext.restore();
	}
}

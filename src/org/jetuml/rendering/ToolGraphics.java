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
 * A utility class to draw various graphics for diagram handling tools.
 */
public final class ToolGraphics
{
	private static final int HANDLE_SIZE = 6; // The length in pixel of one side of the handle.
	private static final Color SELECTION_COLOR = Color.rgb(77, 115, 153);
	private static final Color SELECTION_FILL_COLOR = Color.rgb(173, 193, 214);
	private static final Color SELECTION_FILL_TRANSPARENT = Color.rgb(173, 193, 214, 0.75);
	private static final double LINE_WIDTH = 0.6;
	
	private ToolGraphics() {}
	
	/**
	 * Draws a handle on pGraphics that is centered at the position
	 * (pX, pY).
	 * 
	 * @param pGraphics The graphics context on which to draw the handle.
	 * @param pX The x-coordinate of the center of the handle.
	 * @param pY The y-coordinate of the center of the handle.
	 */
	private static void drawHandle(GraphicsContext pGraphics, int pX, int pY)
	{
		pGraphics.save();
		pGraphics.setStroke(SELECTION_COLOR);
		pGraphics.strokeRect((int)(pX - HANDLE_SIZE / 2.0) + 0.5, (int)(pY - HANDLE_SIZE / 2.0)+ 0.5, HANDLE_SIZE, HANDLE_SIZE);
		pGraphics.setFill(SELECTION_FILL_COLOR);
		pGraphics.fillRect((int)(pX - HANDLE_SIZE / 2.0)+0.5, (int)(pY - HANDLE_SIZE / 2.0)+0.5, HANDLE_SIZE, HANDLE_SIZE);
		pGraphics.restore();
	}
	
	/**
	 * Draws four handles on pGraphics centered at the four corners of 
	 * pBounds.
	 * 
	 * @param pGraphics The graphics context on which to draw the handles.
	 * @param pBounds Defines the four points where to draw the handles
	 */
	public static void drawHandles(GraphicsContext pGraphics, Rectangle pBounds)
	{
		drawHandle(pGraphics, pBounds.x(), pBounds.y());
		drawHandle(pGraphics, pBounds.x(), pBounds.maxY());
		drawHandle(pGraphics, pBounds.maxX(), pBounds.y());
		drawHandle(pGraphics, pBounds.maxX(), pBounds.maxY());
	}
	
	/**
	 * Draws two handles on pGraphics centered at the two ends of 
	 * pBounds.
	 * 
	 * @param pGraphics The graphics context on which to draw the handles.
	 * @param pBounds Defines the two points where to draw the handles
	 */
	public static void drawHandles(GraphicsContext pGraphics, Line pBounds)
	{
		drawHandle(pGraphics, pBounds.x1(), pBounds.y1());
		drawHandle(pGraphics, pBounds.x2(), pBounds.y2());
	}
	
	/**
	 * Draws a "rubberband" line on pGraphics. A rubberband line is a straight line
	 * in the color of the selection tools.
	 * 
	 * @param pGraphics The graphics context on which to draw the line.
	 * @param pLine The line that represents the rubberband.
	 */
	public static void drawRubberband(GraphicsContext pGraphics, Line pLine)
	{
		pGraphics.save();
		pGraphics.setStroke(SELECTION_FILL_COLOR);
		strokeSharpLine(pGraphics, pLine.x1(), pLine.y1(), pLine.x2(), pLine.y2());
		pGraphics.restore();
	}
	
	/**
	 * Draws a "lasso" rectangle on pGraphics. A lasso rectangle is a semi-transparent
	 * rectangle in the color of the selection tools.
	 * 
	 * @param pGraphics The graphics context on which to draw the lasso.
	 * @param pRectangle The rectangle that defines the lasso.
	 */
	public static void drawLasso(GraphicsContext pGraphics, Rectangle pRectangle)
	{
		RenderingUtils.drawRectangle(pGraphics, SELECTION_COLOR, SELECTION_FILL_TRANSPARENT, 
				pRectangle.x(), pRectangle.y(), pRectangle.width(), pRectangle.height());
	}
	
	/**
	 * Strokes a line, originally in integer coordinates, so that it aligns precisely
	 * with the JavaFX coordinate system, which is 0.5 away from the pixel. See
	 * the documentation for javafx.scene.shape.Shape for details.
	 * 
	 * @param pGraphics The graphics context on which to draw the line.
	 * @param pX1 The x-coordinate of the first point.
	 * @param pY1 The y-coordinate of the first point.
	 * @param pX2 The x-coordinate of the second point.
	 * @param pY2 The y-coordinate of the second point.
	 */
	public static void strokeSharpLine(GraphicsContext pGraphics, int pX1, int pY1, int pX2, int pY2)
	{
		pGraphics.strokeLine(pX1 + 0.5, pY1 + 0.5, pX2 + 0.5, pY2 + 0.5);
	}
	
	/**
	 * Strokes a path, by converting the elements to integer coordinates and then
	 * aligning them to the center of the pixels, so that it aligns precisely
	 * with the JavaFX coordinate system. See the documentation for 
	 * javafx.scene.shape.Shape for details.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pPath The path to stroke
	 * @param pStyle The line style for the path.
	 */
	public static void strokeSharpPath(GraphicsContext pGraphics, Path pPath, LineStyle pStyle)
	{
		pGraphics.save();
		pGraphics.setStroke(ColorScheme.getScheme().getStrokeColor());
		pGraphics.setLineDashes(pStyle.getLineDashes());
		pGraphics.setLineWidth(LINE_WIDTH);
		applyPath(pGraphics, pPath);
		pGraphics.stroke();
		pGraphics.restore();
	}
	
	private static void applyPath(GraphicsContext pGraphics, Path pPath)
	{
		pGraphics.beginPath();
		for(PathElement element : pPath.getElements())
		{
			if(element instanceof MoveTo moveTo)
			{
				pGraphics.moveTo(((int)moveTo.getX()) + 0.5, ((int)moveTo.getY()) + 0.5);
			}
			else if(element instanceof LineTo lineTo)
			{
				pGraphics.lineTo(((int)lineTo.getX()) + 0.5, ((int)lineTo.getY()) + 0.5);
			}
			else if(element instanceof QuadCurveTo curve)
			{
				pGraphics.quadraticCurveTo(((int)curve.getControlX())+0.5, ((int)curve.getControlY()) + 0.5, 
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
	 * @param pGraphics The graphics context.
	 * @param pPath The path to stroke
	 * @param pFill The fill color for the path.
	 * @param pShadow True to include a drop shadow.
	 */
	public static void strokeAndFillSharpPath(GraphicsContext pGraphics, Path pPath, Paint pFill, boolean pShadow)
	{
		pGraphics.save();
		pGraphics.setStroke(ColorScheme.getScheme().getStrokeColor());
		pGraphics.setLineWidth(LINE_WIDTH);
		pGraphics.setFill(pFill);
		applyPath(pGraphics, pPath);
		
		if( pShadow )
		{
			pGraphics.setEffect(ColorScheme.getScheme().getDropShadow());
		}
		pGraphics.fill();
		pGraphics.stroke();
		pGraphics.restore();
	}
}

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

import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
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
	private static final DropShadow DROP_SHADOW = new DropShadow(3, 3, 3, Color.LIGHTGRAY);
	
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
		Paint oldStroke = pGraphics.getStroke();
		Paint oldFill = pGraphics.getFill();
		pGraphics.setStroke(SELECTION_COLOR);
		pGraphics.strokeRect((int)(pX - HANDLE_SIZE / 2.0) + 0.5, (int)(pY - HANDLE_SIZE / 2.0)+ 0.5, HANDLE_SIZE, HANDLE_SIZE);
		pGraphics.setFill(SELECTION_FILL_COLOR);
		pGraphics.fillRect((int)(pX - HANDLE_SIZE / 2.0)+0.5, (int)(pY - HANDLE_SIZE / 2.0)+0.5, HANDLE_SIZE, HANDLE_SIZE);
		pGraphics.setStroke(oldStroke);
		pGraphics.setFill(oldFill);
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
		drawHandle(pGraphics, pBounds.getX(), pBounds.getY());
		drawHandle(pGraphics, pBounds.getX(), pBounds.getMaxY());
		drawHandle(pGraphics, pBounds.getMaxX(), pBounds.getY());
		drawHandle(pGraphics, pBounds.getMaxX(), pBounds.getMaxY());
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
		drawHandle(pGraphics, pBounds.getX1(), pBounds.getY1());
		drawHandle(pGraphics, pBounds.getX2(), pBounds.getY2());
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
		Paint oldStroke = pGraphics.getStroke();
		pGraphics.setStroke(SELECTION_FILL_COLOR);
		strokeSharpLine(pGraphics, pLine.getX1(), pLine.getY1(), pLine.getX2(), pLine.getY2());
		pGraphics.setStroke(oldStroke);
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
		ViewUtils.drawRectangle(pGraphics, SELECTION_COLOR, SELECTION_FILL_TRANSPARENT, 
				pRectangle.getX(), pRectangle.getY(), pRectangle.getWidth(), pRectangle.getHeight());
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
		double[] oldDash = pGraphics.getLineDashes();
		pGraphics.setLineDashes(pStyle.getLineDashes());
		double width = pGraphics.getLineWidth();
		pGraphics.setLineWidth(LINE_WIDTH);
		applyPath(pGraphics, pPath);
		pGraphics.stroke();
		pGraphics.setLineDashes(oldDash);
		pGraphics.setLineWidth(width);
	}
	
	private static void applyPath(GraphicsContext pGraphics, Path pPath)
	{
		pGraphics.beginPath();
		for(PathElement element : pPath.getElements())
		{
			if(element instanceof MoveTo)
			{
				pGraphics.moveTo(((int)((MoveTo) element).getX()) + 0.5, ((int)((MoveTo) element).getY()) + 0.5);
			}
			else if(element instanceof LineTo)
			{
				pGraphics.lineTo(((int)((LineTo) element).getX()) + 0.5, ((int)((LineTo) element).getY()) + 0.5);
			}
			else if (element instanceof QuadCurveTo)
			{
				QuadCurveTo curve = (QuadCurveTo) element;
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
		double width = pGraphics.getLineWidth();
		Paint fill = pGraphics.getFill();
		pGraphics.setLineWidth(LINE_WIDTH);
		pGraphics.setFill(pFill);
		applyPath(pGraphics, pPath);
		
		if( pShadow )
		{
			pGraphics.setEffect(DROP_SHADOW);
		}
		pGraphics.fill();
		pGraphics.stroke();
		pGraphics.setLineWidth(width);
		pGraphics.setFill(fill);
		pGraphics.setEffect(null);
	}
}

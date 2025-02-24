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

import org.jetuml.gui.ColorScheme;

import javafx.scene.canvas.GraphicsContext;
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
	private static final double LINE_WIDTH = 0.6;
	
	private ToolGraphics() {}
	
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

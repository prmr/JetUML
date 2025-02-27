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

import java.util.Optional;

import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Wrapper for the canvas to serve as a target for all drawing operations. This
 * class contains rendering operations of three types: stroking a line, filling
 * a space, or drawing a figure, which is equivalent to filling and stroking.
 * All the operations, originally in integer coordinates, are corrected by
 * moving the coordinates by 0.5 in each direction so that they align precisely
 * with the JavaFX coordinate system, which is 0.5 away from the pixel. See the
 * documentation for javafx.scene.shape.Shape for details.
 * 
 * The signature of the various methods takes coordinates instead of geometric elements
 * (e.g., lines) for performance reasons: to avoid creating an object for every call
 * to a rendering primitive.
 */
public class RenderingContext
{
	private static final double LINE_WIDTH = 0.5;
	private static final int ARC_SIZE = 20;
	
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
		aContext.setLineWidth(LINE_WIDTH);
	}
	
	/**
	 * Stroke a line with a specified color and line style.
	 * 
	 * @param pX1 The x-coordinate of the first point.
	 * @param pY1 The y-coordinate of the first point.
	 * @param pX2 The x-coordinate of the second point.
	 * @param pY2 The y-coordinate of the second point.
	 * @param pColor The color for the line.
	 * @param pStyle The line style for the path.
	 */
	public void strokeLine(int pX1, int pY1, int pX2, int pY2, Color pColor, LineStyle pStyle)
	{
		aContext.save();
		aContext.setStroke(pColor);
		aContext.setLineDashes(pStyle.getLineDashes());
		aContext.translate(0.5, 0.5);
		aContext.strokeLine(pX1, pY1, pX2, pY2);
		aContext.restore();
	}
	
	/**
	 * Draws a rectangle with default attributes.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	public void drawRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow)
	{
		assert pRectangle != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setFill(pFillColor);
		aContext.translate(0.5, 0.5);
		pDropShadow.ifPresent(shadow -> aContext.setEffect(shadow));
		aContext.fillRect(pRectangle.x(), pRectangle.y(), pRectangle.width(), pRectangle.height());
		pDropShadow.ifPresent(shadow -> aContext.setEffect(null));
		aContext.strokeRect(pRectangle.x(), pRectangle.y(), pRectangle.width(), pRectangle.height());
		aContext.restore();
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
	
	/**
	 * Draws a circle with default attributes.
	 * 
	 * @param pX The x-coordinate of the top-left of the circle.
	 * @param pY The y-coordinate of the top-left of the circle.
	 * @param pFill The color with which to fill the circle.
	 * @param pDiameter The diameter of the circle.
	 * @param pShadow True to include a drop shadow.
	 */
	public void drawCircle(int pX, int pY, int pDiameter, Paint pFill, boolean pShadow)
	{
		drawOval(pX, pY, pDiameter, pDiameter, pFill, pShadow);
	}
	
	/**
	 * Draws a circle with default attributes, without a drop shadow.
	 * 
	 * @param pX The x-coordinate of the top-left of the circle.
	 * @param pY The y-coordinate of the top-left of the circle.
	 * @param pFill The color with which to fill the circle.
	 * @param pWidth The width of the oval to draw
	 * @param pHeight The height of the oval to draw.
	 * @param pShadow True to include a drop shadow.
	 */
	public void drawOval(int pX, int pY, int pWidth, int pHeight, Paint pFill, boolean pShadow)
	{
		assert pWidth > 0 && pHeight > 0 && pFill != null;
		aContext.save();
		aContext.setFill(pFill);
		aContext.setStroke(ColorScheme.getScheme().getStrokeColor());
		if( pShadow )
		{
			aContext.setEffect(ColorScheme.getScheme().getDropShadow());
		}
		aContext.fillOval(pX + 0.5, pY + 0.5, pWidth, pHeight);
		aContext.strokeOval(pX + 0.5, pY + 0.5, pWidth, pHeight);
		aContext.restore();
	}
	
	/**
	 * Draws a white rounded rectangle with a drop shadow.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	public void drawRoundedRectangle(Rectangle pRectangle)
	{
		assert pRectangle != null;
		aContext.save();
		applyShapeProperties();
		aContext.fillRoundRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, 
				pRectangle.width(), pRectangle.height(), ARC_SIZE, ARC_SIZE );
		aContext.setEffect(null);
		aContext.strokeRoundRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, 
				pRectangle.width(), pRectangle.height(), ARC_SIZE, ARC_SIZE);
		aContext.restore();
	}
	
	/**
	 * Apply the fill, stroke and effect property of the graphics context.
	 * 
	 * @param pGraphics The graphics context.
	 */
	private void applyShapeProperties()
	{
		aContext.setFill(ColorScheme.getScheme().getFillColor());
		aContext.setStroke(ColorScheme.getScheme().getStrokeColor());
		aContext.setEffect(ColorScheme.getScheme().getDropShadow());
	}
	
	/**
	 * Draws a rectangle with default attributes.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	public void drawRectangle(Rectangle pRectangle)
	{
		assert pRectangle != null;
		aContext.save();
		applyShapeProperties();
		aContext.fillRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
		aContext.setEffect(null);
		aContext.strokeRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
		aContext.restore();
	}
	
	/**
	 * Fills a rectangle without stroking it.
	 * 
	 * @param pRectangle The rectangle to fill.
	 */
	public void fillRectangle(Rectangle pRectangle)
	{
		assert pRectangle != null;
		aContext.save();
		applyShapeProperties();
		aContext.fillRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
		aContext.restore();
	}
	
	/**
	 * Draws a line with default attributes and a specified line style.
	 * 
	 * @param pX1 The x-coordinate of the first point
	 * @param pY1 The y-coordinate of the first point
	 * @param pX2 The x-coordinate of the second point
	 * @param pY2 The y-coordinate of the second point
	 * @param pStyle The line style for the path.
	 */
	public void drawLine(int pX1, int pY1, int pX2, int pY2, LineStyle pStyle)
	{
		aContext.save();
		aContext.setStroke(ColorScheme.getScheme().getStrokeColor());
		aContext.setLineDashes(pStyle.getLineDashes());
		aContext.strokeLine(pX1 + 0.5, pY1 + 0.5, pX2 + 0.5, pY2 + 0.5);
		aContext.restore();
	}
	
	/**
	 * Draw pText.
	 * 
	 * @param pText The text to draw
	 * @param pBoundingX The left coordinate of the top-left point for the text.
	 * @param pBoundingY The top coordinate of the top-left point for the text.
	 * @param pRelativeX The x-coordinate where to draw the text, relative to the bounding box.
	 * @param pRelativeY The y-coordinate where to draw the text, relative to the bounding box.
	 * @param pAlignment The alignment.
	 * @param pBaseline The baseline.
	 * @param pFont The font to use.
	 */
	public void drawText(String pText, int pBoundingX, int pBoundingY, int pRelativeX, int pRelativeY,
			TextAlignment pAlignment, VPos pBaseline, Font pFont)
	{
		aContext.save();
		aContext.setTextAlign(pAlignment);
		aContext.setTextBaseline(pBaseline);
		aContext.translate(pBoundingX, pBoundingY);
		aContext.setFont(pFont);
		aContext.setFill(ColorScheme.getScheme().getStrokeColor());
		aContext.fillText(pText, pRelativeX + 0.5, pRelativeY + 0.5);
		aContext.restore();
	}
	
	/**
	 * Draws an arc.
	 * 
	 * @param pArc The arc to draw.
	 * @pre pArc != null.
	 */
	public void drawArc(Arc pArc)
	{
		assert pArc != null;
		aContext.save();
		aContext.setStroke(ColorScheme.getScheme().getStrokeColor());
		aContext.strokeArc(pArc.getCenterX(), pArc.getCenterY(), pArc.getRadiusX(), pArc.getRadiusY(), pArc.getStartAngle(), 
				pArc.getLength(), pArc.getType());
		aContext.restore();
	}
}

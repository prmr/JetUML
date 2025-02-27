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

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Wrapper for a graphics context to serve as a target for all drawing operations. 
 * 
 * All the operations, originally in integer coordinates, are corrected by
 * moving the coordinates by 0.5 in each direction so that they align precisely
 * with the JavaFX coordinate system, which is 0.5 away from the pixel. See the
 * documentation for javafx.scene.shape.Shape for details.
 * 
 * Only a single RenderingContext should ever be associated with any GraphicsContext.
 */
public class GraphicsRenderingContext implements RenderingContext
{
	private static final double LINE_WIDTH = 0.5;
	private static final int ROUNDED_RECTANGLE_ARC = 20;
	
	private final GraphicsContext aContext;
	
	/**
	 * Creates a rendering context that draws on the provided
	 * graphics context.
	 * 
	 * @param pContext The graphics context on which to draw.
	 */
	public GraphicsRenderingContext(GraphicsContext pContext)
	{
		aContext = pContext;
		aContext.setLineWidth(LINE_WIDTH);
		// This tranlation is necessary to align pixels in integer coodinates 
		// To the JavaFX coordinate system.
		aContext.translate(0.5, 0.5);
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
	@Override
	public void strokeLine(int pX1, int pY1, int pX2, int pY2, Color pColor, LineStyle pStyle)
	{
		assert pColor != null && pStyle != null;
		aContext.save();
		aContext.setStroke(pColor);
		aContext.setLineDashes(pStyle.getLineDashes());
		aContext.strokeLine(pX1, pY1, pX2, pY2);
		aContext.restore();
	}
	
	/**
	 * Draws a rectangle with default attributes.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	@Override
	public void drawRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow)
	{
		assert pRectangle != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setFill(pFillColor);
		pDropShadow.ifPresent(shadow -> aContext.setEffect(shadow));
		aContext.fillRect(pRectangle.x(), pRectangle.y(), pRectangle.width(), pRectangle.height());
		pDropShadow.ifPresent(shadow -> aContext.setEffect(null));
		aContext.strokeRect(pRectangle.x(), pRectangle.y(), pRectangle.width(), pRectangle.height());
		aContext.restore();
	}
	
	/**
	 * Draws an oval.
	 * 
	 * @param pX The x-coordinate of the top-left of the oval.
	 * @param pY The y-coordinate of the top-left of the oval.
	 * @param pFillColor The color with which to fill the oval.
	 * @param pWidth The width of the oval to draw
	 * @param pHeight The height of the oval to draw.
	 * @param pShadow The drop shadow, if there is one.
	 */
	@Override
	public void drawOval(int pX, int pY, int pWidth, int pHeight, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pShadow)
	{
		assert pWidth > 0 && pHeight > 0 && pFillColor != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setFill(pFillColor);
		pShadow.ifPresent(shadow -> aContext.setEffect(shadow));
		aContext.fillOval(pX, pY, pWidth, pHeight);
		aContext.strokeOval(pX, pY, pWidth, pHeight);
		pShadow.ifPresent(shadow -> aContext.setEffect(null));
		aContext.restore();
	}
	
	/**
	 * Strokes an arc.
	 * 
	 * @param pArc The arc to stroke.
	 * @param pStrokeColor The color of the stroke
	 * @pre pArc != null && pStrokeColor != null
	 */
	@Override
	public void strokeArc(Arc pArc, Color pStrokeColor)
	{
		assert pArc != null && pStrokeColor != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.strokeArc(pArc.getCenterX(), pArc.getCenterY(), pArc.getRadiusX(), pArc.getRadiusY(), pArc.getStartAngle(), 
				pArc.getLength(), pArc.getType());
		aContext.restore();
	}
	
	/**
	 * Strokes a path.
	 * 
	 * @param pPath The path to stroke
	 * @param pStrokeColor The color of the path.
	 * @param pStyle The line style for the path.
	 */
	@Override
	public void strokePath(Path pPath, Color pStrokeColor, LineStyle pStyle)
	{
		assert pPath != null && pStrokeColor != null && pStyle != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setLineDashes(pStyle.getLineDashes());
		strokePath(pPath);
		aContext.restore();
	}
	
	/**
	 * Strokes and fills a path assumed to be closed.
	 * 
	 * @param pPath The path to stroke
	 * @param pFillColor The fill color for the path.
	 * @param pStrokeColor The stroke color.
	 * @param pShadow The drop shadow
	 */
	@Override
	public void drawClosedPath(Path pPath, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow)
	{
		assert pPath != null && pFillColor != null && pStrokeColor != null && pDropShadow != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setFill(pFillColor);
		strokePath(pPath);
		pDropShadow.ifPresent(shadow -> aContext.setEffect(pDropShadow.get()));
		aContext.fill();
		pDropShadow.ifPresent(shadow -> aContext.setEffect(null));
		aContext.restore();
	}
	
	private void strokePath(Path pPath)
	{
		aContext.beginPath();
		for(PathElement element : pPath.getElements())
		{
			if (element instanceof MoveTo moveTo)
			{
				aContext.moveTo(moveTo.getX(), moveTo.getY());
			}
			else if (element instanceof LineTo lineTo)
			{
				aContext.lineTo(lineTo.getX(), lineTo.getY());
			}
			else if (element instanceof QuadCurveTo curve)
			{
				aContext.quadraticCurveTo(curve.getControlX(), curve.getControlY(), 
						curve.getX(), curve.getY());
			}
		}
		aContext.stroke();
	}
	
	/**
	 * Draws a rounded rectangle.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	@Override
	public void drawRoundedRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow)
	{
		assert pRectangle != null && pFillColor != null && pStrokeColor != null && pDropShadow != null;
		aContext.save();
		aContext.setFill(pFillColor);
		aContext.setStroke(pStrokeColor);
		
		pDropShadow.ifPresent(shadow -> aContext.setEffect(shadow));
		aContext.fillRoundRect(pRectangle.x(), pRectangle.y(), 
				pRectangle.width(), pRectangle.height(), ROUNDED_RECTANGLE_ARC, ROUNDED_RECTANGLE_ARC);
		pDropShadow.ifPresent(shadow -> aContext.setEffect(null));
		aContext.strokeRoundRect(pRectangle.x(), pRectangle.y(), 
				pRectangle.width(), pRectangle.height(), ROUNDED_RECTANGLE_ARC, ROUNDED_RECTANGLE_ARC);
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
	 * @param pTextColor The color of the text.
	 * @param pFont The font to use.
	 */
	@Override
	public void drawText(String pText, int pBoundingX, int pBoundingY, int pRelativeX, int pRelativeY,
			TextAlignment pAlignment, VPos pBaseline, Color pTextColor, Font pFont)
	{
		assert pText != null && pAlignment != null && pBaseline != null;
		assert pTextColor != null && pFont != null;
		aContext.save();
		aContext.setTextAlign(pAlignment);
		aContext.setTextBaseline(pBaseline);
		aContext.translate(pBoundingX, pBoundingY);
		aContext.setFont(pFont);
		aContext.setFill(pTextColor);
		aContext.fillText(pText, pRelativeX, pRelativeY);
		aContext.restore();
	}
}

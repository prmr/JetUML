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

import org.jetuml.geom.Alignment;
import org.jetuml.geom.Rectangle;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
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
	private static final int FULL_CIRCLE = 360;
	private static final double LINE_WIDTH = 0.6;
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
	
	@Override
	public void strokeArc(int pCenterX, int pCenterY, int pRadius, int pStartAngle, int pLength, Color pStrokeColor)
	{
		assert pCenterX >=0 && pCenterY >= 0 && pRadius > 0;
		assert pStartAngle >= 0 && pStartAngle < FULL_CIRCLE;
		assert pLength > 0 && pLength <= FULL_CIRCLE;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.strokeArc(pCenterX - pRadius, pCenterY - pRadius, pRadius * 2, pRadius * 2, pStartAngle, 
				pLength, ArcType.OPEN);
		aContext.restore();
	}
	
	@Override
	public void strokePath(Path pPath, Color pStrokeColor, LineStyle pStyle)
	{
		assert pPath != null && pStrokeColor != null && pStyle != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setLineDashes(pStyle.getLineDashes());
		strokePath(pPath, false);
		aContext.restore();
	}
	
	@Override
	public void drawClosedPath(Path pPath, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow)
	{
		assert pPath != null && pFillColor != null && pStrokeColor != null && pDropShadow != null;
		aContext.save();
		aContext.setStroke(pStrokeColor);
		aContext.setFill(pFillColor);
		strokePath(pPath, true);
		pDropShadow.ifPresent(shadow -> aContext.setEffect(pDropShadow.get()));
		pDropShadow.ifPresent(shadow -> aContext.setEffect(null));
		aContext.restore();
	}
	
	private void strokePath(Path pPath, boolean pFill)
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
		if (pFill)
		{
			aContext.fill();
		}
		aContext.stroke();
	}
	
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
	
	@Override
	public void drawText(String pText, Rectangle pBounds, Alignment pTextPosition,
			Color pTextColor, Font pFont)
	{
		assert pText != null && pTextPosition != null;
		assert pTextColor != null && pFont != null;
		aContext.save();
		aContext.setTextAlign(getTextAlignment(pTextPosition));
		aContext.setTextBaseline(VPos.TOP);
		aContext.setFont(pFont);
		aContext.setFill(pTextColor);
		int anchorX = pBounds.x();
		int anchorY = pBounds.y();
		if (pTextPosition == Alignment.CENTER)
		{
			anchorX = pBounds.center().x();
		}
		aContext.fillText(pText, anchorX, anchorY);
		aContext.restore();
	}
	
	private static TextAlignment getTextAlignment(Alignment pTextPosition)
	{
		if (pTextPosition == Alignment.LEFT)
		{
			return TextAlignment.LEFT;
		}
		else
		{
			return TextAlignment.CENTER;
		}
	}
}

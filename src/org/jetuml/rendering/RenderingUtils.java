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

import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * A collection of utility methods to draw shapes on the canvas.
 * 
 * The methods in this class assume that the default canvas line width is 0.6, 
 * the stroke color black, and the fill color white. Any changes to these 
 * attributes should be reverted.
 * 
 * In the method names, "draw" refers to stroke and fill.
 */
public final class RenderingUtils
{
	private static final int ARC_SIZE = 20;
	
	private RenderingUtils()
	{}
	
	/**
	 * Draws a circle with default attributes, without a drop shadow.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX The x-coordinate of the top-left of the circle.
	 * @param pY The y-coordinate of the top-left of the circle.
	 * @param pFill The color with which to fill the circle.
	 * @param pDiameter The diameter of the circle.
	 * @param pShadow True to include a drop shadow.
	 */
	public static void drawCircle(GraphicsContext pGraphics, int pX, int pY, int pDiameter, Paint pFill, boolean pShadow)
	{
		drawOval( pGraphics, pX, pY, pDiameter, pDiameter, pFill, pShadow);
	}
	
	/**
	 * Draws a circle with default attributes, without a drop shadow.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX The x-coordinate of the top-left of the circle.
	 * @param pY The y-coordinate of the top-left of the circle.
	 * @param pFill The color with which to fill the circle.
	 * @param pWidth The width of the oval to draw
	 * @param pHeight The height of the oval to draw.
	 * @param pShadow True to include a drop shadow.
	 */
	public static void drawOval(GraphicsContext pGraphics, int pX, int pY, int pWidth, int pHeight, Paint pFill, boolean pShadow)
	{
		assert pWidth > 0 && pHeight > 0 && pFill != null && pGraphics != null;
		pGraphics.save();
		pGraphics.setFill(pFill);
		pGraphics.setStroke(ColorScheme.getScheme().getStrokeColor());
		if( pShadow )
		{
			pGraphics.setEffect(ColorScheme.getScheme().getDropShadow());
		}
		pGraphics.fillOval(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.strokeOval(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.restore();
	}
	
	/**
	 * Draws a white rounded rectangle with a drop shadow.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pRectangle The rectangle to draw.
	 */
	public static void drawRoundedRectangle(GraphicsContext pGraphics, Rectangle pRectangle)
	{
		assert pGraphics != null && pRectangle != null;
		pGraphics.save();
		applyShapeProperties(pGraphics);
		pGraphics.fillRoundRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, 
				pRectangle.width(), pRectangle.height(), ARC_SIZE, ARC_SIZE );
		pGraphics.setEffect(null);
		pGraphics.strokeRoundRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, 
				pRectangle.width(), pRectangle.height(), ARC_SIZE, ARC_SIZE);
		pGraphics.restore();
	}

	/**
	 * Strokes and fills a rectangle, originally in integer coordinates, so that it aligns precisely
	 * with the JavaFX coordinate system, which is 0.5 away from the pixel. See
	 * the documentation for javafx.scene.shape.Shape for details.
	 * 
	 * @param pGraphics The graphics context on which to draw the rectangle.
	 * @param pStroke The stroke (border) color for the rectangle.
	 * @param pFill The fill (background) color for the rectangle.
	 * @param pX The x-coordinate of the origin.
	 * @param pY The y-coordinate of the origin.
	 * @param pWidth The width.
	 * @param pHeight The height.
	 */
	public static void drawRectangle(GraphicsContext pGraphics, Paint pStroke, Paint pFill, 
			int pX, int pY, int pWidth, int pHeight)
	{
		pGraphics.save();
		pGraphics.setFill(pFill);
		pGraphics.setStroke(pStroke);
		pGraphics.fillRect(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.strokeRect(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.restore();
	}
	
	/**
	 * Draws a rectangle with default attributes.
	 * 
	 * @param pGraphics The graphics context on which to draw the rectangle.
	 * @param pRectangle The rectangle to draw.
	 */
	public static void drawRectangle( GraphicsContext pGraphics, Rectangle pRectangle)
	{
		assert pGraphics != null && pRectangle != null;
		pGraphics.save();
		applyShapeProperties(pGraphics);
		pGraphics.fillRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
		pGraphics.setEffect(null);
		pGraphics.strokeRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
		pGraphics.restore();
	}
	
	/**
	 * Draws a line with default attributes and a specified line style.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX1 The x-coordinate of the first point
	 * @param pY1 The y-coordinate of the first point
	 * @param pX2 The x-coordinate of the second point
	 * @param pY2 The y-coordinate of the second point
	 * @param pStyle The line style for the path.
	 */
	public static void drawLine(GraphicsContext pGraphics, int pX1, int pY1, int pX2, int pY2, LineStyle pStyle)
	{
		pGraphics.save();
		pGraphics.setStroke(ColorScheme.getScheme().getStrokeColor());
		pGraphics.setLineDashes(pStyle.getLineDashes());
		pGraphics.strokeLine(pX1 + 0.5, pY1 + 0.5, pX2 + 0.5, pY2 + 0.5);
		pGraphics.restore();
	}
	
	/**
	 * Draw pText in black with the given font, at point pX, pY.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX The x-coordinate where to draw the text.
	 * @param pY The y-coordinate where to draw the text.
	 * @param pText The text to draw.
	 * @param pFont The font to use.
	 */
	public static void drawText(GraphicsContext pGraphics, int pX, int pY, String pText, Font pFont)
	{
		pGraphics.save();
		pGraphics.setFont(pFont);
		pGraphics.setFill(ColorScheme.getScheme().getStrokeColor()); // The fill color for the font is the stroke color.
		pGraphics.fillText(pText, pX + 0.5, pY + 0.5);
		pGraphics.restore();
	}
	
	/**
	 * Apply the fill, stroke and effect property of the graphics context.
	 * 
	 * @param pGraphics The graphics context.
	 */
	private static void applyShapeProperties(GraphicsContext pGraphics)
	{
		pGraphics.setFill(ColorScheme.getScheme().getFillColor());
		pGraphics.setStroke(ColorScheme.getScheme().getStrokeColor());
		pGraphics.setEffect(ColorScheme.getScheme().getDropShadow());
	}
}

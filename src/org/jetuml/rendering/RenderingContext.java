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
 ******************************************************************************/
package org.jetuml.rendering;

import java.util.Optional;

import org.jetuml.geom.Alignment;
import org.jetuml.geom.Rectangle;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

/**
 * Object that can serve as a target for all drawing operations. This interface
 * contains rendering operations of two types: stroking a line or drawing a
 * figure, which is equivalent to filling and stroking.
 * 
 * The signature of some methods takes coordinates instead of geometric elements
 * (e.g., lines) for performance reasons: to avoid creating an object for every
 * call to a rendering primitive.
 */
public interface RenderingContext 
{
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
	void strokeLine(int pX1, int pY1, int pX2, int pY2, Color pColor, LineStyle pStyle);

	/**
	 * Draws a rectangle with default attributes.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	void drawRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow);

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
	void drawOval(int pX, int pY, int pWidth, int pHeight, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pShadow);

	/**
	 * Strokes an open arc.
	 * 
	 * @param pCenterX The x coordinate of the center of the arc
	 * @param pCenterY The y coordinate of the center of the arc
	 * @param pRadius The radius of the arc, in pixels.
	 * @param pStartAngle The start angle in degrees, 0 being at the bottom
	 * middle of the circle.
	 * @param pLength The length of the arc in degrees, moving counter-clockwise
	 * from the start angle.
	 * @param pStrokeColor The color of the stroke
	 */
	void strokeArc(int pCenterX, int pCenterY, int pRadius, int pStartAngle, int pLength, Color pStrokeColor);

	/**
	 * Strokes a path.
	 * 
	 * @param pPath The path to stroke
	 * @param pStrokeColor The color of the path.
	 * @param pStyle The line style for the path.
	 */
	void strokePath(Path pPath, Color pStrokeColor, LineStyle pStyle);

	/**
	 * Strokes and fills a path assumed to be closed.
	 * 
	 * @param pPath The path to stroke
	 * @param pFillColor The fill color for the path.
	 * @param pStrokeColor The stroke color.
	 * @param pShadow The drop shadow
	 */
	void drawClosedPath(Path pPath, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow);

	/**
	 * Draws a rounded rectangle.
	 * 
	 * @param pRectangle The rectangle to draw.
	 */
	void drawRoundedRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pDropShadow);

	/**
	 * Draw pText within pBounds. The text should be centered vertically within
	 * pBounds and aligned horizontally according to pTextPosition.
	 * 
	 * @param pText The text to draw
	 * @param pBounds The box in which to draw the text.
	 * @param pTextPosition The position of the text within pBounds.
	 * @param pTextColor The color of the text.
	 * @param pFont The font to use.
	 * @param pFontDimension The dimension of pFont.
	 */
	void drawText(String pText, Rectangle pBounds, Alignment pTextPosition, Color pTextColor, Font pFont, FontDimension pFontDimension);

}
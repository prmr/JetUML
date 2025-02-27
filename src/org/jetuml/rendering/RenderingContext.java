package org.jetuml.rendering;

import java.util.Optional;

import org.jetuml.geom.Rectangle;

import javafx.geometry.VPos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
	 * Strokes an arc.
	 * 
	 * @param pArc The arc to stroke.
	 * @param pStrokeColor The color of the stroke
	 * @pre pArc != null && pStrokeColor != null
	 */
	void strokeArc(Arc pArc, Color pStrokeColor);

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
	void drawText(String pText, int pBoundingX, int pBoundingY, int pRelativeX, int pRelativeY,
			TextAlignment pAlignment, VPos pBaseline, Color pTextColor, Font pFont);

}
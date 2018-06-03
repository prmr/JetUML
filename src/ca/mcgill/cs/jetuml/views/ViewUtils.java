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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A collection of utility methods to draw shapes on the canvas.
 * 
 * The methods in this class assume that the default canvas line width is 0.6, 
 * the stroke color black, and the fill color white. Any changes to these 
 * attributes should be reverted.
 * 
 * In the method names, "draw" refers to stroke and fill.
 */
public final class ViewUtils
{
	private static final DropShadow DROP_SHADOW = new DropShadow(3, 3, 3, Color.LIGHTGRAY);
	
	private ViewUtils()
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
		assert pDiameter > 0 && pFill != null && pGraphics != null;
		Paint oldFill = pGraphics.getFill();
		pGraphics.setFill(pFill);
		if( pShadow )
		{
			pGraphics.setEffect(DROP_SHADOW);
		}
		pGraphics.fillOval(pX + 0.5, pY + 0.5, pDiameter, pDiameter);
		pGraphics.strokeOval(pX + 0.5, pY + 0.5, pDiameter, pDiameter);
		pGraphics.setFill(oldFill);
		pGraphics.setEffect(null);
	}
}

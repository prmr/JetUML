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

import java.awt.BasicStroke;
import java.awt.Stroke;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Defines line styles of various shapes.
 */
public enum LineStyle
{
	SOLID, DOTTED;
	
	private static final int MITER_LIMIT = 10;
	
	private static final Stroke[] STROKES = new Stroke[] {
			new BasicStroke(),
			new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[] { 3, 3 }, 0)
	};
	
	private static final double[][] DASHES = new double[][] {
			new double[] { 1, 0 },
			new double[] { 3, 3 }
	};
	
	/**
	 * @return The stroke with which to draw this line style.
	 */
	public Stroke getStroke()
	{
		return STROKES[ordinal()];
	}
	
	/**
	 * Sets line properties of the graphics context.
	 * @param pGraphics the graphics context.
	 */
	public void setLineProperties(GraphicsContext pGraphics)
	{
		pGraphics.setLineCap(StrokeLineCap.SQUARE);
		pGraphics.setLineJoin(StrokeLineJoin.MITER);
		pGraphics.setMiterLimit(MITER_LIMIT);
		pGraphics.setLineDashes(DASHES[ordinal()]);
	}
}

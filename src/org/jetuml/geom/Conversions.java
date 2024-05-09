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
package org.jetuml.geom;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;

/**
 * Conversion utilities.
 */
public final class Conversions
{
	private Conversions() {}
	
	/**
	 * @param pBounds An input bounds object.
	 * @return A rectangle that corresponds to pBounds.
	 * @pre pBounds != null;
	 */
	public static Rectangle toRectangle(Bounds pBounds)
	{
		assert pBounds != null;
		return new Rectangle((int)pBounds.getMinX(), (int)pBounds.getMinY(), (int)pBounds.getWidth(), (int)pBounds.getHeight());
	}
	
	/**
	 * @param pRectangle2D The rectangle to convert.
	 * @return A rectangle created from rounding both x and y coordinate and the width
	 *     and height of the input parameter to integers.
	 * @pre pRectangle2D != null;
	 */
	public static Rectangle toRectangle(Rectangle2D pRectangle2D)
	{
		assert pRectangle2D != null;
		return new Rectangle( GeomUtils.round(pRectangle2D.getMinX()),
							  GeomUtils.round(pRectangle2D.getMinY()),
							  GeomUtils.round(pRectangle2D.getWidth()),
							  GeomUtils.round(pRectangle2D.getHeight()));
	}
}

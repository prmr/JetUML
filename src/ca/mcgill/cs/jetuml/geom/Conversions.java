/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.geom;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Conversion utilities.
 */
public final class Conversions
{
	private Conversions() {}
	
	/**
	 * @param pPoint2D The point to convert.
	 * @return A point created from rounding both x and y coordinate
	 * of the input parameter to integers.
	 * @pre pPoint2D != null;
	 */
	public static Point toPoint(Point2D pPoint2D)
	{
		assert pPoint2D != null;
		return new Point( (int)Math.round(pPoint2D.getX()), (int)Math.round(pPoint2D.getY()));
	}
	
	/**
	 * @param pPoint The point to covert.
	 * @return A Point2D with the same coordinates as pPoint.
	 * @pre pPoint != null;
	 */
	public static Point2D toPoint2D(Point pPoint)
	{
		assert pPoint != null;
		return new Point2D(pPoint.getX(), pPoint.getY());
	}
	
	/**
	 * @param pRectangle2D The rectangle to convert.
	 * @return A rectangle created from rounding both x and y coordinate and the width
	 * and height of the input parameter to integers.
	 * @pre pRectangle2D != null;
	 */
	public static Rectangle toRectangle(Rectangle2D pRectangle2D)
	{
		assert pRectangle2D != null;
		return new Rectangle( (int)Math.round(pRectangle2D.getMinX()),
							  (int)Math.round(pRectangle2D.getMinY()),
							  (int)Math.round(pRectangle2D.getWidth()),
							  (int)Math.round(pRectangle2D.getHeight()));
	}
	
	/**
	 * @param pRectangle The rectangle to convert.
	 * @return A rectangle created from both x and y coordinate and the width
	 * and height of the input parameter to integers.
	 * @pre pRectangle != null;
	 */
	public static Rectangle2D toRectangle2D(Rectangle pRectangle)
	{
		assert pRectangle != null;
		return new Rectangle2D( pRectangle.getX(),
							  pRectangle.getY(),
							  pRectangle.getWidth(),
							  pRectangle.getHeight());
	}
}

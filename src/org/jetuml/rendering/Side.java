/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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

import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

/**
 * Represents one side of a rectangular node.
 */
public enum Side 
{
	TOP, BOTTOM, RIGHT, LEFT;
	
	/**
	 * @return True if this side is a horizontal line, that is, 
	 * the top or the bottom side.
	 */
	public boolean isHorizontal()
	{
		return this == TOP || this == BOTTOM;
	}
	
	/**
	 * @return True if this side is a vertical line, that is, the 
	 * right or the left side.
	 */
	public boolean isVertical()
	{
		return this == RIGHT || this == LEFT;
	}
	
	/**
	 * @return The side opposite the current side on
	 *     the rectangle.
	 */
	public Side mirrored()
	{
		if( this == TOP )
		{
			return BOTTOM;
		}
		else if( this == BOTTOM)
		{
			return TOP;
		}
		else if( this == RIGHT)
		{
			return LEFT;
		}
		else 
		{
			return RIGHT;
		}
	}
	
	/**
	 * Returns a new Line object that corresponds to the corresponding
	 * side of the input rectangle.
	 * 
	 * @param pSide The required side.
	 * @return A line with the geometry of the required side.
	 * @pre pSide != null
	 */
	public Line getCorrespondingLine(Rectangle pRectangle)
	{
		assert pRectangle != null;
		if( this == Side.TOP )
		{
			return new Line(new Point(pRectangle.x(), pRectangle.y()), 
					new Point(pRectangle.maxX(), pRectangle.y()));
		}
		else if( this == Side.BOTTOM)
		{
			return new Line(new Point(pRectangle.x(), pRectangle.maxY()), 
					new Point(pRectangle.maxX(), pRectangle.maxY()));
		}
		else if( this == Side.RIGHT)
		{
			return new Line(new Point(pRectangle.maxX(), pRectangle.y()), 
					new Point(pRectangle.maxX(), pRectangle.maxY()));
		}
		else
		{
			assert this == Side.LEFT;
			return new Line(new Point(pRectangle.x(), pRectangle.y()), 
					new Point(pRectangle.x(), pRectangle.maxY()));
		}
	}
}

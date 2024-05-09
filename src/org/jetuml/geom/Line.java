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

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * A pair of connected points in the integer space.
 * @param point1 The first point in the line. Conceptually the "start" of the line. point1 != null
 * @param point2 The second point in the line. Conceptually the "end" of the line. point2 != null
 */
public record Line(Point point1, Point point2)
{
	/**
	 * Creates a new line from individual coordinates.
	 * 
	 * @param pX1 The X-coordinate of the start of the line.
	 * @param pY1 The Y-coordinate of the start of the line.
	 * @param pX2 The X-coordinate of the end of the line.
	 * @param pY2 The Y-coordinate of the end of the line.
	 */
	public Line(int pX1, int pY1, int pX2, int pY2)
	{
		this( new Point(pX1, pY1), new Point(pX2, pY2));
	}

	/**
	 * @return The x coordinate of point 1.
	 */
	public int x1()
	{
		return point1.x();
	}
	
	/**
	 * @return The y coordinate of point 1.
	 */
	public int y1()
	{
		return point1.y();
	}
	
	/**
	 * @return The x coordinate of point 2.
	 */
	public int x2()
	{
		return point2.x();
	}
	
	/**
	 * @return The y coordinate of point 2.
	 */
	public int y2()
	{
		return point2.y();
	}
	
	/**
	 * A distance between points is the absolute difference
	 * between X-values expressed as a width and the absolute
	 * difference between Y-values expressed as a height.
	 * 
	 * @return The distance between points as a dimension
	 */
	public Dimension distanceBetweenPoints()
	{
		return new Dimension(Math.abs(x1()-x2()), 
				Math.abs(y1()-y2()));
	}
	
	/**
	 * @return A new line that is this line with the two points reversed
	 */
	public Line reversed()
	{
		return new Line(point2(), point1());
	}
	
	/**
	 * @return The point at the center of the line.
	 */
	public Point center()
	{
		return new Point((x1() + x2())/2, (y1() + y2())/2);
	}
	
	/**
	 * A line is horizontal if the Y-value of both points is the same.
	 * 
	 * @return True iif the line is horizontal.
	 */
	public boolean isHorizontal()
	{
		return y1() == y2();
	}
	
	/**
	 * A line is vertical if the X-value of both points is the same.
	 * 
	 * @return True iif the line is horizontal.
	 */
	public boolean isVertical()
	{
		return x1() == x2();
	}
	
	/**
	 * @return The rectangle spanning this line.
	 */
	public Rectangle spanning()
	{
		return new Rectangle(min(x1(), x2()), min(y1(), y2()), 
				abs(x2() - x1()), abs(y2() - y1()));
	}
}

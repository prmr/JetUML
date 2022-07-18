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

import static java.lang.Math.min;

import java.util.Objects;

import org.jetuml.annotations.Immutable;

import static java.lang.Math.abs;

/**
 * A pair of connected points in the integer space.
 */
@Immutable
public class Line
{
	private final Point aPoint1;
	private final Point aPoint2;
	
	/**
	 * Creates a new line.
	 * 
	 * @param pPoint1 The first point in the line. Conceptually the "start" of the line.
	 * @param pPoint2 The second point in the line. Conceptually the "end" of the line.
	 * @pre pPoint1 != null
	 * @pre pPoint2 != null
	 */
	public Line(Point pPoint1, Point pPoint2)
	{
		assert pPoint1 != null && pPoint2 != null;
		aPoint1 = pPoint1; 
		aPoint2 = pPoint2;
	}
	
	/**
	 * Creates a new line.
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
	 * @return The first point of the line.
	 */
	public Point getPoint1()
	{
		return aPoint1;
	}
	
	/**
	 * @return The x coordinate of point 1.
	 */
	public int getX1()
	{
		return aPoint1.getX();
	}
	
	/**
	 * @return The y coordinate of point 1.
	 */
	public int getY1()
	{
		return aPoint1.getY();
	}
	
	/**
	 * @return The x coordinate of point 2.
	 */
	public int getX2()
	{
		return aPoint2.getX();
	}
	
	/**
	 * @return The y coordinate of point 2.
	 */
	public int getY2()
	{
		return aPoint2.getY();
	}
	
	/**
	 * @return The second point of the line.
	 */
	public Point getPoint2()
	{
		return aPoint2;
	}
	
	/**
	 * @return The rectangle spanning this line.
	 */
	public Rectangle spanning()
	{
		return new Rectangle(min(getX1(), getX2()), min(getY1(), getY2()), 
				abs(getX2() - getX1()), abs(getY2() - getY1()));
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(aPoint1, aPoint2);
	}

	@Override
	public boolean equals(Object pObject)
	{
		if(this == pObject)
		{
			return true;
		}
		if(pObject == null)
		{
			return false;
		}
		if(getClass() != pObject.getClass())
		{
			return false;
		}
		Line other = (Line) pObject;
		return Objects.equals(aPoint1, other.aPoint1) && Objects.equals(aPoint2, other.aPoint2);
	}
	
	@Override
	public String toString()
	{
		return String.format("[%s, %s]", aPoint1, aPoint2);
	}
}

/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

import org.jetuml.annotations.Immutable;

/**
 * A framework independent representation of a point in integer space. 
 */
@Immutable
public final class Point
{
	private int aX;
	private int aY;
	
	/**
	 * Create a new point.
	 * 
	 * @param pX The x-coordinate of the point.
	 * @param pY The y-coordinate of the point.
	 */
	public Point( int pX, int pY)
	{
		aX = pX;
		aY = pY;
	}
	
	/**
	 * @return The X-coordinate.
	 */
	public int getX()
	{
		return aX;
	}
	
	/**
	 * @return The Y-coordinate.
	 */
	public int getY()
	{
		return aY;
	}
	
	/**
	 * @param pPoint Another point.
	 * @return The distance between two points.
	 * @pre pPoint != null
	 */
	public double distance(Point pPoint)
	{
		assert pPoint != null;
		int a = pPoint.aY - aY;
		int b = pPoint.aX - aX;
		return Math.sqrt(a*a + b*b);
	}
	
	/**
	 * @return A copy of this point.
	 */
	public Point copy()
	{
		return new Point(aX, aY);
	}
	
	@Override
	public String toString()
	{
		return String.format("(x=%d,y=%d)", aX, aY);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + aX;
		result = prime * result + aY;
		return result;
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
		Point other = (Point) pObject;
		return aX == other.aX && aY == other.aY;
	}
}

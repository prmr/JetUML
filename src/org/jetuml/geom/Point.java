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

/**
 * A framework independent representation of a point in integer space. 
 * @param x The x-coordinate of the point.
 * @param y The y-coordinate of the point.
 */
public record Point(int x, int y)
{
	/**
	 * @param pPoint Another point.
	 * @return The distance between two points.
	 * @pre pPoint != null
	 * TODO: Convert return type to int.
	 */
	public double distance(Point pPoint)
	{
		assert pPoint != null;
		int a = pPoint.y - y;
		int b = pPoint.x - x;
		return Math.sqrt(a*a + b*b);
	}
	
	/**
	 * @return A copy of this point.
	 */
	public Point copy()
	{
		return new Point(x, y);
	}
}

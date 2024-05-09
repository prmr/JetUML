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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jetuml.geom.Point;

/**
 * Represents the path of an edge on a diagram as a list of points. 
 * Non-segmented paths consist of 2 points (the start and end points).
 */
public final class EdgePath implements Iterable<Point>
{
	private List<Point> aPoints;
	
	/**
	 * Initializes an EdgePath composed of pPoints.
	 * @param pPoints the points of an edge (the start point, possible segment connections, and the end point)
	 * @pre pPoints.length >= 2
	 */
	public EdgePath(Point...pPoints)
	{
		assert pPoints.length >= 2;
		aPoints = Arrays.asList(pPoints);
	}
	
	/**
	 * Gets the starting point for the path.
	 * @return the Point where the edge starts.
	 */
	public Point getStartPoint()
	{
		return aPoints.get(0);
	}
	
	/**
	 * Gets the end point of the edge.
	 * @return the Point where the edge ends.
	 */
	public Point getEndPoint()
	{
		return aPoints.get(aPoints.size()-1);
	}

	/**
	 * Returns the point in the edge path at position pIndex, where index 0 refers to the start point.
	 * @param pIndex the index of aPoints
	 * @return the Point in aPoints at pIndex < aPoints.size()
	 * @pre pIndex > 0 && pIndex
	 */
	public Point getPointByIndex(int pIndex)
	{
		assert pIndex >= 0 && pIndex < aPoints.size();
		return aPoints.get(pIndex);
	}

	@Override
	public int hashCode() 
	{
		return Objects.hash(aPoints);
	}

	@Override
	public boolean equals(Object pObj) 
	{
		if(this == pObj)
		{
			return true;
		}
		if(pObj == null)
		{
			return false;
		}
		if(getClass() != pObj.getClass())
		{
			return false;
		}
		EdgePath other = (EdgePath) pObj;
		if(other.aPoints.size() != this.aPoints.size())
		{
			return false;
		}
		for(int i = 0; i < aPoints.size(); i++)
		{
			if(!other.aPoints.get(i).equals(aPoints.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the number of points in the path.
	 * @return an integer representing the size of the EdgePath
	 */
	public int size()
	{
		return aPoints.size();
	}

	@Override
	public Iterator<Point> iterator() 
	{
		return aPoints.iterator();
	}	
}
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

/**
 * A framework independent representation of a point in 
 * integer space. 
 */
public final class Point implements Cloneable
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
	 * Sets the X coordinate.
	 * 
	 * @param pX The new X coordinate.
	 */
	public void setX(int pX)
	{
		aX = pX;
	}
	
	/**
	 * Sets the Y coordinate.
	 * 
	 * @param pY The new Y coordinate.
	 */
	public void setY(int pY)
	{
		aY = pY;
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
	
	@Override
	public Point clone()
	{
		try
		{
			return (Point) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
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

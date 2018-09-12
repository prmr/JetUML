/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * Represents an immutable pair of width and height.
 */
public class Dimension
{
	private final int aWidth;
	private final int aHeight;
	
	/**
	 * @param pWidth The width. >= 0
	 * @param pHeight The height. >= 0
	 */
	public Dimension( int pWidth, int pHeight )
	{
		assert pWidth >= 0 && pHeight >= 0;
		aWidth = pWidth;
		aHeight = pHeight;
	}
	
	@Override
	public String toString()
	{
		return String.format("[Dimension: w=%d x h=%d]", aWidth, aHeight);
	}
	
	/**
	 * @return The width component of this dimension.
	 */
	public int getWidth()
	{
		return aWidth;
	}
	
	/**
	 * @return The height component of this dimension.
	 */
	public int getHeight()
	{
		return aHeight;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + aHeight;
		result = prime * result + aWidth;
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
		Dimension other = (Dimension) pObject;
		return aHeight == other.aHeight && aWidth == other.aWidth;
	}
}

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

/**
 * Represents one side of a rectangular node.
 */
public enum NodeSide 
{
	NORTH, SOUTH, EAST, WEST;
	
	/**
	 * @return True if this is the North or South side.
	 */
	public boolean isNorthSouth()
	{
		return this == NORTH || this == SOUTH;
	}
	
	/**
	 * @return True if this is the East or West side.
	 */
	public boolean isEastWest()
	{
		return this == EAST || this == WEST;
	}
	
	/**
	 * @return The side opposite the current side on
	 *     the rectangle.
	 */
	public NodeSide mirrored()
	{
		if( this == NORTH )
		{
			return SOUTH;
		}
		else if( this == SOUTH)
		{
			return NORTH;
		}
		else if( this == EAST)
		{
			return WEST;
		}
		else 
		{
			return EAST;
		}
	}
}

/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.geom;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * This class describes an immutable direction in the 2D plane. The
 * direction is conceptually represented as an angle in degrees between
 * 0 and 359 inclusively, where 0 represents "up" or "north", and where
 * an increase in the angle moves the direction clockwise.
 */
public class Direction
{
	public static final Direction NORTH = new Direction(0);
	public static final Direction EAST = new Direction(90);
	public static final Direction SOUTH = new Direction(180);
	public static final Direction WEST = new Direction(270);

	private static final int DEGREES_IN_CIRCLE = 360;

	private final int aAngleInDegrees;

	/**
	 * Creates a new direction that represents this angle.
	 * 
	 * @param pAngle The angle to specify.
	 * @pre pAngle >= 0 && pAngle < 360;
	 */
	public Direction(int pAngle)
	{
		assert pAngle >= 0 && pAngle < DEGREES_IN_CIRCLE;
		aAngleInDegrees = pAngle;
	}

	/**
	 * Constructs a direction from two points. The direction
	 * is equivalent to the direction of the vector that runs
	 * start point to the end point.
	 * 
	 * @param pStart The starting point
	 * @param pEnd The ending point
	 * @pre pStart != null && pEnd != null
	 * @pre ! pStart.equals(pEnd);
	 */
	public Direction(Point pStart, Point pEnd)
	{
		this(asAngle(pEnd.getX() - pStart.getX(), pEnd.getY() - pStart.getY()));
		assert pStart != null && pEnd != null;
		assert !pStart.equals(pEnd);
	}

	/**
	 * Returns a new direction that represents this direction turned clockwise by pAngle.
	 * 
	 * @param pAngle The angle in degrees for which to turn the direction.
	 * @return The new, rotated direction.
	 */
	public Direction rotate(int pAngle)
	{
		return new Direction((aAngleInDegrees + pAngle) % DEGREES_IN_CIRCLE);
	}

	/**
	 * Gets the x-component of this direction.
	 * 
	 * @return the x-component (between -1 and 1)
	 */
	public double getX()
	{
		return sin(Math.toRadians(aAngleInDegrees));
	}

	/**
	 * Gets the y-component of this direction.
	 * 
	 * @return the y-component (between -1 and 1)
	 */
	public double getY()
	{
		return -cos(toRadians(aAngleInDegrees));
	}

	/**
	 * @return The direction as an angle between 0 (north) and 359.
	 */
	public int asAngle()
	{
		return aAngleInDegrees;
	}

	/*
	 * Computes an angle given a width an a height, so 
	 * that angle 0 corresponds to pWidth=0 and pHeight > 0,
	 * in other words a vector pointing north.
	 */
	private static int asAngle(int pWidth, int pHeight)
	{
		// Compute the normalized height 
		// The sign is reversed to account for the fact that in graphics systems
		// the y-coordinate increases from top to bottom.
		double hypothenuse = Math.sqrt(pWidth * pWidth + pHeight * pHeight);
		double normalizedHeight = -pHeight/hypothenuse;
		
		// Compute the angle. We use the arccos instead of the arcsin
		// despite the fact that the height is the opposing side to shift
		// the angle by 90 to align 0 with north.
		long degrees = round(toDegrees(acos(normalizedHeight)));
		
		// We negate the angle for the left half-plane
		if( pWidth <= 0 )
		{
			degrees = -degrees;
		}
		
		// Position the angle in the [0, 359] range
		return (int) (degrees + DEGREES_IN_CIRCLE) % DEGREES_IN_CIRCLE;
	}

	@Override
	public String toString()
	{
		return String.format("[Direction: %d degrees]", aAngleInDegrees);
	}
}

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

package ca.mcgill.cs.jetuml.geom;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * A collection of utility methods related to geometric
 * calculations and supporting functions.
 */
public final class GeomUtils
{
	private GeomUtils()
	{}
	
	/**
	 * @param pArguments Arguments to test
	 * @return The maximum value of all arguments.
	 * @pre pArguments.length > 0
	 */
	public static int max(int... pArguments)
	{
		assert pArguments.length > 0;
		int result = Integer.MIN_VALUE;
		for( int argument : pArguments)
		{
			if( argument > result )
			{
				result = argument;
			}
		}
		return result;
	}
	
	/*
	 * Returns the point that intersects the line that runs in direction pDirection from
	 * the center of pRectangle, with one side of pRectangle.
	 * @pre pDirection.isCardinal()
	 */
	private static Point intersectionForCardinalDirection(Rectangle pRectangle, Direction pDirection)
	{
		assert pDirection.isCardinal();
		if( pDirection == Direction.NORTH )
		{
			return new Point(pRectangle.getCenter().getX(), pRectangle.getY());
		}
		else if( pDirection == Direction.SOUTH )
		{
			return new Point(pRectangle.getCenter().getX(), pRectangle.getMaxY());
		}
		else if( pDirection == Direction.EAST )
		{
			return new Point(pRectangle.getMaxX(), pRectangle.getCenter().getY());
		}
		else // pDirection == Direction.WEST 
		{
			return new Point(pRectangle.getX(), pRectangle.getCenter().getY());
		}
	}
	
	/**
	 * @param pRectangle The rectangle to intersect with.
	 * @param pDirection The direction to follow to intersect the sides of the rectangle.
	 * @return The point that intersects pRectangle if we draw a line from its center going
	 *     in direction pDirection.
	 * @pre pRectangle != null && pDirection != null
	 */
	public static Point intersectRectangle(Rectangle pRectangle, Direction pDirection)
	{
		assert pRectangle != null && pDirection != null;
		
		if( pDirection.isCardinal() )
		{
			return intersectionForCardinalDirection(pRectangle, pDirection);
		}
		
		Direction diagonalNE = Direction.fromLine(pRectangle.getCenter(), new Point(pRectangle.getMaxX(), pRectangle.getY()));
		Direction diagonalSE = Direction.fromLine(pRectangle.getCenter(), new Point(pRectangle.getMaxX(), pRectangle.getMaxY()));
		Direction diagonalSW = diagonalNE.mirrored();
		Direction diagonalNW = diagonalSE.mirrored();
		
		if( pDirection.isBetween(diagonalNE, diagonalSE))
		{
			int offset = lengthOfOpposingSide(pDirection.asAngle() - Direction.EAST.asAngle(), pRectangle.getWidth()/2);
			return new Point(pRectangle.getMaxX(), pRectangle.getCenter().getY() + offset);
		}
		else if( pDirection.isBetween(diagonalSE, diagonalSW))
		{
			int offset = lengthOfOpposingSide(pDirection.asAngle() - Direction.SOUTH.asAngle(), pRectangle.getHeight()/2);
			return new Point(pRectangle.getCenter().getX() - offset, pRectangle.getMaxY());
		}
		else if( pDirection.isBetween(diagonalSW, diagonalNW))
		{
			int offset = lengthOfOpposingSide(pDirection.asAngle() - Direction.WEST.asAngle(), pRectangle.getWidth()/2);
			return new Point(pRectangle.getX(), pRectangle.getCenter().getY() - offset);
		}
		else
		{
			final int angleS = 360;
			int offset = lengthOfOpposingSide(pDirection.asAngle() - angleS, pRectangle.getHeight()/2);
			return new Point(pRectangle.getCenter().getX() + offset, pRectangle.getY());
		}
	}
	
	/**
	 * Returns the point that intersects a circle on the line that originates
	 * at the center of pBounds going in direction pDirection. 
	 * 
	 * @param pBounds The bounds of the rectangle in which the circle to intersect in inscribed.
	 * @param pDirection The direction to follow to intersect the sides of the circle.
	 * @return The point that intersects the circle inscribed in pBounds if we draw a line from its center going
	 *     in direction pDirection.
	 * @pre pBounds != null && pDirection != null && pBounds.getWidth() == pBounds.getHeight()
	 */
	public static Point intersectCircle(Rectangle pBounds, Direction pDirection)
	{
		assert pBounds != null && pDirection != null && pBounds.getWidth() == pBounds.getHeight();
		
		if( pDirection.isCardinal() )
		{
			return intersectionForCardinalDirection(pBounds, pDirection);
		}
		
		final int radius = pBounds.getWidth()/2;
		
		int offsetX = (int) round(cos(toRadians(pDirection.asAngle() - Direction.EAST.asAngle())) * radius);
		int offsetY = (int) round(sin(toRadians(pDirection.asAngle() - Direction.EAST.asAngle())) * radius);
		return new Point( pBounds.getCenter().getX() + offsetX, pBounds.getCenter().getY() + offsetY);
	}   	 
	
	/**
	 * Returns the point that intersects a ellipse on the line that originates
	 * at the center of pBounds going in direction pDirection. 
	 * 
	 * @param pBounds The bounds of the rectangle in which the ellipse to intersect in inscribed.
	 * @param pDirection The direction to follow to intersect the sides of the ellipse.
	 * @return The point that intersects the ellipse inscribed in pBounds if we draw a line from its 
	 *     center going in direction pDirection.
	 * @pre pBounds != null && pDirection != null
	 */
	public static Point intersectEllipse(Rectangle pBounds, Direction pDirection)
	{
		assert pBounds != null && pDirection != null;
		
		if( pDirection.isCardinal() )
		{
			return intersectionForCardinalDirection(pBounds, pDirection);
		}
		
		final int a = pBounds.getWidth()/2;
		final int b = pBounds.getHeight()/2;
		
		int offsetX = (int) round(cos(toRadians(pDirection.asAngle() - Direction.EAST.asAngle())) * a);
		int offsetY = (int) round(sin(toRadians(pDirection.asAngle() - Direction.EAST.asAngle())) * b);
		return new Point( pBounds.getCenter().getX() + offsetX, pBounds.getCenter().getY() + offsetY);
	}

	/**
	 * @param pBounds The bounds of the rectangle in which the rounded rectangle to intersect in inscribed.
	 * @param pDirection The direction to follow to intersect the sides of the rounded rectangle.
	 * @return The point that intersects the rounded rectangle inscribed in pBounds if we draw a line from 
	 * 	   its center going in direction pDirection.
	 * @pre pBounds != null && pDirection != null
	 */
	public static Point intersectRoundedRectangle(Rectangle pBounds, Direction pDirection)
	{
		assert pBounds != null && pDirection != null;
		
		if( pDirection.isCardinal() )
		{
			return intersectionForCardinalDirection(pBounds, pDirection);
		}
		
		final int arcSize = 20; // same as the value in viewUtils
		int radius = arcSize/2;
		int widthOffset = pBounds.getWidth()/2 - radius;
		int heightOffset = pBounds.getHeight()/2 - radius;
		
		// calculate bounds of rounded corner
		Direction topNE = Direction.fromLine(pBounds.getCenter(), new Point(pBounds.getMaxX() - radius, pBounds.getY()));
		Direction bottomNE = Direction.fromLine(pBounds.getCenter(), new Point(pBounds.getMaxX(), pBounds.getY() + radius));
		Direction topSE = Direction.fromLine(pBounds.getCenter(), new Point(pBounds.getMaxX(), pBounds.getMaxY() - radius));
		Direction bottomSE = Direction.fromLine(pBounds.getCenter(), new Point(pBounds.getMaxX() - radius, pBounds.getMaxY()));
		Direction topSW = topNE.mirrored(); 
		Direction bottomSW = bottomNE.mirrored();
		Direction topNW = topSE.mirrored();
		Direction bottomNW = bottomSE.mirrored();
		
		Point rectangleIntersectionPoint = intersectRectangle(pBounds, pDirection);
		Point result = null;
		
		if( pDirection.isBetween(topNE, bottomNE))
		{
			Point cornerCenter = new Point(pBounds.getCenter().getX() + widthOffset, pBounds.getCenter().getY() - heightOffset);
			Direction cornerDirection = Direction.fromLine(cornerCenter, rectangleIntersectionPoint);
			int offsetX = (int) round(cos(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			int offsetY = (int) round(sin(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			result =  new Point( pBounds.getCenter().getX() + offsetX + widthOffset, pBounds.getCenter().getY() + offsetY - heightOffset);
		}
		else if( pDirection.isBetween(topSE, bottomSE))
		{
			Point cornerCenter = new Point(pBounds.getCenter().getX() + widthOffset, pBounds.getCenter().getY() + heightOffset);
			Direction cornerDirection = Direction.fromLine(cornerCenter, rectangleIntersectionPoint);
			int offsetX = (int) round(cos(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			int offsetY = (int) round(sin(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			result = new Point( pBounds.getCenter().getX() + offsetX + widthOffset, pBounds.getCenter().getY() + offsetY + heightOffset);
		}
		else if( pDirection.isBetween(topSW, bottomSW))
		{
			Point cornerCenter = new Point(pBounds.getCenter().getX() - widthOffset, pBounds.getCenter().getY() + heightOffset);
			Direction cornerDirection = Direction.fromLine(cornerCenter, rectangleIntersectionPoint);
			int offsetX = (int) round(cos(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			int offsetY = (int) round(sin(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			result = new Point( pBounds.getCenter().getX() + offsetX - widthOffset, pBounds.getCenter().getY() + offsetY + heightOffset);
		}
		else if( pDirection.isBetween(topNW, bottomNW))
		{
			Point cornerCenter = new Point(pBounds.getCenter().getX() - widthOffset, pBounds.getCenter().getY() - heightOffset);
			Direction cornerDirection = Direction.fromLine(cornerCenter, rectangleIntersectionPoint);
			int offsetX = (int) round(cos(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			int offsetY = (int) round(sin(toRadians(cornerDirection.asAngle() - Direction.EAST.asAngle())) * radius);
			result = new Point( pBounds.getCenter().getX() + offsetX - widthOffset, pBounds.getCenter().getY() + offsetY - heightOffset);
		}
		else
		{
			result = rectangleIntersectionPoint;
		}
		
		return result;
	}
	
	/*
     * returns the length, in pixel, of the opposing side to the angle pAngleInDegrees
     * of a right triangle for when the length (in pixels) of the adjacent side is 
     * known.
 	 */
	private static int lengthOfOpposingSide(int pAngleInDegrees, int pAdjacentSide)
	{
		return (int) Math.round(pAdjacentSide * Math.tan(Math.toRadians(pAngleInDegrees)));
	}

}

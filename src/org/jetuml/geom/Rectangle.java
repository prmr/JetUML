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

import org.jetuml.annotations.Immutable;

/**
 * A framework independent representation of a rectangle in 
 * 2-dimensional integer space.
 */
@Immutable
public class Rectangle
{
	private final int aX;
	private final int aY;
	private final int aWidth;
	private final int aHeight;
	
	/**
	 * Creates a new rectangle.
	 * 
	 * @param pX The X-coordinate of the top-left corner.
	 * @param pY The Y-coordinate of the top-left corner.
	 * @param pWidth The width of the rectangle.
	 * @param pHeight The height of the rectangle.
	 * @pre pWidth >=0 
	 * @pre pHeight >= 0
	 */
	public Rectangle(int pX, int pY, int pWidth, int pHeight)
	{
		assert pWidth >= 0 && pHeight >= 0;
		aX = pX;
		aY = pY;
		aWidth = pWidth;
		aHeight = pHeight;
	}
	
	/**
	 * @return The X-coordinate of the top-left point.
	 */
	public int getX()
	{
		return aX;
	}
	
	/**
	 * @return The Y-coordinate of the top-left point.
	 */
	public int getY()
	{
		return aY;
	}
	
	/**
	 * @return The X-coordinate farthest from the origin. 
	 */
	public int getMaxX()
	{
		return aX + aWidth;
	}
	
	/**
	 * @return The Y-coordinate farthest from the origin. 
	 */
	public int getMaxY()
	{
		return aY + aHeight;
	}
	

	/**
	 * @return The width.
	 */
	public int getWidth()
	{
		return aWidth;
	}
	
	/**
	 * @return The height.
	 */
	public int getHeight()
	{
		return aHeight;
	}
	
	/**
	 * Returns a new Line object that corresponds to the specified
	 * side of the rectangle.
	 * 
	 * @param pSide The required side.
	 * @return A line with the geometry of the required side.
	 * @pre pSide != null
	 */
	public Line getSide(Side pSide)
	{
		assert pSide != null;
		if( pSide == Side.TOP )
		{
			return new Line(new Point(getX(), getY()), new Point(getMaxX(), getY()));
		}
		else if( pSide == Side.BOTTOM)
		{
			return new Line(new Point(getX(), getMaxY()), new Point(getMaxX(), getMaxY()));
		}
		else if( pSide == Side.RIGHT)
		{
			return new Line(new Point(getMaxX(), getY()), new Point(getMaxX(), getMaxY()));
		}
		else
		{
			assert pSide == Side.LEFT;
			return new Line(new Point(getX(), getY()), new Point(getX(), getMaxY()));
		}
	}
	
	/**
	 * @param pDeltaX The amount to translate in the X-coordinate.
	 * @param pDeltaY The amount to translate in the Y-coordinate.
	 * @return The translated rectangle.
	 */
	public Rectangle translated(int pDeltaX, int pDeltaY)
	{
		return new Rectangle(aX + pDeltaX, aY + pDeltaY, aWidth, aHeight);
	}
	
	/**
	 * @param pPoint The point to check.
	 * @return True iif pPoint is within the rectangle or on its boundary.
	 * @pre pPoint != null;
	 */
	public boolean contains(Point pPoint)
	{
		assert pPoint != null;
		return pPoint.getX() >= aX && pPoint.getX() <= aX + aWidth &&
				pPoint.getY() >= aY && pPoint.getY() <= aY + aHeight;
	}
	
	/**
	 * @param pRectangle The rectangle to check.
	 * @return True iif pRectangle is entired contains within this rectangle.
	 * @pre pRectangle !=null.
	 */
	public boolean contains(Rectangle pRectangle)
	{
		assert pRectangle != null;
		return pRectangle.aX >= aX && pRectangle.aY >= aY &&
				pRectangle.getMaxX() <= aX + aWidth &&
				pRectangle.getMaxY() <= aY + aHeight;
	}
	
	/**
	 * @return A point in the center of this rectangle.
	 */
	public Point getCenter()
	{
		return new Point( aX + aWidth/2, aY + aHeight/2);
	}
	
	/**
	 * @param pPoint The point to include.
	 * @return A new rectangle that is this rectangle enlarged to include pPoint.
	 * @pre pPoint != null
	 */ 
	public Rectangle add(Point pPoint)
	{
		assert pPoint != null;
		int x = aX;
		int y = aY;
		int width = aWidth;
		int height = aHeight;
		if( pPoint.getX() < aX)
		{
			x = pPoint.getX();
			width = getMaxX() - pPoint.getX();
		}
		else if( pPoint.getX() > getMaxX())
		{
			width = pPoint.getX() - aX;
		}
		if( pPoint.getY() < aY )
		{
			y = pPoint.getY();
			height = getMaxY() - pPoint.getY();
		}
		else if( pPoint.getY() > getMaxY())
		{
			height = pPoint.getY() - aY;
		}
		return new Rectangle(x, y, width, height);			
	}
	
	/**
	 * @param pRectangle The rectangle to include.
	 * @return A new rectangle that is this rectangle enlarged to include pRectangle.
	 * @pre pRectangle != null
	 */ 
	public Rectangle add(Rectangle pRectangle)
	{
		assert pRectangle != null;
		int x = Math.min(aX, pRectangle.aX);
		int y = Math.min(aY, pRectangle.aY);
		int maxX = Math.max(getMaxX(), pRectangle.getMaxX());
		int maxY = Math.max(getMaxY(), pRectangle.getMaxY());
		return new Rectangle(x, y, maxX - x, maxY-y);
	}
	
	/**
	 * @return The top left corner of the rectangle.
	 */
	public Point getOrigin()
	{
		return new Point(aX, aY);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + aHeight;
		result = prime * result + aWidth;
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
		Rectangle other = (Rectangle) pObject;
		return aX == other.aX && aY == other.aY && aHeight == other.aHeight && aWidth == other.aWidth;
	}
	
	@Override
	public String toString()
	{
		return String.format("[x=%d, y=%d, w=%d, h=%d]", aX, aY, aWidth, aHeight);
	}
}

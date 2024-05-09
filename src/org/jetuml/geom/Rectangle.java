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

/**
 * A framework independent representation of a rectangle in 
 * 2-dimensional integer space.
 * @param x The X-coordinate of the top-left corner.
 * @param y The Y-coordinate of the top-left corner.
 * @param width The width of the rectangle. width >=0 
 * @param height The height of the rectangle. height >= 0
 */
public record Rectangle(int x, int y, int width, int height)
{
	/**
	 * @return The X-coordinate farthest from the origin. 
	 */
	public int maxX()
	{
		return x + width;
	}
	
	/**
	 * @return The Y-coordinate farthest from the origin. 
	 */
	public int maxY()
	{
		return y + height;
	}
	
	/**
	 * @param pDeltaX The amount to translate in the X-coordinate.
	 * @param pDeltaY The amount to translate in the Y-coordinate.
	 * @return The translated rectangle.
	 */
	public Rectangle translated(int pDeltaX, int pDeltaY)
	{
		return new Rectangle(x + pDeltaX, y + pDeltaY, width, height);
	}
	
	/**
	 * @param pPoint The point to check.
	 * @return True iif pPoint is within the rectangle or on its boundary.
	 * @pre pPoint != null;
	 */
	public boolean contains(Point pPoint)
	{
		assert pPoint != null;
		return pPoint.x() >= x && pPoint.x() <= maxX() &&
				pPoint.y() >= y && pPoint.y() <= maxY();
	}
	
	/**
	 * @param pRectangle The rectangle to check.
	 * @return True iif pRectangle is entired contains within this rectangle.
	 * @pre pRectangle !=null.
	 */
	public boolean contains(Rectangle pRectangle)
	{
		assert pRectangle != null;
		return pRectangle.x >= x && pRectangle.y >= y &&
				pRectangle.maxX() <= maxX() &&
				pRectangle.maxY() <= maxY();
	}
	
	/**
	 * @return A point in the center of this rectangle.
	 */
	public Point center()
	{
		return new Point( x + width/2, y + height/2);
	}
	
	/**
	 * @param pPoint The point to include.
	 * @return A new rectangle that is this rectangle enlarged to include pPoint.
	 * @pre pPoint != null
	 */ 
	public Rectangle add(Point pPoint)
	{
		assert pPoint != null;
		int currentX = x;
		int currentY = y;
		int currentWidth = width;
		int currentHeight = height;
		if( pPoint.x() < x)
		{
			currentX = pPoint.x();
			currentWidth = maxX() - pPoint.x();
		}
		else if( pPoint.x() > maxX())
		{
			currentWidth = pPoint.x() - x;
		}
		if( pPoint.y() < y )
		{
			currentY = pPoint.y();
			currentHeight = maxY() - pPoint.y();
		}
		else if( pPoint.y() > maxY())
		{
			currentHeight = pPoint.y() - y;
		}
		return new Rectangle(currentX, currentY, currentWidth, currentHeight);			
	}
	
	/**
	 * @param pRectangle The rectangle to include.
	 * @return A new rectangle that is this rectangle enlarged to include pRectangle.
	 * @pre pRectangle != null
	 */ 
	public Rectangle add(Rectangle pRectangle)
	{
		assert pRectangle != null;
		int currentX = Math.min(x, pRectangle.x);
		int currentY = Math.min(y, pRectangle.y);
		int maxX = Math.max(maxX(), pRectangle.maxX());
		int maxY = Math.max(maxY(), pRectangle.maxY());
		return new Rectangle(currentX, currentY, maxX - currentX, maxY-currentY);
	}
	
	/**
	 * @return The top left corner of the rectangle.
	 */
	public Point origin()
	{
		return new Point(x, y);
	}
}

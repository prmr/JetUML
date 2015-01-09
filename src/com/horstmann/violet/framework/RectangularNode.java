/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A node that has a rectangular shape.
 */
@SuppressWarnings("serial")
public abstract class RectangularNode extends AbstractNode
{
	private transient Rectangle2D aBounds;

	@Override
	public RectangularNode clone()
	{
		RectangularNode cloned = (RectangularNode) super.clone();
		cloned.aBounds = (Rectangle2D) aBounds.clone();
		return cloned;
	}

	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
      aBounds.setFrame(aBounds.getX() + pDeltaX, aBounds.getY() + pDeltaY, aBounds.getWidth(), aBounds.getHeight());
      super.translate(pDeltaX, pDeltaY);
	}

	@Override
	public boolean contains(Point2D pPoint)
	{
		return aBounds.contains(pPoint);
	}

	@Override
	public Rectangle2D getBounds()
	{
		return (Rectangle2D) aBounds.clone();
	}

	/**
	 * @param pNewBounds The new bounds for this node.
	 */
	public void setBounds(Rectangle2D pNewBounds)
	{
		aBounds = pNewBounds;
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		pGrid.snap(aBounds);
	}

	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		double slope = aBounds.getHeight() / aBounds.getWidth();
		double ex = pDirection.getX();
		double ey = pDirection.getY();
		double x = aBounds.getCenterX();
		double y = aBounds.getCenterY();
      
		if(ex != 0 && -slope <= ey / ex && ey / ex <= slope)
		{  
			// intersects at left or right boundary
			if(ex > 0) 
			{
				x = aBounds.getMaxX();
				y += (aBounds.getWidth() / 2) * ey / ex;
			}
			else
			{
				x = aBounds.getX();
				y -= (aBounds.getWidth() / 2) * ey / ex;
			}
		}
		else if(ey != 0)
		{  
			// intersects at top or bottom
			if(ey > 0) 
			{
				x += (aBounds.getHeight() / 2) * ex / ey;
				y = aBounds.getMaxY();
			}
			else
			{
				x -= (aBounds.getHeight() / 2) * ex / ey;
				y = aBounds.getY();
			}
		}
		return new Point2D.Double(x, y);
	}

//   private void writeObject(ObjectOutputStream out)
//      throws IOException
//   {
//      out.defaultWriteObject();
//      writeRectangularShape(out, aBounds);
//   }

//   /**
//      A helper method to overcome the problem that the 2D shapes
//      aren't serializable. It writes x, y, width and height
//      to the stream.
//      @param out the stream
//      @param s the shape      
//   */
//   private static void writeRectangularShape(
//      ObjectOutputStream out, 
//      RectangularShape s)
//      throws IOException
//   {
//      out.writeDouble(s.getX());
//      out.writeDouble(s.getY());
//      out.writeDouble(s.getWidth());
//      out.writeDouble(s.getHeight());
//   }

//   private void readObject(ObjectInputStream in)
//      throws IOException, ClassNotFoundException
//   {
//      in.defaultReadObject();
//      aBounds = new Rectangle2D.Double();
//      readRectangularShape(in, aBounds);
//   }
   
//   /**
//      A helper method to overcome the problem that the 2D shapes
//      aren't serializable. It reads x, y, width and height
//      from the stream.
//      @param in the stream
//      @param s the shape whose frame is set from the stream values
//   */
//   private static void readRectangularShape(ObjectInputStream in,
//      RectangularShape s)
//      throws IOException
//   {
//      double x = in.readDouble();
//      double y = in.readDouble();
//      double width = in.readDouble();
//      double height = in.readDouble();
//      s.setFrame(x, y, width, height);
//   }

	@Override
	public Shape getShape()
	{
		return aBounds;
	}
}

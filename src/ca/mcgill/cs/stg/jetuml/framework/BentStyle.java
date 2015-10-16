/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * A style for a segmented line that indicates the number
 * and sequence of bends.
 */
public final class BentStyle
{
	public static final BentStyle STRAIGHT = new BentStyle();
	public static final BentStyle HV = new BentStyle();
	public static final BentStyle VH = new BentStyle();
	public static final BentStyle HVH = new BentStyle();
	public static final BentStyle VHV = new BentStyle();
	
	private static final int MIN_SEGMENT = 10;
	private static final int SELF_WIDTH = 30;
	private static final int SELF_HEIGHT = 25;

	private BentStyle() {}
  
	/**
     * Gets the points at which a line joining two rectangles
     * is bent according to this bent style.
     * @param pStart the starting rectangle
     * @param pEnd the ending rectangle
     * @return an array list of points at which to bend the
     * segmented line joining the two rectangles
	 */
	public ArrayList<Point2D> getPath(Rectangle2D pStart, Rectangle2D pEnd)
	{
		ArrayList<Point2D> r = getPath(this, pStart, pEnd);
		if(r != null) 
		{
			return r;
		}
      
		if(pStart.equals(pEnd)) 
		{
		r = getSelfPath(pStart);
		} 
		else if (this == HVH)
		{
			r = getPath(VHV, pStart, pEnd);
		} 
		else if (this == VHV) 
		{
			r = getPath(HVH, pStart, pEnd);
		}
		else if (this == HV) 
		{
		r = getPath(VH, pStart, pEnd);
		} 
		else if (this == VH) 
		{
			r = getPath(HV, pStart, pEnd);
		}
		if (r != null) 
		{
			return r;
		}
		return getPath(STRAIGHT, pStart, pEnd);
   }

	/*
     * Gets the four connecting points at which a bent line
     * connects to a rectangle.
	 */
	private static Point2D[] connectionPoints(Rectangle2D pRectangle)
	{
		return new Point2D[] { new Point2D.Double(pRectangle.getX(), pRectangle.getCenterY()),
							   new Point2D.Double(pRectangle.getMaxX(), pRectangle.getCenterY()),
							   new Point2D.Double(pRectangle.getCenterX(), pRectangle.getY()),
							   new Point2D.Double(pRectangle.getCenterX(), pRectangle.getMaxY())};
	}
   
	/*
     * Gets the points at which a line joining two rectangles
     * is bent according to a bent style.
     * @param start the starting rectangle
     * @param end the ending rectangle
     * @return an array list of points at which to bend the
     * segmented line joining the two rectangles
	 */
	private static ArrayList<Point2D> getPath(BentStyle pBent, Rectangle2D pStart, Rectangle2D pEnd)
	{
	   ArrayList<Point2D> r = null;
	   if(pBent == STRAIGHT)
	   {
		   r = getPathStraight(pStart, pEnd);
	   }
       else if(pBent == HV)
       {
    	  r = getPathHV(pStart, pEnd);
      	}
      	else if(pBent == VH)
      	{
      		r = getPathVH(pStart, pEnd);
      	}
      	else if(pBent == HVH)
      	{
      		r = getPathHVH(pStart, pEnd);
      	}
      	else if(pBent == VHV)
      	{
      		r = getPathVHV(pStart, pEnd);
      	}
	   if(r != null)
	   {
		   return r;
	   }
	   	return null;
	}
	
	/**
	 * @param pStart bounds of starting Node
	 * @param pEnd bounds of endiArrayList<Point2D> r = new ArrayList<>();ng Node
	 * @return An ArrayList of points along the straight path between the nodes.
	 */
	public static ArrayList<Point2D> getPathStraight(Rectangle2D pStart, Rectangle2D pEnd)
	{
		ArrayList<Point2D> r = new ArrayList<>();
		Point2D[] a = connectionPoints(pStart);
	    Point2D[] b = connectionPoints(pEnd);
	    Point2D p = a[0];
	    Point2D q = b[0];
	    double distance = p.distance(q);
	    if(distance == 0)
	    {
		   return null;
	    }
	    for(int i = 0; i < a.length; i++) 
	    {
		   for(int j = 0; j < b.length; j++)
		   {
			   double d = a[i].distance(b[j]);
			   if(d < distance)
			   {
				   p = a[i]; q = b[j];
				   distance = d;
			   }
		   }
	   }
	   r.add(p);
	   r.add(q);
	   return r;
	}
	
	/**
	 * @param pStart bounds of starting Node
	 * @param pEnd bounds of ending Node
	 * @return An ArrayList of points along the HV path between the nodes.
	 */
	public static ArrayList<Point2D> getPathHV(Rectangle2D pStart, Rectangle2D pEnd)
	{
		ArrayList<Point2D> r = new ArrayList<>();
		double x1;
  	    double x2 = pEnd.getCenterX();
  	    double y1 = pStart.getCenterY();
  	    double y2;
  	    if(x2 + MIN_SEGMENT <= pStart.getX()) 
  	    {
  		    x1 = pStart.getX();
  	    }
  	    else if(x2 - MIN_SEGMENT >= pStart.getMaxX()) 
  	    {
  		    x1 = pStart.getMaxX();
  	    }
  	    else 
  	    {
		   	return null;
  	    }
  	    if(y1 + MIN_SEGMENT <= pEnd.getY()) 
  	    {
  		    y2 = pEnd.getY();
  	    } 
  	    else if(y1 - MIN_SEGMENT >= pEnd.getMaxY()) 
  	    {
  		    y2 = pEnd.getMaxY();
  	    }
  	    else
  	    {
		 	return null;
  	    }
  	    r.add(new Point2D.Double(x1, y1));
  	    r.add(new Point2D.Double(x2, y1));
  	    r.add(new Point2D.Double(x2, y2));
  	    return r;
	}
	
	/**
	 * @param pStart bounds of starting Node
	 * @param pEnd bounds of ending Node
	 * @return An ArrayList of points along the VH path between the nodes.
	 */
	public static ArrayList<Point2D> getPathVH(Rectangle2D pStart, Rectangle2D pEnd)
	{
		ArrayList<Point2D> r = new ArrayList<>();
		double x1 = pStart.getCenterX();
  		double x2;
  		double y1;
  		double y2 = pEnd.getCenterY();
  		if(x1 + MIN_SEGMENT <= pEnd.getX()) 
  		{
  			x2 = pEnd.getX();
  		}
  		else if(x1 - MIN_SEGMENT >= pEnd.getMaxX()) 
  		{
  			x2 = pEnd.getMaxX();
  		}
  		else 
  		{
  			return null;
  		}
  		if(y2 + MIN_SEGMENT <= pStart.getY()) 
  		{
			y1 = pStart.getY();
		}
  		else if(y2 - MIN_SEGMENT >= pStart.getMaxY()) 
  		{
  			y1 = pStart.getMaxY();
  		} 
  		else 
  		{
  			return null;
  		}
  		r.add(new Point2D.Double(x1, y1));
  		r.add(new Point2D.Double(x1, y2));
  		r.add(new Point2D.Double(x2, y2));
  		return r;
	}
	
	/**
	 * @param pStart bounds of starting Node
	 * @param pEnd bounds of ending Node
	 * @return An ArrayList of points along the HVH path between the nodes.
	 */
	public static ArrayList<Point2D> getPathHVH(Rectangle2D pStart, Rectangle2D pEnd)
	{
		ArrayList<Point2D> r = new ArrayList<>();
		double x1;
  		double x2;
  		double y1 = pStart.getCenterY();
  		double y2 = pEnd.getCenterY();
  		if(pStart.getMaxX() + 2 * MIN_SEGMENT <= pEnd.getX())
  		{
  			x1 = pStart.getMaxX();
  			x2 = pEnd.getX();
  		}
  		else if(pEnd.getMaxX() + 2 * MIN_SEGMENT <= pStart.getX())
  		{
  			x1 = pStart.getX();
  			x2 = pEnd.getMaxX();
  		}
  		else 
  		{
			return null;
		}
  		if(Math.abs(y1 - y2) <= MIN_SEGMENT)
  		{
  			r.add(new Point2D.Double(x1, y2));
  			r.add(new Point2D.Double(x2, y2));
  		}
  		else
  		{
  			r.add(new Point2D.Double(x1, y1));
  			r.add(new Point2D.Double((x1 + x2) / 2, y1));
  			r.add(new Point2D.Double((x1 + x2) / 2, y2));
  			r.add(new Point2D.Double(x2, y2));
  		}
  		return r;
	}
	
	/**
	 * @param pStart bounds of starting Node
	 * @param pEnd bounds of ending Node
	 * @return An ArrayList of points along the VHV path between the nodes.
	 */
	public static ArrayList<Point2D> getPathVHV(Rectangle2D pStart, Rectangle2D pEnd)
	{
		ArrayList<Point2D> r = new ArrayList<>();
		double x1 = pStart.getCenterX();
  		double x2 = pEnd.getCenterX();
  		double y1;
  		double y2;
  		if(pStart.getMaxY() + 2 * MIN_SEGMENT <= pEnd.getY())
  		{
  			y1 = pStart.getMaxY();
  			y2 = pEnd.getY();
  		}
  		else if(pEnd.getMaxY() + 2 * MIN_SEGMENT <= pStart.getY())
  		{
  			y1 = pStart.getY();
  			y2 = pEnd.getMaxY();
  		}
  		else 
  		{
			return null;
		}
  		if(Math.abs(x1 - x2) <= MIN_SEGMENT)
  		{
  			r.add(new Point2D.Double(x2, y1));
  			r.add(new Point2D.Double(x2, y2));
  		}
  		else
  		{
  			r.add(new Point2D.Double(x1, y1));
  			r.add(new Point2D.Double(x1, (y1 + y2) / 2));
  			r.add(new Point2D.Double(x2, (y1 + y2) / 2));
  			r.add(new Point2D.Double(x2, y2));
  		}
  		return r;
  	}
	
	@Override
	public String toString()
	{
		String returnString;
		if( this == STRAIGHT )
		{
			returnString = "Straight";
		}
		else if( this == HV )
		{
			returnString = "HV";
		}
		else if( this == VH )
		{
			returnString = "VH";
		}
		else if( this == HVH)
		{
			returnString = "HVH";
		}
		else if( this == VHV )
		{
			returnString = "VHV";
		}
		else
		{
			returnString = "Unknown";
		}
		return returnString;
	}

	/*
     * Gets the points at which a line joining two rectangles
     * is bent according to a bent style.
     * @param s the starting and ending rectangle
	 */
	private static ArrayList<Point2D> getSelfPath(Rectangle2D pStart)
	{
		ArrayList<Point2D> r = new ArrayList<>();
		double x1 = pStart.getX() + pStart.getWidth() * 3 / 4;
		double y1 = pStart.getY();
		double y2 = pStart.getY() - SELF_HEIGHT;
		double x2 = pStart.getX() + pStart.getWidth() + SELF_WIDTH;
		double y3 = pStart.getY() + pStart.getHeight() / 4;
		double x3 = pStart.getX() + pStart.getWidth();
		r.add(new Point2D.Double(x1, y1));
		r.add(new Point2D.Double(x1, y2));
		r.add(new Point2D.Double(x2, y2));
		r.add(new Point2D.Double(x2, y3));
		r.add(new Point2D.Double(x3, y3));
		return r;
   }
}

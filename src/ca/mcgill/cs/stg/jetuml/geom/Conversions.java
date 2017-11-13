package ca.mcgill.cs.stg.jetuml.geom;

import java.awt.geom.Point2D;

/**
 * Conversion utilities.
 * 
 * @author Martin P. Robillard
 */
public final class Conversions
{
	private Conversions()
	{}
	
	/**
	 * @param pPoint2D The point to convert.
	 * @return A point created from rounding both x and y coordinate
	 * of the input parameter to integers.
	 */
	public static Point toPoint(Point2D pPoint2D)
	{
		 return new Point( (int)Math.round(pPoint2D.getX()), (int)Math.round(pPoint2D.getY()));
	}
	
	/**
	 * @param pPoint The point to covert.
	 * @return A Point2D with the same coordinates as pPoint.
	 */
	public static Point2D toPoint2D(Point pPoint)
	{
		return new Point2D.Double(pPoint.getX(), pPoint.getY());
	}
}

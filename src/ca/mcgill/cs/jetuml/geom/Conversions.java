package ca.mcgill.cs.jetuml.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
	 * @pre pPoint2D != null;
	 */
	public static Point toPoint(Point2D pPoint2D)
	{
		assert pPoint2D != null;
		return new Point( (int)Math.round(pPoint2D.getX()), (int)Math.round(pPoint2D.getY()));
	}
	
	/**
	 * @param pPoint The point to covert.
	 * @return A Point2D with the same coordinates as pPoint.
	 * @pre pPoint != null;
	 */
	public static Point2D toPoint2D(Point pPoint)
	{
		assert pPoint != null;
		return new Point2D.Double(pPoint.getX(), pPoint.getY());
	}
	
	/**
	 * @param pRectangle2D The rectangle to convert.
	 * @return A rectangle created from rounding both x and y coordinate and the width
	 * and height of the input parameter to integers.
	 * @pre pRectangle2D != null;
	 */
	public static Rectangle toRectangle(Rectangle2D pRectangle2D)
	{
		assert pRectangle2D != null;
		return new Rectangle( (int)Math.round(pRectangle2D.getX()),
							  (int)Math.round(pRectangle2D.getY()),
							  (int)Math.round(pRectangle2D.getWidth()),
							  (int)Math.round(pRectangle2D.getHeight()));
	}
	
	/**
	 * @param pRectangle The rectangle to convert.
	 * @return A rectangle created from both x and y coordinate and the width
	 * and height of the input parameter to integers.
	 * @pre pRectangle != null;
	 */
	public static Rectangle2D toRectangle2D(Rectangle pRectangle)
	{
		assert pRectangle != null;
		return new Rectangle2D.Double( pRectangle.getX(),
							  pRectangle.getY(),
							  pRectangle.getWidth(),
							  pRectangle.getHeight());
	}
}

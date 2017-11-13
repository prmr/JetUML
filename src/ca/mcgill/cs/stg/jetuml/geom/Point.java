package ca.mcgill.cs.stg.jetuml.geom;

/**
 * A framework independent representation of a point in 
 * integer space. Immutable.
 * 
 * @author Martin P. Robillard
 */
public final class Point
{
	private final int aX;
	private final int aY;
	
	/**
	 * Create a new point.
	 * 
	 * @param pX The x-coordinate of the point.
	 * @param pY The y-coordinate of the point.
	 */
	public Point( int pX, int pY)
	{
		aX = pX;
		aY = pY;
	}
	
	/**
	 * @return The X-coordinate.
	 */
	public int getX()
	{
		return aX;
	}
	
	/**
	 * @return The Y-coordinate.
	 */
	public int getY()
	{
		return aY;
	}
	
	/**
	 * @param pPoint Another point.
	 * @return The distance between two points.
	 */
	public double distance(Point pPoint)
	{
		int a = pPoint.aY - aY;
		int b = pPoint.aX - aX;
		return Math.sqrt(a*a + b*b);
	}
	
	@Override
	public String toString()
	{
		return String.format("(x=%d,y=%d)", aX, aY);
	}
}

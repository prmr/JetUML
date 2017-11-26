package ca.mcgill.cs.jetuml.geom;

/**
 * A framework independent representation of a point in 
 * integer space. Immutable.
 * 
 * @author Martin P. Robillard
 */
public final class Point implements Cloneable
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
	 * Create a new point by converting the coordinates
	 * by rounding them to the closest integer value.
	 * 
	 * @param pX The x-coordinate of the point.
	 * @param pY The y-coordinate of the point.
	 */
	public Point( double pX, double pY )
	{
		aX = (int) Math.round(pX);
		aY = (int) Math.round(pY);
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
	 * @pre pPoint != null
	 */
	public double distance(Point pPoint)
	{
		assert pPoint != null;
		int a = pPoint.aY - aY;
		int b = pPoint.aX - aX;
		return Math.sqrt(a*a + b*b);
	}
	
	@Override
	public Point clone()
	{
		try
		{
			return (Point) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("(x=%d,y=%d)", aX, aY);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
		Point other = (Point) pObject;
		return aX == other.aX && aY == other.aY;
	}
}

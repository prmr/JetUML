package ca.mcgill.cs.stg.jetuml.geom;

/**
 * An immutable pair of points in the integer space.
 * 
 * @author Martin P. Robillard
 */
public class Line implements Cloneable
{
	private final Point aPoint1;
	private final Point aPoint2;
	
	/**
	 * Creates a new line.
	 * 
	 * @param pPoint1 The first point in the line. Conceptually the "start" of the line.
	 * @param pPoint2 The second point in the line. Conceptually the "end" of the line.
	 * @pre pPoint1 != null
	 * @pre pPoint2 != null
	 */
	public Line(Point pPoint1, Point pPoint2)
	{
		assert pPoint1 != null && pPoint2 != null;
		aPoint1 = pPoint1; 
		aPoint2 = pPoint2;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + aPoint1.hashCode();
		result = prime * result + aPoint2.hashCode();
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
		Line other = (Line) pObject;
		return aPoint1.equals(other.aPoint1) && aPoint2.equals(other.aPoint2);
	}

	/**
	 * @return The first point of the line.
	 */
	public Point getPoint1()
	{
		return aPoint1;
	}
	
	/**
	 * @return The x coordinate of point 1.
	 */
	public int getX1()
	{
		return aPoint1.getX();
	}
	
	/**
	 * @return The y coordinate of point 1.
	 */
	public int getY1()
	{
		return aPoint1.getY();
	}
	
	/**
	 * @return The x coordinate of point 2.
	 */
	public int getX2()
	{
		return aPoint2.getX();
	}
	
	/**
	 * @return The y coordinate of point 2.
	 */
	public int getY2()
	{
		return aPoint2.getY();
	}
	
	/**
	 * @return The second point of the line.
	 */
	public Point getPoint2()
	{
		return aPoint2;
	}
	
	/* 
	 * Shallow clone since points are immutable.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Line clone()
	{
		try
		{
			return (Line) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("[%s, %s]", aPoint1, aPoint2);
	}
}

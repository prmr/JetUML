package ca.mcgill.cs.jetuml.geom;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Represents the path of an edge on a diagram as a list of points. 
 * Non-segmented paths consist of 2 points (the start and end points).
 */
public class EdgePath implements Iterable<Point>
{
	private List<Point> aPoints;
	
	/**
	 * Initializes an EdgePath composed of pPoints.
	 * @param pPoints the points of an edge (the start point, possible segment connections, and the end point)
	 * @pre pPoints.length >= 2
	 */
	public EdgePath(Point...pPoints)
	{
		assert pPoints.length >= 2;
		aPoints = Arrays.asList(pPoints);
	}
	
	/**
	 * Constructor for EdgePath using lines as arguments.
	 * @param pLines the line segment(s) which compose the path.
	 * @pre pLines.length() > 0
	 */
	public EdgePath(Line...pLines)
	{
		assert pLines.length > 0;
		aPoints.add(pLines[0].getPoint1());
		for (Line line : pLines)
		{
			aPoints.add(line.getPoint2());
		}
	}
	

	/**
	 * Gets the starting point for the path.
	 * @return the Point where the edge starts.
	 */
	public Point getStartPoint()
	{
		return aPoints.get(0);
	}
	
	/**
	 * Gets the end point of the edge.
	 * @return the Point where the edge ends.
	 */
	public Point getEndPoint()
	{
		return aPoints.get(aPoints.size()-1);
	}

	/**
	 * Returns the point in the edge path at position pIndex, where index 0 refers to the start point.
	 * @param pIndex the index of aPoints
	 * @return the Point in aPoints at pIndex < aPoints.size()
	 * @pre pIndex > 0 && pIndex
	 */
	public Point getPointByIndex(int pIndex)
	{
		assert pIndex >= 0 && pIndex < aPoints.size();
		return aPoints.get(pIndex);
	}

	@Override
	public int hashCode() 
	{
		return Objects.hash(aPoints);
	}

	@Override
	public boolean equals(Object pObj) 
	{
		if (this == pObj)
		{
			return true;
		}
		if (pObj == null)
		{
			return false;
		}
		if (getClass() != pObj.getClass())
		{
			return false;
		}
		EdgePath other = (EdgePath) pObj;
		if (other.aPoints.size() != this.aPoints.size())
		{
			return false;
		}
		for (int i = 0; i < aPoints.size(); i++)
		{
			if (!other.aPoints.get(i).equals(aPoints.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the number of points in the path.
	 * @return an integer representing the size of the EdgePath
	 */
	public int size()
	{
		return aPoints.size();
	}

	@Override
	public Iterator<Point> iterator() 
	{
		return aPoints.iterator();
	}
	
	
}
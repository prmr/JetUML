package ca.mcgill.cs.jetuml.geom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the EdgePath class
 */
public class TestEdgePath 
{
	private final Point pointA = new Point(0,0);
	private final Point pointB = new Point(200,0);
	private final Point pointC = new Point(200,200);
	private final Point pointD = new Point(300,200);
	private EdgePath aEdgePath;
	
	@BeforeEach
	public void setUp()
	{
		aEdgePath = new EdgePath(pointA, pointB, pointC, pointD);
	}
	
	@Test
	public void testGetStartPoint()
	{
		assertSame(pointA, aEdgePath.getStartPoint());
	}
	
	@Test
	public void testGetEndPoint()
	{
		assertSame(pointD, aEdgePath.getEndPoint());
	}
	
	@Test
	public void testGetPointByIndex()
	{
		assertSame(pointA, aEdgePath.getPointByIndex(0));
		assertSame(pointB, aEdgePath.getPointByIndex(1));
		assertSame(pointC, aEdgePath.getPointByIndex(2));
		assertSame(pointD, aEdgePath.getPointByIndex(3));
	}
	
	
	@Test
	public void testEquals()
	{
		EdgePath samePath = new EdgePath(pointA, pointB, new Point(200, 200), pointD);
		EdgePath reversePath = new EdgePath(pointD, pointC, pointB, pointA);
		EdgePath nullEdgePath = null;
		assertTrue(samePath.equals(samePath));
		assertTrue(samePath.equals(aEdgePath));
		assertFalse(reversePath.equals(aEdgePath));
		assertFalse(samePath.equals(nullEdgePath));
		assertFalse(samePath.equals(new EdgePath(pointA, pointB, new Point(200, 200))));
	}
	
	
	
	 @Test
	 public void testSize()
	 {
		 assertTrue(aEdgePath.size() == 4);
	 }
	 
	
}
package ca.mcgill.cs.jetuml.geom;

import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Point;

import static org.junit.Assert.*;

public class TestPoint
{
	private static final Point ZERO = new Point(0,0);
	private static final Point ONE = new Point(1,1);
	private static final Point M_ONE = new Point(-1, -1);
	
	@Test
	public void testToString()
	{
		assertEquals("(x=0,y=0)", ZERO.toString());
		assertEquals("(x=-1,y=-1)", M_ONE.toString());
	}
	
	@Test
	public void testDistance()
	{
		assertEquals(0, ZERO.distance(ZERO), 0);
		assertEquals(1.4142, ZERO.distance(ONE), 0.0001);
		assertEquals(2.8284, ONE.distance(M_ONE),0.0001);
	}
	
	@Test 
	public void testDoubleConstructor()
	{
		Point point = new Point(0.0, 0.0);
		assertEquals(0, point.getX());
		assertEquals(0, point.getY());
		
		point = new Point(1.1,-1.1);
		assertEquals(1, point.getX());
		assertEquals(-1, point.getY());
		
		point = new Point(1.9,-1.9);
		assertEquals(2, point.getX());
		assertEquals(-2, point.getY());
	}
	
	@Test
	public void testClone()
	{
		Point point = new Point(0,0);
		Point clone = point.clone();
		assertTrue( clone != point );
		assertEquals( point.getX(), clone.getX());
		assertEquals( point.getY(), clone.getY());
	}
	
	@Test
	public void testHashCode()
	{
		assertEquals(961, new Point(0,0).hashCode());
		assertEquals(4161, new Point(100,100).hashCode());
		assertEquals(-2239, new Point(-100,-100).hashCode());
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals()
	{
		Point point1 = new Point(0,0);
		Point point2 = new Point(0,0);
		assertTrue(point1.equals(point1));
		assertTrue(point1.equals(point2));
		assertTrue(point2.equals(point1));
		assertFalse(point1.equals(null));
		assertFalse(point1.equals(new Point(1,1)));
		assertFalse(point1.equals("Foo"));
		assertFalse(point1.equals(new Point(0,1)));
		assertFalse(point1.equals(new Point(1,0)));
	}
}

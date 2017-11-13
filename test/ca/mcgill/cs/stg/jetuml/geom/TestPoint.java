package ca.mcgill.cs.stg.jetuml.geom;

import org.junit.Test;

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
}

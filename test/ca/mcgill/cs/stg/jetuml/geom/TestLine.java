package ca.mcgill.cs.stg.jetuml.geom;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestLine
{
	private static final Point ZERO = new Point(0,0);
	private static final Point ONE = new Point(1,1);
	private static final Line ZERO_TO_ONE = new Line(ZERO, ONE);
	
	@Test
	public void testToString()
	{
		assertEquals("[(x=0,y=0), (x=1,y=1)]", ZERO_TO_ONE.toString());
	}
	
	@Test
	public void testHashCode()
	{
		assertEquals(31745, ZERO_TO_ONE.hashCode());
	}
	
	@Test
	public void testClone()
	{
		Line clone = ZERO_TO_ONE.clone();
		assertFalse( clone == ZERO_TO_ONE);
		assertTrue( ZERO_TO_ONE.getPoint1() == clone.getPoint1() );
		assertTrue( ZERO_TO_ONE.getPoint2() == clone.getPoint2() );
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals()
	{
		assertTrue(ZERO_TO_ONE.equals(ZERO_TO_ONE));
		assertTrue(ZERO_TO_ONE.equals(ZERO_TO_ONE.clone()));
		assertFalse(ZERO_TO_ONE.equals(new Line(ONE, ZERO)));
		assertFalse(ZERO_TO_ONE.equals(null));
		assertFalse(ZERO_TO_ONE.equals("Foo"));
		assertFalse(ZERO_TO_ONE.equals(new Line(ZERO, ZERO)));
		assertFalse(ZERO_TO_ONE.equals(new Line(ONE, ONE)));
	}
	
	@Test
	public void testPositions()
	{
		assertEquals(0, ZERO_TO_ONE.getX1());
		assertEquals(0, ZERO_TO_ONE.getY1());
		assertEquals(1, ZERO_TO_ONE.getX2());
		assertEquals(1, ZERO_TO_ONE.getY2());
	}
}

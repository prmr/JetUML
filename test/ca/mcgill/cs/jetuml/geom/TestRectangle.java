package ca.mcgill.cs.jetuml.geom;

import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

import static org.junit.Assert.*;

public class TestRectangle
{
	private static final Rectangle RECTANGLE_1 = new Rectangle(0,0,60,40);
	private static final Rectangle RECTANGLE_2 = new Rectangle(100,20,1,1);
	
	@Test
	public void testToString()
	{
		assertEquals("[x=0, y=0, w=60, h=40]", RECTANGLE_1.toString());
	}
	
	@Test
	public void testMaxXY()
	{
		assertEquals(60, RECTANGLE_1.getMaxX());
		assertEquals(40, RECTANGLE_1.getMaxY());
		assertEquals(101, RECTANGLE_2.getMaxX());
		assertEquals(21, RECTANGLE_2.getMaxY());
	}
	
	@Test
	public void testHashCode()
	{
		assertEquals(2172821, RECTANGLE_1.hashCode());
		assertEquals(957393, RECTANGLE_2.hashCode());
	}
	
	@Test
	public void tesEquals()
	{
		assertTrue(RECTANGLE_1.equals(RECTANGLE_1));
		assertFalse(RECTANGLE_1.equals(null));
		assertTrue(RECTANGLE_1.equals(new Rectangle(0,0,60,40)));
		assertFalse(RECTANGLE_1.equals(RECTANGLE_2));
	}
	
	@Test
	public void testTranslated()
	{
		assertEquals(new Rectangle(10,20,60,40), RECTANGLE_1.translated(10, 20));
		assertEquals(new Rectangle(-10,-20,60,40), RECTANGLE_1.translated(-10, -20));
	}
	
	@Test
	public void testContainsPoint()
	{
		assertTrue(RECTANGLE_1.contains(new Point(10,20)));
		assertTrue(RECTANGLE_1.contains(new Point(0,0)));
		assertTrue(RECTANGLE_1.contains(new Point(60,0)));
		assertTrue(RECTANGLE_1.contains(new Point(0,40)));
		assertFalse(RECTANGLE_1.contains(new Point(0,41)));
	}
	
	@Test
	public void testGetCenter()
	{
		Point center = RECTANGLE_1.getCenter();
		assertEquals(30, center.getX());
		assertEquals(20, center.getY());
		center = RECTANGLE_1.translated(10, 10).getCenter();
		assertEquals(40, center.getX());
		assertEquals(30, center.getY());
		center = RECTANGLE_2.getCenter();
		assertEquals(100, center.getX());
		assertEquals(20, center.getY());
	}
	
	@Test
	public void testAddPoint()
	{
		Rectangle rectangle = new Rectangle(10,10,0,0);
		rectangle = rectangle.add(new Point(20,30));
		assertEquals( new Rectangle(10,10,10,20), rectangle);
		rectangle = rectangle.add(new Point(15,15));
		assertEquals( new Rectangle(10,10,10,20), rectangle);
		rectangle = rectangle.add(new Point(100,100));
		assertEquals( new Rectangle(10,10,90,90), rectangle);
		rectangle = rectangle.add(new Point(0,0));
		assertEquals( new Rectangle(0,0,100,100), rectangle);
	}
	
	@Test
	public void testAddRectangle()
	{
		Rectangle rectangle = new Rectangle(10,10,0,0);
		rectangle = rectangle.add( new Rectangle(0,0,20,20));
		assertEquals( new Rectangle(0,0,20,20), rectangle);
	}
}

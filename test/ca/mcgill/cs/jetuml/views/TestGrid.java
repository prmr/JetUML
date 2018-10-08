package ca.mcgill.cs.jetuml.views;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestGrid
{
	@Test
	public void testSnapped_ToTopLeft()
	{
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,1)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,2)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,3)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,4)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,5)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(1,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(2,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(3,0)));
		assertEquals(new Point(0,0), Grid.snapped(new Point(4,0)));
		assertEquals(new Point(10,0), Grid.snapped(new Point(5,0)));
	}
	
	@Test
	public void testSnapped_ToTopRight()
	{
		assertEquals(new Point(0,0), Grid.snapped(new Point(0,4)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,5)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,7)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,8)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,9)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,10)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(0,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(1,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(2,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(3,6)));
		assertEquals(new Point(0,10), Grid.snapped(new Point(4,6)));
		assertEquals(new Point(10,10), Grid.snapped(new Point(5,6)));
	}
	
	@Test
	public void testToSnap_origin()
	{
		assertEquals(new Point(0,0), Grid.toSnap(new Dimension(0,0)));
	}
	
	@Test
	public void testToSnap_other()
	{
		assertEquals(new Point(0,0), Grid.toSnap(new Dimension(20,20)));
		assertEquals(new Point(-1,-1), Grid.toSnap(new Dimension(21,21)));
		assertEquals(new Point(-3,-3), Grid.toSnap(new Dimension(3,3)));
		assertEquals(new Point(5,5), Grid.toSnap(new Dimension(55,55)));
		assertEquals(new Point(5,-1), Grid.toSnap(new Dimension(55,51)));
		assertEquals(new Point(4,4), Grid.toSnap(new Dimension(36,36)));
		assertEquals(new Point(3,3), Grid.toSnap(new Dimension(37,37)));
		assertEquals(new Point(2,2), Grid.toSnap(new Dimension(38,38)));
		assertEquals(new Point(1,1), Grid.toSnap(new Dimension(39,39)));
		assertEquals(new Point(0,0), Grid.toSnap(new Dimension(40,40)));
	}
}

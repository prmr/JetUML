package ca.mcgill.cs.stg.jetuml.framework;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;

import static org.junit.Assert.assertEquals;

public class TestSegmentationStyleFactory
{
	private Method aMethod;
	
	@Before
	public void setup() throws Exception
	{
		aMethod = SegmentationStyleFactory.class.getDeclaredMethod("computeDirection", Node.class, Node.class);
		aMethod.setAccessible(true);
	}
	
	@Test
	public void testComputeDirectionEast1() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 100);
		end.translate(200, 100);
		assertEquals(Direction.EAST, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionEast2() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 100);
		end.translate(200, 125);
		assertEquals(Direction.EAST, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionEast3() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 100);
		end.translate(200, 75);
		assertEquals(Direction.EAST, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionWest1() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(200, 100);
		end.translate(0, 100);
		assertEquals(Direction.WEST, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionWest2() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(200, 100);
		end.translate(0, 125);
		assertEquals(Direction.WEST, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionWest3() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(200, 100);
		end.translate(0, 75);
		assertEquals(Direction.WEST, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionNorth1() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 200);
		end.translate(100, 100);
		assertEquals(Direction.NORTH, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionNorth2() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 200);
		end.translate(75, 100);
		assertEquals(Direction.NORTH, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionNorth3() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 200);
		end.translate(125, 100);
		assertEquals(Direction.NORTH, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionSouth1() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 100);
		end.translate(100, 200);
		assertEquals(Direction.SOUTH, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionSouth2() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 100);
		end.translate(75, 200);
		assertEquals(Direction.SOUTH, aMethod.invoke(null, start, end));
	}
	
	@Test
	public void testComputeDirectionSouth3() throws Exception
	{
		Node start = new ClassNode();
		Node end = new ClassNode();
		start.translate(100, 100);
		end.translate(125, 200);
		assertEquals(Direction.SOUTH, aMethod.invoke(null, start, end));
	}
}

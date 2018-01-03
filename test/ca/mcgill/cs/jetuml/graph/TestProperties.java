package ca.mcgill.cs.jetuml.graph;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class TestProperties
{
	static class Stub { String aValue = ""; }
	private Stub aStub;
	private Properties aProperties;
	
	@Before
	public void setup()
	{
		aStub = new Stub();
		aProperties = new Properties();
	}
	
	@Test
	public void testEmpty()
	{
		assertFalse(aProperties.iterator().hasNext());
	}
	
	@Test
	public void testAddOne()
	{
		aProperties.add("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddTwo()
	{
		aProperties.add("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
		
		aProperties.add("test2", () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "X");
		assertEquals(2, size());
		prop = aProperties.get("test2");
		assertEquals("test2", prop.getName());
		assertEquals("X", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddTwiceSame()
	{
		aProperties.add("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test", () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "X");
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddInvisible()
	{
		aProperties.addInvisible("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertFalse(prop.isVisible());
	}
	
	@Test
	public void testAddInvisibleTwice()
	{
		aProperties.addInvisible("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addInvisible("test", () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "Y");
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertFalse(prop.isVisible());
	}
	
	@Test
	public void testAddVisibleInvisible()
	{
		aProperties.add("visible", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addInvisible("invisible", () -> "INVISIBLE", val -> {});
		assertEquals(2, size());
		Property prop = aProperties.get("visible");
		assertEquals("visible", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
		prop = aProperties.get("invisible");
		assertEquals("invisible", prop.getName());
		assertEquals("INVISIBLE", prop.get());
		assertFalse(prop.isVisible());
	}
	
	@Test
	public void testAddAt0()
	{
		aProperties.addAt("test", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertEquals(1, size());
		Property prop = aProperties.iterator().next();
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddAt0of2()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertEquals(2, size());
		Iterator<Property> iterator = aProperties.iterator();
		assertEquals("test2", iterator.next().getName());
		assertEquals("test1", iterator.next().getName());
	}
	
	@Test
	public void testAddAt1of2()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		assertEquals(2, size());
		Iterator<Property> iterator = aProperties.iterator();
		assertEquals("test1", iterator.next().getName());
		assertEquals("test2", iterator.next().getName());
	}
	
	@Test
	public void testAddAt0of3()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertEquals(3, size());
		Iterator<Property> iterator = aProperties.iterator();
		assertEquals("test3", iterator.next().getName());
		assertEquals("test1", iterator.next().getName());
		assertEquals("test2", iterator.next().getName());
	}
	
	@Test
	public void testAddAt1of3()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		assertEquals(3, size());
		Iterator<Property> iterator = aProperties.iterator();
		assertEquals("test1", iterator.next().getName());
		assertEquals("test3", iterator.next().getName());
		assertEquals("test2", iterator.next().getName());
	}
	
	@Test
	public void testAddAt2of3()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 2);
		assertEquals(3, size());
		Iterator<Property> iterator = aProperties.iterator();
		assertEquals("test1", iterator.next().getName());
		assertEquals("test2", iterator.next().getName());
		assertEquals("test3", iterator.next().getName());
	}
	
	@Test
	public void testAddAt1of3AndSome()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		aProperties.add("test4", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(4, size());
		Iterator<Property> iterator = aProperties.iterator();
		assertEquals("test1", iterator.next().getName());
		assertEquals("test3", iterator.next().getName());
		assertEquals("test2", iterator.next().getName());
		assertEquals("test4", iterator.next().getName());
	}
	
	@Test
	public void testAddAtTwice()
	{
		aProperties.addAt("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		aProperties.addAt("test1", () -> "X", val -> aStub.aValue = "Y", 0);
		assertEquals(1, size());
		Property prop = aProperties.get("test1");
		assertEquals("test1", prop.getName());
		assertEquals("", prop.get());
	}
	
	private int size()
	{
		int size = 0;
		for (Iterator<Property> iterator = aProperties.iterator(); iterator.hasNext();)
		{
			iterator.next();
			size++;
		}
		return size;
	}

}

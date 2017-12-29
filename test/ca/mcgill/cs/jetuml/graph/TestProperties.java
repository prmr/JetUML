package ca.mcgill.cs.jetuml.graph;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.application.MultiLineString;

public class TestProperties
{
	private Properties aProperties;
	private static final Consumer<Object> CONSUMER = (e) -> {};
	
	@Before
	public void setup()
	{
		aProperties = new Properties();
	}
	
	@Test
	public void testBasicPut()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> 100, CONSUMER);
		aProperties.put("C", () -> false, CONSUMER);
		aProperties.put("D", () -> MultiLineString.Align.CENTER, CONSUMER);
		assertEquals(4, size());
		assertEquals("A", (String) aProperties.get("A"));
		assertEquals(100, (int) aProperties.get("B"));
		assertEquals(false, (boolean) aProperties.get("C"));
		assertEquals(MultiLineString.Align.CENTER, (MultiLineString.Align) aProperties.get("D"));
	}
	
	@Test
	public void testInsertionOrderBasic()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> 100, CONSUMER);
		aProperties.put("C", () -> false, CONSUMER);
		aProperties.put("D", () -> MultiLineString.Align.CENTER, CONSUMER);
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
		assertEquals("C", iterator.next());
		assertEquals("D", iterator.next());
	}
	
	@Test
	public void testInsertionOrderWithReinsertion()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> 100, CONSUMER);
		aProperties.put("C", () -> false, CONSUMER);
		aProperties.put("D", () -> MultiLineString.Align.CENTER, CONSUMER);
		aProperties.put("A", () -> "AA", CONSUMER);
		aProperties.put("B", () -> 10000, CONSUMER);
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
		assertEquals("C", iterator.next());
		assertEquals("D", iterator.next());
	}
	
	@Test
	public void testPutAt1()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> 100, CONSUMER);
		aProperties.put("C", () -> false, CONSUMER, 0);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}
	
	@Test
	public void testPutAt2()
	{
		aProperties.put("A", () -> "A", CONSUMER );
		aProperties.put("B", () -> 100, CONSUMER );
		aProperties.put("C", () -> false, CONSUMER, 0);
		aProperties.put("D", () -> false, CONSUMER, 1);
		assertEquals(4, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("D", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}
	
	@Test
	public void testPutAt3()
	{
		aProperties.put("A", () -> "A", CONSUMER, 0);
		assertEquals(1, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
	}
	
	@Test
	public void testPutAt4()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> "B", CONSUMER);
		aProperties.put("C", () -> "C", CONSUMER, 2);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
		assertEquals("C", iterator.next());
	}
	
	@Test
	public void testPutAtWithReinsertion1()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> "B", CONSUMER);
		aProperties.put("C", () -> "C", CONSUMER, 0);
		aProperties.put("C", () -> "C", CONSUMER);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}
	
	@Test
	public void testPutAtWithReinsertion2()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		aProperties.put("B", () -> "B", CONSUMER);
		aProperties.put("C", () -> "C", CONSUMER, 0);
		aProperties.put("C", () -> "C", CONSUMER, 1);
		aProperties.put("A", () -> "A", CONSUMER, 0);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}

	@Test
	public void testPutOverride()
	{
		aProperties.put("A", () -> "A", CONSUMER);
		assertEquals(1, size());
		assertEquals("A", (String) aProperties.get("A"));
		aProperties.put("A", () -> 100, CONSUMER );
		assertEquals(1, size());
		assertEquals(100, (int) aProperties.get("A"));
		aProperties.put("A", () -> false, CONSUMER);
		assertEquals(1, size());
		assertEquals(false, (boolean) aProperties.get("A"));
		aProperties.put("A", () -> MultiLineString.Align.CENTER, CONSUMER);
		assertEquals(1, size());
		assertEquals(MultiLineString.Align.CENTER, (MultiLineString.Align) aProperties.get("A"));
	}
	
	private int size()
	{
		int size = 0;
		for( @SuppressWarnings("unused") String s : aProperties )
		{
			size++;
		}
		return size;
	}

}

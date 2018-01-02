package ca.mcgill.cs.jetuml.graph;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.views.ArrowHead;

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
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> 100, CONSUMER);
		aProperties.add("C", () -> false, CONSUMER);
		aProperties.add("D", () -> ArrowHead.DIAMOND, CONSUMER);
		assertEquals(4, size());
		assertEquals("A", (String) aProperties.get("A"));
		assertEquals(100, (int) aProperties.get("B"));
		assertEquals(false, (boolean) aProperties.get("C"));
		assertEquals(ArrowHead.DIAMOND, (ArrowHead) aProperties.get("D"));
	}
	
	@Test
	public void testInsertionOrderBasic()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> 100, CONSUMER);
		aProperties.add("C", () -> false, CONSUMER);
		aProperties.add("D", () -> ArrowHead.DIAMOND, CONSUMER);
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
		assertEquals("C", iterator.next());
		assertEquals("D", iterator.next());
	}
	
	@Test
	public void testInsertionOrderWithReinsertion()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> 100, CONSUMER);
		aProperties.add("C", () -> false, CONSUMER);
		aProperties.add("D", () -> ArrowHead.DIAMOND, CONSUMER);
		aProperties.add("A", () -> "AA", CONSUMER);
		aProperties.add("B", () -> 10000, CONSUMER);
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
		assertEquals("C", iterator.next());
		assertEquals("D", iterator.next());
	}
	
	@Test
	public void testPutAt1()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> 100, CONSUMER);
		aProperties.addAt("C", () -> false, CONSUMER, 0);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}
	
	@Test
	public void testPutAt2()
	{
		aProperties.add("A", () -> "A", CONSUMER );
		aProperties.add("B", () -> 100, CONSUMER );
		aProperties.addAt("C", () -> false, CONSUMER, 0);
		aProperties.addAt("D", () -> false, CONSUMER, 1);
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
		aProperties.addAt("A", () -> "A", CONSUMER, 0);
		assertEquals(1, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
	}
	
	@Test
	public void testPutAt4()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> "B", CONSUMER);
		aProperties.addAt("C", () -> "C", CONSUMER, 2);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
		assertEquals("C", iterator.next());
	}
	
	@Test
	public void testPutAtWithReinsertion1()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> "B", CONSUMER);
		aProperties.addAt("C", () -> "C", CONSUMER, 0);
		aProperties.add("C", () -> "C", CONSUMER);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}
	
	@Test
	public void testPutAtWithReinsertion2()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		aProperties.add("B", () -> "B", CONSUMER);
		aProperties.addAt("C", () -> "C", CONSUMER, 0);
		aProperties.addAt("C", () -> "C", CONSUMER, 1);
		aProperties.addAt("A", () -> "A", CONSUMER, 0);
		assertEquals(3, size());
		Iterator<String> iterator = aProperties.iterator();
		assertEquals("C", iterator.next());
		assertEquals("A", iterator.next());
		assertEquals("B", iterator.next());
	}

	@Test
	public void testPutOverride()
	{
		aProperties.add("A", () -> "A", CONSUMER);
		assertEquals(1, size());
		assertEquals("A", (String) aProperties.get("A"));
		aProperties.add("A", () -> 100, CONSUMER );
		assertEquals(1, size());
		assertEquals(100, (int) aProperties.get("A"));
		aProperties.add("A", () -> false, CONSUMER);
		assertEquals(1, size());
		assertEquals(false, (boolean) aProperties.get("A"));
		aProperties.add("A", () -> ArrowHead.DIAMOND, CONSUMER);
		assertEquals(1, size());
		assertEquals(ArrowHead.DIAMOND, (ArrowHead) aProperties.get("A"));
	}
	
	@Test
	public void testModifications()
	{
		class Stub { int aNumber = 0; }
		Stub stub = new Stub();
		aProperties.add("A", () -> stub.aNumber, number -> stub.aNumber = (int) number);
		assertEquals(0, aProperties.get("A"));
		aProperties.set("A", 28);
		assertEquals(28, aProperties.get("A"));
		assertEquals(28, stub.aNumber);
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

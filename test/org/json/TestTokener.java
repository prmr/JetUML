package org.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestTokener
{
	private static final String TEST = "{\n   \"name\": \"Jo\",\n   \"age\": 27\n}";
	
	private final JSONTokener aTokener = new JSONTokener(TEST);
	
	@Test
	void testNextClean()
	{
		assertEquals('{', aTokener.nextClean());
		assertEquals('"', aTokener.nextClean());
		assertEquals('n', aTokener.nextClean());
		assertEquals('a', aTokener.nextClean());
		assertEquals('m', aTokener.nextClean());
		assertEquals('e', aTokener.nextClean());
		assertEquals('"', aTokener.nextClean());
		assertEquals(':', aTokener.nextClean());
		assertEquals('"', aTokener.nextClean());
		assertEquals('J', aTokener.nextClean());
		assertEquals('o', aTokener.nextClean());
		assertEquals('"', aTokener.nextClean());
		assertEquals(',', aTokener.nextClean());
		assertEquals('"', aTokener.nextClean());
		assertEquals('a', aTokener.nextClean());
		assertEquals('g', aTokener.nextClean());
		assertEquals('e', aTokener.nextClean());
		assertEquals('"', aTokener.nextClean());
		assertEquals(':', aTokener.nextClean());
		assertEquals('2', aTokener.nextClean());
		assertEquals('7', aTokener.nextClean());
		assertEquals('}', aTokener.nextClean());
		assertFalse(aTokener.hasNext());
	}
	
	@Test
	void testNext()
	{
		assertEquals('{', aTokener.next());
		assertEquals(10, aTokener.next()); // New line
		assertEquals(32, aTokener.next()); // Space
		assertEquals(32, aTokener.next()); // Space
		assertEquals(32, aTokener.next()); // Space
		assertEquals('"', aTokener.next());
		assertEquals('n', aTokener.next());
		assertEquals('a', aTokener.next());
		assertEquals('m', aTokener.next());
		assertEquals('e', aTokener.next());
		assertEquals('"', aTokener.next());
		assertEquals(':', aTokener.next());
		assertEquals(32, aTokener.next()); // Space
		assertEquals('"', aTokener.next());
		assertEquals('J', aTokener.next());
		assertEquals('o', aTokener.next());
		assertEquals('"', aTokener.next());
		assertEquals(',', aTokener.next());
		assertEquals(10, aTokener.next()); // New line
		assertEquals(32, aTokener.next()); // Space
		assertEquals(32, aTokener.next()); // Space
		assertEquals(32, aTokener.next()); // Space
		assertEquals('"', aTokener.next());
		assertEquals('a', aTokener.next());
		assertEquals('g', aTokener.next());
		assertEquals('e', aTokener.next());
		assertEquals('"', aTokener.next());
		assertEquals(':', aTokener.next());
		assertEquals(32, aTokener.next()); // Space
		assertEquals('2', aTokener.next());
		assertEquals('7', aTokener.next());
		assertEquals(10, aTokener.next()); // New line
		assertEquals('}', aTokener.next());
		assertFalse(aTokener.hasNext());
	}
	
	@Test
	void testCanBackUp_Yes()
	{
		aTokener.next();
		aTokener.next();
		assertTrue(aTokener.canBackUp());
	}
	
	@Test
	void testCanBackUp_No()
	{
		assertFalse(aTokener.canBackUp());
	}
	
	@Test
	void testBack_Normal()
	{
		aTokener.next();
		aTokener.next();
		aTokener.next();
		aTokener.next();
		aTokener.next();
		aTokener.next();
		aTokener.next(); // Now next is 'a'
		aTokener.backUp();  // Now next is 'n'
		assertEquals('n', aTokener.next());
	}
	
	@Test
	void testNext_One()
	{
		JSONTokener tokener = new JSONTokener("a");
		assertEquals('a', tokener.next());
		assertFalse(tokener.hasNext());
	}
	
	@Test
	void testNext_Two()
	{
		JSONTokener tokener = new JSONTokener("ab");
		assertEquals('a', tokener.next());
		assertEquals('b', tokener.next());
		assertFalse(tokener.hasNext());
	}
}

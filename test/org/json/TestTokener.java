package org.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestTokener
{
	private static final String TEST = "{\n   \"name\": \"Jo\",\n   \"age\": 27\n}";
	
	private final JSONTokener aTokener = new JSONTokener(TEST);
	
	@Test
	void testNextClean()
	{
		assertEquals('{', nextNonWhitespace(aTokener));
		assertEquals('"', nextNonWhitespace(aTokener));
		assertEquals('n', nextNonWhitespace(aTokener));
		assertEquals('a', nextNonWhitespace(aTokener));
		assertEquals('m', nextNonWhitespace(aTokener));
		assertEquals('e', nextNonWhitespace(aTokener));
		assertEquals('"', nextNonWhitespace(aTokener));
		assertEquals(':', nextNonWhitespace(aTokener));
		assertEquals('"', nextNonWhitespace(aTokener));
		assertEquals('J', nextNonWhitespace(aTokener));
		assertEquals('o', nextNonWhitespace(aTokener));
		assertEquals('"', nextNonWhitespace(aTokener));
		assertEquals(',', nextNonWhitespace(aTokener));
		assertEquals('"', nextNonWhitespace(aTokener));
		assertEquals('a', nextNonWhitespace(aTokener));
		assertEquals('g', nextNonWhitespace(aTokener));
		assertEquals('e', nextNonWhitespace(aTokener));
		assertEquals('"', nextNonWhitespace(aTokener));
		assertEquals(':', nextNonWhitespace(aTokener));
		assertEquals('2', nextNonWhitespace(aTokener));
		assertEquals('7', nextNonWhitespace(aTokener));
		assertEquals('}', nextNonWhitespace(aTokener));
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
	
	@Test
	void testHasMore_BeginningOfString_No()
	{
		JSONTokener tokener = new JSONTokener("abcd");
		assertFalse(hasMore(tokener,5));
	}
	
	@Test
	void testHasMore_MiddleOfString_No()
	{
		JSONTokener tokener = new JSONTokener("abcd");
		tokener.next();
		assertFalse(hasMore(tokener,4));
	}
	
	@Test
	void testHasMore_BeginningOfString_Yes()
	{
		JSONTokener tokener = new JSONTokener("abcd");
		assertTrue(hasMore(tokener,3));
	}
	
	@Test
	void testHasMore_MiddleOfString_Yes()
	{
		JSONTokener tokener = new JSONTokener("abcd");
		tokener.next();
		assertTrue(hasMore(tokener,2));
	}
	
	@Test
	void testHasMore_EndOfString_Yes()
	{
		JSONTokener tokener = new JSONTokener("abcd");
		tokener.next();
		assertTrue(hasMore(tokener,3));
	}
	
	@Test
	void testNextInt_Beginning()
	{
		JSONTokener tokener = new JSONTokener("abcd");
		assertEquals("abc", next(tokener,3));
	}
	
	@Test
	void testNextInt_Middle()
	{
		JSONTokener tokener = new JSONTokener("abcde");
		tokener.next();
		assertEquals("bc", next(tokener,2));
	}
	
	@Test
	void testNextInt_All()
	{
		JSONTokener tokener = new JSONTokener("abcde");
		assertEquals("abcde", next(tokener,5));
	}
	
	@Test
	void testHasMoreNonWhiteSpace()
	{
		JSONTokener tokener = new JSONTokener("b  \ncd\ne\r\n");
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();  // e
		assertTrue(hasMoreNonWhitespace(tokener));
		tokener.next();  
		assertFalse(hasMoreNonWhitespace(tokener));
		tokener.next();  
		assertFalse(hasMoreNonWhitespace(tokener));
	}
	
	@Test
	void testNextNonWhiteSpace()
	{
		JSONTokener tokener = new JSONTokener("b  \ncd\ne\r\n");
		assertEquals('b', nextNonWhitespace(tokener));
		assertEquals('c', nextNonWhitespace(tokener));
		assertEquals('d', nextNonWhitespace(tokener));
		assertEquals('e', nextNonWhitespace(tokener));
	}
	
	@ParameterizedTest
	@CsvSource({"abc,abc\"d", 
				"e,e\"de", 
				"sd's,sd's\"d"})
	void testNextString(String pOracle, String pInput) 
	{
		assertEquals(pOracle, nextString(new JSONTokener(pInput)));
	}
	
	@Test
	void testNextString_Empty()
	{
		assertEquals("", nextString(new JSONTokener("\"sds")));
	}
	
	private static boolean hasMore(JSONTokener pTokener, int pNumberOfCharacters)
	{
		try 
		{
			Method method = JSONTokener.class.getDeclaredMethod("hasMore", int.class);
			method.setAccessible(true);
			return (boolean) method.invoke(pTokener, pNumberOfCharacters);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return false;
		}
	}
	
	private static boolean hasMoreNonWhitespace(JSONTokener pTokener)
	{
		try 
		{
			Method method = JSONTokener.class.getDeclaredMethod("hasMoreNonWhitespace");
			method.setAccessible(true);
			return (boolean) method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return false;
		}
	}
	
	private static char nextNonWhitespace(JSONTokener pTokener)
	{
		try 
		{
			Method method = JSONTokener.class.getDeclaredMethod("nextNonWhitespace");
			method.setAccessible(true);
			return (char) method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return 0;
		}
	}
	
	private static String next(JSONTokener pTokener, int pNumberOfCharacters)
	{
		try 
		{
			Method method = JSONTokener.class.getDeclaredMethod("next", int.class);
			method.setAccessible(true);
			return (String) method.invoke(pTokener, pNumberOfCharacters);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return "";
		}
	}
	
	private static String nextString(JSONTokener pTokener)
	{
		try 
		{
			Method method = JSONTokener.class.getDeclaredMethod("nextString");
			method.setAccessible(true);
			return (String) method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			exception.printStackTrace();
			fail();
			return "";
		}
	}
}

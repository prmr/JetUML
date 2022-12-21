package org.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestTokener
{
	private static final String TEST = "{\n   \"name\": \"Jo\",\n   \"age\": 27\n}";
	
	private final JsonParser aTokener = new JsonParser(TEST);
	
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
		JsonParser tokener = new JsonParser("a");
		assertEquals('a', tokener.next());
		assertFalse(tokener.hasNext());
	}
	
	@Test
	void testNext_Two()
	{
		JsonParser tokener = new JsonParser("ab");
		assertEquals('a', tokener.next());
		assertEquals('b', tokener.next());
		assertFalse(tokener.hasNext());
	}
	
	@Test
	void testHasMore_BeginningOfString_No()
	{
		JsonParser tokener = new JsonParser("abcd");
		assertFalse(hasMore(tokener,5));
	}
	
	@Test
	void testHasMore_MiddleOfString_No()
	{
		JsonParser tokener = new JsonParser("abcd");
		tokener.next();
		assertFalse(hasMore(tokener,4));
	}
	
	@Test
	void testHasMore_BeginningOfString_Yes()
	{
		JsonParser tokener = new JsonParser("abcd");
		assertTrue(hasMore(tokener,3));
	}
	
	@Test
	void testHasMore_MiddleOfString_Yes()
	{
		JsonParser tokener = new JsonParser("abcd");
		tokener.next();
		assertTrue(hasMore(tokener,2));
	}
	
	@Test
	void testHasMore_EndOfString_Yes()
	{
		JsonParser tokener = new JsonParser("abcd");
		tokener.next();
		assertTrue(hasMore(tokener,3));
	}
	
	@Test
	void testNextInt_Beginning()
	{
		JsonParser tokener = new JsonParser("abcd");
		assertEquals("abc", next(tokener,3));
	}
	
	@Test
	void testNextInt_Middle()
	{
		JsonParser tokener = new JsonParser("abcde");
		tokener.next();
		assertEquals("bc", next(tokener,2));
	}
	
	@Test
	void testNextInt_All()
	{
		JsonParser tokener = new JsonParser("abcde");
		assertEquals("abcde", next(tokener,5));
	}
	
	@Test
	void testHasMoreNonWhiteSpace()
	{
		JsonParser tokener = new JsonParser("b  \ncd\ne\r\n");
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
		JsonParser tokener = new JsonParser("b  \ncd\ne\r\n");
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
		assertEquals(pOracle, nextString(new JsonParser(pInput)));
	}
	
	@Test
	void testNextString_Empty()
	{
		assertEquals("", nextString(new JsonParser("\"sds")));
	}
	
	@Test
	void testNextString_EscapedBackspace()
	{
		assertEquals("\b", nextString(new JsonParser("\b\"")));
	}
	
	@Test
	void testNextString_EscapedSolidus()
	{
		// Creating a string with an escaped forward slash is not easy
		char[] characters = {'a', 'b', '\\', '/', 'c', '"', 'e' };
		assertEquals("ab/c", nextString(new JsonParser( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedReverseSolidus()
	{
		// Creating a string with an escaped back slash is not easy
		char[] characters = {'a', 'b', '\\', '\\', 'c', '"', 'e' };
		assertEquals("ab\\c", nextString(new JsonParser( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedQuote()
	{
		char[] characters = {'a', 'b', '\\', '"', 'c', '"', 'e' };
		assertEquals("ab\"c", nextString(new JsonParser( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedUnicode()
	{
		char[] characters = {'a', 'b', '\\', 'u', '0', '0', 'C', '2', 'c', '"', 'd' };
		assertEquals("abÂc", nextString(new JsonParser( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedTab()
	{
		assertEquals("\t", nextString(new JsonParser("\t\"")));
	}
	
	@Test
	void testNextString_EscapedNewLine()
	{
		// a JSON string with '\' '\n' is different from the string literal \n
		char[] characters = {'a', 'b', '\\', 'n', 'c', '"', 'e' };
		assertEquals("ab\nc", nextString(new JsonParser( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedFormFeed()
	{
		assertEquals("\f", nextString(new JsonParser("\f\"")));
	}
	
	@Test
	void testNextString_EscapedCarriageReturn()
	{
		// a JSON string with '\' '\r' is different from the string literal \r
		char[] characters = {'a', 'b', '\\', 'r', 'c', '"', 'e' };
		assertEquals("ab\rc", nextString(new JsonParser( new String(characters))));
	}
	
	@Test
	void testNextString_Unterminated_OneCharacter()
	{
		JsonParser tokener = new JsonParser("a\"");
		tokener.next();
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_Unterminated_MultipleCharacters()
	{
		JsonParser tokener = new JsonParser("a\"bcd");
		tokener.next();
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_NewLineInString1()
	{
		JsonParser tokener = new JsonParser("\"a\nb\"");
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_NewLineInString2()
	{
		JsonParser tokener = new JsonParser("\"a\rb\"");
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_IncompleteEscape()
	{
		char[] characters = {'"', 'a', '\\' };
		JsonParser tokener = new JsonParser(new String(characters));
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_InvalidEscape()
	{
		char[] characters = {'"', 'a', '\\' , 'x'};
		JsonParser tokener = new JsonParser(new String(characters));
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_MissingUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3'};
		JsonParser tokener = new JsonParser(new String(characters));
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_InvalidUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3', 'X', '"'};
		JsonParser tokener = new JsonParser(new String(characters));
		tokener.next();
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextValue_String()
	{
		JsonParser tokener = new JsonParser("\"a\" : \"bc\"");
		next(tokener,5);
		assertEquals("bc", nextValue(tokener));
	}
	
	@Test
	void testNextValue_true()
	{
		JsonParser tokener = new JsonParser("{\"a\" : true}");
		next(tokener,6);
		assertEquals(true, nextValue(tokener));
	}
	
	@Test
	void testNextValue_false()
	{
		JsonParser tokener = new JsonParser("{\"a\" : false}");
		next(tokener,6);
		assertEquals(false, nextValue(tokener));
	}
	
	@Test
	void testNextValue_null()
	{
		JsonParser tokener = new JsonParser("{\"a\" : null}");
		next(tokener,6);
		assertEquals(JsonObject.NULL, nextValue(tokener));
	}
	
	@Test
	void testNextValue_positiveInteger()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 54}");
		next(tokener,6);
		assertEquals(54, nextValue(tokener));
	}
	
	@Test
	void testNextValue_negativeInteger()
	{
		JsonParser tokener = new JsonParser("{\"a\" : -54}");
		next(tokener,6);
		assertEquals(-54, nextValue(tokener));
	}
	
	@Test
	void testNextValue_Zero()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 0}");
		next(tokener,6);
		assertEquals(0, nextValue(tokener));
	}
	
	@Test
	void testNextValue_object()
	{
		JsonParser tokener = new JsonParser("{\"a\" : -54}");
		assertTrue(nextValue(tokener).getClass() == JsonObject.class);
	}
	
	@Test
	void testNextValue_array()
	{
		JsonParser tokener = new JsonParser("[]");
		assertTrue(nextValue(tokener).getClass() == JsonArray.class);
	}
	
	@Test
	void testNextValue_NumberEndsBuffer()
	{
		JsonParser tokener = new JsonParser("{\"a\" : -54");
		next(tokener,6);
		assertEquals(-54, nextValue(tokener));
	}
	
	@Test
	void testNextValue_Invalid_IncompleteBoolean1()
	{
		JsonParser tokener = new JsonParser("{\"a\" : tru");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_IncompleteBoolean2()
	{
		JsonParser tokener = new JsonParser("{\"a\" : tru }");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_IncompleteNull()
	{
		JsonParser tokener = new JsonParser("{\"a\" : nul");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_InvalidNull()
	{
		JsonParser tokener = new JsonParser("{\"a\" : nulx }");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_UnrecognizedValue()
	{
		JsonParser tokener = new JsonParser("{\"a\" : xxx }");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_NumberStartsWith0()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 0123 }");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_MinusNotANumber()
	{
		JsonParser tokener = new JsonParser("{\"a\" : -A123 }");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_NumberOverflowsInt()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 21474836470 }");
		next(tokener,6);
		testNextValueWithException(tokener);
	}
	
	@Test 
	void testParseObject_Empty()
	{
		JsonParser tokener = new JsonParser("{  }");
		JsonObject object = tokener.parseObject();
		assertTrue(object.keySet().isEmpty());
	}
	
	@Test 
	void testParseObject_OnePair()
	{
		JsonParser tokener = new JsonParser("{\n \"key\" : \"value\" \n}");
		JsonObject object = tokener.parseObject();
		assertEquals(1, object.keySet().size());
		assertTrue(object.has("key"));
		assertEquals("value", object.getString("key"));
	}
	
	@Test 
	void testParseObject_TwoPairs()
	{
		JsonParser tokener = new JsonParser("""
				{ \"key\" : \"value\", 
				  \"k2\" : 12
				}""");
		JsonObject object = tokener.parseObject();
		assertEquals(2, object.keySet().size());
		assertTrue(object.has("key"));
		assertTrue(object.has("k2"));
		assertEquals("value", object.getString("key"));
		assertEquals(12, object.getInt("k2"));
	}
	
	@Test 
	void testParseObject_ThreePairs()
	{
		JsonParser tokener = new JsonParser("""
				{ \"key\" : \"value\", 
				  \"k2\" : 12,
				  \"k3\" : true
				}""");
		JsonObject object = tokener.parseObject();
		assertEquals(3, object.keySet().size());
		assertTrue(object.has("key"));
		assertTrue(object.has("k2"));
		assertTrue(object.has("k3"));
		assertEquals("value", object.getString("key"));
		assertEquals(12, object.getInt("k2"));
		assertEquals(true, object.get("k3"));
	}
	
	private static boolean hasMore(JsonParser pTokener, int pNumberOfCharacters)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("hasMore", int.class);
			method.setAccessible(true);
			return (boolean) method.invoke(pTokener, pNumberOfCharacters);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return false;
		}
	}
	
	private static boolean hasMoreNonWhitespace(JsonParser pTokener)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("hasMoreNonWhitespace");
			method.setAccessible(true);
			return (boolean) method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return false;
		}
	}
	
	private static char nextNonWhitespace(JsonParser pTokener)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("nextNonWhitespace");
			method.setAccessible(true);
			return (char) method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return 0;
		}
	}
	
	private static String next(JsonParser pTokener, int pNumberOfCharacters)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("next", int.class);
			method.setAccessible(true);
			return (String) method.invoke(pTokener, pNumberOfCharacters);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return "";
		}
	}
	
	private static Object nextValue(JsonParser pTokener)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("nextValue");
			method.setAccessible(true);
			return method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			exception.printStackTrace();
			fail();
			return null;
		}
	}
	
	private static String nextString(JsonParser pTokener)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("nextString");
			method.setAccessible(true);
			return (String) method.invoke(pTokener);
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
			return "";
		}
	}
	
	/**
	 * Checks that calling nextString throws a JSONException 
	 */
	private static void testNextStringWithException(JsonParser pTokener)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("nextString");
			method.setAccessible(true);
			method.invoke(pTokener);
			fail();
		}
		catch(InvocationTargetException exception)
		{
			if( exception.getTargetException().getClass() != org.json.JsonException.class)
			{
				fail();
			}
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
		}
	}
	
	/**
	 * Checks that calling nextString throws a JSONException 
	 */
	private static void testNextValueWithException(JsonParser pTokener)
	{
		try 
		{
			Method method = JsonParser.class.getDeclaredMethod("nextValue");
			method.setAccessible(true);
			method.invoke(pTokener);
			fail();
		}
		catch(InvocationTargetException exception)
		{
			if( exception.getTargetException().getClass() != org.json.JsonException.class)
			{
				fail();
			}
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
		}
	}
}

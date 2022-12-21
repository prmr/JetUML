package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestTokener
{
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
		moveToPosition(tokener, 2);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_Unterminated_MultipleCharacters()
	{
		JsonParser tokener = new JsonParser("a\"bcd");
		moveToPosition(tokener, 2);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_NewLineInString1()
	{
		JsonParser tokener = new JsonParser("\"a\nb\"");
		moveToPosition(tokener, 1);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_NewLineInString2()
	{
		JsonParser tokener = new JsonParser("\"a\rb\"");
		moveToPosition(tokener, 1);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_IncompleteEscape()
	{
		char[] characters = {'"', 'a', '\\' };
		JsonParser tokener = new JsonParser(new String(characters));
		moveToPosition(tokener, 1);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_InvalidEscape()
	{
		char[] characters = {'"', 'a', '\\' , 'x'};
		JsonParser tokener = new JsonParser(new String(characters));
		moveToPosition(tokener, 1);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_MissingUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3'};
		JsonParser tokener = new JsonParser(new String(characters));
		moveToPosition(tokener, 1);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextString_InvalidUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3', 'X', '"'};
		JsonParser tokener = new JsonParser(new String(characters));
		moveToPosition(tokener, 1);
		testNextStringWithException(tokener);
	}
	
	@Test
	void testNextValue_String()
	{
		JsonParser tokener = new JsonParser("\"a\" : \"bc\"");
		moveToPosition(tokener, 5);
		assertEquals("bc", nextValue(tokener));
	}
	
	@Test
	void testNextValue_true()
	{
		JsonParser tokener = new JsonParser("{\"a\" : true}");
		moveToPosition(tokener, 6);
		assertEquals(true, nextValue(tokener));
	}
	
	@Test
	void testNextValue_false()
	{
		JsonParser tokener = new JsonParser("{\"a\" : false}");
		moveToPosition(tokener, 6);
		assertEquals(false, nextValue(tokener));
	}
	
	@Test
	void testNextValue_null()
	{
		JsonParser tokener = new JsonParser("{\"a\" : null}");
		moveToPosition(tokener, 6);
		assertEquals(JsonObject.NULL, nextValue(tokener));
	}
	
	@Test
	void testNextValue_positiveInteger()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 54}");
		moveToPosition(tokener, 6);
		assertEquals(54, nextValue(tokener));
	}
	
	@Test
	void testNextValue_negativeInteger()
	{
		JsonParser tokener = new JsonParser("{\"a\" : -54}");
		moveToPosition(tokener, 6);
		assertEquals(-54, nextValue(tokener));
	}
	
	@Test
	void testNextValue_Zero()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 0}");
		moveToPosition(tokener, 6);
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
		moveToPosition(tokener, 6);
		assertEquals(-54, nextValue(tokener));
	}
	
	@Test
	void testNextValue_Invalid_IncompleteBoolean1()
	{
		JsonParser tokener = new JsonParser("{\"a\" : tru");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_IncompleteBoolean2()
	{
		JsonParser tokener = new JsonParser("{\"a\" : tru }");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_IncompleteNull()
	{
		JsonParser tokener = new JsonParser("{\"a\" : nul");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_InvalidNull()
	{
		JsonParser tokener = new JsonParser("{\"a\" : nulx }");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_UnrecognizedValue()
	{
		JsonParser tokener = new JsonParser("{\"a\" : xxx }");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_NumberStartsWith0()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 0123 }");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_MinusNotANumber()
	{
		JsonParser tokener = new JsonParser("{\"a\" : -A123 }");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
	}
	
	@Test
	void testNextValue_Invalid_NumberOverflowsInt()
	{
		JsonParser tokener = new JsonParser("{\"a\" : 21474836470 }");
		moveToPosition(tokener, 6);
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
			Method method = JsonParser.class.getDeclaredMethod("parseString");
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
			Method method = JsonParser.class.getDeclaredMethod("parseString");
			method.setAccessible(true);
			method.invoke(pTokener);
			fail();
		}
		catch(InvocationTargetException exception)
		{
			if( exception.getTargetException().getClass() != org.jetuml.persistence.json.JsonException.class)
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
			if( exception.getTargetException().getClass() != org.jetuml.persistence.json.JsonException.class)
			{
				fail();
			}
		}
		catch(ReflectiveOperationException exception)
		{
			fail();
		}
	}
	
	private static void moveToPosition(JsonParser pParser, int pPosition)
	{
		try
		{
			Field field = JsonParser.class.getDeclaredField("aInput");
			field.setAccessible(true);
			CharacterBuffer buffer = (CharacterBuffer) field.get(pParser);
			for( int i = 0; i < pPosition; i++ )
			{
				buffer.next();
			}
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
}

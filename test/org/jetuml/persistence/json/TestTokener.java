package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class TestTokener
{

	
	@Test
	void testNextValue_String()
	{
		JsonParser tokener = new JsonParser("\"a\" : \"bc\"");
		moveToPosition(tokener, 6);
		assertEquals("bc", nextValue(tokener));
	}
	
	@Test
	void testNextValue_true()
	{
		JsonParser tokener = new JsonParser("{\"a\" : true}");
		moveToPosition(tokener, 7);
		assertEquals(true, nextValue(tokener));
	}
	
	@Test
	void testNextValue_false()
	{
		JsonParser tokener = new JsonParser("{\"a\" : false}");
		moveToPosition(tokener, 7);
		assertEquals(false, nextValue(tokener));
	}
	
	@Test
	void testNextValue_null()
	{
		JsonParser tokener = new JsonParser("{\"a\" : null}");
		moveToPosition(tokener, 6);
		testNextValueWithException(tokener);
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
		JsonObject object = JsonParser.parse("{  }");
		assertTrue(object.keySet().isEmpty());
	}
	
	@Test 
	void testParseObject_OnePair()
	{
		JsonObject object = JsonParser.parse("{\n \"key\" : \"value\" \n}");
		assertEquals(1, object.keySet().size());
		assertTrue(object.has("key"));
		assertEquals("value", object.getString("key"));
	}
	
	@Test 
	void testParseObject_TwoPairs()
	{
		JsonObject object = JsonParser.parse("""
				{ \"key\" : \"value\", 
				  \"k2\" : 12
				}""");
		assertEquals(2, object.keySet().size());
		assertTrue(object.has("key"));
		assertTrue(object.has("k2"));
		assertEquals("value", object.getString("key"));
		assertEquals(12, object.getInt("k2"));
	}
	
	@Test 
	void testParseObject_ThreePairs()
	{
		JsonObject object = JsonParser.parse("""
				{ \"key\" : \"value\", 
				  \"k2\" : 12,
				  \"k3\" : true
				}""");
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
			if( exception.getTargetException().getClass() != JsonParsingException.class)
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

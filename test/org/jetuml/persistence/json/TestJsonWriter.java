package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/*
 * Only testing that the operation is despatched as expected.
 */
public class TestJsonWriter
{
	@Test
	void testWrite_Boolean()
	{
		assertEquals("true", JsonWriter.write(true));
		assertEquals("false", JsonWriter.write(false));
	}
	
	@Test
	void testWrite_Integer()
	{
		assertEquals("0", JsonWriter.write(0));
		assertEquals("-1", JsonWriter.write(-1));
		assertEquals("123", JsonWriter.write(123));
	}
	
	@Test
	void testWrite_String()
	{
		assertEquals("\"abc\"", JsonWriter.write("abc"));
	}
	
	@Test
	void testWrite_JsonObject()
	{
		assertEquals("{}", JsonWriter.write(new JsonObject()));
	}
	
	@Test
	void testWrite_JsonArray()
	{
		assertEquals("[]", JsonWriter.write(new JsonArray()));
	}
	
	@Test
	void testWrite_Null()
	{
		assertThrows(JsonException.class, () -> JsonWriter.write(null));
	}
}

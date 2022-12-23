package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestJsonAnyValueParser
{
	private static final JsonAnyValueParser PARSER = new JsonAnyValueParser();

	@Test
	void testIsApplicable_Empty()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("")));
	}

	@ParameterizedTest
	@ValueSource(strings = {"[", "{", "\"", "-1", "0", "true", "false"})
	void testIsApplicable_True(String pChar)
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer(pChar)));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"x", "X", "\n", "True", "False"})
	void testIsApplicable_False(String pChar)
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer(pChar)));
	}
	
	@Test
	void testParse_AsString()
	{
		assertEquals("ABC", PARSER.parse(new ParsableCharacterBuffer("\"ABC\"   ")));
	}
	
	@Test
	void testParse_AsInteger()
	{
		assertEquals(12, PARSER.parse(new ParsableCharacterBuffer("12  ")));
	}
	
	@Test
	void testParse_AsBoolean()
	{
		assertEquals(true, PARSER.parse(new ParsableCharacterBuffer("true")));
	}
	
	@Test
	void testParse_AsObject()
	{
		JsonObject result = (JsonObject) PARSER.parse(new ParsableCharacterBuffer("{ \"a\" : 3}"));
		assertEquals(1, result.numberOfProperties());
		assertEquals(3, result.get("a"));
	}
	
	@Test
	void testParse_AsArray()
	{
		JsonArray result = (JsonArray) PARSER.parse(new ParsableCharacterBuffer("[2,3, false ]"));
		assertEquals(3, result.length());
		assertEquals(2, result.get(0));
		assertEquals(3, result.get(1));
		assertEquals(false, result.get(2));
	}
	
	@Test
	void testParse_Invalid()
	{
		assertThrows(JsonParsingException.class, ()-> PARSER.parse(new ParsableCharacterBuffer("x[2,3, false ]")));
	}
}

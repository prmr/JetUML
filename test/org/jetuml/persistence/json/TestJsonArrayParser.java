package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestJsonArrayParser
{
	private static final JsonArrayParser PARSER = new JsonArrayParser();

	@Test
	void testIsApplicable_Empty()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("")));
	}

	@Test
	void testIsApplicable_SingleOpening()
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("[")));
	}

	@Test
	void testIsApplicable_SingleFalse()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer(" ")));
	}

	@Test
	void testParse_Empty()
	{
		JsonArray result = PARSER.parse(new ParsableCharacterBuffer("[]"));
		assertEquals(0, result.size());
	}

	@Test
	void testParse_WithStringValue()
	{
		JsonArray result = PARSER.parse(new ParsableCharacterBuffer("[\"a\"]"));
		assertEquals(1, result.size());
		assertEquals("a", result.get(0));
	}
	
	@Test
	void testParse_WithIntegerValue()
	{
		JsonArray result = PARSER.parse(new ParsableCharacterBuffer("[5]"));
		assertEquals(1, result.size());
		assertEquals(5, result.get(0));
	}
	
	@Test
	void testParse_WithBooleanValue()
	{
		JsonArray result = PARSER.parse(new ParsableCharacterBuffer("[true]"));
		assertEquals(1, result.size());
		assertEquals(true, result.get(0));
	}
	
	@Test
	void testParse_WithMultipleValues()
	{
		JsonArray result = PARSER.parse(new ParsableCharacterBuffer("[\"a\",true , 5, false ,-1]"));
		assertEquals(5, result.size());
		assertEquals("a", result.get(0));
		assertEquals(true, result.get(1));
		assertEquals(5, result.get(2));
		assertEquals(false, result.get(3));
		assertEquals(-1, result.get(4));
	}
}

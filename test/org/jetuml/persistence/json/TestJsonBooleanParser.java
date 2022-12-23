package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestJsonBooleanParser
{
	private static final JsonBooleanParser PARSER = new JsonBooleanParser();
	
	@Test
	void testIsApplicable_Empty()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("")));
	}
	
	@Test
	void testIsApplicable_True()
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("txxx")));
	}
	
	@Test
	void testIsApplicable_False()
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("fxxx")));
	}
	
	@Test
	void testIsApplicable_NotApplicable()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("xxx")));
	}
	
	@Test
	void testParse_TrueOnly()
	{
		assertEquals(true, PARSER.isApplicable(new ParsableCharacterBuffer("true")));
	}
	
	@Test
	void testParse_TrueAndMore()
	{
		assertEquals(true, PARSER.parse(new ParsableCharacterBuffer("true   sdsds")));
	}
	
	@Test
	void testParse_FalseOnly()
	{
		assertEquals(false, PARSER.parse(new ParsableCharacterBuffer("false")));
	}
	
	@Test
	void testParse_FalseAndMore()
	{
		assertEquals(false, PARSER.parse(new ParsableCharacterBuffer("falseXYSZ")));
	}
	
	@Test
	void testParse_InvalidSameLength()
	{
		assertThrows(JsonParsingException.class, ()-> PARSER.parse(new ParsableCharacterBuffer("txxx")));
	}
	
	@Test
	void testParse_InvalidNotSameLength()
	{
		assertThrows(JsonParsingException.class, ()-> PARSER.parse(new ParsableCharacterBuffer("txx")));
	}
	
	@Test
	void testParse_InvalidNotApplicable()
	{
		assertThrows(JsonParsingException.class, ()-> PARSER.parse(new ParsableCharacterBuffer("xxx")));
	}
}

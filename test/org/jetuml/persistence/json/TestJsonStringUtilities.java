package org.jetuml.persistence.json;

import static org.jetuml.persistence.json.JsonStringUtilities.parseString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestJsonStringUtilities
{
	@ParameterizedTest
	@CsvSource({"abc,\"abc\"d", 
				"e,\"e\"\"de", 
				"sd's,\"sd's\"d"})
	void testNextString(String pOracle, String pInput) 
	{
		assertEquals(pOracle, parseString(new ParsableCharacterBuffer(pInput)));
	}
	
	@Test
	void testNextString_Empty()
	{
		assertEquals("", parseString(new ParsableCharacterBuffer("\"\"sds")));
	}
	
	@Test
	void testNextString_EscapedBackspace()
	{
		char[] characters = {'"', '\\', 'b', '"', 'e' };
		assertEquals("\b", parseString(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_EscapedSolidus()
	{
		// Creating a string with an escaped forward slash is not easy
		char[] characters = {'"', 'a', 'b', '\\', '/', 'c', '"', 'e' };
		assertEquals("ab/c", parseString(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedReverseSolidus()
	{
		// Creating a string with an escaped back slash is not easy
		char[] characters = {'"', 'a', 'b', '\\', '\\', 'c', '"', 'e' };
		assertEquals("ab\\c", parseString(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedQuote()
	{
		char[] characters = {'"','a', 'b', '\\', '"', 'c', '"', 'e' };
		assertEquals("ab\"c", parseString(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedUnicode()
	{
		char[] characters = {'"','a', 'b', '\\', 'u', '0', '0', 'C', '2', 'c', '"', 'd' };
		assertEquals("abÂc", parseString(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedTab()
	{
		char[] characters = {'"','\\', 't', '"', 'd' };
		assertEquals("\t", parseString(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_EscapedNewLine()
	{
		// a JSON string with '\' '\n' is different from the string literal \n
		char[] characters = {'"','a', 'b', '\\', 'n', 'c', '"', 'e' };
		assertEquals("ab\nc", parseString(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedFormFeed()
	{
		char[] characters = {'"', '\\', 'f', '"', 'e' };
		assertEquals("\f", parseString(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_EscapedCarriageReturn()
	{
		// a JSON string with '\' '\r' is different from the string literal \r
		char[] characters = {'"','a', 'b', '\\', 'r', 'c', '"', 'e' };
		assertEquals("ab\rc", parseString(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_Unterminated_OneCharacter()
	{
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer("\"")));
	}
	
	@Test
	void testNextString_Unterminated_MultipleCharacters()
	{
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer("a\"bcd")));
	}
	
	@Test
	void testNextString_NewLineInString1()
	{
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer("\"a\nb\"")));
	}
	
	@Test
	void testNextString_NewLineInString2()
	{
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer("\"a\rb\"")));
	}
	
	@Test
	void testNextString_IncompleteEscape()
	{
		char[] characters = {'"', 'a', '\\' };
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_InvalidEscape()
	{
		char[] characters = {'"', 'a', '\\' , 'x'};
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_MissingUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3'};
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_InvalidUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3', 'X', '"'};
		assertThrows(JsonParsingException.class, () -> parseString(new ParsableCharacterBuffer(new String(characters))));
	}
}

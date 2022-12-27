/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestJsonStringParser
{
	private static final JsonStringParser PARSER = new JsonStringParser();
	
	@ParameterizedTest
	@CsvSource({"abc,\"abc\"d", 
				"e,\"e\"\"de", 
				"sd's,\"sd's\"d"})
	void testNextString(String pOracle, String pInput) 
	{
		assertEquals(pOracle, PARSER.parse(new ParsableCharacterBuffer(pInput)));
	}
	
	@Test
	void testNextString_Empty()
	{
		assertEquals("", PARSER.parse(new ParsableCharacterBuffer("\"\"sds")));
	}
	
	@Test
	void testNextString_EscapedBackspace()
	{
		char[] characters = {'"', '\\', 'b', '"', 'e' };
		assertEquals("\b", PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_EscapedSolidus()
	{
		// Creating a string with an escaped forward slash is not easy
		char[] characters = {'"', 'a', 'b', '\\', '/', 'c', '"', 'e' };
		assertEquals("ab/c", PARSER.parse(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedReverseSolidus()
	{
		// Creating a string with an escaped back slash is not easy
		char[] characters = {'"', 'a', 'b', '\\', '\\', 'c', '"', 'e' };
		assertEquals("ab\\c", PARSER.parse(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedQuote()
	{
		char[] characters = {'"','a', 'b', '\\', '"', 'c', '"', 'e' };
		assertEquals("ab\"c", PARSER.parse(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedUnicode()
	{
		char[] characters = {'"','a', 'b', '\\', 'u', '0', '0', 'C', '2', 'c', '"', 'd' };
		assertEquals("abÂc", PARSER.parse(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedTab()
	{
		char[] characters = {'"','\\', 't', '"', 'd' };
		assertEquals("\t", PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_EscapedNewLine()
	{
		// a JSON string with '\' '\n' is different from the string literal \n
		char[] characters = {'"','a', 'b', '\\', 'n', 'c', '"', 'e' };
		assertEquals("ab\nc", PARSER.parse(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_EscapedFormFeed()
	{
		char[] characters = {'"', '\\', 'f', '"', 'e' };
		assertEquals("\f", PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_EscapedCarriageReturn()
	{
		// a JSON string with '\' '\r' is different from the string literal \r
		char[] characters = {'"','a', 'b', '\\', 'r', 'c', '"', 'e' };
		assertEquals("ab\rc", PARSER.parse(new ParsableCharacterBuffer( new String(characters))));
	}
	
	@Test
	void testNextString_Unterminated_OneCharacter()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("\"")));
	}
	
	@Test
	void testNextString_Unterminated_MultipleCharacters()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("a\"bcd")));
	}
	
	@Test
	void testNextString_NewLineInString1()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("\"a\nb\"")));
	}
	
	@Test
	void testNextString_NewLineInString2()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("\"a\rb\"")));
	}
	
	@Test
	void testNextString_IncompleteEscape()
	{
		char[] characters = {'"', 'a', '\\' };
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_InvalidEscape()
	{
		char[] characters = {'"', 'a', '\\' , 'x'};
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_MissingUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3'};
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testNextString_InvalidUnicodeDigits()
	{
		char[] characters = {'"', 'a', '\\' , 'u', '1', '2', '3', 'X', '"'};
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer(new String(characters))));
	}
	
	@Test
	void testWriteJsonStringEmpty()
	{
		assertEquals("\"\"", JsonStringParser.writeJsonString(""));
	}
	
	@Test
	void testWriteJsonStringNormal()
	{
		assertEquals("\"abc\"", JsonStringParser.writeJsonString("abc"));
	}
	
	@Test
	void testWriteJsonStringWithReEscapes()
	{
		assertEquals("\"a\\b\\n\\f\\r\\tc\"", JsonStringParser.writeJsonString("a\b\n\f\r\tc"));
	}
	
	@Test
	void testWriteJsonStringWithQuote()
	{
		char[] characters = {'a', '\\', 'c'};
		assertEquals("\"a\\\\c\"", JsonStringParser.writeJsonString(new String(characters)));
	}
	
	@Test
	void testWriteJsonStringWithSolidus()
	{
		char[] characters = {'a', '/', 'c'};
		assertEquals("\"a\\/c\"", JsonStringParser.writeJsonString(new String(characters)));
	}
	
	@Test
	void testWriteJsonStringWithReverseSolidus()
	{
		char[] characters = {'a', '\\', 'c'};
		assertEquals("\"a\\\\c\"", JsonStringParser.writeJsonString(new String(characters)));
	}
	
	@Test
	void testWriteJsonStringWithControlCharacter()
	{
		char[] characters = {'a', '\u0001', 'c'};
		assertEquals("\"a\\u0001c\"", JsonStringParser.writeJsonString(new String(characters)));
	}
}

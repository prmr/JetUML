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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestJsonIntegerParser
{
	private static final JsonIntegerParser PARSER = new JsonIntegerParser();
	
	@Test
	void testIsApplicable_Empty()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("")));
	}
	
	@Test
	void testIsApplicable_MinusOnly()
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("-")));
	}
	
	@Test
	void testIsApplicable_MinusZero()
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("-0")));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0,1,2,3,4,5,6,7,8,9})
	void testIsApplicable_Digits(int pValue)
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("" + pValue + "xyz")));
	}
	
	@Test
	void testIsApplicable_NotApplicable()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("x")));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,-1,-2,12345,-2384})
	void testParse_ValidIntegers(int pInteger)
	{
		assertEquals(pInteger, PARSER.parse(new ParsableCharacterBuffer(pInteger + " ")));
	}
	
	@Test
	void testParse_BufferEndsWithNumber()
	{
		assertEquals(123, PARSER.parse(new ParsableCharacterBuffer("123")));
	}
	
	@Test
	void testParse_IsBlank()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("")));
	}
	
	@Test
	void testParse_StartsWithMinusZero()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("-0")));
	}
	
	@Test
	void testParse_hasLeadingZero()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("0123")));
	}
	
	@Test
	void testParse_IntegerOverflow()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("2147483648")));
	}
	
	@Test
	void testParse_IntegerUnderflow()
	{
		assertThrows(JsonParsingException.class, () -> PARSER.parse(new ParsableCharacterBuffer("-2147483649")));
	}
}

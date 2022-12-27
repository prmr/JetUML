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

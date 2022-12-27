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
	
	@Test
	void testWriteJsonArray_Empty()
	{
		assertEquals("[]", JsonArrayParser.writeJsonArray(new JsonArray()));
	}
	
	@Test
	void testWriteJsonArray_SingleValue()
	{
		JsonArray array = new JsonArray();
		array.add(1);
		assertEquals("[1]", JsonArrayParser.writeJsonArray(array));
	}
	
	@Test
	void testWriteJsonArray_TwoValues()
	{
		JsonArray array = new JsonArray();
		array.add(1);
		array.add(2);
		assertEquals("[1,2]", JsonArrayParser.writeJsonArray(array));
	}
	
	@Test
	void testWriteJsonArray_MixedValues()
	{
		JsonArray array = new JsonArray();
		array.add(1);
		array.add("XXX");
		array.add(false);
		array.add(new JsonObject());
		array.add(new JsonArray());
		assertEquals("[1,\"XXX\",false,{},[]]", JsonArrayParser.writeJsonArray(array));
	}
	
	@Test
	void testWriteJsonArray_InvalidValue()
	{
		assertThrows(JsonException.class, () -> JsonArrayParser.writeJsonArray(1.0));
	}
}

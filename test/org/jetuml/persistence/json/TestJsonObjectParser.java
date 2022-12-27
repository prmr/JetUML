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

public class TestJsonObjectParser
{
	private static final JsonObjectParser PARSER = new JsonObjectParser();

	@Test
	void testIsApplicable_Empty()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer("")));
	}

	@Test
	void testIsApplicable_SingleOpening()
	{
		assertTrue(PARSER.isApplicable(new ParsableCharacterBuffer("{")));
	}

	@Test
	void testIsApplicable_SingleFalse()
	{
		assertFalse(PARSER.isApplicable(new ParsableCharacterBuffer(" ")));
	}

	@Test
	void testParse_Empty()
	{
		JsonObject result = PARSER.parse(new ParsableCharacterBuffer("{}"));
		assertEquals(0, result.numberOfProperties());
	}

	@Test
	void testParse_WithStringProperty()
	{
		JsonObject result = PARSER.parse(new ParsableCharacterBuffer("{\"a\" : \"b\"}"));
		assertEquals(1, result.numberOfProperties());
		assertEquals("b", result.get("a"));
	}

	@Test
	void testParse_WithIntegerProperty()
	{
		JsonObject result = PARSER.parse(new ParsableCharacterBuffer("{\"a\" : 5  }"));
		assertEquals(1, result.numberOfProperties());
		assertEquals(5, result.get("a"));
	}

	@Test
	void testParse_WithBooleanProperty()
	{
		JsonObject result = PARSER.parse(new ParsableCharacterBuffer("{\"a\" : true  }"));
		assertEquals(1, result.numberOfProperties());
		assertEquals(true, result.get("a"));
	}

	@Test
	void testParse_WithTwoProperties()
	{
		JsonObject result = PARSER.parse(new ParsableCharacterBuffer("{\"a\" : \"b\", \"c\": 4  }"));
		assertEquals(2, result.numberOfProperties());
		assertEquals("b", result.get("a"));
		assertEquals(4, result.get("c"));
	}

	@Test
	void testParse_DuplicateKey()
	{
		assertThrows(JsonParsingException.class,
				() -> PARSER.parse(new ParsableCharacterBuffer("{\"a\" : \"b\", \"a\": 4  }")));
	}
	
	@Test
	void testParse_NotClosed()
	{
		assertThrows(JsonParsingException.class,
				() -> PARSER.parse(new ParsableCharacterBuffer("{\"a\" : \"b\", \"a\": 4  ")));
	}
	
	@Test
	void testParse_MissingColon()
	{
		assertThrows(JsonParsingException.class,
				() -> PARSER.parse(new ParsableCharacterBuffer("{\"a\"  \"b\", \"a\": 4  }")));
	}
	
	@Test
	void testParse_MissingComma()
	{
		assertThrows(JsonParsingException.class,
				() -> PARSER.parse(new ParsableCharacterBuffer("{\"a\" : \"b\" \"a\": 4  }")));
	}
	
	@Test
	void testWriteJsonObject_Empty()
	{
		assertEquals("{}", JsonObjectParser.writeJsonObject(new JsonObject()));
	}
	
	@Test
	void testWriteJsonObject_SingleValue()
	{
		JsonObject object = new JsonObject();
		object.put("a",1);
		assertEquals("{\"a\":1}", JsonObjectParser.writeJsonObject(object));
	}
	
	@Test
	void testWriteJsonObject_TwoValues()
	{
		JsonObject object = new JsonObject();
		object.put("a",1);
		object.put("b",2);
		assertEquals("{\"a\":1,\"b\":2}", JsonObjectParser.writeJsonObject(object));
	}
	
	@Test
	void testWriteJsonObject_MixedValues()
	{
		JsonObject object = new JsonObject();
		object.put("a",1);
		object.put("b","XXX");
		object.put("c",false);
		object.put("d",new JsonObject());
		object.put("e",new JsonArray());
		assertEquals("{\"a\":1,\"b\":\"XXX\",\"c\":false,\"d\":{},\"e\":[]}", JsonObjectParser.writeJsonObject(object));
	}
	
	@Test
	void testWriteJsonObject_InvalidValue()
	{
		assertThrows(JsonException.class, () -> JsonObjectParser.writeJsonObject(1.0));
	}
}

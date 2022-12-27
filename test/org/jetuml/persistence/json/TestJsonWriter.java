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

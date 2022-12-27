/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.persistence.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TestParsableCharacterBuffer
{
	private final ParsableCharacterBuffer aBuffer = new ParsableCharacterBuffer("abc");

	@Test
	void testNext()
	{
		assertEquals('a', aBuffer.next());
		assertEquals('b', aBuffer.next());
		assertEquals('c', aBuffer.next());
		assertThrows(JsonParsingException.class, () -> aBuffer.next());
	}

	@Test
	void testNext_Int_Empty()
	{
		assertEquals("", aBuffer.next(0));
	}

	@Test
	void testNext_Int_One()
	{
		assertEquals("a", aBuffer.next(1));
	}

	@Test
	void testNext_Int_All()
	{
		assertEquals("abc", aBuffer.next(3));
	}

	@Test
	void testNext_Int_Error()
	{
		assertThrows(JsonParsingException.class, () -> aBuffer.next(4));
	}

	@Test
	void testConsume_Correct()
	{
		aBuffer.consume('a');
		assertEquals('b', aBuffer.next());
	}

	@Test
	void testConsume_Incorrect()
	{
		assertThrows(JsonParsingException.class, () -> aBuffer.consume('x'));
	}

	@Test
	void testConsume_ReadPastTheEnd()
	{
		aBuffer.next(3);
		assertThrows(JsonParsingException.class, () -> aBuffer.consume('x'));
	}
}

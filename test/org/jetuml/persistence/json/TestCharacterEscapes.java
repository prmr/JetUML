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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestCharacterEscapes
{
	private final CharacterEscapes aEscapes = new CharacterEscapes();
	
	@ParameterizedTest
	@ValueSource(chars = {'b', 't', 'n', 'f', 'r', '"', '/', '\\'})
	void testIsSymbol_True(char pChar)
	{
		assertTrue(aEscapes.isSymbol(pChar));
	}
	
	@ParameterizedTest
	@ValueSource(chars = {0, 'u', 'x', '\n', '\r'})
	void testIsSymbol_False(char pChar)
	{
		assertFalse(aEscapes.isSymbol(pChar));
	}
	
	@Test
	void testGetCodePoint_backspace()
	{
		assertEquals('\b', aEscapes.getCodePoint('b'));
	}
	
	@Test
	void testGetCodePoint_newline()
	{
		assertEquals('\n', aEscapes.getCodePoint('n'));
	}
	
	@Test
	void testGetCodePoint_carriageReturn()
	{
		assertEquals('\r', aEscapes.getCodePoint('r'));
	}
	
	@Test
	void testGetCodePoint_FormFeed()
	{
		assertEquals('\f', aEscapes.getCodePoint('f'));
	}
	
	@Test
	void testGetCodePoint_Tab()
	{
		assertEquals('\t', aEscapes.getCodePoint('t'));
	}
	
	@Test
	void testGetCodePoint_Quote()
	{
		assertEquals('"', aEscapes.getCodePoint('"'));
	}
	
	@Test
	void testGetCodePoint_Solidus()
	{
		assertEquals('/', aEscapes.getCodePoint('/'));
	}
	
	@Test
	void testGetCodePoint_ReverseSolidus()
	{
		assertEquals('\\', aEscapes.getCodePoint('\\'));
	}
	
	@ParameterizedTest
	@ValueSource(chars = {'\b', '\t', '\n', '\f', '\r', '"', '/', '\\'})
	void testIsEscapableCodePoint_True(char pChar)
	{
		assertTrue(aEscapes.isEscapableCodePoint(pChar));
	}
	
	@ParameterizedTest
	@ValueSource(chars = {'b', 't', 'n', 'u'})
	void testIsEscapableCodePoint_False(char pChar)
	{
		assertFalse(aEscapes.isEscapableCodePoint(pChar));
	}
	
	@Test
	void testGetEscape_backspace()
	{
		assertEquals("\\b", aEscapes.getEscape('\b'));
	}
	
	@Test
	void testGetEscape_newline()
	{
		assertEquals("\\n", aEscapes.getEscape('\n'));
	}
	
	@Test
	void testGetEscape_carriageReturn()
	{
		assertEquals("\\r", aEscapes.getEscape('\r'));
	}
	
	@Test
	void testGetEscape_formFeed()
	{
		assertEquals("\\f", aEscapes.getEscape('\f'));
	}
	
	@Test
	void testGetEscape_Tab()
	{
		assertEquals("\\t", aEscapes.getEscape('\t'));
	}
	
	@Test
	void testGetEscape_Quote()
	{
		assertEquals("\\\"", aEscapes.getEscape('"'));
	}
	
	@Test
	void testGetEscape_Solidus()
	{
		assertEquals("\\/", aEscapes.getEscape('/'));
	}
	
	@Test
	void testGetEscape_ReverseSolidus()
	{
		assertEquals("\\\\", aEscapes.getEscape('\\'));
	}
}

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

public class TestCharacterBuffer
{
	private final CharacterBuffer aEmpty = new CharacterBuffer("");
	private final CharacterBuffer aBuffer1 = new CharacterBuffer("abc\nde\r\b\ffg h");
	private final CharacterBuffer aBuffer2 = new CharacterBuffer("a  r  ");
	
	@Test
	void testSkipBlanks_Empty()
	{
		aEmpty.skipBlanks(); // Test that this does not raise an exception, but nothing happens
	}
	
	@Test
	void testSkipBlanks_None()
	{
		aBuffer1.skipBlanks(); 
		assertEquals('a', aBuffer1.next());
	}
	
	@Test
	void testSkipBlanks_One()
	{
		aBuffer1.next();
		aBuffer1.next();
		aBuffer1.next();
		aBuffer1.skipBlanks();
		assertEquals('d', aBuffer1.next());
	}
	
	@Test
	void testSkipBlanks_Multiple()
	{
		aBuffer2.next();
		aBuffer2.skipBlanks();
		assertEquals('r', aBuffer2.next());
	}
	
	@Test
	void testSkipBlanks_ToEnd()
	{
		aBuffer2.next();
		aBuffer2.skipBlanks();
		aBuffer2.next();
		aBuffer2.skipBlanks();
		assertFalse(aBuffer2.hasMore());
	}
	
	@Test
	void testBackUp()
	{
		assertEquals('a', aBuffer1.next());
		aBuffer1.backUp();
		assertEquals('a', aBuffer1.next());
	}
	
	@Test
	void testNext()
	{
		assertEquals('a', aBuffer1.next());
		assertEquals('b', aBuffer1.next());
		assertEquals('c', aBuffer1.next());
		assertEquals('\n', aBuffer1.next());
		assertEquals('d', aBuffer1.next());
		assertEquals('e', aBuffer1.next());
		assertEquals('\r', aBuffer1.next());
		assertEquals('\b', aBuffer1.next());
		assertEquals('\f', aBuffer1.next());
		assertEquals('f', aBuffer1.next());
		assertEquals('g', aBuffer1.next());
		assertEquals(' ', aBuffer1.next());
		assertEquals('h', aBuffer1.next());
	}
	
	@Test
	void testEmpty()
	{
		assertFalse(aEmpty.hasMore());
		assertFalse(aEmpty.hasMore(2));
	}
	
	@Test
	void testHasMore_Int()
	{
		assertTrue(aBuffer1.hasMore(6));
		aBuffer1.next();
		aBuffer1.next();
		aBuffer1.next();
		aBuffer1.next();
		aBuffer1.next();
		aBuffer1.next(); // read e
		assertTrue(aBuffer1.hasMore(6));
		aBuffer1.next(); // read \r
		assertTrue(aBuffer1.hasMore(6));
		aBuffer1.next(); // read \b
		assertFalse(aBuffer1.hasMore(6));
		aBuffer1.next();
		assertFalse(aBuffer1.hasMore(6));
	}
	
	@Test
	void testHasMore()
	{
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertTrue(aBuffer1.hasMore());
		aBuffer1.next();
		assertFalse(aBuffer1.hasMore());
	}
}

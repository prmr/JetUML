/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
public class TestVersion
{
	private static final Version VERSION1 = Version.create(2, 5);
	private static final Version VERSION2 = Version.create(0, 6, 1);
	
	@Test
	void testMajor()
	{
		assertEquals(2, VERSION1.major());
		assertEquals(0, VERSION2.major());
	}
	
	@Test
	void testMinor()
	{
		assertEquals(5, VERSION1.minor());
		assertEquals(6, VERSION2.minor());
	}
	
	@Test
	void testPatch()
	{
		assertEquals(0, VERSION1.patch());
		assertEquals(1, VERSION2.patch());
	}
	
	@Test
	void testHashcode()
	{
		assertEquals(VERSION1.hashCode(), new Version(2,5,0).hashCode());
		assertEquals(VERSION2.hashCode(), new Version(0,6,1).hashCode());
	}
	
	@Test
	void testToString_Patch()
	{
		assertEquals("2.5", VERSION1.toString());
	}
	
	@Test
	void testToString_NoPatch()
	{
		assertEquals("0.6.1", VERSION2.toString());
	}
	
	@Test
	void testParse_Patch()
	{
		assertEquals(VERSION2, Version.parse("0.6.1"));
	}
	
	@Test
	void testParse_NoPatch()
	{
		assertEquals(VERSION1, Version.parse("2.5"));
	}
	
	@Test
	void testParse_TooShort()
	{
		assertThrows(IllegalArgumentException.class, () -> Version.parse("0"));
	}
	
	@Test
	void testParse_TooLong()
	{
		assertThrows(IllegalArgumentException.class, () -> Version.parse("0.1.2.3"));
	}
	
	@Test
	void testParse_NumberFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> Version.parse("a.1.2"));
		assertThrows(IllegalArgumentException.class, () -> Version.parse("0.a.2"));
		assertThrows(IllegalArgumentException.class, () -> Version.parse("1.2.a"));
	}
	
	@Test
	void testEquals_Same()
	{
		assertTrue(VERSION1.equals(VERSION1));
	}
	
	@Test
	void testEquals_Null()
	{
		assertFalse(VERSION1.equals(null));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testEquals_NotSameClass()
	{
		assertFalse(VERSION1.equals("2.5.0"));
	}
	
	@Test
	void testEquals_DifferentMajor()
	{
		assertFalse(VERSION1.equals(Version.create(3, 5, 0)));
	}
	
	@Test
	void testEquals_DifferentMinor()
	{
		assertFalse(VERSION1.equals(Version.create(2, 6, 0)));
	}
	
	@Test
	void testEquals_DifferentPatch()
	{
		assertFalse(VERSION1.equals(Version.create(2, 5, 1)));
	}
	
	@Test
	void testEquals_True()
	{
		assertTrue(VERSION1.equals(Version.create(2, 5, 0)));
	}
}
